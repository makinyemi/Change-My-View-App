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
    private var mTopicList: ArrayList<TopicItem>? = null
    private var topicItemAdapters: TopicItemAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topics_fragment)

        recyclerView = findViewById(R.id.my_topic_recycler_view)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        mTopicList = ArrayList()

        Database.debates.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mTopicList!!.clear()
                snapshot.children.forEach {
                    val title = it.child("title").value.toString()
                    Log.d("NewMessage", title)
                    mTopicList!!.add(TopicItem(title))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        Log.d("Size of list: ", mTopicList!!.size.toString())
        topicItemAdapters = TopicItemAdapters(applicationContext, mTopicList!!)
        recyclerView?.adapter = topicItemAdapters
    }

    private fun fetchTopics() {



    }


}