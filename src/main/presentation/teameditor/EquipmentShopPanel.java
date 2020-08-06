package main.presentation.teameditor;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.data.entities.Equipment;
import main.presentation.common.ImagePanel;
import main.presentation.common.image.ImageType;
import main.presentation.teameditor.common.TeamUpdater;

public class EquipmentShopPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2855361429579259969L;

	private static final String PREV = "Prev";
	private static final String NEXT = "Next";
	private static final String PADS = "Armor";
	private static final String GLOVES = "Gloves";
	private static final String BELT = "Belt";
	private static final String BOOTS = "Boots";
	public static final String BUY = "Buy";
	public static final String SELL = "Sell";

	private static final int BUTTON_HEIGHT = 30;
	private static final int BUTTON_WIDTH = 72;

	private TeamUpdater teamUpdater;
	private ImagePanel gearImagePanel;
	
	private JLabel nameLabel;
	private JLabel detectionLabel;
	private JLabel costLabel;
	private JTextPane descriptionPane;
	
	private JButton buyButton;
	private JButton sellButton;

	private int currentType = 0;
	private int currentIndex = 0;

	private static final int[] typeCount = { 6, 5, 7, 5 };
	private static final int[][] gearIndexes = {
			{ Equipment.EQUIP_REINFORCED_ARMOR, Equipment.EQUIP_HEAVY_ARMOR, Equipment.EQUIP_SPIKED_ARMOR, Equipment.EQUIP_SURGE_ARMOR,
					Equipment.EQUIP_VORTEX_ARMOR, Equipment.EQUIP_REPULSOR_ARMOR },
			{ Equipment.EQUIP_SAAI_GLOVES, Equipment.EQUIP_REPULSOR_GLOVES, Equipment.EQUIP_MAGNETIC_GLOVES, Equipment.EQUIP_SURGE_GLOVES,
					Equipment.EQUIP_SPIKED_GLOVES },
			{ Equipment.EQUIP_MEDICAL_BELT, Equipment.EQUIP_FIELD_INTEGRITY_BELT, Equipment.EQUIP_BOOSTER_BELT,
					Equipment.EQUIP_BACKFIRE_BELT, Equipment.EQUIP_CLOAKING_BELT, Equipment.EQUIP_HOLOGRAM_BELT,
					Equipment.EQUIP_SCRAMBLER_BELT },
			{ Equipment.EQUIP_SAAI_BOOTS, Equipment.EQUIP_BOUNDER_BOOTS, Equipment.EQUIP_MAGNETIC_BOOTS, Equipment.EQUIP_SPIKED_BOOTS,
					Equipment.EQUIP_INSULATED_BOOTS } };

	public EquipmentShopPanel(TeamUpdater updater, ActionListener actionListener)
	{
		teamUpdater = updater;

		setLayout(null);
		setSize(335, 290);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		gearImagePanel = new ImagePanel(ImageType.GEAR_REINFORCED_PADS);
		gearImagePanel.setLocation(10, 45);
		gearImagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		add(gearImagePanel);
		
		createAndAddLabels();
		createAndAddButtons();
		createAndAddDescriptionTextPane();
		
		buyButton.addActionListener(actionListener);
		sellButton.addActionListener(actionListener);

		updateEquipment(Equipment.EQUIP_REINFORCED_ARMOR);
	}

	private void createAndAddLabels()
	{
		nameLabel = setLabelFontSize(new JLabel(), 12, true);
		nameLabel.setLocation(gearImagePanel.getX(), gearImagePanel.getY() - 35);
		nameLabel.setSize(gearImagePanel.getWidth(), 13);
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		detectionLabel = setLabelFontSize(new JLabel(), 12, false);
		detectionLabel.setLocation(nameLabel.getX(), nameLabel.getY() + 18);
		detectionLabel.setSize(nameLabel.getWidth(), nameLabel.getHeight());
		detectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		costLabel = setLabelFontSize(new JLabel(), 20, true);
		costLabel.setLocation(gearImagePanel.getX() + BUTTON_WIDTH + 10, gearImagePanel.getY() + gearImagePanel.getHeight() + 10);
		costLabel.setSize(48, BUTTON_HEIGHT);
		costLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		add(nameLabel);
		add(detectionLabel);
		add(costLabel);
	}
	
	private void createAndAddButtons()
	{
		int buttonX = 10;
		int buttonY = 10;
		int buttonDist = 40;

		add(createButton(gearImagePanel.getX(), gearImagePanel.getY() + gearImagePanel.getHeight() + 10, PREV));
		add(createButton(gearImagePanel.getX() + gearImagePanel.getWidth() - BUTTON_WIDTH,
				gearImagePanel.getY() + gearImagePanel.getHeight() + 10, NEXT));

		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(null);
		selectorPanel.setLocation(nameLabel.getX() + nameLabel.getWidth() + 10, nameLabel.getY());
		selectorPanel.setSize(BUTTON_WIDTH + (2 * buttonX), 170);
		selectorPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		selectorPanel.add(createButton(buttonX, buttonY, PADS));
		selectorPanel.add(createButton(buttonX, buttonY + (1 * buttonDist), GLOVES));
		selectorPanel.add(createButton(buttonX, buttonY + (2 * buttonDist), BELT));
		selectorPanel.add(createButton(buttonX, buttonY + (3 * buttonDist), BOOTS));
		
		JPanel buySellPanel = new JPanel();
		buySellPanel.setLayout(null);
		buySellPanel.setLocation(selectorPanel.getX(), selectorPanel.getY() + selectorPanel.getHeight() + 10);
		buySellPanel.setSize(BUTTON_WIDTH + (2 * buttonX), 90);
		buySellPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		buyButton = createButton(buttonX, buttonY, BUY);
		sellButton = createButton(buttonX, buttonY + (1 * buttonDist), SELL);
		
		buySellPanel.add(buyButton);
		buySellPanel.add(sellButton);

		add(selectorPanel);
		add(buySellPanel);
	}

	private JButton createButton(int x, int y, String actionCommand)
	{
		JButton button = new JButton(actionCommand);

		button.setLocation(x, y);
		button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);

		return button;
	}

	private JLabel setLabelFontSize(JLabel label, int fontSize, boolean isBold)
	{
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));

		if (!isBold)
		{
			labelFont = label.getFont();
			label.setFont(labelFont.deriveFont(labelFont.getStyle() ^ Font.BOLD));
		}

		return label;
	}
	
	private void createAndAddDescriptionTextPane()
	{
		JPanel descPane = new JPanel();
		descPane.setLayout(null);
		descPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		descPane.setLocation(gearImagePanel.getX(), gearImagePanel.getY() + gearImagePanel.getHeight() + 20 + BUTTON_HEIGHT);
		descPane.setSize(gearImagePanel.getWidth(), 72);
		
		descriptionPane = new JTextPane();
		descriptionPane.setLocation(15, 15);
		descriptionPane.setSize(descPane.getWidth() - 30, descPane.getHeight() - 30);
		descriptionPane.setBackground(TeamEditorPanel.BG_COLOR);
		
		StyledDocument doc = descriptionPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		descPane.add(descriptionPane);
		add(descPane);
	}

	private void updateEquipment(int equipmentIndex)
	{
		Equipment equip = Equipment.getEquipment(equipmentIndex);
		
		nameLabel.setText(equip.name);
		detectionLabel.setText("Detection: " + equip.detection + "%");
		costLabel.setText(equip.cost + "K");
		descriptionPane.setText(equip.description);
		
		gearImagePanel.updateImage(teamUpdater.getEquipmentImage(equipmentIndex));
	}

	private void changeGear(int changeAmount)
	{
		currentIndex += changeAmount;

		if (currentIndex >= typeCount[currentType])
			currentIndex = 0;
		if (currentIndex < 0)
			currentIndex = typeCount[currentType] - 1;

		updateEquipment(getSelectedEquipmentIndex());
	}

	private void changeType(int newType)
	{
		currentType = newType;
		changeGear(0);
		updateEquipment(getSelectedEquipmentIndex());
	}

	public int getSelectedEquipmentIndex()
	{
		return gearIndexes[currentType][currentIndex];
	}
	
	public void updatePanel()
	{
		changeGear(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command.equals(PREV))
			changeGear(-1);
		else if (command.equals(NEXT))
			changeGear(1);
		else if (command.equals(PADS))
			changeType(Equipment.EQUIP_ARMOR);
		else if (command.equals(GLOVES))
			changeType(Equipment.EQUIP_GLOVES);
		else if (command.equals(BELT))
			changeType(Equipment.EQUIP_BELT);
		else if (command.equals(BOOTS))
			changeType(Equipment.EQUIP_BOOTS);
	}
}
