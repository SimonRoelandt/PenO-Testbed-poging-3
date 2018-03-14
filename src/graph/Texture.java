package graph;

import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	public int id;
	private int width;
	private int height;
	
	public Texture(String filename) {
		BufferedImage image;
		try{
			image = ImageIO.read(new FileInputStream(filename));
			width = image.getWidth();
			height = image.getHeight();
			
			int[] pixelsraw = new int[width * height];
			pixelsraw = image.getRGB(0, 0, width, height, null, 0, width);
			
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
			
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int pixel = pixelsraw[i*width + j];
					pixels.put((byte)((pixel >> 16) & 0xFF));   //RED
					pixels.put((byte)((pixel >> 8) & 0xFF));	//GREEN
					pixels.put((byte)((pixel >> 0) & 0xFF));	//BLUE
					pixels.put((byte)((pixel >> 24) & 0xFF));	//ALPHA
				}
			}
			
			pixels.flip();
			
			id = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, id);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
		} catch(IOException e) {
			System.out.println("ERROR 404 DIT WERKT NI xD");
			e.printStackTrace();
		}
		
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
}
