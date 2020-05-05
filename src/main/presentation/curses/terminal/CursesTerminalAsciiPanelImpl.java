package main.presentation.curses.terminal;

import java.awt.Color;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

public class CursesTerminalAsciiPanelImpl implements CursesTerminal
{
	private JFrame frame;
	private AsciiPanel panel;
	
	public CursesTerminalAsciiPanelImpl()
	{
		panel = new AsciiPanel(81, 25, AsciiFont.CP437_9x16);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public CursesTerminalAsciiPanelImpl(String title, int defaultCloseOperation)
	{
		this();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(defaultCloseOperation);
	}
	
	@Override
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}

	@Override
	public void addKeyListener(KeyListener kl)
	{
		frame.addKeyListener(kl);
	}

	@Override
	public void print(int x, int y, String text, Color color)
	{
		panel.write(text, x, y, color);
	}

	@Override
	public void print(int x, int y, String text, int color)
	{
		panel.write(text, x, y, asciiColor(color));
	}

	@Override
	public void print(int x, int y, String text, Color foreground, Color background)
	{
		panel.write(text, x, y, foreground, background);
	}

	@Override
	public void print(int x, int y, String text, int foreground, int background)
	{
		panel.write(text, x, y, asciiColor(foreground), asciiColor(background));
	}
	
	@Override
	public void clear()
	{
		for (int i = 0; i < 25; i++)
			panel.write("                                                                                ", 0, i);
		refresh();
	}

	@Override
	public void refresh()
	{
		panel.revalidate();
		panel.repaint();
	}

	@Override
	public void close()
	{
		//TODO: doesn't trigger any events, but good enough for now
		frame.setVisible(false);
		frame.dispose();
	}
	
	protected Color asciiColor(int color)
	{
		switch (color)
		{
		case 0:
			return AsciiPanel.black;
		case 1:
			return AsciiPanel.blue;
		case 2:
			return AsciiPanel.green;
		case 3:
			return AsciiPanel.cyan;
		case 4:
			return AsciiPanel.red;
		case 5:
			return AsciiPanel.magenta;
		case 6:
			return AsciiPanel.yellow;
		case 7:
			return AsciiPanel.white;
		case 8:
			return AsciiPanel.brightBlack;
		case 9:
			return AsciiPanel.brightBlue;
		case 10:
			return AsciiPanel.brightGreen;
		case 11:
			return AsciiPanel.brightCyan;
		case 12:
			return AsciiPanel.brightRed;
		case 13:
			return AsciiPanel.brightMagenta;
		case 14:
			return AsciiPanel.brightYellow;
		case 15:
			return AsciiPanel.brightWhite;
		case 16:
			return asciiColor((new Random()).nextInt(14) + 1);
		default:
			throw new IllegalArgumentException("Invalid color value of " + color + ".  Color value must be an integer from 0 to 15.");
		}
	}
}
