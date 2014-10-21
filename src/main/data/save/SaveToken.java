package main.data.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SaveToken
{
	public static final int TAG_LENGTH = 5;
	
	private SaveTokenTag tag;
	private String contents;
	
	//takes the arguments in the form of TAG, "contents"
	public SaveToken(SaveTokenTag tag, String contents)
	{
		this.tag = tag;
		this.contents = contents;
	}
	
	//takes the arguments in the form of TAG, {"1", "2", "3", "4", "5"}
	public SaveToken(SaveTokenTag tag, List<String> contents)
	{
		if (contents.isEmpty())
		{
			this.contents = "";
			this.tag = null;
			return;
		}
		
		String compiledContents = "";
		
		for (String s : contents)
		{
			compiledContents = compiledContents + s + ",";
		}
		
		//trim the last comma
		this.contents = compiledContents.substring(0, compiledContents.length() - 1);
		this.tag = tag;
	}
	
	//takes the argument in the form of "[TAG]content"
	public SaveToken(String token)
	{
		String tempTag = token.substring(1, TAG_LENGTH + 1);
		contents = token.substring(TAG_LENGTH + 2);
		
		tag = SaveTokenTag.valueOf(tempTag.toUpperCase());
	}
	
	@Override
	public String toString()
	{
		if (tag == null)
			return "";
		
		return "[" + tag.name() + "]" + contents;
	}
	
	public String getContents()
	{
		return contents;
	}
	
	public SaveTokenTag getTag()
	{
		return tag;
	}
	
	public List<String> getContentSet()
	{
		List<String> toRet = new ArrayList<String>();
		
		Scanner s = new Scanner(contents).useDelimiter(",");	//confirm this line
		
		while (s.hasNext())
		{
			toRet.add(s.next());
		}
		
		return toRet;
	}
}