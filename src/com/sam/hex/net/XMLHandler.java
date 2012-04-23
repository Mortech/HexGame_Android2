package com.sam.hex.net;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
     
public class XMLHandler extends DefaultHandler{
	private boolean in_errorMessage = false;
	private boolean in_loginResult = false;
    private boolean in_uid = false;
    private boolean in_name = false;
    private boolean in_session_id = false;
    private boolean in_sessionList = false;
    private boolean in_session = false;
    private boolean in_sessionInfo = false;
    private boolean in_sid = false;
    private boolean in_server = false;
       
    private ParsedDataset parsedDataset = new ParsedDataset();
 
    public ParsedDataset getParsedData() {
    	return this.parsedDataset;
    }
    
    @Override
    public void startDocument() throws SAXException {
    	this.parsedDataset = new ParsedDataset();
    }
 
    @Override
    public void endDocument() throws SAXException {
    	// Nothing to do
    }
 
    /** Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml is like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    	if (localName.equals("errorMessage")) {
    		this.in_errorMessage = true;
    		parsedDataset.error = true;
        }
    	else if (localName.equals("loginResult")) {
    		this.in_loginResult = true;
        }
    	else if (localName.equals("uid")) {
    		this.in_uid = true;
        }
    	else if (localName.equals("name")) {
        	this.in_name = true;
        }
    	else if (localName.equals("session_id")){
    		this.in_session_id = true;
    	}
    	else if (localName.equals("sessionList")){
    		this.in_sessionList = true;
    	}
    	else if(in_sessionList){
	    	if (localName.equals("session")){
	    		if(atts.getValue("stat").equals("ACTIVE") || atts.getValue("stat").equals("INIT")){
	    			parsedDataset.addSession(atts.getValue("stat"), Integer.parseInt(atts.getValue("sid")), Integer.parseInt(atts.getValue("uid")), atts.getValue("serv"));
		    		this.in_session = true;
	    		}
	    	}
	    	if(in_session){
		    	if (localName.equals("member")){
		    		parsedDataset.addSessionMember(Integer.parseInt(atts.getValue("plc")), Integer.parseInt(atts.getValue("uid")), atts.getValue("nam"), atts.getValue("stat"));
		    	}
	    	}
    	}
    	else if (localName.equals("sessionInfo")){
    		this.in_sessionInfo = true;
    	}
    	else if(in_sessionInfo){
    		if (localName.equals("sid")){
    			this.in_sid = true;
    		}
    		else if (localName.equals("server")){
    			this.in_server = true;
    		}
    	}
    }
       
    /** Gets be called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	if (localName.equals("errorMessage")) {
    		this.in_errorMessage = false;
        }
    	else if (localName.equals("loginResult")) {
    		this.in_loginResult = false;
        }
    	else if (localName.equals("uid")) {
    		this.in_uid = false;
        }
    	else if (localName.equals("name")) {
        	this.in_name = false;
        }
    	else if (localName.equals("session_id")){
    		this.in_session_id = false;
    	}
    	else if (localName.equals("sessionList")){
    		this.in_sessionList = false;
    	}
    	else if (localName.equals("session")){
    		this.in_session = false;
    	}
    	else if (localName.equals("sessionInfo")){
    		this.in_sessionInfo = false;
    	}
    	else if (localName.equals("sid")){
			this.in_sid = false;
		}
		else if (localName.equals("server")){
			this.in_server = false;
		}
    }
       
    /** Gets be called on the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
    	if(this.in_loginResult){
	    	if (this.in_uid) {
	    		parsedDataset.setUid(Integer.parseInt(new String(ch, start, length)));
	        }
	    	else if (this.in_name) {
	    		parsedDataset.setName(new String(ch, start, length));
	        }
	    	else if (this.in_session_id){
	    		parsedDataset.setSession_id(new String(ch, start, length));
	    	}
    	}
    	else if(this.in_sessionInfo){
    		if (this.in_sid) {
    			parsedDataset.setSid(Integer.parseInt(new String(ch, start, length)));
    		}
    		else if (this.in_server) {
    			parsedDataset.setServer(new String(ch, start, length));
    		}
    	}
    	else if (this.in_errorMessage){
    		parsedDataset.setErrorMessage(new String(ch, start, length));
    	}
    }
}