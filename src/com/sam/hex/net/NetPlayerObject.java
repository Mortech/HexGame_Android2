package com.sam.hex.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

import android.graphics.Point;

public class NetPlayerObject implements PlayingEntity {
	
	byte[][] gameBoard; 
	byte team;
	
	public NetPlayerObject(byte i) {
		this.team=i;//Set the player's team
		
		//Register with igGC
		String registrationUrl;
		URL url;
		URLConnection connection;
		HttpURLConnection httpConnection;
		int responseCode;
		try {
			//http://www.iggamecenter.com/api_login.php?app_id=17&app_code=wihamo8984&login=Xlythe&password=crunch&networkuid=NotSet
			registrationUrl = String.format("http://www.iggamecenter.com/api_login.php?app_id=%s&app_code=%s&login=%s&password=%s&networkuid=%s", NetGlobal.id, URLEncoder.encode(NetGlobal.passcode,"UTF-8"), URLEncoder.encode(NetGlobal.username,"UTF-8"), URLEncoder.encode(NetGlobal.password,"UTF-8"), URLEncoder.encode(NetGlobal.uniqueID,"UTF-8"));
			url = new URL(registrationUrl);
			connection = url.openConnection();
			httpConnection = (HttpURLConnection) connection;
			responseCode = httpConnection.getResponseCode();
			
			if(responseCode == HttpURLConnection.HTTP_OK) {
	            //Registration success
				SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
	        }
	        else {
	            //Registration failed             
	        }
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	public void getPlayerTurn() {
		
        

		
		
		
		GameAction.hex = null;
		looper: while (true) {
			Point hex = GameAction.hex;
			while (hex == null) {
				hex = GameAction.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Global.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				GameAction.hex = null;
				break;
			}
			if (Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
				GameAction.makeMove(this, team, hex);
				GameAction.hex = null;
				break;
			}
			GameAction.hex = null;
		}
	}
	
	public void undoCalled(){
	}

	public void newgameCalled() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public boolean supportsUndo() {
		return true;
	}

	@Override
	public boolean supportsNewgame() {
		return true;
	}

	@Override
	public void colorChanged() {
	}

	@Override
	public void nameChanged() {
	}

	@Override
	public void quit() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public void win() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lose() {
		// TODO Auto-generated method stub
		
	}
}