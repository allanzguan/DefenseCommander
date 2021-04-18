package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {

    ArrayList<Score> scoreList = new ArrayList<Score>();
    TextView topTen;
    Button exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);



        SoundPlayer.getInstance().release();
        setupFullScreen();
        topTen = findViewById(R.id.topTen);
        exit = findViewById(R.id.button);

        if(getIntent().hasExtra("scoreList")){
            scoreList = (ArrayList<Score>) getIntent().getSerializableExtra("scoreList");
        }

        StringBuilder sb = new StringBuilder();
//        sb.append("#          Init          Level          Score          Date/Time" + '\n');
        sb.append(String.format("%-10s", "#"));
        sb.append(String.format("%20s", "  Init"));
        sb.append(String.format("%20s", "  Level"));
        sb.append(String.format("%20s", "Score"));
        sb.append(String.format("%30s", "Date/Time"));
        sb.append("\n");

        for(int i = 0; i < scoreList.size(); i++){
//            sb.append(String.format("%d",i+1) );
//            sb.append(String.format("%20s",scoreList.get(i).getInit().toUpperCase()) );
//            sb.append(String.format("%20d",scoreList.get(i).getLevel()) );
//            sb.append(String.format("%20d",scoreList.get(i).getScore()) );
//            sb.append(String.format("%30s",scoreList.get(i).getDate()) );
//            sb.append("\n");

            sb.append(String.format(Locale.getDefault(),
                    "%-10d %20s %20d %20d %30s%n", i+1,
                    scoreList.get(i).getInit().toUpperCase(),
                    scoreList.get(i).getLevel(),
                    scoreList.get(i).getScore(),
                    scoreList.get(i).getDate()));

        }
        topTen.setText(sb.toString());

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

    public void exit(View v){
//        SoundPlayer.getInstance().stop("background");
        finish();
    }
}