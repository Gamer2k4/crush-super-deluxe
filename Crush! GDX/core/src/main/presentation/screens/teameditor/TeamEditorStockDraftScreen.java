package main.presentation.screens.teameditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Team;
import main.data.factory.PlayerFactory;
import main.presentation.ImageType;
import main.presentation.audio.AudioManager;
import main.presentation.audio.SoundType;
import main.presentation.common.ScreenCommand;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.PlayerAnimationManager;
import main.presentation.game.sprite.PlayerSpriteType;
import main.presentation.game.sprite.StaticSprite;
import main.presentation.legacy.common.LegacyUiConstants;

public class TeamEditorStockDraftScreen extends AbstractTeamEditorDraftScreen
{
	private List<Point> spriteLocations;
	private List<ImageButton> buttons;
	private GameText[][] flavorText;
	
	private int currentRace = 0;
	
	private AudioManager audioManager = AudioManager.getInstance();
	
	protected TeamEditorStockDraftScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		subScreenImage = new StaticImage(ImageType.SCREEN_TEAM_EDITOR_DRAFT, screenOrigin);
		defineSpriteLocations();
		defineFlavorText();
		defineButtons();
		refreshContent();
	}

	private void defineSpriteLocations()
	{
		spriteLocations = new ArrayList<Point>();
		spriteLocations.add(new Point(22, 32));
		spriteLocations.add(new Point(22, 77));
		spriteLocations.add(new Point(22, 122));
		spriteLocations.add(new Point(22, 167));
		spriteLocations.add(new Point(77, 54));
		spriteLocations.add(new Point(77, 99));
		spriteLocations.add(new Point(77, 144));
		spriteLocations.add(new Point(77, 189));
	}

	private void defineFlavorText()
	{
		flavorText = new GameText[8][5];
		
		for (Race race : Race.values())
		{
			int index = race.getIndex();
			PlayerFlavorStats flavorStats = PlayerFlavorStats.getFlavorStats(race);
			int worldCenteredX = GameText.getStringStartX(GameText.small2, 211, 86, flavorStats.world);
			int specialCenteredX = GameText.getStringStartX(GameText.small2, 211, 86, flavorStats.special);
			
			flavorText[index][0] = GameText.small2(new Point(257, 129), LegacyUiConstants.COLOR_LEGACY_GOLD, flavorStats.height);
			flavorText[index][1] = GameText.small2(new Point(257, 137), LegacyUiConstants.COLOR_LEGACY_GOLD, flavorStats.weight);
			flavorText[index][2] = GameText.small2(new Point(worldCenteredX, 153), LegacyUiConstants.COLOR_LEGACY_GOLD, flavorStats.world);
			flavorText[index][3] = GameText.small2(new Point(specialCenteredX, 169), LegacyUiConstants.COLOR_LEGACY_GOLD, flavorStats.special);
		}
	}

	private void defineButtons()
	{
		buttons = new ArrayList<ImageButton>();
		
		for (Race race : Race.values())
		{
			buttons.add(defineClickZoneForRace(race));
		}
		
		buttons.add(parentScreen.addButton(37, 137, 287, false, ScreenCommand.HIRE_PLAYER));
		buttons.add(parentScreen.addButton(37, 137, 308, false, ScreenCommand.FIRE_PLAYER));
	}
	
	private ImageButton defineClickZoneForRace(Race race)
	{
		int raceIndex = race.getIndex();
		Point coords = spriteLocations.get(raceIndex);
		ScreenCommand command = ScreenCommand.stockDraftSelect(race);
		
		return parentScreen.addClickZone(coords.x, coords.y, 36, 30, command);
	}

	@Override
	protected void refreshContent()
	{
		setCurrentRace(Race.HUMAN);
	}
	
	private void setCurrentRace(Race race)
	{
		currentRace = race.getIndex();
		draftee = new Player(race, "DRAFTEE");
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		Player playerToHire = PlayerFactory.getInstance().createPlayerWithRandomName(Race.getRace(currentRace));
		
		if (command == ScreenCommand.HIRE_PLAYER && canUserEditTeam())
			teamUpdater.hirePlayerAndAdvanceSlot(playerToHire);
		else if (command == ScreenCommand.FIRE_PLAYER && canUserEditTeam())
			teamUpdater.fireCurrentlySelectedPlayer();
		
		if (!command.isStockDraftSelect())
			return;
		
		Race selectedRace = command.getRaceToDraft();
		setCurrentRace(selectedRace);
		playRaceNameAudio(selectedRace);
	}

	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		screenButtons.addAll(buttons);

		return screenButtons;
	}
	
	@Override
	public List<CrushSprite> getStaticSprites()
	{
		List<CrushSprite> staticSprites = super.getStaticSprites();
		
		Race race = Race.getRace(currentRace);
		Team team = teamUpdater.getTeam();
		TextureRegion spriteTexture = PlayerAnimationManager.getInstance().getSprite(team, race, PlayerSpriteType.WALK_BOTH_E);
		staticSprites.add(new StaticSprite(spriteLocations.get(currentRace), spriteTexture));
		
		return staticSprites;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
	
		screenTexts.addAll(getPlayerAttributeTexts());
		
		screenTexts.add(flavorText[currentRace][0]);
		screenTexts.add(flavorText[currentRace][1]);
		screenTexts.add(flavorText[currentRace][2]);
		screenTexts.add(flavorText[currentRace][3]);
		
		return screenTexts;
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		Image playerImage = getPlayerImage(teamUpdater.getMainColor(), teamUpdater.getTrimColor());
		actors.add(playerImage);
		
		return actors;
	}

	private void playRaceNameAudio(Race selectedRace)
	{
		switch (selectedRace)
		{
		case CURMIAN:
			audioManager.playSound(SoundType.CURVOC);
			return;
		case DRAGORAN:
			audioManager.playSound(SoundType.DRGVOC);
			return;
		case GRONK:
			audioManager.playSound(SoundType.GRKVOC);
			return;
		case HUMAN:
			audioManager.playSound(SoundType.HUMVOC);
			return;
		case KURGAN:
			audioManager.playSound(SoundType.KURVOC);
			return;
		case NYNAX:
			audioManager.playSound(SoundType.NYNVOC);
			return;
		case SLITH:
			audioManager.playSound(SoundType.SLTVOC);
			return;
		case XJS9000:
			audioManager.playSound(SoundType.XJSVOC);
			return;
		default:
			return;
		}
	}
}
