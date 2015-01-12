package effortlessenglish.estorm.vn.effortlessenglish;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import effortlessenglish.estorm.vn.effortlessenglish.Database.LocalStorage;
import effortlessenglish.estorm.vn.effortlessenglish.Database.StorageUnavailableException;

/**
 * Created by Vinh on 1/9/15.
 */
public class EffortlessApplication extends Application {

    public static Context mAppContext;
    private LocalStorage localStorage;
    public static SharedPreferences mPref;
    private Handler handler;

    public static Context getAppContext() {
        return mAppContext;
    }

    public static void setAppContext(Context mAppContext) {
        EffortlessApplication.mAppContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EffortlessApplication.setAppContext(getApplicationContext());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        handler = new Handler();
        try {
            localStorage = LocalStorage.create(this);
        } catch (StorageUnavailableException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void recreateLocalStorage() {
        if (localStorage == null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        localStorage = LocalStorage
                                .create(EffortlessApplication.this);
                    } catch (StorageUnavailableException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public final LocalStorage getLocalStorage() {
        return localStorage;
    }

    public static LocalStorage getLocalStorage(Context context) {
        return ((EffortlessApplication) context.getApplicationContext())
                .getLocalStorage();
    }

    public String getLastPlay(){
        return mPref.getString("lastplay","");
    }

    public void setLastPlay(String last){
        mPref.edit().putString("lastplay", last).commit();
    }

}
