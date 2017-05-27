package com.wb.connect;

import android.app.Application;
import android.content.Context;


/**
 * Created by sam on 2017/5/24.
 */

public class MyApp extends Application {

    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this.getApplicationContext();
    }
}
