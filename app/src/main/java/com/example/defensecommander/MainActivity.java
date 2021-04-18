package com.example.defensecommander;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView scoreView, levelView;
    public static int screenHeight;
    public static int screenWidth;
    private boolean noBackground;
    private int score, level;
    private MainActivity ma;
    private ImageView baseA, baseB, baseC;
    private ArrayList<Base> baseList = new ArrayList<Base>();
    private MissileMaker missileMaker;
    private ArrayList<Missile> missileList = new ArrayList<Missile>();
    private ArrayList<Interceptor> interceptorList = new ArrayList<Interceptor>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ma = this;
        noBackground = true;

        setupFullScreen();
        getScreenDimensions();
        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background);
        SoundPlayer.getInstance().setupSound(this,"base_blast", R.raw.base_blast);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile);
        SoundPlayer.getInstance().setupSound(this, "missile_miss", R.raw.missile_miss);

        layout = findViewById(R.id.background);
        scoreView = findViewById(R.id.score);
        levelView = findViewById(R.id.level);
        baseA = findViewById(R.id.baseA);
        baseB = findViewById(R.id.baseB);
        baseC = findViewById(R.id.baseC);

        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        Base bA = new Base(screenWidth, screenHeight, baseA, this);
        baseList.add(bA);
        Base bB = new Base(screenWidth, screenHeight, baseB, this);
        baseList.add(bB);
        Base bC = new Base(screenWidth, screenHeight, baseC, this);
        baseList.add(bC);



        doTitle();

    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    private void doTitle(){

        ImageView titleView = new ImageView(this);
        titleView.setImageResource(R.drawable.title);
        titleView.setTransitionName("Title Fade");
        float w = titleView.getDrawable().getIntrinsicWidth();
        float h = titleView.getDrawable().getIntrinsicHeight();
        titleView.setX(w/3);
        titleView.setY(h/1.5f);
//        titleView.setZ(-1);
        getLayout().addView(titleView);

        ObjectAnimator tAnim = ObjectAnimator.ofFloat(titleView, "alpha", 0.0f, 1.0f);
        tAnim.setInterpolator(new LinearInterpolator());
        tAnim.setDuration(6000);

        tAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                getLayout().removeView(titleView);
                scoreView.setVisibility(View.VISIBLE);
                levelView.setVisibility(View.VISIBLE);
                baseA.setVisibility(View.VISIBLE);
                baseB.setVisibility(View.VISIBLE);
                baseC.setVisibility(View.VISIBLE);
                new ScrollingBackground(ma, layout, R.drawable.clouds, 8000);
                if(missileMaker == null){
                    missileMaker = new MissileMaker(MainActivity.this, screenWidth, screenHeight);
                    new Thread(missileMaker).start();



                }
            }
        });
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        tAnim.start();

    }



    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void handleTouch(float x1, float y1) {
        if(baseList.size() > 0){

            double closestDistance = Double.MAX_VALUE;
            Base closest = null;


            for(Base b : baseList){
                double distance =  Math.sqrt((y1 - b.getY()) * (y1 - b.getY()) + (x1 - b.getX()) * (x1 - b.getX()));
                if (distance < closestDistance){
                    closestDistance = distance;
                    closest = b;
                }
            }

            // call interceptor pass x y base
            if(interceptorList.size() < 3){
                Interceptor i = new Interceptor(closest.getX(), closest.getY(), x1, y1, MainActivity.this);
                interceptorList.add(i);
                SoundPlayer.getInstance().start("launch_interceptor");
                i.launch();
            }

        }
    }

    public void addMissile(Missile m){
        missileList.add(m);

    }

    public void removeMissile(Missile m){
        missileList.remove(m);
//        missileMaker.remove(m);

    }

    public int missileCount(){
        return missileList.size();
    }

    public void setLevel(int l){
        levelView.setText("Level "+l);
    }


    public void applyInterceptorBlast(Interceptor interceptor, int id) {
//        missileMaker.applyInterceptorBlast(interceptor, id);
        float x1 = interceptor.getX();
        float y1 = interceptor.getY();

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(missileList);

        for(Missile m : temp){
            float x2 = (int) (m.getX() + 0.5 * m.getWidth());
            float y2 = (int) (m.getY() + 0.5 * m.getHeight());

            float dist = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

            if(dist < 120){
                score++;
                scoreView.setText(Integer.toString(score));
                SoundPlayer.getInstance().start("interceptor_hit_missile");
                m.applyInterceptorBlast(m);
                nowGone.add(m);
            }
        }

        for(Missile m : nowGone){
            missileList.remove(m);
        }
        nowGone.clear();

        ArrayList<Base> temp2 = new ArrayList<>(baseList);

        for(Base b : temp2){
            double distance =  Math.sqrt((y1 - b.getY()) * (y1 - b.getY()) + (x1 - b.getX()) * (x1 - b.getX()));
            if (distance < 120){

                baseList.remove(b);
                b.baseDestruct();
                if(baseList.isEmpty()){
                    missileMaker.setRunning(false);
                    endGame();
                }
            }
        }
        interceptorList.remove(interceptor);
    }

    public void applyMissileBlast(float x1, float y1){

        ArrayList<Base> temp = new ArrayList<>(baseList);
        boolean miss = true;
        for(Base b : temp){
            double distance =  Math.sqrt((y1 - b.getY()) * (y1 - b.getY()) + (x1 - b.getX()) * (x1 - b.getX()));
            if (distance < 250){
                miss = false;
                baseList.remove(b);
                b.baseDestruct();
                if(baseList.isEmpty()){
                    missileMaker.setRunning(false);
                    endGame();
                }
            }
        }
        if (miss){
            SoundPlayer.getInstance().start("missile_miss");
        }


    }



    private void checkScore(){

        new Thread(new SelectAllHandler(this)).start();
    }

    public void selectAllResult(ArrayList<Score> scoreList){


        Log.d("SCOREEEEE", "selectAllResult: " + scoreList.toString());
        ArrayList<Score> scores = new ArrayList<Score>(scoreList);

        boolean top = false;
        for(Score s : scores){
            if (score > s.getScore()){
                top = true;
                break;
            }
        }

        if(top){
            final EditText input = new EditText(this);
            Log.d("SCOREEEEE", "top true");
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("You are a Top-Player!");
                builder.setMessage("Please enter your initals (up to 3 characters):");
                builder.setView(input);
                builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String init = input.getText().toString().substring(0,3);
                        Log.d("initinit", "onClick: " + init);
                        new Thread(new InsertHandler(MainActivity.this, init, score, level)).start();
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
        if(!top){
            Log.d("SCOREEEEE", "top false");
            setScoreBoard(scores);
        }


    }

    public void insertResult(ArrayList<Score> scoreList){
        Log.d("SCOREEEEE", " in insertResult: ");
        for(Score s: scoreList){
            Log.d("SCOREEEEE", "insertResult: " +  s.toString());
        }
        setScoreBoard(scoreList);

    }

    public void setScoreBoard(ArrayList<Score> scoreList){
        Log.d("SCOREEEEE", "setScoreBoard: ");
        Intent i = new Intent(this, ScoreActivity.class);
        i.putExtra("scoreList", (Serializable) scoreList);
        finish();
        startActivity(i);

    }

    private void endGame(){
        ImageView goView = new ImageView(this);
        goView.setImageResource(R.drawable.game_over);
//        titleView.setTransitionName("Title Fade");
//        float w = titleView.getDrawable().getIntrinsicWidth();
//        float h = titleView.getDrawable().getIntrinsicHeight();
        goView.setX(screenWidth * 0.2f);
        goView.setY(screenHeight * 0.4f);
        getLayout().addView(goView);
        ObjectAnimator tAnim = ObjectAnimator.ofFloat(goView, "alpha", 0.0f, 1.0f);
        tAnim.setInterpolator(new LinearInterpolator());
        tAnim.setDuration(3000);

        tAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                checkScore();
            }
        });
        tAnim.start();

    }

    private void scoreBoard(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initals (up to 3 characters):");
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("CANCEL", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}//