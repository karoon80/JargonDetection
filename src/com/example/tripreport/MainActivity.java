package com.example.tripreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends Activity {
	ListView visitedLocations;
	public final static String currentLoc = "Current Location";
	public final static String savedLoc = "Saved Location";
	File locationsList, myVisitedLocations, jargonLists;
	List<String[]> locationList;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      //Retrieve the version name and display it
      		PackageInfo pInfo = null;
      		  try {
      		   pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      		  } catch (NameNotFoundException e) {
      		   e.printStackTrace();
      		  }
      		  String version = pInfo.versionName;
      		  
      		  TextView versionText = (TextView)findViewById(R.id.version_name);
      		  versionText.setText("v."+version);
      		  versionText.setBackgroundColor(Color.BLACK);
      		  versionText.setTextColor(Color.RED);
      		  
      		  TextView label1 = (TextView)findViewById(R.id.label1);
      		  label1.setTextColor(Color.WHITE);
      		  label1.setBackgroundColor(Color.RED);
      		  
     		 //Read from Locations List csv file
      		 locationList = readCsv(this,"LocationsList");
      		  visitedLocations = (ListView)findViewById(R.id.visited_locations);
      		  List<String> locations = new ArrayList<String>();
      		  for(int i = 0; i < locationList.size(); i++){
      			  locations.add(locationList.get(i)[3]+"/"+locationList.get(i)[0]);
      		  }
      		
      		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, locations);
      		visitedLocations.setAdapter(adapter);
      		visitedLocations.setOnItemClickListener(listItemListener);
    }
   
 // Create a message handling object as an anonymous class.
    private OnItemClickListener listItemListener = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        	 Intent savedLocation = new Intent(getApplicationContext(),DisplaySavedLocation.class);  	
        	 
        	 savedLocation.putExtra(savedLoc, locationList.get(position));
       		 startActivity(savedLocation);
        }
    };
    
  //Method called when the Start Scan button clicked
  	public void startScan(View view){
  		  Intent currentLocation = new Intent(this,DisplayCurrentLocation.class);  		  
  		  startActivity(currentLocation);
  		  }
 //Read from csv file method
  	public List<String[]> readCsv(Context context, String fileName) {
		  List<String[]> locList = new ArrayList<String[]>();
		  try {
			  myVisitedLocations = new File(Environment.getExternalStorageDirectory(), "MyVisitedLocations");
			  String path = myVisitedLocations.getAbsolutePath();
			  FileInputStream csvStream =  new FileInputStream(path+"/"+fileName+".csv");
		    InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
		    CSVReader csvReader = new CSVReader(csvStreamReader);
		    String[] line;

		    while ((line = csvReader.readNext()) != null) {
		      locList.add(line);
		    }
		  } catch (IOException e) {
		    e.printStackTrace();
		  }
		  return locList;
		}
  	
  	 
}
