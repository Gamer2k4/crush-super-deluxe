package main.presentation.legacy.teameditor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import main.data.entities.Player;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;

public abstract class AbstractLegacyTeamEditorDraftScreen extends LegacyTeamEditorScreenDecorator implements ActionListener
{
	private static final long serialVersionUID = 6431575558631024084L;
	
	private static final Point coordsHire = new Point(137, 275);
	private static final Point coordsFire = new Point(137, 296);

	public AbstractLegacyTeamEditorDraftScreen(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
		
		addClickZone(new Rectangle(coordsHire, buttonDimSmallStats), ScreenCommand.HIRE_PLAYER);
		addClickZone(new Rectangle(coordsFire, buttonDimSmallStats), ScreenCommand.FIRE_PLAYER);
	}
	
	protected void paintPlayer(Graphics2D graphics, Player player)
	{
		BufferedImage playerImage = teamUpdater.getPlayerImage(player.getRace());
		graphics.drawImage(playerImage, 208, 34, null);
		
		paintTextElement(graphics, 169, 41, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_AP)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 168, 72, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_CH)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 168, 103, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_ST)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 168, 134, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_TG)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 316, 41, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_RF)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 316, 72, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_JP)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 316, 103, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_HD)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		paintTextElement(graphics, 316, 134, String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_DA)), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
		
		paintTextElement(graphics, 233, 186, new DecimalFormat("000").format(player.getSalary()), FontType.FONT_SMALL, LegacyUiConstants.COLOR_LEGACY_GOLD);
	}

}
