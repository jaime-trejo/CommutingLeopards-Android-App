package edu.wit.mobileapp.commutingleopards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class EmissionActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emission_activity);


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
        listDataHeader.add(" General Green Tips");
        listDataHeader.add("Walking");
        listDataHeader.add("Biking");
        listDataHeader.add("Driving");

        // Adding child data
        List<String> tips = new ArrayList<String>();
        tips.add("Avoid driving when possible. Boston has a good subway system and reliable cab services in addition to being walkable.");
        tips.add("Research how long it will take to get to campus driving and by public transportation.");
        tips.add("Could you carpool to campus or to a MBTA stop with a friend?");
        tips.add("Is it more efficient and/or cost effective to do a combination of the two options?");

        List<String> walking = new ArrayList<String>();
        walking.add("If you walk or stroll at a steady pace, you will likely walk a mile in 20 minutes.");
        walking.add("Boston is the 3rd most walkable large city in the US with 617,594 residents (2016 Walk Score).");
        walking.add("Vary your routes. This benefits the brain as well as the body.");

        List<String> biking = new ArrayList<String>();
        biking.add("Boston Bikes has installed over 1,800 bike racks around the city");
        biking.add("Bike racks are provided throughout campus, including most residence halls and major academic buildings.");
        biking.add("Bike tire pumps are available on the Pike Pathway next to West Lot.");

        List<String> driving = new ArrayList<String>();
        driving.add ("Aggressive driving can can lower your gas mileage by 33% at highway speeds and by 5% around town.");
        driving.add("Several short trips taken from a cold start can use twice as much fuel as a longer, multipurpose trip covering the same distance.");
        driving.add("Fueleconomy.gov has gas mileage estimates and other information for cars from the current model year back to 1984.");
        driving.add("Gas mileage usually decreases rapidly at speeds above 50 mph.");

        listDataChild.put(listDataHeader.get(0), tips); // Header, Child data
        listDataChild.put(listDataHeader.get(1), walking);
        listDataChild.put(listDataHeader.get(2), biking);
        listDataChild.put(listDataHeader.get(3), driving);
    }


}
