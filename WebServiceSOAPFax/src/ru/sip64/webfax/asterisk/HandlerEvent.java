package ru.sip64.webfax.asterisk;

import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.FaxStatusEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.NewCallerIdEvent;
import org.asteriskjava.manager.event.OriginateResponseEvent;
import org.asteriskjava.manager.event.SendFaxEvent;
import org.jboss.logging.Logger;

import ru.sip64.webfax.utils.Constants;

public class HandlerEvent {
	
	private ManagerEvent event = null;
	
	private static final Logger log = Logger.getLogger(AsteriskListenerEvent.class.getName());
	
	private boolean sendFaxEventOn = false;
	private boolean callSuccess = false;
	
	private String statusCall = Constants.NO_CALL;
	private String sipChannel = "";

	private void setStatusCall() {
		if(event == null){
			sendFaxEventOn = false;
			callSuccess = false;
		}
		if (event instanceof NewCallerIdEvent){
		NewCallerIdEvent newCallerIdEvent = (NewCallerIdEvent)event;
		sipChannel = newCallerIdEvent.getChannel(); 
		sendFaxEventOn = false;
		callSuccess = false;
		log.info("From " + newCallerIdEvent.getChannel()+ " to " + newCallerIdEvent.getCallerIdNum() + ": IDLE!");
		statusCall = Constants.IDLE;
		
	}
		
	if(event instanceof FaxStatusEvent){
		FaxStatusEvent faxStatusEvent = (FaxStatusEvent)event;
		log.info("From " + faxStatusEvent.getChannel()+ " to " + faxStatusEvent.getCallDuration() + ": FAX SENDING!");
		statusCall = Constants.FAX_SENDING; 
	}
	
	if (event instanceof SendFaxEvent){
		SendFaxEvent sendFaxEvent = (SendFaxEvent)event;
		sendFaxEventOn = true;
		log.info("From " + sendFaxEvent.getChannel()+ " to " + sendFaxEvent.getCallerId() + ": FAX SEND!"); 
	}
	if (event instanceof HangupEvent){
		HangupEvent hangupEvent = (HangupEvent)event;
		if (callSuccess && sendFaxEventOn){
			log.info("From " + hangupEvent.getChannel()+ " to " + hangupEvent.getCallerIdNum() + ": FAX SEND!");
			statusCall = Constants.FAX_OK; 
		}else{
			statusCall = Constants.FAX_NOT_SEND;
			log.info("From " + hangupEvent.getChannel()+ " to " + hangupEvent.getCallerIdNum() + ": FAX NOT SEND!");
		}
	}
	if (event instanceof OriginateResponseEvent){
		OriginateResponseEvent originateResponseEvent = (OriginateResponseEvent)event;
			 if (originateResponseEvent.getResponse().equals("Failure")){
				 if (originateResponseEvent.getReason() != 5){
					 log.info("From " + originateResponseEvent.getChannel()+ " to " + originateResponseEvent.getCallerIdNum()  + ": NOT ANSWER!");
					 statusCall = Constants.NOT_ANSWER;
				 }
				 else {
					 log.info("From " + originateResponseEvent.getChannel()+ " to " + originateResponseEvent.getCallerIdNum()  + ": BUSY!");
					 statusCall = Constants.BUSY;
				 }
			 }
			 if (originateResponseEvent.getResponse().equals("Success")){
				 log.info("From " + originateResponseEvent.getChannel()+ " to " + originateResponseEvent.getCallerIdNum()  + ": CALL SUCCESS!");
				 statusCall = Constants.CALL_SUCCESS;
				 callSuccess = true;
			 }
		
	}
	}

	public void setEvent(ManagerEvent event) {
		this.event = event;
		setStatusCall();
	}

	public String getStatusCall() {
		return statusCall;
	}

	public String getSipChannel() {
		return sipChannel;
	}
}
