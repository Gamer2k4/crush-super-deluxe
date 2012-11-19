package main.data.entities;

public class Stats
{	
	public int checksThrown = 0;
	public int checksLanded = 0;
	public int sacks = 0;
	public int kills = 0;
	public int injuries = 0;
	public int rushingAttempts = 0;
	public int rushingYards = 0;
	public int padsActivated = 0;
	public boolean goalScored = false;
	public boolean enteredGame = false;
	public boolean winningTeam = false;
	
	public void updateWithResults(Stats gameResults)
	{
		checksThrown += gameResults.checksThrown;
		checksLanded += gameResults.checksLanded;
		sacks += gameResults.sacks;
		kills += gameResults.kills;
		injuries += gameResults.injuries;
		rushingAttempts += gameResults.rushingAttempts;
		rushingYards += gameResults.rushingYards;
		padsActivated += gameResults.padsActivated;
	}
	
	//call this whenever the player throws a check
	public void check(boolean success, boolean sack, boolean injury, boolean kill)
	{
		checksThrown++;
		if (success) checksLanded++;
		if (sack) sacks++;
		if (injury) injuries++;
		if (kill) kills++;
	}
	
	//call this whenever the player loses the ball
	public void sacked()
	{
		rushingAttempts++;
	}
	
	//call this each time the player moves with the ball
	public void rush(int yards)
	{
		rushingYards++;
	}
	
	//call this upon winning; exclusive with score()
	public void teamWon()
	{
		winningTeam = true;
	}
	
	//call this if the player scores; exclusive with teamWon()
	public void score()
	{
		goalScored = true;
		winningTeam = true;
		rushingAttempts++;
	}
	
	//call this upon successfully teleporting in for the first time
	public void playedGame()
	{
		enteredGame = true;
	}
	
	//call this once the game concludes
	public int getXP()
	{
		int toRet = 2 + 10 * injuries + 1 * sacks + 12 * kills + 6 * padsActivated + 2 * checksLanded + (int)(.5 * rushingYards);
		if (goalScored) toRet += 20;
		if (enteredGame) toRet += 2;
		if (winningTeam) toRet += 2;
		
		return toRet;
	}
}
