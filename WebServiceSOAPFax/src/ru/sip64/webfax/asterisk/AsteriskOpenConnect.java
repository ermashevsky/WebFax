package ru.sip64.webfax.asterisk;

import java.io.IOException;


import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.jboss.logging.Logger;

import ru.sip64.webfax.utils.ReadConfigFile;

public abstract class AsteriskOpenConnect {
	
	private static final Logger log = Logger.getLogger(AsteriskOpenConnect.class.getName());
	
	private static ManagerConnectionFactory managerConnectionFactory = createConnect();
	
	private  static ManagerConnectionFactory createConnect(){
    	return new ManagerConnectionFactory(
				ReadConfigFile.getCfgReadFile().getProperty("bind_address"), 
				ReadConfigFile.getCfgReadFile().getProperty("username"), 
				ReadConfigFile.getCfgReadFile().getProperty("password"));

        
	}
	
	public void loginAsterisk(ManagerConnection managerConnection ){
		
		try {
			managerConnection.login();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationFailedException e) {
			log.error("Authentication Failed For Asterisk.");
			e.printStackTrace();
		} catch (TimeoutException e) {
			log.error("Timeout Connection Asterisk Exception.");
			e.printStackTrace();
		}
	}
	
	public void logoffAsterisk(ManagerConnection managerConnection ){
		managerConnection.logoff();
	}

	public static ManagerConnectionFactory getManagerConnectionFactory() {
		return managerConnectionFactory;
	}

}
