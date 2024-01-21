@Entity
@Table(name = "clients")
data class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "address")
    val address: String,

    @Column(name = "phone")
    val phone: String

) {
    // Method to update the address of the client
    fun updateAddress(newStreet: String, newCity: String, newPostalCode: String) {
        this.street = newStreet
        this.city = newCity
        this.postalCode = newPostalCode
    }

    // Method to print the client details
    fun printDetails() {
        println("Client ID: $idClient")
        println("Name: $name")
        println("Address: $street, $city, $postalCode")
        println("Phone: $phone")
    }
}
