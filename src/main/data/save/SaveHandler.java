package main.data.save;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import main.data.entities.Player;
import main.data.entities.Stats;
import main.data.entities.Team;

public class SaveHandler
{
	private String saveExtension;
	private String savePath;
	private String entityPath;
	private String entityName;

	public SaveHandler(String entityName, String saveExtension)
	{
		this(System.getProperty("user.dir") + "\\save\\", entityName, saveExtension);
	}

	public SaveHandler(String savePath, String entityName, String saveExtension)
	{
		this.saveExtension = saveExtension.toLowerCase();
		this.savePath = savePath;
		this.entityPath = savePath + entityName + "\\";
		this.entityName = entityName;
	}

	public void createCacheDir()
	{
		createDirectory("cache");
	}

	public void createSaveDir()
	{
		createDirectory(entityName);
	}

	public void deleteCacheDir()
	{
		deleteDirectory("cache");
	}

	public void deleteSaveDir()
	{
		deleteDirectory(entityName);
	}

	private void createDirectory(String directory)
	{
		File saveFolder = new File(savePath + directory);
		boolean success = saveFolder.mkdirs();

		if (!success)
			System.out.println("Could not create data directory for " + directory + "!");
	}

	private void deleteDirectory(String directory)
	{
		File saveFolder = new File(savePath + directory);
		for (File file : saveFolder.listFiles())
		{
			file.delete();
		}

		saveFolder.delete();
	}

	public void zipSaveDir() throws IOException
	{
		byte[] buffer = new byte[1024];

		FileOutputStream fos = new FileOutputStream(savePath + entityName + "." + saveExtension);
		ZipOutputStream zos = new ZipOutputStream(fos);

		File folder = new File(entityPath);

		for (File file : folder.listFiles())
		{
			String fileName = file.getAbsoluteFile().toString();
			String zipEntryString = fileName.substring(savePath.length(), fileName.length());

			ZipEntry ze = new ZipEntry(zipEntryString);

			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(savePath + File.separator + zipEntryString);

			int len;
			while ((len = in.read(buffer)) > 0)
			{
				zos.write(buffer, 0, len);
			}

			in.close();
		}

		zos.closeEntry();
		// remember close it
		zos.close();
	}

	public void unzipSaveDir() throws IOException
	{
		byte[] buffer = new byte[1024];

		// create output directory is not exists
		File folder = new File(savePath);
		if (!folder.exists())
		{
			folder.mkdir();
		}

		// get the zip file content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(savePath + entityName + "." + saveExtension));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null)
		{

			String fileName = ze.getName();
			File newFile = new File(savePath + File.separator + fileName);

			// create all non exists folders
			// else you will hit FileNotFoundException for compressed folder
			new File(newFile.getParent()).mkdirs();

			FileOutputStream fos = new FileOutputStream(newFile);

			int len;
			while ((len = zis.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}

			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}

	public List<String> loadTeam()
	{
		return loadFile(entityPath + "team.dat");
	}

	public List<String> loadPlayer()
	{
		return loadFile(entityPath + "player.dat");
	}

	public List<String> loadStats()
	{
		return loadFile(entityPath + "stats.dat");
	}

	public boolean saveTeam(Team team)
	{
		if (EntityMap.getTeam(team.getUniqueId()) == null)
			EntityMap.put(team.getUniqueId(), team);
		return saveLine(entityPath + "team.dat", team.saveAsText());
	}

	public boolean savePlayer(Player player)
	{
		if (EntityMap.getPlayer(player.getUniqueId()) == null)
			EntityMap.put(player.getUniqueId(), player);
		return saveLine(entityPath + "player.dat", player.saveAsText());
	}

	public boolean saveStats(Stats stats)
	{
		if (EntityMap.getStats(stats.getUniqueId()) == null)
			EntityMap.put(stats.getUniqueId(), stats);
		return saveLine(entityPath + "stats.dat", stats.saveAsText());
	}

	private boolean saveLine(String path, String line)
	{
		PrintWriter out;

		try
		{
			out = new PrintWriter(new FileWriter(path, true));
			out.println(line);
			out.close();
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

	private List<String> loadFile(String path)
	{
		List<String> returnLines = new ArrayList<String>();
		BufferedReader in;
		Scanner s;

		try
		{
			in = new BufferedReader(new FileReader(path));
			s = new Scanner(in);

			while (s.hasNextLine())
			{
				returnLines.add(s.nextLine());
			}

			in.close();
		} catch (IOException e)
		{
			System.out.println("SaveHandler - Could not read file " + path);
			return new ArrayList<String>();
		}

		return returnLines;
	}
}
