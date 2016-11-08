package jeco.core.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class devoted to handle critical errors; it generates a log file
 * explaining what happened in the execution.
 * 
 * @author J. M. Colmenar
 */
public class ErrorHandler {
    private static ArrayList<String> errorData = new ArrayList<>();
    private static String instance = "";
    
    private static String getFileName() {
        Calendar now = Calendar.getInstance();
        String fileName = "crashReport"+now.get(Calendar.YEAR)+"_"+now.get(Calendar.MONTH)+"_"+now.get(Calendar.DAY_OF_MONTH);
        fileName += "__"+now.get(Calendar.HOUR_OF_DAY)+"_"+now.get(Calendar.MINUTE)+"_"+now.get(Calendar.SECOND)+".txt";
        return fileName;
    }
    
    public static void setInstance (String instance) {
        ErrorHandler.instance = instance;
    }
    
    public static void reportErrorAndExit(String errorStr) {
        addErrorData(errorStr);
        
        String fileName = getFileName();
        FileWriter f;
	BufferedWriter writer = null;
		
	try {
		f = new FileWriter(fileName);
		writer = new BufferedWriter(f);
                for (String s : errorData) {
                    writer.write(s+"\n");  
                }
                writer.write("\nInstance: "+instance+"\n");
		
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	try {
		if (writer != null ) writer.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
        
        // Once the report was written, print "0" and exit.
        System.out.println("\nExecution error. See log file "+fileName);
        System.out.println("\n\n0");
        System.exit(0);
        
    }
    
    public static void addErrorData(String strErr) {
       errorData.add(strErr);
    }

}
