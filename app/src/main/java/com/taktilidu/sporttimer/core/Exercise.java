package com.taktilidu.sporttimer.core;

import android.view.View;

import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laPublic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

public class Exercise {

    // Default
    private static final int DEFAULT_SOUND_VALUE = 1;
    private static final int DEFAULT_REPEAT_VALUE = 1;

    // Parameters

    private String id = "";
    private String name = "";
    private boolean isSound = true;
    private boolean isRepeat = false;
    private long lSumTime = 0;
    private long lFirstTime = 0;
    private int countExerciseItems = 0;
    public View rowView = null;

    public ArrayList<ExerciseItem> listOfExerciseItems;
    public TreeMap<String, ExerciseItem> mapOfExerciseItems;
    public static ArrayList<Exercise> listOfExercises;
    public static TreeMap<String, Exercise> mapOfExercises;

    private DB db = DB.giveDBlink();

    public Exercise(String id, String name, int iSound, int iRepeat) {
        this.id = id;
        this.name = name;
        this.isSound = iSound>0;
        this.isRepeat = iRepeat>0;

        lSumTime = db.getSumTimeOfExercise(id);
        lFirstTime = db.getTimeOfFirstExerciseItem(id);
        countExerciseItems = db.getCountOfExerciseItems(id);
        //this.lSumTime = lTime;
        //this.sSumTime = Long.valueOf(lTime).toString();

        listOfExerciseItems = new ArrayList<ExerciseItem>();
        mapOfExerciseItems = new TreeMap<String, ExerciseItem>();

        db.fillArrayListOfItem(listOfExerciseItems, mapOfExerciseItems, id);
        //exLog.d("id = " + id);
    }

    public static void loadExercisesList() {

        listOfExercises =  new ArrayList<Exercise>();
        mapOfExercises = new TreeMap<String, Exercise>();

        DB.giveDBlink().fillArrayListOfExercises(listOfExercises, mapOfExercises);
    }

    public static void reloadExercisesList() {
        loadExercisesList();}
    public static void reloadExercises(String exercise_id) {
        Exercise curExercise = Exercise.mapOfExercises.get(exercise_id);
        DB.giveDBlink().fillArrayListOfItem(curExercise.listOfExerciseItems, curExercise.mapOfExerciseItems, curExercise.id);
    }
    public void reloadExercises() {

        db.fillArrayListOfItem(listOfExerciseItems, mapOfExerciseItems, this.id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getSound() {return isSound;}

    public boolean getRepeat() {return isRepeat;}

    public String getSumTime() {
        return laPublic.TimeFormatShort(
                db.getSumTimeOfExercise(id)//lSumTime
        );
    }

    public String getSFirstTime() {
        return laPublic.TimeFormatShort(
                db.getTimeOfFirstExerciseItem(id)//lFirstTime
        );
    }

    public int getCountExerciseItems() {
        return db.getCountOfExerciseItems(this.id);
    }

    public View getRowView() {
        return rowView;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        //TODO: изменить в БД
    }

    public void setRowView(View rowView) {
        this.rowView = rowView;
    }

    public void setSound(boolean sound) {
        this.isSound = sound;
        db.updateExerciseSound(this.id,sound);
    }

    public void setSound(int iSound) {
        setSound(iSound>0);
    }

    public void setRepeat(boolean repeat) {
        this.isRepeat = repeat;
        db.updateExerciseRepeat(this.id,repeat);
    }

    public void setRepeat(int iRepeat) {
        setRepeat(iRepeat>0);
    }

    public static String addExercise(String name) {
        String id = DB.giveDBlink().insertExercise(name);
        Exercise curExercise = new Exercise(id,name,DEFAULT_SOUND_VALUE,DEFAULT_REPEAT_VALUE);
        listOfExercises.add(curExercise);
        mapOfExercises.put(id,curExercise);
        return id;
    }

    public static void updateExerciseName(String exerciseId, String name) {
        Exercise curExercise = mapOfExercises.get(exerciseId);
        curExercise.setName(name);
        DB.giveDBlink().updateExerciseName(curExercise.id,name);
    }

    public void updateExerciseName(String name) {
        setName(name);
        db.updateExerciseName(this.id,name);
    }

    public static void deleteExercise(String exerciseId) {
        exLog.i("deleteExercise = " + listOfExercises.remove(mapOfExercises.get(exerciseId))+", exerciseId = "+exerciseId);
        mapOfExercises.remove(exerciseId);
        DB.giveDBlink().deleteExercise(exerciseId);
    }

    public void deleteItem(String itemId) {
        ExerciseItem curExerciseItem = mapOfExerciseItems.get(itemId);
        curExerciseItem.deleteItemFromDB();
        listOfExerciseItems.remove(curExerciseItem);
        mapOfExerciseItems.remove(itemId);
    }

    public static String copy(String exerciseId) {
        String newExerciseId = DB.giveDBlink().copyExercise(exerciseId);
        return newExerciseId;
    }

    public void rearrangeElementItems() {
        Object[] arrayExerciseItems = listOfExerciseItems.toArray();
        for(int i=0;i<arrayExerciseItems.length-1;i++) {
            if ( ((ExerciseItem) arrayExerciseItems[i+1]).getOrder() < ((ExerciseItem)arrayExerciseItems[i]).getOrder())
                DB.giveDBlink().changeTrainingOrders(
                        ((ExerciseItem)arrayExerciseItems[i+1]).getId(),
                        ((ExerciseItem)arrayExerciseItems[i+1]).getOrder(),
                        ((ExerciseItem)arrayExerciseItems[i]).getOrder(),
                        ((ExerciseItem)arrayExerciseItems[i]).getId(),
                        DB.TABLE_EXERCISE_ITEMS
                );
        }
    }

    public static void changeElementsOrder(String ids, String moveDirect)
    {
        String transformIds = "";
        String[] curAl = ids.split(",");
        Integer[] arrIndex = new Integer[curAl.length];

        for(int i=0;i<curAl.length;i++) {
            Exercise curExercise = mapOfExercises.get(curAl[i]);
            int curIndex = listOfExercises.indexOf(curExercise);
            arrIndex[i]=curIndex;
            transformIds+="\""+curAl[i]+"\",";
        }
        transformIds+="\"0\"";

        switch (moveDirect) {
            case (DB.MOVE_ELEMENT_UP): {
                Arrays.sort(arrIndex);
                break;
            }
            case (DB.MOVE_ELEMENT_DOWN): {
                Arrays.sort(arrIndex,Collections.<Integer>reverseOrder());
                break;
            }
            default:
                break;
        }

        for(int i=0;i<arrIndex.length;i++) {
            Exercise curExercise = listOfExercises.get(arrIndex[i]);
            exLog.i("changeElementOrder, curIndex=" + arrIndex[i] + ",  curAl[i]=" + curAl[i] + ",  curExercise=" + curExercise);
            if (curExercise != null) {
                switch (moveDirect) {
                    case (DB.MOVE_ELEMENT_UP): {
                        if (arrIndex[i] != 0) {
                            int nextIndex = arrIndex[i] - 1;
                            Exercise nextTS = listOfExercises.get(nextIndex);
                            listOfExercises.set(nextIndex, curExercise);
                            listOfExercises.set(arrIndex[i], nextTS);
                        }
                        break;
                    }
                    case (DB.MOVE_ELEMENT_DOWN): {
                        if (arrIndex[i] != (listOfExercises.size() - 1)) {
                            int nextIndex = arrIndex[i] + 1;
                            Exercise nextTS = listOfExercises.get(nextIndex);
                            listOfExercises.set(arrIndex[i], nextTS);
                            listOfExercises.set(nextIndex, curExercise);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        DB.giveDBlink().moveElementsOfExercise(transformIds,"",moveDirect,DB.TABLE_EXERCISES);
    }

}