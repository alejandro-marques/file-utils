package org.chronos.utils;

import java.util.HashMap;
import java.util.HashSet;

public class CombinerInfo {

	private long processedFiles = 0;
	private long uniqueFiles = 0;
	private long duplicateFiles = 0;
	
	private String workingFolder;
	private HashMap<Long, HashSet<String>> fileIdentifiers = new HashMap<>();
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

	
	public String getWorkingFolder() { return workingFolder; }
	public void setWorkingFolder(String workingFolder) { this.workingFolder = workingFolder; }
	
	
	public HashMap<Long, HashSet<String>> getFileIdentifiers() { return fileIdentifiers; }
	public void setFileIdentifiers(HashMap<Long, HashSet<String>> fileIdentifiers) { this.fileIdentifiers = fileIdentifiers; }
	
	public HashSet<String> getExtensions() { return extensions; }
	public void setExtensions(HashSet<String> extensions) { this.extensions = extensions; }
}
