package main.presentation.common;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class PaddedPanel extends JPanel
{
	private static final long serialVersionUID = -6205007216890470873L;

	private JPanel contentPanel;

	public PaddedPanel(Dimension dimension, JPanel contentPanel)
	{
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
		setLayout(null);
		setBackground(Color.BLACK);

		centerContentPanel(contentPanel);
	}

	private void centerContentPanel(JPanel panelToCenter)
	{
		if (panelToCenter.getWidth() > getWidth() || panelToCenter.getHeight() > getHeight())
			throw new IllegalArgumentException("Cannot pad panel; panel is too large.");

		int startX = (getWidth() - panelToCenter.getWidth()) / 2;
		int startY = (getHeight() - panelToCenter.getHeight()) / 2;

		panelToCenter.setLocation(startX, startY);
		add(panelToCenter);

		contentPanel = panelToCenter;
	}

	public JPanel getPanel()
	{
		return contentPanel;
	}
}
