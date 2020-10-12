package br.com.infoshop;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import dagger.hilt.android.HiltAndroidApp;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;


@HiltAndroidApp
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());

        Log.d(MY_LOG_TAG, " ======= Application @onCreate ========");
    }
}
