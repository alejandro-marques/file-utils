package org.chronos.utils.common;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class Utils {
	
	
	public static String getHashForFile(File file) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);

        // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
        // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.

//        String hash = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
        String hash = DigestUtils.sha256Hex(IOUtils.toByteArray(fileInputStream));
        fileInputStream.close();
        
        return hash;
    }

}
