package com.sam.hex.net;

import java.util.ArrayList;

import com.sam.hex.R;
 
public class ParsedDataset {
	//Logging in
    private int uid = 0;
    private String name = null;
    private String session_id = null;
    
    //Current games
    public ArrayList<GameSession> sessions = new ArrayList<GameSession>();
    public class GameSession{
    	public ArrayList<Member> members = new ArrayList<Member>();
    	public String state = null;
    	public int sid = 0;
    	public int uid = 0;
    	public GameSession(String state, int sid, int uid){
        	this.state = state;
        	this.sid = sid;
        	this.uid = uid;
    	}
    	public String toString(){
    		if(state.equals("INIT")){
    			return NetLobbyActivity.context.getString(R.string.waiting);
    		}
    		else{
    			return NetLobbyActivity.context.getString(R.string.active);
    		}
    	}
    }
    public class Member{
    	public int position = 9;
    	public int uid = 0;
    	public String name = null;
    	public String state = null;
    	public Member(int position, int uid, String name, String state){
    		this.position = position;
        	this.uid = uid;
        	this.name = name;
        	this.state = state;
    	}
    	public String toString(){
    		if(position==1){
    			return "Player 1: "+name;
    		}
    		else if(position==2){
    			return "Player 2: "+name;
    		}
    		return "Watching: "+name;
    	}
    }
    public void addSession(String state, int sid, int uid){
    	sessions.add(new GameSession(state, sid, uid));
    }
    public void addSessionMember(int position, int uid, String name, String state){
    	sessions.get(sessions.size()-1).members.add(new Member(position, uid, name, state));
    }
    
    //Playing a game
    private int sid;
    private String server = null;
    
    //Errors
    public boolean error = false;
    private String errorMessage = null;
 
    public int getUid() {
    	return uid;
    }
    
    public void setUid(int uid) {
    	this.uid = uid;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
}