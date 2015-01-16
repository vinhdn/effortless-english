package effortlessenglish.estorm.vn.effortlessenglish.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import effortlessenglish.estorm.vn.effortlessenglish.Base.BaseActivity;

/**
 * Created by Vinh on 1/10/15.
 */
public class PlayerBroadcast extends BroadcastReceiver{

    public static String BR_PLAY = "vn.vinhblue.effortless.PLAY";
    public static String BR_PAUSE = "vn.vinhblue.effortless.PAUSE";
    public static String BR_START = "vn.vinhblue.effortless.START";
    public static String BR_ERROR = "vn.vinhblue.effortless.ERROR";
    public static String BR_STOP = "vn.vinhblue.effortless.STOP";
    public static String BR_LOADED = "vn.vinhblue.effortless.LOADED";
    public static String BR_LOADING = "vn.vinhblue.effortless.LOADING";
    public static String BR_CHANGE_LESSION = "vn.vinhblue.effortless.CHANGELESSION";
    public static String BR_FINISH_DOWNLOAD = "vn.vinhblue.effortless.FINISHDOWNLOAD";

    private BaseActivity activity;

    public PlayerBroadcast(BaseActivity activity){
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        activity.onPlayerChange(action);
        if(action.equals(BR_PLAY)){
            activity.onPlayerPlay();
            return;
        }
        if(action.equals(BR_PAUSE)){
            activity.onPlayerPause();
            return;
        }
        if(action.equals(BR_ERROR)){
            activity.onPlayerError();
            return;
        }
        if(action.equals(BR_START)){
            activity.onPlayerStart();
            return;
        }
        if(action.equals(BR_STOP)){
            activity.onPlayerStop();
            return;
        }
        if(action.equals(BR_LOADED)){
            return;
        }
        if(action.equals(BR_LOADING)){
            return;
        }
        if(action.equals(BR_CHANGE_LESSION)){
            activity.onConnectServcie();
            return;
        }
        if(action.equals(BR_FINISH_DOWNLOAD)){

        }
    }
}
