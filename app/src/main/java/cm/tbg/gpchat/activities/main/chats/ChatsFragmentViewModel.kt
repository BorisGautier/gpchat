package cm.tbg.gpchat.activities.main.chats

import cm.tbg.gpchat.common.DisposableViewModel
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.utils.TimeHelper
import cm.tbg.gpchat.utils.network.FireManager
import io.reactivex.rxkotlin.addTo
import java.util.*

class ChatsFragmentViewModel : DisposableViewModel() {
    private val currentDownloads = mutableListOf<String>()


    fun fetchUserImage(pos: Int, user: User) {
        if (user.isBroadcastBool)return
        if (TimeHelper.canFetchUserImage(Date().time, user.lastTimeFetchedImage) && !currentDownloads.contains(user.uid)) {
            currentDownloads.add(user.uid)
            FireManager.checkAndDownloadUserThumbImg(user).subscribe({image ->

            }, { throwable ->

            }).addTo(disposables)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}