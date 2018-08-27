package org.chronos.utils.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.chronos.utils.model.ImageInfo;

public class Utils {

	private static final Logger logger = Logger.getLogger(Utils.class);
	
	public static String getHashForFile(File file) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);

        // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
        // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.

//        String hash = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
        String hash = DigestUtils.sha256Hex(IOUtils.toByteArray(fileInputStream));
        fileInputStream.close();
        
        return hash;
    }
	
	
	public static ImageInfo getImageInfo(File file) throws Exception {
		ImageInfo info = new ImageInfo();
		
		String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
		
		boolean sampled = false;
		try {
	        if("jpg".equals(extension) || "jpeg".equals(extension)){
	    		BufferedImage image = ImageIO.read(file);
	    		int height = image.getHeight();
	    		info.setHeight(height);
	    		int width = image.getWidth();
	    		info.setWidth(width);
	    		
	    		if(height<100 || width<100 || height<320 && width < 320) {
	    			return null;
	    		}
	    		   
	    		List<Integer> pixelSamples = new ArrayList<>();
	    		for (int i = 0; i < width; i = i + Constants.pixelSampleStep) {
	    			for (int j = 0; j < height; j = j + Constants.pixelSampleStep) {
	    				pixelSamples.add(image.getRGB(i,j));
	    			}
	    		}
	    		
	    		String sample = "";
	    		for (Integer pixelSample : pixelSamples) {
	    			sample = sample + pixelSample;
	    		}
	    		
	    		info.setSampleHash(DigestUtils.sha256Hex(sample));
	    		sampled = true;
	        }
		}
		catch (Exception exception) {
			logger.warn("Error while trying to sample \"" + file.getPath() + "\" (Reason: " + exception.getMessage() + ")");
		}
		
        if(!sampled){
        	info.setSize("" + file.length());
        	info.setSampleHash(getHashForFile(file));
        }
		
		return info;
	}

}
