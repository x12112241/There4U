package com.project.x12112241.there4u;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Friends {

    public String date;
    public Double location;

    public Friends() {

    }

    public Friends(String date, Double location) {
        this.date = date;
        this.location = location;
    }

    public Double getLocation() {
        return location;
    }

    public void setLocation(Double location) {
        this.location = location;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}