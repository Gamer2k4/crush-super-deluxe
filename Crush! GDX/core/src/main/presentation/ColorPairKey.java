package main.presentation;

import java.awt.Color;

import main.data.entities.Team;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.TeamColorType;

public class ColorPairKey
{

	private Color foreground;
	private Color background;
	
	public ColorPairKey(Team team)
	{
		this(team.teamColors[0], team.teamColors[1]);
	}
	
	public ColorPairKey(TeamColorType fg, TeamColorType bg)
	{
		this(fg.getColor(), bg.getColor());
	}
	
	public ColorPairKey(Color fg, Color bg)
	{
		foreground = fg;
		background = bg;
	}
	
	public Color getForeground()
	{
		return foreground;
	}
	
	public Color getBackground()
	{
		return background;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((background == null) ? 0 : background.hashCode());
		result = prime * result + ((foreground == null) ? 0 : foreground.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColorPairKey other = (ColorPairKey) obj;
		if (background == null)
		{
			if (other.background != null)
				return false;
		} else if (!ImageUtils.rgbEquals(background, other.background))
			return false;
		if (foreground == null)
		{
			if (other.foreground != null)
				return false;
		} else if (!ImageUtils.rgbEquals(foreground, other.foreground))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "CPK[" + foreground.toString() + "," + background.toString() + "]";
	}
}
