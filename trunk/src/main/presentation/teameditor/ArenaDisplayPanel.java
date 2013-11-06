package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import main.data.entities.Arena;

public class ArenaDisplayPanel extends JPanel
{
	private static final long serialVersionUID = -4106567243482679386L;
	
	private Arena arena;	
	private static final int TILE_SIZE = 3;

	public ArenaDisplayPanel(int arenaNumber)
	{
		setArena(arenaNumber);
		
		this.setMinimumSize(new Dimension(30 * TILE_SIZE + 2, 30 * TILE_SIZE + 2));
		this.setMaximumSize(new Dimension(30 * TILE_SIZE + 2, 30 * TILE_SIZE + 2));
		this.setPreferredSize(new Dimension(30 * TILE_SIZE + 2, 30 * TILE_SIZE + 2));
	}
	
	public void setArena(int arenaNumber)
	{
		arena = Arena.generateArena(arenaNumber);
		repaint();
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				drawTile(g2, j, i, arena.getTile(i, j));
			}
		}
	}
	
	private void drawTile(Graphics2D g2, int x, int y, int tile)
	{
		Color fillColor = Color.BLACK;
		
		if (tile == Arena.TILE_TELE || tile == Arena.TILE_GOAL)
			fillColor = Color.BLUE;
		else if (tile == Arena.TILE_PAD)
			fillColor = Color.CYAN;
		else if (tile == Arena.TILE_WALL || tile == Arena.TILE_BIN)
			fillColor = Color.GRAY;
		
		Rectangle tileRect = new Rectangle(TILE_SIZE * x, TILE_SIZE * y, TILE_SIZE, TILE_SIZE);
		
		g2.setColor(fillColor);
		g2.fill(tileRect);
	}
}
