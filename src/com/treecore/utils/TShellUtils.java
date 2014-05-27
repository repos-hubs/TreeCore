package com.treecore.utils;

import com.treecore.utils.log.TLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TShellUtils {
	public static boolean mIsRooted = false;

	public boolean verifyRootAccess() {
		String[] command = { "su", "-c", "ls", "/data" };
		TShellUtils shell = new TShellUtils();
		String text = shell.sendShellCommand(command);
		if ((text.indexOf("app") > -1) || (text.indexOf("anr") > -1)
				|| (text.indexOf("user") > -1) || (text.indexOf("data") > -1))
			mIsRooted = false;
		else {
			mIsRooted = true;
		}

		return isVerifyRootAccess();
	}

	public static boolean isVerifyRootAccess() {
		return mIsRooted;
	}

	public String sendShellCommand(String[] cmd) {
	    TLog.i(this, "\n###executing: " + cmd[0] + "###");
	    String AllText = "";
	    try {
	      Process process = new ProcessBuilder(cmd).start();
	      BufferedReader STDOUT = new BufferedReader(new InputStreamReader(
	        process.getInputStream()));
	      BufferedReader STDERR = new BufferedReader(new InputStreamReader(
	        process.getErrorStream()));
	      try {
	        process.waitFor();
	      } catch (InterruptedException ex) {
	        Logger.getLogger(TShellUtils.class.getName()).log(Level.SEVERE, 
	          null, ex);
	      }
	      String line;
	      while ((line = STDERR.readLine()) != null) {
	    	  AllText = AllText + "\n" + line;
	      }
	      for (; (line = STDOUT.readLine()) != null; (line = STDERR.readLine()) != null) {
	    	  AllText = AllText + "\n" + line;
	    	  continue;
	    	  AllText = AllText + "\n" + line;
	      }

	      return AllText;
	    } catch (IOException ex) {
	    	TLog.i(this, 
	    			"Problem while executing in Shell.sendShellCommand() Received " + 
	    					AllText);
	    }
	    return "CritERROR!!!";
	}
}