package ru.sip64.webfax.bean;

import javax.ejb.Local;

@Local
public interface AccessForDBPinCodeBeanLocal {
	
	public String getPinCode(String userName);
	public String getPinCodePassword(String userName);
	public void createNewUser(String username, String pinCode, String pinPassword);
	public void deleteUser(String username);
	public void updateUser(String username, String pinCode, String pinPassword);

}
