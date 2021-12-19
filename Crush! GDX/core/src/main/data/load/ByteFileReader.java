package main.data.load;

import java.io.DataInputStream;
import java.io.IOException;

public class ByteFileReader
{
	public static void scanBytes(DataInputStream dis, int bytesToScan) throws IOException
	{
		for (int i = 0; i < bytesToScan; i++)
			dis.readByte();
	}

	public static int readNumByte(DataInputStream dis) throws IOException
	{
		return ((Byte) dis.readByte()).intValue();
	}

	public static int readUnsignedByte(DataInputStream dis) throws IOException
	{
		return dis.readByte() & 0xFF;
	}

	public static char readCharByte(DataInputStream dis) throws IOException
	{
		return (char) readUnsignedByte(dis);
	}

	// Any time a data file has a value taking up two bytes, both bytes are unsigned, and the total value is the first byte plus 256 times the second byte.
	// There's probably something really obvious with regard to binary math that I'm missing, but this works for now.
	public static short readShortBytes(DataInputStream dis) throws IOException
	{
		int addend1 = readUnsignedByte(dis);
		int addend2 = 256 * readUnsignedByte(dis);

		return (short) (addend1 + addend2);
	}
}
