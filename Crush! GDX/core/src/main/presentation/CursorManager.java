package main.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;

public class CursorManager
{
	private Map<ImageType, Cursor> cursors;
	
	private static ImageType computerCursorFrame = ImageType.POINTER_COMP1;
	private static ImageType networkCursorFrame = ImageType.POINTER_NET1;
	
	private static Timer cursorTimer;
	
	private static CursorManager instance = null;
	
	private CursorManager()
	{
		cursors = new HashMap<ImageType, Cursor>();

		cursors.put(ImageType.POINTER_HIDDEN, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_HIDDEN), 0, 0));
		cursors.put(ImageType.POINTER_MAIN, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_MAIN), 0, 0));
		cursors.put(ImageType.POINTER_CRUSH, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_CRUSH), 0, 0));
		cursors.put(ImageType.POINTER_SWAP, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_SWAP), 0, 0));
		cursors.put(ImageType.POINTER_COMP1, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_COMP1), 0, 0));
		cursors.put(ImageType.POINTER_COMP2, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_COMP2), 0, 0));
		cursors.put(ImageType.POINTER_COMP3, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_COMP3), 0, 0));
		cursors.put(ImageType.POINTER_COMP4, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_COMP4), 0, 0));
		cursors.put(ImageType.POINTER_NET1, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_NET1), 0, 0));
		cursors.put(ImageType.POINTER_NET2, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_NET2), 0, 0));
		cursors.put(ImageType.POINTER_NET3, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_NET3), 0, 0));
		cursors.put(ImageType.POINTER_NET4, Gdx.graphics.newCursor(ImageFactory.getInstance().getPixMap(ImageType.POINTER_NET4), 0, 0));
		
		createCursorTimer();
	}
	
	public static CursorManager getInstance()
	{
		if (instance == null)
			instance = new CursorManager();
		
		return instance;
	}
	
	public Cursor getCursor(ImageType type)
	{
		Cursor cursor = cursors.get(type);
		
		if (cursor == null)
			return cursors.get(ImageType.POINTER_MAIN);
		
		return cursor;
	}
	
	private void createCursorTimer()
	{
		TimerTask task = new TimerTask() {
	        @Override
			public void run() {
	           if (computerCursorFrame == ImageType.POINTER_COMP1)
	        	   computerCursorFrame = ImageType.POINTER_COMP2;
	           else if (computerCursorFrame == ImageType.POINTER_COMP2)
	        	   computerCursorFrame = ImageType.POINTER_COMP3;
	           else if (computerCursorFrame == ImageType.POINTER_COMP3)
	        	   computerCursorFrame = ImageType.POINTER_COMP4;
	           else if (computerCursorFrame == ImageType.POINTER_COMP4)
	        	   computerCursorFrame = ImageType.POINTER_COMP1;
	           else if (networkCursorFrame == ImageType.POINTER_NET1)
	        	   networkCursorFrame = ImageType.POINTER_NET2;
	           else if (networkCursorFrame == ImageType.POINTER_NET2)
	        	   networkCursorFrame = ImageType.POINTER_NET3;
	           else if (networkCursorFrame == ImageType.POINTER_NET3)
	        	   networkCursorFrame = ImageType.POINTER_NET4;
	           else if (networkCursorFrame == ImageType.POINTER_NET4)
	        	   networkCursorFrame = ImageType.POINTER_NET1;
	        }
	    };
	    
		cursorTimer = new Timer();
		cursorTimer.scheduleAtFixedRate(task, 0, 250);
	}
	
	public static Cursor hidden()
	{
		return getInstance().getCursor(ImageType.POINTER_HIDDEN);
	}
	
	public static Cursor main()
	{
		return getInstance().getCursor(ImageType.POINTER_MAIN);
	}
	
	public static Cursor crush()
	{
		return getInstance().getCursor(ImageType.POINTER_CRUSH);
	}
	
	public static Cursor swap()
	{
		return getInstance().getCursor(ImageType.POINTER_SWAP);
	}
	
	public static Cursor computer()
	{
		return getInstance().getCursor(computerCursorFrame);
	}
	
	public void dispose()
	{
		for (ImageType key : cursors.keySet())
		{
			Cursor cursor = cursors.get(key);
			cursor.dispose();
		}
		
		cursors.clear();
	}
}
