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
    private var mTopicList: ArrayList<String>? = null
    private var topicItemAdapters: TopicItemAdapters? = null
    private var arrayList: ArrayList<TopicItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topics_fragment)

        recyclerView = findViewById(R.id.my_topic_recycler_view)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        arrayList = ArrayList()

        Database.debates.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayList!!.clear()
                snapshot.children.forEach {
                    arrayList!!.add(TopicItem(it.child("title").value.toString()))
                    Log.d("NewMessage", arrayList.toString())
                }
                topicItemAdapters = TopicItemAdapters(applicationContext, arrayList!!)
                recyclerView?.adapter = topicItemAdapters
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }


}