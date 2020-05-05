package test.data.save;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.data.save.ByteFileWriter;

public class ByteFileWriterTest
{
	private static final String FILE_NAME = "dosTest.txt";
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	@Before
	public void setup() throws FileNotFoundException
	{
		dos = new DataOutputStream(new FileOutputStream(FILE_NAME));
	}
	
	@Test
	public void writeShortBytes_largeValue_valueSplitBetweenTwoBytes() throws IOException
	{
		ByteFileWriter.writeShortBytes(dos, 300);
		dos.close();
		
		dis = new DataInputStream(new FileInputStream(FILE_NAME));
		
		int byte1 = dis.readByte() & 0xFF;
		int byte2 = dis.readByte() & 0xFF;
		
		assertEquals(44, byte1);
		assertEquals(1, byte2);
	}
	
	@Test
	public void writeShortBytes_smallValue_valueInFirstByteWithZeroSecondByte() throws IOException
	{
		ByteFileWriter.writeShortBytes(dos, 32);
		switchToInput();
		
		int byte1 = dis.readByte() & 0xFF;
		int byte2 = dis.readByte() & 0xFF;
		
		assertEquals(32, byte1);
		assertEquals(0, byte2);
	}
	
	@Test
	public void padByte_threeBytesPadded_threeZeroesWritten() throws IOException
	{
		ByteFileWriter.padBytes(dos, 3);
		switchToInput();
		
		int byte1 = dis.readByte() & 0xFF;
		int byte2 = dis.readByte() & 0xFF;
		int byte3 = dis.readByte() & 0xFF;

		assertEquals(0, byte1);
		assertEquals(0, byte2);
		assertEquals(0, byte3);
	}
	
	@Test
	public void writeChar_validChar_charInOneByte() throws IOException
	{
		char character = 'A';
		
		ByteFileWriter.writeChar(dos, character);
		switchToInput();
		
		int result = dis.readByte() & 0xFF;
		
		assertEquals('A', (char)result);
	}
	
	@Test
	public void writeString_fiveCharString_stringCharsInFiveByte() throws IOException
	{
		String testString = "JUnit";
		
		ByteFileWriter.writeString(dos, testString);
		switchToInput();
		
		for (int i = 0; i < testString.length(); i++)
		{
			int result = dis.readByte() & 0xFF;
			assertEquals(testString.charAt(i), (char)result);
		}
	}
	
	@After
	public void teardown() throws IOException
	{
		dos.close();
		dis.close();
		
		File file = new File(FILE_NAME);
		file.delete();
	}
	
	private void switchToInput() throws IOException
	{
		dos.close();
		dis = new DataInputStream(new FileInputStream(FILE_NAME));
	}
}
