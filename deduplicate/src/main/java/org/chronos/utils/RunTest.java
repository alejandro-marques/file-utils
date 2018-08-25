package org.chronos.utils;

public class RunTest {

	private static String workingFolder = "D:\\Imágenes\\test";

	private static String targetFolder = "unique";
	private static String[] combineFolders = new String[] {"folder1","folder2"};
	private static String newFolder = "add";
	private static String[] exclusionFolder = new String[] {"D:\\Imágenes\\test\\exclude1", "D:\\Imágenes\\test\\exclude2"};
	private static String[] allowedExtensions = new String[] {"jpg","jpeg"};
	
	
	public static void main(String[] args) throws Exception {
		Deduplicator.combineFolders(workingFolder, combineFolders);
		Deduplicator.addFolder(workingFolder, targetFolder, newFolder);
		Deduplicator.excludeFiles(workingFolder, targetFolder, exclusionFolder, allowedExtensions);
		
		System.out.println("END!");
	}
}
