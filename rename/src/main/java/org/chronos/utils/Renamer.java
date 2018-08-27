package org.chronos.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;

public class Renamer {
	
	private static final Logger logger = Logger.getLogger(Renamer.class);
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	private static String separator = System.getProperty("file.separator");
	private static int logFrequency = 100;
	
	public static void renameWithDate (String folderName) throws Exception {
		File folder = new File(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new Exception ("Given folder \"" + folder + "\" does not exist or is not a directory");
		}
		
		long fileCount = 0;
		long start = System.currentTimeMillis();
		long partialStart = start;
		
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				String completeName = file.getName();
				String fileName = completeName.substring(0, completeName.lastIndexOf("."));
				String extension = completeName.substring(completeName.lastIndexOf(".") + 1);
				String date = format.format(getFileDate(file));
				
				String newName = date + " - " + ((fileName.length() > 100)? fileName.substring(0, 100) : fileName) + "." + extension;
				File newFile = new File(folderName + separator + newName);

				FileUtils.moveFile(file, newFile);
				
				if (++fileCount % logFrequency == 0) {
					logger.info(fileCount + " files processed [" + (System.currentTimeMillis() - partialStart) + "ms]");
					partialStart = System.currentTimeMillis();
				}
				
//				BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//				Date creationDate = new Date(attributes.creationTime().toMillis());
//				Date modifiedDate = new Date(attributes.lastModifiedTime().toMillis());
//				Date accessDate = new Date(attributes.lastAccessTime().toMillis());
//				
//				Metadata metadata = ImageMetadataReader.readMetadata(file);
//				ExifSubIFDDirectory exifMetadata = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
//				FileSystemDirectory fileMetadata = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
//
//				Date date = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME);
//				Date dateOriginal = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//				Date dateDigitized = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
//				
//				Date modifiedDateTag = fileMetadata.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
//				
//				
//				
//				if (null != creationDate) System.out.println("\tCreated: " + format.format(creationDate));
//				if (null != modifiedDate) System.out.println("\tModified: " + format.format(modifiedDate));
//				if (null != accessDate) System.out.println("\tAccesed: " + format.format(accessDate));
//				if (null != date) System.out.println("\tDate: " + format.format(date));
//				if (null != dateOriginal) System.out.println("\tDate original: " + format.format(dateOriginal));
//				if (null != dateDigitized) System.out.println("\tDate digitized: " + format.format(dateDigitized));
//				if (null != modifiedDateTag) System.out.println("\tModified tag: " + format.format(modifiedDateTag));
			}
		}

		logger.info(fileCount + " files processed [" + (System.currentTimeMillis() - start) + "ms]");
		logger.info("END!");
	}
	
	private static Date getFileDate (File file) throws Exception {
		Date date = null;
		
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(file);
			ExifSubIFDDirectory exifMetadata = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
	
			if (null != exifMetadata) {
				date = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				if (null != date) { return date; }
		
				date = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
				if (null != date) { return date; }
				
				date = exifMetadata.getDate(ExifSubIFDDirectory.TAG_DATETIME);
				if (null != date) { return date; }
			}
			
			FileSystemDirectory fileMetadata = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
			if (null != fileMetadata) {
				date = fileMetadata.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
				if (null != date) { return date; }
			}
		}
		catch (Exception exception) {
			logger.warn(file.getName() + ": " + exception.getMessage());
		}
		
		BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		if (null != attributes) {
			FileTime time = attributes.lastModifiedTime();
			if (null != time) {
				date = new Date(attributes.lastModifiedTime().toMillis());
				return date;
			}
		}
		
		return format.parse("1900-01-01 00.00.01");
	}
}
