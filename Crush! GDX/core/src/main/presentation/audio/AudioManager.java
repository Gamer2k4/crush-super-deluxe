package main.presentation.audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import main.presentation.screens.ScreenType;

public class AudioManager
{
	private static AudioManager instance = null;
	
	private Map<SoundType, Sound> sounds;
	private Sound background = null;
	
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
	
	//TODO: this needs work
	public void updateBackground(ScreenType screen)
	{
		if (screen != ScreenType.GAME_PLAY)
			background = sounds.get(SoundType.THEME);
		
		if (background != null)
			background.loop();
	}
	
	public void playSound(SoundType soundType)	//TODO: siren for successful bin, horn for unsuccessful one
	{
		Sound sound = sounds.get(soundType);
		if (sound != null)
			sound.play();
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
