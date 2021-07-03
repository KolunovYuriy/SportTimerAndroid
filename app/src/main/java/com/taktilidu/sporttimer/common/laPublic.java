package com.taktilidu.sporttimer.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.crashlytics.android.Crashlytics;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;

public class laPublic {

	private static boolean isCrashlyticsActive = false;

	// нарисовать кнопку +
	public static void buildAddButton(Context c){

		LayoutInflater ltInflater = ((Activity) c).getLayoutInflater();

		//LinearLayout linLayout = (LinearLayout) ((Activity) c).findViewById(R.id.linLayout);//!
		//RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.rl_add_button);
		//View view = ltInflater.inflate(R.layout.add_button, linLayout, true);//!
		//View view = ltInflater.inflate(R.layout.add_button, relLayout, true);

		//LinearLayout LL = (LinearLayout) ((Activity) c).findViewById(R.id.linLay1);//!
		//LL.addView(view,0);//!

	}

	//Раскрасить Title
	public static void paintTitle(Context c){
		ActionBar AB = ((Activity) c).getActionBar(); // Cryptic Code //TODO: -> making sense name
		Activity A = (Activity) c;
		//AB.setBackgroundDrawable(A.getResources().getDrawable(R.drawable.titlebackgroundcolor));
		//AB.setCustomView(R.layout.fragment_navigation_drawer);
		//AB.s
		AB.setIcon(R.drawable.ic_action_logo);//todo сделать не прикладным кодом, а через xml
	}

	public static void restoreActionBar(ActionBar actionBar, CharSequence mTitle) {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
		//Подготавливаем Title
		setTitleButton(actionBar);
	}

	public static void restoreActionBar(Toolbar toolbar, CharSequence mTitle) {
		toolbar.setTitle(mTitle);
		//toolbar.setLogo(R.drawable.ic_action_logo);
	}

	public static void setTitleButton(ActionBar AB){
		AB.setIcon(R.drawable.ic_action_logo);//todo сделать не прикладным кодом, а через xml
	}

	//Time Format for application//
	private static String TimeFormat(long t, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US); // Cryptic Code //TODO: -> making sense name
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(t));
	}

	public static String TimeFormatFull(long t) {
		String format = "HH:mm:ss.SSS";
		return TimeFormat(t, format);
	}
	//---------------------------//

	//Time Format for application//
	public static String TimeFormatShort(long t) {
		String format = "HH:mm:ss";
		return TimeFormat(t, format);
	}
	//---------------------------//

	//Инициализация БД//
	public static DB InitDB(Context c) {
		DB db = new DB(c);
		db.open();
		return db;
	}
	//----------------//

	//generate random guid//
	public static String generateId() {
		//return "qwe212";
		exLog.i("UUID.randomUUID().toString() = " + UUID.randomUUID().toString());
		return UUID.randomUUID().toString();
	}
	//----------------//

	public static void useNotifyDataSetChanged(BaseAdapter adapter){
		if (adapter !=null) adapter.notifyDataSetChanged();
	}

	public static void implementCrashlytics(Context c){
		if (isCrashlyticsActive) Fabric.with(c, new Crashlytics());
	}

	public static Toast customToast(Activity activity, String text){
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_back,
				(ViewGroup) activity.findViewById(R.id.toast_back_layout));
        ((TextView) layout.findViewById(R.id.toast_text)).setText(text);
		Toast customToast = new Toast(activity.getBaseContext());
		customToast.setDuration(Toast.LENGTH_LONG);
		customToast.setView(layout);
		//customToast.show();
		return customToast;
	}

}
