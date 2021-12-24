package main.presentation.common.image;

import java.awt.Color;

public class ColorReplacer extends AbstractColorReplacer
{
	//0 red is 7, 6, 5, 4, 2, 1, NO NUMBER (31, 0, 0)
	//1 orange (COLOR2) is 15, 14, 13, 12, 10, 9, 8
	
	//9 blue (COLOR1) is 79, 78, 77, 76, 74, 73, 72
	//28 light brown is 231, 230, 229, 228, 226, 225, 224
	
	//so it's (8 * index), then +7, 6, 5, 4, 2, 1, and 0
	
	
	private static final Color[] COLOR1 = { new Color(0, 0, 224), 
											new Color(0, 0, 199),
											new Color(0, 0, 168),
											new Color(0, 0, 143),
											new Color(0, 0, 112),
											new Color(0, 0, 87),
											new Color(0, 0, 56),
											new Color(0, 0, 31)};
	
	private static final Color[] COLOR2 = { new Color(224, 96, 0),
											new Color(199, 87, 0),
											new Color(168, 72, 0),
											new Color(143, 63, 0),
											new Color(112, 48, 0),
											new Color(87, 39, 0),
											new Color(56, 24, 0),
											new Color(31, 16, 0)};
	
	private ColorMap colorMap;
	
	private static ColorReplacer instance = null;
	
	private ColorReplacer()
	{
		colorMap = new InGameColorMap();
	}
	
	public static ColorReplacer getInstance()
	{
		if (instance == null)
			instance = new ColorReplacer();

		return instance;
	}

	@Override
	protected Color getColor1(int level)
	{
		return COLOR1[level];
	}

	@Override
	protected Color getColor2(int level)
	{
		return COLOR2[level];
	}

	@Override
	protected Color getBackgroundBase()
	{
		return new Color(0, 0, 0, 0);
	}
	
	@Override
	protected Color darkenColor(Color color, int level)
	{
		if (level < 0 || level > MAX_DARKEN_LEVEL)
			throw new IllegalArgumentException("Level must be between 0 and " + MAX_DARKEN_LEVEL + ", inclusive.");
		
		int darken = 7 - level;
		
		if (level < 4)
			darken = 8 - level;
		
		if (!TeamColorType.isValidLegacyTeamColor(color))
			return super.darkenColor(color, level);		//darken mathematically, rather than using specific replacement colors
		
		Integer index = TeamColorType.getIndex(color);
		
		index = (index * 8) + darken;
		
		if (index == 0)
			return new Color(31, 0, 0);	//special case for red
		
		return colorMap.getColor(index);
	}
}
