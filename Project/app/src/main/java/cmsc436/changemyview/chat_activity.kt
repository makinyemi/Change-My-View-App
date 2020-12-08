package cmsc436.changemyview

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
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
import java.time.format.DateTimeFormatter
import java.util.*


class chat_activity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()


    lateinit var dID : String
    lateinit var uid : String
    lateinit var title: TextView
    lateinit var timer : TextView
    lateinit var countDownTimer : CountDownTimer
    lateinit var team : String
    lateinit var participation : String
    var remTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate)

        dID = intent.getStringExtra(Database.DEBATE_ID).toString()
        chatBox.adapter = adapter
        uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val reference = Database.users.child(uid).child(Database.DEBATES).child(dID)
        
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (px in snapshot.children) {
                    if (px.key.equals(Database.PARTICIPATION)){
                        participation = px.getValue(String :: class.java).toString()
                        if (participation != Database.DEBATING) {
                            message_box.visibility = View.GONE
                        }
                    }
                    else if (px.key.equals(Database.SIDE)) {
                        team = px.getValue(String :: class.java).toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        title = findViewById(R.id.debate_title)
        timer = findViewById(R.id.debate_time_rem)

        Database.debates.child(dID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(DebateTopic::class.java)
                if(data != null) {
                    // Set the title
                    title.text = data.title

                    // Calculate remaining time
                    val formatter = DateTimeFormatter.ISO_DATE_TIME
                    val startTime: LocalDateTime = LocalDateTime.parse(data.startTime, formatter)
                    val currentTime = LocalDateTime.now()
                    val elapsed = currentTime.nano - startTime.nano
                    remTime = 3600000 - (elapsed / 1000000)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        startTimer(remTime.toLong() , dID)

        // send messages to the database
        chat_btn_send.setOnClickListener {
            performSendMessage()
        }


        listenForMessages()

    }


    private fun startTimer(runtime : Long, debateID : String) {

        val surveyIntent = Intent(this, SurveyActivity::class.java)
        surveyIntent.putExtra(Database.DEBATE_ID, debateID)
        surveyIntent.putExtra(SurveyActivity.MODE, SurveyActivity.POST_DEBATE)

        countDownTimer = object : CountDownTimer(runtime, 1000){

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                timer.text = timeLeft.toString()
            }

            override fun onFinish() {
                startActivity(surveyIntent)
            }

        }
        countDownTimer.start()
    }


    private fun listenForMessages(){


        val reference = Database.chats.child(dID)

        Log.i("My Activity", "We've entered the method")

        reference.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.i("My Activity", "We've entered the database")

                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    if (team == chatMessage.team) {
                        adapter.add(MessageSent(chatMessage.message))
                    }

                    else

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

        val debateID = dID

        val reference = FirebaseDatabase.getInstance().getReference("/chats").push()

        val chatMessage = ChatMessage(reference.key!!, debateID, uid!!, chatText, timeStamp.toString(), team)

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
