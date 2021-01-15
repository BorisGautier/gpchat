package cm.tbg.gpchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cm.tbg.gpchat.R;
import cm.tbg.gpchat.activities.authentication.AuthenticationActivity;
import cm.tbg.gpchat.activities.main.MainActivity;
import cm.tbg.gpchat.activities.setup.SetupUserActivity;
import cm.tbg.gpchat.utils.DetachableClickListener;
import cm.tbg.gpchat.utils.network.FireManager;
import cm.tbg.gpchat.utils.PermissionsUtil;
import cm.tbg.gpchat.utils.SharedPreferencesManager;

//this is the First Activity that launched when user starts the App
public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!SharedPreferencesManager.hasAgreedToPrivacyPolicy()) {
            startPrivacyPolicyActivity();
            //check if user isLoggedIn
        } else if (!FireManager.isLoggedIn()) {
            startLoginActivity();
            //request permissions if there are no permissions granted
        } else if (FireManager.isLoggedIn() && !PermissionsUtil.hasPermissions(this)) {
            requestPermissions();
        } else {
            startNextActivity();
        }

    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, PERMISSION_REQUEST_CODE);
    }


    private void startLoginActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startPrivacyPolicyActivity() {
        Intent intent = new Intent(this, AgreePrivacyPolicyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startNextActivity() {
        if (!SharedPreferencesManager.hasAgreedToPrivacyPolicy()) {
            startPrivacyPolicyActivity();
        } else if (SharedPreferencesManager.isUserInfoSaved()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, SetupUserActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void showAlertDialog() {

        DetachableClickListener positiveClickListener = DetachableClickListener.wrap(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions();

            }
        });

        DetachableClickListener negativeClickListener = DetachableClickListener.wrap(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });


        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(R.string.missing_permissions)
                .setMessage(R.string.you_have_to_grant_permissions)
                .setPositiveButton(R.string.ok, positiveClickListener)
                .setNegativeButton(R.string.no_close_the_app, negativeClickListener)
                .create();

        //avoid memory leaks
        positiveClickListener.clearOnDetach(builder);
        negativeClickListener.clearOnDetach(builder);
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtil.permissionsGranted(grantResults)) {
            if (!FireManager.isLoggedIn())
                startLoginActivity();
            else
                startNextActivity();
        } else
            showAlertDialog();
    }

}



