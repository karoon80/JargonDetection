package com.example.tripreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DisplayCurrentLocation extends ActionBarActivity implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,LocationListener, ConnectionCallbacks{
	GoogleMap gMap;
	MarkerOptions markerOptions;
	GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location mLocation;
    TextView locationInfo;
    protected static final int RESULT_SPEECH = 1;
    Geocoder geocoder;
    List<Address> addresses;
    List<String[]> jargons;
    String addressLine1="";
    String addressLine2="";
    String addressLine3="";
    String jargon="";
    File locationsList, myVisitedLocations, jargonLists;
    double lat, lon;
    int jargonIndex = -1;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_current_location);
		//Retrieve Intent from MainActivity
		Intent displayLocation = getIntent();
		
		//Make Dir
        myVisitedLocations = new File(Environment.getExternalStorageDirectory(), "MyVisitedLocations");
        if (!myVisitedLocations.exists()) {
       	 myVisitedLocations.mkdirs();
		}
        jargonLists = new File(myVisitedLocations, "JargonLists.csv");
        if(!jargonLists.exists()){
        //Declare jargons
			String [] bio = {"chromatid","chromosome","genetic","gene","cytogenetic","embryo","epistatic"
					,"anomaly","cell","germ","mutation","pathogenic","unigenic"};
			String [] computer = {"android","antivirus","ios","app","auto complete","bandwidth",
					"bit","cable","sensor","console","cpu","processor","database","bluetooth","bug","mobile","gage"};
			String [] home = {"home","house","place","room","kitchen","bathroom"};
			String [] gym = {"excercise","workout","treadmill","zumba","elliptical","cycling","turbo","workingout","exercising"};
			String [] food = {"restaurant","lunch","breakfast","dinner","fries","egg","steak","chicken","bacon"
					,"pizza","kabob","rice"};
			String [] entertainment = {"fun","fountain","santa","theater","trolley"};
			//Add jargon lists to a csv file
			addJargonList(bio);
			addJargonList(computer);
			addJargonList(home);
			addJargonList(gym);
			addJargonList(food);
			addJargonList(entertainment);
        }
		
		//Google API Client and Location Request code:
		 mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
         .addOnConnectionFailedListener(this).build();
		 //Speech to text
		 locationInfo = (TextView)findViewById(R.id.location_info);
		 geocoder = new Geocoder(this,Locale.getDefault());
		 
		 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
         intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 30000);
         intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 30000);
         intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.domain.app");

         try {
             startActivityForResult(intent, RESULT_SPEECH);
             locationInfo.setText("");
         } catch (ActivityNotFoundException a) {
             Toast.makeText(getApplicationContext(),"Opps! Your device doesn't support Speech to Text",Toast.LENGTH_SHORT).show();
         }
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        	case RESULT_SPEECH: {
        		if (resultCode == RESULT_OK && null != data) {
        			ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        			String[] separated = text.get(0).split(" ");
        			jargons = readCsv(getApplicationContext());
        			boolean found = false;
        			for(int i = 0; i < separated.length;i++){
        				for(int k = 0; k < jargons.size(); k++){
        					String [] x = jargons.get(k);
        					for(int j = 0; j < x.length; j++){
        						if((x[j].equals(separated[i])) || (x[j].equals(separated[i].substring(0,separated[i].length()-1)))){
        							found = true;
        							jargonIndex = k;
        							break;
        						}
        					}
        				}
        			}
        			locationInfo.setText(addressLine1+"\n"+addressLine2+"\n"+addressLine3);
        			if(jargonIndex == -1){
        				jargon = "N/A";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 0){
        				jargon = "Biology Department";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 1){
        				jargon = "Engineering and Computer Science Department";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 2){
        				jargon = "Home";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 3){
        				jargon = "Gym";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 4){
        				jargon = "Food Place";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			else if(jargonIndex == 5){
        				jargon = "Entertainment";
        				locationInfo.setText(locationInfo.getText()+"\n"+jargon);
        			}
        			addAddress(addressLine1,addressLine2,addressLine3,jargon,lat,lon);
        		}
        		break;
        	}
        }
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

	    @SuppressLint("NewApi") @Override
	    public void onConnected(Bundle bundle) {

	        mLocationRequest = LocationRequest.create();
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        mLocationRequest.setInterval(1000); // Update location every second

	        LocationServices.FusedLocationApi.requestLocationUpdates(
	                mGoogleApiClient, mLocationRequest, this);
	        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	        //Google Map handle
			  gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			  lat = mLocation.getLatitude();
			  lon = mLocation.getLongitude();
			  LatLng initialPos = new LatLng(lat,lon);
			  markerOptions = new MarkerOptions();
			  markerOptions.position(initialPos);
			  markerOptions.title("Begin");
			  gMap.addMarker(markerOptions);
			  gMap.moveCamera(CameraUpdateFactory.newLatLng(initialPos));
			  gMap.animateCamera(CameraUpdateFactory.zoomTo(18));
			  //Get the address
			  try {
				addresses = geocoder.getFromLocation(initialPos.latitude, initialPos.longitude, 5);
				addressLine1=addresses.get(0).getAddressLine(0);
				addressLine2=addresses.get(0).getAddressLine(1);
				addressLine3=addresses.get(0).getAddressLine(2);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	    
	    //Add a new address to the app
	    public void addAddress(String add1, String add2, String add3, String jargon, double lat, double lon) { 
			myVisitedLocations = new File(Environment.getExternalStorageDirectory(), "MyVisitedLocations");

			if (myVisitedLocations.exists()) {
				String path = myVisitedLocations.getAbsolutePath();
				locationsList = new File(myVisitedLocations, "LocationsList.csv");
				try {
					locationsList.createNewFile();
					CSVWriter csvWrite = new CSVWriter(new FileWriter(locationsList , true));
					String arrStr[] = {String.valueOf(add1), String.valueOf(add2),String.valueOf(add3),
							String.valueOf(jargon),String.valueOf(lat),String.valueOf(lon)};
					csvWrite.writeNext(arrStr);
					csvWrite.close();
				} catch (IOException e) {
					Log.d("TAG", "File Error");
				}
			}
	    }
	    
	  //Read from csv file method
	  	public List<String[]> readCsv(Context context) {
			  List<String[]> jargList = new ArrayList<String[]>();
			  try {
				  myVisitedLocations = new File(Environment.getExternalStorageDirectory(), "MyVisitedLocations");
				  String path = myVisitedLocations.getAbsolutePath();
				  FileInputStream csvStream =  new FileInputStream(path+"/JargonLists.csv");
			    InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
			    CSVReader csvReader = new CSVReader(csvStreamReader);
			    String[] line;

			    while ((line = csvReader.readNext()) != null) {
			      jargList.add(line);
			    }
			  } catch (IOException e) {
			    e.printStackTrace();
			  }
			  return jargList;
			}

	    @Override
	    public void onConnectionSuspended(int i) {
	        Log.i("Suspend", "GoogleApiClient connection has been suspend");
	    }

	    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        Log.i("Failed", "GoogleApiClient connection has failed");
	       
	    }
	    @SuppressLint("NewApi") @Override
	    public void onLocationChanged(Location location) {
	      
	    }

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			Toast.makeText(this, "Disconnected. Please re-connect.",
	                Toast.LENGTH_SHORT).show();
		}
		
		//Add jargon list
	    public void addJargonList(String [] jargonList) { 
			myVisitedLocations = new File(Environment.getExternalStorageDirectory(), "MyVisitedLocations");

			if (myVisitedLocations.exists()) {
				jargonLists = new File(myVisitedLocations, "JargonLists.csv");
				try {
					jargonLists.createNewFile();
					CSVWriter csvWrite = new CSVWriter(new FileWriter(jargonLists , true));
					
					csvWrite.writeNext(jargonList);
					csvWrite.close();
				} catch (IOException e) {
					Log.d("TAG", "File Error");
				}
			}
	    }
	    
	    @Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    // Inflate the menu items for use in the action bar
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.main_activity_actions, menu);
		    return super.onCreateOptionsMenu(menu);
		    
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
		    switch (item.getItemId()) {
		        case R.id.back_item:
		            createIntent();
		            return true;
		       
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
		public void createIntent(){
			Intent homePage = new Intent(this,MainActivity.class);
			startActivity(homePage);
			finish();
		}
}
