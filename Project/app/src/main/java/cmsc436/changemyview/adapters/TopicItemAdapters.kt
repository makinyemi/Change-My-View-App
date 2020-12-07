package cmsc436.changemyview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cmsc436.changemyview.R
import cmsc436.changemyview.model.TopicItem

class TopicItemAdapters(var context: Context, var arrayList: ArrayList<TopicItem>) :
    RecyclerView.Adapter<TopicItemAdapters.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.topic_grid_layout_list, parent, false)

        return ItemHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var topicItem:TopicItem = arrayList[position]

        holder.button!!.text = topicItem.title

        holder.button!!.setOnClickListener {
            Toast.makeText(context, topicItem.title, Toast.LENGTH_LONG)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: TextView? = itemView.findViewById<TextView>(R.id.topic1)
    }
}