package cm.tbg.gpchat.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cm.tbg.gpchat.R
import cm.tbg.gpchat.adapters.messaging.holders.base.BaseSentHolder
import cm.tbg.gpchat.common.extensions.setHidden
import cm.tbg.gpchat.model.constants.DownloadUploadStat
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.utils.Util


class SentFileHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView) {

    private val tvFileSize: TextView = itemView.findViewById(R.id.tv_file_size)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvFileExtension: TextView = itemView.findViewById(R.id.tv_file_extension)


    private val fileIcon: ImageView = itemView.findViewById(R.id.file_icon)
    override fun bind(message: Message,user: User) {
        super.bind(message,user)
        val fileExtension = Util.getFileExtensionFromPath(message.metadata).toUpperCase()
        tvFileExtension.text = fileExtension
        //set file name
        tvFileName.text = message.metadata

        //file size
        tvFileSize.text = message.fileSize

        fileIcon.setHidden(message.downloadUploadStat != DownloadUploadStat.SUCCESS, true)

    }


}
