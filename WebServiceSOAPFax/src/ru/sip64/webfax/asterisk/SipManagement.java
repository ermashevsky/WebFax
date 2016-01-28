package ru.sip64.webfax.asterisk;


import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.action.CommandAction;
import org.asteriskjava.manager.response.CommandResponse;
import org.jboss.logging.Logger;


public class SipManagement extends AsteriskOpenConnect{
	public SipManagement(){
		log.info("Connect to Asterisk...");
	}
	
	private ManagerConnection mc = getManagerConnectionFactory().createManagerConnection();
	
	//Log
	private static final Logger log = Logger.getLogger(SipManagement.class.getName());
	
	/**
	 * Execute command in CLI Asterisk
	 * @param command - command send on Asterisk
	 * @return CommandResponse 
	 * @throws Exception
	 */
	public CommandResponse sendCommandOnAsterisk(String command) throws Exception{
				log.info("Send on Asterisk: "  + command);
				CommandAction commandAction = new CommandAction(command);
				loginAsterisk(mc);
				CommandResponse response = (CommandResponse) mc.sendAction(commandAction);
				logoffAsterisk(mc);
				return response;
	}
	//--------------------------------------------------------------
	
	
}



