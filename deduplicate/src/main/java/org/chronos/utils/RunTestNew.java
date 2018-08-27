package org.chronos.utils;

public class RunTestNew {

	private static String workingFolder = "D:\\Imágenes\\test";
	private static String[] combineFolders = new String[] {"skipped"};
	
	
	public static void main(String[] args) throws Exception {
		Deduplicator.combineFolders(workingFolder, combineFolders);
		
		System.out.println("END!");
	}
}
