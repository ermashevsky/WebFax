package ru.sip64.webfax.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.logging.Logger;


//Create and delete sip configured file
public final class SipConfig {
	
	private static String filePath =  ReadConfigFile.getCfgReadFile().getProperty("sip_config") + File.separator;
	
	private File fileConfig = null;
	
	/**
	 * Constructor, create link to sip config file
	 * @param filePath - path to config sip file
	 */
	public SipConfig(String username){
		fileConfig = new File(filePath + username + ".conf");
	}
	
	//Log
	private static final Logger log = Logger.getLogger(SipConfig.class.getName());
	
	/**
	 * Creates a file sip registration and writes a line like:
	 * register => " + pinCode + ":" + pinCodePassword + "@" + sipServer + "/" + pinCode;
	 * @param username - name client
	 * @param pinCode - pin code
	 * @param pinCodePassword - pin password
	 * @return true if file not exist and create without problem, otherwise false
	 */
	public boolean createSipConfig(String username, String pinCode, String pinCodePassword, String sipServer){
		FileWriter fstream = null;
		 log.info("Create config file " + fileConfig.getPath());
		 
		 if(!fileConfig.exists()){
			 
			String registry = "register => " + pinCode + ":" + pinCodePassword + "@" + sipServer + "/" + pinCode;
	     	try {
	     		fileConfig.createNewFile();
				fstream = new FileWriter(fileConfig);
				fstream.write(registry);
				
			} catch (IOException e) {
				e.printStackTrace();
				log.error("File " + fileConfig.getPath() + " not be create!");
				return false;
			}finally{
				try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	     	return true;
	     	
		 }
		 log.error("Error:" + fileConfig.getParent() +  "already exist!");
		 return false;
	}
	
	/**
	 * Delete sip config file
	 * @param username - name client
	 * @return true if file remove without problem, otherwise false
	 */
	public boolean deleteSipConfig(String username){
		if (!fileConfig.exists()){
			log.error(fileConfig.getPath() + " file not exist!");
			return false;
		}
		
	        if (WorkWithConsole.commandExec("rm -I " + filePath + username + ".conf").getStatus() != 0)
	        {
	        	log.error(fileConfig.getPath() + " file not remove!");
	        	return false;
	        }
	        log.info(fileConfig.getPath() + " remove!");
			return true;
	}
}
