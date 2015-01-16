package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.Dialogs.AboutDialogFragment;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Danhmuc;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;

public class MainActivity extends BaseActivity implements GridView.OnItemClickListener{

    private GridView gvDanhMuc;
    private UiLifecycleHelper uiHelper;
    private InterstitialAd interstitialAd;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, PlayerService.class));
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.banner_full));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(isFirst) {
                    interstitialAd.show();
                    isFirst = false;
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void setActionView() {
        gvDanhMuc = (GridView) findViewById(R.id.main_gridview_item);
        gvDanhMuc.setOnItemClickListener(this);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new DanhMucAdapter(this));
        swingBottomInAnimationAdapter.setAbsListView(gvDanhMuc);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        gvDanhMuc.setAdapter(swingBottomInAnimationAdapter);
    }

    @Override
    public void onPlayerChange(String action) {

    }

    @Override
    public void onConnectServcie() {
        if( service != null && service.getCurrentLession() != null && service.getCurrentLession().getId() > 0){
            showController();
        }
        if(mApp.getLastPlay().equals(""))
            return;
        String[] listID = mApp.getLastPlay().split(",");
        if(listID.length != 2)
            return;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 5) {
            //sendRequestDialog(true);
            //login();
            Intent i = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(i);
            return;
        }
        Constants.selectedModel = new Danhmuc(position + 1);
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        i.putExtra(Constants.EXTRA_ID, position + 1);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    SessionStatusCallback statusCallback = new SessionStatusCallback();

    /**
     * Class Listen to Changing of Login facebook
     *
     */
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (exception != null) {
                //Have a exception
            }
            if (state.isOpened()) {

//                Request.newMeRequest(session, new Request.GraphUserCallback() {
//                    @Override
//                    public void onCompleted(final GraphUser graphUser, Response response) {
//                        if (graphUser != null) {
//                            Log.d("ddd", graphUser.getId() + "");
//                            Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
//                                @Override
//                                public void onCompleted(List<GraphUser> graphUsers, Response response) {
//                                    if (graphUsers != null) {
//                                        Log.d("GraphUsers: ", graphUser.toString());
//                                        for (int i = 0; i < graphUsers.size(); i++) {
//                                            Log.d("FBID", graphUsers.get(i).getId());
//                                        }
//                                    }
//                                    if(response != null)
//                                    Log.e("Error", response.toString());
//                                }
//                            }).executeAsync();
//                        }
//                    }
//                }).executeAsync();

                //Login success
                sendRequestDialog(false);
            } else if (state.isClosed()) {
                //user logout
            }
        }
    }

    private void login(){
        List<String> permissions = new ArrayList<>();
//        permissions.add("publish_actions");
        permissions.add("email");
        //permissions.add("read_friendlists");
        permissions.add("user_friends");
        Session.openActiveSession(this,true,permissions,statusCallback);
    }

    private void sendRequestDialog(Boolean isFirst) {

        Session session = Session.getActiveSession();
        if (session == null || !session.isOpened()) {
            if(!isFirst)
                return;
            List<String> permissions = new ArrayList<>();
//            permissions.add("publish_actions");
//            permissions.add("email");
//            permissions.add("read_friendlists");
            Session.openActiveSession(this,true,permissions,statusCallback);
            return;
        }
        //100002994386631

        Bundle params = new Bundle();
        params.putString("message", "Learn English with Effortless English https://play.google.com/store/apps/details?id=com.estorm.effortlessenglish");
        params.putString("data",
                "{\"badge_of_awesomeness\":\"1\"," +
                        "\"social_karma\":\"5\"}");

// Optionally provide a 'to' param to direct the request at a specific user
// Give the action and object information
        params.putString("action_type", "send");
        params.putString("object_id", "1524714071143955");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this,
                        Session.getActiveSession(),
                        params)).setOnCompleteListener(new WebDialog.OnCompleteListener() {
            @Override
            public void onComplete(Bundle values, FacebookException error) {
                if (error != null) {
                    if (error instanceof FacebookOperationCanceledException) {
                        Toast.makeText(getApplicationContext(),
                                "Request cancelled",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Network Error",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final String requestId = values.getString("request");
                    if (requestId != null) {
                        Toast.makeText(getApplicationContext(),
                                "Request sent",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Request cancelled",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        })
                .build();
        requestsDialog.show();
//
//        WebDialog requestsDialog = (
//                new WebDialog.RequestsDialogBuilder(this,
//                        Session.getActiveSession(),
//                        params))
//                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
//
//                    @Override
//                    public void onComplete(Bundle values,
//                                           FacebookException error) {
//                        if (error != null) {
//                            if (error instanceof FacebookOperationCanceledException) {
//                                Toast.makeText(getApplicationContext(),
//                                        "Request cancelled",
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Network Error",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            final String requestId = values.getString("request");
//                            if (requestId != null) {
//                                Toast.makeText(getApplicationContext(),
//                                        "Request sent",
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Request cancelled",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                })
//                .build();
//        requestsDialog.show();
    }

    /**
     * This is function process for action press Button Back
     */
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if(interstitialAd != null && interstitialAd.isLoaded())
                interstitialAd.show();
            if(Build.VERSION.SDK_INT < 11)
                stopService(new Intent(MainActivity.this, PlayerService.class));
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit",
                Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
class DanhMucAdapter extends BaseAdapter{

    private Context mContext;

    public DanhMucAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DanhMucHolder holder;
        if(convertView == null){
            holder = new DanhMucHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_in_main, null);
            holder.image = (ImageView)convertView.findViewById(R.id.gridViewImage);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.gridViewTitleText);
            convertView.setTag(holder);
        }else
            holder = (DanhMucHolder)convertView.getTag();
        final Danhmuc danhmuc = new Danhmuc(position + 1);
        if(danhmuc.getImage() > 0)
            holder.image.setImageResource(danhmuc.getImage());
        holder.tvTitle.setText(danhmuc.getName());
        convertView.findViewById(R.id.main_item_introduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialogFragment dialog = AboutDialogFragment.newInstance(
                        mContext,
                        R.style.MyDialogTheme,
                        danhmuc.getName(),
                        mContext.getString(danhmuc.getIntroduce()));
                dialog.show();
            }
        });
        return convertView;
    }

    public class DanhMucHolder{
        public ImageView image;
        public TextView tvTitle;
    }

}
