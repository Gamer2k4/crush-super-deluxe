package main.presentation.teameditor;

import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import main.data.entities.Player;

public class DraftPlayerInfoPanel extends AbstractPlayerInfoPanel
{
	private static final long serialVersionUID = 1416276204171667448L;

	private FlavorStats[] flavorStats = new FlavorStats[8];

	private JPanel namePane;
	private JLabel raceLabel;

	private JLabel APlabel;
	private JLabel CHlabel;
	private JLabel STlabel;
	private JLabel TGlabel;
	private JLabel RFlabel;
	private JLabel JPlabel;
	private JLabel HDlabel;
	private JLabel DAlabel;

	private JLabel heightLabel;
	private JLabel weightLabel;
	private JLabel worldLabel;
	private JLabel specialLabel;

	private JPanel costPane;
	private JLabel costLabel;

	public DraftPlayerInfoPanel(int imageSize, int labelWidth, int labelHeight)
	{
		super(imageSize, labelWidth, labelHeight, 24);
		loadFlavorStats();

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLayout(null);
		setSize(280, 365);

		playerLabelWidth = 40;
		playerLabelHeight = 30;

		createRaceNameLabel();

		imagePanel.setLocation(namePane.getX(), namePane.getHeight() + 10);
		add(imagePanel);

		createAttributeLabels();
		createFlavorStatsLabels();
		createCostLabel();
	}

	public void updatePanel(int playerRace, BufferedImage playerImage)
	{
		Player player = new Player(playerRace, "DRAFTEE");

		raceLabel.setText(Player.races[playerRace].toUpperCase());

		APlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_AP)));
		CHlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_CH)));
		STlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_ST)));
		TGlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_TG)));
		RFlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_RF)));
		JPlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_JP)));
		HDlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_HD)));
		DAlabel.setText(String.valueOf(player.getAttributeWithoutModifiers(Player.ATT_DA)));

		heightLabel.setText(flavorStats[playerRace].height + " Ft");
		weightLabel.setText(flavorStats[playerRace].weight + " Lbs");
		worldLabel.setText(flavorStats[playerRace].world);
		specialLabel.setText(flavorStats[playerRace].special);
		
		costLabel.setText(player.getSalary() + "K");

		setRosterImage(player, playerImage);

		repaint();
	}

	private void loadFlavorStats()
	{
		flavorStats[0] = new FlavorStats("2.8", 85, "Curmadon", "High Jump");
		flavorStats[1] = new FlavorStats("7.1", 145, "Bakus", "Pop Up");
		flavorStats[2] = new FlavorStats("7.0", 560, "Daboo", "Regenerate");
		flavorStats[3] = new FlavorStats("6.0", 200, "Earth", "Uncommon Valor");
		flavorStats[4] = new FlavorStats("4.2", 275, "Kra", "Blood Lust");
		flavorStats[5] = new FlavorStats("4.9", 110, "Volticon", "Hive Mind");
		flavorStats[6] = new FlavorStats("5.4", 260, "Slogotha", "Death Reek");
		flavorStats[7] = new FlavorStats("6.0", 375, "XJS - Fab. 9", "Gyro Stabilizer");
	}

	private void createRaceNameLabel()
	{
		raceLabel = setLabelFontSize(new JLabel(), 24, true);
		raceLabel.setSize(playerImageSize, 25);
		raceLabel.setLocation(0, 2);
		raceLabel.setHorizontalAlignment(SwingConstants.CENTER);

		namePane = new JPanel();
		namePane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		namePane.setLayout(null);
		namePane.setSize(playerImageSize, raceLabel.getHeight() + 4);
		namePane.setLocation(playerLabelWidth + 20, 0);
		namePane.add(raceLabel);

		add(namePane);
	}

	private void createAttributeLabels()
	{
		JLabel APtitle = createPlayerPaneTextLabel("AP", new JLabel());
		APtitle.setLocation(10, imagePanel.getY() + 10);
		add(APtitle);

		APlabel = createPlayerPaneValueLabel(APtitle);
		add(APlabel);

		JLabel CHtitle = createPlayerPaneTextLabel("CH", APlabel);
		add(CHtitle);

		CHlabel = createPlayerPaneValueLabel(CHtitle);
		add(CHlabel);

		JLabel STtitle = createPlayerPaneTextLabel("ST", CHlabel);
		add(STtitle);

		STlabel = createPlayerPaneValueLabel(STtitle);
		add(STlabel);

		JLabel TGtitle = createPlayerPaneTextLabel("TG", STlabel);
		add(TGtitle);

		TGlabel = createPlayerPaneValueLabel(TGtitle);
		add(TGlabel);

		JLabel RFtitle = createPlayerPaneTextLabel("RF", new JLabel());
		RFtitle.setLocation(namePane.getX() + playerImageSize + 10, APtitle.getY());
		add(RFtitle);

		RFlabel = createPlayerPaneValueLabel(RFtitle);
		add(RFlabel);

		JLabel JPtitle = createPlayerPaneTextLabel("JP", RFlabel);
		add(JPtitle);

		JPlabel = createPlayerPaneValueLabel(JPtitle);
		add(JPlabel);

		JLabel HDtitle = createPlayerPaneTextLabel("HD", JPlabel);
		add(HDtitle);

		HDlabel = createPlayerPaneValueLabel(HDtitle);
		add(HDlabel);

		JLabel DAtitle = createPlayerPaneTextLabel("DA", HDlabel);
		add(DAtitle);

		DAlabel = createPlayerPaneValueLabel(DAtitle);
		add(DAlabel);
	}

	private void createFlavorStatsLabels()
	{
		JPanel flavorPane = new JPanel();
		flavorPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		flavorPane.setLayout(null);
		flavorPane.setSize(playerImageSize, playerImageSize - 25);
		flavorPane.setLocation(imagePanel.getX(), imagePanel.getY() + playerImageSize);

		playerLabelWidth = playerImageSize;
		playerLabelHeight = 20;
		playerLabelFontSize = 16;

		JPanel flavorTitlePane = new JPanel();
		flavorTitlePane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		flavorTitlePane.setLayout(null);
		flavorTitlePane.setSize(playerLabelWidth, playerLabelHeight + 5);
		flavorTitlePane.setLocation(0, 0);
		flavorPane.add(flavorTitlePane);

		JLabel traitsTitle = createPlayerPaneTextLabel("Species Traits", new JLabel());
		traitsTitle.setLocation(0, 0);
		flavorTitlePane.add(traitsTitle);

		int playerHalfWidth = playerLabelWidth / 2;
		playerLabelHeight = 15;
		playerLabelFontSize = 12;

		JLabel heightTitle = createPlayerPaneTextLabel("Avg. Ht:", traitsTitle);
		heightTitle.setLocation(heightTitle.getX(), heightTitle.getY() + 10);
		heightTitle.setSize(playerHalfWidth, playerLabelHeight);
		heightTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		flavorPane.add(heightTitle);

		heightLabel = createPlayerPaneValueLabel(heightTitle);
		heightLabel.setLocation(playerHalfWidth + 5, heightTitle.getY());
		heightLabel.setSize(playerHalfWidth, playerLabelHeight);
		heightLabel.setHorizontalAlignment(SwingConstants.LEFT);
		flavorPane.add(heightLabel);

		JLabel weightTitle = createPlayerPaneTextLabel("Avg. Wt:", heightTitle);
		weightTitle.setSize(playerHalfWidth, playerLabelHeight);
		weightTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		flavorPane.add(weightTitle);

		weightLabel = createPlayerPaneValueLabel(weightTitle);
		weightLabel.setLocation(playerHalfWidth + 5, weightTitle.getY());
		weightLabel.setSize(playerHalfWidth, playerLabelHeight);
		weightLabel.setHorizontalAlignment(SwingConstants.LEFT);
		flavorPane.add(weightLabel);

		JLabel worldTitle = createPlayerPaneTextLabel("Homeworld:", weightTitle);
		flavorPane.add(worldTitle);

		worldLabel = createPlayerPaneValueLabel(worldTitle);
		flavorPane.add(worldLabel);

		JLabel specialTitle = createPlayerPaneTextLabel("Special:", worldLabel);
		flavorPane.add(specialTitle);

		specialLabel = createPlayerPaneValueLabel(specialTitle);
		flavorPane.add(specialLabel);

		add(flavorPane);
	}

	private void createCostLabel()
	{
		int costStart = 15;
		int costWidth = 70;
		
		JLabel costText = setLabelFontSize(new JLabel(), 20, true);
		costText.setSize(costWidth, 20);
		costText.setLocation(costStart, 3);
		costText.setHorizontalAlignment(SwingConstants.RIGHT);
		costText.setText("COST: ");
		
		costLabel = setLabelFontSize(new JLabel(), 20, false);
		costLabel.setSize(costWidth, 20);
		costLabel.setLocation(costStart + costWidth, costText.getY());
		costLabel.setHorizontalAlignment(SwingConstants.LEFT);

		costPane = new JPanel();
		costPane.setLayout(null);
		costPane.setSize(costWidth * 2, costText.getHeight() + 5);
		costPane.setLocation((this.getWidth() / 2) - (costWidth), 335);
		costPane.add(costText);
		costPane.add(costLabel);

		add(costPane);
	}

	private class FlavorStats
	{
		public String height;
		public int weight;
		public String world;
		public String special;

		public FlavorStats(String height, int weight, String world, String special)
		{
			this.height = height;
			this.weight = weight;
			this.world = world;
			this.special = special;
		}
	}
}
