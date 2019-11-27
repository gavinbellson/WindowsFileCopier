 
/**
 * Java utility class for reading a given "in" Windows directory and all 
 * of its sub directories and copying the files to a single "out" directory,
 * taking care of naming collisions by building a Map with every file name 
 * and incrementing the N of each instance of that file name and adding 
 * that N to the end of the colliding file names.
 *   
 * @author Gavin Bellson
 * @version 20191126
 *
 */
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

import java.util.*;

public class FileCopierUtility {

	
	/**
	 * private constructor to prevent instantiation.
	 */
	private FileCopierUtility() {
		
	}
	
	/**
	 * 
	 */
	
	/** found this recursive method online here:
	 * http://javaconceptoftheday.com/list-all-files-in-directory-in-java/
	 * 
	 * it recursively calls itself if it comes to a folder,
	 * else if it comes to a file it passes to to other method
	 * for doing the copying 
	 * 
	 * @param path
	 * @param outFilePath
	 * 
	 */
	private static void listMyFiles(String inPath, String outFilePath, Map <String,Integer> fileNameMap, PrintWriter outputBatchFile) throws IOException
    {
        try {
        	File folder = new File(inPath);
        	//System.out.println("Folder = " + folder.getAbsolutePath());
        	
        	File[] files = folder.listFiles();
            
            for (File file : files) 
            {
                if (file.isFile())
                {

                	if (fileNameFilter(file.getName())) {

                		if (fileNameMap.containsKey(file.getName())) {
                			int numberOfCopiesOfFile = fileNameMap.get(file.getName());
                			numberOfCopiesOfFile++;
                			fileNameMap.put(file.getName(), numberOfCopiesOfFile);
                		} else {
                			fileNameMap.put(file.getName(), 1);
                		}
                	
                		copyFiles(folder.getAbsolutePath(), file.getName(), outFilePath, fileNameMap, outputBatchFile);
                	}
                }
                else if (file.isDirectory())
                {
                    if (!file.getAbsolutePath().startsWith("D:\\Windows")) {
                    	listMyFiles(file.getAbsolutePath(), outFilePath,  fileNameMap, outputBatchFile);
                    }
                }
            }

        } catch (Exception e) {
        	System.out.println("Error: " + (String) e.getMessage());
        	//System.out.println("problem creating in File " + inPath);
        }
        
        
            }

	/** Create filename filter so only text files, video files, picture files 
	 * are picked up by the copy. 
	 * 
	 * @param fileName
	 * 		a string name of the file 
	 * 
	 */
	private static boolean fileNameFilter(String fileName) {
			
		String lowercaseName = fileName;
		//System.out.println("filename = " + lowercaseName);
		
			if (lowercaseName.endsWith(".txt")) 				{return true;}
			else if (lowercaseName.endsWith(".rtf"))				{return true;}
			else if (lowercaseName.endsWith(".doc"))				{return true;}
			else if (lowercaseName.endsWith(".docx"))				{return true;}
			else if (lowercaseName.endsWith(".ppt"))				{return true;}
			else if (lowercaseName.endsWith(".pptx"))				{return true;}
			else if (lowercaseName.endsWith(".pdf"))				{return true;}
			else if (lowercaseName.endsWith(".jpg"))				{return true;}
			else if (lowercaseName.endsWith(".jpeg"))				{return true;}
			else if (lowercaseName.endsWith(".png"))				{return true;}
			else if (lowercaseName.endsWith(".h264"))				{return true;}
			else if (lowercaseName.endsWith(".mp4"))				{return true;}
			else if (lowercaseName.endsWith(".mpg"))				{return true;}
			else if (lowercaseName.endsWith(".mpeg"))				{return true;}
			else if (lowercaseName.endsWith(".wmv"))				{return true;}
			else if (lowercaseName.endsWith(".mp3"))				{return true;}
			else if (lowercaseName.endsWith(".cda"))				{return true;}
			else if (lowercaseName.endsWith(".ogg"))				{return true;}
			else if (lowercaseName.endsWith(".wav"))				{return true;}
			else if (lowercaseName.endsWith(".xlsx"))				{return true;}
			else if (lowercaseName.endsWith(".xls")) 				{return true;}
			else {
				return false;}
	
	}

	
	/**
	 * Writes out Windows commands to copy files with prompting for overwrites.
	 * 
	 * @param inFilePath
	 * 		the absolute path of the folder being read from
	 * 
	 * @param fileToCopy
	 * 		the file name to copy
	 * 
	 * @param fileNameMap
	 * 		the destination folder to copy into
	 */
	private static void copyFiles(String inFilePath, String fileToCopy , String outFilePath, Map<String, Integer> fileNameMap, PrintWriter outputBatchFile) 
	throws IOException {
		
		String lineToWrite;
		int numberOfCopiesOfFile = fileNameMap.get(fileToCopy);
		
		if (numberOfCopiesOfFile == 1) {
			lineToWrite = "copy \"" + inFilePath + "\\" + fileToCopy + "\" \"" + outFilePath + "\"";
			System.out.println(lineToWrite);
			outputBatchFile.println(lineToWrite);
		} else {
			int fileNameLastPeriodLocation = fileToCopy.lastIndexOf('.');
			String fileToCopyBegin = fileToCopy.substring(0, fileNameLastPeriodLocation);
			String fileToCopyExtension = fileToCopy.substring(fileNameLastPeriodLocation);
			/* System.out.println("fileToCopyBegin=" + fileToCopyBegin + " , fileToCopyExtension = " + fileToCopyExtension); */
			String fileToCopyFinal = fileToCopyBegin + "(" + numberOfCopiesOfFile + ")" + fileToCopyExtension;
			lineToWrite = "copy \"" + inFilePath + "\\" + fileToCopy + "\" \"" + outFilePath + fileToCopyFinal + "\"" ;
			System.out.println(lineToWrite);
			outputBatchFile.println(lineToWrite);
		}
		
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/* a text file for spooling the copy commands */
		PrintWriter outputBatchFile = null;
		System.out.println("enter a path/file for the output batch file:");
		String batchString = "C:\\Users\\Jeremy\\Downloads\\mom\\copyOutput.bat";
		try {
			outputBatchFile = new PrintWriter (new BufferedWriter(new FileWriter(batchString)));
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		/* a map of the filenames with a counter of how many iterations of the filename */
		Map <String,Integer> fileNameMap = new HashMap<String,Integer> ();
		Scanner keyboardReader = new Scanner (System.in);
		System.out.println("Enter path to copy:");
		//String userStatedInPath = keyboardReader.nextLine();
		String userStatedInPath = "D:\\";
		System.out.println("you entered: " + userStatedInPath);
		
		/* having problems reading the hard disk
		 * mounted via usb, so adding this block which 
		 * lists the connected drives to confirm the program
		 * reads the D:\ drive it got mapped to */
		File [] drives = File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		
		if (drives != null && drives.length > 0) {
			for (File aDrive : drives) {
				System.out.println(aDrive);
				System.out.println(fsv.getSystemDisplayName(aDrive));
			}
		}
		
		try {
			//File outFile = new File ("C:\\Users\\Jeremy\\Downloads\\mom");
			String outFilePath = "C:\\Users\\Jeremy\\Downloads\\mom";
			listMyFiles(userStatedInPath,outFilePath,fileNameMap, outputBatchFile );
		}
		catch (Exception e) {
			System.out.println("error opening in directory");
			keyboardReader.close();
			outputBatchFile.close(); //close resources if errors
			return;// end here if can't get the filenames
		}
		
		
		System.out.println("end of program.");
		
		/* Successful conclusion, now close all open resources */
		try {
			keyboardReader.close();
			outputBatchFile.close();
		}
		catch (Exception e) {
			System.out.println("error closing resources");
		}
		
		
	}

}








