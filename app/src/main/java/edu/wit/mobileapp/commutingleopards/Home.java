package edu.wit.mobileapp.commutingleopards;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "main-activity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    //protected TextView mLocationAddressTextView;
    protected AutoCompleteTextView autocompleteView;

    /**
     * Visible while the address is being fetched.
     */
    ProgressBar mProgressBar;

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    Button mFetchAddressButton;

    Button goToMap;

    List<Address> address_list;
    Geocoder geocoder;

    private Context _context;
    private boolean gps_location_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /**
         * For autocompleting location
         */
        final AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item));


        /**
         * Navigation drawer for user to access different parts of the application.
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mResultReceiver = new AddressResultReceiver(new Handler());

        //final EditText mLocationAddressTextView = (EditText) findViewById(R.id.startLoc);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mFetchAddressButton = (Button) findViewById(R.id.fetch_address_button);


        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                //  Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                autocompleteView.showDropDown();
            }
        });


        /**
         * When the user clicks this button, they will be sent to the map where
         * The route to the Wentworth Campus will be displayed
         */
        goToMap = (Button) findViewById(R.id.btn_go);
        goToMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create the list to store the latitude and longitude using Geocoder
                address_list = new ArrayList<Address>();
                geocoder = new Geocoder(Home.this);

                try {
                    address_list = geocoder.getFromLocationName(autocompleteView.getText().toString(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (address_list == null || address_list.equals("") || address_list.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No Route available from starting location.", Toast.LENGTH_SHORT).show();
                } else {
                    Address address = address_list.get(0);

                    // Store the starting location latitude and longitude
                    final LatLng start_latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    double latitude = start_latLng.latitude;
                    double longitude = start_latLng.longitude;
                    Log.v(TAG, "Start latitude is " + latitude);
                    Log.v(TAG, "Start longitude is " + longitude);
                    Intent intent = new Intent(Home.this, Map.class);
                    Bundle b = new Bundle();
                    b.putDouble("start_latLng.latitude", latitude);
                    b.putDouble("start_latLng.longitude", longitude);
                    b.putString("start_location_name", autocompleteView.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);

                }
                if (autocompleteView.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "You must enter a starting location", Toast.LENGTH_SHORT).show();

                }
            }

        });


        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);


        //result = isLocationServiceEnabled();
        //Log.v(TAG,"GPS services: " + result);
        //Toast.makeText(getApplicationContext(), "Please enable your gps.", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(ProgressBar.GONE);
        mFetchAddressButton.setEnabled(true);

        //updateUIWidgets();
        buildGoogleApiClient();

        // Takes a random tip from the array of tips in Strings and displays it via a toast
        // message on application load

        String[] tips_array = this.getResources().getStringArray(R.array.random_tips);
        String random_tip = tips_array[new Random().nextInt(tips_array.length)];

        for (int i=0; i < 2; i++) {
            Toast.makeText(getApplicationContext(),
                    "Green tip: \n" + random_tip,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        /**
         * When the back button is pressed, close the navigation drawer if it is open.
         * Otherwise, leave the application.
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /**
         * When a navigation tray item is clicked,
         * Bring the user to the page the item points to.
         * Also close the drawer afterwards.
         */
        int id = item.getItemId();

        if (id == R.id.tips) {
            Log.v("myApp", "toTips option is clicked");
            Intent intent = new Intent(Home.this, Tips.class);
            startActivity(intent);
        } else if (id == R.id.emissions_data) {
            Log.v("myApp", "toEmmissions option is clicked");
            Intent intent = new Intent(Home.this, EmissionActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Log.v("myApp", "toEmmissions option is clicked");
            Intent intent = new Intent(Home.this, About.class);
            startActivity(intent);
        } else if (id == R.id.legal) {
            Log.v("myApp", "toEmmissions option is clicked");
            Intent intent = new Intent(Home.this, LicenseActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    /**
     * When the application starts, connect to the Google Api Client
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * When the application stops, disconnect from the Google Api Client
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onRestart() {
        super.onRestart();

        if (isLocationServiceEnabled() == true) {
            mFetchAddressButton.setEnabled(true);
        }
    }

    private boolean locationJustUpdated = false;

    @Override
    public void onResume() {
        super.onResume();

        if (isLocationServiceEnabled() == true) {
            mFetchAddressButton.setEnabled(true);
        }
        if (locationJustUpdated == true) { // if location was updated... refresh
            locationJustUpdated = false;
            finish();
            startActivity(getIntent());
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setText(mAddressOutput);
    }

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;
        _context = getApplicationContext();
        if (locationManager == null)
            locationManager = (LocationManager) this._context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Please enable your gps.", Toast.LENGTH_SHORT).show();
        }

        // try{
        //   network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        //}catch(Exception ex){
        //   Toast.makeText(getApplicationContext(), "Please enable your network.", Toast.LENGTH_SHORT).show();
        // }

        return gps_enabled; //|| network_enabled;

    }



    private boolean isLocationEnabled = false;
    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        gps_location_result = isLocationServiceEnabled();

        Log.v(TAG,"GPS sservices gps_location_result: " + gps_location_result);
        Log.v(TAG, "mAddressRequested: " + mAddressRequested);
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        }
        if (gps_location_result == false) {
            Toast.makeText(getApplicationContext(), "Please enable your GPS location.", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
            //finish();
            //startActivity(getIntent());
            locationJustUpdated = true; // just use boolean to say, hey, modifying the location settings so refresh please
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if (mAddressRequested == false && gps_location_result == true)
        {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }

    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }



    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }
            /////// testing
            else {
                showToast("Location is not turned on");
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }


}
