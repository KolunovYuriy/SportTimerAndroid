package com.taktilidu.sporttimer;

import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.core.DB;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private String sCurrentSelectedPosition = Constants.SECTION_ALL_EXERCISES_MODE;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		exLog.i("MyLogs","onCreate Nav 1");
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		exLog.i("MyLogs","onCreate Nav 2");
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
		exLog.i("MyLogs","onCreate Nav 3   "+mCurrentSelectedPosition);
		// Select either the default item (0) or the last selected item.
		if (mCurrentSelectedPosition>0) selectItem(mCurrentSelectedPosition, sCurrentSelectedPosition);
		exLog.i("MyLogs","onCreate Nav 4");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		exLog.i("MyLogs","onActivityCreated Nav 1");
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
		exLog.i("MyLogs","onActivityCreated Nav 2");
	}

	//--класс SwypeItem для бокового меню--//

	private class SwypeItem extends HashMap<String, String> {
		public static final String NAME = "name";
		public static final String COUNT = "count";
		public static final String SECTION = "section";
		//public static final String TITLE = "title";

		public SwypeItem(String name, String count, String section) {
			super();
			super.put(NAME, name );
			super.put(COUNT,count);
			super.put(SECTION,section);
			//super.put(TITLE,count);
		}
	}
	//-------------------------------------//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		exLog.i("MyLogs","onCreateView Nav 1");

		//*
		View mDrawerList = inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);

		mDrawerListView = (ListView) mDrawerList.findViewById(R.id.swypeList);
		//*/
		
		/*
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);//;
				//.findViewById(R.id.swypeList);
		//*/

		exLog.i("MyLogs","onCreateView Nav 2");
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						//selectItem(position);
						selectItem(position,(String)((TextView) view.findViewById(R.id.swype_item_section_name)).getText());
					}
				});
		exLog.i("MyLogs","onCreateView Nav 3");
		//--создаем Adapter--//
		mDrawerListView.setAdapter(setAdapterDrawerListView());
		exLog.i("MyLogs","onCreateView Nav 5");
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		exLog.i("MyLogs","onCreateView Nav 6");

		return mDrawerList;
	}

	public boolean isDrawerOpen() {
		exLog.i("MyLogs","isDrawerOpen");
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public void closeDrawer() {
		exLog.i("closeDrawer");
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);;
		}
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 *
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		exLog.i("MyLogs","setUp Nav 1");
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		exLog.i("MyLogs","setUp Nav 2");

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_r,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		exLog.i("MyLogs","setUp Nav 3");
		//ActionBar actionBar = getActionBar();//TODO: toolbar
		//actionBar.setDisplayHomeAsUpEnabled(true);//TODO: toolbar
		//actionBar.setHomeButtonEnabled(true);//TODO: toolbar

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_navdrawerclosed, /* nav drawer image to replace 'Up' caret */
				R.string.navigation_drawer_open, /*
											 * "open drawer" description for
											 * accessibility
											 */
				R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				exLog.i("MyLogs","onDrawerClosed");
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				exLog.i("MyLogs","onDrawerOpened");
				mDrawerListView.setAdapter(setAdapterDrawerListView());
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().invalidateOptionsMenu(); // calls
				// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		exLog.i("MyLogs","setUp Nav 4");
	}

	public void selectItem(int position, String mode) {
		mCurrentSelectedPosition = position;
		if (mode!= Constants.SECTION_SETTINGS_MODE){//TODO: разобраться в том, что это за хня!. Скорее всего не выделять "Настройка"
			if (mDrawerListView != null) {
				mDrawerListView.setItemChecked(position, true);
			}
			if (mDrawerLayout != null) {
				mDrawerLayout.closeDrawer(mFragmentContainerView);
			}
		}

		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(mode);
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			//showGlobalContextActionBar();//TODO: toolbar
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(String mode);
	}

	//--создаем и присваиваем adapter--//
	private ListAdapter setAdapterDrawerListView (){

		ArrayList<SwypeItem> list = new ArrayList<SwypeItem>();
		list.add(new SwypeItem(getString(R.string.title_section_my_exercise),String.valueOf(DB.giveDBlink().getCountOfTableExercise()), Constants.SECTION_ALL_EXERCISES_MODE));
		list.add(new SwypeItem(getString(R.string.title_section_training_set),"", Constants.SECTION_SET_OF_TRAININGS_MODE));
		list.add(new SwypeItem(getString(R.string.title_section_schedule),"", Constants.SECTION_SCHEDULE_MODE));
		list.add(new SwypeItem(getString(R.string.title_sport_map),"", Constants.SECTION_SPORT_MAP));
		list.add(new SwypeItem(getString(R.string.title_section_settings),"", Constants.SECTION_SETTINGS_MODE));
		exLog.i("MyLogs","onCreateView Nav 4");
		return new SimpleAdapter(
				getContext(),
				//getActionBar().getThemedContext(),
				list,
				R.layout.navigation_list_view,
				new String[] {SwypeItem.NAME,SwypeItem.COUNT,SwypeItem.SECTION},
				new int[] {R.id.swype_item_name,R.id.swype_item_count,R.id.swype_item_section_name}
		);
	}
}