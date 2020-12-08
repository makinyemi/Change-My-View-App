package cmsc436.changemyview

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Database {

    companion object {
        // Main database objects
        const val USERS = "users"
        const val CHATS = "chats"
        const val AVERAGE_SCORES = "averageScores"
        const val DEBATES = "debates"
//        const val DEBATE_ID = "debateID"
        const val QUEUE = "queue"

        // Sub-objects
        const val DEBATE_ID = "debateID"
        const val INITIAL_SCORE = "initialScore"
        const val FINAL_SCORE = "finalScore"
        const val SIDE = "side"
        const val LEFT = "left"
        const val RIGHT = "right"
        const val PARTICIPATION = "participation"
        const val DEBATING = "debating"
        const val OBSERVING = "observing"

        const val NONE = "none"

        private val database = FirebaseDatabase.getInstance()
        val averageScores = database.getReference(AVERAGE_SCORES)
        val users = database.getReference(USERS)
        val chats = database.getReference(CHATS)
        val debates = database.getReference(DEBATES)

        fun pushUser(uid:String, username: String, email: String) {
            val data = UserData(uid, username, email)
            users.child(uid).setValue(data)
        }

        fun pushDebateTopic(title: String, questions: List<String>, runtime: Int) {
            val debateID = debates.push().key
            if(debateID != null) {
                val data = DebateTopic(
                    debateID,
                    title,
                    questions,
                    runtime,
                    null,
                )
                debates.child(debateID).setValue(data)
            }
        }

        fun startDebate(debateID: String) {
            val debate = debates.child(debateID)
            debate.child(DebateTopic.START_TIME).setValue(LocalDateTime.now().format(
                DateTimeFormatter.ISO_DATE_TIME))
        }
    }

}