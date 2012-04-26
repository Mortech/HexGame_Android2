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

import android.content.DialogInterface;

import com.sam.hex.DialogBox;
import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.R;

public class MoveListener implements Runnable{
	private boolean listen = true;
	public MoveListener(){
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(listen){
			try {
				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&lasteid=%s", URLEncoder.encode(NetGlobal.server,"UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, NetGlobal.lasteid);
				URL url = new URL(lobbyUrl);
				SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser parser = spf.newSAXParser();
	            XMLReader reader = parser.getXMLReader();
	            XMLHandler xmlHandler = new XMLHandler();
	            reader.setContentHandler(xmlHandler);
	            reader.parse(new InputSource(url.openStream()));
	            
	            ParsedDataset parsedDataset = xmlHandler.getParsedData();
	        	if(!parsedDataset.error){
        			NetGlobal.hex = parsedDataset.getMove();
        			if(parsedDataset.undoRequested){
        				new DialogBox(Global.board.getContext(), 
    	    					GameAction.InsertName.insert(Global.board.getContext().getString(R.string.LANUndo), NetGlobal.netPlayerName), 
    	    					new DialogInterface.OnClickListener() {
    	    	    	    	    public void onClick(DialogInterface dialog, int which) {
    	    	    	    	        switch (which){
    	    	    	    	        case DialogInterface.BUTTON_POSITIVE:
    	    	    	    	            //Yes button clicked
    	    	    		    			NetGlobal.undoRequested = true;
    	    	    	    	        	GameAction.undo();
    	    	    	    	        	try {
    	    	    	    	        		String undoUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=UNDO&type=ACCEPT", URLEncoder.encode(NetGlobal.server,"UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, NetGlobal.lasteid);
												new URL(undoUrl).openStream();
											} catch (MalformedURLException e) {
												e.printStackTrace();
											} catch (IOException e) {
												e.printStackTrace();
											}
    	    	    	    	            break;
    	    	    	    	        case DialogInterface.BUTTON_NEGATIVE:
    	    	    	    	            //No button clicked
    	    	    	    	        	try {
    	    	    	    	        		String undoUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=UNDO&type=DENY", URLEncoder.encode(NetGlobal.server,"UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, NetGlobal.lasteid);
												new URL(undoUrl).openStream();
											} catch (MalformedURLException e) {
												e.printStackTrace();
											} catch (IOException e) {
												e.printStackTrace();
											}
    	    	    	    	            break;
    	    	    	    	        }
    	    	    	    	    }
    	    	    	    	}, 
    	    					Global.board.getContext().getString(R.string.yes), 
    	    					Global.board.getContext().getString(R.string.no));
        			}
    				if(parsedDataset.undoAccepted){
    					GameAction.undo();
	    				new DialogBox(Global.board.getContext(), 
		    					Global.board.getContext().getString(R.string.LANundoAccepted), 
		    					null, 
		    					Global.board.getContext().getString(R.string.okay));
    				}
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
			
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop(){
		listen = false;
	}
}