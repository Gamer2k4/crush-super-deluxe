package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import main.data.entities.Arena;
import main.data.factory.SimpleArenaFactory;
import main.presentation.common.image.ArenaImageGenerator;

public class ArenaDisplayPanel extends JPanel
{
	private static final long serialVersionUID = -4106567243482679386L;
	
	private Arena arena;	
	private static final int TILE_SIZE = 3;
	private static final int ARENA_SIDE_LENGTH = 30;

	public ArenaDisplayPanel(int arenaNumber)
	{
		setBackground(Color.BLACK);
		setArena(arenaNumber);
		
		this.setMinimumSize(new Dimension(ARENA_SIDE_LENGTH * TILE_SIZE + 2, ARENA_SIDE_LENGTH * TILE_SIZE + 2));
		this.setMaximumSize(new Dimension(ARENA_SIDE_LENGTH * TILE_SIZE + 2, ARENA_SIDE_LENGTH * TILE_SIZE + 2));
		this.setPreferredSize(new Dimension(ARENA_SIDE_LENGTH * TILE_SIZE + 2, ARENA_SIDE_LENGTH * TILE_SIZE + 2));
	}
	
	public void setArena(int arenaNumber)
	{
		arena = SimpleArenaFactory.getInstance().generateArena(arenaNumber);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < ARENA_SIDE_LENGTH; i++)
		{
			for (int j = 0; j < ARENA_SIDE_LENGTH; j++)
			{
				ArenaImageGenerator.drawTile(g2, j, i, arena.getTile(i, j), TILE_SIZE);
			}
		}
	}
	
	public BufferedImage getArenaImage(int tileSize)
	{
		return ArenaImageGenerator.getArenaImage(arena, ARENA_SIDE_LENGTH, tileSize);
	}
}
