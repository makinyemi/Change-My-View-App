package cmsc436.changemyview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        //Database.pushChat("exampleDebateID", "exampleUID", "Lol, idk whats happening")
        Database.pushDebateTopic(
            "Example Debate Topic",
            listOf("Question 1", "Question 2", "Question 3", "Question 4", "Question 5"),
            3600
        )
    }
}