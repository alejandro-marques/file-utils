package org.chronos.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.chronos.utils.common.Constants;
import org.chronos.utils.common.Utils;

public class Deduplicator {
	
	private static final Logger logger = Logger.getLogger(Deduplicator.class);
	
	
	private static int logFrequency = 100;
	
	
	public static void combineFolders(String workingFolder, String[] combineFolders) throws Exception {
		long start;
		logger.info("Starting combination process");
		CombinerInfo info = new CombinerInfo();
			info.setWorkingFolder(workingFolder);

		File uniqueFolder = new File(workingFolder + "/" + Constants.uniqueFolder);
		if (uniqueFolder.exists() && uniqueFolder.isDirectory()) {
			logger.info("There is a previous \"unique\" folder so it is removed");
			start = System.currentTimeMillis();
			FileUtils.deleteDirectory(uniqueFolder);
			logger.info("Folder removed in " + (System.currentTimeMillis() - start) + " ms.");
		}
		uniqueFolder.mkdir();
		logger.info("Unique folder created");

		String resultsFileName = workingFolder + "/" + Constants.hashesFile;
		logger.info("Creating new hashes file: " + resultsFileName);
		
		File results = new File(resultsFileName);
		if (results.exists()) {
			results.delete();
		}
		PrintWriter hashesOutput = new PrintWriter(new FileWriter(resultsFileName, true));
		
		for (String originFolder : combineFolders) {
			logger.info("Processing folder \"" + originFolder + "\"");
			readFilesInFolder(workingFolder + "/" + originFolder, hashesOutput, info);
		}
		hashesOutput.close();
		printInfo(info);
		logger.info("Retrieved extensions: " + info.getExtensions());
		logger.info("Combination process ended");
		
	}
	
	
	public static void addFolder (String workingFolder, String targetFolder, String newFolder) throws Exception {
		File hashesFile = new File(workingFolder + "/" + Constants.hashesFile);
		long start;
		CombinerInfo info = new CombinerInfo();
			info.setWorkingFolder(workingFolder);
		
		if (!hashesFile.exists()) {
			logger.info("Hashes file is not present so a new one would be generated");
			start = System.currentTimeMillis();
			HashRetriever.writeHashFile(workingFolder, targetFolder);
			logger.info("Hashes file generated in " + (System.currentTimeMillis() - start) + " ms."); 
		}
		
		logger.info("Generating hashes map from hashes file");
		start = System.currentTimeMillis();
		HashMap<Long, HashSet<String>> hashesMap = HashRetriever.getMapFromHashFile(workingFolder);
		logger.info("Hashes map generated in " + (System.currentTimeMillis() - start) + " ms."); 
		info.setFileIdentifiers(hashesMap);


		String resultsFileName = workingFolder + "/" + Constants.hashesFile;
		PrintWriter hashesOutput = new PrintWriter(new FileWriter(resultsFileName, true));
		readFilesInFolder(workingFolder + "/" + newFolder, hashesOutput, info);
		hashesOutput.close();
	}
	
	
	
	private static void readFilesInFolder (String folderName, PrintWriter hashesOutput, CombinerInfo info) throws Exception {
		File folder = new File(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new Exception("Requested folder " + folderName + " does not exists or is not a directory");
		}
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				readFilesInFolder(file.getPath(), hashesOutput, info);
			}
			else {
				boolean unique = false;
				long size = file.length();
				String checksum = Utils.getHashForFile(file);
				
				HashSet<String> checksums = info.getFileIdentifiers().get(size);
				
				if (null == checksums) {
					checksums = new HashSet<>();
					checksums.add(checksum);
					info.getFileIdentifiers().put(size, checksums);
					unique = true;
				}
				else {
					unique = checksums.add(checksum);
				}
				
				if (unique) {
					String id = size + "_" + checksum;
					processUniqueFile(file, id, hashesOutput, info);
				}
				else {
					info.increaseDuplicateFiles();;
				}
				
				info.increaseProcessedFiles();
			}
			if (info.getProcessedFiles() % logFrequency == 0) {
				printInfo(info);
			}
		}
	}
	
	private static void processUniqueFile (File file, String fileId, PrintWriter hashesOutput, CombinerInfo info) throws Exception {
		File uniqueFile = new File(info.getWorkingFolder() + "/" + Constants.uniqueFolder + "/" + file.getName());
		FileUtils.copyFile(file, uniqueFile);
		hashesOutput.println(fileId + Constants.hashLineSeparator + file.getName());
		info.getExtensions().add(file.getName().substring(file.getName().lastIndexOf(".") + 1));
		info.increaseUniqueFiles();
	}
	
	private static void printInfo(CombinerInfo info) {
		logger.info(info.getProcessedFiles() + " files processed [" + info.getUniqueFiles() + " unique / " + info.getDuplicateFiles() + " duplicates]");
	}
}
