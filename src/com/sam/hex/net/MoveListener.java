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

public class MoveListener implements Runnable{
	private boolean listen = true;
	public MoveListener(){
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(listen){
			try {
				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s", URLEncoder.encode(NetGlobal.server,"UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid);
				URL url = new URL(lobbyUrl);
				SAXParserFactory spf = SAXParserFactory.newInstance();
	            SAXParser parser = spf.newSAXParser();
	            XMLReader reader = parser.getXMLReader();
	            XMLHandler xmlHandler = new XMLHandler();
	            reader.setContentHandler(xmlHandler);
	            reader.parse(new InputSource(url.openStream()));
	            
	            ParsedDataset parsedDataset = xmlHandler.getParsedData();
	        	if(!parsedDataset.error){
        			//TODO
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
		listen = false;
	}
}