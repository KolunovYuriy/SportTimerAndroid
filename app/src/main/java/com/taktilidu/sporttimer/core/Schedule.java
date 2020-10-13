package com.taktilidu.sporttimer.core;

import java.util.ArrayList;
import java.util.TreeMap;

public class Schedule {
    private String id = "";
    private String trainingId = "";
    private Training training;
    private long dateTimeStart;
    private String dateStart = "";
    private String timeStart = "";
    private float locationX;
    private float locationY;

    public static ArrayList<Schedule> listOfSchedule;
    public static TreeMap<String, Schedule> mapOfSchedule;

    private DB db = DB.giveDBlink();

    public Schedule(String id, String trainingId, long dateTimeStart) {
        this.id = id;
        this.trainingId = trainingId;
        this.dateTimeStart = dateTimeStart;
        this.training = Training.mapOfTrainings.get(trainingId);
    }

    public static void loadScheduleList() {

        listOfSchedule =  new ArrayList<Schedule>();
        mapOfSchedule = new TreeMap<String, Schedule>();

        DB.giveDBlink().fillArrayListOfSchedule(listOfSchedule, mapOfSchedule);
    }
}
