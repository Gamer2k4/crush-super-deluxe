package main.presentation.screens.teameditor;

import java.awt.Color;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import main.data.entities.Attribute;
import main.data.entities.Player;
import main.data.entities.Race;
import main.presentation.TeamColorsManager;
import main.presentation.common.PlayerTextFactory;
import main.presentation.game.FontType;
import main.presentation.game.GameText;
import main.presentation.legacy.common.LegacyUiConstants;

public abstract class AbstractTeamEditorDraftScreen extends AbstractTeamEditorSubScreen
{
	protected Player draftee = new Player(Race.HUMAN, "DRAFTEE");
	
	private static final Point PLAYER_IMAGE_COORDS = new Point(208, 279);
	private static final Point PLAYER_SALARY_COORDS = new Point(233, 188);
	
	private Map<Attribute, Point> attributeTextCoords = new HashMap<Attribute, Point>();
	
	protected AbstractTeamEditorDraftScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		defineAttributeTextCoords();
	}
	
	private void defineAttributeTextCoords()
	{
		attributeTextCoords.clear();
		
		attributeTextCoords.put(Attribute.AP, new Point(169, 43));
		attributeTextCoords.put(Attribute.CH, new Point(168, 74));
		attributeTextCoords.put(Attribute.ST, new Point(168, 105));
		attributeTextCoords.put(Attribute.TG, new Point(168, 136));
		attributeTextCoords.put(Attribute.RF, new Point(316, 43));
		attributeTextCoords.put(Attribute.JP, new Point(316, 74));
		attributeTextCoords.put(Attribute.HD, new Point(316, 105));
		attributeTextCoords.put(Attribute.DA, new Point(316, 136));
	}

	protected Image getPlayerImage(Color mainColor, Color trimColor)
	{
		Texture playerTexture = TeamColorsManager.getInstance().getPlayerImage(draftee.getRace(), mainColor, trimColor);
		Image playerImage = new Image(new TextureRegionDrawable(playerTexture));
		playerImage.setPosition(PLAYER_IMAGE_COORDS.x, PLAYER_IMAGE_COORDS.y);
		return playerImage;
	}
	
	public List<GameText> getPlayerAttributeTexts()
	{
		PlayerTextFactory.setPlayer(draftee);
		
		List<GameText> screenTexts = super.getScreenTexts();
		
		for (Attribute attribute : Attribute.values())
		{
			screenTexts.add(getAttributeText(attribute, attributeTextCoords.get(attribute)));
		}
		
		screenTexts.add(GameText.smallSpread(PLAYER_SALARY_COORDS, LegacyUiConstants.COLOR_LEGACY_GOLD, new DecimalFormat("000").format(draftee.getSalary())));
		
		return screenTexts;
	}

	private GameText getAttributeText(Attribute att, Point coords)
	{
		GameText attributeText = PlayerTextFactory.getColoredAttributeWithModifiers(att.getIndex(), LegacyUiConstants.COLOR_LEGACY_GOLD, LegacyUiConstants.COLOR_LEGACY_GREEN, FontType.FONT_SMALL);
		attributeText.setCoords(coords);
		return attributeText;
	}
}
