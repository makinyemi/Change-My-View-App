package cmsc436.changemyview.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import cmsc436.changemyview.Database
import cmsc436.changemyview.R
import cmsc436.changemyview.SurveyActivity
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
            val intent = Intent(context, SurveyActivity::class.java)
            intent.putExtra(Database.DEBATE_ID,topicItem.id)
            intent.putExtra(SurveyActivity.PARTICIPATION,topicItem.participation)
            intent.putExtra(SurveyActivity.MODE,SurveyActivity.PRE_DEBATE)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
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