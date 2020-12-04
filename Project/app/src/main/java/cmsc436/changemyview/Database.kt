package cmsc436.changemyview

import com.google.firebase.database.FirebaseDatabase
import java.util.*

class Database {

    companion object {
        const val USERS = "users"
        const val CHATS = "chats"
        const val DEBATES = "debates"

        val database = FirebaseDatabase.getInstance()
        val users = database.getReference(USERS)
        val chats = database.getReference(CHATS)
        val debates = database.getReference(DEBATES)

        fun pushUser() {

        }

        fun pushChat(debateID: String, uid: String, message: String) {
            val chatID = chats.push().key
            if(chatID != null) {
                val data = ChatMessage(chatID, debateID, uid, message, Date())
                chats.child(chatID).setValue(data)
            }
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
                    null,
                    null,
                    0, 0
                )
                debates.child(debateID).setValue(data)
            }
        }

        fun startDebate(debateID: String) {
            val debate = debates.child(debateID)
            debate.child(DebateTopic.START_TIME).setValue(Date())
        }

        fun addUserToDebateSide(debateID: String, uid: String, side: Boolean) {
            val debate = debates.child(debateID)

            // side == true -> places user on the LEFT team
            if(side) {
                debate.child(DebateTopic.LEFT_UIDS).push().setValue(uid)
            }

            // side == false -> places user on the RIGHT team
            else {
                debate.child(DebateTopic.RIGHT_UIDS).push().setValue(uid)
            }
        }
    }

}