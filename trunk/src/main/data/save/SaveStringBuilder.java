package main.data.save;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.data.entities.EntityType;


public class SaveStringBuilder
{
	private Map<SaveTokenTag, SaveToken> contentsMap;
	private List<SaveTokenTag> addedTokens;
	private EntityType entityType;
	
	//creates an empty builder for the specified type
	public SaveStringBuilder(EntityType entityType)
	{
		contentsMap = new HashMap<SaveTokenTag, SaveToken>();
		addedTokens = new ArrayList<SaveTokenTag>();
		this.entityType = entityType;
	}
	
	//used for loading; takes in the entire unparsed string and the entity type
	public SaveStringBuilder(EntityType entityType, String saveString) throws ParseException
	{
		this(entityType);
		
		int tagLength = entityType.toString().length() + 2;
		int end = saveString.length();
		
		String openingTag = "<" + entityType.toString() + ">";
		String closingTag = "</" + entityType.toString() + ">";
		
		if (saveString.substring(0, tagLength).equals(openingTag) && saveString.substring(end - (tagLength + 1)).equals(closingTag))
		{
			saveString = saveString.substring(tagLength, end - (tagLength + 1));
		}
		else
		{
			throw new ParseException("Invalid format in loading string - Invalid or missing entity tags.", 0);
		}
		
		Scanner s = new Scanner(saveString).useDelimiter(";");
		
		while (s.hasNext())
		{
			SaveToken saveToken = new SaveToken(s.next());
			addToken(saveToken);
		}
	}
	
	//used for finding the type of a saved entity
	public static EntityType getEntityType(String saveString) throws ParseException
	{
		int tagStart = saveString.indexOf('<') + 1;
		int tagLength = saveString.indexOf('>') - tagStart + 1;
		String entityName = saveString.substring(tagStart, tagLength);
		
		EntityType toRet = null;
		
		try
		{
			toRet = EntityType.valueOf(entityName);
		} catch (IllegalArgumentException e)
		{
			throw new ParseException("Invalid format in loading string - Entity " + entityName + " does not exist.", 0);
		}
		
		return toRet;
	}
	
	public void addToken(SaveToken saveToken)
	{
		if (saveToken == null)
			return;
		
		if(saveToken.getTag() == null)
			return;
		
		contentsMap.put(saveToken.getTag(), saveToken);
		addedTokens.add(saveToken.getTag());
	}
	
	public SaveToken getToken(SaveTokenTag saveTokenTag)
	{
		return contentsMap.get(saveTokenTag);
	}
	
	//this will be outputted to the save file
	public String getSaveString()
	{
		String toRet = "<" + entityType.name() + ">";
		
		for (SaveTokenTag saveTokenTag : addedTokens)
		{
			SaveToken saveToken = getToken(saveTokenTag);
			
			toRet = toRet + saveToken.toString() + ";";
		}
		
		return toRet.substring(0, toRet.length() - 1) + "</" + entityType.name() + ">";
	}
}
