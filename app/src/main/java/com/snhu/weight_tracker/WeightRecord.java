package com.snhu.weight_tracker;

import java.io.Serializable;

/**
 * The individual weight record entries for the user body weight journal
 */
public class WeightRecord implements Serializable {
    private int id;
    private float weight;
    private String date;

    public WeightRecord(float weight, String date) {
        this.weight = weight;
        this.date = date;
    }
    public WeightRecord(int id, float weight, String date) {
        this.id = id;
        this.weight = weight;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
