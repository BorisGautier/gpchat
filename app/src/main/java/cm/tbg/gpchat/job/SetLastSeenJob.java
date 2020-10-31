package cm.tbg.gpchat.job;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import cm.tbg.gpchat.model.constants.LastSeenStates;
import cm.tbg.gpchat.utils.JobSchedulerSingleton;
import cm.tbg.gpchat.utils.MyApp;
import cm.tbg.gpchat.utils.SharedPreferencesManager;

import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetLastSeenJob extends BaseJob {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        int lastSeenState = SharedPreferencesManager.getLastSeenState();

        if (MyApp.isBaseActivityVisible() && lastSeenState != LastSeenStates.ONLINE) {
            disposables.add(fireManager.setOnlineStatus().subscribe(()->{
                jobFinished(jobParameters,false);
            }));
        } else if (!MyApp.isBaseActivityVisible() && lastSeenState != LastSeenStates.LAST_SEEN) {
            disposables.add(fireManager.setLastSeen().subscribe(()->{
                jobFinished(jobParameters,false);
            },throwable -> {

            }));
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        disposables.dispose();
        return false;
    }

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, SetLastSeenJob.class);

        JobInfo.Builder builder = new JobInfo.Builder(JobIds.JOB_ID_SET_LAST_SEEN, component)
                .setPersisted(true)
                .setPeriodic(TimeUnit.MINUTES.toMillis(5))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);


        JobSchedulerSingleton.getInstance().schedule(builder.build());
    }

}
