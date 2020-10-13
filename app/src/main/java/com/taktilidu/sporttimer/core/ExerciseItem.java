package com.taktilidu.sporttimer.core;

import android.view.View;

import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laPublic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.taktilidu.sporttimer.core.Exercise.mapOfExercises;

public class ExerciseItem {
    private String id = "";
    private String exerciseId = "";
    private String name = "";
    private long lTime = 0;
    private int order = -1;
    public View rowView;

    private DB db = DB.giveDBlink();

    public ExerciseItem(String id, String exerciseId, String name, long lTime, int order) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.name = name;
        this.lTime = lTime;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public String getName() {
        return name;
    }

    public String getSTime() {
        return laPublic.TimeFormatShort(lTime);
    }

    public long getlTime() {
        return lTime;
    }

    public void setlTime(long lTime) {
        this.lTime = lTime;
    }

    public void setTime(long lTime) {
        setlTime(lTime);
        db.updateTimeItemExercise(id, lTime);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRowView(View rowView) {
        this.rowView = rowView;
    }
    public View getRowView() {
        return rowView;
    }

    public static void updateItem(String exerciseId, String itemId, Long time, String itemName) {
        exLog.i("updateItem, exerciseId = "+exerciseId + ", itemId = "+itemId);
        Exercise curExercise = mapOfExercises.get(exerciseId);
        ExerciseItem curExerciseItem = curExercise.mapOfExerciseItems.get(itemId);
        exLog.i("updateItem, curExerciseItem = " + curExerciseItem);
        exLog.i("updateItem, time = " + time);
        curExerciseItem.setlTime(time);
        curExerciseItem.setName(itemName);
        DB.giveDBlink().updateItemExercise(itemId, time, itemName);
    }

    public void updateItem(Long time, String itemName) {
        this.setlTime(time);
        this.setName(itemName);
        db.updateItemExercise(this.id, time, itemName);
    }

    public static String addItem(String curExerciseId, long time, String itemName) {
        String itemId = String.valueOf(DB.giveDBlink().addItemExercise(curExerciseId,time,itemName));
        ExerciseItem curExerciseItem = new ExerciseItem(itemId, curExerciseId, itemName, time, DB.giveDBlink().maxExerciseItemOrder(curExerciseId));

        Exercise curExercise = mapOfExercises.get(curExerciseId);
        curExercise.listOfExerciseItems.add(curExerciseItem);
        curExercise.mapOfExerciseItems.put(curExerciseItem.id,curExerciseItem);

        return itemId;
    }

    public static void deleteItemFromExercise(String itemId, String exerciseId) {
        Exercise curExercise = mapOfExercises.get(exerciseId);
        curExercise.deleteItem(itemId);
    }

    public void deleteItemFromDB() {
        db.deleteExercise(this.id,this.exerciseId);
    }

    public static String copy(String itemId, String exerciseId) {
        String newItemId = DB.giveDBlink().copyItemExercise(itemId);

        Exercise curExercise = mapOfExercises.get(exerciseId);
        ExerciseItem curExerciseItem = curExercise.mapOfExerciseItems.get(itemId);

        ExerciseItem copySI = new ExerciseItem(
                newItemId,
                exerciseId,
                curExerciseItem.getName(),
                curExerciseItem.getlTime(),
                curExercise.getCountExerciseItems()//
        );

        curExercise.listOfExerciseItems.add(copySI);
        curExercise.mapOfExerciseItems.put(copySI.id,copySI);

        return copySI.id;
    }

    public static void rearrangeElements() {

    }

    public static void changeElementsOrder(String ids, String exerciseId, String moveDirect)
    {
        String transformIds = "";
        String[] curAl = ids.split(",");
        Integer[] arrIndex = new Integer[curAl.length];

        Exercise curExercise = mapOfExercises.get(exerciseId);
        ArrayList<ExerciseItem> curlistOfExerciseItems = curExercise.listOfExerciseItems;

        for(int i=0;i<curAl.length;i++) {
            ExerciseItem curExerciseItem = curExercise.mapOfExerciseItems.get(curAl[i]);
            int curIndex = curlistOfExerciseItems.indexOf(curExerciseItem);
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
                Arrays.sort(arrIndex, Collections.<Integer>reverseOrder());
                break;
            }
            default:
                break;
        }

        for(int i=0;i<arrIndex.length;i++) {
            ExerciseItem curExerciseItem = curlistOfExerciseItems.get(arrIndex[i]);
            exLog.i("changeElementOrder, curIndex=" + arrIndex[i] + ",  curAl[i]=" + curAl[i] + ",  curExerciseItem=" + curExerciseItem);
            if (curExercise != null) {
                switch (moveDirect) {
                    case (DB.MOVE_ELEMENT_UP): {
                        if (arrIndex[i] != 0) {
                            int nextIndex = arrIndex[i] - 1;
                            ExerciseItem nextSI = curlistOfExerciseItems.get(nextIndex);
                            curlistOfExerciseItems.set(nextIndex, curExerciseItem);
                            curlistOfExerciseItems.set(arrIndex[i], nextSI);
                        }
                        break;
                    }
                    case (DB.MOVE_ELEMENT_DOWN): {
                        if (arrIndex[i] != (curlistOfExerciseItems.size() - 1)) {
                            int nextIndex = arrIndex[i] + 1;
                            ExerciseItem nextSI = curlistOfExerciseItems.get(nextIndex);
                            curlistOfExerciseItems.set(arrIndex[i], nextSI);
                            curlistOfExerciseItems.set(nextIndex, curExerciseItem);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        DB.giveDBlink().moveElementsOfExercise(transformIds,exerciseId,moveDirect,DB.TABLE_EXERCISE_ITEMS);
    }

}