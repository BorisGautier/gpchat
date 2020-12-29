/*
 * Created by Boris Gautier on 29/$today.mounth/2020
 */

package cm.tbg.gpchat.utils;

import android.content.Context;

import cm.tbg.gpchat.R;
import cm.tbg.gpchat.utils.network.FireManager;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class SinchConfig {
    private static final String DEBUG_ENVIRONMENT = "sandbox.sinch.com";
    private static final String RELEASE_ENVIRONMENT = "clientapi.sinch.com";

    public static SinchClient getSinchClient(Context context) {
        return Sinch.getSinchClientBuilder()
                .context(context.getApplicationContext())
                .userId(FireManager.getUid())
                .applicationKey(MyApp.context().getString(R.string.sinch_app_id))
                .applicationSecret(MyApp.context().getString(R.string.sinch_app_secret))
                .environmentHost(RELEASE_ENVIRONMENT)
                .build();
    }
}
