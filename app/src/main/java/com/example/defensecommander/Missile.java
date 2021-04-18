package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.Image;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {

    private MainActivity mainActivity;

    private final ImageView imageView;
    private int screenWidth;
    private int screenHeight;
    private long screenTime;
    private AnimatorSet aSet = new AnimatorSet();
    private final boolean hit = false;
    private float w;
    boolean active = true;

    Missile(int screenWidth, int screenHeight, long screenTime, MainActivity mainActivity){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mainActivity = mainActivity;
        this.screenTime = screenTime;

        imageView = new ImageView(mainActivity);
        mainActivity.runOnUiThread(() -> {
            mainActivity.getLayout().addView(imageView);
            imageView.setImageResource(R.drawable.missile);
            w = imageView.getDrawable().getIntrinsicWidth();
        });

    }

   AnimatorSet setData(){


        int startX = (int) (Math.random() * screenWidth);
        int endX = (int) (Math.random() * screenWidth);
        float angle = calculateAngle(startX, endX, -100, screenHeight);


        imageView.setX(startX);
        imageView.setY(-100);
        imageView.setZ(-10);
        imageView.setRotation(angle);

       ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", -100-(w * 0.5f), screenHeight );
       yAnim.setInterpolator(new LinearInterpolator());
       yAnim.setDuration(screenTime);

       ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX -(w * 0.5f), endX);
       xAnim.setInterpolator(new LinearInterpolator());
       xAnim.setDuration(screenTime);
       xAnim.addListener(new AnimatorListenerAdapter() {
           @Override
           public void onAnimationEnd(Animator animation) {
               mainActivity.runOnUiThread(() -> {
                   if (getY() > screenHeight * 0.85) {
                        aSet.cancel();
                        makeGroundBlase();
                        mainActivity.removeMissile(Missile.this);
                   }
//                   Log.d(TAG, "run: NUM VIEWS " + mainActivity.getLayout().getChildCount());
               });

           }
       });





       aSet.playTogether(xAnim, yAnim);

        return aSet;
   }

   private float calculateAngle(double x1, double x2, double y1, double y2){
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
   }

   float getX() { return imageView.getX();}
   float getY() { return imageView.getY();}
   float getWidth() { return imageView.getWidth();}
   float getHeight() { return imageView.getHeight();}
   void stop() { aSet.cancel();}
   ImageView getView(){
        return imageView;
   }




   void makeGroundBlase(){

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);






       int w = imageView.getDrawable().getIntrinsicWidth();
       int offset = (int) (w * 0.5);

        iv.setX(getX() - offset);
        iv.setY(getY() - offset);
        iv.setZ(-15);
        mainActivity.getLayout().addView(iv);

       final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
       alpha.setInterpolator(new LinearInterpolator());
       alpha.setDuration(3000);
       alpha.addListener(new AnimatorListenerAdapter() {
           @Override
           public void onAnimationEnd(Animator animation) {

               mainActivity.getLayout().removeView(imageView);
               mainActivity.removeMissile(Missile.this);
           }
       });
       alpha.start();
       mainActivity.applyMissileBlast(iv.getX(), iv.getY());
   }

   void applyInterceptorBlast(Missile m){
       aSet.cancel();

       mainActivity.getLayout().removeView(imageView);

       mainActivity.removeMissile(Missile.this);

       final ImageView iv = new ImageView(mainActivity);
       iv.setImageResource(R.drawable.explode);
       iv.setTransitionName("Missile Intercepted Blast");
       int w = imageView.getDrawable().getIntrinsicWidth();
       iv.setX(getX());
       iv.setY(getY());


       mainActivity.getLayout().addView(iv);
       final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
       alpha.setInterpolator(new LinearInterpolator());
       alpha.setDuration(3000);
       alpha.addListener(new AnimatorListenerAdapter() {
           @Override
           public void onAnimationEnd(Animator animation) {
               mainActivity.getLayout().removeView(imageView);
           }
       });
       alpha.start();
   }
}//
