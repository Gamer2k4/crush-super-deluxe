package main.presentation.game.sprite;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CrushAnimatedTile extends CrushTile
{
	private Animation<TextureRegion> animation = null;
	private boolean shouldLoop = false;
	
	private float offsetFromGlobalTimer = 0f;
	private static float globalRecurringTimer = 0f;
	private static Map<TileSpriteType, CrushAnimatedTile> staticAnimations = null;
	
	private static final float ANIMATED_TILE_FRAME_DURATION = .250f;
	
	private static Map<TileSpriteType, TextureRegion[]> animatedTiles = new HashMap<TileSpriteType, TextureRegion[]>();
	
	private CrushAnimatedTile(Point coords, TextureRegion[] keyFrames)
	{
		this(coords, keyFrames, false);
	}
	
	private CrushAnimatedTile(Point coords, TextureRegion[] keyFrames, float frameDuration)
	{
		this(coords, keyFrames, frameDuration, false);
	}
	
	private CrushAnimatedTile(Point coords, TextureRegion[] keyFrames, boolean shouldLoop)
	{
		this(coords, keyFrames, ANIMATED_TILE_FRAME_DURATION, shouldLoop);
	}
	
	private CrushAnimatedTile(Point coords, TextureRegion[] keyFrames, float frameDuration, boolean shouldLoop)
	{
		super(coords, keyFrames[0]);
		this.shouldLoop = shouldLoop;
		this.animation = new Animation<TextureRegion>(frameDuration, keyFrames);
	}
	
	private static CrushAnimatedTile getStaticAnimation(TileSpriteType type)
	{
		if (staticAnimations == null)
			generateAnimations();
		
		CrushAnimatedTile animation = staticAnimations.get(type);
		animation.restart();
		return animation;
	}
	
	private static void generateAnimations()
	{
		staticAnimations = new HashMap<TileSpriteType, CrushAnimatedTile>();
		staticAnimations.put(TileSpriteType.WARP_ANIMATION, createWarpAnimation(CrushSprite.OFFSCREEN_COORDS));
	}

	@Override
	public TextureRegion getImage()
	{
		return animation.getKeyFrame(globalRecurringTimer + offsetFromGlobalTimer, shouldLoop);
	}
	
	public void restart()
	{
		//a little janky, but basically sets the keyframe timer to 0 for this particular animation
		offsetFromGlobalTimer = -1 * globalRecurringTimer;
	}
	
	public static CrushTile createTile(Point coords, int index)
	{
		return createTile(coords, TileSpriteType.getTileSpriteType(index));
	}
	
	public static CrushTile createTile(Point coords, TileSpriteType type)
	{
		if (animatedTiles.get(type) != null)
			return new CrushAnimatedTile(coords, animatedTiles.get(type), true);
		
		int index = type.getIndex();
		
		if (type.isStatic())
			return CrushTile.createTile(coords, index);
		
		TextureRegion[] keyFrames = new TextureRegion[type.getFrameCount()];
		
		for (int i = 0; i < type.getFrameCount(); i++)
		{
			TextureRegion keyFrame = getTextureRegion(index + i);
			keyFrames[i] = keyFrame;
		}
		
		CrushAnimatedTile animatedTile = new CrushAnimatedTile(coords, keyFrames, true);	//loop by default, but most created animations won't follow this flow (so they won't loop)
		animatedTiles.put(type, keyFrames);
		
		return animatedTile;
	}
	
	//consider extracting common code between here and above, depending on how many exception cases there are
	private static CrushAnimatedTile createWarpAnimation(Point coords)
	{
		int frames = TileSpriteType.WARP_ANIMATION.getFrameCount();
		TextureRegion[] keyFrames = new TextureRegion[frames];
		
		for (int i = 0; i < frames; i++)
		{
			TextureRegion keyFrame = getTextureRegion(TileSpriteType.WARP_ANIMATION.getIndex() + (4 * i));
			keyFrames[i] = keyFrame;
		}
		
		CrushAnimatedTile animatedTile = new CrushAnimatedTile(coords, keyFrames, .075f);
		animatedTiles.put(TileSpriteType.WARP_ANIMATION, keyFrames);
		
		return animatedTile;
	}
	
	public static CrushAnimatedTile warpAnimation()
	{
		return getStaticAnimation(TileSpriteType.WARP_ANIMATION);
	}
	
	public boolean isActive()
	{
		offsetFromGlobalTimer += .0000000000001f;	//done to prevent the animation from locking
		float elapsedAnimationTime = globalRecurringTimer + offsetFromGlobalTimer;
		
		if (animation.isAnimationFinished(elapsedAnimationTime))
			return false;
		
		return true;
	}

	public static void increaseElapsedTime(float increment)
	{
		globalRecurringTimer += increment;
	}
}
