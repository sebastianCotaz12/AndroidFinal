package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetectionItem {

    @SerializedName("class")
    private String itemClass;

    @SerializedName("score")
    private float score;

    @SerializedName("box")
    private List<Float> box;

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<Float> getBox() {
        return box;
    }

    public void setBox(List<Float> box) {
        this.box = box;
    }
}
