package com.taktilidu.sporttimer;

import android.Manifest;
import android.app.Activity;

import com.taktilidu.sporttimer.adapters.ExerciseAdapter;
import com.taktilidu.sporttimer.adapters.TrainingAdapter;
import com.taktilidu.sporttimer.adapters.TrainingDragAdapter;
import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laDialogs;
import com.taktilidu.sporttimer.common.laPublic;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.GeoMap;
import com.taktilidu.sporttimer.core.Training;
import com.woxthebox.draglistview.DragListView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class MainActivitySportTimer extends AppCompatActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private static android.support.v4.app.FragmentManager mSupportFragmentManager;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */

	private static Toolbar mToolbar=null;
	private static CharSequence mTitle;
	private static ExerciseAdapter mExerciseAdapter;
	private static TrainingAdapter mTrainingAdapter;
	private static Context mContext;
	private Toast mDoubleToast;
	private static long mBackPressed;

	private static String mSectionAttached = Constants.SECTION_ALL_EXERCISES_MODE;



	private static android.support.v4.app.FragmentManager mFragmentManager;
	private static SectionsPagerAdapter mSectionsPagerAdapter;
	private static TrainingDragAdapter mTrainingDragAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		laPublic.implementCrashlytics(this);
		mContext =getApplicationContext();

		exLog.i("MyLogs","onCreate 0");

		//setContentView(R.layout.screen_add_exercise);
		setContentView(R.layout.activity_start_sport_timer);

		//toolbar init
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		findViewById(R.id.tabs).setVisibility(View.GONE);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		mToolbar.setTitle(null);
		//*
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		mToolbar.getNavigationIcon().setColorFilter(mContext.getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
		//

		exLog.i("MyLogs","onCreate 1");

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		exLog.i("MyLogs","onCreate 2");
		mTitle = getTitle();

		exLog.i("MyLogs","onCreate 3");
		// Set up the drawer.
		if (mNavigationDrawerFragment != null) mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));//
		exLog.i("MyLogs","onCreate 4");

		//load default mode//
		mNavigationDrawerFragment.selectItem(1, Constants.SECTION_ALL_EXERCISES_MODE);

		//
		mSupportFragmentManager = getSupportFragmentManager();

		//Заствляем экран не тухнуть во время работы с приложением//
		//PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//manager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		//mFragmentManager = getFragmentManager();
	}

	public void forceCrash(View view) {
		throw new RuntimeException("This is a crash");
	}

	@Override
	public void onBackPressed() {//TODO покрасить в красивый диалог
		if (mSectionAttached == Constants.SELECT_SOME_EXERCISE_MODE) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(Constants.SECTION_ALL_EXERCISES_MODE,this)).commit();//
			return;
		}
		if (mBackPressed + 2000 > System.currentTimeMillis())
		{
			if (mDoubleToast != null)
				mDoubleToast.cancel();
			super.onBackPressed();
		}
		else {
			//*
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_back,
					(ViewGroup) findViewById(R.id.toast_back_layout));
			mDoubleToast = new Toast(getApplicationContext());
			mDoubleToast.setDuration(Toast.LENGTH_LONG);
			mDoubleToast.setView(layout);
			mDoubleToast.show();
		}
		mBackPressed = System.currentTimeMillis();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		exLog.i("onActivityResult", "start");
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case (Constants.EDIT_ALL_EXERCISES): {
				if (resultCode == Activity.RESULT_OK) {
					useNotifyDataSetChanged();
					//FragmentManager fragmentManager = getFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(Constants.SECTION_ALL_EXERCISES_MODE,this)).commit();
				}
			}
			case Constants.SELECTED_EXERCISE:
				useNotifyDataSetChanged();
			default: break;
		}
		exLog.i("onActivityResult", "end");
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		exLog.i("onPostResume MainActivitySportTimer");
		useNotifyDataSetChanged();
	}

	@Override
	protected void onStart() {
		super.onStart();
		exLog.i("onStart MainActivitySportTimer");
		useNotifyDataSetChanged();
	}

	private boolean isGrantedLocationPermission() {
		int permACL = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		int permAFL = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

		if (permACL != PackageManager.PERMISSION_GRANTED ||
				permAFL != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
							Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_LOCATION_CODE_SPORT_MAP);
		}
		return permACL == PackageManager.PERMISSION_GRANTED && permAFL == PackageManager.PERMISSION_GRANTED;
	}

	//

	//*
	private static boolean isSimplePreferences(Context context) {
		return Constants.ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}
	//*/

	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	//onNavigationDrawerItemSelected

	@Override
	public void onNavigationDrawerItemSelected(String mode) {
		// update the main content by replacing fragments
		if (onSectionAttached(mode)) {

			exLog.i("MyLogs", "onNavigationDrawerItemSelected 1");
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager
					.beginTransaction()
					.replace(R.id.container,
							PlaceholderFragment.newInstance(mode, this)).commit();
			exLog.i("MyLogs", "onNavigationDrawerItemSelected 2");
		}
		else {mNavigationDrawerFragment.closeDrawer();}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case Constants.PERMISSION_LOCATION_CODE_SPORT_MAP: {
				Map<String, Integer> perms = new HashMap<>();
				// Initial
				perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
				perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						&& perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					//startBackgroundServiceForLocationUpdate();
					mSectionAttached = Constants.SECTION_SPORT_MAP;
					mTitle = getString(R.string.title_sport_map);
					restoreActionBar();
					onNavigationDrawerItemSelected(Constants.SECTION_SPORT_MAP);//
				} else {
					// Permission Denied
					laPublic.customToast(this, "Some Permission is Denied").show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public boolean onSectionAttached(String name) {
		exLog.i("onSectionAttached","name="+name);
		switch (name) {
			case Constants.SECTION_ALL_EXERCISES_MODE:
				mSectionAttached = Constants.SECTION_ALL_EXERCISES_MODE;
				mTitle = getString(R.string.title_section_my_exercise);
				return true;
			case Constants.SECTION_SET_OF_TRAININGS_MODE:
				mSectionAttached = Constants.SECTION_SET_OF_TRAININGS_MODE;
				mTitle = getString(R.string.title_section_training_set);
				return true;
			case Constants.SECTION_SCHEDULE_MODE:
				mSectionAttached = Constants.SECTION_SCHEDULE_MODE;
				mTitle = getString(R.string.title_section_schedule);
				return true;
            case Constants.SECTION_SPORT_MAP:
				if (!isGrantedLocationPermission()) {
					return false;
				}
                mSectionAttached = Constants.SECTION_SPORT_MAP;
                mTitle = getString(R.string.title_sport_map);
                /*
				Intent i = new Intent();
				i.setClass(this, MapsActivity.class);
				startActivity(i);
				//*/
				return true;
			case Constants.SECTION_SETTINGS_MODE:
				mSectionAttached = Constants.SECTION_SETTINGS_MODE;
				mTitle = getString(R.string.title_section_settings);
				initSettings();
				return true;
		}
		return false;
	}

	public void restoreActionBar() {
		//ActionBar actionBar = getActionBar();
		//laPublic.restoreActionBar(actionBar,mTitle);
		laPublic.restoreActionBar(mToolbar,mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {//
		getMenuInflater().inflate(R.menu.start_sport_timer, menu);
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			switch(mSectionAttached){
				case Constants.SECTION_ALL_EXERCISES_MODE:
					menu.findItem(R.id.action_edit).setVisible(true);
					menu.findItem(R.id.action_add).setVisible(true);
					menu.findItem(R.id.action_refresh).setVisible(false);
					break;
				case Constants.SECTION_SET_OF_TRAININGS_MODE:
					menu.findItem(R.id.action_edit).setVisible(false);
					menu.findItem(R.id.action_add).setVisible(false);
					menu.findItem(R.id.action_refresh).setVisible(true);
					break;
				default: break;
			}
			exLog.i("onCreateOptionsMenu", "mSectionAttached="+ mSectionAttached);
			restoreActionBar();//TODO: toolbar
			return true;
		}
		else {
			menu.findItem(R.id.action_edit).setVisible(false);
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_refresh).setVisible(false);
		};
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent i = new Intent();
		switch (item.getItemId()) {
			case R.id.action_edit:
				i.setClass(this, ExerciseEditActivity.class);//
				i.putExtra(Constants.CONST_MODE, mSectionAttached);
				//i.putExtra(Constants.CONST_EXERCISE_ID, mExerciseId);
				startActivityForResult(i, Constants.EDIT_ALL_EXERCISES);
				//startActivity(i);
				return true;
			case R.id.action_add:
                laDialogs.showDialogAddExercise(this, laDialogs.ADD_EDIT_EXERCISE_DIALOG);
				return true;
			case R.id.action_refresh:
				return true;
			default: break;
		}

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private Context curContext;
		private GeoMap mMap;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(String sectionName, Context c) {
			exLog.i("MyLogs","PlaceholderFragment sectionName="+sectionName);
			//PlaceholderFragment fragment = new PlaceholderFragment(c);
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putString(Constants.ARG_SECTION_NAME, sectionName);
			fragment.setArguments(args);
			return fragment;
		}

		public static PlaceholderFragment newInstance(boolean isInitTraining, String exerciseId, Context c) {//
			exLog.i("MyLogs","PlaceholderFragment isInitTraining="+isInitTraining);
			//PlaceholderFragment fragment = new PlaceholderFragment(c);
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putBoolean(Constants.ARG_IS_INIT_EXERCISE, true);
			args.putString(Constants.ARG_EXERCISE_ID, exerciseId);
			args.putString(Constants.ARG_SECTION_NAME, Constants.SECTION_SET_OF_TRAININGS_MODE);
			fragment.setArguments(args);//
			return fragment;
		}

		/*
		public PlaceholderFragment(Context c) {
			exLog.i("MyLogs","PlaceholderFragment");
			//curContext = c;
		}
		//*/

		public PlaceholderFragment() {
			exLog.i("MyLogs","PlaceholderFragment");
			//curContext = MainActivitySportTimer.mContext;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			exLog.i("MyLogs","onCreateView 1");
			View rootView = inflater.inflate(
					R.layout.fragment_start_sport_timer, container, false);
			//TextView TV = (TextView) rootView.findViewById(R.id.section_label);
			//TV.setText(mTitle);

			exLog.i("MyLogs","onCreateView ARG_SECTION_NAME="+getArguments().getString(Constants.ARG_SECTION_NAME));

			exLog.i("MyLogs","onCreateView 2");
//
			if (getArguments().getString(Constants.ARG_SECTION_NAME).equals(Constants.SECTION_ALL_EXERCISES_MODE)) {
				exLog.i("onCreateView, fragment SECTION_ALL_EXERCISES_MODE");
				return initExercises(inflater, container, savedInstanceState);
			}
			if (getArguments().getString(Constants.ARG_SECTION_NAME).equals(Constants.SECTION_SET_OF_TRAININGS_MODE)) {//2
				exLog.i("onCreateView, fragment SECTION_SET_OF_TRAININGS_MODE");
				return initTrainings(inflater, container);
			}
			if (getArguments().getString(Constants.ARG_SECTION_NAME).equals(Constants.SECTION_SCHEDULE_MODE)) {//3
				exLog.i("onCreateView, fragment SECTION_SHEDULE_MODE");
				return initSchedule(inflater, container);
			}
            //if (false) {//(getArguments().getString(Constants.ARG_SECTION_NAME).equals(Constants.SECTION_SPORT_MAP)) {//4
            if (getArguments().getString(Constants.ARG_SECTION_NAME).equals(Constants.SECTION_SPORT_MAP)) {//4
                exLog.i("onCreateView, fragment SECTION_SHEDULE_MODE");
                return initSportMap(inflater, container, savedInstanceState);
            }
			else {
				exLog.i("onCreateView, fragment null");
				return null;
			}

		}

		//--init-Exercises-list--//
		private View initExercises(LayoutInflater inflater, ViewGroup container,
								   Bundle savedInstanceState) {
			View resultView = null;
			container.getRootView().findViewById(R.id.tabs).setVisibility(View.GONE);

			exLog.i("MainActivitySportTimer","initExercises");
			mTitle = mContext.getResources().getString(R.string.title_section_my_exercise);

			DB db = DB.giveDBlink();
			//if (false)
			if (db.getCountOfTableExercise()>0)
			{

				View mDrawerList = inflater.inflate(
						R.layout.exercises_list, container, false);

				((FrameLayout) mDrawerList.findViewById(R.id.vertical_shadow)).setVisibility(View.GONE);
                ((FrameLayout) mDrawerList.findViewById(R.id.horizontal_shadow)).setVisibility(View.GONE);

				ListView mDrawerListView = (ListView) mDrawerList.findViewById(R.id.myTrainingList);
				ExerciseAdapter adapter = new ExerciseAdapter(mContext, inflater, container);
				mDrawerListView.setAdapter(adapter);
				mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Exercise curExercise = (Exercise) parent.getItemAtPosition(position);
						String exerciseId = curExercise.getId();
						exLog.i("onClick EXERCISE_ID=" + exerciseId);
						//---//
						initIntervalTimer(mContext, exerciseId);
						//fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(true, exerciseId, baseContext)).commit();
					}
				});
				mExerciseAdapter = adapter;
				resultView = mDrawerList;
			}
			else {
				resultView = inflater.inflate(
						R.layout.screen_add_exercise, container, false);

				LinearLayout layoutAddFirstTrainingExercise = (LinearLayout) resultView.findViewById(R.id.layout_add_first_training_exercise);
				layoutAddFirstTrainingExercise.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(mContext,"Click",Toast.LENGTH_LONG);
					}
				});

			}
			return resultView;
		}
		//-------------------------------//

		//--init_activity_interval-timer--//
		private void initIntervalTimer(Context c, String exerciseId) {
			Intent i = new Intent();
			i.putExtra(Constants.ARG_EXERCISE_ID, exerciseId);
			i.setClass(c, IntervalTimerActivity.class);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivityForResult(i, Constants.SELECTED_EXERCISE);
		}

		//--Activate-Shedule--//
		private View initSchedule(LayoutInflater inflater, ViewGroup container) {
			exLog.i("initSchedule","1");
			container.getRootView().findViewById(R.id.tabs).setVisibility(View.GONE);
			View rootView = inflater.inflate(
					//R.layout.fragment_start_sport_timer, container, false);
					R.layout.schedule, container, false);
			return rootView;
		}

		//--Activate-Sport-Map--//
		private View initSportMap(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			exLog.i("initSportMap","1");
			container.getRootView().findViewById(R.id.tabs).setVisibility(View.GONE);

			mMap = GeoMap.sportObjectsMap(getActivity(), inflater, container, savedInstanceState);

			return mMap.getView();
		}



		private ViewPager mViewPager;
		//--Активировать Мои тренировки--//
		private View initTrainings(LayoutInflater inflater, ViewGroup container) {
			exLog.i("initTrainings","1");
			container.getRootView().findViewById(R.id.tabs).setVisibility(View.VISIBLE);

			View resultView = inflater.inflate(
					R.layout.training, container, false);
			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) resultView.findViewById(R.id.container);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			TabLayout tabLayout = (TabLayout) container.getRootView().findViewById(R.id.tabs);
			tabLayout.setupWithViewPager(mViewPager);

			return resultView;
		}
		//-------------------------------//

	}

	public static class PlaceholderTabFragment extends android.support.v4.app.Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION = "section_id";

		public PlaceholderTabFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderTabFragment newInstance(int tabId) {
			PlaceholderTabFragment fragment = new PlaceholderTabFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION, tabId);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View resultView = null;

            switch (getArguments().getInt(ARG_SECTION)) {
                case R.string.trainings_my_list:
					//resultView = inflater.inflate(R.layout.training_my_list, container, false);
					resultView = initListOfMyTraining(inflater,container);
                    break;
                case R.string.trainings_store:
                    break;
                default:
                    break;
            }
			return resultView;
		}

		private View initListOfMyTraining(LayoutInflater inflater, ViewGroup container) {
			View resultView = null;
			DB db = DB.giveDBlink();
			//if (false)
			if (db.getCountOfTableTrainings()>0)
			{

				resultView = inflater.inflate(
						R.layout.training_my_list, container, false);

				//*

				if ((db.fillArrayListOfTodayTraining().size()>0)) {
					ListView mDrawerListView = (ListView) resultView.findViewById(R.id.today_training_list);
					TrainingAdapter adapter = new TrainingAdapter(mContext, inflater, container, true);
					mDrawerListView.setAdapter(adapter);
					mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Training curTraining = (Training) parent.getItemAtPosition(position);
							String trainingId = curTraining.getId();
							exLog.i("onClick TRAINING_ID=" + trainingId);
							Intent i = new Intent();
							i.putExtra(Constants.ARG_TRAINING_ID, trainingId);
							i.setClass(mContext, IntervalTimerActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(i);
						}
					});
					mTrainingAdapter = adapter;
				}
				else {
					resultView.findViewById(R.id.layout_today_trainings).setVisibility(View.GONE);
				}

				//-init-Drag-and-Drop-list-//
				DragListView mDragListView = (DragListView) resultView.findViewById(R.id.my_training_list);

				mDragListView.setLayoutManager(new LinearLayoutManager(getActivity()));

				TrainingDragAdapter listAdapter = new TrainingDragAdapter(Training.listOfTrainings, R.layout.trainings_drag_list_view, R.id.item_stripe, mContext, true);
				mDragListView.setAdapter(listAdapter, true);
				mDragListView.setCanDragHorizontally(false);

				mTrainingDragAdapter = listAdapter;
				//*/
			}
			return resultView;
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}
		private int[] mTabId = {R.string.trainings_my_list, R.string.trainings_store};

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderTabFragment.newInstance(mTabId[position]);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getBaseContext().getResources().getString(R.string.trainings_my_list);
				case 1:
                    return getBaseContext().getResources().getString(R.string.trainings_store);
			}
			return null;
		}
	}

	//--Активировать настройки--//
	private void initSettings() {
		Intent i = new Intent();
		i.setClass(this, SettingsActivity.class);
		startActivity(i);
	}
	//--------------------------//


	//use outside function "notifyDataSetChanged" of current adapter
	public void useNotifyDataSetChanged(){
        laPublic.useNotifyDataSetChanged(mExerciseAdapter);
	}

}
