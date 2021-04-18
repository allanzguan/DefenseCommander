package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.media.Image;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Base {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final int screenWidth;
    private final int screenHeight;

    Base(int screenWidth, int screenHeight, ImageView imageView, MainActivity mainActivity ) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mainActivity = mainActivity;
        this.imageView = imageView;

//        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));
    }
//
//    float getX(){
//        return imageView.getX() + (0.5f * imageView.getWidth());
//    }
//    float getY(){
//        return imageView.getY() + (0.5f * imageView.getHeight());
//    }

    float getX(){
        return imageView.getX() ;
    }
    float getY(){
        return imageView.getY() ;
    }

    public void baseDestruct(){
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(imageView);

        ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.blast);
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
