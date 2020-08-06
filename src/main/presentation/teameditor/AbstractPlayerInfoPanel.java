package main.presentation.teameditor;

import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.data.entities.Player;
import main.presentation.common.ImagePanel;

public abstract class AbstractPlayerInfoPanel extends JPanel
{
	private static final long serialVersionUID = -4241210602834566733L;

	protected ImagePanel imagePanel;
	
	protected int playerImageSize;
	protected int playerLabelWidth;
	protected int playerLabelHeight;
	protected int playerLabelFontSize;
	
	public AbstractPlayerInfoPanel(int imageSize, int labelWidth, int labelHeight, int labelFontSize)
	{
		playerImageSize = imageSize;
		playerLabelWidth = labelWidth;
		playerLabelHeight = labelHeight;
		playerLabelFontSize = labelFontSize;
		
		imagePanel = createImagePanel();
	}

	protected JLabel createPlayerPaneTextLabel(String text, JLabel precedingLabel)
	{
		JLabel label = createPlayerPaneLabel(precedingLabel.getX(), precedingLabel.getY() + playerLabelHeight + (playerLabelFontSize / 3), true);
		label.setText(text);
		return label;
	}
	
	protected JLabel createPlayerPaneValueLabel(JLabel precedingLabel)
	{
		return createPlayerPaneLabel(precedingLabel.getX(), precedingLabel.getY() + playerLabelHeight, false);
	}
	
	private JLabel createPlayerPaneLabel(int X, int Y, boolean bold)
	{
		JLabel label = setLabelFontSize(new JLabel(), playerLabelFontSize, bold);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setLocation(X, Y);
		label.setSize(playerLabelWidth, playerLabelHeight);
		return label;
	}

	protected JLabel setLabelFontSize(JLabel label, int fontSize, boolean isBold)
	{
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		if (!isBold)
		{
			labelFont = label.getFont();
			label.setFont(labelFont.deriveFont(labelFont.getStyle() ^ Font.BOLD));
		}

		return label;
	}

	protected ImagePanel createImagePanel()
	{
		ImagePanel panel = new ImagePanel(playerImageSize, playerImageSize);

		return panel;
	}

	protected void setRosterImage(Player player, BufferedImage playerImage)
	{
		if (player == null)
		{
			imagePanel.updateImage(null);
			return;
		}

		imagePanel.updateImage(playerImage);
	}
}
