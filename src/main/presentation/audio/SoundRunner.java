package main.presentation.audio;

public class SoundRunner
{
	public static void main(String[] args) throws Exception
	{
		CrushAudioManager.getInstance().playForegroundSound(SoundClipType.SLTVOC);
//		CrushAudioManager.getInstance().playBackgroundLoop(SoundClipType.THEME);

		final int interval = 2 * 1000;
		Thread.sleep(interval);
		System.exit(0);
	}
}
