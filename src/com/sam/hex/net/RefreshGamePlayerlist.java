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

import android.os.Handler;

public class RefreshGamePlayerlist implements Runnable{
	private boolean refresh = true;
	Handler handler;
	Runnable updateResults;
	public RefreshGamePlayerlist(Handler handler, Runnable updateResults){
		this.handler = handler;
		this.updateResults = updateResults;
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(refresh){
			try {
				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&lasteid=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, NetGlobal.lasteid);
				URL url = new URL(lobbyUrl);
				SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser parser = spf.newSAXParser();
	            XMLReader reader = parser.getXMLReader();
	            XMLHandler xmlHandler = new XMLHandler();
	            reader.setContentHandler(xmlHandler);
	            reader.parse(new InputSource(url.openStream()));
	            
	            ParsedDataset parsedDataset = xmlHandler.getParsedData();
	        	if(!parsedDataset.error){
        			NetGlobal.members = parsedDataset.players;
        			for(int i=0;i<parsedDataset.messages.size();i++){
        				WaitingRoomActivity.messages.add(parsedDataset.messages.get(i).name+": "+parsedDataset.messages.get(i).msg);
        			}
	        		handler.post(updateResults);
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
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop(){
		refresh = false;
	}
}