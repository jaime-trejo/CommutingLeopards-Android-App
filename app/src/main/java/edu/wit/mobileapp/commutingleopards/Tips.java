package edu.wit.mobileapp.commutingleopards;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Tips extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(getResources().getString(R.string.car_safety_header));
        listDataHeader.add(getResources().getString(R.string.personal_safety_header));
        listDataHeader.add(getResources().getString(R.string.bus_safety_header));
        listDataHeader.add(getResources().getString(R.string.train_safety_header));
        listDataHeader.add(getResources().getString(R.string.bicycle_safety_trafficHeader));
        listDataHeader.add(getResources().getString(R.string.bicycle_safety_gearHeader));

        // Adding child data
        List<String> vehicleSafety = new ArrayList<String>();
        vehicleSafety.add(getResources().getString(R.string.car_tip1));
        vehicleSafety.add(getResources().getString(R.string.car_tip2));
        vehicleSafety.add(getResources().getString(R.string.car_tip3));
        vehicleSafety.add(getResources().getString(R.string.car_tip4));
        vehicleSafety.add(getResources().getString(R.string.car_tip5));
        vehicleSafety.add(getResources().getString(R.string.car_tip6));
        vehicleSafety.add(getResources().getString(R.string.car_tip7));
        vehicleSafety.add(getResources().getString(R.string.car_tip8));

        List<String> personalSafety = new ArrayList<String>();
        personalSafety.add(getResources().getString(R.string.personal_tip1));
        personalSafety.add(getResources().getString(R.string.personal_tip2));
        personalSafety.add(getResources().getString(R.string.personal_tip3));

        List<String> busSafety = new ArrayList<String>();
        busSafety.add(getResources().getString(R.string.bus_safety_tip1));
        busSafety.add(getResources().getString(R.string.bus_safety_tip2));
        busSafety.add(getResources().getString(R.string.bus_safety_tip3));

        List<String> trainSafety = new ArrayList<String>();
        trainSafety.add(getResources().getString(R.string.train_safety_tip1));
        trainSafety.add(getResources().getString(R.string.train_safety_tip2));
        trainSafety.add(getResources().getString(R.string.train_safety_tip3));
        trainSafety.add(getResources().getString(R.string.train_safety_tip4));
        trainSafety.add(getResources().getString(R.string.train_safety_tip5));

        List<String> bicycleSafety = new ArrayList<String>();
        bicycleSafety.add(getResources().getString(R.string.bicycle_safety_traffic1));
        bicycleSafety.add(getResources().getString(R.string.bicycle_safety_traffic2));
        bicycleSafety.add(getResources().getString(R.string.bicycle_safety_traffic3));
        bicycleSafety.add(getResources().getString(R.string.bicycle_safety_traffic4));
        bicycleSafety.add(getResources().getString(R.string.bicycle_safety_traffic5));

        List<String> gearSafety = new ArrayList<String>();
        gearSafety.add(getResources().getString(R.string.bicycle_safety_gear1));
        gearSafety.add(getResources().getString(R.string.bicycle_safety_gear2));
        gearSafety.add(getResources().getString(R.string.bicycle_safety_gear3));
        gearSafety.add(getResources().getString(R.string.bicycle_safety_gear4));
        gearSafety.add(getResources().getString(R.string.bicycle_safety_gear5));

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), vehicleSafety);
        listDataChild.put(listDataHeader.get(1), personalSafety);
        listDataChild.put(listDataHeader.get(2), busSafety);
        listDataChild.put(listDataHeader.get(3), trainSafety);
        listDataChild.put(listDataHeader.get(4), bicycleSafety);
        listDataChild.put(listDataHeader.get(5), gearSafety);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}

