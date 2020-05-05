package main.presentation.common;

import main.data.entities.Team;

public interface TeamEditor
{
	public void setTeam(Team team);
	public Team getTeam();
	public void setLoadEnabled(boolean isEnabled);
	public void setBudget(int budget);
}
