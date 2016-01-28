package ru.sip64.webfax.webserice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.jboss.logging.Logger;


import ru.sip64.webfax.asterisk.SendFax;
import ru.sip64.webfax.asterisk.SipManagement;
import ru.sip64.webfax.bean.AccessForDBPinCodeBeanLocal;
import ru.sip64.webfax.session.ClientSession;
import ru.sip64.webfax.utils.Constants;
import ru.sip64.webfax.utils.ReadConfigFile;
import ru.sip64.webfax.utils.Trunk;
import ru.sip64.webfax.utils.WorkWithConsole;


@WebService(endpointInterface = "ru.sip64.webfax.webserice.WebServiceFax")
public class WebServiceFaxImp implements WebServiceFax{
	
	private static final Logger log = Logger.getLogger(WebServiceFaxImp.class.getName());
	
	//List users
	private static Map<String, ClientSession> usersMap = new HashMap<String, ClientSession>();
	
	private static String workDir = ReadConfigFile.getCfgReadFile().getProperty("workdir") + File.separator;
	private static String workScriptDir = ReadConfigFile.getCfgReadFile().getProperty("script_workdir") + File.separator;
	
	@EJB
	AccessForDBPinCodeBeanLocal pinCodeDB = null;
	//---------------------------------------------------------------------------------------------------------------------
							//WORK WITH FILE
	//---------------------------------------------------------------------------------------------------------------------
	@Override
	public String convertFile(String username, String filePath) {
		ClientSession cs = usersMap.get(username);
		if (cs == null) return Constants.TIMEOUT;
		cs.getTimeout().setCount(0);
		
		String command = workScriptDir + "faxconvert.sh " + workDir + filePath;
		log.info(filePath + " trying convert ...");
		WorkWithConsole wwc = WorkWithConsole.commandExec(command);
		if(wwc.getStatus() != 0){ 
			log.error(filePath + " convert fail.");
			log.error(wwc.getResponce());
			return Constants.CONVERT_FAIL;
		}
		if (new Integer(wwc.getResponce().trim()) > Constants.COUNT_PAGES){
			log.info(filePath + " very many pages.");
			return Constants.MANY_PAGES;
		}
		log.info(filePath + " convert success.");
		return Constants.CONVERT_OK;
	}
	
	@Override
	public String deleteFile(String username, String filePath) {
		ClientSession cs = usersMap.get(username);
		if (cs == null) return Constants.TIMEOUT;
		cs.getTimeout().setCount(0);
		
		String originalFilePath = filePath.substring(0, filePath.length() - 4);
		log.info(filePath + " trying delete ...");
		String command = "rm -f "  + workDir + originalFilePath + "*";
		if (  WorkWithConsole.commandExec(command).getStatus() == 0){
			log.info(filePath + " delete success.");
			return Constants.DELETE_SUCCESS;
		}
		log.info(filePath + " delete fail.");
		return Constants.DELETE_FAIL;
	}
	
	@Override
	public String moveFile(String username, String fileName) {
		ClientSession cs = usersMap.get(username);
		if (cs == null) return Constants.TIMEOUT;
		cs.getTimeout().setCount(0);
		
		String oldFilePath = workDir + username + File.separator + "history" + File.separator;
		String newFilePath = workDir + username + File.separator ;
		String originalFilePath = fileName.substring(0, fileName.length() - 4);
		log.info("mv " + oldFilePath + originalFilePath + ".*" + " " + newFilePath);
		WorkWithConsole.commandExec("rm -f " + workDir + username + File.separator + "*" + ";" +"mv " + oldFilePath + originalFilePath + ".*" + " " + newFilePath);
		log.info("Fax send from history " + fileName);
		return "OK";
	}
	//---------------------------------------------------------------------------------------------------------------------
								//OPEN AND CLOSE SESSION
	//---------------------------------------------------------------------------------------------------------------------
	/**
	 * Open session
	 */
	@Override
	public String openSession(String username) {
		if (username.trim().equals("administrator")){
			log.info(username + " open session .");
			return "OK";
		}
		
		if (usersMap.get(username) != null)
			return "FAIL";
		ClientSession cs = null;
		try {
			cs = new ClientSession(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!cs.isRegistry()){
			return Constants.NOT_REG;
		}
		usersMap.put(username, cs);
		return "OK";
	}
	/**
	 * Close session
	 */
	@Override
	public String closeSession(String username) {
		if (username.trim().equals("administrator")){
			log.info(username + " close session .");
			return "OK";
		}
		ClientSession cs = usersMap.remove(username);
		if (cs == null) return Constants.TIMEOUT;
		String result = Constants.FAIL;
		try {
			result = cs.closeClientSession();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	//---------------------------------------------------------------------------------------------------------------------
								//SEND FAX
	//---------------------------------------------------------------------------------------------------------------------
	@Override
	public String sendFax(String username, String destinationNumber, String fileName) {
		ClientSession cs = usersMap.get(username);
		if (cs == null) return Constants.TIMEOUT;
		cs.getTimeout().setCount(0);
		
		String pinCode = pinCodeDB.getPinCode(username);
		SendFax sf = null;
		try {
			sf = new SendFax(destinationNumber, fileName, pinCode);
			cs.setSf(sf);
			sf.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("SendFax-----------------------------");
		return sf.getSendFaxStatus();
	}
	
	@Override
	public String resultSendFax(String username, String destinationNumber, String fileName) {
		ClientSession cs = usersMap.get(username);
		if (cs == null) return Constants.TIMEOUT;
		cs.getTimeout().setCount(0);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String resultFax = cs.getSf().getSendFaxStatus();
		if (resultFax.equals(Constants.FAX_NOT_SEND)
				|| resultFax.equals(Constants.FAX_OK)) {
			if (fileName.indexOf("history/") == -1) {
				String originalFilePath = fileName.substring(0, fileName.length() - 4);
				WorkWithConsole.commandExec("mv " + workDir  + 
						originalFilePath + "*" + " "  + workDir + username + File.separator + "history" + File.separator);
			}
		}
		return resultFax;
	}
	
	@Override
	public void stopFax(String username){
		
		ClientSession cs = usersMap.get(username);
		if (cs == null) return ;
		cs.getTimeout().setCount(0);
		
		String channel = cs.getSf().getSipChannel();
		try {
			log.info("Fax: " + channel + " stop by user: " + username);
			new SipManagement().sendCommandOnAsterisk("channel request hangup " + channel);
		} catch (Exception e) {
			log.error("Fax: " + channel + "NOT stop by user: " + username);
			e.printStackTrace();
		}
		
	}
	//---------------------------------------------------------------------------------------------------------------------
							//WORK WITH USER
	//---------------------------------------------------------------------------------------------------------------------
	
	@Override
	public void createUser(String username, String pinCode, String pinPassword) {
		pinCodeDB.createNewUser(username, pinCode, pinPassword);
		Trunk.createNewTrunk(pinCode, pinPassword);
	}
	
	@Override
	public void updateUser(String username, String pinCode, String pinPassword) {
		if((pinCode.trim().length() > 0) && (pinPassword.trim().length() > 0)){
			pinCodeDB.updateUser(username, pinCode, pinPassword);
			Trunk.deleteTrunk(username);
			Trunk.createNewTrunk(pinCode, pinPassword);
		}
	}
	
	@Override
	public void deleteUser(String username) {
		pinCodeDB.deleteUser(username);
		Trunk.deleteTrunk(username);
	}
	
	//---------------------------------------------------------------------------------------------------------------------
	
	//List current users
	public static Map<String, ClientSession> getUsersMap() {
		return usersMap;
	}

}


