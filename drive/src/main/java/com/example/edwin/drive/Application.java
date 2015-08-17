package com.example.edwin.drive;


import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Edwin on 8/9/2015.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(PostActivity.class);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));
    }
}