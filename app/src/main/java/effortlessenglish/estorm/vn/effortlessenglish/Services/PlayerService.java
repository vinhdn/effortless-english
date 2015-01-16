package effortlessenglish.estorm.vn.effortlessenglish.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import effortlessenglish.estorm.vn.effortlessenglish.EffortlessApplication;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Lession;
import effortlessenglish.estorm.vn.effortlessenglish.Models.Models;
import effortlessenglish.estorm.vn.effortlessenglish.R;
import effortlessenglish.estorm.vn.effortlessenglish.Utils.Constants;

public class PlayerService extends Service implements OnPreparedListener,
        OnCompletionListener, OnErrorListener, OnBufferingUpdateListener, MusicFocusable {

    // debug TAG
    private static final String TAG = PlayerService.class.getSimpleName();
    private static PlayerService mInstance = null;
    public MediaPlayer mMediaPlayer;
    // our AudioFocusHelper object, if it's available (it's available on SDK
    // level >= 8)
    // If not available, this will be null. Always check for null before using!
    AudioFocusHelper mAudioFocusHelper = null;
    private int statusPlayer; // status of player , 1 is playing, 2 is pause...
    private NotificationManager mNotificationManager;
    private Lession lessionPlaying;
    private ArrayList<Lession> listLession;

    private IBinder musicBinder = new LessionBinder();

    // nofification builder
    private NotificationCompat.Builder mNotificationBuilder;
    private RemoteViews mRemoteViews;

    public int statusPlayMode;
    public int indexSongPlayRandom;
    public int indexSongPlay;
    public int indexSongObjectPlay;
    private Boolean endPlaylistSong;
    private Boolean statusGetSeekBar;
    private EffortlessApplication application;
    public int playingQuality;
    public int qualitySetting;
    public boolean offlineMode = false;

    EffortlessApplication mApp;
    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;

    // our AudioFocusHelper object, if it's available (it's available on SDK
    // level >= 8)
    // If not available, this will be null. Always check for null before using!

    enum AudioFocus {
        NoFocusNoDuck, // we don't have audio focus, and can't duck
        NoFocusCanDuck, // we don't have focus, but can play at a low volume
        // ("ducking")
        Focused // we have full audio focus
    }

    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent
                    .getAction())) {
                if (mMediaPlayer != null) {
                    pauseMediaPlayer();
                }
            }
        }
    }

    private IntentFilter intentFilter = new IntentFilter(
            AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    NoisyAudioStreamReceiver audioStreamReceiver = new NoisyAudioStreamReceiver();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_STICKY;
        } else if (intent.getAction().equals("NOTIFICATION_PLAY")) {
            clickPlayButton();
        } else if (intent.getAction().equals("NOTIFICATION_NEXT")) {
            playNextSong();
            return START_STICKY;
        } else if (intent.getAction().equals("NOTIFICATION_EXIT")) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                broadCastMusicPause();
            }
            Log.d(TAG, "stop nhe");
            stopForeground(true);
            stopSelf();
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (EffortlessApplication) getApplication();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        lessionPlaying = new Lession();
        listLession = new ArrayList<>();
        application = (EffortlessApplication) getApplication();
        registerReceiver(audioStreamReceiver, intentFilter);
        // create the Audio Focus Helper, if the Audio Focus feature is
        // available (SDK 8 or above)
        if (android.os.Build.VERSION.SDK_INT >= 8)
            mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(),
                    this);
        else
            mAudioFocus = AudioFocus.Focused; // no focus feature, so we always
        // "have" audio focus
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(audioStreamReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {

        // return Ibinder object to activity
        return musicBinder;
    }

    // Music binder to bound activity with service
    public class LessionBinder extends Binder {
        public PlayerService getService() {

            // return service object
            return PlayerService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // startForeground(1, mNotificationBuilder.build());
        // mMediaPlayer.stop();
        // mMediaPlayer.release();
        return super.onUnbind(intent);
        // return true;
    }

    @Override
    public boolean onError(MediaPlayer mMediaPlayer, int what, int extra) {

        Log.d(TAG, what + " " + extra);
        broadCastMusicError();
        stopForeground(true);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mMediaPlayer) {
        broadCastMusicStop();
        Log.d(TAG, "playermode" + statusPlayMode);
        playMusicWhenOnCompletetSong();

    }

    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
        isPrepared = true;
        startMediaPlayer();
        broadCastMusicLoaded();
        broadCastMusicStarting();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mMediaPlayer, int arg1) {

    }

    public void clickPlayButton() {
        Log.d("PlayerService", "clickPlayButton");
        if (mMediaPlayer != null) {
            if (statusPlayer == 1) {
                pauseMediaPlayer();
            } else if (statusPlayer == 0 && lessionPlaying != null) {
                playSong(lessionPlaying);

            } else if (statusPlayer == 2) {
                resumeMediaPlayer();
                broadCastMusicPlay();
                return;
            }
        }
    }

    public void pauseMediaPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            Log.d("PlayerService", "pauseMediaPlayer");
            mMediaPlayer.pause();
            statusPlayer = 2;
            if (VERSION.SDK_INT >= 11) {
                updateNotificationForPause(1);
            }
            broadCastMusicPause();
        }
    }

    private void resumeMediaPlayer() {
        Log.d(TAG, "on resum media player");
        mMediaPlayer.start();
        statusPlayer = 1;
        if (VERSION.SDK_INT >= 11) {
            updateNotificationForResume(0);
        }
        statusGetSeekBar = Boolean.valueOf(true);
    }

    public void playNextSong() {
        playSongIndex(indexSongPlay + 1);
    }

    public void playPreSong(){
        playSongIndex(indexSongPlay - 1);
    }

    public void playSong(Lession lession) {
        isPrepared = false;
        statusPlayer = 1;
        if (lession != null && lession.getLink() != null
                && lession.getLink().length() > 0) {
            lessionPlaying = lession;
            initializePlayer(lession);
            // Log.d(TAG, "Playing song :" + songobject.toString());
            return;
        }
    }

    public void playSongInfoObject(final Lession lession) {

    }

    private void broadcastLoadingSong() {
        Intent intent = new Intent();
        intent.setAction("BROADCAST_LOADINGSONG");
        sendBroadcast(intent);

    }


    public void initializePlayer(Lession lession) {
        statusGetSeekBar = Boolean.valueOf(false);
        Constants.statusPlayer = 3;
        try {
            setDataSourcePlayer(lession, this);
            if (mMediaPlayer != null) {
                mMediaPlayer.prepareAsync();
            }
            Constants.lessionPlaying = lession;
//            broadCastMusicLoaded();
//            broadCastMusicStarting();
            // mMediaPlayer.start();
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setDataSourcePlayer(Lession lession, Context context) {
        createMediaPlayer();
        String mp3URL = lession.getLink();
        Log.d("VInh - URL Lession", mp3URL);
        boolean isOffline = false;
        try {
            String urll = Constants.FOLDER_LESSION + "/" + lession.getParent().getId() + "/" + lession.getId() + ".mp3";
            Log.d("URL OFFLINE",urll);
            File fileMp3 = new File(urll);
            boolean isSaved = false;
            try{
                Models modelSave = mApp.getLocalStorage().getModels(lession.getId());
                if(modelSave.getId() == lession.getId()){
                    getCurrentLession().setOffline(true);
                    isSaved = true;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if (fileMp3.exists()) {
                mp3URL = urll;
                isOffline = true;
            }else{
                if(isSaved)
                    saveOfflineLession();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(mp3URL) || !mp3URL.contains("http://")) {
                Log.d(TAG, "null url" + mp3URL);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            //return;
        }
        Log.d(TAG, "url" + mp3URL);
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if(isOffline)
            {
                mMediaPlayer.setDataSource(context, Uri.parse(mp3URL));
            }else
            mMediaPlayer.setDataSource(mp3URL);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    private void createMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            return;
        } else {
            mMediaPlayer.reset();
            return;
        }
    }

    public Lession getCurrentLession() {
        return lessionPlaying;
    }

    // create notification on notification bar

    private void notificationCustome(int i) {
        if (mNotificationBuilder == null) {
            Intent intent = new Intent(this, PlayerService.class);
            intent.setAction("NOTIFICATION_PLAY");
            PendingIntent pendingintent = PendingIntent.getService(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intent1 = new Intent(this, PlayerService.class);
            intent1.setAction("NOTIFICATION_NEXT");
            PendingIntent pendingintent1 = PendingIntent.getService(this, 0,
                    intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intent2 = new Intent(this, PlayerService.class);
            intent2.setAction("NOTIFICATION_EXIT");
            PendingIntent pendingintent2 = PendingIntent.getService(this, 0,
                    intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            createInitNotificationView(i);
            mNotificationBuilder = (new NotificationCompat.Builder(this))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContent(mRemoteViews);
            PendingIntent pendingintent3 = PendingIntent.getActivity(
                    this,
                    0,
                    (new Intent("android.intent.action.MAIN")).addCategory(
                            "android.intent.category.LAUNCHER").setComponent(
                            getPackageManager().getLaunchIntentForPackage(
                                    getPackageName()).getComponent()), 0);
            mNotificationBuilder.setContentIntent(pendingintent3);
            mRemoteViews.setOnClickPendingIntent(
                    R.id.player_notification_linear, pendingintent3);
            mRemoteViews.setOnClickPendingIntent(
                    R.id.player_notification_bnt_play, pendingintent);
            mRemoteViews.setOnClickPendingIntent(
                    R.id.player_notification_bnt_delete, pendingintent2);
            initNotificationView(i);
            return;
        }
        try {
            initNotificationView(i);
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return;
    }

    @SuppressWarnings("deprecation")
    private void notificationNormal() {
        Log.d(TAG, "notificationNormal");
        Context context = getApplicationContext();
        String s = getCurrentLession().getName();
        Notification notification = new Notification(
                R.drawable.ic_launcher, s, System.currentTimeMillis());
        PendingIntent pendingintent = PendingIntent.getActivity(
                this,
                0,
                (new Intent("android.intent.action.MAIN")).addCategory(
                        "android.intent.category.LAUNCHER").setComponent(
                        getPackageManager().getLaunchIntentForPackage(
                                getPackageName()).getComponent()), 0);
        notification.contentView = mRemoteViews;
        String parentName = "";
        if(getCurrentLession().getParent() != null)
            parentName = getCurrentLession().getParent().getName();
        notification.setLatestEventInfo(context, s,
                parentName, pendingintent);
        startForeground(1, notification);
        Log.d(TAG, "start nhe");
    }

    private void createInitNotificationView(int i) {
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(getPackageName(),
                    R.layout.player_notification);
        }
    }

    private void initNotificationView(int i) {
        Log.d(TAG, "initNotificationView " + i);
        try {
            mRemoteViews.setTextViewText(R.id.player_notification_tv_title,
                    lessionPlaying.getName());
            String parentName = "";
            if(getCurrentLession().getParent() != null)
                parentName = getCurrentLession().getParent().getName();
            mRemoteViews.setTextViewText(R.id.player_notification_tv_singer,
                    parentName);
            mRemoteViews.setImageViewResource(R.id.player_notification_img_bg,
                    R.drawable.ic_launcher);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        if (i == 1) {
            mRemoteViews.setImageViewResource(
                    R.id.player_notification_bnt_play,
                    R.drawable.bt_minibar_play);
        } else if (i != 1) {
            mRemoteViews.setImageViewResource(
                    R.id.player_notification_bnt_play,
                    R.drawable.bt_minibar_pause_nor);
            if (mNotificationBuilder != null) {
                mNotificationBuilder.setContent(mRemoteViews);
                mNotificationBuilder.setOngoing(true);
                startForeground(1, mNotificationBuilder.build());
                Log.d(TAG, "start nhe !=1");
            }

            return;
        }
    }

    private void updateNotificationForPause(int i) {
        Log.d(TAG, "updateNotificationForPause " + i);
        mRemoteViews.setImageViewResource(R.id.player_notification_bnt_play,
                R.drawable.bt_minibar_play_nor);

        if (mNotificationBuilder != null) {
            mNotificationBuilder.setContent(mRemoteViews);
            startForeground(1, mNotificationBuilder.build());
            Log.d(TAG, "start nhe for pause");
        }
        return;
    }

    private void updateNotificationForResume(int i) {

        mRemoteViews.setImageViewResource(R.id.player_notification_bnt_play,
                R.drawable.bt_minibar_pause_nor);
        if (mNotificationBuilder != null) {
            mNotificationBuilder.setContent(mRemoteViews);
            startForeground(1, mNotificationBuilder.build());
            Log.d(TAG, "start nhe for resume");
        }
    }

    public void broadCastMusicChangeLession() {
        Log.d("HOAN", "broadcast Lession");
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_CHANGE_LESSION);
        sendBroadcast(intent);
    }

    public void broadCastFinishDownload() {
        Log.d("HOAN", "broadcast Finish Download");
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_FINISH_DOWNLOAD);
        sendBroadcast(intent);
    }

    public void broadCastMusicPlay() {
        Log.d("HOAN", "broadcast play");
        // lockScreenControl(1);
        Constants.statusPlayer = 1;
        statusPlayer = 1;
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_PLAY);
        sendBroadcast(intent);
        // broadcastWidgetMusicPlay();
    }

    public void broadCastMusicPause() {
        Log.d(TAG, "broadcast pause");
        // lockScreenControl(2);
        Constants.statusPlayer = 2;
        statusPlayer = 2;
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_PAUSE);
        sendBroadcast(intent);
        // broadcastWidgetMusicPause();
    }

    public void broadCastMusicError() {
        Log.d(TAG, "broadcast error");
        Constants.statusPlayer = -1;
        statusPlayer = -1;
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_ERROR);
        sendBroadcast(intent);
    }

    public void broadCastMusicStarting() {
        Log.d(TAG, "broadcast starting");
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_START);
        sendBroadcast(intent);
        // broadcastWidgetMusicStarting();
        // lockScreenControl(4);
    }

    public void broadCastMusicLoaded() {
        Log.d(TAG, "broadcast LOADED");
        Intent intent = new Intent();
        intent.setAction(PlayerBroadcast.BR_LOADED);
        sendBroadcast(intent);
        // broadcastWidgetMusicStarting();
        // lockScreenControl(4);
    }

    public void broadCastMusicStop() {
        if (lessionPlaying != null) {
            // lockScreenControl(2);
            Constants.statusPlayer = 0;
            statusPlayer = 0;
            Intent intent = new Intent();
            intent.setAction("BROADCAST_STOP");
            sendBroadcast(intent);
        }
    }

    public void startMediaPlayer() {
        if (VERSION.SDK_INT >= 11) {
            notificationCustome(0);
        } else {
            notificationNormal();
        }
        mMediaPlayer.start();
        broadCastMusicPlay();

        //pauseMediaPlayer();
        statusGetSeekBar = Boolean.valueOf(true);
    }

    public void seekPlayerToTime(int i) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(i);
        }
    }

    public void playSong(int index,ArrayList<Models> listLession){
        indexSongPlay = index;
        playSong(new Lession(listLession.get(index)));
        addNewLessionArray(listLession);
    }

    public void playSongIndex(int i) {
        Log.d(TAG, "Onplaysong index");
        Constants.indexLession = i;
        if (listLession != null && i < listLession.size() && i >= 0) {
            stopForeground(true);
            indexSongPlay = i;
            playSong(listLession.get(i));
            broadCastMusicChangeLession();
        }

    }

    public ArrayList<Lession> getListLession(){
        return listLession;
    }

    public void playSongObjectIndex(int i) {
        Log.d(TAG, "Onplaysong index");

    }

    public void addNewLessionArray(ArrayList<Models> arraylist) {
        indexSongPlay = 0;
        listLession.clear();
        for (int i = 0; i < arraylist.size(); i++) {
            listLession.add(new Lession(arraylist.get(i)));
        }
        Constants.indexLession = indexSongPlay;
        // if (statusPlayMode == 3) {
        // randomListSong();
        // }

        return;
    }

    public void playMusicWhenOnCompletetSong() {
        Log.e("Play", "Complete");
        playSong(this.getCurrentLession());
    }


    public static PlayerService getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            return null;
        }
    }

    private boolean isPrepared = false;

    public int getCurrentTimePlayer() {
        if(!isPrepared)
            return 0;
        int i = 0;
        MediaPlayer mediaplayer = mMediaPlayer;
        i = 0;
        if (mediaplayer != null) {
            i = mMediaPlayer.getCurrentPosition();
        }
        return i;
    }

    public int getTotalTimePlayer() {
        if(!isPrepared)
            return 0;
        MediaPlayer mediaplayer = mMediaPlayer;
        int i = 0;
        if (mediaplayer != null) {
            i = mMediaPlayer.getDuration();
        }
        return i;
    }


    public int changeStatusPlayMode() {
        statusPlayMode = (1 + statusPlayMode) % 4;
        if (statusPlayMode == 3) {
        }
        if (statusPlayMode == 1) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setLooping(true);
            }
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.setLooping(false);
            }
        }

        // broadcastWidgetPlayMode(statusPlayMode);
        return statusPlayMode;
    }

    @Override
    public void onGainedAudioFocus() {
        Toast.makeText(getApplicationContext(), "gained audio focus.",
                Toast.LENGTH_SHORT).show();
        mAudioFocus = AudioFocus.Focused;

        // restart media player with new focus settings
        if (statusPlayer == 1)
            configAndStartMediaPlayer();
    }

    @Override
    public void onLostAudioFocus(boolean canDuck) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(),
                "lost audio focus." + (canDuck ? "can duck" : "no duck"),
                Toast.LENGTH_SHORT).show();
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck
                : AudioFocus.NoFocusNoDuck;

        // start/restart/pause media player with new focus settings
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            configAndStartMediaPlayer();

    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }

    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause,
            // even if mState
            // is State.Playing. But we stay in the Playing state so that we
            // know we have to resume
            // playback once we get the focus back.
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.pause();
            return;
        } else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            mMediaPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll be
            // relatively
            // quiet
        else
            mMediaPlayer.setVolume(1.0f, 1.0f); // we can be loud

        if (!mMediaPlayer.isPlaying())
            mMediaPlayer.start();
    }

    public Boolean isCurrentSongPlaying(int id) {
        if (id >= 0) {
            if (lessionPlaying != null) {
                if (lessionPlaying.getId() >= 0
                        && lessionPlaying.getId() == id) {
                    return Boolean.valueOf(true);
                } else {
                    return Boolean.valueOf(false);
                }
            } else {
                return Boolean.valueOf(false);
            }
        } else {
            return Boolean.valueOf(false);
        }
    }

    public void deleteFileServices(String s) {
        // TODO Auto-generated method stub

    }

    public boolean isPlaying() {
        if (statusPlayer == 1)
            return true;
        return false;
    }

    public static boolean isDownloading = false;
    public static final String ACTION_DOWNLOAD = "vn.vinhblue.effortless.action.DOWNLOAD";
    android.support.v4.app.NotificationCompat.Builder mBuilder;

    private class DownloadTask extends AsyncTask<Void, Integer, String> {

        int id, idPar;
        String name, link;


        public DownloadTask(int ID, int idPar, String name, String link) {
            this.id = ID;
            this.idPar = idPar;
            this.name = name;
            this.link = link;
        }

        private void Download() throws Exception {
            try {
                URL url = new URL(link);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                File outputFile = new File(Constants.FOLDER_LESSION + "/" + idPar, id + ".mp3");
                if (!outputFile.getParentFile().exists())
                    outputFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long lengthCurrent = 0;
                long lengthFile = c.getContentLength();
                int percent = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    lengthCurrent += len1;
                    if ((lengthCurrent * 100) / lengthFile > percent) {
                        percent = (int) ((lengthCurrent * 100) / lengthFile);
                        if (VERSION.SDK_INT >= 11) {
                            mBuilder.setProgress(100, Math.abs(percent), false);
                            mNotificationManager.notify(111, mBuilder.build());
                        }else{
                            notificationNormal("Saving in process - " + percent + "%",getCurrentLession().getName(),2);
                        }
                    }
                    fos.write(buffer, 0, len1);

                }
                fos.close();
                is.close();// till here, it works fine - .apk is download to my
                // sdcard in download file
                broadCastFinishDownload();
                if (VERSION.SDK_INT >= 11) {
                    mBuilder.setContentText("Download complete")
                            // Removes the progress bar
                            .setProgress(0, 0, false);
                    mNotificationManager.notify(111, mBuilder.build());
                }else{
                    notificationNormal("Download complete!",getCurrentLession().getName(),111);
                }
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
                if (VERSION.SDK_INT >= 11) {
                    mBuilder.setContentText("Download error!")
                            // Removes the progress bar
                            .setProgress(0, 0, false);
                    mNotificationManager.notify(111, mBuilder.build());
                }else{
                    notificationNormal("Download error!",getCurrentLession().getName(),111);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            stopSelf();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //Update(params[0], params[1]);
                Download();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (VERSION.SDK_INT >= 11) {
                mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle(name)
                        .setContentText("saving in progress")
                        .setSmallIcon(R.drawable.icon_downloaded);
                mBuilder.setProgress(100, 0, false);
                mNotificationManager.notify(111, mBuilder.build());
            } else {
                notificationNormal("saving in progress",getCurrentLession().getName(),111);
            }

        }

    }

    public void saveOfflineLession() {
        if (this.getCurrentLession() != null) {
            try {
                Models model = this.getCurrentLession();
                while (model != null && model.getId() > 0) {
                    mApp.getLocalStorage().insertModels(model);
                    model = model.getParent();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            File outputFile = new File(Constants.FOLDER_LESSION + "/" +
                    this.getCurrentLession().getParent().getId(),
                    this.getCurrentLession().getId() + ".mp3");
            if (outputFile.exists())
                return;
            new DownloadTask(this.getCurrentLession().getId(),
                    this.getCurrentLession().getParent().getId(),
                    this.getCurrentLession().getName(),
                    this.getCurrentLession().getLink()).execute();
        }
    }

    private void notificationNormal(String content,String subtitle,int id) {
        Log.d(TAG, "notificationNormal");
        Context context = getApplicationContext();
        Notification notification = new Notification(
                R.drawable.icon_downloaded, content, System.currentTimeMillis());
        PendingIntent pendingintent = PendingIntent.getActivity(
                this,
                0,
                (new Intent("android.intent.action.MAIN")).addCategory(
                        "android.intent.category.LAUNCHER").setComponent(
                        getPackageManager().getLaunchIntentForPackage(
                                getPackageName()).getComponent()), 0);
        notification.contentView = mRemoteViews;
        notification.setLatestEventInfo(context, content,
                subtitle, pendingintent);
        startForeground(id,notification);
        Log.d(TAG, "start nhe");
    }

}
