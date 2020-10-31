package cm.tbg.gpchat.adapters.messaging.holders.base

import android.content.Context
import android.view.View
import android.widget.ImageView
import cm.tbg.gpchat.R
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.utils.AdapterHelper

open class BaseSentHolder(context: Context, itemView: View) : BaseHolder(context,itemView) {

    var messageStatImg:ImageView? = itemView.findViewById(R.id.message_stat_img)


    override fun bind(message: Message, user: User) {
        super.bind(message, user)


        //imgStat (received or read)
        messageStatImg?.setImageResource(AdapterHelper.getMessageStatDrawable(message.messageStat))


    }




}

