package main.presentation.curses.terminal;

import java.awt.Color;
import java.awt.event.KeyListener;

public interface CursesTerminal
{
	public void setTitle(String title);
	public void addKeyListener(KeyListener kl);
	public void print(int x, int y, String text, Color color);
	public void print(int x, int y, String text, int color);
	public void print(int x, int y, String text, Color foreground, Color background);
	public void print(int x, int y, String text, int foreground, int background);
	public void clear();
	public void refresh();
	public void close();
}
