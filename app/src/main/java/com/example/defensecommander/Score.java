package com.example.defensecommander;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable {
    private String init;
    private int level;
    private int score;
    private String date;

    Score(String init, int score, int level, String date){
        this.init = init;
        this.level = level;
        this.score = score;
        this.date = date;
    }

    public String getInit() {
        return init;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Score{" +
                "init='" + init + '\'' +
                ", level=" + level +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}
