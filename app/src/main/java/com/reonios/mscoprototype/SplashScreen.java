package com.reonios.mscoprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by reonios on 5/19/16.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(intent);

//        Thread timerThread = new Thread(){
//            public void run(){
//                try{
//
//                    sleep(2500);
//                }catch(InterruptedException e){
//                    e.printStackTrace();
//                }finally{
//                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        };
//        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
