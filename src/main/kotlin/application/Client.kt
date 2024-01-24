package application

import jakarta.persistence.*

@Entity
@Table(name = "klienci")
data class Client(
    @Id

    @Column(name = "idklienta", unique = true)
    val id: Long?=null,

    @Column(name = "nazwa", nullable = false)
    var name: String,

    @Column(name = "ulica", nullable = false)
    var street: String,

    @Column(name = "miejscowosc", nullable = false)
    var town: String,

    @Column(name = "kod", nullable = false)
    var postalCode: String,

    @Column(name = "telefon", nullable = false)
    var phone: String

)
