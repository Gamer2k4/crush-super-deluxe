package main.presentation.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.presentation.common.image.AbstractColorReplacer;
import main.presentation.common.image.ImageUtils;

public abstract class AbstractScreenPanel extends JPanel
{
	private static final long serialVersionUID = 3477872221104928253L;

	// TODO: correct this path
	private static final String BG_IMAGE_PATH = Paths.get(System.getProperty("user.dir")).getParent().toString()
			+ "\\resources\\editor_images\\backgrounds\\";

	private BufferedImage bgBaseImage = null;
	private BufferedImage bgImage;
	private Color bgTint = Color.RED;

	protected AbstractScreenPanel(Dimension dimension, BufferedImage backgroundImage)
	{
		super();
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
		setLayout(null);

		bgBaseImage = ImageUtils.padImage(backgroundImage, dimension);
		updateBackgroundImage();
	}

	protected AbstractScreenPanel(Dimension dimension)
	{
		this(dimension, null);
	}

	protected abstract String getBgFilename();

	public abstract void resetScreen();

	protected void setBackgroundTint(Color tint)
	{
		bgTint = tint;
		updateBackgroundImage();
	}

	protected BufferedImage getBackgroundImage()
	{
		return bgImage;
	}

	private void updateBackgroundImage()
	{
		if (bgBaseImage != null)
		{
			bgImage = bgBaseImage;
		} else
		{
			String path = BG_IMAGE_PATH + getBgFilename();

			try
			{
				bgImage = ImageIO.read(new File(path));
			} catch (IOException e)
			{
				System.out.println("Abstract Screen Panel - Could not load graphic! Path was " + path);
				return;
			}
		}

		bgImage = AbstractColorReplacer.tintImage(bgImage, bgTint);

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (bgImage == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int x = (this.getWidth() - bgImage.getWidth()) / 2;
		int y = (this.getHeight() - bgImage.getHeight()) / 2;

		g2.drawImage(bgImage, x, y, null);
	}
}
