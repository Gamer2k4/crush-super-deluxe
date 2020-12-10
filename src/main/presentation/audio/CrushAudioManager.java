package main.presentation.audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import main.presentation.common.Logger;

public class CrushAudioManager implements AudioManager
{
	private static final int TOTAL_TRACKS = 4;
	private static String AUDIO_SOURCE = Paths.get(System.getProperty("user.dir")).getParent().toString() + "\\resources\\CRUSH.wav";
	
	private Clip[] audioTracks = new Clip[TOTAL_TRACKS];
	private SoundClipType currentBackground = null;
	
	private boolean muted = false;
	
	private static CrushAudioManager instance = null;
	
	private CrushAudioManager() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		for (int i = 0; i < TOTAL_TRACKS; i++)
			audioTracks[i]= loadClip();
		
		//make the background music louder than the rest of the sounds - at least the main theme
		FloatControl gainControl = (FloatControl)audioTracks[0].getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-5);
	}
	
	private Clip loadClip() throws LineUnavailableException, IOException, UnsupportedAudioFileException
	{
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(AUDIO_SOURCE));
		Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		clip.setFramePosition(0);
//		System.out.println("Total Frames: " + clip.getFrameLength());
//		System.out.println("Total Microseconds: " + clip.getMicrosecondLength());
		
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-15);
		
		return clip;
	}

	public static CrushAudioManager getInstance()
	{
		if (instance == null)
		{
			try
			{
				instance = new CrushAudioManager();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
			{
				Logger.error("Exception thrown when initializing audio manager: " + e.getMessage());
			}
		}
		
		return instance;
	}

	@Override
	public void stopBackgroundLoop()
	{
		audioTracks[0].stop();
		currentBackground = null;
	}

	@Override
	public void playBackgroundLoop(SoundClipType clipType)
	{	
		if (audioTracks[0].isActive())
		{
			if (currentBackground == clipType)
				return;
			
			stopBackgroundLoop();
		}
		
		loopTrack(audioTracks[0], clipType);
		currentBackground = clipType;
	}

	@Override
	public void playForegroundSound(SoundClipType clipType)
	{
		for (int i = 1; i < TOTAL_TRACKS; i++)
		{
			Clip clip = audioTracks[i];
			if (!clip.isActive())
			{
				playTrack(clip, clipType);
				return;
			}
		}
		
		Logger.warn("No track is available to play clip " + clipType.name() + ".");
	}

	private void loopTrack(Clip clip, SoundClipType clipType)
	{
		if (muted)
			return;
		
		clip.setFramePosition(clipType.getStartFrame());
		clip.setLoopPoints(clipType.getStartFrame(), clipType.getStartFrame() + clipType.getDurationInFrames());
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	private void playTrack(Clip clip, SoundClipType clipType)
	{
		if (muted)
			return;
		
		clip.setFramePosition(clipType.getStartFrame());
		clip.start();
		TimerTask task = new TimerTask() {
	        @Override
			public void run() {
	            clip.stop();
	        }
	    };
	    
	    Timer timer = new Timer();
	    timer.schedule(task, clipType.getDurationInMs());
	}
	
	public void mute()
	{
		muted = true;
	}
	
	public void unmute()
	{
		muted = false;
	}
}