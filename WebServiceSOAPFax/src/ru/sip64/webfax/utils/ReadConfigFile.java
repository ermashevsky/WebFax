package ru.sip64.webfax.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class ReadConfigFile extends Properties{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private static ReadConfigFile cfg = null;
		
		private ReadConfigFile(){
			
		}
		
		public static ReadConfigFile getCfgReadFile(){
			if (cfg == null){
				cfg = new  ReadConfigFile();
				cfg.getCfgFile();
			}
			return cfg;
		}
		
		public void getCfgFile(){
			String workingDir = "/etc/faxdialog";
			String cfgFileName = "faxdialog.cfg";
			BufferedReader inStream = null;
			try {
				inStream = new BufferedReader(new InputStreamReader(new FileInputStream(workingDir + File.separator + cfgFileName)));
				try {
					cfg.load(inStream);
					
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			} catch (Exception e) {
				System.err.println("File faxdialog.cfg not found!!!");
				System.exit(-1);
			}finally{
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			}

	}	
