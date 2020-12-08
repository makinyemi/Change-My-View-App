
class Database {
    // Main database objects
    const val USERS = "users"
    const val CHATS = "chats"
    const val AVERAGE_SCORES = "averageScores"
    const val DEBATES = "debates"

    // Sub-objects
    const val DEBATE_ID = "debateID"
    const val INITIAL_SCORE = "initialScore"
    const val FINAL_SCORE = "finalScore"

    private val database = FirebaseDatabase.getInstance()
    val averageScores = database.getReference(AVERAGE_SCORES)
    val users = database.getReference(USERS)
    val chats = database.getReference(CHATS)
    val debates = database.getReference(DEBATES)

    fun pushChat(debateID: String, uid: String, message: String) {
        val chatID = chats.push().key
        if(chatID != null) {
            val data = ChatMessage(chatID, debateID, uid, message, Date())
            chats.child(debateID).child(chatID).setValue(data)
        }
    }
}