package main.presentation.screens.teameditor.utilities;

import java.text.DecimalFormat;

import main.data.entities.Stats;

public class GUIStatsFormatter
{
	private static DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	private static DecimalFormat intFormatter = new DecimalFormat("000");
	
	//TODO: eventually add methods for teams and players if it makes sense to do so
	
	public static String formatStat(Stats stats, int statIndex)
	{
		return formatValue(stats.getStat(statIndex), statIndex);
	}
	
	public static String formatValue(int value, int statIndex)
	{
		if (statIndex == Stats.STATS_CHECKING_AVERAGE)
			return formatCheckingAverage(value);
		
		return intFormatter.format(value);
	}
	
	private static String formatCheckingAverage(int value)
	{
		if (value < 1)
			return "0.00";
		
		if (value > 99)
			return "1.00";
		
		return decimalFormatter.format(value / 100.0);
	}
}
