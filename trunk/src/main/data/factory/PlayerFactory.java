package main.data.factory;

import java.util.ArrayList;
import java.util.List;

import main.data.entities.Player;
import main.logic.Engine;

public class PlayerFactory
{
	private static boolean namesDefined = false;

	private static List<String> NAMES_CURMIAN = null;
	private static List<String> NAMES_DRAGORAN = null;
	private static List<String> NAMES_GRONK = null;
	private static List<String> NAMES_HUMAN = null;
	private static List<String> NAMES_KURGAN = null;
	private static List<String> NAMES_NYNAX = null;
	private static List<String> NAMES_SLITH = null;
	private static List<String> NAMES_XJS9000 = null;
	
	private static List<String> assignableCurmianNames = new ArrayList<String>();
	private static List<String> assignableDragoranNames = new ArrayList<String>();
	private static List<String> assignableGronkNames = new ArrayList<String>();
	private static List<String> assignableHumanNames = new ArrayList<String>();
	private static List<String> assignableKurganNames = new ArrayList<String>();
	private static List<String> assignableNynaxNames = new ArrayList<String>();
	private static List<String> assignableSlithNames = new ArrayList<String>();
	private static List<String> assignableXjs9000Names = new ArrayList<String>();
	
	private PlayerFactory(){}
	
	public static Player createEmptyPlayer()
	{
		return new Player(-1, "EMPTY");
	}
	
	public static Player createPlayerWithRandomName(int race)
	{
		return new Player(race, randomName(race));
	}
	
	public static Player createPlayerWithDefinedName(int race, String name)
	{
		return new Player(race, name);
	}
	
	// ensures that the same names don't get picked twice
	private static String randomName(int race)
	{
		if (!namesDefined)
			defineNames();

		String toRet = "NO NAME";

		if (race == Player.RACE_CURMIAN)
		{
			toRet = safeGetRandomName(NAMES_CURMIAN, assignableCurmianNames);
		} else if (race == Player.RACE_DRAGORAN)
		{
			toRet = safeGetRandomName(NAMES_DRAGORAN, assignableDragoranNames);
		} else if (race == Player.RACE_GRONK)
		{
			toRet = safeGetRandomName(NAMES_GRONK, assignableGronkNames);
		} else if (race == Player.RACE_HUMAN)
		{
			toRet = safeGetRandomName(NAMES_HUMAN, assignableHumanNames);
		} else if (race == Player.RACE_KURGAN)
		{
			toRet = safeGetRandomName(NAMES_KURGAN, assignableKurganNames);
		} else if (race == Player.RACE_NYNAX)
		{
			toRet = safeGetRandomName(NAMES_NYNAX, assignableNynaxNames);
		} else if (race == Player.RACE_SLITH)
		{
			toRet = safeGetRandomName(NAMES_SLITH, assignableSlithNames);
		} else if (race == Player.RACE_XJS9000)
		{
			toRet = safeGetRandomName(NAMES_XJS9000, assignableXjs9000Names);
		}

		return toRet;
	}
	
	private static String safeGetRandomName(List<String> all, List<String> available)
	{
		if (available.isEmpty())
		{
			available = randomNameFill(all);
		}
		
		int nameIndex = Engine.Randint(0, available.size() - 1);
		return available.remove(nameIndex);
	}

	private static List<String> randomNameFill(List<String> source)
	{
		List<String> shuffledList = new ArrayList<String>();
		List<String> unshuffledList = deepCopyList(source);
		
		while (!unshuffledList.isEmpty())
		{
			int index = Engine.Randint(0, unshuffledList.size() - 1);
			shuffledList.add(unshuffledList.remove(index));
		}
		
		return shuffledList;
	}
	
	private static List<String> deepCopyList(List<String> toCopy)
	{
		List<String> copy = new ArrayList<String>();
		
		for (String s : toCopy)
		{
			copy.add(new String(s));
		}
		
		return copy;
	}

	// TODO: define all the names, not just a sampling
	private static void defineNames()
	{
		namesDefined = true;

		// Define Curmian names
		NAMES_CURMIAN = new ArrayList<String>();
		NAMES_CURMIAN.add("Regee");
		NAMES_CURMIAN.add("Risbe");
		NAMES_CURMIAN.add("Roggo");
		NAMES_CURMIAN.add("Crannok");
		NAMES_CURMIAN.add("Croscka");
		NAMES_CURMIAN.add("Crigget");
		NAMES_CURMIAN.add("Refret");
		NAMES_CURMIAN.add("Rinick");
		NAMES_CURMIAN.add("Robnit");
		NAMES_CURMIAN.add("Crabnic");
		NAMES_CURMIAN.add("Cronop");
		NAMES_CURMIAN.add("Cridmic");
		NAMES_CURMIAN.add("Rennoc");
		NAMES_CURMIAN.add("Risboc");

		// Define Dragoran names
		NAMES_DRAGORAN = new ArrayList<String>();
		NAMES_DRAGORAN.add("Aytricus");
		NAMES_DRAGORAN.add("Flavicus");
		NAMES_DRAGORAN.add("Bruticus");
		NAMES_DRAGORAN.add("Octaicus");
		NAMES_DRAGORAN.add("Vaticus");
		NAMES_DRAGORAN.add("Creticus");
		NAMES_DRAGORAN.add("Lynticus");
		NAMES_DRAGORAN.add("Radicus");
		NAMES_DRAGORAN.add("Deticus");
		NAMES_DRAGORAN.add("Extricus");

		// Define Gronk names
		NAMES_GRONK = new ArrayList<String>();
		NAMES_GRONK.add("Darg");
		NAMES_GRONK.add("Sigg");
		NAMES_GRONK.add("Nirk");
		NAMES_GRONK.add("Lonk");
		NAMES_GRONK.add("Jork");
		NAMES_GRONK.add("Mook");
		NAMES_GRONK.add("Koog");
		NAMES_GRONK.add("Zuck");
		NAMES_GRONK.add("Purk");
		NAMES_GRONK.add("Jurg");

		// Define Human names
		NAMES_HUMAN = new ArrayList<String>();
		NAMES_HUMAN.add("Joe Cool");
		NAMES_HUMAN.add("Ben G.");
		NAMES_HUMAN.add("David A.");
		NAMES_HUMAN.add("Barry G.");
		NAMES_HUMAN.add("Milton");
		NAMES_HUMAN.add("Rocky");
		NAMES_HUMAN.add("Dr.Death");
		NAMES_HUMAN.add("Freddy");
		NAMES_HUMAN.add("Billy");
		NAMES_HUMAN.add("Bob");

		// Define Kurgan names
		NAMES_KURGAN = new ArrayList<String>();
		NAMES_KURGAN.add("Grimfang");
		NAMES_KURGAN.add("Lockjaw");
		NAMES_KURGAN.add("Razor");
		NAMES_KURGAN.add("Slice");
		NAMES_KURGAN.add("Cutter");
		NAMES_KURGAN.add("Chomps");
		NAMES_KURGAN.add("Deadsoul");
		NAMES_KURGAN.add("Doomchop");
		NAMES_KURGAN.add("Coolhate");
		NAMES_KURGAN.add("Psycho");

		// Define Nynax names
		NAMES_NYNAX = new ArrayList<String>();
		NAMES_NYNAX.add("Nenain");
		NAMES_NYNAX.add("Nythu");
		NAMES_NYNAX.add("Nxnon");
		NAMES_NYNAX.add("Thinu");
		NAMES_NYNAX.add("Thunon");
		NAMES_NYNAX.add("Thwix");
		NAMES_NYNAX.add("Phydox");
		NAMES_NYNAX.add("Nenu");
		NAMES_NYNAX.add("Nythain");
		NAMES_NYNAX.add("Nxcon");

		// Define Slith names
		NAMES_SLITH = new ArrayList<String>();
		NAMES_SLITH.add("Ssinot");
		NAMES_SLITH.add("Sscrit");
		NAMES_SLITH.add("Ssdesid");
		NAMES_SLITH.add("Ssloit");
		NAMES_SLITH.add("Ssthirt");
		NAMES_SLITH.add("Ssadwi");
		NAMES_SLITH.add("Ssviyt");
		NAMES_SLITH.add("SSqurit");
		NAMES_SLITH.add("Ssopit");
		NAMES_SLITH.add("Ssjisut");

		// Define XJS9000 names
		NAMES_XJS9000 = new ArrayList<String>();
		NAMES_XJS9000.add("XJS9011");
		NAMES_XJS9000.add("XJS9765");
		NAMES_XJS9000.add("XJS9567");
		NAMES_XJS9000.add("XJS9113");
		NAMES_XJS9000.add("XJS9234");
		NAMES_XJS9000.add("XJS9456");
		NAMES_XJS9000.add("XJS9809");
		NAMES_XJS9000.add("XJS9657");
		NAMES_XJS9000.add("XJS9690");
		NAMES_XJS9000.add("XJS9645");
	}
}
