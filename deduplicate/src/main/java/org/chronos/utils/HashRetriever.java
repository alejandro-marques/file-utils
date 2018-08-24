package org.chronos.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.chronos.utils.common.Constants;
import org.chronos.utils.common.Utils;

public class HashRetriever {
	
	private static final Logger logger = Logger.getLogger(HashRetriever.class);
	
	private static int logFrequency = 100;
	private static int processedFiles = 0;

	private static HashSet<String> hashes = new HashSet<>();
	private static HashSet<String> extensions = new HashSet<>();
	
	
	public static HashMap<Long, HashSet<String>> getMapFromHashFile(String workingFolder) throws Exception {
		logger.info("Reading hashes file into a map");
		HashMap<Long, HashSet<String>> map = new HashMap<>();
		String hashesFileName = workingFolder + "/" + Constants.hashesFile;
		
		if (!new File(hashesFileName).exists()) {
			throw new Exception ("Hashes file was not found");
		}
		
		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(hashesFileName))) {
			stream.forEach((line) -> addHashLineToMap(line, map));
		} 
		catch (Exception exception) {
			throw new Exception("Error while reading the hashes file (Reason: " + exception.getMessage() + ")");
		}
		
		logger.info("Hashes map retrieved");
		
		return map;
	}
	
	
	public static void writeHashFile (String workingFolder, String originFolder) throws Exception {
		String resultsFileName = workingFolder + "/" + Constants.hashesFile;
		logger.info("Creating new hashes file: " + resultsFileName);
		
		File results = new File(resultsFileName);
		if (results.exists()) {
			results.delete();
		}
		PrintWriter output = new PrintWriter(new FileWriter(resultsFileName, true));
		
		readFilesInFolder(workingFolder + "/" + originFolder, output);
		printInfo();
	}
	
	
	private static void addHashLineToMap (String line, HashMap<Long, HashSet<String>> map) throws RuntimeException {
		String[] lineValues = line.split(Constants.hashLineSeparatorRegex);
		if (2 != lineValues.length) {
			throw new RuntimeException("Wrong format for hash line: " + line);
		}
		String fileId = lineValues[0];
		String[] fileValues = fileId.split("_");
		if (2 != fileValues.length) {
			throw new RuntimeException("Wrong format for hash line: " + line);
		}
		
		long fileSize = Long.parseLong(fileValues[0]);
		HashSet<String> hashes = map.get(fileSize);
		if (null == hashes) {
			hashes = new HashSet<>();
		}
		hashes.add(fileValues[1]);
		map.put(fileSize, hashes);
	}
	
	
	private static void readFilesInFolder (String folderName, PrintWriter output) throws Exception {
		File folder = new File(folderName);
		
		if (!folder.exists() || !folder.isDirectory()) {
			output.close();
			throw new Exception("Requested folder " + folderName + " does not exists or is not a directory");
		}
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				readFilesInFolder(file.getPath(), output);
			}
			else {
				long size = file.length();
				String checksum = Utils.getHashForFile(file);
				String id = size + "_" + checksum;
				boolean unique = hashes.add(id);
				
				if (unique) {
					output.println(id + Constants.hashLineSeparator + file.getName());
					extensions.add(file.getName().substring(file.getName().lastIndexOf(".") + 1));
				}
				else {
					logger.warn("The retrieved file " + file.getName() + " is not unique");
				}
			}
			if (++processedFiles % logFrequency == 0) {
				printInfo();
			}
		}
		output.close();
	}
	
	private static void printInfo() {
		logger.info(processedFiles + " files processed");
	}
}
