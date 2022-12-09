package main.presentation.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import main.execute.DebugConstants;
import main.presentation.common.Logger;

public class AudioManager
{
	private static AudioManager instance = null;
	
	private Map<SoundType, Sound> sounds;
	
	private SoundType loopingSound = null;
	
	private AudioManager()
	{
		sounds = new HashMap<SoundType, Sound>();
		
		for (SoundType type : SoundType.values())
		{
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/" + type.name().toLowerCase() + ".wav"));
			sounds.put(type, sound);
		}
	}
	
	public static AudioManager getInstance()
	{
		if (instance == null)
			instance = new AudioManager();
		
		return instance;
	}
	
	public long loopSound(SoundType soundType)
	{
		if (!DebugConstants.AUDIO_ON)
			return -1;
		
		if (loopingSound == soundType)	//already looping this sound
			return -1;
		
		stopSound(loopingSound);
		loopingSound = soundType;
		
		Sound sound = sounds.get(soundType);
		
		if (sound != null)
			sound.loop();
		
		return -1;
	}
	
	public long playSound(SoundType soundType)
	{
		if (!DebugConstants.AUDIO_ON)
			return -1;
		
		Sound sound = sounds.get(soundType);
		
		if (sound != null)
			return sound.play();
		
		return -1;
	}
	
	public void stopSound(SoundType soundType)
	{
		if (soundType == null)
			return;
		
		if (soundType == loopingSound)
			loopingSound = null;
		
		Sound sound = sounds.get(soundType);
		sound.stop();
	}
	
	public void dispose()
	{
		for (SoundType type : sounds.keySet())
		{
			Sound sound = sounds.get(type);
			sound.dispose();
		}
		
		sounds.clear();
	}
}
