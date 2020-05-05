package test.presentation.legacy.game.sprites;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.presentation.common.ImagePanel;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;

public class ImageDisplayRunner
{
	private static JFrame mainWindow;
	private static ImagePanel imagePanel;
	private static LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	
	public static void main(String[] args)
	{
		defineMainWindow();
		BufferedImage image = imageFactory.getImage(ImageType.TEAM_COLOR_DIAMONDS);
		System.out.println("Image dimensions: [" + image.getWidth() + "x" + image.getHeight() + "]");
		imagePanel.updateImage(image);
	}
	
	private static void defineMainWindow()
	{
		mainWindow = new JFrame();
		mainWindow.setTitle("Image Display");
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		imagePanel = new ImagePanel(640, 400);
		mainWindow.setContentPane(imagePanel);
		
		mainWindow.pack();
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
	}
}
