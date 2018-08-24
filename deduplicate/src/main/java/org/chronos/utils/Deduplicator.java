package org.chronos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class Deduplicator {
	
	private static final Logger logger = Logger.getLogger(Deduplicator.class);
	
	private static String workingFolder = "/media/alejandro/My Passport 500GB/Recover";
	private static String[] originFolders = new String [] {"2018", "2013"};
	private static String targetFolder = "unique";

	private static int processedFiles = 0;
	private static int uniqueFiles = 0;
	private static int duplicateFiles = 0;
	
	private static int logFrequency = 100;
	
	private static HashMap<Long, HashSet<String>> fileIdentifiers = new HashMap<>();
	private static HashSet<String> extensions = new HashSet<>();
	
	public static void main(String[] args) throws Exception {
		logger.info("Starting deduplication process");

		File uniqueFolder = new File(workingFolder + "/" + targetFolder);
		if (uniqueFolder.exists()) {
			uniqueFolder.delete();
		}
		uniqueFolder.mkdir();
		logger.info("Unique folder created");
		
		for (String originFolder : originFolders) {
			readFilesInFolder(workingFolder + "/" + originFolder);
		}
		
		printInfo();
		logger.info("Retrieved extensions: " + extensions);
//		System.out.println(fileIdentifiers);
		logger.info("Deduplication process ended");
	}
	
	
	private static void readFilesInFolder (String folderName) throws Exception {
		File folder = new File(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new Exception("Requested folder " + folderName + " does not exists or is not a directory");
		}
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				readFilesInFolder(file.getPath());
			}
			else {
				boolean unique = false;
				long size = file.length();
				String checksum = getHashForFile(file);
				
				HashSet<String> checksums = fileIdentifiers.get(size);
				
				if (null == checksums) {
					checksums = new HashSet<>();
					checksums.add(checksum);
					fileIdentifiers.put(size, checksums);
					unique = true;
				}
				else {
					unique = checksums.add(checksum);
				}
				
				if (unique) {
					processUniqueFile(file);
				}
				else {
					duplicateFiles++;
				}
				
				processedFiles++;
			}
			if (processedFiles % logFrequency == 0) {
				printInfo();
			}
		}
	}
	
	
	private static void processUniqueFile (File file) throws Exception {
		File uniqueFile = new File(workingFolder + "/" + targetFolder + "/" + file.getName());
		FileUtils.copyFile(file, uniqueFile);
		extensions.add(file.getName().substring(file.getName().lastIndexOf(".") + 1));
		uniqueFiles++;
	}
	
	
	private static String getHashForFile(File file) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);

        // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
        // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.

//        String hash = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
        String hash = DigestUtils.sha256Hex(IOUtils.toByteArray(fileInputStream));
        fileInputStream.close();
        
        return hash;
    }
	
	private static void printInfo() {
		logger.info(processedFiles + " files processed [" + uniqueFiles + " unique / " + duplicateFiles + " duplicates]");
	}
}
