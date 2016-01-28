package ru.sip64.webfax.session;

import ru.sip64.webfax.utils.Constants;
import ru.sip64.webfax.webserice.WebServiceFaxImp;

public class TimeoutSession implements Runnable {
	
	private static final int SLEEP_TIME = 100;
	private static final int IN_MILISECONDS = 1000;
	private static final int IN_SECONDS = 60;
	
	private Thread timeout;
	private int count = 0;
	private String username;

	public TimeoutSession(String username){
		this.username = username;
		timeout = new Thread(this);
		timeout.start();
	}

	@Override
	public void run() {
	
		Thread th = Thread.currentThread();
		while ((timeout == th) & (count <= (Constants.TIMEOUT_SESSION * IN_MILISECONDS * IN_SECONDS))){
			try {
				Thread.sleep(SLEEP_TIME);
				count = count + SLEEP_TIME;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		WebServiceFaxImp.getUsersMap().remove(username);
	}
	
	public void stop(){
		timeout = null;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count ;
	}

}
