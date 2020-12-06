package cmsc436.changemyview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.debate.*

class chat_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate)


        val adapter = GroupAdapter<GroupieViewHolder>()

        chatBox.adapter = adapter

        adapter.add(MessageSent())
        adapter.add(MessageReceived())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageReceived())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
        adapter.add(MessageReceived())
        adapter.add(MessageSent())
        adapter.add(MessageSent())
    }
}

class MessageReceived : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class MessageSent : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
