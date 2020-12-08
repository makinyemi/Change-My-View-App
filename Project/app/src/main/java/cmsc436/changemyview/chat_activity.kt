package cmsc436.changemyview

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

    lateinit var debate : DebateTopic
    lateinit var uid : String
    lateinit var timer : TextView
    lateinit var countDownTimer : CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate)

        chatBox.adapter = adapter

        uid = FirebaseAuth.getInstance().currentUser?.uid!!
        timer = findViewById(R.id.debate_time_rem)
        val dID = intent.getStringExtra(Database.DEBATE_ID)


        if (dID != null) {

            val reference = FirebaseDatabase.getInstance().getReference("/debates").child(dID)



            Database.debates.child(dID).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    val debateTopic = p0.getValue(DebateTopic :: class.java)
                    startTimer()
                    if (debateTopic != null) {
                        debate = debateTopic
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })


        }

        // send messages to the database
        chat_btn_send.setOnClickListener {
            performSendMessage()
        }


        listenForMessages()

    }


    private fun startTimer(runtime : Long, debateID : String) {

        val resultsIntent = Intent(this, ResultsActivity::class.java)
        resultsIntent.putExtra("DEBATE_ID", debateID)

        countDownTimer = object : CountDownTimer(runtime, 1000){

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                timer.text = timeLeft.toString()
            }

            override fun onFinish() {
                startActivity(resultsIntent)
            }

        }
    }


    private fun listenForMessages(){


        val reference = Database.chats.child(debate.debateID)

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

        val debateID = debate.debateID

        val reference = FirebaseDatabase.getInstance().getReference("/chats").push()

        val chatMessage = ChatMessage(reference.key!!, debateID, uid!!, chatText, timeStamp.toString())

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
