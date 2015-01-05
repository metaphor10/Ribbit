package com.software.arielb.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by arielb on 1/4/2015.
 */
public class RibbitApplication extends Application {
    @Override
     public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "lTw82FBjRavSFRKHUGiGkVUY1Igo7HX3xLn4MJcP", "YMVHkJINJwxiTTYFZQcPpLCWjKLoLdR0VQzSxLOZ");



    }





}
