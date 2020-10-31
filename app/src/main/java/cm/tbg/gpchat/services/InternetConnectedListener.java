package cm.tbg.gpchat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import cm.tbg.gpchat.model.constants.PendingGroupTypes;
import cm.tbg.gpchat.model.realms.GroupEvent;
import cm.tbg.gpchat.model.realms.Message;
import cm.tbg.gpchat.model.realms.PendingGroupJob;
import cm.tbg.gpchat.model.realms.UnUpdatedStat;
import cm.tbg.gpchat.model.realms.UnUpdatedVoiceMessageStat;
import cm.tbg.gpchat.utils.FireConstants;
import cm.tbg.gpchat.utils.network.FireManager;
import cm.tbg.gpchat.utils.network.GroupManager;
import cm.tbg.gpchat.utils.MyApp;
import cm.tbg.gpchat.utils.RealmHelper;
import cm.tbg.gpchat.utils.ServiceHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmResults;


//this class will indicates when there is Internet connection
public class InternetConnectedListener extends Service {

    private CompositeDisposable disposables = new CompositeDisposable();
    DatabaseReference connectedRef;
    DatabaseReference presenceRef;
    private FireManager fireManager = new FireManager();
    private GroupManager groupManager = new GroupManager();

    public InternetConnectedListener() {
    }

    @Override
    public void onCreate() {
        super.onCreate();


        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        presenceRef = FireConstants.presenceRef.child(FireManager.getUid());
    }

    //send pending messages that are not sent while there is no internet connection
    private void sendPendingMessages() {
        if (RealmHelper.getInstance().getPendingMessages().isEmpty())
            return;

        for (final Message message : RealmHelper.getInstance().getPendingMessages()) {
            ServiceHelper.startNetworkRequest(this, message.getMessageId(), message.getChatId());
        }
    }

    //update messages states (received,read) while there is no internet connection
    private void updateMessagesStats() {
        RealmResults<UnUpdatedStat> unUpdateMessageStat = RealmHelper.getInstance().getUnUpdateMessageStat();
        for (final UnUpdatedStat unUpdatedStat : unUpdateMessageStat) {
            ServiceHelper.startUpdateMessageStatRequest(this, unUpdatedStat.getMessageId(), unUpdatedStat.getMyUid(), null, unUpdatedStat.getStatToBeUpdated());
        }
    }

    //update voice messages states when voice message is listened while there is no internet connection
    private void updateVoiceMessagesStats() {
        RealmResults<UnUpdatedVoiceMessageStat> unUpdatedVoiceMessageStat = RealmHelper.getInstance().getUnUpdatedVoiceMessageStat();
        for (final UnUpdatedVoiceMessageStat unUpdatedStat : unUpdatedVoiceMessageStat) {
            ServiceHelper.startUpdateVoiceMessageStatRequest(this, unUpdatedStat.getMessageId(), unUpdatedStat.getMyUid(), null);
        }
    }

    private void processPendingGroupEvents() {
        for (PendingGroupJob pendingGroupJob : RealmHelper.getInstance().getPendingGroupCreationJobs()) {
            String groupId = pendingGroupJob.getGroupId();
            if (pendingGroupJob.getType() == PendingGroupTypes.CHANGE_EVENT) {
                GroupEvent groupEvent = pendingGroupJob.getGroupEvent();
                disposables.add(groupManager.updateGroup(groupId, groupEvent).subscribe());
            } else {
                disposables.add(groupManager.fetchAndCreateGroup(groupId).subscribe());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        //start service again
        startService(new Intent(this, InternetConnectedListener.class));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    sendPendingMessages();
                    updateMessagesStats();
                    updateVoiceMessagesStats();
                    processPendingGroupEvents();
                    //set online status if the App is in Foreground
                    if (MyApp.isBaseActivityVisible()) {
                        disposables.add(fireManager.setOnlineStatus().subscribe());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        //set last seen when user disconnects from internet
        presenceRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
