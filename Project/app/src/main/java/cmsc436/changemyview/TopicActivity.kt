package cmsc436.changemyview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cmsc436.changemyview.adapters.TopicItemAdapters
import cmsc436.changemyview.model.TopicItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        mTopicList = ArrayList()

        //Gets Debate Topics and
        Database.debates.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mTopicList!!.clear()
                snapshot.children.forEach {
                    mTopicList!!.add(TopicItem(it.child("title").value.toString()))
                    Log.d("NewMessage", mTopicList.toString())
                }
                topicItemAdapters = TopicItemAdapters(applicationContext, mTopicList!!)
                recyclerView?.adapter = topicItemAdapters
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }


}