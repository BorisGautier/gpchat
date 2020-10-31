package cm.tbg.gpchat.adapters.messaging.holders

import ak.sh.ay.musicwave.MusicWave
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cm.tbg.gpchat.R
import cm.tbg.gpchat.adapters.messaging.AudibleBase
import cm.tbg.gpchat.adapters.messaging.AudibleInteraction
import cm.tbg.gpchat.adapters.messaging.holders.base.BaseReceivedHolder
import cm.tbg.gpchat.common.extensions.setHidden
import cm.tbg.gpchat.model.AudibleState
import cm.tbg.gpchat.model.constants.DownloadUploadStat
import cm.tbg.gpchat.model.constants.DownloadUploadStat.SUCCESS
import cm.tbg.gpchat.model.realms.Message
import cm.tbg.gpchat.model.realms.User
import cm.tbg.gpchat.utils.AdapterHelper

class ReceivedAudioHolder(context: Context, itemView: View) : BaseReceivedHolder(context, itemView), AudibleBase {
    val waveView: MusicWave = itemView.findViewById(R.id.wave_view)
    val playBtn: ImageView = itemView.findViewById(R.id.voice_play_btn)
    val seekBar: SeekBar = itemView.findViewById(R.id.voice_seekbar)
    private val tvAudioSize: TextView = itemView.findViewById(R.id.tv_audio_size)
    val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
    val imgHeadset: ImageView = itemView.findViewById(R.id.img_headset)

    override var audibleState: LiveData<Map<String, AudibleState>>? = null
    override var audibleInteraction: AudibleInteraction? = null


    override fun bind(message: Message, user: User) {
        super.bind(message, user)


        //Set Initial Values
        seekBar.progress = 0
        playBtn.setImageResource(AdapterHelper.getPlayIcon(false))

        //if it's sending set the audio size
        if (message.downloadUploadStat != DownloadUploadStat.SUCCESS) {
            tvAudioSize.visibility = View.VISIBLE
            tvAudioSize.text = message.metadata
        } else {
            //otherwise hide the audio textview
            tvAudioSize.visibility = View.GONE
        }

        tvDuration.text = message.mediaDuration

        playBtn.setHidden(message.downloadUploadStat != SUCCESS, true)
        lifecycleOwner?.let {


            audibleState?.observe(it, Observer { audioRecyclerStateMap ->
                if (audioRecyclerStateMap.containsKey(message.messageId)) {
                    audioRecyclerStateMap[message.messageId]?.let { mAudioRecyclerState ->


                        if (mAudioRecyclerState.currentDuration != null)
                            tvDuration.text = mAudioRecyclerState.currentDuration

                        if (mAudioRecyclerState.progress != -1) {
                            seekBar.progress = mAudioRecyclerState.progress
                        }

                        if (mAudioRecyclerState.max != -1) {
                            val max = mAudioRecyclerState.max
                            seekBar.max = max
                        }

                        if (mAudioRecyclerState.isPlaying) {
                            imgHeadset.visibility = View.GONE
                            waveView.visibility = View.VISIBLE
                        } else {
                            imgHeadset.visibility = View.VISIBLE
                            waveView.visibility = View.GONE
                        }

                        if (mAudioRecyclerState.waves != null)
                            waveView.updateVisualizer(mAudioRecyclerState.waves)

                        playBtn.setImageResource(AdapterHelper.getPlayIcon(mAudioRecyclerState.isPlaying))

                    }
                } else {
                    tvDuration.text = message.mediaDuration
                    imgHeadset.visibility = View.VISIBLE
                    waveView.visibility = View.GONE
                }
            })
        }

        playBtn.setOnClickListener {
            interaction?.onContainerViewClick(adapterPosition, itemView, message)
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    audibleInteraction?.onSeek(message, progress, seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


    }

}
