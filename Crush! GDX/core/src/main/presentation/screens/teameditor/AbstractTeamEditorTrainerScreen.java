package main.presentation.screens.teameditor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import main.data.entities.Player;
import main.data.entities.Skill;
import main.presentation.ImageFactory;
import main.presentation.ImageType;
import main.presentation.common.ScreenCommand;
import main.presentation.common.SkillPrerequisiteValidator;
import main.presentation.game.GameText;
import main.presentation.game.StaticImage;
import main.presentation.game.sprite.CrushSprite;
import main.presentation.game.sprite.StaticSprite;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public abstract class AbstractTeamEditorTrainerScreen extends AbstractTeamEditorSubScreen
{
	private List<ImageButton> pageButtons = new ArrayList<ImageButton>();
	private Map<Skill, Point> buttonLocations = new HashMap<Skill, Point>();
	private Map<Skill, CrushSprite> buttonMasks = new HashMap<Skill, CrushSprite>();
	private Map<Skill, ImageButton> skillButtons = new HashMap<Skill, ImageButton>();
	private Map<Skill, GameText[]> skillDescriptions = new HashMap<Skill, GameText[]>();
	
	private Skill visibleSkillDescription = null;
	private GameText terrorFifthLine = null;
	private GameText juggernautFifthLine = null;
	private GameText stripFifthLine = null;
	private GameText judoFifthLine = null;
	private GameText leaderFifthLine = null;
	
	private TextureRegion arrowHighlight;
	private SkillPrerequisiteValidator skillPrereqValidator = new SkillPrerequisiteValidator();
	
	private static final int SKILL_BUTTON_WIDTH = 91;
	private static final int SKILL_BUTTON_HEIGHT = 15;
	
	protected AbstractTeamEditorTrainerScreen(TeamEditorParentScreen parentScreen)
	{
		super(parentScreen);
		defineSkillDescriptions();
		definePageButtons();
		
		Texture arrowSource = ImageFactory.getInstance().getTexture(ImageType.SCREEN_TEAM_EDITOR_POWER_MASK);
		arrowHighlight = new TextureRegion(arrowSource, 158, 43, 9, 19);
	}

	private void definePageButtons()
	{
		pageButtons.add(parentScreen.addClickZone(40, 307, 72, 17, ScreenCommand.POWER_VIEW));
		pageButtons.add(parentScreen.addClickZone(125, 307, 72, 17, ScreenCommand.AGILITY_VIEW));
		pageButtons.add(parentScreen.addClickZone(210, 307, 72, 17, ScreenCommand.PSYCHE_VIEW));
	}

	@Override
	protected void refreshContent()
	{
		skillPrereqValidator.setSensei(teamUpdater.hasSensei());
	}
	
	protected void defineSkillButton(Skill skill, int x, int y)
	{
		int yShift = 12;
		Point coords = new Point(x, y);
		buttonLocations.put(skill, coords);
		Texture buttonMaskSource = ImageFactory.getInstance().getTexture(getMaskTextureImageType());
		TextureRegion buttonMask = new TextureRegion(buttonMaskSource, coords.x, coords.y, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT);
		CrushSprite buttonMaskImage = new StaticSprite(new Point(coords.x, coords.y + yShift), buttonMask);
		buttonMasks.put(skill, buttonMaskImage);
		ImageButton skillButton = parentScreen.addButton(x, y + yShift, SKILL_BUTTON_WIDTH, SKILL_BUTTON_HEIGHT, false, true, ScreenCommand.gainSkill(skill), false);
		skillButtons.put(skill, skillButton);
	}

	@Override
	protected void handleCommand(ScreenCommand command)
	{
		// note that the skills have already been validated, so it shouldn't be possible to click a skill you can't get
		if (!command.isGainSkill())
			return;
		
		if (isCpuTeam())
			return;
		
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		Skill skillToGain = command.getSkillToGain();
		
		if (!skillPrereqValidator.isButtonEnabled(skillToGain, player))
			return;
		
		player.purchaseSkill(skillToGain);
		refreshContent();
	}
	
	@Override
	protected void handleMouseEnterEvent(ActionEvent event)
	{
		ScreenCommand command = ScreenCommand.fromActionEvent(event);
		visibleSkillDescription = command.getSkillToGain();
		refreshParentStage();
	}
	
	@Override
	protected void handleMouseExitEvent(ActionEvent event)
	{
		visibleSkillDescription = null;
		refreshParentStage();
	}

	@Override
	public List<ImageButton> getScreenButtons()
	{
		List<ImageButton> screenButtons = super.getScreenButtons();
		
		for (int i = 0; i < 3; i++)
		{
			ImageButton button = pageButtons.get(i);
			Point disableButtonCoords = getScreenClickedButtonCoords();
			
			if ((int) button.getX() == disableButtonCoords.x)
				continue;
			
			screenButtons.add(button);
		}
		
		for (Skill skill : buttonLocations.keySet())
		{
			screenButtons.add(skillButtons.get(skill));
		}

		return screenButtons;
	}
	
	@Override
	public List<Actor> getActors()
	{
		List<Actor> actors = super.getActors();
		
		StaticImage pressedButton = new StaticImage(ImageType.BUTTON_72x17_CLICKED, getScreenClickedButtonCoords());
		actors.add(pressedButton.getImage());
		
		return actors;
	}
	
	@Override
	public List<CrushSprite> getStaticSprites()
	{
		List<CrushSprite> staticSprites = super.getStaticSprites();
		
		Player player = teamUpdater.getCurrentlySelectedPlayer();
		
		if (player == null)
			return staticSprites;
		
		for (Skill skill : buttonLocations.keySet())
		{
			int buttonX = buttonMasks.get(skill).getX();
			int buttonY = buttonMasks.get(skill).getY();
			Point arrowLocation = new Point(buttonX + 41, buttonY + 17);
			CrushSprite arrowSprite = new StaticSprite(arrowLocation, arrowHighlight);
			
			if (skillPrereqValidator.isButtonEnabled(skill, player))
				staticSprites.add(arrowSprite);
			
			//Nynax players won't have Leader, but mask the button anyway if they have Hive Overseer instead.
			//The flow will then continue to the next iteration, since they'll fail the next check for already having the skill
			if (skill == Skill.LEADER && player.hasSkill(Skill.HIVE_OVERSEER))
			{
				staticSprites.add(buttonMasks.get(skill));
				staticSprites.add(arrowSprite);
			}
			
			if (!player.hasSkill(skill))
				continue;
			
			staticSprites.add(buttonMasks.get(skill));
			staticSprites.add(arrowSprite);
			
			//TODO: worry about adding the horizontal arrows later; vertical is good enough for now
		}
		
		return staticSprites;
	}
	
	@Override
	public List<GameText> getScreenTexts()
	{
		List<GameText> screenTexts = super.getScreenTexts();
		
		if (visibleSkillDescription == null)
			return screenTexts;
		
		List<GameText> descriptionLines = Arrays.asList(skillDescriptions.get(visibleSkillDescription));
		screenTexts.addAll(descriptionLines);
		
		if (visibleSkillDescription == Skill.TERROR)
			screenTexts.add(terrorFifthLine);
		else if (visibleSkillDescription == Skill.JUGGERNAUT)
			screenTexts.add(juggernautFifthLine);
		else if (visibleSkillDescription == Skill.STRIP)
			screenTexts.add(stripFifthLine);
		else if (visibleSkillDescription == Skill.JUDO)
			screenTexts.add(judoFifthLine);
		else if (visibleSkillDescription == Skill.LEADER)
			screenTexts.add(leaderFifthLine);
		
		return screenTexts;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(TeamUpdater.UPDATER_PLAYERS_CHANGED))
			refreshContent();
	}
	
	protected abstract ImageType getMaskTextureImageType();
	
	protected abstract Point getScreenClickedButtonCoords();
	
	private void defineSkillDescriptions()
	{
		addSkillDescription(Skill.TERROR, "This is the Terror skill.", "This skill costs 100 skill points, and", "at the start of each turn, there is a 33%", "chance that all adjacent opposing players");
		addSkillDescription(Skill.JUGGERNAUT, "This is the Juggernaut skill.", "This skill costs 80 skill points, and", "prevents a player from being knocked", "down or pushed from a check unless KO'd, injured");
		addSkillDescription(Skill.TACTICS, "This is the Tactics skill.", "This skill costs 40 skill points, and", "prevents opponents from getting assists", "on this player.");
		addSkillDescription(Skill.VICIOUS, "This is the Vicious skill.", "This skill costs 60 skill points, and", "adds to the injury type of a successful", "check.");
		addSkillDescription(Skill.BRUTAL, "This is the Brutal skill.", "This skill costs 40 skill points, and", "increases the ST score of the", "player by 10.");
		addSkillDescription(Skill.CHECKMASTER, "This is the Checkmaster skill.", "This skill costs 60 skill points, and", "increases the CH score of the", "player by 10.");
		addSkillDescription(Skill.STALWART, "This is the Stalwart skill.", "This skill costs 40 skill points, and", "increases the TG score of the", "player by 10.");
		addSkillDescription(Skill.GUARD, "This is the Guard skill.", "This skill costs 20 skill points, and", "increases the assist bonus for the player", "by 150%.");
		addSkillDescription(Skill.RESILIENT, "This is the Resilient skill.", "This skill costs 60 skill points, and", "subtracts from the injury type of a", "successful check.");
		addSkillDescription(Skill.CHARGE, "This is the Charge skill.", "This skill costs 80 skill points, and", "reduces a check cost to only 10AP.", "");
		addSkillDescription(Skill.BOXING, "This is the Boxing skill.", "This skill costs 20 skill points, and", "increases the RF score of the player", "by 10.");
		addSkillDescription(Skill.COMBO, "This is the Combo skill.", "This skill costs 40 skill points, and", "gives the player two opportunities", "to reaction check, instead of one.");
		addSkillDescription(Skill.QUICKENING, "This is the Quickening skill.", "This skill costs 60 skill points, and", "increases the AP score of the player", "by 10.");
		addSkillDescription(Skill.GYMNASTICS, "This is the Gymnastics skill.", "This skill costs 20 skill points, and", "increases the DA and JP score of the", "player by 10.");
		addSkillDescription(Skill.JUGGLING, "This is the Juggling skill.", "This skill costs 20 skill points, and", "increases the HD score of the player", "by 10.");
		addSkillDescription(Skill.SCOOP, "This is the Scoop skill.", "This skill costs 40 skill points, and", "allows the player to pick up the ball", "without any AP cost");
		addSkillDescription(Skill.STRIP, "This is the Strip skill.", "This skill costs 60 skill points, and", "gives the player a 33% chance of stripping", "the ball out of an adjacent player's hands");
		addSkillDescription(Skill.JUDO, "This is the Judo skill.", "This skill costs 40 skill points, and", "raises the player's CH to the same level", "as any opposing player who attempts to");
		addSkillDescription(Skill.FIST_OF_IRON, "This is the Fist of Iron skill.", "This skill costs 80 skill points, and", "gives the player a 16% chance of stunning", "an opponent during a checking attempt.");
		addSkillDescription(Skill.DOOMSTRIKE, "This is the Doomstrike skill.", "This skill costs 100 skill points, and", "gives the player a 16% chance of injuring", "an opponent during a checking attempt.");
		addSkillDescription(Skill.AWE, "This is the Awe skill.", "This skill costs 80 skill points, and", "causes opposing players to react only", "5% of the time to this player.");
		addSkillDescription(Skill.STOIC, "This is the Stoic skill.", "This skill costs 40 skill points, and", "makes a player immune to Terror and Awe.", "");
		addSkillDescription(Skill.LEADER, "This is the Leader/Hive Overseer skill", "This skill costs 60 skill points, and", "all players within 5 tiles receive 5 bonus to", "their CH.  Hive Overseer adds an additional +1 to the");
		addSkillDescription(Skill.SENSEI, "This is the Sensei skill.", "This skill costs 100 skill points, and", "makes all skills 10% easier for team members", "to achieve.  Effect is not cumulative.");
		addSkillDescription(Skill.SLY, "This is the Sly skill.", "This skill costs 40 skill points, and", "reduces the overall player equipment detection", "factor by 1/2.");
		addSkillDescription(Skill.INTUITION, "This is the Intuition skill.", "This skill costs 20 skill points, and", "doubles the player's chance of finding the ball.", "");
		addSkillDescription(Skill.HEALER, "This is the Healer skill.", "This skill costs 80 skill points, and", "gives each player a 2% chance, before each game,", "of healing all injured attributes.");
		addSkillDescription(Skill.KARMA, "This is the Karma skill.", "This skill costs 60 skill points, and", "allows a player to cheat death once per", "season.");
		
		terrorFifthLine = addSkillDescriptionLine(4, "collapse.");
		juggernautFifthLine = addSkillDescriptionLine(4, "or killed.");
		stripFifthLine = addSkillDescriptionLine(4, "at the end of each turn.");
		judoFifthLine = addSkillDescriptionLine(4, "check them.");
		leaderFifthLine = addSkillDescriptionLine(4, "Hive Mind effect. (Nynax only - replaces Leader Skill)");
	}

	private void addSkillDescription(Skill skill, String line1, String line2, String line3, String line4)
	{
		String[] texts = {line1, line2, line3, line4};
		GameText[] gameTexts = new GameText[4];
		
		for (int i = 0; i < 4; i++)
			gameTexts[i] = addSkillDescriptionLine(i, texts[i]);

		
		skillDescriptions.put(skill, gameTexts);
	}
	
	private GameText addSkillDescriptionLine(int line, String desc)
	{
		int xStart = GameText.getStringStartX(GameText.small2, 42, 300, desc);
		int yStart = 239 + (7 * line);
		return GameText.small2(new Point(xStart, yStart), LegacyUiConstants.COLOR_LEGACY_DULL_WHITE, desc);
	}
}
