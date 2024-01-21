data class Client(
    var idClient: Int,
    var name: String,
    var street: String,
    var city: String,
    var postalCode: String,
    var phone: String
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
