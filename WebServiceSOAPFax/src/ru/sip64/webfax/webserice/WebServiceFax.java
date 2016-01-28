package ru.sip64.webfax.webserice;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService()
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL) 
public interface WebServiceFax {
	
//---------------------------------------------------------------------------------------------------------------------
	//WORK WITH FILE
//---------------------------------------------------------------------------------------------------------------------
	
	@WebMethod(operationName="ConvertFile")
	@WebResult(name="Result")
	public String convertFile(@WebParam(name="Username")String username,
			@WebParam(name="FilePath")String filePath);
	
	@WebMethod(operationName="DeleteFile")
	@WebResult(name="Result")
	public String deleteFile(@WebParam(name="Username")String username, 
			@WebParam(name="FilePathDelete")String filePath);
	
	@WebMethod(operationName="MoveFile")
	@WebResult(name="Result")
	public String moveFile(@WebParam(name="Username")String username, 
			@WebParam(name="FileName")String fileName);
	
//---------------------------------------------------------------------------------------------------------------------
	//OPEN AND CLOSE SESSION
//---------------------------------------------------------------------------------------------------------------------
	
	@WebMethod(operationName="OpenSession")
	@WebResult(name="Result")
	public String openSession(@WebParam(name="Username")String username);
	
	@WebMethod(operationName="CloseSession")
	@WebResult(name="Result")
	public String closeSession(@WebParam(name="Username")String username);

//---------------------------------------------------------------------------------------------------------------------
	//SEND FAX
//---------------------------------------------------------------------------------------------------------------------
	
	@WebMethod(operationName="SendFax")
	@WebResult(name="Result")
	public String sendFax(@WebParam(name="Username")String username, 
			@WebParam(name="DestinationNumber")String destinationNumber,
			@WebParam(name="FileName")String fileName);
	
	@WebMethod(operationName="ResultSendFax")
	@WebResult(name="Result")
	public String resultSendFax (@WebParam(name="Username")String username, 
			@WebParam(name="DestinationNumber")String destinationNumber,
			@WebParam(name="FileName")String fileName);
	
	@WebMethod(operationName="StopFax")
	public void stopFax (@WebParam(name="Username")String username);
	
	
//---------------------------------------------------------------------------------------------------------------------
	//WORK WITH USER
//---------------------------------------------------------------------------------------------------------------------	
	
	
	@WebMethod(operationName="CreateUser")
	@Oneway
	public void createUser(@WebParam(name="Username")String username, @WebParam(name="PinCode")String pinCode, 
			@WebParam(name="PinPassword")String pinPassword);

	@WebMethod(operationName="DeleteUser")
	@Oneway
	public void deleteUser(@WebParam(name="Username")String username);

	@WebMethod(operationName="UpdateUser")
	@Oneway
	public void updateUser(@WebParam(name="Username")String username, @WebParam(name="PinCode")String pinCode, 
			@WebParam(name="PinPassword")String pinPassword);

}
