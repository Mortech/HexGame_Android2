package com.sam.hex.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sam.hex.HexGame;
import com.sam.hex.Preferences;
import com.sam.hex.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WaitingRoomActivity extends Activity {
	public static LinkedList<String> messages = new LinkedList<String>();
	private RefreshGamePlayerlist refreshPlayers;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitingroom);

        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	sendMessage((EditText) findViewById(R.id.sendMessage));
            }
        });
        
        EditText text = (EditText) findViewById(R.id.sendMessage);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE || event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
					sendMessage(v);
					return true;
				}
				return false;
			}
		});
    	
    	ListView lobby = (ListView) findViewById(R.id.players);
    	Button start = new Button(this);
    	start.setText(this.getString(R.string.start));
    	start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(),HexGame.class));
        		finish();
			}
		});
    	lobby.addFooterView(start);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	refreshPlayers = new RefreshGamePlayerlist(new Handler(), new Runnable(){
    		public void run(){
    			refreshPlayers();
    			refreshMessages();
    		}});
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	if(refreshPlayers!=null) refreshPlayers.stop();
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

        	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Light));
        	builder.setMessage(this.getString(R.string.confirmExit)).setPositiveButton(this.getString(R.string.yes), dialogClickListener).setNegativeButton(this.getString(R.string.no), dialogClickListener).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void sendMessage(TextView v){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	final String username = prefs.getString("netUsername", "");
    	final String message = v.getText().toString();
    	if(!message.equals("")){
			messages.add(username+": "+message);
        	v.setText("");
        	refreshMessages();
		}
    	
    	new Thread(new Runnable(){
    		public void run(){
    			try {
    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=MSG&message=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, URLEncoder.encode(message,"UTF-8"));
    				URL url = new URL(lobbyUrl);
    				SAXParserFactory spf = SAXParserFactory.newInstance();
    	            SAXParser parser = spf.newSAXParser();
    	            XMLReader reader = parser.getXMLReader();
    	            XMLHandler xmlHandler = new XMLHandler();
    	            reader.setContentHandler(xmlHandler);
    	            reader.parse(new InputSource(url.openStream()));
    	            
    	            ParsedDataset parsedDataset = xmlHandler.getParsedData();
    	        	if(!parsedDataset.error){
    	        	}
    	        	else{
    	        		System.out.println(parsedDataset.getErrorMessage());
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
    
    private void refreshMessages(){
    	TextView messageBoard = (TextView) findViewById(R.id.messages);
    	String msg = "";
    	for(int i=0;i<messages.size();i++){
    		msg+=messages.get(i)+"\n";
    	}
    	messageBoard.setText(msg);
    	final ScrollView sv = (ScrollView) findViewById(R.id.messageScroller);
    	sv.post(new Runnable() {            
    	    @Override
    	    public void run() {
    	        sv.fullScroll(View.FOCUS_DOWN);              
    	    }
    	});

    }
    
    private void refreshPlayers(){
    	ListView lobby = (ListView) findViewById(R.id.players);
    	ArrayAdapter<ParsedDataset.Member> adapter = new ArrayAdapter<ParsedDataset.Member>(this,R.layout.simple_list_item_1, R.id.text1, NetGlobal.members);
        lobby.setAdapter(adapter);
        
        lobby.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
	        	
			}
        });
    }
}