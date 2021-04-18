package com.example.defensecommander;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;

public class MissileMaker implements Runnable{

    private final MainActivity mainActivity;
    private final int screenWidth;
    private final int screenHeight;
    private boolean isRunning;
    private static final int NUM_LEVELS = 5;
    private static final int MISSILE_PER_LEVEL = 5;
    private int count=0;
    private int currentLevel=1;
    long delay = NUM_LEVELS * 1000;
    private ArrayList<Missile> activeMissile = new ArrayList<>();

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight){
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running){
        Log.d("MISSILEE", "setRunning: " + running);
        isRunning = running;
    }

    @Override
    public void run(){
        setRunning(true);

        try {
            Thread.sleep((long) (delay * 0.5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(isRunning){

            makeMissile(delay);

            count++;
            if(count > MISSILE_PER_LEVEL){
                increaseLevel();
                count = 0;
            }
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

   void makeMissile(long delay){
       long missileTime = (long) ((delay*2) + (Math.random() * delay));

       final Missile missile = new Missile(screenWidth, screenHeight, missileTime, mainActivity);
       mainActivity.addMissile(missile);
//       final AnimatorSet as = missile.setData();
       SoundPlayer.getInstance().start("launch_missile");
//       mainActivity.runOnUiThread(as::start);
       mainActivity.runOnUiThread(missile.setData()::start);
   }

   void increaseLevel(){
        currentLevel++;
        if(delay-500 <= 0){
            delay=100;
            mainActivity.runOnUiThread(() -> {mainActivity.setLevel(currentLevel);});
            return;
        }
        delay= delay-500;
       mainActivity.runOnUiThread(() -> {mainActivity.setLevel(currentLevel);});
   }

   long getSleepTime(){
        double rand = Math.random();
        if(rand < 0.1){
            return 100;
        }
        else if(rand < 0.2){
            return (long) (0.5 * delay);
        }
        else{
            return delay;
        }
   }

//    void applyInterceptorBlast(Interceptor interceptor, int id) {
//        float x1 = interceptor.getX();
//        float y1 = interceptor.getY();
//
//    }
}//
