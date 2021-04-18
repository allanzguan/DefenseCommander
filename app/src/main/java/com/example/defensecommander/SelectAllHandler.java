package com.example.defensecommander;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SelectAllHandler implements Runnable{
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final MainActivity mainActivity;
    private static String dbURL;
    private Connection conn;
    private static final String TABLE = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private ArrayList<Score> scoreList = new ArrayList<Score>();

    SelectAllHandler(MainActivity mainActivity){
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
        this.mainActivity = mainActivity;
    }

    public void run(){
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

            Statement stmt = conn.createStatement();

            String sql = "select * from " + TABLE + " order by Score desc limit 10";

            StringBuilder sb = new StringBuilder();

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                Score s = new Score(rs.getString(2), rs.getInt(3), rs.getInt(4), sdf.format(new Date(rs.getLong(1))) );
                scoreList.add(s);
            }
            rs.close();
            stmt.close();
            mainActivity.selectAllResult(scoreList);
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}//

