package cmsc436.changemyview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var debateButton : Button
    private lateinit var observerButton : Button
    private lateinit var logoutButton : Button
    private lateinit var profileButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        debateButton = findViewById(R.id.home_btn_debate)
        observerButton = findViewById(R.id.home_btn_observe)
        logoutButton = findViewById(R.id.home_btn_logout)
        profileButton = findViewById(R.id.home_btn_profile)

        debateButton.setOnClickListener {
            val intent = Intent(this, TopicActivity::class.java)
            //TODO
            //Set flag user in database as a debater
            startActivity(intent)
        }

        observerButton.setOnClickListener{
            val intent = Intent(this, TopicActivity::class.java)
            //TODO
            //Set flag user in database as a observer
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            //TODO
            //log user out and then go back to login activity

        }

        profileButton.setOnClickListener{
            val intent = Intent(this, TopicActivity::class.java)
            //TODO
            //Set flag user in database as a observer
            startActivity(intent)
        }

    }
}