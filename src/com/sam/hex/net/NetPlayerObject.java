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

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

import android.graphics.Point;

public class NetPlayerObject implements PlayingEntity {
	private String name;
	private int color;
	private long timeLeft;
	private int team;
	private MoveListener listener;
	
	public NetPlayerObject(int i) {
		this.team=i;//Set the player's team
		this.listener = new MoveListener();
	}
	
	public void getPlayerTurn() {
		if(Global.moveNumber>1 && !(GameAction.getPlayer(team%2+1) instanceof NetPlayerObject)){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=MOVE&move=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, URLEncoder.encode(GameAction.pointToString(new Point(Global.moveList.getmove().getX(),Global.moveList.getmove().getY())),"UTF-8"));
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
		
		NetGlobal.hex = null;
		looper: while (true) {
			Point hex = NetGlobal.hex;
			while (hex == null) {
				hex = NetGlobal.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Global.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				NetGlobal.hex = null;
				break;
			}
			else if (GameAction.makeMove(this, (byte) team, hex)) {
				NetGlobal.hex = null;
				break;
			}
			NetGlobal.hex = null;
		}
	}
	
	public void undoCalled(){
	}

	public void newgameCalled() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public boolean supportsUndo() {
		//TODO Ask to undo
		return false;
	}

	@Override
	public boolean supportsNewgame() {
		//TODO Ask for a new game
		return false;
	}

	@Override
	public void quit() {
		listener.stop();
	}

	@Override
	public void win() {
	}

	@Override
	public void lose() {
		if(Global.moveNumber>1 && !(GameAction.getPlayer(team%2+1) instanceof NetPlayerObject)){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=MOVE&move=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, URLEncoder.encode(GameAction.pointToString(new Point(Global.moveList.getmove().getX(),Global.moveList.getmove().getY())),"UTF-8"));
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
	}

	@Override
	public boolean supportsSave() {
		return false;
	}

	@Override
	public void endMove() {
		NetGlobal.hex = new Point(-1,-1);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setTime(long time) {
		this.timeLeft = time;
	}

	@Override
	public long getTime() {
		return timeLeft;
	}
}