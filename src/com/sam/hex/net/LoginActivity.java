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
import com.sam.hex.R;
import com.sam.hex.startup.StartUpActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	SharedPreferences settings;
	Context context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = getApplicationContext();
        
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),StartUpActivity.class));
            	finish();
            	StartUpActivity.startup.finish();
            }
        });
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button enter = (Button) findViewById(R.id.loginEnter);
        final String username = ((EditText) findViewById(R.id.username)).getText().toString();
        final String password = ((EditText) findViewById(R.id.password)).getText().toString();
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new Thread(new Runnable(){
					@Override
					public void run() {
						try {
		            		String registrationUrl = String.format("http://www.iggamecenter.com/api_login.php?app_id=%s&app_code=%s&login=%s&password=%s", NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), URLEncoder.encode(username,"UTF-8"), URLEncoder.encode(password,"UTF-8"));
							URL url = new URL(registrationUrl);
							SAXParserFactory spf = SAXParserFactory.newInstance();
			                SAXParser parser = spf.newSAXParser();
			                XMLReader reader = parser.getXMLReader();
			                XMLHandler handler = new XMLHandler();
			                reader.setContentHandler(handler);
			                reader.parse(new InputSource(url.openStream()));
			                
			                ParsedDataset parsedDataset = handler.getParsedData();
			            	if(!parsedDataset.error){
				            	settings.edit().putString("netUsername", username).commit();
				            	settings.edit().putString("netPassword", password).commit();
				            	
				            	startActivity(new Intent(getBaseContext(),NetLobbyActivity.class));
				            	finish();
			            	}
			            	else{
			            		new DialogBox(LoginActivity.this, context.getString(R.string.loginFailed), new DialogInterface.OnClickListener() {
			                	    public void onClick(DialogInterface dialog, int which) {
			                	        switch (which){
			                	        case DialogInterface.BUTTON_POSITIVE:
			                	            //Yes button clicked
			                	        	startActivity(new Intent(getBaseContext(),RegistrationActivity.class));
			                	            break;
			                	        case DialogInterface.BUTTON_NEGATIVE:
			                	            //No button clicked
			                	            break;
			                	        }
			                	    }
			                	}, context.getString(R.string.register), context.getString(R.string.cancel));
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
        });
        
        Button register = (Button) findViewById(R.id.registerEnter);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),RegistrationActivity.class));
            }
        });
    }
}