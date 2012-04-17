package com.sam.hex.net;
 
public class ParsedDataset {
    private String uid = null;
    private String name = null;
    private String session_id = null;
    public boolean error = false;
    private String errorMessage = null;
 
    public String getUid() {
    	return uid;
    }
    
    public void setUid(String uid) {
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
}