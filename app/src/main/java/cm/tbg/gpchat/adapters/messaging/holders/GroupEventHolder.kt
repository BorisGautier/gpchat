package cm.tbg.gpchat.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cm.tbg.gpchat.R
import cm.tbg.gpchat.model.realms.GroupEvent
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User

 class GroupEventHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvGroupEvent: TextView = itemView.findViewById(R.id.tv_group_event)

     fun bind(message: Message,user: User){
         tvGroupEvent.text = GroupEvent.extractString(message.content, user.group.users)
     }


}