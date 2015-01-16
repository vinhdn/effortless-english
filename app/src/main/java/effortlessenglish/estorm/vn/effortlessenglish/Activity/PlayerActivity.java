package effortlessenglish.estorm.vn.effortlessenglish.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import effortlessenglish.estorm.vn.effortlessenglish.Adapters.PlayerViewAdapter;
import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Services.PlayerBroadcast;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;

public class PlayerActivity extends BaseActivity implements View.OnClickListener{

    ViewPager mPager;
    CirclePageIndicator mIndicator;
    private SeekBar seekBar;
    private ImageView btPlay, btNext, btPre;
    private TextView tvTotalTime, tvCurrentTime,btnDownload;
    private boolean isCreated = false;
    //Handler object.
    private Handler mHandler = new Handler();

    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_view);
        setContentView(R.layout.activity_player);
        showProgressDialog(true);
        overridePendingTransition(R.anim.trans_bottom_in, R.anim.trans_bottom_out);
    }

    private void setTitleActionBar(String title){
        TextView tvInActionbar = (TextView)getSupportActionBar().getCustomView().findViewById(R.id.title_actionbar);
        tvInActionbar.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
            if(service != null && service.isPlaying()){
                btPlay.setImageResource(R.drawable.btn_playing_pause);
            }else
                btPlay.setImageResource(R.drawable.btn_playing_play);
        try {
            setSeekbarDuration(getTotalTimePlayer()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //Update the seekbar.
//        try {
//            setSeekbarDuration(getTotalTimePlayer()/1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(seekbarUpdateRunnable);
        showProgressDialog(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(seekbarUpdateRunnable);
    }

    @Override
    public void setActionView() {
        setAction();
    }

    @Override
    public void onPlayerChange(String action) {
        if(action.equals(PlayerBroadcast.BR_PLAY)){
            btPlay.setImageResource(R.drawable.btn_playing_pause);
            return;
        }
        if(action.equals(PlayerBroadcast.BR_PAUSE)){
            btPlay.setImageResource(R.drawable.btn_playing_play);
            return;
        }
        if(action.equals(PlayerBroadcast.BR_ERROR)){
            btPlay.setImageResource(R.drawable.btn_playing_play);
            return;
        }
        if(action.equals(PlayerBroadcast.BR_START)){
            return;
        }
        if(action.equals(PlayerBroadcast.BR_STOP)){
            btPlay.setImageResource(R.drawable.btn_playing_play);
            mHandler.removeCallbacks(seekbarUpdateRunnable);
            return;
        }
        if(action.equals(PlayerBroadcast.BR_LOADED)){
            Log.d("Total time: ", getTotalTimePlayer() + "");
            if(service.getCurrentLession().isOffline()){
                btnDownload.setText(getString(R.string.download_offline_ready));
                btnDownload.setClickable(false);
            }
            showProgressDialog(false);
            setSeekbarDuration((int) (getTotalTimePlayer() / 1000));
            return;
        }
        if(action.equals(PlayerBroadcast.BR_LOADING)){
            btPlay.setImageResource(R.drawable.btn_playing_play);
        }
        if(action.equals(PlayerBroadcast.BR_FINISH_DOWNLOAD)){
            btnDownload.setText(getString(R.string.download_offline_ready));
            btnDownload.setClickable(false);
        }
    }

    @Override
    public void onConnectServcie() {
        showProgressDialog(true);
        if(service != null && service.getCurrentLession() != null){
            if(getTotalTimePlayer() > 0)
                showProgressDialog(false);
            if(service.isPlaying()){
                btPlay.setImageResource(R.drawable.btn_playing_pause);
            }else
                btPlay.setImageResource(R.drawable.btn_playing_play);
            mPager.setAdapter(new PlayerViewAdapter(getSupportFragmentManager(),service));
            mIndicator.setViewPager(mPager);
            mIndicator.setPageColor(getResources().getColor(R.color.orgrince));
            mIndicator.setRadius(mIndicator.getRadius() * 1.5f);
            this.setTitleActionBar(service.getCurrentLession().getName());
            if(service.getCurrentLession().isOffline()){
                btnDownload.setText(getString(R.string.download_offline_ready));
                btnDownload.setClickable(false);
            }
            try {
                setSeekbarDuration(getTotalTimePlayer()/1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Log.e("Getservie", "null");
        }
    }

    public void changeTextCurrentTime(long time){
        this.tvCurrentTime.setText(Constants.convertMillisToMinsSecs(time));
    }

    private void setAction(){
        btPlay = (ImageView)findViewById(R.id.play_or_pause);
        btPre = (ImageView)findViewById(R.id.play_pre);
        btNext = (ImageView)findViewById(R.id.play_next);
        seekBar = (SeekBar) findViewById(R.id.seek_bar_progress);
        tvCurrentTime = (TextView) findViewById(R.id.txt_current_time);
        tvTotalTime = (TextView) findViewById(R.id.txt_total_time);
        btPlay.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btPre.setOnClickListener(this);
        btnDownload = (TextView)findViewById(R.id.lession_download);
        btnDownload.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(lessionSeekbar);
        mPager = (ViewPager) findViewById(R.id.workspace);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        hideController();
        isCreated = true;
    }

    public int getTotalTimePlayer()
    {
        if (this.service != null)
            return this.service.getTotalTimePlayer();
        return 0;
    }

    public int getCurrentTimePlayer()
    {
        if (this.service != null)
            return this.service.getCurrentTimePlayer();
        return 0;
    }

    /**
     * Create a new Runnable to update the seekbar and time every 100ms.
     */
    public Runnable seekbarUpdateRunnable = new Runnable() {

        public void run() {

            try {
                long currentPosition = getCurrentTimePlayer();
                changeTextCurrentTime(currentPosition);
                tvTotalTime.setText(Constants.convertMillisToMinsSecs(getTotalTimePlayer()));
                int currentPositionInSecs = (int) currentPosition/1000;
                seekBar.setProgress(currentPositionInSecs);
                mHandler.postDelayed(seekbarUpdateRunnable, 100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    /**
     * Sets the seekbar's duration. Also updates the
     * elapsed/remaining duration text.
     */
    private void setSeekbarDuration(int duration) {
        if(duration <= 0)
            return;
        if(service != null && service.getCurrentLession() != null) {
            seekBar.setMax(duration);
            seekBar.setProgress(getCurrentTimePlayer() / 1000);
            mHandler.post(seekbarUpdateRunnable);
        }
    }

    /**
     * Smoothly scrolls the seekbar to the indicated position.
     */
    private void smoothScrollSeekbar(int progress) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_or_pause:
                if(service != null)
                service.clickPlayButton();
                break;
            case R.id.lession_download:
                if(service != null)
                    if(!service.getCurrentLession().isOffline()) {
                        service.saveOfflineLession();
                        btnDownload.setText(getString(R.string.downloading));
                        btnDownload.setClickable(false);
                    }
                break;
            case R.id.play_next:
                if(service != null)
                    service.playNextSong();
                break;
            case R.id.play_pre:
                if(service != null)
                    service.playPreSong();
                break;
        }
    }
    /**
    * Seekbar change listener.
    */
    private SeekBar.OnSeekBarChangeListener lessionSeekbar = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            long currentSongDuration = getTotalTimePlayer();
            seekBar.setMax((int) currentSongDuration / 1000);
            if(fromUser)
                changeTextCurrentTime(seekBar.getProgress() * 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(seekbarUpdateRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int seekBarPosition = seekBar.getProgress();
            seekPlayerToTime(seekBarPosition * 1000);
            mHandler.post(seekbarUpdateRunnable);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_top_in, R.anim.trans_top_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.trans_top_in, R.anim.trans_top_out);
    }
}


