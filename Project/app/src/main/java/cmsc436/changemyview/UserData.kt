package cmsc436.changemyview

data class UserData(
    val uid: String,
    val username: String,
    val email: String
) {
    companion object {
        const val UID = "UID"
        const val USERNAME = "username"
        const val EMAIL = "email"
    }
}