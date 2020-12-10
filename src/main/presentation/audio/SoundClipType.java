package main.presentation.audio;

public enum SoundClipType
{
	BUTTON(23, 150),
	WINDOW(130, 665),
	THEME(700, 8110),
	HORN(0, 0),
	DING(0, 0),
	ZAP(0, 0),
	CROUD(0, 0),
	LOWWALK(0, 0),
	MEDWALK(0, 0),
	ROBWALK(0, 0),
	KURWALK(0, 0),
	SLWALK(0, 0),
	HIGHOOH(0, 0),
	LOWOOH(0, 0),
	BEVOOH(0, 0),
	MEDOOH(0, 0),
	ANTOOH(0, 0),
	JUMP(0, 0),
	ANTDEAT(0, 0),
	GRDEAT(0, 0),
	KRMDEAT(0, 0),
	ROBDEAT(0, 0),
	ROBHIT1(0, 0),
	ROBOOH(0, 0),
	PREGAME(0, 0),
	DRGDEAT(0, 0),
	KURDEAT(0, 0),
	HUMDEAT(0, 0),
	DRGOOH(0, 0),
	SLTOOH(0, 0),
	TP(0, 0),
	SLDEAT(0, 0),
	SIREN(0, 0),
	DODGE(0, 0),
	THROW(0, 0),
	HUMVOC(51100, 550),
	GRKVOC(51650, 720),
	CURVOC(52300, 900),
	NYNVOC(53200, 750),
	XJSVOC(54000, 1500),
	KURVOC(55550, 700),
	DRGVOC(56250, 800),
	SLTVOC(57050, 900),
	INJURY(0, 0),
	FATAL(0, 0),
	RUNOUT(0, 0),
	CROWD(0, 0),
	MUTATE(0, 0),
	WHISTLE(0, 0),
	REPULSE(0, 0),
	VORTEX(0, 0),
	KO(0, 0),
	EXPLODE(0, 0),
	VICTORY(0, 0),
	CLAP(0, 0),
	ORGAN1(0, 0),
	ORGAN2(0, 0),
	HOTDOGS(0, 0),
	DRINKS(0, 0),
	FANFARE(0, 0),
	ORGAN3(0, 0),
	INTROA(0, 0),
	INTROB(0, 0);
	
	private int startFrame;
	private int durationInFrames;
	private int durationInMs;
	
	private SoundClipType(int startMs, int duration)
	{
		startFrame = framesFromMilliseconds(startMs);
		durationInFrames = framesFromMilliseconds(duration);
		durationInMs = duration;
	}

	public int getStartFrame()
	{
		return startFrame;
	}

	public int getDurationInFrames()
	{
		return durationInFrames;
	}

	public int getDurationInMs()
	{
		return durationInMs;
	}
	
	private int framesFromMilliseconds(int milliseconds)
	{
		int microseconds = milliseconds * 1000;
		int frames = microseconds / 45;
		return frames;
	}
}
