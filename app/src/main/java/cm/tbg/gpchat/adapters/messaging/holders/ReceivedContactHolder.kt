package cm.tbg.gpchat.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import cm.tbg.gpchat.R
import cm.tbg.gpchat.adapters.messaging.ContactHolderBase
import cm.tbg.gpchat.adapters.messaging.ContactHolderInteraction
import cm.tbg.gpchat.adapters.messaging.holders.base.BaseReceivedHolder
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User

class ReceivedContactHolder(context: Context, itemView: View) : BaseReceivedHolder(context, itemView),ContactHolderBase {

    private val relativeContactInfo: RelativeLayout = itemView.findViewById(R.id.relative_contact_info)
    private val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)
    private val btnMessageContact: Button = itemView.findViewById(R.id.btn_message_contact)
    private val btnAddContact: Button = itemView.findViewById(R.id.btn_add_contact)

    override var contactHolderInteraction: ContactHolderInteraction? = null

    override fun bind(message: Message, user: User) {
        super.bind(message, user)
        //set contact name
        tvContactName.text = message.content



        //send a message to this contact if installed this app
        btnMessageContact.setOnClickListener {
            contactHolderInteraction?.onMessageClick(message.contact)
        }


        //add this contact to phonebook
        btnAddContact.setOnClickListener {
            contactHolderInteraction?.onAddContactClick(message.contact)
        }

    }


}
