package org.chronos.utils;

public class TestHashRetriever {
	
	
	private static String workingFolder = "D:\\Imágenes";
//	private static String originFolder = "unique";
	private static String originFolder = "unique";
	
	
	public static void main(String[] args) throws Exception {
		
		HashRetriever.writeHashFile(workingFolder, originFolder);
	}
}
