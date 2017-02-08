package edu.wit.mobileapp.commutingleopards;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

public class LicenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);


        /**
         * Display the License terms for this application in the Text View
         * Unless there google play services is not installed
         */
        String license = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
        TextView textView = (TextView)findViewById(R.id.gps_license_text);

        if (license != null){
            textView.setText(license);
        }else{
            textView.setText("Google Play Services is not installed in this device");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}