package main.presentation.screens.teameditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Equipment;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.legacy.common.LegacyUiConstants;

public class TeamEditorAcquireScreen extends AbstractTeamEditorEquipmentScreen
{
	private int selectedEquipmentType = Equipment.EQUIP_ARMOR;
	private int selectedEquipmentIndex = 0;
	
	private List<ImageButton> buttons = new ArrayList<ImageButton>();
	private Map<Integer, Integer> typeSelectionMap = new HashMap<Integer, Integer>();
	
	private int[][] equipmentInStore = {{0,  1,  2,  3,  4,   5, -2},
										{6,  7,  8,  9,  10, 11, 12},
										{13, 14, 15, 16, 17, -2, -1},
										{18, 19, 20, 21, 22, -2, -1}};
	
	private static final Point ACQUIRE_TEXT_ORIGIN = new Point(72, 206);
	
	public TeamEditorAcquireScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_ACQUIRE, screenOrigin);
		defineButtons();
		defineSelectionMap();
	}

	private void defineButtons()
	{
		buttons.add(parentScreen.addClickZone(207, 67, 37, 25, ScreenCommand.EQUIP_SELECT_PADS));
		buttons.add(parentScreen.addClickZone(207, 95, 37, 25, ScreenCommand.EQUIP_SELECT_BELTS));
		buttons.add(parentScreen.addClickZone(207, 123, 37, 25, ScreenCommand.EQUIP_SELECT_BOOTS));
		buttons.add(parentScreen.addClickZone(207, 151, 37, 25, ScreenCommand.EQUIP_SELECT_GLOVES));
		buttons.add(parentScreen.addButton(46, 207, 20, 38, false, false, ScreenCommand.EQUIP_SCROLL_LEFT, true));
		buttons.add(parentScreen.addButton(194, 206, 20, 38, false, false, ScreenCommand.EQUIP_SCROLL_RIGHT, true));	//yes, this button is one pixel too high
		buttons.add(parentScreen.addButton(37, 60, 264, false, ScreenCommand.EQUIP_BUY));
		buttons.add(parentScreen.addButton(37, 161, 264, false, ScreenCommand.EQUIP_SELL));
	}

	private void defineSelectionMap()
	{
		typeSelectionMap.clear();
		typeSelectionMap.put(Equipment.EQUIP_ARMOR, 0);
		typeSelectionMap.put(Equipment.EQUIP_GLOVES, 3);
		typeSelectionMap.put(Equipment.EQUIP_BELT, 1);
		typeSelectionMap.put(Equipment.EQUIP_BOOTS, 2);
	}

	@Override
	protected void refreshContent()
	{
		super.refreshContent();
		
		//the original game doesn't actually reset these no matter how far from the screen you go
		selectedEquipmentType = Equipment.EQUIP_ARMOR;
		selectedEquipmentIndex = 0;
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		actors.add(getPressedEquipmentTypeButton()); 
		
		return actors;
	}
	
	private Actor getPressedEquipmentTypeButton()
	{
		int buttonIndex = typeSelectionMap.get(selectedEquipmentType);
		Point buttonCoords = new Point(207, 307 - (28 * buttonIndex));
		
		if (buttonIndex == 3)
			buttonCoords.y -= 1;	//yes, the gloves button is one pixel lower than it's supposed to be
		
		StaticImage pressedButton = new StaticImage(ImageType.BUTTON_37x25_CLICKED, buttonCoords);
		return pressedButton.getImage();
	}

	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		screenTexts.addAll(getEquipmentDetailText(ACQUIRE_TEXT_ORIGIN, getSelectedEquipment()));
		
		int equipmentCost = Equipment.getEquipment(getSelectedEquipment()).cost;
		screenTexts.add(GameText.small(new Point(121, 258), LegacyUiConstants.COLOR_LEGACY_GOLD, String.valueOf(equipmentCost)));
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		screenButtons.addAll(buttons);
		
		return screenButtons;
	}
	
	@Override
	public List<CrushSprite> getStaticSprites()
	{
		List<CrushSprite> staticSprites = super.getStaticSprites();
	
		staticSprites.add(getNormalGearImage(new Point(80, 95), getSelectedEquipment()));
		
		return staticSprites;
	}
	
	private int getSelectedEquipment()
	{
		int type = typeSelectionMap.get(selectedEquipmentType);
		
		//if scrolling backwards, keep going until at an index with a valid equipment
		if (selectedEquipmentIndex < 0)
		{
			selectedEquipmentIndex = 6;
			while (equipmentInStore[type][selectedEquipmentIndex] < 0)
				selectedEquipmentIndex--;
		}
		
		int equipmentIndex = equipmentInStore[type][selectedEquipmentIndex];
		
		if (equipmentIndex >= 0)
			return equipmentIndex;
		
		System.out.println("Invalid equipment type " + equipmentIndex + " found for selected index " + selectedEquipmentIndex);
		
		selectedEquipmentIndex = equipmentIndex + 2;		//wrap around to the start if the index is greater than the total equipments of this type (such as when moving from belts to gloves)
		
		System.out.println("Selected index has been set to " + selectedEquipmentIndex);
		return equipmentInStore[type][selectedEquipmentIndex];
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		switch (command)
		{
		case EQUIP_SELECT_PADS:
			selectedEquipmentType = Equipment.EQUIP_ARMOR;
			break;
		case EQUIP_SELECT_BELTS:
			selectedEquipmentType = Equipment.EQUIP_BELT;
			break;
		case EQUIP_SELECT_BOOTS:
			selectedEquipmentType = Equipment.EQUIP_BOOTS;
			break;
		case EQUIP_SELECT_GLOVES:
			selectedEquipmentType = Equipment.EQUIP_GLOVES;
			break;
		case EQUIP_SCROLL_LEFT:
			selectedEquipmentIndex--;
			if (selectedEquipmentIndex < -1)
				selectedEquipmentIndex = -1;
			break;
		case EQUIP_SCROLL_RIGHT:
			selectedEquipmentIndex++;
			if (selectedEquipmentIndex > 6)
				selectedEquipmentIndex = 0;
			break;
		case EQUIP_BUY:
			if (!canUserEditTeam())
				break;
			
			Equipment equipment = Equipment.getEquipment(getSelectedEquipment());
			teamUpdater.buyEquipment(topIndex + currentSelection, equipment);
			break;
		case EQUIP_SELL:
			if (!canUserEditTeam())
				break;
			
			if (currentSelection != -1)
				teamUpdater.sellEquipment(topIndex + currentSelection);
			break;
			//$CASES-OMITTED$
		default:
			super.handleCommand(command);
			break;
		}
	}

	@Override
	protected Point getScreenClickedButtonCoords()
	{
		return new Point(40, 75);
	}
}
