package com.tikalk.tikalhub;

import android.app.Application;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tikalk.tikalhub.model.FeedAggregator;

import java.util.Timer;
import java.util.TimerTask;

public class TikalHubApplication extends Application {

    public static String LogTag = "TikalHubApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        FeedAggregator.init(this);

        new Timer("Update Feed Timer").schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(LogTag, "Update feed");
                FeedAggregator.getInstance().fetchNewItems();
            }
        }, 1000, 600000);
    }
}
