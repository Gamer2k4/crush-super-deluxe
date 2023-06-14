package main.presentation.screens.stats;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;

import main.data.entities.SortableStatsCollection;
import main.presentation.ImageType;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.GUIStatsFormatter;

public abstract class AbstractTeamStatsScreen		//team stats as opposed to event stats; this can still be for individuals
{
	protected AbstractTeamStatsParentScreen parentScreen;
	protected StaticImage screenImage;

	private Map<Integer, Integer> columnMappings = new HashMap<Integer, Integer>();
	private int selectedColumnIndex = 0;
	
	private ColumnHighlight[] columnHighlights = {null, null, null, null};
	
	protected AbstractTeamStatsScreen(AbstractTeamStatsParentScreen parentScreen, ImageType screenImageType)
	{
		this.parentScreen = parentScreen;
		this.screenImage = new StaticImage(screenImageType, new Point(0, 0));
	}
	
	public List<Actor> getActors()
	{
		List<Actor> actors = new ArrayList<Actor>();
		
		actors.add(screenImage.getImage());
		
		return actors;
	}
	
	public List<GameText> getScreenTexts()
	{
		List<GameText> texts = new ArrayList<GameText>();
		
		texts.add(columnHighlights[selectedColumnIndex].getTopText());
		texts.add(columnHighlights[selectedColumnIndex].getBottomText());
		
		SortableStatsCollection stats = parentScreen.getStats();
		
		for (int i = 0; i < 9; i++)
		{
			int playerIndex = i + parentScreen.getTopIndex();
			
			if (!stats.hasPlayerEntry(playerIndex))
				continue;
			
			int space = (i / 3) * 6;
			int y = space + 94 + (20 * i);
			
			Color color = stats.getColor(playerIndex);
			texts.add(GameText.smallSpread(new Point(73, y), color, stats.getPlayerName(playerIndex)));
			texts.add(GameText.small2(new Point(254, y + 5), color, stats.getPlayerRace(playerIndex)));
			
			for (int j = 0; j < 4; j++)
			{
				Integer statIndex = columnMappings.get(j);
				
				if (statIndex == null)
					break;
				
				int x = 358 + (57 * j);
				texts.add(GameText.smallSpread(new Point(x, y), LegacyUiConstants.COLOR_LEGACY_GREY, GUIStatsFormatter.formatValue(stats.getStat(i, statIndex), statIndex)));
			}
		}
		
		return texts;
	}

	protected void setColumnHighlight(int columnIndex, String topText, int topX, String bottomText, int bottomX)
	{
		columnHighlights[columnIndex] = new ColumnHighlight(topText, topX, bottomText, bottomX);
	}
	
	protected void defineColumnStatIndex(int columnIndex, int statIndex)
	{
		columnMappings.put(columnIndex, statIndex);
	}
	
	protected int selectColumnToSortOn(int columnIndex)
	{
		Integer statIndex = columnMappings.get(columnIndex);
		if (statIndex == null)
			statIndex = 0;
		
		selectedColumnIndex = columnIndex;
		return statIndex;
	}
	
	public boolean isColumnEnabled(int columnIndex)
	{
		return columnHighlights[columnIndex] != null;
	}
	
	private class ColumnHighlight
	{
		private String topText;
		private String bottomText;
		private int topX;
		private int bottomX;
		
		private ColumnHighlight(String topText, int topX, String bottomText, int bottomX)
		{
			this.topText = topText;
			this.bottomText = bottomText;
			this.topX = topX;
			this.bottomX = bottomX;
		}
		
		private GameText getTopText()
		{
			return GameText.small2(new Point(topX, 71), LegacyUiConstants.COLOR_LEGACY_GOLD, topText);
		}
		
		private GameText getBottomText()
		{
			return GameText.small2(new Point(bottomX, 79), LegacyUiConstants.COLOR_LEGACY_GOLD, bottomText);
		}
	}
}
