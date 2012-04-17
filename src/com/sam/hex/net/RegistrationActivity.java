package com.sam.hex.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sam.hex.R;
import com.sam.hex.startup.StartUpActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class RegistrationActivity extends Activity {
	SharedPreferences settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        
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
        final String email = ((EditText) findViewById(R.id.email)).getText().toString();
        final Calendar cal = Calendar.getInstance();
        final int birthDay = ((DatePicker) findViewById(R.id.birthday)).getDayOfMonth();
        final int birthMonth = ((DatePicker) findViewById(R.id.birthday)).getMonth();
        final int birthYear = ((DatePicker) findViewById(R.id.birthday)).getYear();
        final String about = ((EditText) findViewById(R.id.about)).getText().toString();
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new Thread(new Runnable(){
					@Override
					public void run() {
						try {
		            		String registrationUrl = String.format("http://www.iggamecenter.com/api_user_add.php?app_id=%s&app_code=%s&name=%s&password=%s", NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), URLEncoder.encode(username,"UTF-8"), URLEncoder.encode(password,"UTF-8"));
		            		if(!email.equals("")) registrationUrl += "&email="+URLEncoder.encode(email,"UTF-8");
		            		if(birthYear!=cal.get(Calendar.YEAR) && birthMonth!=cal.get(Calendar.MONTH) && birthDay!=cal.get(Calendar.DAY_OF_MONTH)) registrationUrl += "&birthDay="+birthDay+"&birthMonth="+birthMonth+"&birthYear="+birthYear;
		            		if(!about.equals("")) registrationUrl += "&about="+URLEncoder.encode(about,"UTF-8");
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
			            	else if(parsedDataset.getErrorMessage().equals("DUPLICATE_NICK")){
			            		
			            	}
			            	else if(parsedDataset.getErrorMessage().equals("DUPLICATE_EMAIL")){
			            		
			            	}
			            	else if(parsedDataset.getErrorMessage().equals("NICK_TOO_LONG") || parsedDataset.getErrorMessage().equals("NICK_INVALID_SYMBOLS") || parsedDataset.getErrorMessage().equals("NICK_NO_ENG_LETTERS")){
			            		
			            	}
			            	else if(parsedDataset.getErrorMessage().equals("INVALID_EMAIL")){
			            		
			            	}
			            	else if(parsedDataset.getErrorMessage().equals("INVALID_BIRTH_DATE")){
			            		
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
    }
}