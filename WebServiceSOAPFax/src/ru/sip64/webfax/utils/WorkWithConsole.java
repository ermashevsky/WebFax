package ru.sip64.webfax.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.jboss.logging.Logger;

public final class WorkWithConsole {
	
	private int status = 0;
	private String responce = "";
	
	private WorkWithConsole(){}
	
	//Log
	private static final Logger log = Logger.getLogger(WorkWithConsole.class.getName());
	
	
	/**
	 * Execute command in shell
	 * @param command - string command 
	 * @return Status execute command
	 */
	public static WorkWithConsole commandExec(String command){
		WorkWithConsole wwc = new WorkWithConsole();
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);		
		InputStream shellIn = null;
		try {
			Process shell = pb.start();
			// To capture output from the shell
			shellIn = shell.getInputStream();
			// Wait for the shell to finish and get the return code
			wwc.status = shell.waitFor();
			wwc.responce = convertStreamToStr(shellIn);
			


		} catch (IOException e) {
			log.error("Error occured while executing Linux command. Error Description: ");
			log.error(e.getMessage());
			}
			catch (InterruptedException e) {
				log.error("Error occured while executing Linux command. Error Description: ");
				log.error(e.getMessage());
			}
		finally{
			try {
				shellIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return wwc;

	}
	
	// Convert InputStream to String
	private static String convertStreamToStr(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
	
	/**
	 * Status shell command
	 * @return status shell command
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Responce shell command
	 * @return responce shell command
	 */
	public String getResponce() {
		return responce;
	}


}
