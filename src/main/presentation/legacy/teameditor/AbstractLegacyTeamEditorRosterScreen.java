package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import main.presentation.common.image.ImageType;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.RegionTriggerType;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.teameditor.common.TeamUpdater;

public abstract class AbstractLegacyTeamEditorRosterScreen extends AbstractLegacyTeamEditorSubScreen
{
	protected int topIndex = 0;
	
	protected static final String PLAYER_LABELS = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static final Point coordsButtonScrollUp = new Point(448, 317);
	private static final Point coordsButtonScrollDown = new Point(505, 317);
	
	private Dimension rosterDimensions;
	
	private static final Dimension buttonDimScroll = new Dimension(45, 20);

	public AbstractLegacyTeamEditorRosterScreen(ImageType foregroundImage, TeamUpdater teamUpdater, ActionListener actionListener)
	{
		super(foregroundImage, teamUpdater, actionListener);
		rosterDimensions = imageFactory.getImageSize(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED);
	}
	
	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		super.paintComponent(graphics);
		graphics.drawImage(teamUpdater.getHelmetImage(), 10, 12, null);
		paintTextElement(graphics, 79, 13, teamUpdater.getTeamCoach(), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_BLACK);
		paintTextElement(graphics, 45, 22, teamUpdater.getTeamName(), FontType.FONT_SMALL_TIGHT, LegacyUiConstants.COLOR_LEGACY_BLACK);
	}
	
	@Override
	protected void defineClickableRegions()
	{
		//TODO: see if i've extracted highlight images for these buttons
		createClickZone(new Rectangle(coordsButtonScrollUp, buttonDimScroll), new ClickableRegion(coordsButtonScrollUp, imageFactory.getImage(ImageType.NO_TYPE), RegionTriggerType.SINGLE_CLICK, ScreenCommand.SCROLL_UP, RegionTriggerType.SINGLE_CLICK));
		createClickZone(new Rectangle(coordsButtonScrollDown, buttonDimScroll), new ClickableRegion(coordsButtonScrollDown, imageFactory.getImage(ImageType.NO_TYPE), RegionTriggerType.SINGLE_CLICK, ScreenCommand.SCROLL_DOWN, RegionTriggerType.SINGLE_CLICK));
	}
	
	@Override
	protected Dimension getScreenDimensions()
	{
		return rosterDimensions;
	}
}
