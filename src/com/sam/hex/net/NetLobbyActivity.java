package com.sam.hex.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sam.hex.DialogBox;
import com.sam.hex.Preferences;
import com.sam.hex.R;
import com.sam.hex.startup.StartUpActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class NetLobbyActivity extends Activity {
	Context context;
	private boolean loginSucceeded = false;
	RefreshPlayerlist refreshPlayers;
	final Handler handler = new Handler();
	final Runnable updateResults = new Runnable() {
        public void run() {
        	updateResultsInUi();
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netlobby);
        
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),StartUpActivity.class));
            	finish();
            	StartUpActivity.startup.finish();
            }
        });
        context = getApplicationContext();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("netUsername", "").equals("") || prefs.getString("netPassword", "").equals("")){
        	startActivity(new Intent(getBaseContext(),LoginActivity.class));
        	finish();
        }
        else{
        	new Thread(new Runnable(){
    			@Override
    			public void run() {
    				try {
                		String registrationUrl = String.format("http://www.iggamecenter.com/api_login.php?app_id=%s&app_code=%s&login=%s&password=%s", NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), URLEncoder.encode(prefs.getString("netUsername", ""),"UTF-8"), URLEncoder.encode(prefs.getString("netPassword", ""),"UTF-8"));
    					URL url = new URL(registrationUrl);
    					SAXParserFactory spf = SAXParserFactory.newInstance();
    	                SAXParser parser = spf.newSAXParser();
    	                XMLReader reader = parser.getXMLReader();
    	                XMLHandler xmlHandler = new XMLHandler();
    	                reader.setContentHandler(xmlHandler);
    	                reader.parse(new InputSource(url.openStream()));
    	                
    	                ParsedDataset parsedDataset = xmlHandler.getParsedData();
    	                if(!parsedDataset.error){
    	                	NetGlobal.uid = parsedDataset.getUid();
    	                	NetGlobal.session_id = parsedDataset.getSession_id();
    	        	    	refreshPlayers = new RefreshPlayerlist(handler, updateResults);
    	                	loginSucceeded = true;
    	                }
    	                else{
    	                	new DialogBox(NetLobbyActivity.this, context.getString(R.string.loginFailed), new DialogInterface.OnClickListener() {
    	                	    public void onClick(DialogInterface dialog, int which) {
    	                	        switch (which){
    	                	        case DialogInterface.BUTTON_POSITIVE:
    	                	            //Register button clicked
    	                	        	startActivity(new Intent(getBaseContext(),RegistrationActivity.class));
    	                	        	finish();
    	                	            break;
    	                	        case DialogInterface.BUTTON_NEGATIVE:
    	                	            //Cancel button clicked
    	                	        	android.os.Process.killProcess(android.os.Process.myPid());
    	                	            break;
    	                	        case DialogInterface.BUTTON_NEUTRAL:
    	                	        	//Login button clicked
    	                	        	startActivity(new Intent(getBaseContext(),LoginActivity.class));
    	                	        	finish();
    	                	        	break;
    	                	        }
    	                	    }
    	                	}, context.getString(R.string.register), context.getString(R.string.login), context.getString(R.string.cancel),false);
    	            	}
    				} catch (MalformedURLException e) {
    					e.printStackTrace();
    				} catch (ParserConfigurationException e) {
    					e.printStackTrace();
    				} catch (SAXException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
        	}).start();
        }
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	if(loginSucceeded){
    		if(refreshPlayers!=null) refreshPlayers.stop();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.locallobby_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.settings:
        	startActivity(new Intent(getBaseContext(),Preferences.class));
            return true;
        case R.id.quit:
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	            //Yes button clicked
        	        	android.os.Process.killProcess(android.os.Process.myPid());
        	            break;
        	        case DialogInterface.BUTTON_NEGATIVE:
        	            //No button clicked
        	        	//Do nothing
        	            break;
        	        }
        	    }
        	};

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage(this.getString(R.string.confirmExit)).setPositiveButton(this.getString(R.string.yes), dialogClickListener).setNegativeButton(this.getString(R.string.no), dialogClickListener).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateResultsInUi(){
    	final ListView lobby = (ListView) findViewById(R.id.players);
        ArrayAdapter<ParsedDataset.GameSession> adapter = new ArrayAdapter<ParsedDataset.GameSession>(this,R.layout.simple_list_item_1, NetGlobal.sessions);
        lobby.setAdapter(adapter);
        
        lobby.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
        });
    }
}