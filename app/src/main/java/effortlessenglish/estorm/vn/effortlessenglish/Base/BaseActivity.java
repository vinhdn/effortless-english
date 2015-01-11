package effortlessenglish.estorm.vn.effortlessenglish.Base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import effortlessenglish.estorm.vn.effortlessenglish.Activity.PlayerActivity;
import effortlessenglish.estorm.vn.effortlessenglish.Dialogs.WaitingDialog;
import effortlessenglish.estorm.vn.effortlessenglish.EffortlessApplication;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerBroadcast;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerService;


public abstract class BaseActivity extends ActionBarActivity implements View.OnClickListener{

    private RelativeLayout rootLayout;
    protected WaitingDialog progress;
    protected EffortlessApplication mApp;
    protected PlayerService service;
    protected PlayerBroadcast mBroadcastListener = new PlayerBroadcast(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (EffortlessApplication) getApplication();
        super.setContentView(R.layout.activity_base);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        setTitle(getString(R.string.app_name));
        setTitleColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setIcon(R.drawable.originalenglish);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(R.color.blue_action_bar));

        progress = new WaitingDialog(this, R.style.PleaseWaitDialog, null);
        progress.setCancelable(false);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(R.color.blue_action_bar));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, PlayerService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    public void setContentView(int id) {
        LinearLayout contentView = (LinearLayout) findViewById(R.id.content_view);
        contentView.removeAllViews();
        getLayoutInflater().inflate(id, contentView);
        super.setContentView(rootLayout);
        setActionView();
        findViewById(R.id.include_layout_control).setOnClickListener(this);
        Intent intent= new Intent(this, PlayerService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        registerBroadCastMusic();
    }

    public abstract void setActionView();

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.include_layout_control){
            Intent i = new Intent(BaseActivity.this, PlayerActivity.class);
            startActivity(i);
        }
    }

    public void setTitle(String title) {
        super.getSupportActionBar().setTitle(title);
    }

    public void hideController() {
        findViewById(R.id.include_layout_control).setVisibility(View.GONE);
    }

    public void showController() {
        findViewById(R.id.include_layout_control).setVisibility(View.VISIBLE);
    }

    protected boolean isNetwork() {
        ConnectivityManager connectivity = (ConnectivityManager) this
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        Toast.makeText(this, this.getString(R.string.check_network),
                Toast.LENGTH_SHORT).show();
        return false;
    }

    protected void showProgressDialog(boolean show) {
        if (show) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    protected void showView(int id, boolean isShow){
        if(isShow){
            findViewById(id).setVisibility(View.VISIBLE);
        }else
            findViewById(id).setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
        unregisterReceiver(this.mBroadcastListener);
    }

    protected void changeController(){
        TextView ctrolTitle = (TextView) findViewById(R.id.layout_control_title);
        TextView ctrolMenu = (TextView) findViewById(R.id.layout_control_menu);
        ImageView ctrolImage = (ImageView) findViewById(R.id.layout_control_image);
        ImageView btnPlay = (ImageView) findViewById(R.id.layout_control_bt_play);
        ctrolTitle.setText(service.getCurrentLession().getName());
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.clickPlayButton();
            }
        });
     }

    public void seekPlayerToTime(int time)
    {
        if (this.service != null)
            this.service.seekPlayerToTime(time);
    }

    public void onPlayerPlay(){
        ImageView btnPlay = (ImageView) findViewById(R.id.layout_control_bt_play);
        btnPlay.setBackgroundResource(R.drawable.bt_minibar_pause_nor);
    }

    public void onPlayerPause(){
        ImageView btnPlay = (ImageView) findViewById(R.id.layout_control_bt_play);
        btnPlay.setBackgroundResource(R.drawable.bt_control_play_nor);
    }

    public void onPlayerStop(){
        ImageView btnPlay = (ImageView) findViewById(R.id.layout_control_bt_play);
        btnPlay.setBackgroundResource(R.drawable.bt_control_play_nor);
    }

    public void onPlayerStart(){

    }

    public void onPlayerError(){

    }

    public abstract void onPlayerChange(String action);
    public abstract void onConnectServcie();

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            PlayerService.LessionBinder b = (PlayerService.LessionBinder) binder;
            service = b.getService();
            if( service != null && service.getCurrentLession() != null){
                onConnectServcie();
                //showController();
                changeController();
            }else
                hideController();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };

    public void registerBroadCastMusic()
    {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(PlayerBroadcast.BR_PLAY);
        localIntentFilter.addAction(PlayerBroadcast.BR_ERROR);
        localIntentFilter.addAction(PlayerBroadcast.BR_LOADED);
        localIntentFilter.addAction(PlayerBroadcast.BR_PAUSE);
        localIntentFilter.addAction(PlayerBroadcast.BR_STOP);
        localIntentFilter.addAction(PlayerBroadcast.BR_START);
        localIntentFilter.addAction(PlayerBroadcast.BR_LOADING);
        registerReceiver(this.mBroadcastListener, localIntentFilter);
    }
}
