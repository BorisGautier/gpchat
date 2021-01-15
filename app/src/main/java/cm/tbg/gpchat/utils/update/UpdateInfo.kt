package cm.tbg.gpchat.utils.update

import androidx.annotation.Keep

@Keep
data class UpdateInfo(val latestVersion: Int = -1, val versionsToUpdate: Int = -1, val updateCondition: String = "")

