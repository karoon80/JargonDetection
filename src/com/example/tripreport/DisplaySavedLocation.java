package com.example.tripreport;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DisplaySavedLocation extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	GoogleMap gMap;
	MarkerOptions markerOptions;
	GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location mLocation;
    TextView locationSaved;
    String [] savedLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_saved_location);
		
		//Retrieve Intent from MainActivity
		Intent displayLocation = getIntent();
		savedLoc = displayLocation.getStringArrayExtra(MainActivity.savedLoc);
		//Google API Client and Location Request code:
		 mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).build();
		 locationSaved = (TextView)findViewById(R.id.location_saved);
		 locationSaved.setText("Here "+savedLoc.length);
		 locationSaved.setText(savedLoc[0]+"\n"+savedLoc[1]+"\n"+savedLoc[2]+"\n"+savedLoc[3]);
		 
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
	        //Google Map handle
			  gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map2)).getMap();
			  LatLng initialPos = new LatLng(Double.valueOf(savedLoc[4]),Double.valueOf(savedLoc[5]));
			  markerOptions = new MarkerOptions();
			  markerOptions.position(initialPos);
			  markerOptions.title("Begin");
			  gMap.addMarker(markerOptions);
			  gMap.moveCamera(CameraUpdateFactory.newLatLng(initialPos));
			  gMap.animateCamera(CameraUpdateFactory.zoomTo(11));
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();
	        // Connect the client.
	        mGoogleApiClient.connect();
	    }

	    @Override
	    protected void onStop() {
	        // Disconnecting the client invalidates it.
	        mGoogleApiClient.disconnect();
	        super.onStop();
	    }
	    
	    @SuppressLint("NewApi") public void onLocationChanged(Location location) {
	    }

		public void onDisconnected() {
			// TODO Auto-generated method stub
			Toast.makeText(this, "Disconnected. Please re-connect.",
	                Toast.LENGTH_SHORT).show();
		}
}
