package application

import hibernate.HibernateLoadBalancer
import jakarta.persistence.PersistenceException
import loadBalancer.loadBalancingMechanism.RandConnection
import loadBalancer.loadBalancingMechanism.RoundRobin
import loadBalancer.loadBalancingMechanism.WeightedResponseTime
import loggerDB.LoggerDB
import org.hibernate.Session
import java.lang.IllegalStateException
import java.util.Scanner

class App {
    private val scanner = Scanner(System.`in`)
    private val log = LoggerDB.getLogger(this.javaClass)!!

    fun run() {
        try {
            val loadBalancer = HibernateLoadBalancer(listOf(), RoundRobin())
            loadBalancer.use {
                var running = true
                var logging = true

                while (running) {
                    try {
                        val option = action(
                            "Quit",
                            "Add new client",
                            "Print all client",
                            "Print all client with id in range",
                            "Update client's data",
                            "Delete client",
                            "Toggle logging",
                            "Change load balancing algorithm"
                        )

                        when (option) {
                            0 -> running = false
                            1 -> {
                                print("Client name: ")
                                val name = scanner.nextLine()
                                println("Contact info: ")
                                print("Street: ")
                                val street = scanner.nextLine()
                                print("Town:")
                                val town = scanner.nextLine()
                                print("Postal code: ")
                                val code = scanner.nextLine()
                                print("Phone number: ")
                                val phone = scanner.nextLine()

                                val client = Client(name = name, street = street, town = town,
                                    postalCode = code, phone = phone)

                                addClient(it.connection(), client)

                                if (logging)
                                    log.info("User successfully persisted")
                            }
                            2 -> {
                                val connection = it.connection()
                                val query = connection.createQuery("from Client", Client::class.java)

                                println("client: ")
                                for (client in query.list())
                                    println(client)
                            }
                            3 -> {
                                print("Lower id: ")
                                val min = scanner.nextLine().toLong()
                                print("Higher id: ")
                                val max = scanner.nextLine().toLong()

                                val clients = clientsBetween(it.connection(), min, max)

                                println("Clients with indices between $min and $max: ")
                                for (client in clients)
                                    println(client)
                            }
                            4 -> {
                                print("Client id: ")
                                val id = scanner.nextLine().toLong()
                                val client = getClient(it.connection(), id)

                                print("New name: ")
                                val name = scanner.nextLine()
                                if (name.isNotBlank())
                                    client.name = name

                                print("New street: ")
                                val street = scanner.nextLine()
                                if (street.isNotBlank())
                                    client.street = street

                                print("New town: ")
                                val town = scanner.nextLine()
                                if (town.isNotBlank())
                                    client.town = town

                                print("New postal code: ")
                                val code = scanner.nextLine()
                                if (code.isNotBlank())
                                    client.postalCode = code

                                print("New username: ")
                                val phone = scanner.nextLine()
                                if (phone.isNotBlank())
                                    client.phone = phone

                                updateClient(it.connection(), client)

                                if (logging)
                                    log.info("Client's contact info successfully updated")
                            }
                            5 -> {
                                print("Enter user ID to delete: ")
                                val clientId = readlnOrNull()?.toLongOrNull()
                                    ?: throw IllegalArgumentException("Invalid ID")

                                deleteClient(it.connection(), clientId)
                                if (logging)
                                    log.info("User successfully removed")
                            }
                            6 -> {
                                logging = !logging
                                it.isLogging = logging
                                log.info("Logging mode set to $logging")
                            }
                            7 -> {
                                val choice = action("Round Robin", "Random connection", "Weighted Response Time")
                                it.loadBalancingMechanism = when(choice) {
                                    0 -> RoundRobin()
                                    1 -> RandConnection()
                                    2 -> WeightedResponseTime()
                                    else -> throw IllegalArgumentException("Invalid option")
                                }
                            }
                            else -> throw IllegalArgumentException("Invalid option")
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is IllegalArgumentException -> {
                                log.warning("${e.message}")
                            }
                            is PersistenceException, is IllegalStateException -> {
                                if (logging)
                                    log.warning("Exception occurred: ${e.message}")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.severe("Fatal exception: ${e.message}")
        }

    }

    private fun action(vararg choices: String): Int {
        println("Choose:")
        choices.forEachIndexed {
            index, choice -> println("$index: $choice")
        }

        print("Your choice: ")
        val selection = scanner.nextLine().toIntOrNull()
            ?: throw IllegalArgumentException("Invalid option")
        return selection
    }

    private fun addClient(connection: Session, client: Client) {
        connection.transaction.apply {
            begin()
            connection.persist(client)
            commit()
        }
    }

    private fun clientsBetween(connection: Session, min: Long, max: Long): List<Client> {
        if (max < min)
            throw IllegalArgumentException("Higher index must not be smaller than lower")

        val query = connection.createQuery("from Client where id between :min and :max", Client::class.java)
        query.setParameter("min", min)
        query.setParameter("max", max)

        return query.list()
    }

    private fun getClient(connection: Session, clientId: Long): Client {
        return connection.find(Client::class.java, clientId)
            ?: throw IllegalArgumentException("Client with id '$clientId does not exist")
    }

    private fun updateClient(connection: Session, client: Client) {
        connection.transaction.apply {
            begin()
            connection.merge(client)
            commit()
        }
    }

    private fun deleteClient(connection: Session, clientId: Long){
        val client = connection.find(Client::class.java, clientId)
            ?: throw IllegalArgumentException("Client with ID '$clientId' does not exist")

        connection.transaction.apply {
            begin()
            connection.remove(client)
            commit()
        }
    }
}