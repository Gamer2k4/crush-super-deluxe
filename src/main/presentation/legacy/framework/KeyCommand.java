package main.presentation.legacy.framework;

import java.awt.event.KeyEvent;

public class KeyCommand
{
	public static final String VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .";
	public static final String ENTER = "ENTER";
	public static final String ESCAPE = "ESCAPE";
	public static final String BACKSPACE = "BACKSPACE";
	
	private String key = null;
	
	public KeyCommand(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public static KeyCommand fromKeyEvent(KeyEvent ke)
	{
		if (ke.getKeyCode() == KeyEvent.VK_ENTER)
			return new KeyCommand(ENTER);

		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
			return new KeyCommand(ESCAPE);
		
		if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			return new KeyCommand(BACKSPACE);
		
		for (int i = 0; i < VALID_CHARS.length(); i++)
		{
			char character = VALID_CHARS.charAt(i);
			
			if (Character.toLowerCase(ke.getKeyChar()) == character)
				return new KeyCommand("" + character);
		}
		
		return null;
	}
}
