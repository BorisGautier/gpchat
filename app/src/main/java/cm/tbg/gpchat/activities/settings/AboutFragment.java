package cm.tbg.gpchat.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import cm.tbg.gpchat.R;
import cm.tbg.gpchat.utils.IntentUtils;
import cm.tbg.gpchat.utils.MyApp;



public class AboutFragment extends PreferenceFragmentCompat implements View.OnClickListener {
    private ImageButton emailBtn, websiteBtn, twitterBtn;
    private TextView tvAppInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        initViews(view);

        String appInfo = String.format(getString(R.string.app_info), getString(R.string.app_name));
        tvAppInfo.setText(appInfo);
        emailBtn.setOnClickListener(this);
        websiteBtn.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);
        return view;
    }


    private void initViews(View view) {

        emailBtn = (ImageButton) view.findViewById(R.id.email_btn);
        websiteBtn = (ImageButton) view.findViewById(R.id.website_btn);
        twitterBtn = (ImageButton) view.findViewById(R.id.twitter_btn);
        tvAppInfo = view.findViewById(R.id.tv_app_info);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.email_btn:
                intent = IntentUtils.getSendEmailIntent(getActivity());
                break;

            case R.id.website_btn:
                intent = IntentUtils.getOpenWebsiteIntent(MyApp.context().getString(R.string.website));
                break;

            case R.id.twitter_btn:
                intent = IntentUtils.getOpenTwitterIntent();
                break;
        }
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}

