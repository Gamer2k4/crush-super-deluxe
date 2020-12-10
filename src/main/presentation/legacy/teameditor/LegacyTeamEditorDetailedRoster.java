package main.presentation.legacy.teameditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import main.data.entities.Player;
import main.data.entities.Skill;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyPlayerTextFactory;
import main.presentation.legacy.common.LegacyTextElement;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.legacy.framework.KeyCommand;
import main.presentation.legacy.framework.ScreenCommand;
import main.presentation.teameditor.common.GUIPlayerAttributes;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorDetailedRoster extends AbstractLegacyTeamEditorRosterScreen
{
	public LegacyTeamEditorDetailedRoster(TeamUpdater teamUpdater, ActionListener actionListener)
	{
		super(ImageType.SCREEN_TEAM_EDITOR_ROSTER_DETAILED, teamUpdater, actionListener);
	}
	
	@Override
	protected void defineClickableRegions()
	{
		super.defineClickableRegions();
	}

	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		super.paintComponent(graphics);
		paintImages(graphics);
		paintText(graphics);
	}

	protected void paintImages(Graphics2D graphics)
	{
		Player player = teamUpdater.getPlayer(teamUpdater.getCurrentPlayerIndex());
		
		if (player != null)
			graphics.drawImage(teamUpdater.getPlayerImage(player.getRace()), 17, 82, null);
	}

	protected void paintText(Graphics2D graphics)
	{
		Player player = teamUpdater.getPlayer(teamUpdater.getCurrentPlayerIndex());
		
		String index = PLAYER_LABELS.charAt(teamUpdater.getCurrentPlayerIndex()) + "";
		String name = GUIPlayerAttributes.getNameEmpty(player);
		
		paintTextElement(graphics, 22, 65, index, FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
		paintTextElement(graphics, 46, 65, name, FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GREY);
		
		if (player == null)
			return;
		
		LegacyPlayerTextFactory.setPlayer(player);
		
		//work on the text box width and location
		paintPaddedTextElement(graphics, 111, 95, 51, GUIPlayerAttributes.getRank(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintPaddedTextElement(graphics, 116, 112, 41, GUIPlayerAttributes.getSeasons(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintPaddedTextElement(graphics, 116, 129, 41, GUIPlayerAttributes.getRating(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);	//TODO: still a little off-center at times
		paintPaddedTextElement(graphics, 101, 146, 71, GUIPlayerAttributes.getStatus(player), FontType.FONT_SMALL2, LegacyUiConstants.COLOR_LEGACY_GOLD);	//TODO: this might have a different color based on its value
		
		for (int i = 0; i < 8; i++)
		{
			LegacyTextElement text = LegacyPlayerTextFactory.getColoredAttributeWithModifiers(i, LegacyUiConstants.COLOR_LEGACY_GOLD, LegacyUiConstants.COLOR_LEGACY_GREEN);
			graphics.drawImage(fontFactory.generateString(text), 17 + (20 * i), 172, null);
		}
		
		paintSkillsTextBox(graphics, player);
	}

	private void paintSkillsTextBox(Graphics2D graphics, Player player)
	{
		// TODO Auto-generated method stub
		BufferedImage skillsTextBox = ImageUtils.createBlankBufferedImage(new Dimension(200, 70));	
		String currentLine = "";
		int startY = 0;
		
		for (Skill skill : player.getSkills())
		{
			String skillText = skill.getName().toUpperCase() + ", ";
			String newLine = currentLine + skillText;
			
			if (newLine.length() > 37)	//TODO: verify this is the right value (may be 1 or 2 more)
			{
				LegacyTextElement text = new LegacyTextElement(currentLine, LegacyUiConstants.COLOR_LEGACY_GREY, FontType.FONT_SMALL2);
				skillsTextBox.getGraphics().drawImage(fontFactory.generateString(text), 0, startY, null);
				currentLine = skillText;
				startY += 8;
			}
			else
			{
				currentLine = newLine;
			}
		}
		
		graphics.drawImage(skillsTextBox, 17, 192, null);
		
		//note that this should be extracted to a helper class, since it needs to take in arbitrary dimensions if the pool draft screen is to make use of it 
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!isActive)
			return;
		
		if (ScreenCommand.SCROLL_UP.equals(command))
			teamUpdater.selectPreviousPlayer();
		if (ScreenCommand.SCROLL_DOWN.equals(command))
			teamUpdater.selectNextPlayer();
	}

	@Override
	public void resetScreen()
	{
		topIndex = 0;
	}

	@Override
	protected void handleKeyCommand(KeyCommand command) {}
}
