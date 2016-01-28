package ru.sip64.webfax.asterisk;


import java.io.File;
import java.io.IOException;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.jboss.logging.Logger;

import ru.sip64.webfax.session.ClientSession;
import ru.sip64.webfax.utils.Constants;
import ru.sip64.webfax.utils.ReadConfigFile;
import ru.sip64.webfax.webserice.WebServiceFaxImp;



public class SendFax extends AsteriskOpenConnect {
	 private String numberPhone;
	 private String sendImage;
	 private String trunkSip;
	 
	 
	 private static final int SLEEP_TIME = 1; 
	 
	 private static final Logger log = Logger.getLogger(SendFax.class.getName());
	 
	//Handler event
	private HandlerEvent handlerEvent = new HandlerEvent();
	 
	 	
	 public SendFax(String numberPhone, String sendImage, String trunkSip) throws IOException
	    {
	    
	    	this.numberPhone = numberPhone;
	    	this.sendImage = ReadConfigFile.getCfgReadFile().getProperty("workdir") + File.separator +  sendImage;
	    	this.trunkSip = trunkSip;
	    }
	 
	 //Create sip channel
	 private OriginateAction createCallTrunk(){
	 		OriginateAction originateAction = new OriginateAction();
	 		originateAction.setChannel(Constants.CHANNEL + trunkSip + "/" + numberPhone);
	 		originateAction.setCodecs(Constants.CODEC);
	 		originateAction.setContext(Constants.CONTEXT);
	 		originateAction.setExten(Constants.EXTEN);
	 		originateAction.setPriority(new Integer(1));
	        originateAction.setAsync(true);
	        originateAction.setVariable("sendimage", sendImage);
	        originateAction.setCallerId(numberPhone);
	        return originateAction;
	 }
	 	
	 	//----------------------------------------------------------------------------
	    
	    
	    public String run()  
	    {
	    	log.info("Number Phone: " + numberPhone + "   Send Image: " + sendImage);
	    	ManagerConnection mc = getManagerConnectionFactory().createManagerConnection();
	        // connect to Asterisk and log in
				loginAsterisk(mc);
				
				 try {
					 ManagerResponse originateResponse = mc.sendAction(createCallTrunk(), 30000);
					 log.info(originateResponse.getResponse());
					 Thread.sleep(SLEEP_TIME*1000);
				} catch (Exception e) {
					log.error("Exception send action.");
					e.printStackTrace();
				}
				finally{
					logoffAsterisk(mc);
				}
	        return WebServiceFaxImp.getUsersMap().get(ClientSession.getNumberPhomeToUsername(trunkSip)).getSf().getHandlerEvent().getStatusCall();
	    }

		public HandlerEvent getHandlerEvent() {
			return handlerEvent;
		}
		
		//============================================================================
		public String getSendFaxStatus(){
			return handlerEvent.getStatusCall();
		}
	    
	    public String getSipChannel(){
	    	return handlerEvent.getSipChannel();
	    }
		
}

