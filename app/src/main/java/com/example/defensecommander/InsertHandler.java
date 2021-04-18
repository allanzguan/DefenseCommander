package com.example.defensecommander;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class InsertHandler implements Runnable{
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final MainActivity mainActivity;
    private static String dbURL;
    private Connection conn;
    private static final String TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private ArrayList<Score> scoreList = new ArrayList<Score>();
    private int score, level;
    private String name;

    InsertHandler(MainActivity mainActivity, String name, int score, int level){
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
        this.mainActivity = mainActivity;
        this.name = name;
        this.score = score;
        this.level = level;
    }


    @Override
    public void run() {
        Log.d("SCOREEEEE", "in insert run");
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

            Statement stmt = conn.createStatement();


            String sql = "insert into " + TABLE + " values (" +
                    System.currentTimeMillis() + ", '" + name + "', " + score + ", " + level + ")";

            int result = stmt.executeUpdate(sql);

            Log.d("SCOREEEEE", "in insert run ---" + sql);
            Log.d("SCOREEEEE", "in insert run" +result);

            stmt.close();

            getAll();

            mainActivity.insertResult(scoreList);
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getAll() throws SQLException{
        Statement stmt = conn.createStatement();
        String sql = "select * from " + TABLE + " order by Score DESC LIMIT 10";

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {

            Score s = new Score(rs.getString(2), rs.getInt(3), rs.getInt(4), sdf.format(new Date(rs.getLong(1))) );
            scoreList.add(s);
        }
        rs.close();
        stmt.close();

    }
}

