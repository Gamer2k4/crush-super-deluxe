package main.presentation.teameditor;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import main.data.entities.Equipment;
import main.data.entities.Player;

public class EquipmentEquipPanel extends JPanel
{
	private static final long serialVersionUID = -4501789678084715284L;

	public static final String EQUIP_WEAR = "equipWear";
	public static final String EQUIP_REMOVE = "equipRemove";

	private static final int SLOT_X = 22;
	private static final int SLOT_Y = 3;
	private static final int TEXT_HEIGHT = 25;
	private static final int TEXT_WIDTH = 180;
	private static final int BUTTON_HEIGHT = TEXT_HEIGHT;
	private static final int BUTTON_WIDTH = 100;

	private JTextField[] wornField = new JTextField[4];
	private JLabel wornDetectionLabel;

	public EquipmentEquipPanel(ActionListener actionListener)
	{
		setLayout(null);
		setSize(335, 290);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		int slotSpacing = TEXT_HEIGHT + BUTTON_HEIGHT + 5;

		for (int i = 0; i < 4; i++)
		{
			wornField[i] = createEquipSlot(SLOT_X, SLOT_Y + (slotSpacing * i), i, actionListener);
		}

		wornDetectionLabel = setLabelFontSize(new JLabel(), 12, false);
		wornDetectionLabel = new JLabel();
		wornDetectionLabel.setLocation((getWidth() / 2) - (TEXT_WIDTH / 2), wornField[3].getY() + TEXT_HEIGHT + 7);
		wornDetectionLabel.setSize(TEXT_WIDTH, TEXT_HEIGHT);
		wornDetectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(wornDetectionLabel);

		JButton equipButton = createButton((getWidth() / 2) - (BUTTON_WIDTH / 2), wornDetectionLabel.getY() + TEXT_HEIGHT + 4, "Equip",
				EQUIP_WEAR, actionListener);
		add(equipButton);

		updateEquipment(null);
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

	private JTextField createEquipSlot(int x, int y, int equipType, ActionListener actionListener)
	{
		String[] typeNames = { "Armor", "Gloves", "Belt", "Boots" };

		JLabel nameLabel = new JLabel(typeNames[equipType]);
		nameLabel.setSize(TEXT_WIDTH, TEXT_HEIGHT);
		nameLabel.setLocation(x, y);

		JTextField nameDescField = new JTextField("");
		nameDescField.setSize(TEXT_WIDTH, TEXT_HEIGHT);
		nameDescField.setLocation(nameLabel.getLocation().x, nameLabel.getLocation().y + TEXT_HEIGHT);
		nameDescField.setEditable(false);

		JButton removeButton = createButton(nameDescField.getX() + TEXT_WIDTH + 10, nameDescField.getY(), "Remove", EQUIP_REMOVE
				+ equipType, actionListener);

		add(nameLabel);
		add(nameDescField);
		add(removeButton);

		return nameDescField;
	}

	private JButton createButton(int x, int y, String buttonText, String actionCommand, ActionListener actionListener)
	{
		JButton button = new JButton(buttonText);

		button.setLocation(x, y);
		button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		button.setActionCommand(actionCommand);
		button.addActionListener(actionListener);

		return button;
	}

	public void updateEquipment(Player player)
	{
		double detectionChance = 0;

		for (int i = 0; i < 4; i++)
		{
			int equipIndex = Equipment.EQUIP_BLANK;

			if (player != null)
				equipIndex = player.getEquipment(i);

			Equipment equip = Equipment.getEquipment(equipIndex);

			detectionChance += equip.detection;
			wornField[i].setText(equip.name);
		}

		if (player != null && player.hasSkill(Player.SKILL_SLY))
			detectionChance /= 2.0;

		wornDetectionLabel.setText("Detection: " + (int) (detectionChance + .5) + "%");
	}
}
