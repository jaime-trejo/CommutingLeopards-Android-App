package edu.wit.mobileapp.commutingleopards;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Map extends FragmentActivity implements MenuItem.OnMenuItemClickListener {


    GoogleMap googleMap;
    private double miles;
    private String miles_output;
    private String start_location;
    private final String TAG = "myApp";
    private final String serverKey = "AIzaSyAKhVAVLx4M3SU-LoEUXdrccpxrQVAhL8Q";
    private String car_emission;
    private double car_emissionCalculation;
    private String bicycle_walking_emission;
    private double train_emissionCalculation;
    private String train_emission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final ImageButton driving_button = (ImageButton)findViewById(R.id.driving_button);
        final ImageButton train_button = (ImageButton)findViewById(R.id.train_button);
        final ImageButton walking_button = (ImageButton)findViewById(R.id.walking_button);
        final ImageButton biking_button = (ImageButton)findViewById(R.id.biking_button);
        final TextView trip_calc = (TextView) findViewById(R.id.trip_calculations);
        //final ImageButton terrain = (ImageButton)findViewById(R.id.terrain);


        //terrain.setBackgroundResource(R.drawable.terrain);

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        googleMap = mapFragment.getMap();

        // Getting the starting location from the home page
        Bundle b = getIntent().getExtras();
        double lat = b.getDouble("start_latLng.latitude");
        double longi = b.getDouble("start_latLng.longitude");
        String start_name = b.getString("start_location_name");

        final LatLng start_latLng = new LatLng(lat,longi);

        // Add a marker at Wentworth
        final LatLng wentworth = new LatLng(42.3356, -71.0956);
        miles_output = "Trip: " + CalculationByDistance(start_latLng, wentworth) + " miles";
        car_emissionCalculation = CalculationByDistance(start_latLng, wentworth) * 0.83;
        car_emissionCalculation = (double) Math.round(car_emissionCalculation* 100) / 100;
        car_emission =  "Emission: " +car_emissionCalculation + " lbs/eCO2/mile" ;

        train_emissionCalculation = CalculationByDistance(start_latLng, wentworth) * 0.31;
        train_emissionCalculation = (double) Math.round(train_emissionCalculation* 100) / 100;
        train_emission = "Emission: " +train_emissionCalculation + " lbs/eCO2/mile" ;

        bicycle_walking_emission = "Emission: 0 lbs/eCO2/mile" ;


        Marker marker_wentworth = googleMap.addMarker(new MarkerOptions().position(wentworth).title("Wentworth Institute of Technology"));


        // Add a marker for the starting location
        Marker marker_start = googleMap.addMarker(new MarkerOptions().position(start_latLng).title(start_name));



        // Move the camera starting position to Wentworth
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(wentworth));

        // Zoom the camera in a bit just so we don't get a view of the whole United States
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));


        //set zoom settings
        googleMap.getUiSettings().setZoomControlsEnabled(true);

/*
        try {
            googleMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e){
            Log.v(TAG, "Error");
        }
*/
        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.window_layout, null);

                //Setting title text
                TextView title = (TextView) v.findViewById(R.id.location_title);
                title.setText(arg0.getTitle());


                // Getting reference to the TextView to set miles
                //TextView tvMiles = (TextView) v.findViewById(R.id.trip_miles);

                // Getting reference to the TextView to set trip emission
                //TextView tvEmission = (TextView) v.findViewById(R.id.trip_ec);

                // Setting the miles
                // tvMiles.setText(miles_output);

                // Setting the emission
                // tvEmission.setText(emission);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // Display a window above the marker that say's the starting location
        marker_start.showInfoWindow();


        // Display a window above the marker that says it's location title
        marker_wentworth.showInfoWindow();

        // Calculate the bounds of the markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker_start.getPosition());
        builder.include(marker_wentworth.getPosition());

        final LatLngBounds bounds = builder.build();
        final int padding = 500; // offset from edges of the map in pixels


        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                GoogleDirection.withServerKey(serverKey)
                        .from(start_latLng)
                        .to(wentworth)
                        .transportMode(TransportMode.DRIVING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                // Do something here

                                if (direction.isOK()) {

                                    Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);

                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(Map.this, directionPositionList, 5, Color.BLUE);
                                    googleMap.addPolyline(polylineOptions);

                                    Info distanceInfo = leg.getDistance();
                                    Info durationInfo = leg.getDuration();
                                    String distance = distanceInfo.getText();
                                    String duration = durationInfo.getText();

                                    trip_calc.setText("Car Travel Information\n" + "Distance: " + distance + "\n" + car_emission + "\n" + "ETA: " + duration);

                                    Log.v(TAG, "Distance is " + distance + " Duration is " + duration);
                                }
                                else {
                                    // Log.v(TAG, "No route available");
                                    Toast.makeText(getApplicationContext(), "No route available for that form of transportation", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onDirectionFailure(Throwable t) {
                                Log.v(TAG, "Not possible" );
                            }

                        });

                driving_button.setImageResource(R.drawable.cargrey);
                train_button.setImageResource(R.drawable.train);
                biking_button.setImageResource(R.drawable.bike);
                walking_button.setImageResource(R.drawable.walk);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (v.equals(driving_button)) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(wentworth).title("Wentworth Institute of Technology"));
                            // Add a marker for the starting location
                            googleMap.addMarker(new MarkerOptions().position(start_latLng).title(start_location));
                            // do something
                            GoogleDirection.withServerKey(serverKey)
                                    .from(start_latLng)
                                    .to(wentworth)
                                    .transportMode(TransportMode.DRIVING)
                                    .execute(new DirectionCallback() {
                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            // Do something here

                                            if (direction.isOK()) {

                                                Route route = direction.getRouteList().get(0);
                                                Leg leg = route.getLegList().get(0);

                                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(Map.this, directionPositionList, 5, Color.BLUE);
                                                googleMap.addPolyline(polylineOptions);

                                                Info distanceInfo = leg.getDistance();
                                                Info durationInfo = leg.getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();

                                                trip_calc.setText("Car Travel Information\n" + "Distance: " + distance + "\n" + car_emission + "\n" + "ETA: " + duration);

                                                Log.v(TAG, "Distance is " + distance + " Duration is " + duration);
                                            }
                                            else {
                                                //  Log.v(TAG, "No route available");
                                                Toast.makeText(getApplicationContext(), "No route available for that form of transportation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            Log.v(TAG, "Not possible" );
                                        }
                                    });
                            driving_button.setImageResource(R.drawable.cargrey);
                            train_button.setImageResource(R.drawable.train);
                            biking_button.setImageResource(R.drawable.bike);
                            walking_button.setImageResource(R.drawable.walk);
                        }


                        else if (v.equals(train_button)) {
                            // do something else
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(wentworth).title("Wentworth Institute of Technology"));


                            // Add a marker for the starting location
                            googleMap.addMarker(new MarkerOptions().position(start_latLng).title(start_location));

                            GoogleDirection.withServerKey(serverKey)
                                    .from(start_latLng)
                                    .to(wentworth)
                                    .transportMode(TransportMode.TRANSIT)
                                    .execute(new DirectionCallback() {
                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            // Do something here

                                            if (direction.isOK()) {

                                                Route route = direction.getRouteList().get(0);
                                                Leg leg = route.getLegList().get(0);

                                                List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(Map.this, stepList, 5, Color.BLUE, 3, Color.RED);
                                                for (PolylineOptions polylineOption : polylineOptionList) {
                                                    googleMap.addPolyline(polylineOption);
                                                }

                                                Step stepup = new Step();

                                                Info distanceInfo = leg.getDistance();
                                                Info durationInfo = leg.getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();

                                                trip_calc.setText("Transit Travel Information\n" + "Distance: " + distance + "\n" + train_emission + "\n" + "ETA: " + duration);

                                                Log.v(TAG, "Distance is " + distance + " Duration is " + duration);
                                            }
                                            else {
                                                //   Log.v(TAG, "No route available");
                                                Toast.makeText(getApplicationContext(), "No route available for that form of transportation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            Log.v(TAG, "Not possible" );
                                        }
                                    });

                            driving_button.setImageResource(R.drawable.car);
                            train_button.setImageResource(R.drawable.traingrey);
                            biking_button.setImageResource(R.drawable.bike);
                            walking_button.setImageResource(R.drawable.walk);
                        }



                        else if (v.equals(walking_button)) {
                            // do something else
                            googleMap.clear();

                            googleMap.addMarker(new MarkerOptions().position(wentworth).title("Wentworth Institute of Technology"));


                            // Add a marker for the starting location
                            googleMap.addMarker(new MarkerOptions().position(start_latLng).title(start_location));
                            GoogleDirection.withServerKey(serverKey)
                                    .from(start_latLng)
                                    .to(wentworth)
                                    .transportMode(TransportMode.WALKING)
                                    .execute(new DirectionCallback() {
                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            // Do something here

                                            if (direction.isOK()) {

                                                Route route = direction.getRouteList().get(0);
                                                Leg leg = route.getLegList().get(0);

                                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(Map.this, directionPositionList, 5, Color.BLUE);
                                                googleMap.addPolyline(polylineOptions);

                                                Info distanceInfo = leg.getDistance();
                                                Info durationInfo = leg.getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();

                                                trip_calc.setText("Walking Travel Information\n" + "Distance: " + distance + "\n" + bicycle_walking_emission + "\n" + "ETA: " + duration);

                                                Log.v(TAG, "Distance is " + distance + " Duration is " + duration);
                                            }
                                            else {
                                                //   Log.v(TAG, "No route available");
                                                Toast.makeText(getApplicationContext(), "No route available for that form of transportation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            Log.v(TAG, "Not possible" );
                                        }
                                    });

                            driving_button.setImageResource(R.drawable.car);
                            train_button.setImageResource(R.drawable.train);
                            biking_button.setImageResource(R.drawable.bike);
                            walking_button.setImageResource(R.drawable.walkgrey);
                        }



                        else if (v.equals(biking_button)) {
                            // do something else
                            googleMap.clear();

                            googleMap.addMarker(new MarkerOptions().position(wentworth).title("Wentworth Institute of Technology"));


                            // Add a marker for the starting location
                            googleMap.addMarker(new MarkerOptions().position(start_latLng).title(start_location));

                            GoogleDirection.withServerKey(serverKey)
                                    .from(start_latLng)
                                    .to(wentworth)
                                    .transportMode(TransportMode.BICYCLING)
                                    .execute(new DirectionCallback() {
                                        @Override
                                        public void onDirectionSuccess(Direction direction, String rawBody) {
                                            // Do something here

                                            if (direction.isOK()) {

                                                Route route = direction.getRouteList().get(0);
                                                Leg leg = route.getLegList().get(0);

                                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(Map.this, directionPositionList, 5, Color.BLUE);
                                                googleMap.addPolyline(polylineOptions);

                                                Info distanceInfo = leg.getDistance();
                                                Info durationInfo = leg.getDuration();
                                                String distance = distanceInfo.getText();
                                                String duration = durationInfo.getText();

                                                trip_calc.setText("Biking Travel Information\n" + "Distance: " + distance + "\n" + bicycle_walking_emission + "\n" + "ETA: " + duration);

                                                Log.v(TAG, "Distance is " + distance + " Duration is " + duration);
                                            } else {
                                                //   Log.v(TAG, "No route available");
                                                Toast.makeText(getApplicationContext(), "No route available for that form of transportation", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onDirectionFailure(Throwable t) {
                                            Log.v(TAG, "Not possible");
                                        }
                                    });
                            driving_button.setImageResource(R.drawable.car);
                            train_button.setImageResource(R.drawable.train);
                            biking_button.setImageResource(R.drawable.bikegrey);
                            walking_button.setImageResource(R.drawable.walk);
                        }
                    }
                };

                driving_button.setOnClickListener(listener);
                train_button.setOnClickListener(listener);
                walking_button.setOnClickListener(listener);
                biking_button.setOnClickListener(listener);
            }
        });




    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        double Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        valueResult = (double) Math.round(valueResult* 100) / 100;
        double km=valueResult/1;
        km = (double) Math.round(km* 100) / 100;
        // DecimalFormat newFormat = new DecimalFormat("####");
        // double kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult*1000;
        meter = (double) Math.round(meter* 100) / 100;
        // double  meterInDec= Integer.valueOf(newFormat.format(meter));
        miles = km * 0.621371;
        miles = (double) Math.round(miles* 100) / 100;
        Log.i("Radius Value",""+valueResult+"   KM  "+km+" Meter   "+meter + " Miles    " + miles);

        return miles;
    }

    @Override
    public void onBackPressed() {
        googleMap.clear();
        super.onBackPressed();

    }

    @Override
    public void onDestroy() {
        googleMap.clear();
        super.onDestroy();

    }

    @Override
    public void onPause() {
        //googleMap.clear();
        super.onPause();

    }

    // Clicking the Map Style button opens a pop up window where the user
    // can select a map style - normal, terrain, or hybrid
    // The entire map will take on this new style
    public void mapStylePopUp(View v) {
        // Make a pop up menu
        PopupMenu popup = new PopupMenu(this, v);
        // When the listener is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.normal:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        return true;
                    case R.id.hybrid:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        return true;
                    case R.id.terrain:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Open the map style menu
        popup.inflate(R.menu.map_style);
        // and show it
        popup.show();

        //terrain.setBackgroundResource(R.drawable.terraingrey);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}