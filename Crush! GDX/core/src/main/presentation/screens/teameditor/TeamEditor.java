package main.presentation.screens.teameditor;

import main.data.entities.Team;
import main.presentation.screens.ScreenType;
import main.presentation.screens.teameditor.utilities.TeamUpdater;

public interface TeamEditor
{
	public void setTeamIndex(int index);
	public int getTeamIndex();
	public void setTeam(Team team);
	public Team getTeam();
	public TeamUpdater getTeamUpdater();
	public void setLoadEnabled(boolean isEnabled);
	public void setBudget(int budget);
	public int getBudget();
	public void setOriginScreen(ScreenType originScreen);
	public ScreenType getOriginScreen();
	public boolean areTeamsLockedForEditing();
}
