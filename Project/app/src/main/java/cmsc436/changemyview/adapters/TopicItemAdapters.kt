package cmsc436.changemyview.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cmsc436.changemyview.R
import cmsc436.changemyview.TopicQuestionFragment
import cmsc436.changemyview.VoteFragment
import cmsc436.changemyview.model.TopicItem


class TopicItemAdapters(var context: Context, var arrayList: ArrayList<TopicItem>) :
    RecyclerView.Adapter<TopicItemAdapters.ItemHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.topic_grid_layout_list, parent, false)

        return ItemHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var topicItem:TopicItem = arrayList[position]


        holder.button!!.text = topicItem.title

        holder.button!!.setOnClickListener {
//            val intent = Intent(context, TopicsQuestionsFragmentV::class.java)
//            context.startActivity(intent)
            val mTopicQuestionFragment = TopicQuestionFragment()

            val activity: AppCompatActivity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .add(R.id.topic_question_layout, mTopicQuestionFragment)
                .addToBackStack(null)
                .commit()

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        var button: TextView? = itemView.findViewById<TextView>(R.id.topic1)



        override fun onClick(v: View?) {
        }
    }
}