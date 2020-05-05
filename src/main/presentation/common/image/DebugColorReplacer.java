package main.presentation.common.image;

import java.awt.Color;

public class DebugColorReplacer extends AbstractColorReplacer
{
	private static final Color[] COLOR1 = { new Color(224, 224, 224), 
											new Color(199, 199, 199),
											new Color(168, 168, 168),
											new Color(143, 143, 143),
											new Color(87, 87, 87),
											new Color(56, 56, 56),
											new Color(31, 31, 31)};
	
	private static final Color[] COLOR2 = { new Color(224, 0, 224), 
											new Color(199, 0, 199),
											new Color(168, 0, 168),
											new Color(143, 0, 143),
											new Color(87, 0, 87),
											new Color(56, 0, 56),
											new Color(31, 0, 31)};

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
		return new Color(255, 0, 255);
	}
}
