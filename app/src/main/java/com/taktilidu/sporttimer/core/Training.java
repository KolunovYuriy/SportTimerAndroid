package com.taktilidu.sporttimer.core;

import android.view.View;

import com.taktilidu.sporttimer.common.laPublic;

import java.util.ArrayList;
import java.util.TreeMap;

public class Training {

    // Parameters
    private String id = "";
    private String name = "";
    private int countExercises = 0;

    public ArrayList<Exercise> listOfExercises;
    public TreeMap<String, Exercise> mapOfExercises;
    public static ArrayList<Training> listOfTrainings;
    public static TreeMap<String, Training> mapOfTrainings;
    public View rowView = null;

    private DB db = DB.giveDBlink();

    public Training(String id, String name) {
        this.id = id;
        this.name = name;

        countExercises = db.getCountOfExercises(id);

        listOfExercises = new ArrayList<Exercise>();
        mapOfExercises = new TreeMap<String, Exercise>();

        db.fillTrainingArrayListOfExercise(listOfExercises, mapOfExercises, id);
    }

    public static void loadTrainingsList() {

        listOfTrainings =  new ArrayList<Training>();
        mapOfTrainings = new TreeMap<String, Training>();

        DB.giveDBlink().fillArrayListOfTrainings(listOfTrainings, mapOfTrainings);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getCountOfExercises() {
        return db.getCountOfExercises(this.id);
    }

    public String getSumTime() {
        return laPublic.TimeFormatShort(
                db.getSumTimeOfTraining(id)
        );
    }

    public void setRowView(View rowView) {
        this.rowView = rowView;
    }

}
