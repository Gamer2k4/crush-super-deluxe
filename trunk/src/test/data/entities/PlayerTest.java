package test.data.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import main.data.entities.Player;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	private Player blankPlayer;
	private Player humanPlayer;
	
	@Before
	public void setUp()
	{
		blankPlayer = Player.createEmptyPlayer();
		humanPlayer = new Player(Player.RACE_HUMAN);
	}
	
	@Test
	public void newBlankPlayerEqualsSelf()
	{
		assertTrue(blankPlayer.equals(blankPlayer));
	}
	
	@Test
	public void clonedBlankPlayerEqualsSelf()
	{
		Player blankPlayer2 = blankPlayer.clone();
		
		assertEquals(blankPlayer.hashCode(), blankPlayer2.hashCode());
		assertTrue(blankPlayer.equals(blankPlayer2));
		assertTrue(blankPlayer2.equals(blankPlayer));
	}
	
	@Test
	public void newBlankPlayersEqualAndHash()
	{
		Player blankPlayer2 = Player.createEmptyPlayer();
		
		assertEquals(blankPlayer.hashCode(), blankPlayer2.hashCode());
		assertTrue(blankPlayer.equals(blankPlayer2));
		assertTrue(blankPlayer2.equals(blankPlayer));
	}
	
	@Test
	public void newPlayerEqualsSelf()
	{
		Player player = new Player(Player.RACE_GRONK);
		
		assertTrue(player.equals(player));
	}
	
	@Test
	public void clonedPlayerEqualsSelf()
	{
		Player player = new Player(Player.RACE_SLITH);
		Player player2 = player.clone();
		
		assertEquals(player.hashCode(), player2.hashCode());
		assertTrue(player.equals(player2));
		assertTrue(player2.equals(player));
	}
	
	@Test
	public void getSkillListNoSkills()
	{
		assertEquals("None", humanPlayer.getSkillList());
	}
	
	@Test
	public void getSkillListStoicThenSlySkills()
	{
		humanPlayer.gainSkill(Player.SKILL_STOIC);
		humanPlayer.gainSkill(Player.SKILL_SLY);
		
		assertEquals("Stoic, Sly", humanPlayer.getSkillList());
	}
	
	@Test
	public void getSkillListSlyThenStoicSkills()
	{
		humanPlayer.gainSkill(Player.SKILL_SLY);
		humanPlayer.gainSkill(Player.SKILL_STOIC);
		
		assertEquals("Sly, Stoic", humanPlayer.getSkillList());
	}
	
	@Test
	public void gainSkillAddingSameSkill()
	{
		humanPlayer.gainSkill(Player.SKILL_GUARD);
		humanPlayer.gainSkill(Player.SKILL_GUARD);
		
		assertTrue(humanPlayer.hasSkill(Player.SKILL_GUARD));
		assertEquals("Guard", humanPlayer.getSkillList());
	}
}
