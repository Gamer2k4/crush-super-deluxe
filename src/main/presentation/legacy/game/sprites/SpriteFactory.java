package main.presentation.legacy.game.sprites;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import main.data.entities.Player;
import main.data.entities.Race;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.common.image.LegacyImageFactory;
import main.presentation.legacy.common.LegacyUiConstants;

public class SpriteFactory
{
	private static final int TILE_SPRITE_AMOUNT = 192;
	
	private static SpriteFactory instance = null;
	private LegacyImageFactory imageFactory = LegacyImageFactory.getInstance();
	
	private BufferedImage[] tileSprites = new BufferedImage[TILE_SPRITE_AMOUNT];
	
	private BufferedImage[][] teamRaceSpriteSheets = new BufferedImage[3][8];
	
	private Map<CompositeSpriteKey, Sprite> playerSpriteMappings = new HashMap<CompositeSpriteKey, Sprite>();
	
	private Timer tileCyclingTimer = new Timer(250, null);
	private Timer playerAnimationTimer = new Timer(200, null);
	
	//TODO: eventually put these (and all others) into an array that just has indexes mapping to the value in the arena
	private Sprite electricTileSprite;
	private Sprite portalSprite;
	private Sprite stunStarsSprite;
	private Sprite slithGas;
	
	private List<Sprite> staticSprites = new ArrayList<Sprite>();
	private Map<Integer, Integer> spriteMappings = new HashMap<Integer, Integer>();
	
	private SpriteFactory()
	{
		defineTileSpriteImages();
		
		createStaticTileSprites();
		createAnimatedTileSprites();
		
		slithGas = new StaticSprite(tileSprites[79]);
		tileCyclingTimer.start();
		playerAnimationTimer.start();
	}

	public static SpriteFactory getInstance()
	{
		if (instance == null)
			instance = new SpriteFactory();
		
		return instance;
	}
	
	public Sprite getTileSprite(TileSpriteType tileSpriteType)
	{
		switch(tileSpriteType)
		{
		case PORTAL_TILE:
			return portalSprite;
		case ELECTRIC_TILE:
			return electricTileSprite;
		case STUN_STARS:
			return stunStarsSprite;
		case UNDEFINED_TYPE:
			return slithGas;
		default:
			return getStaticSprite(tileSpriteType);
		
		}
	}
	
	public Sprite getPlayerSprite(Race playerRace, PlayerSpriteType playerSpriteType, int teamIndex)
	{
		CompositeSpriteKey key = new CompositeSpriteKey(teamIndex, playerRace, playerSpriteType);
		Sprite sprite = playerSpriteMappings.get(key);
		return sprite;
	}
	
	public void setTeamColors(int teamIndex, Color mainColor, Color trimColor)
	{
		for (int race = 0; race < 8; race++)
		{
			teamRaceSpriteSheets[teamIndex][race] = LegacyColorReplacer.getInstance().setColors(getPlayerSpriteSheet(race), mainColor, trimColor, LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		}
		
		generateAndMapPlayerSprites(teamIndex);
	}
	
	private void generateAndMapPlayerSprites(int teamIndex)
	{
		for (Race race : Race.values())
		{
			BufferedImage spriteSheet = teamRaceSpriteSheets[teamIndex][race.getIndex()];
			
			for (PlayerSpriteType spriteType : PlayerSpriteType.values())
			{
				Sprite sprite;
				
				if (spriteType.isStatic())
					sprite = new StaticSprite(getPlayerSpriteFrame(spriteSheet, spriteType.getIndex()));
				else
					sprite = createdAnimatedPlayerSprite(spriteSheet, spriteType, playerAnimationTimer);
				
				CompositeSpriteKey key = new CompositeSpriteKey(teamIndex, race, spriteType);
				playerSpriteMappings.remove(key);
				playerSpriteMappings.put(key, sprite);
			}
		}
	}
	
	private BufferedImage getPlayerSpriteFrame(BufferedImage spriteSheet, int index)	//titled calls out PLAYER because those are 35 wide, while tiles are 36 wide
	{
		return spriteSheet.getSubimage(0, 30 * index, 35, 30);
	}

	private BufferedImage getPlayerSpriteSheet(int race)
	{
		switch (race)
		{
		case Player.RACE_CURMIAN:
			return imageFactory.getImage(ImageType.SPRITES_CURMIAN);
		case Player.RACE_DRAGORAN:
			return imageFactory.getImage(ImageType.SPRITES_DRAGORAN);
		case Player.RACE_GRONK:
			return imageFactory.getImage(ImageType.SPRITES_GRONK);
		case Player.RACE_HUMAN:
			return imageFactory.getImage(ImageType.SPRITES_HUMAN);
		case Player.RACE_KURGAN:
			return imageFactory.getImage(ImageType.SPRITES_KURGAN);
		case Player.RACE_NYNAX:
			return imageFactory.getImage(ImageType.SPRITES_NYNAX);
		case Player.RACE_SLITH:
			return imageFactory.getImage(ImageType.SPRITES_SLITH);
		case Player.RACE_XJS9000:
			return imageFactory.getImage(ImageType.SPRITES_XJS9000);
		}
		
		throw new IllegalArgumentException("No race sprites defined for race " + race);
	}

	private Sprite getStaticSprite(TileSpriteType tileSpriteType)
	{
		int spriteIndex = tileSpriteType.getIndex();
		int staticSpriteIndex = spriteMappings.get(spriteIndex);
		return staticSprites.get(staticSpriteIndex);
	}

	private void defineTileSpriteImages()
	{
		BufferedImage tileSheet = imageFactory.getImage(ImageType.CRUSH_TILES);
		
		for (int i = 0; i < TILE_SPRITE_AMOUNT; i++)
			tileSprites[i] = tileSheet.getSubimage(0, 30 * i, 36, 30);	//probably no need to deep copy
	}
	
	private void createStaticTileSprites()
	{
		for (TileSpriteType type : TileSpriteType.values())
		{
			if (type.isStatic())
			{
				StaticSprite sprite = new StaticSprite(tileSprites[type.getIndex()]);
				spriteMappings.put(type.getIndex(), staticSprites.size());
				staticSprites.add(sprite);
			}
		}
	}
	
	private void createAnimatedTileSprites()
	{
		electricTileSprite = createdAnimatedTileSprite(TileSpriteType.ELECTRIC_TILE, 4, tileCyclingTimer);
		portalSprite = createdAnimatedTileSprite(TileSpriteType.PORTAL_TILE, 4, tileCyclingTimer);
		stunStarsSprite = createStunStarsSprite(tileCyclingTimer);
	}
	
	private Sprite createStunStarsSprite(Timer timer)
	{
		AnimatedSprite sprite = new AnimatedSprite(timer);
		sprite.addFrame(tileSprites[TileSpriteType.STUN_STARS1.getIndex()]);
		sprite.addFrame(tileSprites[TileSpriteType.STUN_STARS2.getIndex()]);
		sprite.addFrame(tileSprites[TileSpriteType.STUN_STARS3.getIndex()]);
		sprite.addFrame(tileSprites[TileSpriteType.STUN_STARS4.getIndex()]);
		
		return sprite;
	}

	private AnimatedSprite createdAnimatedTileSprite(TileSpriteType spriteType, int frames, Timer timer)
	{
		int tileIndex = spriteType.getIndex();
		AnimatedSprite sprite = new AnimatedSprite(timer);
		
		for (int i = tileIndex; i < tileIndex + frames; i++)
			sprite.addFrame(tileSprites[i]);
		
		return sprite;
	}

	private AnimatedSprite createdAnimatedPlayerSprite(BufferedImage spriteSheet, PlayerSpriteType spriteType, Timer timer)
	{
		int spriteIndex = spriteType.getIndex();
		AnimatedSprite sprite = new AnimatedSprite(timer);
		
		for (int i = spriteIndex; i < spriteIndex + spriteType.getFrames(); i++)
			sprite.addFrame(getPlayerSpriteFrame(spriteSheet, i));
		
		return sprite;
	}
	
	private class CompositeSpriteKey
	{
		private int teamIndex;
		private Race race;
		private PlayerSpriteType type;
		
		public CompositeSpriteKey(int teamIndex, Race race, PlayerSpriteType type)
		{
			this.teamIndex = teamIndex;
			this.race = race;
			this.type = type;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((race == null) ? 0 : race.hashCode());
			result = prime * result + teamIndex;
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
			return (race == other.race && teamIndex == other.teamIndex && type == other.type);
		}
		
		@Override
		public String toString()
		{
			return "CSK[" + teamIndex + "," + race + "," + type + "]";
		}
	}
}
