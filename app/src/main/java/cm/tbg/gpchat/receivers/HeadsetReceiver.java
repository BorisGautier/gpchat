package cm.tbg.gpchat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cm.tbg.gpchat.events.HeadsetStateChanged;
import cm.tbg.gpchat.model.constants.HeadsetState;

import org.greenrobot.eventbus.EventBus;



//indicates when the user conencts a head phone to the device
public class HeadsetReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    sendStateEvent(HeadsetState.UNPLUGGED);
                    break;
                case 1:
                    sendStateEvent(HeadsetState.PLUGGED_IN);
                    break;
                default:
            }
        }
    }

    //update activity with the state when user connect/disconnect headphone
    private void sendStateEvent(int state) {
        EventBus.getDefault().post(new HeadsetStateChanged(state));
    }
}