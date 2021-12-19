package main.presentation.game.sprite;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.data.entities.Race;
import main.data.entities.Team;
import main.presentation.ColorPairKey;
import main.presentation.TeamColorsManager;

public class PlayerAnimationManager
{
	private Map<CompositeSpriteKey, Animation<TextureRegion>> playerSpriteMappings;
	
	private static PlayerAnimationManager instance = null;
	
	private PlayerAnimationManager()
	{
		playerSpriteMappings = new HashMap<CompositeSpriteKey, Animation<TextureRegion>>();
	}
	
	public static PlayerAnimationManager getInstance()
	{
		if (instance == null)
			instance = new PlayerAnimationManager();
		
		return instance;
	}
	
	
	public void generateAndMapPlayerSprites(Team team)
	{
		generateAndMapPlayerSprites(new ColorPairKey(team));
	}
	
	private void generateAndMapPlayerSprites(ColorPairKey colorPairKey)
	{
		for (Race race : Race.values())
		{
			Texture spriteSheet = TeamColorsManager.getInstance().getSpriteSheet(colorPairKey, race);
			
			for (PlayerSpriteType spriteType : PlayerSpriteType.values())
			{
				Animation<TextureRegion> animation;
				
				if (spriteType.isStatic())
					animation = createStaticPlayerSprite(spriteSheet, spriteType.getIndex());
				else
					animation = createAnimatedPlayerSprite(spriteSheet, spriteType);
					
				
				CompositeSpriteKey key = new CompositeSpriteKey(colorPairKey, race, spriteType);
				playerSpriteMappings.remove(key);
				playerSpriteMappings.put(key, animation);
			}
		}
	}
	
	
	private TextureRegion getPlayerSpriteFrame(Texture spriteSheet, int index)
	{
		return new TextureRegion(spriteSheet, 0, 30 * index, 35, 30);
	}
	
	private Animation<TextureRegion> createStaticPlayerSprite(Texture spriteSheet, int index)
	{
		TextureRegion[] keyFrame = new TextureRegion[1];
		keyFrame[0] = getPlayerSpriteFrame(spriteSheet, index);
		return new Animation<TextureRegion>(0, keyFrame);
	}
	
	private Animation<TextureRegion> createAnimatedPlayerSprite(Texture spriteSheet, PlayerSpriteType spriteType)
	{
		int spriteIndex = spriteType.getIndex();
		TextureRegion[] keyFrames = new TextureRegion[spriteType.getFrames()];
		
		for (int i = 0; i < spriteType.getFrames(); i++)
			keyFrames[i] = getPlayerSpriteFrame(spriteSheet, spriteIndex + i);
		
		return new Animation<TextureRegion>(CrushPlayerSprite.PLAYER_ANIMATION_FRAME_DURATION, keyFrames);
	}
	
	public Animation<TextureRegion> getAnimation(Team team, Race race, PlayerSpriteType type)
	{
		ColorPairKey colorPairKey = new ColorPairKey(team);
		CompositeSpriteKey csk = new CompositeSpriteKey(colorPairKey, race, type);
		return getAnimation(csk);
	}
	
	private Animation<TextureRegion> getAnimation(CompositeSpriteKey csk)
	{
		if (!playerSpriteMappings.containsKey(csk))
			generateAndMapPlayerSprites(csk.colorPair);
		
		return playerSpriteMappings.get(csk);
	}
	
	private class CompositeSpriteKey
	{

		private ColorPairKey colorPair;
		private Race race;
		private PlayerSpriteType type;
		
		public CompositeSpriteKey(ColorPairKey colorPair, Race race, PlayerSpriteType type)
		{
			this.colorPair = colorPair;
			this.race = race;
			this.type = type;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((colorPair == null) ? 0 : colorPair.hashCode());
			result = prime * result + ((race == null) ? 0 : race.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompositeSpriteKey other = (CompositeSpriteKey) obj;
			if (colorPair == null)
			{
				if (other.colorPair != null)
					return false;
			} else if (!colorPair.equals(other.colorPair))
				return false;
			if (race != other.race)
				return false;
			if (type != other.type)
				return false;
			return true;
		}
		
		@Override
		public String toString()
		{
			return "CSK[" + colorPair + "," + race + "," + type + "]";
		}
	}
}
