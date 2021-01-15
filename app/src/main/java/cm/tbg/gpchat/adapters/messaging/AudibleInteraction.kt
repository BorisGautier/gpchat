package cm.tbg.gpchat.adapters.messaging

import cm.tbg.gpchat.model.realms.Message

interface AudibleInteraction {
    fun onSeek(message:Message,progress:Int,max:Int)
}