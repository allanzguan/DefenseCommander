package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Interceptor {

    private MainActivity mainActivity;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private ImageView imageView;

    private ObjectAnimator moveX, moveY;
    private static int idVal = -1;

    Interceptor(float startX, float startY, float endX,  float endY, MainActivity mainActivity){
        imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.interceptor);
        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        this.startX = startX +10;
        this.startY = startY +10;
        this.endX = endX - offset;
        this.endY = endY - offset;
        this.mainActivity = mainActivity;

        setImageView();
    }

    private void setImageView(){
        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);
        imageView.setId(idVal--);
        imageView.setRotation(calculateAngle(startX, endX, startY, endY));
        mainActivity.getLayout().addView(imageView);
        double distance =  Math.sqrt((endY - imageView.getY()) * (endY - imageView.getY()) + (endX - imageView.getX()) * (endX - imageView.getX()));

        moveX = ObjectAnimator.ofFloat(imageView, "x", endX);
        moveX.setInterpolator(new AccelerateInterpolator());
        moveX.setDuration((long) (distance * 2));

        moveY = ObjectAnimator.ofFloat(imageView, "y", endY);
        moveY.setInterpolator(new AccelerateInterpolator());
        moveY.setDuration((long) (distance * 2));

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
                makeBlast();
            }
        });
    }

    void launch() {
        moveX.start();
        moveY.start();
    }

    private float calculateAngle(double x1, double x2, double y1, double y2){
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    private void makeBlast() {
        SoundPlayer.getInstance().start("interceptor_blast");
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.i_explode);

        explodeView.setTransitionName("Interceptor blast");

        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(this.getX() - (w/2));

        explodeView.setY(this.getY() - (w/2));

        explodeView.setZ(-15);

        mainActivity.getLayout().addView(explodeView);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView);
            }
        });
        alpha.start();

        mainActivity.applyInterceptorBlast(this, imageView.getId());
    }

    float getX() {
        int xVar = imageView.getWidth() / 2;
        return imageView.getX() + xVar;
    }

    float getY() {
        int yVar = imageView.getHeight() / 2;
        return imageView.getY() + yVar;
    }
}//
