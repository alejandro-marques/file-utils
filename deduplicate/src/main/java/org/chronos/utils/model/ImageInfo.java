package org.chronos.utils.model;

public class ImageInfo {
	
	private int height;
	private int width;
	private String size;
	private String sampleHash;
	
	
	public int getHeight() { return height; }
	public void setHeight(int height) { this.height = height; }
	
	public int getWidth() { return width; }
	public void setWidth(int width) { this.width = width; }

	
	public String getSize() { return null == size? height + "x" + width : size; }
	public void setSize(String size) { this.size = size; }
	
	public String getSampleHash() { return sampleHash; }
	public void setSampleHash(String sampleHash) { this.sampleHash = sampleHash; }
}