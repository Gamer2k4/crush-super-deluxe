package main.presentation.legacy.teameditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;

import main.presentation.common.image.ArenaImageGenerator;
import main.presentation.common.image.ColorMap;
import main.presentation.common.image.ImageType;
import main.presentation.common.image.ImageUtils;
import main.presentation.common.image.InGameColorMap;
import main.presentation.common.image.LegacyColorReplacer;
import main.presentation.legacy.common.FontType;
import main.presentation.legacy.common.LegacyUiConstants;
import main.presentation.teameditor.ArenaDisplayPanel;
import main.presentation.teameditor.common.TeamFileFilter;
import main.presentation.teameditor.common.TeamUpdater;

public class LegacyTeamEditorSettingsScreen extends LegacyTeamEditorScreenDecorator implements ActionListener
{
	private static final String TAG_TEAM_NAME = "_TEAM_NAME";
	private static final String TAG_TEAM_COACH = "_TEAM_COACH";

	private static final long serialVersionUID = 8616234598963655693L;

	private static final Point coordsTeamName = new Point(42, 19);
	private static final Point coordsCoachName = new Point(42, 73);
	
	private static final Point coordsButtonArena0 = new Point(127, 139);
	private static final Point coordsButtonArena1 = new Point(127, 152);
	private static final Point coordsButtonArena2 = new Point(127, 165);
	private static final Point coordsButtonArena3 = new Point(127, 178);
	
	private static final Point coordsButtonArenaSetA = new Point(134, 197);
	private static final Point coordsButtonArenaSetB = new Point(151, 197);
	private static final Point coordsButtonArenaSetC = new Point(168, 197);
	private static final Point coordsButtonArenaSetD = new Point(185, 197);
	private static final Point coordsButtonArenaSetE = new Point(202, 197);
	
	private static final Point coordsButtonSave = new Point(246, 106);
	private static final Point coordsButtonLoad = new Point(287, 106);
	
	private static final Point coordsMainColor = new Point(322, 158);
	private static final Point coordsTrimColor = new Point(322, 184);
	
	private static final Dimension buttonDimArenaName = new Dimension(88, 13);
	private static final Dimension buttonDimTeamColor = new Dimension(13, 13);
	private static final Dimension textTeamCoachName = new Dimension(152, 18);
	
	private ColorMap colorMap;
	private ArenaDisplayPanel mapPanel;
	private JFileChooser fileChooser;
	
	private LegacyTextField teamNameField;
	private LegacyTextField teamCoachField;
	
	private int arenaSet = 0;
	private int arenaIndexInSet = 0;
	private boolean updatingPrimaryColor = true;
	
	private static final int[][] teamColors = { { 0, 9, 18 },
											  { 1, 11, 29 },
											  { 2, 12, 21 },
											  { 3, 13, 19 },
											  { 5, 14, 26 },
											  { 7, 17, 28 }};
	
	private static final String[][] arenaNames = {{ "BRIDGES", "JACKAL'S LAIR", "CRISSICK", "WHIRLWIND"},
												  {"THE VOID", "OBSERVATORY", "ABYSS", "GADEL SPYRE"},
												  {"FULCRUM", "SAVANNA", "BARROW", "MAELSTROM"},
												  {"VAULT", "NEXUS", "DARKSUN", "BADLANDS"},
												  {"LIGHTWAY", "EYES", "DARKSTAR", "SPACECOM"}};
	
	public LegacyTeamEditorSettingsScreen(LegacyTeamEditorScreen screenToPaint)
	{
		super(screenToPaint);
		teamUpdater.addUpdateListener(this);

		colorMap = new InGameColorMap();
		mapPanel = new ArenaDisplayPanel(teamUpdater.getHomeField());
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new TeamFileFilter());
		
		teamNameField = new LegacyTextField(TAG_TEAM_NAME, 13, FontType.FONT_SMALL_TIGHT, this);
		teamCoachField = new LegacyTextField(TAG_TEAM_COACH, 13, FontType.FONT_SMALL_TIGHT, this);
		
		addDoubleClickZone(new Rectangle(coordsTeamName, textTeamCoachName), ScreenCommand.RENAME_TEAM);
		addDoubleClickZone(new Rectangle(coordsCoachName, textTeamCoachName), ScreenCommand.RENAME_COACH);
		
		addClickZone(new Rectangle(coordsButtonArena0, buttonDimArenaName), ScreenCommand.ARENA_NAME_0);
		addClickZone(new Rectangle(coordsButtonArena1, buttonDimArenaName), ScreenCommand.ARENA_NAME_1);
		addClickZone(new Rectangle(coordsButtonArena2, buttonDimArenaName), ScreenCommand.ARENA_NAME_2);
		addClickZone(new Rectangle(coordsButtonArena3, buttonDimArenaName), ScreenCommand.ARENA_NAME_3);
		
		addClickZone(new Rectangle(coordsButtonArenaSetA, buttonDimArenaSet), ScreenCommand.ARENA_SET_0);
		addClickZone(new Rectangle(coordsButtonArenaSetB, buttonDimArenaSet), ScreenCommand.ARENA_SET_1);
		addClickZone(new Rectangle(coordsButtonArenaSetC, buttonDimArenaSet), ScreenCommand.ARENA_SET_2);
		addClickZone(new Rectangle(coordsButtonArenaSetD, buttonDimArenaSet), ScreenCommand.ARENA_SET_3);
		addClickZone(new Rectangle(coordsButtonArenaSetE, buttonDimArenaSet), ScreenCommand.ARENA_SET_4);
		
		addClickZone(new Rectangle(coordsButtonSave, buttonDimSmallEditor), ScreenCommand.TEAM_SAVE);
		addClickZone(new Rectangle(coordsButtonLoad, buttonDimSmallEditor), ScreenCommand.TEAM_LOAD);
		
		addPaletteClickZones();
	}

	private void addPaletteClickZones()
	{
		int startX = 312;
		int startY = 211;
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				Rectangle zoneArea = new Rectangle(startX + (12 * i), startY + (12 * j), 9, 9);
				addClickZone(zoneArea, ScreenCommand.valueOf("TEAM_COLOR_" + j + "" + i));
			}
		}
		
		addClickZone(new Rectangle(coordsMainColor, buttonDimTeamColor), ScreenCommand.TEAM_MAIN_COLOR);
		addClickZone(new Rectangle(coordsTrimColor, buttonDimTeamColor), ScreenCommand.TEAM_TRIM_COLOR);
	}
	
	public void updateArenaSet(int set)
	{
		arenaSet = set;
	}
	
	public void refreshArena()
	{
		mapPanel.setArena(teamUpdater.getHomeField());
	}
	
	private void setArenaIndex()
	{
		int set = teamUpdater.getHomeField() / 4;
		arenaIndexInSet = teamUpdater.getHomeField() - (4 * set);
	}

	private void swapToCorrectArenaSet()
	{
		int set = teamUpdater.getHomeField() / 4;
		handleCommand(ScreenCommand.valueOf("ARENA_SET_" + set));
	}
	
	@Override
	protected void handleCommand(ScreenCommand command)
	{
		if (!buttonsEnabled)
			return;
		
		String commandString = command.name();
		System.out.println(commandString);
		
		if (ScreenCommand.TEAM_SAVE.equals(command))
			saveTeam();
		if (ScreenCommand.TEAM_LOAD.equals(command))
			loadTeam();
		
		if (ScreenCommand.RENAME_TEAM.equals(command) && !teamCoachField.isActive())
			teamNameField.activate();
		if (ScreenCommand.RENAME_COACH.equals(command) && !teamNameField.isActive())
			teamCoachField.activate();
		
		if (ScreenCommand.TEAM_MAIN_COLOR.equals(command))
			updatingPrimaryColor = true;
		if (ScreenCommand.TEAM_TRIM_COLOR.equals(command))
			updatingPrimaryColor = false;
		
		if (commandString.startsWith("ARENA_SET_"))	//TODO: in the original game, changing the set retains the selected index for the new set, so it updates the arena
		{
			updateArenaSet(Integer.parseInt(commandString.substring(10)));
			commandString = "ARENA_NAME_" + arenaIndexInSet;		//since we don't exit the method, this (intentionally) hits the next if statement 
		}
		
		if (commandString.startsWith("ARENA_NAME_"))
		{
			arenaIndexInSet = Integer.parseInt(commandString.substring(11));
			teamUpdater.setHomeField(4 * arenaSet + arenaIndexInSet);
			refreshArena();
		}
		
		if (commandString.startsWith("TEAM_COLOR_"))
		{
			int row = Integer.parseInt(commandString.substring(11, 12));
			int column = Integer.parseInt(commandString.substring(12));
			Color newColor = colorMap.getColor((teamColors[row][column] * 8) + 7);
			
			if (updatingPrimaryColor)
				teamUpdater.setMainColor(newColor);
			else
				teamUpdater.setTrimColor(newColor);
		}
	}

	private void paintArenaNames(Graphics2D graphics)
	{
		for (int i = 0; i < 4; i++)
		{
			String arenaName = arenaNames[arenaSet][i];
			Color nameColor = LegacyUiConstants.COLOR_LEGACY_GREY;
			
			if (teamUpdater.getHomeField() == (4 * arenaSet + i))
				nameColor = LegacyUiConstants.COLOR_LEGACY_GOLD;
			
			paintTextElement(graphics, 142, 143 + (13 * i), arenaName, FontType.FONT_SMALL2, nameColor);
		}
	}

	@Override
	protected void paintText(Graphics2D graphics)
	{
		if (teamNameField.isActive())
			graphics.drawImage(teamNameField.getTextImage(), 47, 23, null);
		else
			paintTextElement(graphics, 47, 23, teamUpdater.getTeamName(), FontType.FONT_SMALL_TIGHT, LegacyUiConstants.COLOR_LEGACY_GREY);
		
		if (teamCoachField.isActive())
			graphics.drawImage(teamCoachField.getTextImage(), 47, 77, null);
		else
			paintTextElement(graphics, 47, 77, teamUpdater.getTeamCoach(), FontType.FONT_SMALL_TIGHT, LegacyUiConstants.COLOR_LEGACY_GREY);
		
		paintArenaNames(graphics);
	}

	@Override
	protected void paintImages(Graphics2D graphics)
	{
		paintArena(graphics);
		paintSettingsPalette(graphics);
	}

	@Override
	protected void paintButtonShading(Graphics2D graphics)
	{
		for (int i = 0; i < 5; i++)
		{
			if (arenaSet == i)
				graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_ARENA_SET_CLICKED), 134 + (17 * i), 197, null);
			else
				graphics.drawImage(imageFactory.getImage(ImageType.BUTTON_ARENA_SET_NORMAL), 134 + (17 * i), 197, null);
		}
	}

	//TODO: this is not properly changing the color of the padded pixels (they're remaining transparent)
	private void paintArena(Graphics2D graphics)
	{
		refreshArena();
		BufferedImage arenaImage = mapPanel.getArenaImage(2);
		
		Color floorColor = new Color(0, 0, 0, 0);
		Color goalAndPortalColor = new Color(0, 128, 224);
		Color padColor = new Color(224, 224, 224);
		Color wallAndBinColor = new Color(103, 120, 143);
		Color tempFloorColor = new Color(0, 255, 0);
		
		arenaImage = ImageUtils.padImage(arenaImage, new Dimension(64, 64));
		Color imagePaddingColor = new Color(arenaImage.getRGB(0, 0));
//		System.out.println(arenaImage.getRGB(63, 63));
//		System.out.println(new Color(0, 0, 0, 0).getRGB());
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.FLOOR_COLOR, tempFloorColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, imagePaddingColor, ArenaImageGenerator.WALL_COLOR);
		arenaImage = ImageUtils.replaceColor(arenaImage, tempFloorColor, floorColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.GOAL_COLOR, goalAndPortalColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.TELE_COLOR, goalAndPortalColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.PAD_COLOR, padColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.WALL_COLOR, wallAndBinColor);
		arenaImage = ImageUtils.replaceColor(arenaImage, ArenaImageGenerator.BIN_COLOR, wallAndBinColor);
		
		graphics.drawImage(arenaImage, 52, 128, null);
	}

	private void paintSettingsPalette(Graphics2D graphics)
	{
		int startX = 312;
		int startY = 211;
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				Rectangle zoneArea = new Rectangle(startX + (12 * i), startY + (12 * j), 9, 9);
				
				graphics.setPaint(colorMap.getColor((teamColors[j][i] * 8) + 7));
				graphics.fill(zoneArea);
			}
		}
		
		BufferedImage teamColorDiamonds = imageFactory.getImage(ImageType.TEAM_COLOR_DIAMONDS);
		teamColorDiamonds = LegacyColorReplacer.getInstance().setColors(teamColorDiamonds, teamUpdater.getMainColor(), teamUpdater.getTrimColor(), LegacyUiConstants.COLOR_LEGACY_TRANSPARENT);
		graphics.drawImage(teamColorDiamonds, 321, 157,  null);
		
		int x1 = coordsMainColor.x;
		int x2 = x1 + buttonDimTeamColor.width - 1;
		int y1a = coordsMainColor.y;
		int y2a = y1a + buttonDimTeamColor.height - 1;
		int y1b = coordsTrimColor.y;
		int y2b = y1b + buttonDimTeamColor.height - 1;
		
		if (!updatingPrimaryColor)
		{
			y1a = coordsTrimColor.y;
			y2a = y1a + buttonDimTeamColor.height - 1;
			y1b = coordsMainColor.y;
			y2b = y1b + buttonDimTeamColor.height - 1;
		}
		
		drawDiamond(graphics, x1, y1a, x2, y2a, LegacyUiConstants.COLOR_LEGACY_GOLD);
		drawDiamond(graphics, x1, y1b, x2, y2b, LegacyUiConstants.COLOR_LEGACY_BLACK);
	}
	
	private void drawDiamond(Graphics2D g, int x1, int y1, int x2, int y2, Color color)
	{
	    int x = (x1+x2)/2;
	    int y = (y1+y2)/2;
	    g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
	    g.setColor(color);
	    g.setStroke(new BasicStroke(1));
	    g.drawLine(x1, y , x , y1);
	    g.drawLine(x , y1, x2, y );
	    g.drawLine(x2, y , x , y2);
	    g.drawLine(x , y2, x1, y );
	}
	
	private void saveTeam()
	{
		//TODO: somehow default to only *.cdst when saving (at least until I can figure out the full legacy format)
		
		int returnValue = fileChooser.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			teamUpdater.saveTeam(file);
		}

		fileChooser.setSelectedFile(null);
	}

	private void loadTeam()
	{
		int returnValue = fileChooser.showOpenDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			teamUpdater.loadTeam(file);
		}

		fileChooser.setSelectedFile(null);
		refreshTeam();
	}
	
	private void refreshTeam()
	{
		setArenaIndex();		
		swapToCorrectArenaSet();
		refreshArena();
	}

	@Override
	public void actionPerformed(ActionEvent event)	//triggered when a new team is loaded
	{
		String command = event.getActionCommand();
		
		if (TeamUpdater.UPDATE_ACTION.equals(command))
			refreshTeam();
		
		if (command.equals(LegacyTextField.SUBMIT_ACTION + TAG_TEAM_NAME))
			teamUpdater.setTeamName(teamNameField.getText());
		
		if (command.equals(LegacyTextField.SUBMIT_ACTION + TAG_TEAM_COACH))
			teamUpdater.setTeamCoach(teamCoachField.getText());
	}

	@Override
	protected void keyAction(ActionEvent keyAction)
	{
		System.out.println("in settings!");
		
		if (teamNameField.isActive())
			teamNameField.pressKey(keyAction);
		
		if (teamCoachField.isActive())
			teamCoachField.pressKey(keyAction);
	}
	
	@Override
	public void mousePressed(MouseEvent event)
	{
		teamNameField.deactivate();
		teamCoachField.deactivate();
		super.mousePressed(event);
	}
}
