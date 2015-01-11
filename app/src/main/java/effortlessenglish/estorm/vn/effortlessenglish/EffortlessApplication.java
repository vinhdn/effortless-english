package effortlessenglish.estorm.vn.effortlessenglish;

import android.app.Application;
import android.content.Context;

/**
 * Created by Vinh on 1/9/15.
 */
public class EffortlessApplication extends Application{

    public static Context mAppContext;

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
    }
}
