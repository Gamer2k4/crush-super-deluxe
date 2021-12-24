package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Attribute;
import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public abstract class MedicalEjectionAlert extends EjectionAlert
{
	protected List<GameText> text = new ArrayList<GameText>();
	
	protected MedicalEjectionAlert(ImageType imageType, Data data, Event event)
	{
		super(imageType, new Rectangle(0, 0, 160, 83));
		defineGameTexts(data, event);
	}

	private void defineGameTexts(Data data, Event event)
	{
		Player player = data.getPlayer(event.flags[0]);
		Team team = data.getTeamOfPlayer(player);
		int playerNumber = data.getNumberOfPlayer(player);
		
		int causeIndex = event.flags[1];
		String cause = "";
		String causerTeam = "";
		String causerNumber = "";
		
		if (causeIndex == Event.EJECT_CAUSE_ELECTROCUTION)
			causerTeam = "ELECTROCUTION";
		else if (causeIndex == Event.EJECT_CAUSE_GROUND)
			causerTeam = "HITTING THE GROUND";
		else if (causeIndex == Event.EJECT_CAUSE_BACKFIRE)
			causerTeam = "BACKFIRE BELT";
		else
		{
			Player causerPlayer = data.getPlayer(causeIndex);
			cause = causerPlayer.name;
			causerTeam = data.getTeamOfPlayer(causerPlayer).teamName;
			causerNumber = "#" + data.getNumberOfPlayer(causerPlayer);
		}
		
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, team.teamName));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(85, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, "#" + playerNumber + " " + player.name));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 15), LegacyUiConstants.COLOR_LEGACY_BLUE, explanationText()));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 25), LegacyUiConstants.COLOR_LEGACY_WHITE, causerTeam));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(85, 25), LegacyUiConstants.COLOR_LEGACY_WHITE, causerNumber + " " + cause));
		
		if (event.flags[2] == Event.EJECT_DEATH)	//TODO: will get updated once there's such a thing as regeneration or docbot resusciation, but for now it's assumed
			return;									//		that "dead" means "not revived," so there will be no injury explanation text
		
		addInjurySpecifics(event);
	}
	
	private void addInjurySpecifics(Event event)
	{
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 47), LegacyUiConstants.COLOR_LEGACY_WHITE, "INJURY STATUS:"));
		
		if (event.flags[2] == Event.EJECT_TRIVIAL)
		{
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 57), LegacyUiConstants.COLOR_LEGACY_BLUE, "TRIVIAL INJURY"));
			return;
		}
		
		if (event.flags[4] == 0 && event.flags[6] == 0)
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 57), LegacyUiConstants.COLOR_LEGACY_GOLD, "MINOR INJURY"));
		else
		{
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 57), LegacyUiConstants.COLOR_LEGACY_ORANGE, "CRIPPLING INJURY"));
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 69), LegacyUiConstants.COLOR_LEGACY_WHITE, "-" + event.flags[4] + " " + Attribute.fromIndex(event.flags[3]).getLongDescription()));
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 75), LegacyUiConstants.COLOR_LEGACY_WHITE, "-" + event.flags[6] + " " + Attribute.fromIndex(event.flags[5]).getLongDescription()));
		}
		
		int weeksOut = event.flags[7];
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 63), LegacyUiConstants.COLOR_LEGACY_WHITE, "OUT FOR " + weeksOut + " GAMES"));
	}

	@Override
	protected List<GameText> getGameTexts()
	{
		return text;
	}
	
	protected abstract String explanationText();
}
