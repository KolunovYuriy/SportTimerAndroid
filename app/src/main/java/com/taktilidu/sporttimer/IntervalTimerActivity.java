package com.taktilidu.sporttimer;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.taktilidu.sporttimer.adapters.ExerciseAdapter;
import com.taktilidu.sporttimer.adapters.ExerciseItemAdapter;
import com.taktilidu.sporttimer.adapters.ExerciseDragItemAdapter;
import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laDialogs;
import com.taktilidu.sporttimer.common.laPublic;
import com.taktilidu.sporttimer.core.ApplicationTimer;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.Training;
import com.taktilidu.sporttimer.transformers.CustomTransformer;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class IntervalTimerActivity extends AppCompatActivity {

    private static Toolbar mToolbar=null;
    private static Context mContext;
    private static String mTrainingId="";
    private static String mExerciseId="";
    private static String mExerciseItemId="";
    private static ExerciseItemAdapter mExerciseItemAdapter;
    private static ExerciseDragItemAdapter mExerciseDragItemAdapter;
    private static ExerciseAdapter mExerciseAdapter;
    private static ApplicationTimer mAppTimer;
    private static ViewPager mViewPager;
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private static FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        laPublic.implementCrashlytics(this);
        mContext=getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(Constants.ARG_TRAINING_ID)) {
            mTrainingId = extras.getString(Constants.ARG_TRAINING_ID);
            mExerciseId = Training.mapOfTrainings.get(mTrainingId).listOfExercises.get(0).getId();
        }
        else if (extras.containsKey(Constants.ARG_EXERCISE_ID)) {
            mExerciseId = extras.getString(Constants.ARG_EXERCISE_ID);
        }

        if(extras.containsKey(Constants.ARG_EXERCISE_ITEM_ID)) {
            mExerciseItemId = extras.getString(Constants.ARG_EXERCISE_ITEM_ID);
        }
        else {
            mExerciseItemId = "";
        }

        setContentView(R.layout.exercise_tab);

        //toolbar init
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(Exercise.mapOfExercises.get(mExerciseId).getName());
        }
        mToolbar.getNavigationIcon().setColorFilter(this.getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.tabs).setVisibility(View.GONE);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mExerciseId);


        mFragmentManager = getFragmentManager();

        mViewPager = (ViewPager) findViewById(R.id.containerExerciseTab);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageTransformer(true, new CustomTransformer());//StackTransformer());//CubeOutTransformer()
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                exLog.i("onPageScrolled","1");
            }

            @Override
            public void onPageSelected(int position) {
                exLog.i("onPageSelected","1");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                exLog.i("onPageScrollStateChanged","1");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {//
            case (Constants.EDIT_EXERCISE): {
                useNotifyDataSetChanged();
                break;
            }
            default: break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//
        getMenuInflater().inflate(R.menu.start_sport_timer, menu);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_add).setVisible(true);
        menu.findItem(R.id.action_refresh).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = new Intent();
        switch (item.getItemId()) {
            case R.id.action_edit:
                i.setClass(this, ExerciseEditActivity.class);//
                i.putExtra(Constants.CONST_MODE, Constants.SELECT_SOME_EXERCISE_MODE);
                i.putExtra(Constants.CONST_EXERCISE_ID, mExerciseId);
                startActivityForResult(i, Constants.EDIT_EXERCISE);
                //startActivity(i);
                return true;
            case R.id.action_add:
                laDialogs.showDialogAddItemExercise(this, laDialogs.ADD_EDIT_ITEM_EXERCISE_DIALOG, mExerciseId);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public String exerciseId;

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm, String exerciseId) {
            super(fm);
            this.exerciseId = exerciseId;
        }

        public void setExerciseId(String exerciseId) {
            this.exerciseId = exerciseId;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //if (position!=0)
                return PlaceholderExerciseFragment.newInstance(position + 1, this.exerciseId);
            //else return (android.support.v4.app.Fragment) MainActivitySportTimer.PlaceholderFragment.newInstance(NavigationDrawerFragment.SECTION_ALL_EXERCISES_MODE,mContext);
        }

        //*
        @Override
        public float getPageWidth (int position) {
            exLog.i("getPageWidth","position="+position);
            return (position == 1 ? 1f : (1f - Constants.DELTA_WIDTH));
        }
        //*/

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }

    public static class PlaceholderExerciseFragment extends android.support.v4.app.Fragment {

        public PlaceholderExerciseFragment() {
        }

        public static PlaceholderExerciseFragment newInstance(int sectionNumber, String exerciseId) {
            PlaceholderExerciseFragment fragment = new PlaceholderExerciseFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
            args.putString(Constants.ARG_EXERCISE_ID, exerciseId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            switch (getArguments().getInt(Constants.ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = initMyExercises(inflater, container, savedInstanceState);
                    rootView.setTranslationZ(1);
                    break;
                case 2:
                    rootView = initSelectedExercise2(inflater, container, getArguments().getString(Constants.ARG_EXERCISE_ID));
                    rootView.setTranslationZ(2);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.exercise_settings, container, false);
                    mFragmentManager.beginTransaction().replace(R.id.exercise_container, new SettingsFragment()).commit();
                    rootView.setTranslationZ(1);
                    break;
                default:
                    break;
            }

            return rootView;
        }

        //--init-Selected-Exercise--//
        //--Активировать выбранную тренировку--//
        private View initSelectedExercise(LayoutInflater inflater, ViewGroup container, String exerciseId) {//

            exLog.i("initSelectedExercise","1");
            View mDrawerList = inflater.inflate(
                    R.layout.start_exercise_drawer, container, false);

            ListView mDrawerListView = (ListView) mDrawerList.findViewById(R.id.myTrainingList);
            ExerciseItemAdapter adapter = new ExerciseItemAdapter(mContext, inflater, container, exerciseId);
            mDrawerListView.setAdapter(adapter);

            //Вытащить Adapter в глобальные переменные//
            mExerciseItemAdapter = adapter;
            //Создаем кастомный таймер//
            //mAppTimer = new ApplicationTimer(mContext, container, mDrawerList, adapter, Exercise.mapOfExercises.get(exerciseId));

            ((LinearLayout) mDrawerList.findViewById(R.id.main_inerval_timer_layout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exLog.i("onClick","mDrawerList.findViewById(R.id.main_inerval_timer_layout)");
                    //to avoid hidden click
                }
            });

            //клик по позиции
            mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((ListView) parent).smoothScrollToPositionFromTop(position, 0, 500);
                    mExerciseItemAdapter.setSelectedRow(position);
                    if (mAppTimer!=null) mAppTimer.selectPosition(position);
                    exLog.i("onItemClick","onClick position="+(position+1));
                }
            });

            return mDrawerList;
        }

        private View initSelectedExercise2(LayoutInflater inflater, ViewGroup container, final String exerciseId) {//

            View mDrawerList = inflater.inflate(
                    R.layout.start_exercise_drawer, container, false);

            DragListView mDragListView = (DragListView) mDrawerList.findViewById(R.id.myTrainingList);
            mDragListView.setDragListListener(new DragListView.DragListListener() {
                @Override
                public void onItemDragStarted(int position) {
                    //Toast.makeText(getActivity(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemDragging(int itemPosition, float x, float y) {

                }

                @Override
                public void onItemDragEnded(int fromPosition, int toPosition) {
                    if (fromPosition != toPosition) {
                        //Toast.makeText(getActivity(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                        Exercise.mapOfExercises.get(exerciseId).rearrangeElementItems();
                        if (mAppTimer!=null) mAppTimer.setCurrentPosition(toPosition);
                    }
                }
            });

            mDragListView.setLayoutManager(new LinearLayoutManager(getActivity()));

            //--test-//
            /*
            ArrayList<Pair<Long, String>> mItemArray;

            mItemArray = new ArrayList<>();
            for (int i = 0; i < Exercise.mapOfExercises.get(exerciseId).listOfExerciseItems.size(); i++) {
                mItemArray.add(new Pair<>((long) i, "Item " + i));
            }
            //*/
            //ItemAdapter2 listAdapter = new ItemAdapter2(mItemArray, R.layout.exercise_items_drag_list_view, R.id.number, mExerciseId, true);
            //-------//

            String curExerciseItemId = Exercise.mapOfExercises.get(exerciseId).listOfExerciseItems.get(0).getId();
            if (!mExerciseItemId.equals("")) curExerciseItemId = mExerciseItemId;

            ExerciseDragItemAdapter listAdapter = new ExerciseDragItemAdapter(Exercise.mapOfExercises.get(exerciseId).listOfExerciseItems, R.layout.exercise_items_drag_list_view, R.id.item_stripe, mExerciseId, curExerciseItemId, true);
            mDragListView.setAdapter(listAdapter, true);
            mDragListView.setCanDragHorizontally(false);

            mAppTimer = new ApplicationTimer(mContext, container, mDrawerList, listAdapter, Exercise.mapOfExercises.get(exerciseId));

            listAdapter.setApplicationTimer(mAppTimer);
            listAdapter.setDragListView(mDragListView);

            mExerciseDragItemAdapter = listAdapter;

            return mDrawerList;
        }

        //--init-Exercises-list--//
        public View initMyExercises(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
            View resultView = null;

            exLog.i("IntervalTimerActivity","initMyExercises");

            DB db = DB.giveDBlink();
            //if (false)
            if (db.getCountOfTableExercise()>0)
            {

                View mDrawerList = inflater.inflate(
                        R.layout.exercises_list, container, false);

                ListView mDrawerListView = (ListView) mDrawerList.findViewById(R.id.myTrainingList);
                ExerciseAdapter adapter;
                if (mTrainingId.equals(""))
                    adapter = new ExerciseAdapter(mContext, inflater, container, mExerciseId);
                else
                    adapter = new ExerciseAdapter(mContext, inflater, container, mExerciseId, mTrainingId);
                mDrawerListView.setAdapter(adapter);
                mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Context c = parent.getContext();
                        Activity a = (Activity) c;
                        Context baseContext = a.getBaseContext();
                        //---//
                        Exercise curExercise = (Exercise) parent.getItemAtPosition(position);
                        String exerciseId = curExercise.getId();
                        //запишем в глобальную переменную значение//
                        mExerciseId = exerciseId;
                        mExerciseAdapter.setExerciseId(mExerciseId);
                        useNotifyDataSetChanged();
                        reloadTimerView();

                        mViewPager.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mViewPager.setCurrentItem(1, true);
                            }
                        }, 100);

                        //mSectionsPagerAdapter.setExerciseId(mExerciseId);
                        //mViewPager.
                        exLog.i("MyLogs", "onClick EXERCISE_ID=" + exerciseId);
                        //---//
                        //fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(true, exerciseId, baseContext)).commit();
                    }
                });
                mExerciseAdapter = adapter;
                resultView = mDrawerList;
            }

            return resultView;
        }
        //-------------------------------//
        public static class SettingsFragment extends PreferenceFragment {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.activity_preference);
            }
        }

    }

    private static void reloadTimerView() {//TODO: доделать
        //mExerciseItemAdapter.setExerciseId(mExerciseId);
        mExerciseDragItemAdapter.setExerciseId(mExerciseId);
        useNotifyDataSetChanged();
        mToolbar.setTitle(Exercise.mapOfExercises.get(mExerciseId).getName());
        if (mAppTimer!=null) mAppTimer.selectPosition(0);
    }


    public static void useNotifyDataSetChanged(){
        laPublic.useNotifyDataSetChanged(mExerciseItemAdapter);
        laPublic.useNotifyDataSetChanged(mExerciseAdapter);
    }

}
