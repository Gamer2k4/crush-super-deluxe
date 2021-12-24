package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class Runner
{
	private static final int OFFSET_PNT = 0;
	private static final int OFFSET_DVE = 768;
	private static final int OFFSET_MAP = 2874;
	private static final int OFFSET_FNT = 0;
	private static final int OFFSET_SPT = 0;
	private static final int OFFSET_TIL = 1084;

	private static final int FONT_CHARS = 56;
	private static final int SPRITE_COUNT = 192;
	private static final int SPRITE_COLUMNS = 1;
//	private static final int SPRITE_COLUMNS = 16;
	private static final int TILE_COUNT = 192;

	private static final int MAP_WIDTH = 1152;
	private static final int MAP_HEIGHT = 960;

	private static final int SPRITE_WIDTH = 35;
	private static final int SPRITE_HEIGHT = 30;

	private static final int TILE_WIDTH = 36;
	private static final int TILE_HEIGHT = 30;

//	private static final boolean SHOW_OUTPUT = true;
	private static final boolean SHOW_OUTPUT = false;

	private static JFrame frame;
	private static JFileChooser loadFileImageChooser;
	private static JFileChooser loadFileMapChooser;
	private static JFileChooser loadFileAllChooser;
	private static JFileChooser saveFileChooser;
	private static ImagePanel imagePanel;
	private static ColorMap colorMap;

	private static File currentFile;
	private static BufferedImage currentImage;

	public static void main(String[] args)
	{
		imagePanel = new ImagePanel(1, 1);

		frame = new JFrame("Crush! Deluxe Image Viewer");
		frame.setJMenuBar(createMenuBar());
		frame.setContentPane(imagePanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);

		loadFileImageChooser = createLoadFileImageChooser();
		loadFileMapChooser = createLoadFileMapChooser();
		loadFileAllChooser = createLoadFileAllChooser();
		saveFileChooser = createSaveFileChooser();

		setColorMap(2);

		currentImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
		showImage();
	}

	private static void setColorMap(int mapType)
	{
		if (mapType == 1)
			colorMap = new InGameColorMap();
		else if (mapType == 2)
			colorMap = new GreyArenaColorMap();
		else if (mapType == 3)
			colorMap = new GreenArenaColorMap();
		else
			colorMap = new OutsideGameColorMap();

		if (currentFile != null)
		{
			currentImage = loadImageFile(currentFile, OFFSET_DVE, false, 1, 1);
			showImage();
		}
	}

	private static void loadImage()
	{
		currentImage = null;

		int returnValue = loadFileImageChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			currentFile = loadFileImageChooser.getSelectedFile();
			currentImage = loadImageFile(currentFile, OFFSET_DVE, false, 1, 1);
		}

		showImage();
	}

	private static void loadMap()
	{
		currentImage = null;

		int returnValue = loadFileMapChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			currentFile = loadFileMapChooser.getSelectedFile();
			currentImage = loadImageFile(currentFile, OFFSET_MAP, false, MAP_HEIGHT, MAP_WIDTH, 1, 1);
		}

		showImage();
	}

	private static void loadAny()
	{
		currentImage = null;

		int returnValue = loadFileAllChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			currentFile = loadFileAllChooser.getSelectedFile();

			if (currentFile.getName().toLowerCase().endsWith("pnt"))
				currentImage = loadImageFile(currentFile, OFFSET_PNT, true, 1, 1);
			else if (currentFile.getName().toLowerCase().endsWith("dve"))
				currentImage = loadImageFile(currentFile, OFFSET_DVE, false, 1, 1);
			else if (currentFile.getName().toLowerCase().endsWith("map"))
				currentImage = loadImageFile(currentFile, OFFSET_MAP, false, MAP_HEIGHT, MAP_WIDTH, 1, 1);
			else if (currentFile.getName().toLowerCase().endsWith("fnt"))
				currentImage = loadImageFile(currentFile, OFFSET_FNT, true, 1, FONT_CHARS);
			else if (currentFile.getName().toLowerCase().endsWith("spt"))
				currentImage = loadImageFile(currentFile, OFFSET_SPT, false, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_COUNT / SPRITE_COLUMNS, SPRITE_COLUMNS);
			else if (currentFile.getName().toLowerCase().endsWith("til"))
				currentImage = loadImageFile(currentFile, OFFSET_TIL, true, TILE_HEIGHT, TILE_WIDTH, TILE_COUNT, 1);
			else
				throw new UnsupportedOperationException("Unrecognized file type.");
		}

		showImage();
	}

	private static void saveImage()
	{
		if (currentImage == null)
			return;

		int returnValue = saveFileChooser.showSaveDialog(null);

		if (returnValue != JFileChooser.APPROVE_OPTION)
			return;

		File saveFile = saveFileChooser.getSelectedFile();
		String extension = saveFile.getAbsolutePath();

		if (!extension.contains("."))
			return;

		extension = extension.substring(extension.length() - 3);

		try
		{

			if (extension.equals("jpg"))
				ImageIO.write(currentImage, "JPEG", saveFile);
			else if (extension.equals("png"))
				ImageIO.write(currentImage, "PNG", saveFile);
		} catch (IOException ioe)
		{
			// DO NOTHING
		}
	}

	private static void showImage()
	{
		if (currentImage == null)
			return;

		imagePanel.updateImage(currentImage);

		int windowWidth = currentImage.getWidth();
		int windowHeight = currentImage.getHeight();

		frame.setMinimumSize(new Dimension(windowWidth + 15, windowHeight + 61));
		frame.setMaximumSize(new Dimension(windowWidth + 15, windowHeight + 61));
		frame.setPreferredSize(new Dimension(windowWidth + 15, windowHeight + 61));
		frame.setSize(new Dimension(windowWidth + 15, windowHeight + 61));
	}

	private static JFileChooser createLoadFileImageChooser()
	{
		JFileChooser chooser = new JFileChooser("C:\\Games\\Crush! Deluxe\\DATA");
		chooser.setFileFilter(new LoadFileImageFilter());
		return chooser;
	}

	private static JFileChooser createLoadFileMapChooser()
	{
		JFileChooser chooser = new JFileChooser("C:\\Games\\Crush! Deluxe\\DATA");
		chooser.setFileFilter(new LoadFileMapFilter());
		return chooser;
	}

	private static JFileChooser createLoadFileAllChooser()
	{
		return new JFileChooser("C:\\Games\\Crush! Deluxe\\DATA");
	}

	private static JFileChooser createSaveFileChooser()
	{
		// JFileChooser chooser = new JFileChooser("C:\\");
		JFileChooser chooser = new JFileChooser("H:\\My Projects\\Programming\\Crush! Clone\\resources\\original_game_images");
		chooser.setFileFilter(new SaveFileFilter());
		return chooser;
	}

	private static BufferedImage loadImageFile(File file, int offset, boolean singlePixelsDefined, int finalRows, int finalColumns)
	{
		return prepImageFile(file, offset, singlePixelsDefined, true, -1, -1, finalRows, finalColumns);
	}

	private static BufferedImage loadImageFile(File file, int offset, boolean singlePixelsDefined, int spriteHeight, int spriteWidth,
			int finalRows, int finalColumns)
	{
		return prepImageFile(file, offset, singlePixelsDefined, false, spriteHeight, spriteWidth, finalRows, finalColumns);
	}

	private static BufferedImage prepImageFile(File file, int offset, boolean singlePixelsDefined, boolean getDimensionsFromFile,
			int spriteHeight, int spriteWidth, int finalRows, int finalColumns)
	{
		int height = spriteHeight;
		int width = spriteWidth;

		FileInputStream fis = null;
		DataInputStream dis = null;

		BufferedImage image = null;

		try
		{
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			System.out.println("Total file size to read (in bytes) : " + fis.available());

			if (getDimensionsFromFile)
			{
				width = readShortBytes(dis);
				readShortBytes(dis); // 0, 0 - UNKNOWN
				height = readShortBytes(dis);
				readShortBytes(dis); // 0, 0 - UNKNOWN
			}

			image = loadImageData(dis, offset, singlePixelsDefined, height, width, finalRows, finalColumns);

			dis.close();
			fis.close();

		} catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Exception when reading legacy image file at " + file.getAbsolutePath());
		}

		return image;
	}

	// TODO: necessary considerations
	// MAP files have data before the image data that I actually know what to do with (defining which tiles are displayed on them)
	// MAP files print out at curX + 1, not curX
	// SPT files have a bonus byte at the start of each of their lines
	// TIL files define their tile count in the file (first byte)
	private static BufferedImage loadImageData(DataInputStream dis, int offset, boolean singlePixelsDefined, int spriteHeight,
			int spriteWidth, int finalRows, int finalColumns) throws IOException
	{
		int imageWidth = spriteWidth * finalColumns;
		int imageHeight = spriteHeight * finalRows;
		
		//pretty good chance this is a .spt file, meaning we skip the first byte of each row
		boolean isSprite = (spriteWidth == SPRITE_WIDTH && spriteHeight == SPRITE_HEIGHT && offset == OFFSET_SPT);
		boolean byteSkipped = false;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		int startX = 0; // left side of current sprite
		int startY = 0; // top edge of current sprite
		int curX = 0; // X location in current sprite (NOT the full image)
		int curY = 0; // Y location in current sprite (NOT the full image)

		scanBytes(dis, offset);

		System.out.println("Width is: " + spriteWidth + "\nHeight is: " + spriteHeight);

		while (dis.available() > 0)
		{
			int colorCode = readUnsignedByte(dis);
			
			//hack to deal with that extra byte at the start of each line on the player sprites
			if (curX == 0 && isSprite && !byteSkipped)
			{
				byteSkipped = true;
				continue;
			}
			
			int amount = singlePixelsDefined ? 1 : readUnsignedByte(dis);
			
			Color color = Color.CYAN;
			
			try
			{
				color = colorMap.getColor(colorCode);
			} catch (IllegalArgumentException iae)
			{
				System.out.println("[" + (startX + curX) + ", " + (startY + curY) + "] has an unrecognized color code of " + colorCode);
				System.exit(1);
			}
					

			if (SHOW_OUTPUT)
				System.out.println("Printing color(" + colorCode + ") " + amount + " times.");

			for (int j = 0; j < amount; j++)
			{
				if (SHOW_OUTPUT)
					System.out.println("Printing (" + (startX + curX) + ", " + (startY + curY) + ")");

				image.setRGB(startX + curX, startY + curY, color.getRGB());

				curX++;

				if (curX >= spriteWidth) // move to the next row for this particular sprite
				{
					curX = 0;
					curY++;
					byteSkipped = false;	//sprite byte skipping hack
				}

				if (curY >= spriteHeight) // past the bottom of the sprite, so move to the next sprite in the row
				{
					curX = 0;
					curY = 0;
					startX = startX + spriteWidth;
				}

				if (startX >= imageWidth) // if after completing a sprite (as above), we're past the edge of the image, move to the next row
				{
					curX = 0;
					curY = 0;
					startX = 0;
					startY = startY + spriteHeight;
				}
			}
		}

		return image;
	}

	private static void scanBytes(DataInputStream dis, int bytesToScan) throws IOException
	{
		for (int i = 0; i < bytesToScan; i++)
			dis.readByte();
	}

	private static int readUnsignedByte(DataInputStream dis) throws IOException
	{
		return dis.readByte() & 0xFF;
	}

	private static short readShortBytes(DataInputStream dis) throws IOException
	{
		int addend1 = readUnsignedByte(dis);
		int addend2 = 256 * readUnsignedByte(dis);

		return (short) (addend1 + addend2);
	}

	private static JMenuBar createMenuBar()
	{
		ActionListener al = createMenuListener();

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenu colorMenu = new JMenu("Color");

		menuBar.add(fileMenu);
		menuBar.add(colorMenu);

		JMenuItem loadImageItem = new JMenuItem("Load DVE File...");
		JMenuItem loadMapItem = new JMenuItem("Load MAP File...");
		JMenuItem loadAllItem = new JMenuItem("Load any File...");
		JMenuItem saveItem = new JMenuItem("Save as...");
		JMenuItem exitItem = new JMenuItem("Exit");

		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem gameColors = new JRadioButtonMenuItem("In-Game");
		group.add(gameColors);
		colorMenu.add(gameColors);
		
		JRadioButtonMenuItem greyColors = new JRadioButtonMenuItem("Grey Arena");
		greyColors.setSelected(true);
		group.add(greyColors);
		colorMenu.add(greyColors);
		
		JRadioButtonMenuItem greenColors = new JRadioButtonMenuItem("Green Arena");
		group.add(greenColors);
		colorMenu.add(greenColors);

		JRadioButtonMenuItem introColors = new JRadioButtonMenuItem("Opening/Credits");
		group.add(introColors);
		colorMenu.add(introColors);

		loadImageItem.setActionCommand("loadImage");
		loadMapItem.setActionCommand("loadMap");
		loadAllItem.setActionCommand("loadAll");
		saveItem.setActionCommand("save");
		exitItem.setActionCommand("exit");
		gameColors.setActionCommand("gameColor");
		greyColors.setActionCommand("greyColor");
		greenColors.setActionCommand("greenColor");
		introColors.setActionCommand("introColor");

		loadImageItem.addActionListener(al);
		loadMapItem.addActionListener(al);
		loadAllItem.addActionListener(al);
		saveItem.addActionListener(al);
		exitItem.addActionListener(al);
		gameColors.addActionListener(al);
		greenColors.addActionListener(al);
		introColors.addActionListener(al);

		fileMenu.add(loadImageItem);
		fileMenu.add(loadMapItem);
		fileMenu.add(loadAllItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);

		return menuBar;
	}

	public static ActionListener createMenuListener()
	{
		ActionListener listener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent event)
			{
				String command = event.getActionCommand();

				if (command.equals("exit"))
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				if (command.equals("loadImage"))
					loadImage();
				if (command.equals("loadMap"))
					loadMap();
				if (command.equals("loadAll"))
					loadAny();
				if (command.equals("save"))
					saveImage();
				if (command.equals("gameColor"))
					setColorMap(1);
				if (command.equals("greyColor"))
					setColorMap(2);
				if (command.equals("greenColor"))
					setColorMap(3);
				if (command.equals("introColor"))
					setColorMap(4);
			}
		};

		return listener;
	}
}
