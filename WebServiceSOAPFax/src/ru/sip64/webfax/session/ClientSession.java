package ru.sip64.webfax.session;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;

import org.asteriskjava.manager.response.CommandResponse;
import org.jboss.logging.Logger;

import ru.sip64.webfax.asterisk.AsteriskOpenConnect;
import ru.sip64.webfax.asterisk.SendFax;
import ru.sip64.webfax.asterisk.SipManagement;
import ru.sip64.webfax.bean.AccessForDBPinCodeBeanLocal;
import ru.sip64.webfax.utils.Constants;
import ru.sip64.webfax.utils.ReadConfigFile;
import ru.sip64.webfax.utils.SipConfig;



//New client session
public class ClientSession {
	
	//For convert number phone to username
	private static Map<String, String > numberPhomeToUsername = new HashMap<String, String >();
	//Log
	private static final Logger log = Logger.getLogger(ClientSession.class.getName());
	
	//Connect to database
	@EJB
	private AccessForDBPinCodeBeanLocal pinCodeDB = null; 
	
	//Client name
	private String username;
	
	//Pin code
	private String pinCode;
	
	//Pin Password
	private String pinPassword;
	
	//Status Session
	private boolean openstatus = false;
	
	//Registry on Asterisk
	private boolean registry = false;
	
	//Sip Server
	private String sipServer = ReadConfigFile.getCfgReadFile().getProperty("sip_server");
	
	// File Sip Config
	private SipConfig sc = null;
	
	private SendFax sf = null;
	
	private TimeoutSession timeout = null;
	/**
	 * This constructor create new client session
	 * @param username - name of client
	 * @throws Exception 
	 */
	public  ClientSession(String username) throws Exception{
		InitialContext ctx = new InitialContext();
		pinCodeDB = (AccessForDBPinCodeBeanLocal) ctx.lookup("java:app/WebServiceSOAPFax/AccessForDBPinCodeBean");
		log.info(username + " try open session...");
		this.username = username;
		this.openstatus = true;
		this.timeout = new TimeoutSession(username);
		this.pinCode = getPinCodeFromDatabase(username);
		numberPhomeToUsername.put(pinCode, username);
		this.pinPassword = getPinPasswordFromDatabase(username);
		this.registry = registryOnAsterisk();
		log.info(username + " open session.");
	}
	
	/**
	 * Check registry user or not
	 * @param username - name of client
	 * @return false if user not registry and true - registry
	 * @throws Exception 
	 */
	private boolean registryOnAsterisk() throws Exception{
		sc = new SipConfig(username);
		sc.createSipConfig(username, pinCode, pinPassword, sipServer);
		AsteriskOpenConnect sm = new SipManagement();
		((SipManagement)sm).sendCommandOnAsterisk("sip reload");
		int reg = 0;
		int count = 0;
			while((reg = auth((SipManagement) sm)) == 0){
					if(count > 4){
						log.info(username + " not registry for timeout.");
						sc.deleteSipConfig(username);
						((SipManagement)sm).sendCommandOnAsterisk("sip reload");
						return false;
					}
					count++;
			}
		if(reg > 0) return true;
		return false;
	}
	
	//For result - Registry or Not
	private int auth(SipManagement sm) throws Exception{
		CommandResponse cr = sm.sendCommandOnAsterisk("sip show registry");
		for (String line: cr.getResult()){
			if (line.contains(pinCode) && line.contains(Constants.REGISTRY)){
				log.info(username + " registry.");
				return 1;
			}
			if (line.contains(pinCode) && line.contains(Constants.NOT_AUTH)){
				log.info(username + " not registry.");
				sc.deleteSipConfig(username);
				sm.sendCommandOnAsterisk("sip reload");
				return -1;
			}
		}
			Thread.sleep(500);
			return 0;
		
		
	}
	
	private String getPinCodeFromDatabase(String username){
		return pinCodeDB.getPinCode(username);
	}
	
	private String getPinPasswordFromDatabase(String username){
		return pinCodeDB.getPinCodePassword(username);
	}
	
	/**
	 * Close session for client
	 * @return Status close session
	 * @throws Exception 
	 */
	public String closeClientSession() throws Exception{
		log.info(username + " try close session ...");
		this.openstatus = false;
		this.registry = false;
		if (!sc.deleteSipConfig(username)){
			log.error(username + " ERROR close session when delete config file");
			return Constants.FAIL;
		}
		AsteriskOpenConnect sm = new SipManagement();
		((SipManagement)sm).sendCommandOnAsterisk("sip reload");
		log.info(username + " close session");
		timeout.stop();
		this.sc = null;
		return Constants.OK;
		
	}

	/**
	 * Sip Config File
	 * @return link to SIP Config File
	 */
	public SipConfig getSipConfig() {
		return sc;
	}
	
	/**
	 * 
	 * @return User connected or not
	 */
	public boolean isOpenstatus() {
		return openstatus;
	}
	
	/**
	 * 
	 * @return User registry on Asterisk or not 
	 */
	public boolean isRegistry() {
		return registry;
	}

	public SendFax getSf() {
		return sf;
	}

	public void setSf(SendFax sf) {
		this.sf = sf;
	}

	/**
	 * @return the numberPhomeToUsername
	 */
	public static String getNumberPhomeToUsername(String number) {
		return numberPhomeToUsername.get(number);
	}

	public TimeoutSession getTimeout() {
		return timeout;
	}

	
}
