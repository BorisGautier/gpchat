package cm.tbg.gpchat.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cm.tbg.gpchat.R
import cm.tbg.gpchat.adapters.messaging.holders.base.BaseReceivedHolder
import cm.tbg.gpchat.common.extensions.setHidden
import cm.tbg.gpchat.model.constants.DownloadUploadStat
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.utils.Util

class ReceivedFileHolder(context: Context, itemView: View) : BaseReceivedHolder(context, itemView) {

    private val fileIcon: ImageView
    private val tvFileName: TextView
    private val tvFileExtension: TextView
    private val tvFileSize: TextView

    override fun bind(message: Message, user: User) {
        super.bind(message, user)
        //get file extension
        val fileExtension = Util.getFileExtensionFromPath(message.metadata).toUpperCase()
        tvFileExtension.text = fileExtension
        //set file name
        tvFileName.text = message.metadata

        //file size
        tvFileSize.text = message.fileSize

        fileIcon.setHidden(message.downloadUploadStat != DownloadUploadStat.SUCCESS, true)


    }

    init {
        fileIcon = itemView.findViewById(R.id.file_icon)
        tvFileName = itemView.findViewById(R.id.tv_file_name)
        tvFileExtension = itemView.findViewById(R.id.tv_file_extension)
        tvFileSize = itemView.findViewById(R.id.tv_file_size)
    }


}

