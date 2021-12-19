package main.data;

import com.badlogic.gdx.assets.AssetManager;

//TODO: ultimately use this to pull in assets as needed for each screen, rather than loading them all at once
public class CrushAssetManager
{
	private static CrushAssetManager instance = null;
	private AssetManager manager;
	private boolean assetsLoaded = false;
	
	private CrushAssetManager()
	{
		manager = new AssetManager();
		loadAssets();
		assetsLoaded = true;
	}

	public static CrushAssetManager getInstance()
	{
		if (instance == null)
			instance = new CrushAssetManager();
		
		return instance;
	}
	
	public static AssetManager getAssetManager()
	{
		return getInstance().manager;
	}
	
	public boolean assetsLoaded()
	{
		return assetsLoaded;
	}
	
	private void loadAssets()
	{
//		manager.load
	}
}
