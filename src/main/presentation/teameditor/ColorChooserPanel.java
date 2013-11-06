package main.presentation.teameditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ColorChooserPanel extends JPanel
{
	private static final long serialVersionUID = 5257498328063174480L;
	
	private Color color;

	public ColorChooserPanel(Color color, MouseListener mouseListener, String componentName)
	{
		setColor(color);
		
		this.addMouseListener(mouseListener);
		this.setName(componentName);
		
		this.setMinimumSize(new Dimension(20, 20));
		this.setMaximumSize(new Dimension(20, 20));
		this.setPreferredSize(new Dimension(20, 20));
	}
	
	public void setColor(Color color)
	{
		this.color = color;
		this.setBackground(color);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		repaint();
	}
	
	public Color getColor()
	{
		return color;
	}
}
