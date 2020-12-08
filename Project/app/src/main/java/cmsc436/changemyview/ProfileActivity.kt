package cmsc436.changemyview

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val userName = findViewById<TextView>(R.id.userName)
        val email = findViewById<TextView>(R.id.profile_email)

        //Sets users name on profile page on create
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Database.users.child(currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val usersName = snapshot.child("username").value.toString()
                        userName.text = usersName
                        email.text = emailData
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

}