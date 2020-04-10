package com.kent.anim;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

/**
 * Created by Kent on 2016/10/5.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);

    }


    public static MyApplication getInstance() {
        return mInstance;
    }
}
