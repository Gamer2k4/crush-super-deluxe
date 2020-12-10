package main.presentation.legacy.teameditor;

import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import main.presentation.common.image.ImageType;
import main.presentation.legacy.framework.AbstractLegacyScreen;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class AbstractLegacyTeamEditorSubScreen extends AbstractLegacyScreen
{
	protected TeamUpdater teamUpdater;
	private BufferedImage foregroundImage;
	
	public AbstractLegacyTeamEditorSubScreen(ImageType foregroundImage, TeamUpdater teamUpdater, ActionListener listener)
	{
		super(listener, ImageType.NO_TYPE);
		this.foregroundImage = imageFactory.getImage(foregroundImage);
		this.teamUpdater = teamUpdater;
	}
	
	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		graphics.drawImage(foregroundImage, 0, 0, null);
//		graphics.drawImage(clickMap, 0, 0, null);
	}
}
