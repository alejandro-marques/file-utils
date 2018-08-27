package org.chronos.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.chronos.utils.common.Constants;
import org.chronos.utils.common.Utils;
import org.chronos.utils.model.ProcessInfo;
import org.chronos.utils.model.ImageInfo;

public class Deduplicator {
	
	private static final Logger logger = Logger.getLogger(Deduplicator.class);
	
	
	private static int logFrequency = 100;
	private static Random random = new Random();
	
	
	public static void combineFolders(String workingFolder, String[] combineFolders) throws Exception {
		long start;
		logger.info("Starting combination process");
		ProcessInfo info = new ProcessInfo();
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
		ProcessInfo info = new ProcessInfo();
			info.setWorkingFolder(workingFolder);
		
		if (!hashesFile.exists()) {
			logger.info("Hashes file is not present so a new one would be generated");
			start = System.currentTimeMillis();
			HashRetriever.writeHashFile(workingFolder, targetFolder);
			logger.info("Hashes file generated in " + (System.currentTimeMillis() - start) + " ms."); 
		}
		
		logger.info("Generating hashes map from hashes file");
		start = System.currentTimeMillis();
		HashMap<String, HashMap<String, String>> hashesMap = HashRetriever.getMapFromHashFile(workingFolder);
		logger.info("Hashes map generated in " + (System.currentTimeMillis() - start) + " ms."); 
		info.setFileIdentifiers(hashesMap);


		String resultsFileName = workingFolder + "/" + Constants.hashesFile;
		PrintWriter hashesOutput = new PrintWriter(new FileWriter(resultsFileName, true));
		readFilesInFolder(workingFolder + "/" + newFolder, hashesOutput, info);
		hashesOutput.close();
	}
	
	
	public static void excludeFiles (String workingFolder, String targetFolder, String[] completeExclusionFolderNames, String[] allowedExtensionsArray) throws Exception {
		File hashesFile = new File(workingFolder + "/" + Constants.hashesFile);
		long start;
		ProcessInfo info = new ProcessInfo();
			info.setWorkingFolder(workingFolder);


		File excludedFolder = new File(workingFolder + "/" + targetFolder + "/" + Constants.excludedFolder);
		if (excludedFolder.exists() && excludedFolder.isDirectory()) {
			logger.info("There is a previous \"excluded\" folder so it is removed");
			start = System.currentTimeMillis();
			FileUtils.deleteDirectory(excludedFolder);
			logger.info("Folder removed in " + (System.currentTimeMillis() - start) + " ms.");
		}
		excludedFolder.mkdir();
		logger.info("Excluded folder created");
		
		if (!hashesFile.exists()) {
			logger.info("Hashes file is not present so a new one would be generated");
			start = System.currentTimeMillis();
			HashRetriever.writeHashFile(workingFolder, targetFolder);
			logger.info("Hashes file generated in " + (System.currentTimeMillis() - start) + " ms."); 
		}
		
		logger.info("Generating hashes map from hashes file");
		start = System.currentTimeMillis();
		HashMap<String, HashMap<String, String>> hashesMap = HashRetriever.getMapFromHashFile(workingFolder);
		logger.info("Hashes map generated in " + (System.currentTimeMillis() - start) + " ms."); 
		info.setFileIdentifiers(hashesMap);
		
		HashSet<String> allowedExtensions = new HashSet<>();
		for (String extension : allowedExtensionsArray) {
			allowedExtensions.add(extension.toLowerCase());
		}

		for (String completeExclusionFolderName : completeExclusionFolderNames) {
			excludeFilesInFolder(completeExclusionFolderName, workingFolder + "/" + targetFolder, allowedExtensions, info);
		}
	}
	
	
	
	private static void readFilesInFolder (String folderName, PrintWriter hashesOutput, ProcessInfo processInfo) throws Exception {
		File folder = new File(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new Exception("Requested folder " + folderName + " does not exists or is not a directory");
		}
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				readFilesInFolder(file.getPath(), hashesOutput, processInfo);
			}
			else {
				ImageInfo imageInfo = Utils.getImageInfo(file);
				
				if (null == imageInfo) {
					File uniqueFile = new File(processInfo.getWorkingFolder() + "/" + Constants.uniqueFolder + "/" + Constants.smallFolder + "/" + file.getName());
					FileUtils.copyFile(file, uniqueFile);
					processInfo.increaseDiscardedFiles();
				}
				else { 

					boolean unique = false;
					boolean debug = false;
					
					if ("2003-12-06 23.00.50 - f12128904.jpg".equals(file.getName()) || "2014-11-09 17.42.56 - IMG-20141109-WA0001.jpg".equals(file.getName())) {
						debug = true;
					}
					
					String size = imageInfo.getSize();
					String hash = imageInfo.getSampleHash();
					
					HashMap<String, String> checksums = processInfo.getFileIdentifiers().get(size);
					
					if (null == checksums) {
						checksums = new HashMap<>();
						processInfo.getFileIdentifiers().put(size, checksums);
						unique = true;
					}
					else {
						String previousFile = checksums.get(hash);
						unique = null == previousFile;
					}
					if (unique) {
						if (true == debug) {
							logger.warn("#### adding unique file " + file.getPath() + " - " + hash);
						}
						checksums.put(hash, file.getName());
						String id = size + "_" + hash;
						processUniqueFile(file, id, hashesOutput, processInfo);
					}
					else {
						if (true == debug) {
							logger.warn("#### " + file.getPath() + " repetido de " + checksums.get(hash) + " [" + hash + "]");
						}
						processInfo.increaseDuplicateFiles();
					}
				}

				processInfo.increaseProcessedFiles();
			}
			if (processInfo.getProcessedFiles() % logFrequency == 0) {
				printInfo(processInfo);
			}
		}
	}
	
	
	
	private static void excludeFilesInFolder (String completeExclusionFolderName, String completeTargetFolderName, HashSet<String> allowedExtensions, ProcessInfo processInfo) throws Exception {
		File completeExclusionFolder = new File(completeExclusionFolderName);
		if (!completeExclusionFolder.exists() || !completeExclusionFolder.isDirectory()) {
			throw new Exception("Requested folder " + completeExclusionFolderName + " does not exists or is not a directory");
		}
		
		for (File file : completeExclusionFolder.listFiles()) {
			if (file.isDirectory()) {
				excludeFilesInFolder(file.getPath(), completeTargetFolderName, allowedExtensions, processInfo);
			}
			else if (allowedExtensions.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase())) {
				ImageInfo imageInfo = Utils.getImageInfo(file);
				
				if (null != imageInfo) {
					String size = imageInfo.getSize();
					String hash = imageInfo.getSampleHash();
					
					HashMap<String, String> hashes = processInfo.getFileIdentifiers().get(size);
					if (null != hashes && null != hashes.get(hash)){
						String fileName = hashes.get(hash);
	
						if ("f12128904.jpg".equals(fileName)) {
							logger.warn("#### excluido por " + file.getName());
						}
						
						File movedFile = new File(completeTargetFolderName + "/" + fileName);
						if (movedFile.exists()){
							FileUtils.moveFile(movedFile, new File(completeTargetFolderName + "/" + Constants.excludedFolder + "/" + fileName));
							hashes.remove(hash);
							if (hashes.size() == 0){
								processInfo.getFileIdentifiers().remove(size);
							}
							processInfo.increaseExcludedFiles();
						}
						else {
							logger.warn("File " + movedFile.getPath() + " does not exist, but it seems to be present in the hash [" + hashes.get(hash) + "]");
						}
					}
					processInfo.increaseProcessedFiles();
				}
			}
			if (processInfo.getProcessedFiles() % logFrequency == 0) {
				printExclusionInfo(processInfo);
			}
		}
	}
	
	private static void processUniqueFile (File file, String fileId, PrintWriter hashesOutput, ProcessInfo info) throws Exception {
		File uniqueFile = new File(info.getWorkingFolder() + "/" + Constants.uniqueFolder + "/" + file.getName());
		if (uniqueFile.exists()){
			logger.warn("File " + uniqueFile.getName() + " already exists. Adding some random number to the file");
			uniqueFile = new File(info.getWorkingFolder() + "/" + Constants.uniqueFolder + "/" + "_" + random.nextInt(1000) + file.getName());
		}
		FileUtils.copyFile(file, uniqueFile);
		hashesOutput.println(fileId + Constants.hashLineSeparator + file.getName());
		info.getExtensions().add(file.getName().substring(file.getName().lastIndexOf(".") + 1));
		info.increaseUniqueFiles();
	}
	
	private static void printInfo(ProcessInfo info) {
		logger.info(info.getProcessedFiles() + " files processed [" + info.getUniqueFiles() + " unique / " + info.getDuplicateFiles() + " duplicates / " + info.getDiscardedFiles() + " discarded] - " + (System.currentTimeMillis() - info.getPartialStart()) + "ms");
		info.restartPartialStart();
	}
	
	private static void printExclusionInfo(ProcessInfo info) {
		logger.info(info.getProcessedFiles() + " files processed [" + info.getExcludedFiles() + " excluded] - " + (System.currentTimeMillis() - info.getPartialStart()) + "ms");
		info.restartPartialStart();
	}
}
