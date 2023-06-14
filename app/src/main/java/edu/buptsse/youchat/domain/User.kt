package edu.buptsse.youchat.domain

data class User(
    var id: String, var username: String, var password: String, var role: String
) {
    companion object {
        const val ADMIN = "admin"
        const val SELLER = "seller"
        const val CUSTOMER = "customer"
    }
}