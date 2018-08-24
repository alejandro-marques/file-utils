package org.chronos.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.chronos.utils.common.Mode;

public class DeduplicateApplication {
	private static final Logger logger = Logger.getLogger(DeduplicateApplication.class);
	
	private static Scanner reader = new Scanner(System.in);
	
	
	
	
	public static void main(String[] args) throws Exception {
		Mode mode = chooseMode();
		String workingFolder = chooseFolder("working", "/media/alejandro", null);
		
		switch (mode) {
			case COMBINE:
				logger.info("Combine mode selected");
				String[] combineFolders = chooseMultipleFolders(workingFolder);
				logger.info("Folders to be combined:");
				for (String folderName : combineFolders) {
					logger.info(workingFolder + "/" + folderName);
				}
				if(confirmExecution("These folders " + Arrays.asList(combineFolders) + " will be combined into a folder called \"unique\"")) {
					logger.info("Combining folders...");
					Deduplicator.combineFolders(workingFolder, combineFolders);
				}
				else {
					System.out.println("Execution aborted");
				}
				
				
				break;
				
			case ADD:
				logger.info("Add mode selected");
				String targetFolder = chooseFolder("target", "unique", workingFolder);
				String newFolder = chooseFolder("new", "add", workingFolder);
				if(confirmExecution("The folder \"" + newFolder + "\" will be combined into \"" + targetFolder + "\"")) {
					logger.info("Combining folders...");
					Deduplicator.addFolder(workingFolder, targetFolder, newFolder);
				}
				else {
					System.out.println("Execution aborted");
				}
				break;
				
			case EXCLUDE:
				logger.info("Exclude mode selected");
				break;
	
			default:
				throw new Exception ("Mode " + mode + " is not valid");
		}
		
		reader.close();

		logger.info("END!");
		
	}
	
	private static Mode chooseMode() throws Exception {
		System.out.println("Please, choose one number from the following modes : ");
		System.out.println("[1] Combine: Combines several folders by adding every unique file into a unique folder ");
		System.out.println("[2] Add: Adds the contents of a given folder to another one skipping duplicates");
		System.out.println("[3] Exclude: Excludes the files included in a given folder from another one ");
		String option = reader.nextLine();
		
		
		int optionNumber = 0;
		try {
			optionNumber = Integer.parseInt(option);
		}
		catch (Exception exception){
			throw new Exception("Option " + option + " is not a valid number");
		}
		
		switch (optionNumber) {
			case 1:
				return Mode.COMBINE;
				
			case 2:
				return Mode.ADD;
			
			case 3:
				return Mode.EXCLUDE;
	
			default:
				throw new Exception ("Option " + optionNumber + " is not a valid option");
		}
	}
	
	private static String chooseFolder(String folderId, String example, String parent) throws Exception {
		System.out.println("Please, choose your " + folderId + " folder (for example \"" + example + "\"): ");
		String folderName = reader.nextLine();
		
		File folder = new File ((null != parent? parent + "/" : "") + folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new Exception ("Given folder \"" + folder + "\" does not exist or is not a directory");
		}
		
		return folderName;
	}
	
	private static String[] chooseMultipleFolders(String workingFolder) throws Exception {
		System.out.println("Please, choose the folders being combined in your working folder separated by commas (\"test1,test2\"): ");
		String folders = reader.nextLine();
		
		String[] foldersArray = folders.split(",");
		
		for (String folderName : foldersArray) {
			File folder = new File (workingFolder + "/" + folderName);
			if (!folder.exists() || !folder.isDirectory()) {
				throw new Exception ("Folder \"" + folderName + "\" does not exist or is not a directory");
			}
		}
		
		return foldersArray;
	}
	
	private static boolean confirmExecution (String message) {
		System.out.println(message + ". Are you sure? [y/N]:");
		String accept = reader.nextLine();
		reader.close();
		if (accept.toLowerCase().equals("y")) {
			return true;
		}
		return false;
	}

}
