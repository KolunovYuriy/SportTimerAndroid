package com.taktilidu.sporttimer.common;

import android.util.Log;

public class exLog {
	final static boolean bWriteLog = true;
	final static String logsName = "MyLogs";

	public static int d (String tag, String msg){
		if (bWriteLog) {return Log.d(tag, msg);}
		else return -1;
	}
	public static int e (String tag, String msg){
		if (bWriteLog) {return Log.e(tag, msg);}
		else return -1;
	}
	public static int i (String tag, String msg){
		if (bWriteLog) {return Log.i(tag, msg);}
		else return -1;
	}
	public int v (String tag, String msg){
		if (bWriteLog) {return Log.v(tag, msg);}
		else return -1;
	}
	public static int w (String tag, String msg){
		if (bWriteLog) {return Log.w(tag, msg);}
		else return -1;
	}
	public static int wtf (String tag, String msg){
		if (bWriteLog) {return Log.wtf(tag, msg);}
		else return -1;
	}

	public static int d (String msg){
		if (bWriteLog) {return d(logsName, msg);}
		else return -1;
	}
	public static int e (String msg){
		if (bWriteLog) {return e(logsName, msg);}
		else return -1;
	}
	public static int i (String msg){
		if (bWriteLog) {return i(logsName, msg);}
		else return -1;
	}
	public int v (String msg){
		if (bWriteLog) {return v(logsName, msg);}
		else return -1;
	}
	public static int w (String msg){
		if (bWriteLog) {return w(logsName, msg);}
		else return -1;
	}
	public static int wtf (String msg){
		if (bWriteLog) {return wtf(logsName, msg);}
		else return -1;
	}
}
