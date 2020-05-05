package main.presentation.legacy.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyFontFactory;
import main.presentation.legacy.common.LegacyUiConstants;

public class LegacyTextField
{
	public static final String SUBMIT_ACTION = "SUBMIT_TEXT_FIELD";
	public static final String REVERT_ACTION = "REVERT_TEXT_FIELD";
	
	private static final Color TEXT_COLOR = LegacyUiConstants.COLOR_LEGACY_GREY;
	
	public static final String VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .";
	public static final String ENTER = "ENTER";
	public static final String ESCAPE = "ESCAPE";
	
	private JButton submitButton = new JButton();
	private JButton revertButton = new JButton();
	
	private boolean isActive = false;
	private String currentText = "";
	
	private int maxLength;
	private FontType font;
	
	private BufferedImage cursor;
	private BufferedImage textImage;
	
	public LegacyTextField(String tag, int maxLength, FontType font, ActionListener updateListener)
	{
		this.maxLength = maxLength;
		this.font = font;
		
		createCursor();
		
		submitButton.setActionCommand(SUBMIT_ACTION + tag);
		revertButton.setActionCommand(REVERT_ACTION);
		
		submitButton.addActionListener(updateListener);
		revertButton.addActionListener(updateListener);
		
		textImage = ImageUtils.createBlankBufferedImage(new Dimension(1, 1));
	}

	private void createCursor()
	{
		int height = font.getSize();
		cursor = ImageUtils.createBlankBufferedImage(new Dimension(height, height), TEXT_COLOR);
	}

	public boolean isActive()
	{
		return isActive;
	}
	
	public void activate()
	{
		currentText = "";
		isActive = true;
		updateTextImage();
	}
	
	public void deactivate()
	{
		isActive = false;
	}
	
	private void updateTextImage()
	{
		int height = font.getSize();
		int width = (font.getSize() + font.getPadding()) * maxLength;
				
		textImage = ImageUtils.createBlankBufferedImage(new Dimension(width, height), Color.BLACK);
		BufferedImage text = LegacyFontFactory.getInstance().generateString(currentText, TEXT_COLOR, font);
		ImageUtils.copySrcIntoDstAt(text, textImage, 0, 0);
		
		int negativePadding = getSpaceCountInCurrentText() * 3;
		
		if (isActive())
			ImageUtils.copySrcIntoDstAt(cursor, textImage, text.getWidth() - negativePadding, 0);
		
	}
	
	private int getSpaceCountInCurrentText()
	{
		int count = 0;
		
		for (int i = 0; i < currentText.length(); i++)
		{
			if (LegacyFontFactory.isShortChar(currentText.charAt(i)))
				count++;
		}
		
		return count;
	}

	public BufferedImage getTextImage()
	{
		return textImage;
	}
	
	public String getText()
	{
		return currentText;
	}
	
	public void pressKey(ActionEvent keyPress)
	{		
		String command = keyPress.getActionCommand();
		int keyCode = -1;
		
		if (ENTER.equals(command))
			keyCode = KeyEvent.VK_ENTER;
		if (ESCAPE.equals(command))
			keyCode = KeyEvent.VK_ESCAPE;
		
		if (VALID_CHARS.contains(command))
			currentText = currentText + command;
		
		if (currentText.length() >= maxLength || keyCode == KeyEvent.VK_ENTER)
		{
			deactivate();
			submitButton.doClick();
		}
		
		if (keyCode == KeyEvent.VK_ESCAPE)
		{
			deactivate();
			revertButton.doClick();
		}
		
		updateTextImage();
	}
}
