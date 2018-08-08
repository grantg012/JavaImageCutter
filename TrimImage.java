import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class TrimImage 
{
	public static void main(String[] args) {
		if(args.length >= 1) {
			BufferedImage img = loadImage(args[0]);
			if(img == null)
				return; 
			
			img = trimImage(img);
			
			saveImage(img, args[0]);
		} else
			System.out.println("Not Picture Arguments passed! Nothing was done. "); 
	}

    private static BufferedImage loadImage(String path) {
        BufferedImage img = null;
	    try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
			System.out.println("Unable to read image. "); 
			System.out.print("Message: "); 
			System.out.println(e.getMessage()); 
        }
		return img;
    }
	
	private static void saveImage(BufferedImage img, String path) {
		int dotSpot = path.indexOf('.');
		String newPath = path.substring(0, dotSpot) + "_trimmed" + path.substring(dotSpot);
		File outputfile = new File(newPath);
		try {
			ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
			System.out.println("Unable to save image. "); 
			System.out.print("Message: "); 
			System.out.println(e.getMessage()); 
        }
	}
	
	public static boolean isNotCuttablePixel(int pixel) {
		return !pixelIsWhite(pixel) && !(((pixel & 0xff000000) >> 24) == 0);
	}
	
	public static boolean pixelIsWhite(int pixel) {
		return ((pixel & 0x00ff0000) >> 16 == 255) &&
			((pixel & 0x0000ff00) >> 8 == 255) &&
			((pixel & 0x000000ff) == 255);
	}
	
	public static BufferedImage trimImage(BufferedImage image) {
        int leftCut = 0;
        for(int xLeft = 0; xLeft < image.getWidth(); xLeft++)
			for(int yLeft = 0; yLeft < image.getHeight(); yLeft++)
                if(isNotCuttablePixel(image.getRGB(xLeft, yLeft))) {
                    leftCut = xLeft;
                    // GoTo endOfLeft
					yLeft += image.getHeight();
					xLeft += image.getWidth();
                }
		// endOfLeft:

		int topCut = 0;
        for(int yTop = 0; yTop < image.getHeight(); yTop++)
			for(int xTop = leftCut; xTop < image.getWidth(); xTop++)
                if(isNotCuttablePixel(image.getRGB(xTop, yTop))) {
                    topCut = yTop;
                    // GoTo endOfTop
					yTop += image.getHeight();
					xTop += image.getWidth();
                }
		// endOfTop:
		
		int rightCut = image.getWidth() - 1;
        for(int xRight = image.getWidth() - 1; xRight >= leftCut; xRight--)
			for(int yRight = topCut; yRight < image.getHeight(); yRight++)
                if(isNotCuttablePixel(image.getRGB(xRight, yRight))) {
                    rightCut = xRight;
                    // GoTo endOfRight
					yRight += image.getHeight();
					xRight -= image.getWidth();
                }
		// endOfRight:

		int bottomCut = image.getHeight() - 1;
        for(int yBottom = image.getHeight() - 1; yBottom >= topCut; yBottom--)
			for(int xBottom = leftCut; xBottom <= rightCut; xBottom++)
                if(isNotCuttablePixel(image.getRGB(xBottom, yBottom))) {
                    bottomCut = yBottom;
                    // GoTo endOfBottom
					yBottom -= image.getHeight();
					xBottom += image.getWidth();
                }
		// endOfBottom:
		
		// BufferedImage finalImage = new BufferedImage(rightCut - leftCut + 1, bottomCut - topCut + 1);
		return image.getSubimage(leftCut, topCut, (rightCut - leftCut + 1), (bottomCut - topCut + 1)); 
        
		/* 
		For x = 0 To finalImage.getWidth() - 1
            For y = 0 To finalImage.getHeight() - 1
                finalImage.SetPixel(x, y, image.getRGB(x + leftCut, y + topCut))
            Next
        Next
		*/ 
	}
}