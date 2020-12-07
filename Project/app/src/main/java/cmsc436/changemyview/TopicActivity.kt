package cmsc436.changemyview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmsc436.changemyview.adapters.TopicItemAdapters
import cmsc436.changemyview.model.TopicItem
import com.google.firebase.database.*
class TopicActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayList:ArrayList<TopicItem> ? = null
    private var topicItemAdapters: TopicItemAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topics_fragment)

        recyclerView = findViewById(R.id.my_topic_recycler_view)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)



        fetchTopics()
    }

    private fun fetchTopics() {
        arrayList = ArrayList()

        Database.debates.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val title = snapshot.getValue(DebateTopic::class.java)
                Log.d("NewMessage", snapshot.toString())
                if (title != null) {
                    arrayList!!.add(TopicItem(title.title))
                }
            }
        })
        topicItemAdapters = TopicItemAdapters(applicationContext, arrayList!!)
        recyclerView?.adapter = topicItemAdapters

    }

    private fun setTopicsToList() : ArrayList<TopicItem>{

        var items:ArrayList<TopicItem> = ArrayList()

        items.add(TopicItem("To Vote or Not to Vote"))
        items.add(TopicItem("Peanut Butter is Better than Jelly"))
        items.add(TopicItem("Covid a Hoax?"))
        items.add(TopicItem("Football is "))


        return items
    }

}