package main.presentation.legacy.teamselect;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import main.data.entities.Team;
import main.presentation.common.image.ImageType;
import main.presentation.legacy.framework.ClickableRegion;
import main.presentation.legacy.framework.ScreenCommand;

public class ExhibitionTeamSelectScreen extends AbstractTeamSelectScreen
{
	protected static final Point coordsButtonBack = new Point(585, 342);
	protected static final Point coordsButtonStart = new Point(486, 362);

	public ExhibitionTeamSelectScreen(ActionListener listener)
	{
		super(listener, ImageType.BG_BG1, ImageType.SCREEN_EXHIBITION_START);
	}

	@Override
	protected void defineClickableRegions()
	{
		super.defineClickableRegions();
		createClickZone(new Rectangle(coordsButtonBack, buttonDimSmall2),
				ClickableRegion.smallButton2(coordsButtonBack, ScreenCommand.MAIN_SCREEN));
		createClickZone(new Rectangle(coordsButtonStart, buttonDimLarge),
				ClickableRegion.largeButton(coordsButtonStart, ScreenCommand.BEGIN_GAME));
	}

	@Override
	protected List<Point> getHelmetLocations()
	{
		List<Point> locations = new ArrayList<Point>();
		locations.add(new Point(305, 142));
		locations.add(new Point(205, 246));
		locations.add(new Point(405, 246));
		return locations;
	}

	// TODO: this will both be updated as functionality is added
	@Override
	public int getBudget()
	{
		return 900;
	}

	@Override
	public int getGoal()
	{
		return 1;
	}

	@Override
	public boolean isGameReadyToStart()
	{
		boolean readyToStart = false;

		int budget = getBudget();
		boolean budgetOkay = true;

		String overBudget = "";

		for (int i = 0; i < getTotalTeams(); i++)
		{
			Team team = getTeam(i);

			if (team.getValue() > budget)
			{
				budgetOkay = false;
				overBudget = overBudget + "\n" + team.teamName;
			}
		}

		int seasonWinner = getSeasonWinner();

		if (!budgetOkay) // TODO: being over budget doesn't matter if we're already in the exhibition (that is, if players got better).
		{
			JOptionPane.showMessageDialog(null, "The following teams are over budget:" + overBudget, "Teams Over Budget",
					JOptionPane.ERROR_MESSAGE);
		} else if (seasonWinner > -1) // TODO: extract this to the startup screen to display an actual victory screen
		{
			JOptionPane.showMessageDialog(null, "Team " + seasonWinner + " has won the exhibition!", "Exhibition Winner!",
					JOptionPane.WARNING_MESSAGE);
		} else
		{
			readyToStart = true;
		}
		
		return readyToStart;
	}

	@Override
	protected int getTotalTeams()
	{
		return 3;
	}

	@Override
	public int getSeasonWinner()
	{
		// TODO Auto-generated method stub - reference ExhibitionTeamSelectionScreen.java
		return -1;
	}
}
