package main.presentation.screens.teameditor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Equipment;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.StaticSprite;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamEditorEquipmentScreen extends AbstractTeamEditorSubScreen
{
	private List<ImageButton> pageButtons = new ArrayList<ImageButton>();
	
	protected static int topIndex = 0;
	protected static int currentSelection = -1;
	
	protected static int grabbedEquipmentIndex = Equipment.EQUIP_NO_TYPE;
	
	private static final Dimension SCROLL_BUTTON_DIM = new Dimension(45, 20);
	private static final int OWNED_EQUIPMENT_ORIGIN_X = 256;
	private static final int OWNED_EQUIPMENT_ORIGIN_Y = 45;
	private static final Point SELECTION_TEXT_ORIGIN = new Point(249, 281);
	
	String[][] equipmentTexts = new String[Equipment.EQUIP_TYPE_COUNT][3];
	
	protected AbstractTeamEditorEquipmentScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		definePageButtons();
		defineEquipmentText();
	}
	
	private void definePageButtons()
	{
		pageButtons.add(parentScreen.addClickZone(40, 307, 72, 17, ScreenCommand.ACQUIRE_VIEW));
		pageButtons.add(parentScreen.addClickZone(125, 307, 72, 17, ScreenCommand.OUTFIT_VIEW));
	}
	
	private void defineEquipmentText()
	{
		defineEquipmentText(Equipment.EQUIP_HEAVY_ARMOR, "+10 Toughness");
		defineEquipmentText(Equipment.EQUIP_REINFORCED_ARMOR, "+5 Toughness");
		defineEquipmentText(Equipment.EQUIP_REPULSOR_ARMOR, "Pushes away", "opposing players");
		defineEquipmentText(Equipment.EQUIP_SPIKED_ARMOR, "+5 Strength");
		defineEquipmentText(Equipment.EQUIP_SURGE_ARMOR, "50% chance of", "zapping opponents", "when they block you");
		defineEquipmentText(Equipment.EQUIP_VORTEX_ARMOR, "Pulls in", "opposing players");
		defineEquipmentText(Equipment.EQUIP_BACKFIRE_BELT, "Explodes when the", "player gets checked");
		defineEquipmentText(Equipment.EQUIP_BOOSTER_BELT, "+10 AP, 6% chance", "of injury every", "turn");
		defineEquipmentText(Equipment.EQUIP_CLOAKING_BELT, "Player is invisible,", "except when", "carrying the ball");
		defineEquipmentText(Equipment.EQUIP_HOLOGRAM_BELT, "Disguises the true", "species of the", "player");
		defineEquipmentText(Equipment.EQUIP_FIELD_INTEGRITY_BELT, "Keeps player from", "being blobbed");
		defineEquipmentText(Equipment.EQUIP_MEDICAL_BELT, "Prevents injuries", "and death 33% of", "the time");
		defineEquipmentText(Equipment.EQUIP_SCRAMBLER_BELT, "Causes teleporter", "accidents 25% of", "the time");
		defineEquipmentText(Equipment.EQUIP_SPIKED_BOOTS, "+5 Strength");
		defineEquipmentText(Equipment.EQUIP_BOUNDER_BOOTS, "Player cannot fail", "a jump");
		defineEquipmentText(Equipment.EQUIP_INSULATED_BOOTS, "Player is immune", "to all electrical", "effects");
		defineEquipmentText(Equipment.EQUIP_MAGNETIC_BOOTS, "+5 CH,", "Player can't be", "pushed or pulled");
		defineEquipmentText(Equipment.EQUIP_SAAI_BOOTS, "Gives player", "40 RF and 40 DA");
		defineEquipmentText(Equipment.EQUIP_MAGNETIC_GLOVES, "Player always", "picks up the ball");
		defineEquipmentText(Equipment.EQUIP_REPULSOR_GLOVES, "Pushes opponents", "when player checks", "them");
		defineEquipmentText(Equipment.EQUIP_SAAI_GLOVES, "Gives player", "60 CH");
		defineEquipmentText(Equipment.EQUIP_SPIKED_GLOVES, "+5 Strength");
		defineEquipmentText(Equipment.EQUIP_SURGE_GLOVES, "50% chance of", "zapping opponents", "when checking them");
	}

	private void defineEquipmentText(int equipmentIndex, String... textLines)
	{
		for (int i = 0; i < 3; i++)
		{
			if (i >= textLines.length)
				equipmentTexts[equipmentIndex][i] = "";
			else
				equipmentTexts[equipmentIndex][i] = textLines[i];
		}
	}

	private void refreshOwnedEquipment()
	{
		List<Integer> equipment = teamUpdater.getTeam().getEquipment();
		
		if (equipment.isEmpty())
			currentSelection = -1;
		else if (currentSelection == -1)
			currentSelection = 0;
		else if (equipment.size() > 3 && topIndex > equipment.size() - 4)
			topIndex = equipment.size() - 4;
		else if (currentSelection + topIndex >= equipment.size())
			currentSelection = 0;
			
		//TODO more?
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		StaticImage pressedButton = new StaticImage(ImageType.BUTTON_72x17_CLICKED, getScreenClickedButtonCoords());
		actors.add(pressedButton.getImage());
		
		return actors;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		//TODO: there's technically supposed to be an extra pixel between detection and description (which isn't there in the acquire screen)
		if (grabbedEquipmentIndex != -1)
			screenTexts.addAll(getEquipmentDetailText(SELECTION_TEXT_ORIGIN, grabbedEquipmentIndex));
		else if (currentSelection != -1)
			screenTexts.addAll(getEquipmentDetailText(SELECTION_TEXT_ORIGIN, teamUpdater.getTeam().getEquipment().get(currentSelection + topIndex)));
		
		return screenTexts;
	}
	
	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		//TODO: see if i've extracted highlight images for the scroll buttons
		//TODO: a clickmap image is defined in the original files for these (COLORMAP.DVE); I can just paint that on the clickmap screen like I do for the action buttons in the game window
		//		Or, I can just handle it like I do the roster arrows
		
		screenButtons.add(parentScreen.addClickZone(252, 257, SCROLL_BUTTON_DIM.width, SCROLL_BUTTON_DIM.height, ScreenCommand.EQUIP_SCROLL_UP));
		screenButtons.add(parentScreen.addClickZone(309, 257, SCROLL_BUTTON_DIM.width, SCROLL_BUTTON_DIM.height, ScreenCommand.EQUIP_SCROLL_DOWN));
		screenButtons.add(parentScreen.addClickZone(253, 44, 114, 51, ScreenCommand.EQUIP_SELECT_0));
		screenButtons.add(parentScreen.addClickZone(253, 95, 114, 51, ScreenCommand.EQUIP_SELECT_1));
		screenButtons.add(parentScreen.addClickZone(253, 146, 114, 51, ScreenCommand.EQUIP_SELECT_2));
		screenButtons.add(parentScreen.addClickZone(253, 197, 114, 51, ScreenCommand.EQUIP_SELECT_3));
		
		for (int i = 0; i < 2; i++)
		{
			ImageButton button = pageButtons.get(i);
			Point disableButtonCoords = getScreenClickedButtonCoords();
			
			if ((int) button.getX() == disableButtonCoords.x)
				continue;
			
			screenButtons.add(button);
		}
		
		return screenButtons;
	}
	
	@Override
	public List<CrushSprite> getStaticSprites()
	{
		List<CrushSprite> staticSprites = super.getStaticSprites();
		List<Integer> equipment = teamUpdater.getTeam().getEquipment();
		
		staticSprites.add(getEquipmentSelectionHighlight());
		
		for (int i = 0; i < 4; i++)
		{
			int index = topIndex + i;
			if (index >= equipment.size())
				break;
			
			staticSprites.add(getNormalGearImage(new Point(OWNED_EQUIPMENT_ORIGIN_X, OWNED_EQUIPMENT_ORIGIN_Y + (51 * i)), equipment.get(index)));
		}
		
		return staticSprites;
	}
	
	private StaticSprite getEquipmentSelectionHighlight()
	{
		Texture blackTexture = ImageFactory.getInstance().getTexture(ImageType.BLACK_SCREEN);
		
		if (currentSelection == -1)
			return new StaticSprite(new Point(-50, -50), new TextureRegion(blackTexture, 0, 0, 1, 1));
		
		TextureRegion gearHighlight = new TextureRegion(blackTexture, 0, 0, 100, 51);
		return new StaticSprite(new Point(OWNED_EQUIPMENT_ORIGIN_X, OWNED_EQUIPMENT_ORIGIN_Y + (51 * currentSelection)), gearHighlight, .5f);
	}
	
	protected StaticSprite getNormalGearImage(Point originCoords, int equipmentIndex)
	{
		//pads: 100x50
		//belts: 48x24
		//boots: 84x34
		//gloves: 98x24
		
		int equipmentType = Equipment.getType(equipmentIndex);
		
		if (equipmentType == Equipment.EQUIP_BELT)
		{
			originCoords.x += 26;		//28 would match how the original game does it, but it makes the belt off-center
			originCoords.y += 15;
		}
		else if (equipmentType == Equipment.EQUIP_BOOTS)
		{
			originCoords.x += 8;		//6 would match how the original game does it, but it makes the boots off-center
			originCoords.y += 10;
		}
		else if (equipmentType == Equipment.EQUIP_GLOVES)
		{
			originCoords.x += 1;
			originCoords.y += 14;
		}
		
		TextureRegionDrawable equipmentImage = (TextureRegionDrawable) teamUpdater.getEquipmentImage(equipmentIndex);
		return new StaticSprite(originCoords, equipmentImage.getRegion());
	}
	
	protected StaticSprite getLeftGearImage(Point originCoords, int equipmentIndex)
	{
		return getSubGearImage(originCoords, equipmentIndex, false);
	}
	
	protected StaticSprite getRightGearImage(Point originCoords, int equipmentIndex)
	{
		return getSubGearImage(originCoords, equipmentIndex, true);
	}
	
	protected StaticSprite getSubGearImage(Point originCoords, int equipmentIndex, boolean offset)
	{
		Equipment equipment = Equipment.getEquipment(equipmentIndex);
		TextureRegionDrawable equipmentImage = (TextureRegionDrawable) teamUpdater.getEquipmentImage(equipmentIndex);
		int offsetMultiplier = offset ? 1 : 0;
		/*
		if (equipment.type == Equipment.EQUIP_GLOVES)
		{
			originCoords.x += 1;
			originCoords.y += 14;
			TextureRegion rightGlove = new TextureRegion(equipmentImage.getRegion(), 70 * offsetMultiplier, 0, 27, 22);
			return new StaticSprite(originCoords, rightGlove);
		}
		
		if (equipment.type == Equipment.EQUIP_BOOTS)
		{
			originCoords.x += 8;
			originCoords.y += 10;
			TextureRegion rightBoot = new TextureRegion(equipmentImage.getRegion(), 53 * offsetMultiplier, 0, 31, 33);
			return new StaticSprite(originCoords, rightBoot);
		}
		*/
		return getNormalGearImage(originCoords, equipmentIndex);
	}
	
	protected List<GameText> getEquipmentDetailText(Point originCoords, int equipmentIndex)
	{
		List<GameText> details = new ArrayList<GameText>();
		
		Equipment equipment = Equipment.getEquipment(equipmentIndex);
		
		int originX = originCoords.x;
		int originY = originCoords.y;
		int maxWidth = 116;
		
		int nameX = GameText.getStringStartX(GameText.small2, originX, maxWidth, equipment.name);
		details.add(GameText.small2(new Point(nameX, originY), LegacyUiConstants.COLOR_LEGACY_GOLD, equipment.name));
		
		String detection = "Detection: " + equipment.detection + "%";
		int detectX = GameText.getStringStartX(GameText.small2, originX, maxWidth, detection);
		details.add(GameText.small2(new Point(detectX, originY + 6), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, detection));
		
		for (int i = 0; i < 3; i++)
		{
			String textLine = equipmentTexts[equipmentIndex][i];
			if (textLine.isEmpty())
				return details;
			
			int textX = GameText.getStringStartX(GameText.small2, originX, maxWidth, textLine);
			int textY = originY + 15 + (i * 6);
			details.add(GameText.small2(new Point(textX, textY), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, textLine));
		}
		
		return details;
	}

	@Override
	protected void refreshContent()
	{
		grabbedEquipmentIndex = Equipment.EQUIP_NO_TYPE;
		
		topIndex = 0;
		if (!teamUpdater.getTeam().getEquipment().isEmpty())
			currentSelection = 0;
		refreshOwnedEquipment();
		
		//TODO: acquire screen will need to call this, then reset its own indices
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		List<Integer> equipment = teamUpdater.getTeam().getEquipment();
		
		if (command == ScreenCommand.EQUIP_SCROLL_UP && topIndex > 0)
			topIndex--;
		
		if (command == ScreenCommand.EQUIP_SCROLL_DOWN && topIndex < equipment.size() - 4)
			topIndex++;
		
		if (!command.isEquipSelect())
			return;
		
		int newSelection = command.getCommandIndex();
		
		if (newSelection + topIndex >= equipment.size())
			return;
		
		currentSelection = newSelection;
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if (command.equals(TeamUpdater.UPDATER_NEW_TEAM) || command.equals(TeamUpdater.UPDATER_COLORS_CHANGED))
		{
			//TODO: refresh image colors if necessary; since the images aren't stored here (like the helmet image is for roster),  
			refreshParentStage();
		}
		
		if (command.equals(TeamUpdater.UPDATER_NEW_TEAM) || command.equals(TeamUpdater.UPDATER_EQUIPMENT_CHANGED) || command.equals(TeamUpdater.UPDATER_PLAYERS_CHANGED))
			refreshOwnedEquipment();
	}
	
	protected abstract Point getScreenClickedButtonCoords();
}
