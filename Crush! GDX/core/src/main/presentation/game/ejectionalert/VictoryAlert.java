package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Team;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class VictoryAlert extends PopupAlert
{
	private List<GameText> text = new ArrayList<GameText>();
	
	public VictoryAlert(Data data, Event event)
	{
		super(640, 320);
		//TODO: set partial transparency if possible
		defineGameTexts(data, event);
	}

	private void defineGameTexts(Data data, Event event)
	{
		GameText victory = new GameText(FontType.FONT_HUGE, new Point(0, 0), LegacyUiConstants.COLOR_LEGACY_WHITE, "VICTORY");
		int messagePadding = textBox.getWidth() - victory.getStringPixelLength();
		victory.setCoords(new Point(messagePadding / 2, 252));
		text.add(victory);
		
		Team team = data.getTeam(event.flags[0]);
		GameText teamName = new GameText(FontType.FONT_HUGE, new Point(0, 0), team.teamColors[0], team.teamName);
		messagePadding = textBox.getWidth() - teamName.getStringPixelLength();
		teamName.setCoords(new Point(messagePadding / 2, 297));
		text.add(teamName);
	}
	
	@Override
	protected List<GameText> getGameTexts()
	{
		return text;
	}
	
	@Override
	protected Point getTextBoxCoords()
	{
		return new Point(0, 80);
	}

	@Override
	protected Point getOffsetTextBoxCoords()
	{
		return getTextBoxCoords();
	}

}
