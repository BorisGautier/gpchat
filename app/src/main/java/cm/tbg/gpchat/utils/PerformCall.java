package cm.tbg.gpchat.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import cm.tbg.gpchat.R;
import cm.tbg.gpchat.activities.calling.CallingActivity;
import cm.tbg.gpchat.model.constants.FireCallType;
import cm.tbg.gpchat.utils.network.FireManager;

import io.reactivex.disposables.CompositeDisposable;

public class PerformCall {
    Activity context;
    CompositeDisposable disposables;
    FireManager fireManager;
    public PerformCall(Activity context, FireManager fireManager, CompositeDisposable disposables) {
        this.context = context;
        this.disposables = disposables;
        this.fireManager = fireManager;
    }

    //this will check for call requirements then open the Calling Activity
    public void performCall(final boolean isVideo, final String uid) {
        if (!NetworkHelper.isConnected(context)) {
            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (MyApp.isIsCallActive()) {
            Toast.makeText(context, R.string.there_is_active_call_currently, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isVideo && !PermissionsUtil.hasVideoCallPermissions(context)) {
            Toast.makeText(context, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
            return;
        } else if (!isVideo && !PermissionsUtil.hasVoiceCallPermissions(context)) {
            Toast.makeText(context, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        int message = isVideo ? R.string.video_call_confirmation : R.string.voice_call_confirmation;
        dialog.setMessage(message);
        dialog.setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage(context.getResources().getString(R.string.loading));
                        progressDialog.show();
                        disposables.add(fireManager.isUserBlocked(uid).subscribe(isBlocked -> {
                            progressDialog.dismiss();

                            if (isBlocked) {
                                Util.showSnackbar(context, context.getResources().getString(R.string.error_calling));
                            } else {

                                Intent callScreen = new Intent(context, CallingActivity.class);
                                callScreen.putExtra(IntentUtils.PHONE_CALL_TYPE, FireCallType.OUTGOING);
                                callScreen.putExtra(IntentUtils.ISVIDEO, isVideo);
                                callScreen.putExtra(IntentUtils.UID, uid);
                                context.startActivity(callScreen);
                            }

                        }, throwable -> {
                            progressDialog.dismiss();

                        }));
                    }
                });

        dialog.show();

    }

    //this will check for call requirements then open the Calling Activity
    public void performConferenceCall(final boolean isVideo, String conferenceId) {
        if (!NetworkHelper.isConnected(context)) {
            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (MyApp.isIsCallActive()) {
            Toast.makeText(context, R.string.there_is_active_call_currently, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isVideo && !PermissionsUtil.hasVideoCallPermissions(context)) {
            Toast.makeText(context, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
            return;
        } else if (!isVideo && !PermissionsUtil.hasVoiceCallPermissions(context)) {
            Toast.makeText(context, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        int message = isVideo ? R.string.video_call_confirmation : R.string.voice_call_confirmation;
        dialog.setMessage(message);
        dialog.setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent callScreen = new Intent(context, CallingActivity.class);
                        callScreen.putExtra(IntentUtils.PHONE_CALL_TYPE, FireCallType.OUTGOING);
                        callScreen.putExtra(IntentUtils.ISVIDEO, isVideo);
                        callScreen.putExtra(IntentUtils.UID, conferenceId);
                        callScreen.putExtra(IntentUtils.IS_CONFERENCE_CALL, true);
                        context.startActivity(callScreen);
                    }
                });

        dialog.show();

    }
}
