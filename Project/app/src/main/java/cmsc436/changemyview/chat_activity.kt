package cmsc436.changemyview

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.debate.*
import java.time.LocalDateTime
import java.util.*


class chat_activity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate)

        chatBox.adapter = adapter

        // send messages to the database
        chat_btn_send.setOnClickListener {
            performSendMessage()
        }

        //
        listenForMessages()

    }


    private fun listenForMessages(){


        val reference = Database.chats.child("kamdlkamsdkamsd")

        Log.i("My Activity", "We've entered the method")

        reference.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.i("My Activity", "We've entered the database")

                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    adapter.add(MessageReceived(chatMessage.message))
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
        })
    }

    // this function sends the message and stores it on the database
    private fun performSendMessage(){

        val chatText = chat_edit_message.text.toString()
        var timeStamp = LocalDateTime.now()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        //val debateID = intent.getStringExtra(Database.DEBATE_ID)

        val debateID = "kamdlkamsdkamsd"

        val reference = FirebaseDatabase.getInstance().getReference("/chats").push()

        val chatMessage = ChatMessage(reference.key!!, "ex debate ID", "exUID", chatText, timeStamp.toString())

        if (debateID != null) {
            Database.chats.child(debateID).child(reference.key!!).setValue(chatMessage).addOnSuccessListener {
                Log.d("", "Saved message: ${reference.key!!}")
            }
        }

    }

    // dummy function for test data
    private fun setupDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()

        chatBox.adapter = adapter

        adapter.add(MessageSent("yo yoooo wassguuuud brodie"))
        adapter.add(MessageReceived("how you been?"))
        adapter.add(MessageSent("I been good hbu"))
        adapter.add(MessageSent("i head you went to school X"))
        adapter.add(MessageSent("Is school X better or worse than our old school, y?"))

    }
}

// holds the view for all messages we received
class MessageReceived(val message : String) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatFromText.text = message
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

// holds the view for all messages we send!
class MessageSent(val message : String) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatToText.text = message
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
