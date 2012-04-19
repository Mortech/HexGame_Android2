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

import com.sam.hex.HexGame;
import com.sam.hex.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class CreateBoardActivity extends Activity {
	SharedPreferences settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netlobby_createboard);
        
        Button create = (Button) findViewById(R.id.create);
        final Spinner gameSize = (Spinner) findViewById(R.id.gameSize);
        final Spinner position = (Spinner) findViewById(R.id.position);
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new Thread(new Runnable(){
					@Override
					public void run() {
						NetGlobal.gridSize = Integer.parseInt(getResources().getStringArray(R.array.netGameSizeValues)[gameSize.getSelectedItemPosition()]);
						NetGlobal.place = Integer.parseInt(getResources().getStringArray(R.array.netPositionValues)[position.getSelectedItemPosition()]);
						System.out.println(NetGlobal.gridSize);
						System.out.println(NetGlobal.place);
						try {
	                		String registrationUrl = String.format("http://www.iggamecenter.com/api_board_create.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&gid=%s&place=%s", NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.gid, NetGlobal.place);
	    					URL url = new URL(registrationUrl);
	    					SAXParserFactory spf = SAXParserFactory.newInstance();
	    	                SAXParser parser = spf.newSAXParser();
	    	                XMLReader reader = parser.getXMLReader();
	    	                XMLHandler xmlHandler = new XMLHandler();
	    	                reader.setContentHandler(xmlHandler);
	    	                reader.parse(new InputSource(url.openStream()));
	    	                
	    	                ParsedDataset parsedDataset = xmlHandler.getParsedData();
	    	                if(!parsedDataset.error){
	    	                	NetGlobal.sid = parsedDataset.getSid();
	    	                	NetGlobal.server = parsedDataset.getServer();
	    	                	startActivity(new Intent(getBaseContext(),HexGame.class));
	    	                	finish();
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
        
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
    }
}