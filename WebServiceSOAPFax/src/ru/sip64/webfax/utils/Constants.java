package ru.sip64.webfax.utils;

public final class Constants {
	
	private Constants(){}
	
	//Open Session-------------------------------------------------------------------
	public static final String BAD_USERNAME = "BAD USERNAME";
	public static final String OK = "OK";
	public static final String FAIL = "FAIL";
	public static final String NOT_REG = "NOT_REG";
	
	//Registration on Asterisk-------------------------------------------------------
	public static final String REGISTRY = "Registered";
	public static final String NOT_AUTH = "No Authentication";
	
	//Create Channel-----------------------------------------------------------------
	public static final String CHANNEL = "SIP/";
	public static final String CODEC = "g729";
	public static final String CONTEXT = "sendfax";
	public static final String EXTEN = "fax";
	//-------------------------------------------------------------------------------
	
	//Send fax ----------------------------------------------------------------------
	public static final String NO_CALL = "NO_CALL";
	public static final String IDLE = "IDLE";
	public static final String FAX_SENDING = "FAX_SENDING";
	public static final String FAX_OK = "FAX_OK";
	public static final String FAX_NOT_SEND = "FAX_NOT_SEND";
	public static final String NOT_ANSWER = "NOT_ANSWER";
	public static final String BUSY = "BUSY";
	public static final String CALL_SUCCESS = "CALL_SUCCESS";
	
	
	//Convert file to TiFF--------------------------------------------------------
	public static final String CONVERT_OK = "CONVERT_OK";
	public static final String CONVERT_FAIL = "CONVERT_FAIL";
	public static final String MANY_PAGES = "MANY_PAGES";
	public static final int COUNT_PAGES = 3;
	
	
	//Delete file from user`s directory------------------------------------------- 
	public static final String DELETE_SUCCESS = "DELETE_SUCCESS";
	public static final String DELETE_FAIL = "DELETE_FAIL";
	
	//Timeout Session in minutes--------------------------------------------------
	public static final Integer TIMEOUT_SESSION = 15;
	public static final String TIMEOUT = "TIMEOUT";
	
	
	
	
	
	
	
}
