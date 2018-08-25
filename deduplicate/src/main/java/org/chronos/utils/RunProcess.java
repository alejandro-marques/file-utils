package org.chronos.utils;

public class RunProcess {

	private static String workingFolder = "D:\\Imágenes";

	private static String targetFolder = "unique";
	private static String[] combineFolders = new String[] {"Recover 2018","Recover 2013", "add"};
	private static String[] exclusionFolder = new String[] {"E:\\Documentos", "E:\\Imágenes"};
	private static String[] allowedExtensions = new String[] {"jpg","jpeg"};
	
	public static void main(String[] args) throws Exception {
		Deduplicator.combineFolders(workingFolder, combineFolders);
		Deduplicator.excludeFiles(workingFolder, targetFolder, exclusionFolder, allowedExtensions);
		
		System.out.println("END!");
	}
}
