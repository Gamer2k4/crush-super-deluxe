package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ScreenCommand;

public abstract class AbstractLegacyStatsScreenOverview extends AbstractLegacyStatsScreenPanel
{
	private static final long serialVersionUID = -3090205738857180063L;

	public AbstractLegacyStatsScreenOverview(ImageType screenImage, ActionListener listener)
	{
		super(LegacyImageFactory.getInstance().getImage(screenImage));
		setActionListener(listener);

		addClickZone(new Rectangle(coordsButtonTop, buttonDimSmallStats), ScreenCommand.SHOW_MISC_TAB);
		addClickZone(new Rectangle(coordsButtonBottom, buttonDimSmallStats), ScreenCommand.SHOW_MVP_TAB);
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		// paint top teams
		if (!topTeams.isEmpty())
		{
			for (int i = 0; i < topTeams.size(); i++)
			{
				String[] statsStrings = topTeams.get(i);

				// we're trusting the subclasses to put these values in the right places
				int y = 104 + (20 * i);
				paintTextElement(graphics, 73, y, statsStrings[0], FontType.FONT_SMALL, new Color(Integer.valueOf(statsStrings[6])));
				paintTextElement(graphics, 250, y, statsStrings[1], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY); 
				paintTextElement(graphics, 307, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 364, y, statsStrings[3], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 421, y, statsStrings[4], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 469, y, statsStrings[5], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 526, y, "XXX", FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_RED); // TODO: damaged value; also, it should be grey if it's zero (ternary conditional?)
			}
		}
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		super.handleCommand(command);
	}

	@Override
	public void resetScreen()
	{
		super.repaint();
	}
}
