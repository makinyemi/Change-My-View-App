package cmsc436.changemyview

import android.content.ClipData
import android.os.Bundle
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.debate.*

class chat_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate)


        val adapter = GroupAdapter<GroupieViewHolder>()

        recyclerView.adapter = adapter
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
    }
}

class MessageSent : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
