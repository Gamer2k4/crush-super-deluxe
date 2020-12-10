package main.presentation.legacy.stats.gamestats;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.ScreenCommand;

public abstract class AbstractLegacyStatsScreenChecking extends AbstractLegacyStatsScreenPanel
{
	private static final long serialVersionUID = -3090205738857180063L;

	public AbstractLegacyStatsScreenChecking(ImageType screenImage, ActionListener listener)
	{
		super(LegacyImageFactory.getInstance().getImage(screenImage));
		setActionListener(listener);
		
		addClickZone(new Rectangle(coordsButtonTop, buttonDimSmallStats), ScreenCommand.SHOW_CHECK_TAB);
		addClickZone(new Rectangle(coordsButtonBottom, buttonDimSmallStats), ScreenCommand.SHOW_SACK_TAB);
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

				//we're trusting the subclasses to put these values in the right places
				int y = 104 + (20 * i);
				paintTextElement(graphics, 73, y, statsStrings[0], FontType.FONT_SMALL, new Color(Integer.valueOf(statsStrings[6])));
				paintTextElement(graphics, 250, y, statsStrings[1], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 307, y, statsStrings[2], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 364, y, formatAverage(statsStrings[3]), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 421, y, statsStrings[4], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
				paintTextElement(graphics, 478, y, statsStrings[5], FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
			}
		}
	}
	
	//TODO: this is used on team record screens, not here; leaving it in to keep the file in a "warning" state
	private List<String> splitAverage(String stat)
	{
		List<String> split = new ArrayList<String>();
		String part1 = "";
		String part2 = stat;
		
		if (stat.length() == 4 && stat.charAt(1) == '.')
		{
			if (stat.charAt(0) != '0')
				part1 = "" + stat.charAt(0);
			
			part2 = stat.substring(2);
		}
		
		split.add(part1);
		split.add(part2);
		
		return split;
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
