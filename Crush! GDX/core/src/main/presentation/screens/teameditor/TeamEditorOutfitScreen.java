package main.presentation.screens.teameditor;

import java.awt.Point;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Player;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.StaticSprite;
import main.presentation.legacy.common.LegacyUiConstants;

public class TeamEditorOutfitScreen extends AbstractTeamEditorEquipmentScreen
{
	private Point cursorCoords = CrushSprite.OFFSCREEN_COORDS;
	private boolean grabbingFromTeam = false;
	private boolean grabbingFromPlayer = false;
	private int grabSlot = -1;

	public TeamEditorOutfitScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_OUTFIT, screenOrigin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();

		Player selectedPlayer = teamUpdater.getCurrentlySelectedPlayer();

		if (selectedPlayer == null)
			return screenTexts;

		if (selectedPlayer.getDetectionChance() == 0)
			return screenTexts;

		screenTexts.add(GameText.small2(new Point(91, 247), LegacyUiConstants.COLOR_LEGACY_GREY, "DETECTION: " + selectedPlayer.getDetectionChance() + "%"));

		return screenTexts;
	}

	@Override
	public List<CrushSprite> getStaticSprites()
	{
		List<CrushSprite> staticSprites = super.getStaticSprites();
		Player selectedPlayer = teamUpdater.getCurrentlySelectedPlayer();

		StaticSprite draggedEquipmentSprite = null;
		
		if (grabbedEquipmentIndex != -1 && canUserEditTeam())
			draggedEquipmentSprite = getNormalGearImage(new Point(cursorCoords.x - 50, cursorCoords.y - 25), grabbedEquipmentIndex);

		if (selectedPlayer == null)
		{
			if (draggedEquipmentSprite != null)
				staticSprites.add(draggedEquipmentSprite);
			
			return staticSprites;
		}

		int padsIndex = selectedPlayer.getEquipment(Equipment.EQUIP_ARMOR);
		int glovesIndex = selectedPlayer.getEquipment(Equipment.EQUIP_GLOVES);
		int beltIndex = selectedPlayer.getEquipment(Equipment.EQUIP_BELT);
		int bootsIndex = selectedPlayer.getEquipment(Equipment.EQUIP_BOOTS);

		if (padsIndex != Equipment.EQUIP_NONE)
			staticSprites.add(getNormalGearImage(new Point(81, 90), padsIndex));

		if (glovesIndex != Equipment.EQUIP_NONE)
		{
			staticSprites.add(getLeftGearImage(new Point(41, 130), glovesIndex));
//			staticSprites.add(getRightGearImage(new Point(192, 130), glovesIndex));
		}

		if (beltIndex != Equipment.EQUIP_NONE)
			staticSprites.add(getNormalGearImage(new Point(81, 134), beltIndex));

		if (bootsIndex != Equipment.EQUIP_NONE)
		{
			staticSprites.add(getLeftGearImage(new Point(66, 185), bootsIndex));
//			staticSprites.add(getRightGearImage(new Point(150, 185), bootsIndex));
		}
		
		if (draggedEquipmentSprite != null)
			staticSprites.add(draggedEquipmentSprite);

		return staticSprites;
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// TODO Auto-generated method stub
		switch (command)
		{
		// $CASES-OMITTED$
		default:
			super.handleCommand(command);
			break;
		}
	}

	@Override
	protected Point getScreenClickedButtonCoords()
	{
		return new Point(125, 75);
	}

	@Override
	protected boolean dragEnabled()
	{
		return true;
	}

	@Override
	protected void mouseClicked(Point clickCursorCoords)
	{
		cursorCoords = new Point(clickCursorCoords.x, clickCursorCoords.y);

		if (grabbedEquipmentIndex != Equipment.EQUIP_NO_TYPE)
			return;

		grabSlot = -1;

		if (cursorCoords.x > 247 && cursorCoords.x < 363)
			grabEquipmentFromTeam();
		else
			grabEquipmentFromPlayer();
	}

	private void grabEquipmentFromTeam()
	{
		grabbingFromPlayer = false;
		grabbingFromTeam = true;

		if (cursorCoords.y > 44 && cursorCoords.y < 96)
			grabSlot = 0;
		else if (cursorCoords.y > 95 && cursorCoords.y < 147)
			grabSlot = 1;
		else if (cursorCoords.y > 146 && cursorCoords.y < 198)
			grabSlot = 2;
		else if (cursorCoords.y > 197 && cursorCoords.y < 249)
			grabSlot = 3;
		else
			grabbingFromTeam = false;

		if (!grabbingFromTeam)
			return;

		int teamInventoryIndex = grabSlot + topIndex;
		List<Integer> inventory = teamUpdater.getTeam().getEquipment();

		if (teamInventoryIndex < 0 || teamInventoryIndex >= inventory.size())
		{
			grabbingFromTeam = false;
			grabSlot = -1;
			topIndex = 0;
			return;
		}

		currentSelection = grabSlot;
		grabbedEquipmentIndex = teamUpdater.getTeam().getEquipment().get(teamInventoryIndex);

		return;
	}

	private void grabEquipmentFromPlayer()
	{
		if (!canUserEditTeam())
			return;
		
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		
		if (player == null)
			return;
		
		grabbingFromPlayer = true;
		grabbingFromTeam = false;
		
		if (cursorCoords.x > 80 && cursorCoords.x < 181 && cursorCoords.y > 89 && cursorCoords.y < 140)
			grabSlot = Equipment.EQUIP_ARMOR;
		
		if (cursorCoords.x > 106 && cursorCoords.x < 155 && cursorCoords.y > 145 && cursorCoords.y < 174)
			grabSlot = Equipment.EQUIP_BELT;
		
		if (cursorCoords.x > 36 && cursorCoords.x < 75 && cursorCoords.y > 135 && cursorCoords.y < 174)
			grabSlot = Equipment.EQUIP_GLOVES;
		
		if (cursorCoords.x > 186 && cursorCoords.x < 225 && cursorCoords.y > 135 && cursorCoords.y < 174)
			grabSlot = Equipment.EQUIP_GLOVES;
		
		if (cursorCoords.x > 66 && cursorCoords.x < 115 && cursorCoords.y > 185 && cursorCoords.y < 234)
			grabSlot = Equipment.EQUIP_BOOTS;
		
		if (cursorCoords.x > 146 && cursorCoords.x < 195 && cursorCoords.y > 185 && cursorCoords.y < 234)
			grabSlot = Equipment.EQUIP_BOOTS;
		
		if (grabSlot == -1)
			return;
		
		if (player.getEquipment(grabSlot) == Equipment.EQUIP_NONE)
			return;
		
		grabbedEquipmentIndex = player.unequipItem(grabSlot);
	}

	@Override
	protected void mouseReleased()
	{
		if (grabbedEquipmentIndex == Equipment.EQUIP_NO_TYPE)
			return;

		if (cursorCoords.x > 22 && cursorCoords.x < 238 && cursorCoords.y > 41 && cursorCoords.y < 271)
			releaseGearOnPlayer();
		else if (cursorCoords.x > 247 && cursorCoords.x < 363 && cursorCoords.y > 41 && cursorCoords.y < 252)
			releaseGearOnTeam();
		else if (grabbingFromPlayer)
			returnGrabbedGearToPlayer();

		cursorCoords = CrushSprite.OFFSCREEN_COORDS;

		grabbingFromPlayer = false;
		grabbingFromTeam = false;
		grabSlot = -1;
		grabbedEquipmentIndex = Equipment.EQUIP_NO_TYPE;
	}

	// these two methods are assumed to only be called from mouseReleased(), and do not properly clean up otherwise
	private void releaseGearOnPlayer()
	{
		if (isCpuTeam())	//TODO: or current player has technophobia
			return;
		
		if (grabbingFromPlayer)
		{
			returnGrabbedGearToPlayer();
			return;
		}

		int teamInventoryIndex = currentSelection + topIndex;
		teamUpdater.equipToSelectedPlayer(teamInventoryIndex);
	}

	private void releaseGearOnTeam()
	{
		if (!grabbingFromPlayer || isCpuTeam())
			return;

		teamUpdater.addEquipmentToTeam(currentSelection + topIndex, grabbedEquipmentIndex);
	}

	private void returnGrabbedGearToPlayer()
	{
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		
		if (player == null || !grabbingFromPlayer || !canUserEditTeam())
			return;
		
		int type = Equipment.getType(grabbedEquipmentIndex);
		
		if (player.getEquipment(type) != Equipment.EQUIP_NONE)
			return;
		
		player.equipItem(grabbedEquipmentIndex);
	}
}
