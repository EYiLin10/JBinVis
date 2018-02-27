package jbinvis.backend;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jbinvis.renderer.CanvasTexture;

public class ByteToImage {
    private BufferedImage image;
    
	public ByteToImage(int i, int j) {
		image = new BufferedImage(i, j, BufferedImage.TYPE_INT_RGB);
	}

	public void put(byte[] digraph) {
		int w = image.getWidth();
		int length = image.getWidth() * image.getHeight();
		length = digraph.length < length ? digraph.length : length;
		
		int value;
		
		for(int i=0;i<length;i++) {
			value = (digraph[i] & 0xFF)<<8;
		    image.setRGB(i % w, i/w, value);
		}
	}
	
	public void put(CanvasTexture texture) {
		int w = image.getWidth();
		int length = image.getWidth() * image.getHeight();
		int value;
		int pixel;
		
		for(int i=0;i<length;i++) {
			pixel = texture.getPixel(i % w, i / w);
			value = (pixel & 0xFF00);
		    image.setRGB(i % w, i/w, value);
		}
	}
	
	public BufferedImage getImage() { return image; }

	public void save(String name) {
		File file = new File(name);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
