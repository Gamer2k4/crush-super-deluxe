package main.data.save;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteFileWriter
{
	private static final int BYTE_SIZE = 256;
	
	public static DataOutputStream writeShortBytes(DataOutputStream dos, int value) throws IOException
	{
		int firstByte = value;
		int secondByte = 0;
		
		if (value > BYTE_SIZE)
		{
			secondByte = value / BYTE_SIZE;
			firstByte = (value - (secondByte * BYTE_SIZE));
		}
		
		dos.writeByte(firstByte);
		dos.writeByte(secondByte);
		
		return dos;
	}
	
	public static DataOutputStream writeByte(DataOutputStream dos, int value) throws IOException
	{
		dos.writeByte((value % BYTE_SIZE) & 0xFF);
		return dos;
	}
	
	public static DataOutputStream writeBoolean(DataOutputStream dos, boolean value) throws IOException
	{
		if (value)
			return writeByte(dos, 1);
		
		return writeByte(dos, 0);
	}
	
	public static DataOutputStream writeChar(DataOutputStream dos, char value) throws IOException
	{
		return writeByte(dos, (int)value);
	}
	
	public static DataOutputStream writeString(DataOutputStream dos, String value) throws IOException
	{
		DataOutputStream returnStream = dos;
		
		for (int i = 0; i < value.length(); i++)
		{
			returnStream = writeChar(dos, value.charAt(i));
		}
		
		return returnStream;
	}
	
	public static DataOutputStream padBytes(DataOutputStream dos, int amount) throws IOException
	{
		DataOutputStream returnStream = dos;
		
		for (int i = 0; i < amount; i++)
		{
			returnStream = writeByte(dos, 0);
		}
		
		return returnStream;
	}
}
