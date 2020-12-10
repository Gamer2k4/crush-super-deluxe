package main.presentation.audio;

public interface AudioManager
{
	void stopBackgroundLoop();
	void playBackgroundLoop(SoundClipType clip);
	void playForegroundSound(SoundClipType clip);
}
