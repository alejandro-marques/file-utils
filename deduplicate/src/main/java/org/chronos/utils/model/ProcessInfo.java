package org.chronos.utils.model;

import java.util.HashMap;
import java.util.HashSet;

public class ProcessInfo {

	private long processedFiles = 0;
	private long uniqueFiles = 0;
	private long duplicateFiles = 0;
	private long excludedFiles = 0;
	
	private long startTime = System.currentTimeMillis();
	private long partialStart = System.currentTimeMillis();
	
	private String workingFolder;
	private HashSet<String> allowedExtensions = new HashSet<>();
	
	private HashMap<String, HashMap<String, String>> fileIdentifiers = new HashMap<>();
	private HashSet<String> extensions = new HashSet<>();
	
	
	
	public long getProcessedFiles() { return processedFiles; }
	public void setProcessedFiles(long processedFiles) { this.processedFiles = processedFiles; }
	public void increaseProcessedFiles () {processedFiles++;}
	
	public long getUniqueFiles() { return uniqueFiles; }
	public void setUniqueFiles(long uniqueFiles) { this.uniqueFiles = uniqueFiles; }
	public void increaseUniqueFiles () {uniqueFiles++;}
	
	public long getDuplicateFiles() { return duplicateFiles; }
	public void setDuplicateFiles(long duplicateFiles) { this.duplicateFiles = duplicateFiles; }
	public void increaseDuplicateFiles () {duplicateFiles++;}
	
	public long getExcludedFiles() { return excludedFiles; }
	public void setExcludedFiles(long excludedFiles) { this.excludedFiles = excludedFiles; }
	public void increaseExcludedFiles () {excludedFiles++;}

	
	public long getStartTime() { return startTime; }
	public long getPartialStart() { return partialStart; }
	public void restartPartialStart () { partialStart = System.currentTimeMillis();}
	
	
	public String getWorkingFolder() { return workingFolder; }
	public void setWorkingFolder(String workingFolder) { this.workingFolder = workingFolder; }
	
	public HashSet<String> getAllowedExtensions() { return allowedExtensions; }
	public void setAllowedExtensions(HashSet<String> allowedExtensions) { this.allowedExtensions = allowedExtensions; }
	
	
	public HashMap<String, HashMap<String, String>> getFileIdentifiers() { return fileIdentifiers; }
	public void setFileIdentifiers(HashMap<String, HashMap<String, String>> fileIdentifiers) { this.fileIdentifiers = fileIdentifiers; }
	
	public HashSet<String> getExtensions() { return extensions; }
	public void setExtensions(HashSet<String> extensions) { this.extensions = extensions; }
}
