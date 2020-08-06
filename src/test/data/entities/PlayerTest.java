package test.data.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import main.data.entities.Player;
import main.data.entities.Race;
import main.data.entities.Skill;
import main.data.factory.PlayerFactory;

public class PlayerTest {

	private Player blankPlayer;
	private Player humanPlayer;
	
	@Before
	public void setUp()
	{
		blankPlayer = PlayerFactory.createEmptyPlayer();
		humanPlayer = PlayerFactory.createPlayerWithDefinedName(Race.HUMAN, "HUMAN");
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
		Player blankPlayer2 = PlayerFactory.createEmptyPlayer();
		
		assertEquals(blankPlayer.hashCode(), blankPlayer2.hashCode());
		assertTrue(blankPlayer.equals(blankPlayer2));
		assertTrue(blankPlayer2.equals(blankPlayer));
	}
	
	@Test
	public void newPlayerEqualsSelf()
	{
		Player player = PlayerFactory.createPlayerWithRandomName(Race.GRONK);
		
		assertTrue(player.equals(player));
	}
	
	@Test
	public void clonedPlayerEqualsSelf()
	{
		Player player = PlayerFactory.createPlayerWithRandomName(Race.SLITH);
		Player player2 = player.clone();
		
		assertEquals(player.hashCode(), player2.hashCode());
		assertTrue(player.equals(player2));
		assertTrue(player2.equals(player));
	}
	
	@Test
	public void getSkillListStoicThenSlySkills()
	{
		humanPlayer.gainSkill(Skill.STOIC);
		humanPlayer.gainSkill(Skill.SLY);
		
		List<Skill> skills = humanPlayer.getSkills();
		assertEquals(3, skills.size());
		assertEquals(Skill.UNCOMMON_VALOR, skills.get(0));
		assertEquals(Skill.STOIC, skills.get(1));
		assertEquals(Skill.SLY, skills.get(2));
	}
	
	@Test
	public void getSkillListSlyThenStoicSkills()
	{
		humanPlayer.gainSkill(Skill.SLY);
		humanPlayer.gainSkill(Skill.STOIC);
		
		List<Skill> skills = humanPlayer.getSkills();
		assertEquals(3, skills.size());
		assertEquals(Skill.UNCOMMON_VALOR, skills.get(0));
		assertEquals(Skill.SLY, skills.get(1));
		assertEquals(Skill.STOIC, skills.get(2));
	}
	
	@Test
	public void gainSkillAddingSameSkill()
	{
		humanPlayer.gainSkill(Skill.GUARD);
		humanPlayer.gainSkill(Skill.GUARD);
		
		assertTrue(humanPlayer.hasSkill(Skill.GUARD));
		
		List<Skill> skills = humanPlayer.getSkills();
		assertEquals(2, skills.size());
		assertEquals(Skill.UNCOMMON_VALOR, skills.get(0));
		assertEquals(Skill.GUARD, skills.get(1));
	}
}
