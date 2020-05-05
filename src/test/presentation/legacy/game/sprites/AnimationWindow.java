package test.presentation.legacy.game.sprites;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.presentation.common.ImagePanel;
import main.presentation.legacy.game.sprites.Sprite;

public class AnimationWindow extends JFrame
{
	private static final long serialVersionUID = 103715580171680471L;
	
	private static final int SPRITE_DIMENSION = 32;
	
	private Sprite sprite;
	
	private ImagePanel panel;
	
	public AnimationWindow()
	{
		setTitle("Sprite Animation Test");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(SPRITE_DIMENSION, SPRITE_DIMENSION));
		setResizable(false);
		
		panel = new ImagePanel(SPRITE_DIMENSION, SPRITE_DIMENSION);
		setContentPane(panel);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}
	
	public void refresh()
	{
		panel.updateImage(sprite.getCurrentFrame());
	}
}
