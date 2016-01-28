package ru.sip64.webfax.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jboss.logging.Logger;

public class Trunk {
	
	private static final Logger log = Logger.getLogger(Trunk.class.getName());
	
	private static String sipServer = ReadConfigFile.getCfgReadFile().getProperty("sip_server");
	
	private static  StringBuffer trunkConfig(String username, String password){
		StringBuffer str = new StringBuffer();
		str.append("[" + username + "]");
		str.append('\n');
		str.append("disallow=all");
		str.append('\n');
		str.append("defaultuser=" + username);
		str.append('\n');
		str.append("type=peer");
		str.append('\n');
		str.append("secret=" + password);
		str.append('\n');
		str.append("realm=" + sipServer);
		str.append('\n');
		str.append("language=ru");
		str.append('\n');
		str.append("insecure=invite");
		str.append('\n');
		str.append("host=" + sipServer);
		str.append('\n');
		str.append("fromuser=" + username);
		str.append('\n');
		str.append("fromdomain=" + sipServer);
		str.append('\n');
		str.append("allow=alaw");
		str.append('\n');
		str.append("call-limit=1");
		str.append('\n');
		str.append("faxdetect=no");
		return str;	
	}
	public static void createNewTrunk(String username, String password) {
			String filePath =  ReadConfigFile.getCfgReadFile().getProperty("trunk_config") + File.separator + username + ".conf";
			 File f=new File(filePath);
			 if(!f.exists()){
		     	try {
					f.createNewFile();
					FileWriter fstream = new FileWriter(f);
			     	BufferedWriter outobj = new BufferedWriter(fstream);
			        outobj.write((trunkConfig(username, password)).toString());
			        outobj.close();
			        log.info("INFO:" + filePath + "CREATE!");
				} catch (IOException e) {
					e.printStackTrace();
				}
		     	return;
		     	
			 }
			 log.info("INFO:" + filePath + "ALREADY EXIST!");
		}
	
	public static void deleteTrunk(String username){
		String filePath = ReadConfigFile.getCfgReadFile().getProperty("trunk_config") + File.separator + username + ".conf";
		String cmd = "rm -f " + filePath;
		int result = WorkWithConsole.commandExec(cmd).getStatus();
		if(result == 0) log.info("File was removed " + filePath);
		else log.info("File NOT was removed " + filePath);
	}
}
