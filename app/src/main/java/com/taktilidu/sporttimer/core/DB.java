package com.taktilidu.sporttimer.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;

import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laPublic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DB {

	//Constants
	private static final String DB_NAME = "TimerExercises.db";
	private static final int DB_VERSION = 29;
	public static final String TABLE_EXERCISES = "exercises";
	public static final String TABLE_EXERCISE_ITEMS = "exercise_items";
	public static final String TABLE_TRAINIGS = "trainings";
	public static final String TABLE_TRAININGS_RELATION = "trainings_relation";
	public static final String TABLE_SCHEDULE = "schedule";

	public static final String MOVE_ELEMENT_UP = "UP";
	public static final String MOVE_ELEMENT_DOWN = "DOWN";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_EXERCISE_NAME = "exercise_name";
	public static final String COLUMN_EXERCISE_ITEM_NAME = "exercise_item_name";
	public static final String COLUMN_EXERCISE_ID = "exercise_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_TIME_TEXT = "time_string";
	public static final String COLUMN_ORDER = "order_field";
	public static final String COLUMN_SOUND = "is_sound";
	public static final String COLUMN_REPEAT = "is_repeat";
	public static final String COLUMN_TRAINING_ID = "training_id";
	public static final String COLUMN_TRAINING_NAME = "training_name";
	public static final String COLUMN_SCHEDULE_DATETIME = "DATETIME"; //"YYYY-MM-DD HH:MM:SS.SSS"

	//
	private ContactDBHelper DBHelper;
	private SQLiteDatabase DB;
	private static DB currentDB;

	private final Context mContext;

	public DB(Context mContext) {
		this.mContext = mContext;
	}

	public static DB giveDBlink(){
		return currentDB;
	}
	//static {}

	//Time Format for application//
	private String ShortTimeFormat(long t) {
		String format = "HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(t));
	}
	//---------------------------//

	// открыть подключение
	public void open() {
		DBHelper = new ContactDBHelper(mContext, DB_NAME, null, DB_VERSION);
		DB = DBHelper.getWritableDatabase();
	    
	    /*
	    if (DB != null) {
        	Toast.makeText(mContext,
        		"DB Contacts is created", Toast.LENGTH_LONG).show();
        }
        else {
        	Toast.makeText(mContext,
        		"Error create database!", Toast.LENGTH_LONG).show();
        }
        //*/
		currentDB=this;
	}

	// закрыть подключение
	public void close() {
		if (DBHelper!=null) DBHelper.close();
	}

	//открыть транзакцию
	public void beginTransaction() {
		DB.beginTransaction();
	}

	//закрыть транзакцию
	public void endTransaction() {
		DB.endTransaction();
	}

	//транзакция успешна
	public void setTransactionSuccessful() {
		DB.setTransactionSuccessful();
	}

	// получить все данные из таблицы TABLE_EXERCISE_ITEMS
	public Cursor getTableExerciseItemsData(String exerciseId) {
		String where = COLUMN_EXERCISE_ID +"=\""+exerciseId+"\"";
		String order = COLUMN_ORDER;
		return DB.query(TABLE_EXERCISE_ITEMS, null, where, null, null, null, order);
		//return DB.query(TABLE_EXERCISE_ITEMS, null, null, null, null, null, null);
	}

	// all data from table TABLE_EXERCISES witch relation with training
	public Cursor getTableExercisesData(String trainingId) {
		String pickedRows = "R";
		String sql;
		/*
		sql =
				"select " +
							 TABLE_EXERCISES+"."+COLUMN_ID
						+","+TABLE_EXERCISES+"."+COLUMN_EXERCISE_NAME
						+","+TABLE_EXERCISES+"."+COLUMN_SOUND
						+","+TABLE_EXERCISES+"."+COLUMN_REPEAT
						+","+pickedRows+"."+COLUMN_ORDER
						+
							" from " + TABLE_EXERCISES + " inner join " +
								" (select "+COLUMN_EXERCISE_ID + ", " + COLUMN_ORDER + " from " + TABLE_TRAININGS_RELATION +
								   " where " + COLUMN_TRAINING_ID +"= ? " + //"\"" + trainingId + "\" " +

								") " + pickedRows +
							" on " + TABLE_EXERCISES+"."+COLUMN_ID+"=" + pickedRows+"."+COLUMN_EXERCISE_ID +
							" order by " + pickedRows+"."+COLUMN_ORDER;
		exLog.i("getTableExercisesData","sql = " + sql);
		//*/
		sql =
				"select " +
						TABLE_EXERCISES+"."+COLUMN_ID
						+","+TABLE_EXERCISES+"."+COLUMN_EXERCISE_NAME
						+","+TABLE_EXERCISES+"."+COLUMN_SOUND
						+","+TABLE_EXERCISES+"."+COLUMN_REPEAT
						+","+TABLE_TRAININGS_RELATION+"."+COLUMN_ORDER
						+
						" from " + TABLE_EXERCISES + ", " + TABLE_TRAININGS_RELATION +
						" where " + TABLE_TRAININGS_RELATION+"."+COLUMN_TRAINING_ID +"= ? " + //"\"" + trainingId + "\" " +

						" and " + TABLE_EXERCISES+"."+COLUMN_ID+"=" + TABLE_TRAININGS_RELATION+"."+COLUMN_EXERCISE_ID +
						" order by " + TABLE_TRAININGS_RELATION +"."+COLUMN_ORDER;

		exLog.i("getTableExercisesData","sql = " + sql);
		String selectionArgs[] = new String[] {
				trainingId
		};
		return DB.rawQuery(sql,selectionArgs);
	}

	// получить данные из таблицы TABLE_EXERCISE_ITEMS по id элемента
	public Cursor getExerciseItemData(String _id) {
		String where = COLUMN_ID+"=\""+_id+"\"";
		return DB.query(TABLE_EXERCISE_ITEMS, null, where, null, null, null, null);
		//return DB.query(TABLE_EXERCISE_ITEMS, null, null, null, null, null, null);
	}

	//получить данные из таблицы TABLE_EXERCISES
	public Cursor getAllTableExercisesData() {
		return DB.query(TABLE_EXERCISES, null, null, null, null, null, null);
	}

	// получить данные по трениовочным сессиям
	public Cursor getExerciseData(int _id) {
		String where = COLUMN_ID+"=\""+_id+"\"";
		//[] result_columns = new String[] {COLUMN_EXERCISE_NAME};
		return DB.query(TABLE_EXERCISES, null, where, null, null, null, null);
	}

	// добавить элемент сессии

	public String insertExercise(String name){
		ContentValues values = new ContentValues();
		String result = "", newExerciseId=laPublic.generateId();
		values.put(COLUMN_ID, newExerciseId);
		values.put(COLUMN_EXERCISE_NAME, name);
		values.put(COLUMN_ORDER, maxExerciseOrder()+1);
		if (DB.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values)>0) {
			result = newExerciseId;
		}
		return result;
	}

	// добавить элемент тренировочной сессии
	public String addItemExercise(String exerciseId, long time, String name) {
		ContentValues values = new ContentValues();

		String result = "";
		String newExerciseId = laPublic.generateId();
		values.put(COLUMN_ID, newExerciseId);
		values.put(COLUMN_EXERCISE_ITEM_NAME, name);
		values.put(COLUMN_EXERCISE_ID, exerciseId);
		values.put(COLUMN_TIME, time);
		values.put(COLUMN_TIME_TEXT, ShortTimeFormat(time));
		values.put(COLUMN_ORDER, maxExerciseItemOrder(exerciseId)+1);
		if (DB.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values) > 0) {
			result=newExerciseId;
		}
		return result;
	}

	// модифицируем имя сессии
	public void updateExerciseName(String exerciseId, String name) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_EXERCISE_NAME, name);

		String where = COLUMN_ID + "=\"" + exerciseId+"\"";
		// Обновляем
		DB.update(TABLE_EXERCISES, updatedValues, where, null);
	}

	// модифицируем элемент тренировочной сессии	
	public void updateItemExercise(String itemId, long time, String name) {
		ContentValues updatedValues = new ContentValues();

		updatedValues.put(COLUMN_EXERCISE_ITEM_NAME, name);
		updatedValues.put(COLUMN_TIME, time);
		updatedValues.put(COLUMN_TIME_TEXT, ShortTimeFormat(time));

		String where = COLUMN_ID + "=\"" + itemId+"\"";
		// Обновляем
		DB.update(TABLE_EXERCISE_ITEMS, updatedValues, where, null);
	}

    // to update time of Exercise item
    public void updateTimeItemExercise(String itemId, long time) {
        ContentValues updatedValues = new ContentValues();

        updatedValues.put(COLUMN_TIME, time);
        updatedValues.put(COLUMN_TIME_TEXT, ShortTimeFormat(time));

        String where = COLUMN_ID + "=\"" + itemId+"\"";
        DB.update(TABLE_EXERCISE_ITEMS, updatedValues, where, null);
    }

	// удаляем элемент тренировочной сессии	
	public void deleteItemExercise(int itemId) {
		String where = COLUMN_ID + "=\"" + itemId+"\"";
		// Удаляем элемент сессии
		DB.delete(TABLE_EXERCISE_ITEMS, where, null);
	}

	// удаляем сессию	
	public void deleteExercise(String exerciseId) {
		String where = COLUMN_ID + "=\"" + exerciseId+"\"";
		// Удаляем элементы сессии
		Cursor c = getTableExerciseItemsData(exerciseId);
		if (c.moveToFirst()) {
			do {
				deleteItemExercise(c.getInt(c.getColumnIndex(COLUMN_ID)));
			} while (c.moveToNext());
		}
		c.close();
		// Удаляем сессию
		DB.delete(TABLE_EXERCISES, where, null);
	}

	// удаляем элемент сессии	
	public void deleteExercise(String itemId, String exerciseId) {
		String where = COLUMN_ID + "=\"" + itemId + "\" and " + COLUMN_EXERCISE_ID + " = \"" + exerciseId+"\"";
		// Удаляем элемент сессии
		DB.delete(TABLE_EXERCISE_ITEMS, where, null);
	}

	// поменять order сессии 
	public void moveElementsOfExercise(String ids, String exerciseId, String moveTo, String tableMove) {
		String orderTo="";
		switch (moveTo) {
			case (MOVE_ELEMENT_UP): {
				orderTo=" ASC";
				break;
			}
			case (MOVE_ELEMENT_DOWN): {
				orderTo=" DESC";
				break;
			}
			default: break;
		}
		String[] result_columns = new String[] {COLUMN_ID};
		String where = COLUMN_ID+" in("+ids+")";
		String order = COLUMN_ORDER + orderTo;
		exLog.i("moveElementsOfExercise","result_columns="+result_columns+", where="+where+", order="+order);
		Cursor c = DB.query(tableMove, result_columns, where, null, null, null, order);
		if (c.moveToFirst()) {
			do {
				moveElementOfExercise(c.getString(0),exerciseId,moveTo,tableMove);
			} while (c.moveToNext());
		}

		c.close();
	}

	// двигаем сессию	//TODO: доделать
	public void moveElementOfExercise(String _id, String exerciseId, String moveTo, String tableMove) {
		//определяем текущий order
		int columnOrderFrom=0;
		String where = COLUMN_ID + "=\"" + _id+"\"";
		String[] result_columns = new String[] {"max("+COLUMN_ORDER+") as max_order"};
		String groupBy = "";
		Cursor c = DB.query(tableMove, result_columns, where, null, null, null, null);
		if (c.moveToFirst()) {
			columnOrderFrom=c.getInt(0);
			exLog.i("moveElementOfExercise","OrderFrom="+columnOrderFrom);
		}
		//двигаем элемент
		switch (moveTo) {
			case (MOVE_ELEMENT_UP): {

				int columnOrderTo=0;
				String columnTo="";
				where = COLUMN_ORDER + "<" + columnOrderFrom;
				if (exerciseId!="") {
					where = where + " and "+ COLUMN_EXERCISE_ID +" = \"" + exerciseId+"\"";
				}
				result_columns = new String[] {"max("+COLUMN_ORDER+") as max_order",COLUMN_ID};
				groupBy = null;//COLUMN_ID;
				c = DB.query(tableMove, result_columns, where, null, groupBy, null, null);
				if (c.moveToFirst()) {
					columnOrderTo=c.getInt(0);
					columnTo=c.getString(1);
					exLog.i("moveElementOfExercise","OrderTo="+columnOrderTo);
				}

				if (columnOrderTo!=0) {
					exLog.i("moveElementOfExercise","columnOrderTo!=0");
					// Обновляем order выбранного элемента					
					changeTrainingOrders(_id, columnOrderFrom, columnOrderTo, columnTo, tableMove);
				}

				break;
			}
			case (MOVE_ELEMENT_DOWN): {

				int columnOrderTo=0;
				String columnTo="";
				where = COLUMN_ORDER + ">" + columnOrderFrom;
				if (exerciseId!="") {
					where = where + " and "+ COLUMN_EXERCISE_ID +" = \"" + exerciseId+"\"";
				}
				result_columns = new String[] {"min("+COLUMN_ORDER+") as min_order",COLUMN_ID};
				groupBy = null;//COLUMN_ID;
				c = DB.query(tableMove, result_columns, where, null, groupBy, null, null);
				if (c.moveToFirst()) {
					columnOrderTo=c.getInt(0);
					columnTo=c.getString(1);
					exLog.i("moveElementOfExercise","OrderTo="+columnOrderTo);
				}

				if (columnOrderTo!=0) {
					exLog.i("moveElementOfExercise","columnOrderTo!=0");
					// Обновляем order выбранного элемента					
					changeTrainingOrders(_id, columnOrderFrom, columnOrderTo, columnTo, tableMove);
				}

				break;
			}
			default: break;
		}
		c.close();
	}

	public void changeTrainingOrders(String exerciseId, int columnOrderFrom, int columnOrderTo, String columnTo, String tableMove) {
		exLog.i("changeTreaningOrders","1");
		String where="";
		// Обновляем order выбранного элемента					
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ORDER, columnOrderTo);
		where = COLUMN_ID + "=\"" + exerciseId+"\"";
		DB.update(tableMove, updatedValues, where, null);
		exLog.i("changeTreaningOrders","update 1 | "+ COLUMN_ID+"=" + exerciseId);
		// Обновляем order друго элемента
		updatedValues.put(COLUMN_ORDER, columnOrderFrom);
		where = COLUMN_ID + "=\"" + columnTo+"\"";
		DB.update(tableMove, updatedValues, where, null);
		exLog.i("changeTreaningOrders","update 2 | "+ COLUMN_ID+"=\"" + columnTo+"\"");
	}

	// копируем сессию

	public String copyExercise(String exerciseId) {
		String where = COLUMN_ID + "=\"" + exerciseId+"\"";
		Cursor c = DB.query(TABLE_EXERCISES, null, where, null, null, null, null);
		ContentValues values = new ContentValues();

		String newExerciseId = "";

		if (c.moveToFirst()) {
			newExerciseId = laPublic.generateId();
			values.put(COLUMN_ID, newExerciseId);
			values.put(COLUMN_EXERCISE_NAME, c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME)));
			values.put(COLUMN_ORDER, maxExerciseOrder()+1);
			values.put(COLUMN_SOUND, c.getInt(c.getColumnIndex(COLUMN_SOUND)));
			values.put(COLUMN_REPEAT, c.getInt(c.getColumnIndex(COLUMN_REPEAT)));

			//newExerciseId = String.valueOf( (int) DB.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values));
			DB.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			Cursor cursorItem = getTableExerciseItemsData(exerciseId);
			if (cursorItem.moveToFirst()) {
				do {
					copyItemExercise(cursorItem.getString(cursorItem.getColumnIndex(COLUMN_ID)), newExerciseId);
				} while (cursorItem.moveToNext());
			}

			//add new object Exercise
			Exercise curExercise = new Exercise(
					newExerciseId,
					c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME)),
					c.getInt(c.getColumnIndex(COLUMN_SOUND)),
					c.getInt(c.getColumnIndex(COLUMN_REPEAT))
			);
			Exercise.listOfExercises.add(curExercise);
			Exercise.mapOfExercises.put(newExerciseId,curExercise);
		}
		c.close();

        return newExerciseId;
	}

	// копируем элемент тренировочной сессии

	public String copyItemExercise(String itemId) {
		return copyItemExercise(itemId, "");
	}

	public String copyItemExercise(String itemId, String exerciseId) {
		String where = COLUMN_ID + "=\"" + itemId+"\"";
		Cursor c = DB.query(TABLE_EXERCISE_ITEMS, null, where, null, null, null, null);
		ContentValues values = new ContentValues();
		String result="", newItemId = laPublic.generateId();
		if (c.moveToFirst()) {
			//System.out.println("COLUMN_EXERCISE_ITEM_NAME="+c.getString(c.getColumnIndex(COLUMN_EXERCISE_ITEM_NAME)));
			values.put(COLUMN_EXERCISE_ITEM_NAME, c.getString(c.getColumnIndex(COLUMN_EXERCISE_ITEM_NAME)));
			if (exerciseId.length()>0) {
				values.put(COLUMN_EXERCISE_ID, exerciseId);
			}
			else {
				exerciseId = c.getString(c.getColumnIndex(COLUMN_EXERCISE_ID));
				values.put(COLUMN_EXERCISE_ID, exerciseId);
			}
			//System.out.println("exerciseId="+values.getAsString(COLUMN_EXERCISE_ID));
			values.put(COLUMN_ID, newItemId);
			values.put(COLUMN_TIME, c.getInt(c.getColumnIndex(COLUMN_TIME)));
			values.put(COLUMN_TIME_TEXT, ShortTimeFormat(c.getInt(c.getColumnIndex(COLUMN_TIME))));
			values.put(COLUMN_ORDER, maxExerciseItemOrder(exerciseId)+1);
			if (DB.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values)>0) {
				result = newItemId;
			}
		}
		c.close();

        return result;
	}

	// находим максимальное значение order сессии

	public int maxExerciseOrder() {
		int Result=0;
		String[] result_columns = new String[] {"max("+COLUMN_ORDER+") as max_order"};
		Cursor c = DB.query(TABLE_EXERCISES, result_columns, null, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			exLog.i("maxSessioanItemOrder","Order="+Result);
		}
		c.close();
		return Result;
	}

	// находимо максимальное значение order для элемента сессии

	public int maxExerciseItemOrder(String exerciseId) {
		int Result=0;
		String where = COLUMN_EXERCISE_ID + "=\"" + exerciseId+"\"";
		String[] result_columns = new String[] {"max("+COLUMN_ORDER+") as max_order"};
		Cursor c = DB.query(TABLE_EXERCISE_ITEMS, result_columns, where, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			exLog.i("maxSessioanItemOrder","Order="+Result);
		}
		c.close();
		return Result;
	}

	// посчитать количество данных из таблицы TABLE_EXERCISE_ITEMS
	public int getCountOfTableExercise() {
		int Result=0;
		String[] result_columns = new String[] {"count() as count_of_exercises"};
		Cursor c = DB.query(TABLE_EXERCISES, result_columns, null, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			//exLog.i("getCountOfTableExercise",""+Result);
		}
		c.close();
		return Result;
	}

	// count of TABLE_TRAINIGS
	public int getCountOfTableTrainings() {
		int Result=0;
		String[] result_columns = new String[] {"count() as count_of_trainings"};
		Cursor c = DB.query(TABLE_TRAINIGS, result_columns, null, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
		}
		c.close();
		return Result;
	}

	// посчитать общее время сессии
	public long getSumTimeOfExercise(String exerciseId) {
		long Result=0;
		String where = COLUMN_EXERCISE_ID + "=\"" + exerciseId+"\"";
		String[] result_columns = new String[] {"sum("+COLUMN_TIME+") as total_time"};
		Cursor c = DB.query(TABLE_EXERCISE_ITEMS, result_columns, where, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			exLog.i("getSumTimeOfAdapterItems",""+Result);
		}
		c.close();
		return Result;
	}

	// sum time of training
	public long getSumTimeOfTraining(String trainingId) {
		long Result=0;

		Cursor c = getTableExercisesData(trainingId);
		if (c.moveToFirst()) {
			do {
				Result+=getSumTimeOfExercise(c.getString(c.getColumnIndex(COLUMN_ID)));
			} while (c.moveToNext());
		}
		c.close();
		return Result;
	}

	// найти время первого элемента сессии
	public long getTimeOfFirstExerciseItem(String exerciseId) {
		long Result=0;
		String where = COLUMN_EXERCISE_ID + "=\"" + exerciseId+"\"";
		Cursor c = DB.query(TABLE_EXERCISE_ITEMS, null, where, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(c.getColumnIndex(COLUMN_TIME));
			exLog.i("getTimeOfFirstExerciseItem",""+Result);
		}
		c.close();
		return Result;
	}

	// посчитать общее количество элементов сессии
	public int getCountOfExerciseItems(String exerciseId) {
		int Result=0;
		String where = COLUMN_EXERCISE_ID + "=\"" + exerciseId+"\"";
		String[] result_columns = new String[] {"count(*) as count_of_items"};
		Cursor c = DB.query(TABLE_EXERCISE_ITEMS, result_columns, where, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			exLog.i("getCountOfExerciseItems",""+Result);
		}
		c.close();
		return Result;
	}

	public int getCountOfExercises(String trainingId) {
		int Result=0;
		String where = COLUMN_TRAINING_ID + "=\"" + trainingId + "\"";
		String[] result_columns = new String[] {"count(*) as count_of_items"};
		Cursor c = DB.query(TABLE_TRAININGS_RELATION, result_columns, where, null, null, null, null);
		if (c.moveToFirst()) {
			Result=c.getInt(0);
			exLog.i("getCountOfExercises",""+Result);
		}
		c.close();
		return Result;
	}

	// вернуть ArrayList<AdapterItem>

    /*
	public void fillArrayListOfExercises(ArrayList<AdapterItem> AL) {
		String order = COLUMN_ORDER;
		Cursor c = DB.query(TABLE_EXERCISES, null, null, null, null, null, order);
		if (c.moveToFirst()) {
			do {
				AL.add(new AdapterItem(
								c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME)),
								getSumTimeOfExercise(c.getInt(c.getColumnIndex(COLUMN_ID))),
								getTimeOfFirstExerciseItem(c.getInt(c.getColumnIndex(COLUMN_ID))),
								getCountOfExerciseItems(c.getInt(c.getColumnIndex(COLUMN_ID))),
								c.getInt(c.getColumnIndex(COLUMN_ID))
						)
				);
				exLog.i("fillArrayListOfAdapterItem","ROW "+
								COLUMN_ID+"="+c.getInt(c.getColumnIndex(COLUMN_ID))+"|"+
								COLUMN_ORDER+"="+c.getString(c.getColumnIndex(COLUMN_ORDER))+"|"+
								COLUMN_EXERCISE_NAME+"="+c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME))+"|"+
								"count_of_item"+"="+getCountOfExerciseItems(c.getInt(c.getColumnIndex(COLUMN_ID)))+"|"
				);
			} while (c.moveToNext());
		}
		c.close();
	}
    //*/

	// return ArrayList<Exercise>
	public void fillArrayListOfExercises(ArrayList<Exercise> AL, TreeMap<String, Exercise> TM) {
		String order = COLUMN_ORDER;
		Cursor c = DB.query(TABLE_EXERCISES, null, null, null, null, null, order);
		if (c.moveToFirst()) {
			do {
				Exercise curExercise = new Exercise(
						c.getString(c.getColumnIndex(COLUMN_ID)),
						c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME)),
						c.getInt(c.getColumnIndex(COLUMN_SOUND)),
						c.getInt(c.getColumnIndex(COLUMN_REPEAT))
				);
				AL.add(curExercise);
				TM.put(c.getString(c.getColumnIndex(COLUMN_ID)),curExercise);

                exLog.i("fillArrayListOfExercises, COLUMN_ID = " + c.getString(c.getColumnIndex(COLUMN_ID)));

				exLog.i("fillArrayListOfAdapterItem","ROW "+
						COLUMN_ID+"="+c.getInt(c.getColumnIndex(COLUMN_ID))+"|"+
						COLUMN_ORDER+"="+c.getString(c.getColumnIndex(COLUMN_ORDER))+"|"+
						COLUMN_EXERCISE_NAME+"="+c.getString(c.getColumnIndex(COLUMN_EXERCISE_NAME))+"|"+
						"count_of_item"+"="+getCountOfExerciseItems(c.getString(c.getColumnIndex(COLUMN_ID)))+"|"
				);
			} while (c.moveToNext());
		}
		c.close();
	}

	// return ArrayList<Schedule>
	public void fillArrayListOfTrainings(ArrayList<Training> AL, TreeMap<String, Training> TM) {
		Cursor c = DB.query(TABLE_TRAINIGS, null, null, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				Training curTraining = new Training(
						c.getString(c.getColumnIndex(COLUMN_ID)),
						c.getString(c.getColumnIndex(COLUMN_TRAINING_NAME))
				);
				AL.add(curTraining);
				TM.put(c.getString(c.getColumnIndex(COLUMN_ID)),curTraining);
				exLog.i("fillArrayListOfTrainings, COLUMN_ID = " + c.getString(c.getColumnIndex(COLUMN_ID)));
			} while (c.moveToNext());
		}
		c.close();
	}

	// return ArrayList<Schedule>
	public void fillArrayListOfSchedule(ArrayList<Schedule> AL, TreeMap<String, Schedule> TM) {
		Cursor c = DB.query(TABLE_SCHEDULE, null, null, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				Schedule curSchedule = new Schedule(
						c.getString(c.getColumnIndex(COLUMN_ID)),
						c.getString(c.getColumnIndex(COLUMN_TRAINING_ID)),
						c.getLong(c.getColumnIndex(COLUMN_SCHEDULE_DATETIME))
				);
				AL.add(curSchedule);
				TM.put(c.getString(c.getColumnIndex(COLUMN_ID)),curSchedule);
				exLog.i("fillArrayListOfSchedules, COLUMN_ID = " + c.getString(c.getColumnIndex(COLUMN_ID)));
			} while (c.moveToNext());
		}
		c.close();
	}

	// return today ArrayList<Training>
	public ArrayList<Training> fillArrayListOfTodayTraining() {
		ArrayList<Training> AL = new ArrayList<Training>();
		long oneDayInSeconds = 24*60*60;
		long today = (System.currentTimeMillis() / 1000L / oneDayInSeconds)*oneDayInSeconds;
		long tomorrow = (System.currentTimeMillis() / 1000L / oneDayInSeconds + 1)*oneDayInSeconds;
		String where = COLUMN_SCHEDULE_DATETIME + " BETWEEN " + today + " AND " + tomorrow;
		Cursor c = DB.query(TABLE_SCHEDULE, null, where, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				AL.add(Training.mapOfTrainings.get(c.getString(c.getColumnIndex(COLUMN_TRAINING_ID))));
				exLog.i("fillArrayListOfSchedules, COLUMN_ID = " + c.getString(c.getColumnIndex(COLUMN_ID)));
			} while (c.moveToNext());
		}
		c.close();
		return AL;
	}

	// для элементов сессии

    /*
	public void fillArrayListOfItem(ArrayList<AdapterItem> AL, int exerciseId) {
		Cursor c = getTableExerciseItemsData(exerciseId);
		if (c.moveToFirst()) {
			do {
				AL.add(new AdapterItem(
								c.getString(c.getColumnIndex(COLUMN_EXERCISE_ITEM_NAME)),
								c.getLong(c.getColumnIndex(COLUMN_TIME)),
								c.getInt(c.getColumnIndex(COLUMN_ID))
						)
				);
			} while (c.moveToNext());
		}

		c.close();
	}
    //*/

	// return ArrayList of items some exercise

	public void fillArrayListOfItem(ArrayList<ExerciseItem> AL, TreeMap<String,ExerciseItem> TM, String exerciseId) {
		Cursor c = getTableExerciseItemsData(exerciseId);
		if (c.moveToFirst()) {
			do {

                //ExerciseItem curExerciseItem2 = new ExerciseItem("","","",1);


				ExerciseItem curExerciseItem = new ExerciseItem(
						c.getString(c.getColumnIndex(COLUMN_ID)),
						exerciseId,
						c.getString(c.getColumnIndex(COLUMN_EXERCISE_ITEM_NAME)),
						c.getLong(c.getColumnIndex(COLUMN_TIME)),
                        c.getInt(c.getColumnIndex(COLUMN_ORDER))
				);
				AL.add(curExerciseItem);
                exLog.i("fillArrayListOfItem, COLUMN_ID = " + c.getString(c.getColumnIndex(COLUMN_ID)));
				TM.put(c.getString(c.getColumnIndex(COLUMN_ID)), curExerciseItem);

			} while (c.moveToNext());
		}

		c.close();
	}

	// return ArrayList of exercises some training

	public void fillTrainingArrayListOfExercise(ArrayList<Exercise> AL, TreeMap<String,Exercise> TM, String trainingId) {
		Cursor c = getTableExercisesData(trainingId);
		exLog.i("fillTrainingArrayListOfExercise","c.getColumnCount() = "+c.getColumnCount());

		if (c.moveToFirst()) {
			do {
				int idIndex = c.getColumnIndex(COLUMN_ID);
				exLog.i("fillTrainingArrayListOfExercise","index = " + idIndex);
				Exercise exercise = Exercise.mapOfExercises.get(c.getString(idIndex));
				AL.add(exercise);
				exLog.i("fillTrainingArrayListOfExercise","COLUMN_ID = " + c.getString(idIndex));
				TM.put(c.getString(idIndex), exercise);

			} while (c.moveToNext());
		}

		c.close();
	}

	//---------------------------------//

	public void updateExerciseSound(String exerciseId, boolean sound) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_SOUND, sound?1:0);

		String where = COLUMN_ID + "=\"" + exerciseId+"\"";
		// Update
		DB.update(TABLE_EXERCISES, updatedValues, where, null);
	}

	//---------------------------------//

	public void updateExerciseRepeat(String exerciseId, boolean repeat) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_REPEAT, repeat?1:0);

		String where = COLUMN_ID + "=\"" + exerciseId+"\"";
		// Update
		DB.update(TABLE_EXERCISES, updatedValues, where, null);
	}

	//---------------------------------//

	public class ContactDBHelper extends SQLiteOpenHelper
			implements BaseColumns {

		public ContactDBHelper(Context context, String name, CursorFactory factory,
							   int version) {
			super(context, name, factory, version);
		}

		public ContactDBHelper(Context context) {
			super(context, DB_NAME, null, 1);
		}


		@Override
		public void onCreate(SQLiteDatabase db) {

			//создать таблицу TABLE_EXERCISES
			db.execSQL("CREATE TABLE " + TABLE_EXERCISES
					+ " ("
					+ COLUMN_ID+" TEXT PRIMARY KEY, "
					+ COLUMN_ORDER + " INTEGER, "
					+ COLUMN_EXERCISE_NAME + " TEXT, "
					+ COLUMN_SOUND + " INTEGER, "
					+ COLUMN_REPEAT + " INTEGER "
					//+ ", PRIMARY KEY ("+COLUMN_ID+","+COLUMN_ORDER+") "
					//+ PHONE + " TEXT "
					+ ");");

			//создать таблицу TABLE_EXERCISE_ITEMS
			db.execSQL("CREATE TABLE " + TABLE_EXERCISE_ITEMS
					+ " ("
					+ COLUMN_ID+" TEXT PRIMARY KEY, "
					+ COLUMN_EXERCISE_ITEM_NAME + " TEXT, "
					+ COLUMN_EXERCISE_ID + " TEXT, "
					+ COLUMN_TIME + " INTEGER, "
					+ COLUMN_TIME_TEXT + " TEXT, "
					+ COLUMN_ORDER + " INTEGER "
					+ ");");

			//create table TABLE_TRAINIGS

			db.execSQL("CREATE TABLE " + TABLE_TRAINIGS
					+ " ("
					+ COLUMN_ID+" TEXT PRIMARY KEY, "
					+ COLUMN_TRAINING_NAME + " TEXT, "
					+ COLUMN_ORDER + " INTEGER "
					+ ");");

			//create table TABLE_TRAININGS_RELATION

			db.execSQL("CREATE TABLE " + TABLE_TRAININGS_RELATION
					+ " ("
					+ COLUMN_TRAINING_ID + " TEXT, "
					+ COLUMN_EXERCISE_ID + " TEXT, "
					+ COLUMN_ORDER + " INTEGER "
					+ ");");

			//create table TABLE_SCHEDULE

			db.execSQL("CREATE TABLE " + TABLE_SCHEDULE
					+ " ("
					+ COLUMN_ID+" TEXT PRIMARY KEY, "
					+ COLUMN_TRAINING_ID + " TEXT, "
					+ COLUMN_SCHEDULE_DATETIME + " INTEGER " //"YYYY-MM-DD HH:MM:SS.SSS" // количество секунд, прошедших с полуночи (00:00:00 UTC) 1 января 1970 года (четверг)
					+ ");");

			fillInTestDate(db);

		}

		private void fillInTestDate (SQLiteDatabase db) {
			ContentValues values = new ContentValues();

			//-fill-exercises-//

			values.put(COLUMN_EXERCISE_NAME, "Упражнение 1");
			values.put(COLUMN_ID, "0e05bc23-c0ae-49a5-8d27-bd4dfaaa2e1f");
			values.put(COLUMN_ORDER, 1);
			values.put(COLUMN_SOUND, 1);
			values.put(COLUMN_REPEAT, 1);
			db.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			values.put(COLUMN_EXERCISE_NAME, "Упражнение 2");
			values.put(COLUMN_ID, "e68536a9-e7d7-47d8-b9bf-22a35e3779c6");
			values.put(COLUMN_ORDER, 2);
			values.put(COLUMN_SOUND, 0);
			values.put(COLUMN_REPEAT, 0);
			db.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			values.put(COLUMN_EXERCISE_NAME, "Упражнение 3");
			values.put(COLUMN_ID, "cab10cf1-ab39-45ff-950a-c75172d1773f");
			values.put(COLUMN_ORDER, 3);
			db.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			values.put(COLUMN_EXERCISE_NAME, "Упражнение 4");
			values.put(COLUMN_ID, "df6d4bd1-aafb-4466-9629-bce83a3fd552");
			values.put(COLUMN_ORDER, 4);
			db.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			values.put(COLUMN_EXERCISE_NAME, "Упражнение 5");
			values.put(COLUMN_ID, "9674e705-1581-4281-acb4-0997ea19702a");
			values.put(COLUMN_ORDER, 5);
			db.insert(TABLE_EXERCISES, COLUMN_EXERCISE_NAME, values);

			//-fill-exercise-items-//
			ContentValues values2 = new ContentValues();

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 1.1");
			values2.put(COLUMN_EXERCISE_ID, "0e05bc23-c0ae-49a5-8d27-bd4dfaaa2e1f");
			values2.put(COLUMN_TIME, 10*1000);
			values2.put(COLUMN_TIME_TEXT, "00:00:10");
			values2.put(COLUMN_ORDER, 1);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 1.2");
			values2.put(COLUMN_EXERCISE_ID, "0e05bc23-c0ae-49a5-8d27-bd4dfaaa2e1f");
			values2.put(COLUMN_TIME, 30*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:30:00");
			values2.put(COLUMN_ORDER, 2);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 1.3");
			values2.put(COLUMN_EXERCISE_ID, "0e05bc23-c0ae-49a5-8d27-bd4dfaaa2e1f");
			values2.put(COLUMN_TIME, 5*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:05:00");
			values2.put(COLUMN_ORDER, 3);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 1.4");
			values2.put(COLUMN_EXERCISE_ID, "0e05bc23-c0ae-49a5-8d27-bd4dfaaa2e1f");
			values2.put(COLUMN_TIME, 15*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:15:00");
			values2.put(COLUMN_ORDER, 4);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 2.1");
			values2.put(COLUMN_EXERCISE_ID, "e68536a9-e7d7-47d8-b9bf-22a35e3779c6");
			values2.put(COLUMN_TIME, 45*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:45:00");
			values2.put(COLUMN_ORDER, 5);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 2.2");
			values2.put(COLUMN_EXERCISE_ID, "e68536a9-e7d7-47d8-b9bf-22a35e3779c6");
			values2.put(COLUMN_TIME, 15*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:15:00");
			values2.put(COLUMN_ORDER, 6);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 2.3");
			values2.put(COLUMN_EXERCISE_ID, "e68536a9-e7d7-47d8-b9bf-22a35e3779c6");
			values2.put(COLUMN_TIME, 45*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:45:00");
			values2.put(COLUMN_ORDER, 7);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 3.1");
			values2.put(COLUMN_EXERCISE_ID, "cab10cf1-ab39-45ff-950a-c75172d1773f");
			values2.put(COLUMN_TIME, 7*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:07:00");
			values2.put(COLUMN_ORDER, 8);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 3.2");
			values2.put(COLUMN_EXERCISE_ID, "cab10cf1-ab39-45ff-950a-c75172d1773f");
			values2.put(COLUMN_TIME, 20*1000);
			values2.put(COLUMN_TIME_TEXT, "00:00:20");
			values2.put(COLUMN_ORDER, 9);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 4.1");
			values2.put(COLUMN_EXERCISE_ID, "df6d4bd1-aafb-4466-9629-bce83a3fd552");
			values2.put(COLUMN_TIME, 30*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:30:00");
			values2.put(COLUMN_ORDER, 10);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 4.2");
			values2.put(COLUMN_EXERCISE_ID, "df6d4bd1-aafb-4466-9629-bce83a3fd552");
			values2.put(COLUMN_TIME, 2*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:02:00");
			values2.put(COLUMN_ORDER, 11);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 4.3");
			values2.put(COLUMN_EXERCISE_ID, "df6d4bd1-aafb-4466-9629-bce83a3fd552");
			values2.put(COLUMN_TIME, 30*60*1000);
			values2.put(COLUMN_TIME_TEXT, "00:30:00");
			values2.put(COLUMN_ORDER, 12);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			values2.put(COLUMN_ID, laPublic.generateId());
			values2.put(COLUMN_EXERCISE_ITEM_NAME, "Сессия 5.1");
			values2.put(COLUMN_EXERCISE_ID, "9674e705-1581-4281-acb4-0997ea19702a");
			values2.put(COLUMN_TIME, 90*1000);
			values2.put(COLUMN_TIME_TEXT, "1:30");
			values2.put(COLUMN_ORDER, 13);
			db.insert(TABLE_EXERCISE_ITEMS, COLUMN_EXERCISE_ITEM_NAME, values2);

			//-fill-trainings-//
			ContentValues values3 = new ContentValues();

			values3.put(COLUMN_ID, "14f170f0-51e3-4e1b-ac4d-c194f6b95427");
			values3.put(COLUMN_TRAINING_NAME, "Тренировка 1");
			values3.put(COLUMN_ORDER, 1);
			db.insert(TABLE_TRAINIGS, COLUMN_TRAINING_NAME, values3);

			values3.put(COLUMN_ID, "f40a6422-0bfb-4586-b6fe-fffa195faa53");
			values3.put(COLUMN_TRAINING_NAME, "Тренировка 2");
			values3.put(COLUMN_ORDER, 2);
			db.insert(TABLE_TRAINIGS, COLUMN_TRAINING_NAME, values3);

			//-fill-trainings-items-//
			ContentValues values4 = new ContentValues();

			values4.put(COLUMN_TRAINING_ID, "14f170f0-51e3-4e1b-ac4d-c194f6b95427");
			values4.put(COLUMN_EXERCISE_ID, "9674e705-1581-4281-acb4-0997ea19702a");
			values4.put(COLUMN_ORDER, 1);
			db.insert(TABLE_TRAININGS_RELATION, COLUMN_TRAINING_ID, values4);

			values4.put(COLUMN_TRAINING_ID, "14f170f0-51e3-4e1b-ac4d-c194f6b95427");
			values4.put(COLUMN_EXERCISE_ID, "e68536a9-e7d7-47d8-b9bf-22a35e3779c6");
			values4.put(COLUMN_ORDER, 2);
			db.insert(TABLE_TRAININGS_RELATION, COLUMN_TRAINING_ID, values4);

			values4.put(COLUMN_TRAINING_ID, "14f170f0-51e3-4e1b-ac4d-c194f6b95427");
			values4.put(COLUMN_EXERCISE_ID, "cab10cf1-ab39-45ff-950a-c75172d1773f");
			values4.put(COLUMN_ORDER, 3);
			db.insert(TABLE_TRAININGS_RELATION, COLUMN_TRAINING_ID, values4);

			values4.put(COLUMN_TRAINING_ID, "f40a6422-0bfb-4586-b6fe-fffa195faa53");
			values4.put(COLUMN_EXERCISE_ID, "df6d4bd1-aafb-4466-9629-bce83a3fd552");
			values4.put(COLUMN_ORDER, 1);
			db.insert(TABLE_TRAININGS_RELATION, COLUMN_TRAINING_ID, values4);

			values4.put(COLUMN_TRAINING_ID, "f40a6422-0bfb-4586-b6fe-fffa195faa53");
			values4.put(COLUMN_EXERCISE_ID, "9674e705-1581-4281-acb4-0997ea19702a");
			values4.put(COLUMN_ORDER, 2);
			db.insert(TABLE_TRAININGS_RELATION, COLUMN_TRAINING_ID, values4);

			//-fill-SCHEDULE-//

			ContentValues values5 = new ContentValues();

			values5.put(COLUMN_ID, "8002ebdb-362e-4f23-a3a9-61ca2beb5d20");
			values5.put(COLUMN_TRAINING_ID, "14f170f0-51e3-4e1b-ac4d-c194f6b95427");
			values5.put(COLUMN_SCHEDULE_DATETIME, System.currentTimeMillis() / 1000L);
			db.insert(TABLE_SCHEDULE, COLUMN_ID, values5);

			/*
			e5c83904-6875-4ba5-abc0-bceed261fd17
			7618e328-e7ed-4437-a22b-018c6ced01ad
			6c65442f-dd25-4214-88b7-c7830b3cae01
			b7c76a98-4d6c-4cef-9406-8e084cb3393e
			6bc80ab2-36da-4e18-9df5-ab9d8d03e712
			0884740b-8e52-4a22-bda8-0be6bb135658
			b2d443ff-cc53-4a17-a326-10ccbf621887
			40cce512-6b6c-44d7-8219-00a3593c751b
			ace8d528-93ba-4b6f-afda-67743863bf32
			ee513be0-2c87-4e9a-8f22-4588480cd346
			//*/
		}

		@Override
		public void onUpgrade(
				SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_ITEMS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINIGS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS_RELATION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
			onCreate(db);
		}
	}
}
