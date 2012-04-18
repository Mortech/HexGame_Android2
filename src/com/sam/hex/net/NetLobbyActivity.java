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

import com.sam.hex.R;
import com.sam.hex.startup.StartUpActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class NetLobbyActivity extends Activity {
	Context context;
	
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
    	                XMLHandler handler = new XMLHandler();
    	                reader.setContentHandler(handler);
    	                reader.parse(new InputSource(url.openStream()));
    	                
    	                ParsedDataset parsedDataset = handler.getParsedData();
    	            	if(parsedDataset.error){
    	            		Looper.prepare();
    	            		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	                	    public void onClick(DialogInterface dialog, int which) {
    	                	        switch (which){
    	                	        case DialogInterface.BUTTON_POSITIVE:
    	                	            //Yes button clicked
    	                	        	startActivity(new Intent(getBaseContext(),RegistrationActivity.class));
    	                	        	finish();
    	                	            break;
    	                	        case DialogInterface.BUTTON_NEGATIVE:
    	                	            //No button clicked
    	                	        	android.os.Process.killProcess(android.os.Process.myPid());
    	                	            break;
    	                	        }
    	                	    }
    	                	};

    	                	AlertDialog.Builder builder = new AlertDialog.Builder(NetLobbyActivity.this);
    	                	builder.setMessage(context.getString(R.string.loginFailed)).setPositiveButton(context.getString(R.string.register), dialogClickListener).setNegativeButton(context.getString(R.string.cancel), dialogClickListener).setCancelable(false).show();
    	                	Looper.loop();
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
}