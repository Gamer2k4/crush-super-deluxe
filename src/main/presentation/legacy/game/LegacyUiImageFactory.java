package main.presentation.legacy.game;

import java.awt.image.BufferedImage;
import java.util.List;

import main.data.Data;
import main.data.entities.Player;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.LegacyFontFactory;

public abstract class LegacyUiImageFactory
{
	protected LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	protected LegacyColorReplacer colorReplacer = LegacyColorReplacer.getInstance();
	protected LegacyFontFactory fontFactory = LegacyFontFactory.getInstance();
	
	protected Data data;
	protected Player currentPlayer;
	
	protected List<Player> playersInGame;
	
	public abstract BufferedImage generateImage(Data gameData, Player gameCurrentPlayer);
}
