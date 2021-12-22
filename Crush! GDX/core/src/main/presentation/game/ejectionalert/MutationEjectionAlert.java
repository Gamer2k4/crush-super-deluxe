package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class MutationEjectionAlert extends EjectionAlert
{
	private List<GameText> text = new ArrayList<GameText>();
	
	public MutationEjectionAlert(Data data, Event event)
	{
		super(ImageType.EJECT_BLOB, new Rectangle(0, 0, 160, 35));
		
		Player player = data.getPlayer(event.flags[0]);
		Team team = data.getTeamOfPlayer(player);
		int playerNumber = data.getNumberOfPlayer(player);
		
		defineGameTexts(team.teamName, playerNumber, player.name);
	}

	private void defineGameTexts(String teamName, int playerNumber, String playerName)
	{
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, teamName));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(85, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, "#" + playerNumber + " " + playerName));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 15), LegacyUiConstants.COLOR_LEGACY_BLUE, "MUTATED BY"));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 25), LegacyUiConstants.COLOR_LEGACY_WHITE, "TELEPORTER ACCIDENT"));
	}

	@Override
	protected List<GameText> getGameTexts()
	{
		return text;
	}
	
}
