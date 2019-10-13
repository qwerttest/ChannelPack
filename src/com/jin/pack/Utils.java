package com.jin.pack;

import java.io.File;

public class Utils {
	public static boolean isApk(File file) {
		return file != null && file.getName().endsWith("apk");
	}
	
	public static boolean isTxt(File file) {
		return file != null && file.getName().endsWith("txt");
	}
	
	public static boolean isEmptyText(String text) {
		return text == null || text.trim().length() == 0;
	}
	
	/*** 
     *  
     * @param folder 
     *            : directory 
     */  
    public static void openDirectory(String folderPath) {  
        if (isEmptyText(folderPath)) {  
            return;  
        }  
        File file = new File(folderPath);;  
        if (!file.exists()) {  
            return;  
        }  
        Runtime runtime = null;  
        try {  
            runtime = Runtime.getRuntime();  
//            if (SystemHWUtil.isWindows) {  
//                runtime.exec("cmd /c start explorer " + file.getAbsolutePath());  
//            } else {  
                runtime.exec("nautilus " + folderPath);  
//            }  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        } finally {  
            if (null != runtime) {  
                runtime.runFinalization();  
            }  
        }  
    }  
}
