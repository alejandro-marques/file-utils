package org.chronos.utils;

public class RunRename {

	private static String folder = "D:\\Imágenes\\Recover Varios";
	
	
	public static void main(String[] args) throws Exception {
		Renamer.renameWithDate(folder);
		
		System.out.println("END!");
	}
}
