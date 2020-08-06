package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;

public abstract class AbstractLegacyTeamEditorRosterScreen extends LegacyTeamEditorScreenDecorator
{
	private static final long serialVersionUID = -4974085219516823723L;
	
	protected int topIndex = 0;
	
	protected static final String PLAYER_LABELS = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static final Point coordsButtonScrollUp = new Point(448, 317);
	private static final Point coordsButtonScrollDown = new Point(505, 317);
	
	private static final Dimension buttonDimScroll = new Dimension(45, 20);

	public AbstractLegacyTeamEditorRosterScreen(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
		
		addClickZone(new Rectangle(coordsButtonScrollUp, buttonDimScroll), ScreenCommand.SCROLL_UP);
		addClickZone(new Rectangle(coordsButtonScrollDown, buttonDimScroll), ScreenCommand.SCROLL_DOWN);
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		graphics.drawImage(teamUpdater.getHelmetImage(), 393, 22, null);
	}
	
	@Override
	protected void paintText(Graphics2D graphics)
	{
		paintTextElement(graphics, 462, 23, teamUpdater.getTeamCoach(), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_BLACK);
		paintTextElement(graphics, 428, 32, teamUpdater.getTeamName(), FontType.FONT_SMALL_TIGHT, LegacyUiConstants.COLOR_LEGACY_BLACK);
	}
}
