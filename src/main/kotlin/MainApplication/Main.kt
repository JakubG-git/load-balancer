package MainApplication

import hibernate.HibernateLoadBalancer
import jakarta.persistence.PersistenceException
import loadBalancer.loadBalancingMechanism.RandConnection
import loadBalancer.loadBalancingMechanism.RoundRobin
import loadBalancer.loadBalancingMechanism.WeightedResponseTime
import loggerDB.LoggerDB
import org.hibernate.Session
import org.hibernate.query.Query
import java.lang.IllegalStateException
import java.util.Scanner

class Main {
    private val scanner = Scanner(System.`in`)
    private val log = LoggerDB.getLogger(this.javaClass)!!

    fun main() {
        val loadBalancer = HibernateLoadBalancer(listOf(), RoundRobin())
        loadBalancer.use {
            var run = true
            var logging = true

            while (run) {
                try {
                    val option = action()

                    when (option) {
                        0 -> run = false
                        1 -> {
                            print("User name: ")
                            val name = scanner.nextLine()
                            addUser(loadBalancer, name)
                            if (logging)
                                log.info("User successfully persisted")
                        }
                        2 -> {
                            val connection = it.connection()
                            val query: Query<User> //connection.createQuery("", User.class)

                            println("Users: ")
                            for (user in query.list())
                                println(user)
                        }
                        3 -> {
                            print("Lower id: ")
                            val min = scanner.nextLine().toInt()
                            print("Higher id: ")
                            val max = scanner.nextLine().toInt()

                            val users = usersBetween(it.connection(), min, max)

                            println("Users with indices between $min and $max: ")
                            for (user in users)
                                println(user)
                        }
                        4 -> {
                            print("User id: ")
                            val id = scanner.nextLine().toInt()
                            print("New username: ")
                            val name = scanner.nextLine()

                            updateUser(it.connection(), id, name)

                            if (logging)
                                log.info("User's name successfully updated")
                        }
                        5 -> {
                            print("Enter user ID to delete: ")
                            val userId = readlnOrNull()?.toLongOrNull() ?: throw IllegalArgumentException("Invalid ID")

                            deleteUser(loadBalancer, userId)
                            if (logging)
                                log.info("User successfully removed")
                        }
                        6 -> {
                            logging = !logging
                            it.isLogging = logging
                            log.info("Logging mode set to $logging")
                        }
                        7 -> {
                            val choice = action()
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
                        is PersistenceException, is IllegalArgumentException, is IllegalStateException -> {
                            if (logging)
                                log.warning("Exception occurred: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    fun action(vararg choices: String): Int {
        var selection: Int?
        println("Choose:")
        choices.forEachIndexed {
            index, choice -> println("$index: $choice")
        }
        do {
            println("Your choice:")
            val input = scanner.nextLine()
            selection = input.toIntOrNull()

            if (selection == null || selection !in choices.indices) {
                println("Invalid input, try again")
                selection = null
            }

        } while (selection == null)
        return selection
    }

    private fun addUser(loadBalancer: HibernateLoadBalancer, name: String) {
        val user = User(name)
        val connection = loadBalancer.connection()
        connection.beginTransaction()
        connection.persist(user)
        connection.transaction.commit()
    }

    private fun usersBetween(connection: Session, min: Int, max: Int): List<User> {
        if (max <= min)
            throw IllegalArgumentException("Higher index must be bigger than lower")

        val query: Query<User>
        query.setParameter(1, min)
        query.setParameter(2, max)

        return query.list()
    }

    private fun updateUser(connection: Session, userId: Int, newName: String) {
        val user = connection.find(User.class, userId) ?: throw IllegalArgumentException("User with ID '$userId' does not exist")
        connection.transaction.apply {
            begin()
            user.name = newName
            connection.merge(user)
            commit()
        }
    }

    private fun deleteUser(loadBalancer: HibernateLoadBalancer, userId: Long){
        val connection = loadBalancer.connection()
        val user = connection.find(User.class, userId) ?: throw IllegalArgumentException("User with ID '$userId' does not exist")

        connection.transaction.apply {
            begin()
            connection.remove(user)
            commit()
        }
    }
}