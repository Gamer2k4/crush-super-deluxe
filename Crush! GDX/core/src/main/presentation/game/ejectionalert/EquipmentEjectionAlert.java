package main.presentation.game.ejectionalert;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.data.Data;
import main.data.Event;
import main.data.entities.Equipment;
import main.data.entities.Player;
import main.data.entities.Team;
import main.presentation.ImageType;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public class EquipmentEjectionAlert extends EjectionAlert
{
	private List<GameText> text = new ArrayList<GameText>();
	
	public EquipmentEjectionAlert(Data data, Event event)
	{
		super(ImageType.EJECT_REF, new Rectangle(0, 0, 160, 65));
		
		Player player = data.getPlayer(event.flags[0]);
		Team team = data.getTeamOfPlayer(player);
		int playerNumber = data.getNumberOfPlayer(player);
		
		defineGameTexts(team.teamName, playerNumber, player);
	}

	private void defineGameTexts(String teamName, int playerNumber, Player player)
	{
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, teamName));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(85, 5), LegacyUiConstants.COLOR_LEGACY_WHITE, "#" + playerNumber + " " + player.name));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 15), LegacyUiConstants.COLOR_LEGACY_ORANGE, "EJECTED"));
		text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 25), LegacyUiConstants.COLOR_LEGACY_WHITE, "ILLEGAL ITEMS:"));
		
		for (int i = 0; i < 4; i++)
		{
			int equipmentIndex = player.getEquipment(i);
			
			if (equipmentIndex == Equipment.EQUIP_NONE)
				continue;
			
			Equipment equippedItem = Equipment.getEquipment(equipmentIndex);
			
			text.add(new GameText(FontType.FONT_SMALL2, new Point(5, 35), LegacyUiConstants.COLOR_LEGACY_WHITE, equippedItem.name.toUpperCase()));
		}
	}

	@Override
	protected List<GameText> getGameTexts()
	{
		return text;
	}
	
}
