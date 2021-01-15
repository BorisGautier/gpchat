package cm.tbg.gpchat.utils;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import cm.tbg.gpchat.model.constants.PendingGroupTypes;
import cm.tbg.gpchat.model.realms.Message;
import cm.tbg.gpchat.model.realms.PendingGroupJob;
import cm.tbg.gpchat.model.realms.UnUpdatedStat;
import cm.tbg.gpchat.model.realms.UnUpdatedVoiceMessageStat;

import io.realm.RealmResults;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UnProcessedJobs {

    public static void process(Context context) {


        RealmResults<Message> unProcessedNetworkRequests = RealmHelper.getInstance().getUnProcessedNetworkRequests();
        for (Message unProcessedNetworkRequest : unProcessedNetworkRequests) {
            if (!doesJobExists(unProcessedNetworkRequest.getMessageId(), false)) {
                ServiceHelper.startNetworkRequest(context, unProcessedNetworkRequest.getMessageId(), unProcessedNetworkRequest.getChatId());

            }
        }

        for (UnUpdatedVoiceMessageStat unUpdatedVoiceMessageStat : RealmHelper.getInstance().getUnUpdatedVoiceMessageStat()) {
            if (!doesJobExists(unUpdatedVoiceMessageStat.getMessageId(), true)) {
                ServiceHelper.startUpdateVoiceMessageStatRequest(context, unUpdatedVoiceMessageStat.getMessageId(), null, unUpdatedVoiceMessageStat.getMyUid());
            }
        }

        for (UnUpdatedStat unUpdatedStat : RealmHelper.getInstance().getUnUpdateMessageStat()) {

            if (!doesJobExists(unUpdatedStat.getMessageId(), false)) {
                ServiceHelper.startUpdateMessageStatRequest(context, unUpdatedStat.getMessageId(), unUpdatedStat.getMyUid(), null, unUpdatedStat.getStatToBeUpdated());
            }

        }

        for (PendingGroupJob pendingGroupJob : RealmHelper.getInstance().getPendingGroupCreationJobs()) {
            String groupId = pendingGroupJob.getGroupId();
            if (!doesJobExists(groupId, false)) {
                if (pendingGroupJob.getType() == PendingGroupTypes.CHANGE_EVENT) {
                    ServiceHelper.updateGroupInfo(context, pendingGroupJob.getGroupId(), pendingGroupJob.getGroupEvent());
                } else {
                    ServiceHelper.fetchAndCreateGroup(context, groupId);
                }
            }
        }

    }

    private static boolean doesJobExists(String id, boolean isVoiceMessage) {
        int jobId = RealmHelper.getInstance().getJobId(id, isVoiceMessage);
        if (jobId == -1)
            return false;
        for (JobInfo jobInfo : JobSchedulerSingleton.getInstance().getAllPendingJobs()) {
            if (jobInfo.getId() == jobId)
                return true;
        }
        return false;
    }

}
