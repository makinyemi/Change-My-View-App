package cmsc436.changemyview

import java.time.LocalDateTime
import java.util.*

data class ChatMessage(
    val chatID: String,
    val debateID: String,
    val uid: String,
    val message: String,
    val timestamp: String,
    val team : String
) {
    constructor() : this("","","","","", "")
    companion object {
        const val CHAT_ID = "chatID"
        const val DEBATE_ID = "debateID"
        const val UID = "UID"
        const val MESSAGE = "message"
        const val TIMESTAMP = "timestamp"
        const val TEAM = "team"
    }
}