package main.logic.ai.coach.player;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Equipment;
import main.data.entities.Race;
import main.logic.RandomGeneratorSingletonImpl;
import main.logic.ai.coach.skill.SkillSelectionAi;
import main.logic.ai.coach.skill.SkillSelectionAiType;

public class PlayerPersona
{
	private List<Race> racePriority = new ArrayList<Race>();
	private int[] idealEquipment = new int[4];
	private SkillSelectionAi skillAi;
	
	private PlayerPersona(List<Race> races, int[] equipment, SkillSelectionAi ai)
	{
		for (Race race : races)
			racePriority.add(race);
		
		for (int i = 0; i < 4; i++)
			idealEquipment[i] = equipment[i];
		
		skillAi = ai;
	}
	
	public List<Race> getRacePriority()
	{
		return racePriority;
	}
	
	public int[] getIdealEquipment()
	{
		return idealEquipment;
	}

	public SkillSelectionAi getSkillAi()
	{
		return skillAi;
	}
	
	private static List<Race> raceList(Race... races)
	{
		List<Race> raceList = new ArrayList<Race>();
		
		for (Race race : races)
			raceList.add(race);
		
		return raceList;
	}
	
	@Override
	public String toString()
	{
		return racePriority.toString();
	}
	
	//catch all - if there is no race, the best player available should be chosen (this logic will probably be in the coach class)
	public static PlayerPersona emptyPersona()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona randomPersona()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		List<Race> race = new ArrayList<Race>();
		int randomRaceIndex = RandomGeneratorSingletonImpl.getInstance().getRandomInt(0, Race.values().length - 1);
		race.add(raceList(Race.values()).get(randomRaceIndex));
		
		return new PlayerPersona(
				raceList(),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	//generic race slots
	public static PlayerPersona curmian()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.CURMIAN, Race.NYNAX, Race.HUMAN, Race.DRAGORAN, Race.SLITH, Race.KURGAN, Race.XJS9000, Race.GRONK),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona dragoran()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.DRAGORAN, Race.NYNAX, Race.HUMAN, Race.CURMIAN, Race.KURGAN, Race.SLITH, Race.GRONK, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona gronk()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.GRONK, Race.KURGAN, Race.HUMAN, Race.SLITH, Race.DRAGORAN, Race.NYNAX, Race.CURMIAN, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona human()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.HUMAN, Race.DRAGORAN, Race.KURGAN, Race.SLITH, Race.GRONK, Race.NYNAX, Race.CURMIAN, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona kurgan()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.KURGAN, Race.GRONK, Race.SLITH, Race.HUMAN, Race.DRAGORAN, Race.NYNAX, Race.CURMIAN, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona nynax()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX, Race.CURMIAN, Race.DRAGORAN, Race.HUMAN, Race.KURGAN, Race.SLITH, Race.GRONK, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona slith()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.SLITH, Race.KURGAN, Race.HUMAN, Race.GRONK, Race.DRAGORAN, Race.NYNAX, Race.CURMIAN, Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjs9000()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN, Race.DRAGORAN, Race.SLITH, Race.KURGAN, Race.GRONK),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	
	//specific loadouts
	public static PlayerPersona captain()
	{
		int[] equipment = {
				Equipment.EQUIP_REINFORCED_ARMOR,
				Equipment.EQUIP_MAGNETIC_GLOVES,
				Equipment.EQUIP_FIELD_INTEGRITY_BELT,
				Equipment.EQUIP_INSULATED_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.DRAGORAN, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona leadBlocker()
	{
		int[] equipment = {
				Equipment.EQUIP_SURGE_ARMOR,
				Equipment.EQUIP_REPULSOR_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_MAGNETIC_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.GRONK, Race.KURGAN, Race.SLITH, Race.HUMAN),
				equipment,
				SkillSelectionAiType.POWER_FIRST.getAi());
	}
	
	public static PlayerPersona slayer()
	{
		int[] equipment = {
				Equipment.EQUIP_SPIKED_ARMOR,
				Equipment.EQUIP_SPIKED_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_SPIKED_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.KURGAN, Race.GRONK, Race.SLITH, Race.HUMAN),
				equipment,
				SkillSelectionAiType.POWER_FIRST.getAi());
	}
	
	public static PlayerPersona guard()
	{
		int[] equipment = {
				Equipment.EQUIP_HEAVY_ARMOR,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.SLITH, Race.GRONK, Race.KURGAN, Race.HUMAN),
				equipment,
				SkillSelectionAiType.INVINCIBLE.getAi());
	}
	
	public static PlayerPersona cleanUp()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.HUMAN, Race.DRAGORAN, Race.NYNAX, Race.CURMIAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona speedDemon()
	{
		int[] equipment = {
				Equipment.EQUIP_HEAVY_ARMOR,
				Equipment.EQUIP_MAGNETIC_GLOVES,
				Equipment.EQUIP_BOOSTER_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.CURMIAN, Race.NYNAX, Race.DRAGORAN, Race.SLITH, Race.KURGAN),
				equipment,
				SkillSelectionAiType.AGILITY_FIRST.getAi());
	}
	
	public static PlayerPersona surge()
	{
		int[] equipment = {
				Equipment.EQUIP_SURGE_ARMOR,
				Equipment.EQUIP_SURGE_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.HUMAN, Race.GRONK, Race.KURGAN, Race.SLITH, Race.DRAGORAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona nynaxCaptain()		//also have support (sensei) and other nynax profiles - a team should have maybe 5 different ones
	{
		int[] equipment = {
				Equipment.EQUIP_HEAVY_ARMOR,
				Equipment.EQUIP_MAGNETIC_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX, Race.CURMIAN, Race.DRAGORAN, Race.HUMAN),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona nynaxStealthSurgeLead()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_SURGE_GLOVES,
				Equipment.EQUIP_HOLOGRAM_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona nynaxStealthSurgeRegular()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_HOLOGRAM_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona nynaxGronkLead()
	{
		int[] equipment = {
				Equipment.EQUIP_HEAVY_ARMOR,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_INSULATED_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona nynaxGronkHeavy()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_SPIKED_GLOVES,
				Equipment.EQUIP_BOOSTER_BELT,
				Equipment.EQUIP_SPIKED_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.GRONK, Race.NYNAX),
				equipment,
				SkillSelectionAiType.POWER_FIRST.getAi());
	}
	
	public static PlayerPersona nynaxGronkRegular()
	{
		int[] equipment = {
				Equipment.EQUIP_HEAVY_ARMOR,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.NYNAX),
				equipment,
				SkillSelectionAiType.INVINCIBLE.getAi());
	}
	
	public static PlayerPersona kurganSlithLead()
	{
		int[] equipment = {
				Equipment.EQUIP_REINFORCED_ARMOR,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_MAGNETIC_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.KURGAN),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona kurganSlithRegularS()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_MAGNETIC_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.SLITH, Race.KURGAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona kurganSlithRegularK()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_MAGNETIC_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.KURGAN, Race.SLITH),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona curmianLead()
	{
		int[] equipment = {
				Equipment.EQUIP_REINFORCED_ARMOR,
				Equipment.EQUIP_SAAI_GLOVES,
				Equipment.EQUIP_BOOSTER_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.CURMIAN),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona curmianRegular()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_SAAI_GLOVES,
				Equipment.EQUIP_BOOSTER_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.CURMIAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjsCaptain()
	{
		int[] equipment = {
				Equipment.EQUIP_REPULSOR_ARMOR,
				Equipment.EQUIP_MAGNETIC_GLOVES,
				Equipment.EQUIP_MEDICAL_BELT,
				Equipment.EQUIP_SAAI_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.BALL_CARRIER.getAi());
	}
	
	public static PlayerPersona xjsKamikaze()
	{
		int[] equipment = {
				Equipment.EQUIP_VORTEX_ARMOR,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BACKFIRE_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjsCheckmaster()
	{
		int[] equipment = {
				Equipment.EQUIP_REINFORCED_ARMOR,
				Equipment.EQUIP_SAAI_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_SAAI_BOOTS
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjsLiveWire()
	{
		int[] equipment = {
				Equipment.EQUIP_SURGE_ARMOR,
				Equipment.EQUIP_SURGE_GLOVES,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjsCleanup()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_MAGNETIC_GLOVES,
				Equipment.EQUIP_FIELD_INTEGRITY_BELT,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000, Race.CURMIAN, Race.NYNAX, Race.HUMAN),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
	
	public static PlayerPersona xjsDefault()
	{
		int[] equipment = {
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK,
				Equipment.EQUIP_BLANK
		};
		
		return new PlayerPersona(
				raceList(Race.XJS9000),
				equipment,
				SkillSelectionAiType.RANDOM.getAi());
	}
}
