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
import android.os.Handler;

public class NetPlayerObject implements PlayingEntity {
	private String name;
	private int color;
	private long timeLeft;
	private int team;
	private MoveListener listener;
	private Point hex;
	
	public NetPlayerObject(int team, Handler handler, Runnable newgame) {
		this.team=team;//Set the player's team
		this.listener = new MoveListener(handler, newgame);
	}

	@Override
	public void getPlayerTurn() {
		if(Global.game.moveNumber>1 && !(GameAction.getPlayer(team%2+1) instanceof NetPlayerObject)){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=MOVE&move=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, URLEncoder.encode(GameAction.pointToString(new Point(Global.game.moveList.getmove().getX(),Global.game.moveList.getmove().getY())),"UTF-8"));
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
		
		hex = null;
		while (true) {
			while (hex == null) {
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (hex.equals(new Point(-1,-1))){
				hex = null;
				break;
			}
			else if (GameAction.makeMove(this, (byte) team, hex)) {
				hex = null;
				break;
			}
			hex = null;
		}
	}

	@Override
	public void undoCalled(){
	}

	@Override
	public void newgameCalled() {
		hex = new Point(-1,-1);
	}

	@Override
	public boolean supportsUndo() {
		new Thread(new Runnable(){
    		public void run(){
    			try {
    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=UNDO&type=ASK&move_ind=", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, (Global.game.moveNumber-1));
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
		return false;
	}

	@Override
	public boolean supportsNewgame() {
		if(!Global.game.gameOver){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=END&type=GIVEUP", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid);
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
		new Thread(new Runnable(){
    		public void run(){
    			try {
    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=RESTART", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid);
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
		return true;
	}

	@Override
	public void quit() {
		if(!Global.game.gameOver){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=END&type=GIVEUP", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid);
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
		listener.stop();
	}

	@Override
	public void win() {
	}

	@Override
	public void lose() {
		if(Global.game.moveNumber>1 && !(GameAction.getPlayer(team%2+1) instanceof NetPlayerObject)){
			new Thread(new Runnable(){
	    		public void run(){
	    			try {
	    				String lobbyUrl = String.format("http://%s.iggamecenter.com/api_handler.php?app_id=%s&app_code=%s&uid=%s&session_id=%s&sid=%s&cmd=MOVE&move=%s", URLEncoder.encode(NetGlobal.server, "UTF-8"), NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), NetGlobal.uid, URLEncoder.encode(NetGlobal.session_id,"UTF-8"), NetGlobal.sid, URLEncoder.encode(GameAction.pointToString(new Point(Global.game.moveList.getmove().getX(),Global.game.moveList.getmove().getY())),"UTF-8"));
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
		hex = new Point(-1,-1);
	}

	@Override
	public void setName(String name) {
		this.name = name;
		NetGlobal.netPlayerName = name;
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

	@Override
	public void setMove(Point hex) {
		this.hex = hex;
	}
}