package cm.tbg.gpchat.adapters.messaging

import cm.tbg.gpchat.model.realms.RealmContact

interface ContactHolderInteraction {
    fun onMessageClick(contact:RealmContact)
    fun onAddContactClick(contact:RealmContact)
}