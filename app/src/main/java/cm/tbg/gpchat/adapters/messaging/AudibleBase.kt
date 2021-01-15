package cm.tbg.gpchat.adapters.messaging

import androidx.lifecycle.LiveData
import cm.tbg.gpchat.model.AudibleState

interface AudibleBase {
    var audibleState: LiveData<Map<String, AudibleState>>?
    var audibleInteraction:AudibleInteraction?
}