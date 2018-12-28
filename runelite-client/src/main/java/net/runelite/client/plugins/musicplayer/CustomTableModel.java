package net.runelite.client.plugins.musicplayer;

import lombok.Getter;

import javax.swing.table.DefaultTableModel;
import java.util.*;

class CustomTableModel extends DefaultTableModel
{
	@Getter
	private Map<String, Integer> musicRowIndex = new HashMap<>();

	@Getter
	private Map<Integer, String> rowMusicIndex = new HashMap<>();

	private List<Class<?>> columnClasses;

	private CustomTableModel(Vector<? extends Vector> data, Vector<?> columnNames)
	{
		super(data, columnNames);
		columnClasses = new ArrayList<>();
		if (!data.isEmpty())
		{
			for (Object obj : data.get(0))
			{
				if (obj instanceof String)
				{
					columnClasses.add(String.class);
				}
				else if (obj instanceof Boolean)
				{
					columnClasses.add(Boolean.class);
				}
				else
				{
					columnClasses.add(Object.class);
				}
			}
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	@Override
	public String getColumnName(int column)
	{
		return "";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return this.columnClasses.get(columnIndex);
	}

	static CustomTableModel getCustomTableModel(Playlist playlist)
	{
		return getCustomTableModel(playlist, false);
	}

	static CustomTableModel getCustomTableModel(Playlist playlist, boolean editMode)
	{
		if (editMode)
		{
			return getCustomTableModelEditable(playlist);
		}
		else
		{
			return getCustomTableModelNotEditable(playlist);
		}
	}

	private static CustomTableModel getCustomTableModelEditable(Playlist playlist)
	{
		Vector<String> columnNames = new Vector<>();
		columnNames.add("Song Name");
		columnNames.add("In Playlist");
		Vector<Vector<Object>> data = new Vector<>();
		Map<String, Integer> musicRowIndex = new HashMap<>();
		Map<Integer, String> rowMusicIndex = new HashMap<>();
		int rowCount = 0;
		for (String key : MusicPlayerPlugin.songsOrderedAlpha)
		{
			if (playlist.contains(key))
			{
				Vector<Object> row = new Vector<>();
				row.add(MusicPlayerPlugin.musicNameIndex.get(key));
				row.add(true);
				data.add(row);
				musicRowIndex.put(key, rowCount);
				rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}
		for (String key : MusicPlayerPlugin.songsOrderedAlpha)
		{
			if (!playlist.contains(key))
			{
				Vector<Object> row = new Vector<>();
				row.add(MusicPlayerPlugin.musicNameIndex.get(key));
				row.add(false);
				data.add(row);
				musicRowIndex.put(key, rowCount);
				rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}
		CustomTableModel ctm = new CustomTableModel(data, columnNames);
		ctm.musicRowIndex = musicRowIndex;
		ctm.rowMusicIndex = rowMusicIndex;
		return ctm;
	}

	private static CustomTableModel getCustomTableModelNotEditable(Playlist playlist)
	{
		Vector<String> columnNames = new Vector<>();
		columnNames.add("Song Name");
		Vector<Vector<String>> data = new Vector<>();
		Map<String, Integer> musicRowIndex = new HashMap<>();
		Map<Integer, String> rowMusicIndex = new HashMap<>();
		int rowCount = 0;
		for (String key : MusicPlayerPlugin.songsOrderedAlpha)
		{
			if (playlist.contains(key))
			{
				Vector<String> row = new Vector<>();
				row.add(MusicPlayerPlugin.musicNameIndex.get(key));
				data.add(row);
				musicRowIndex.put(key, rowCount);
				rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}
		CustomTableModel ctm = new CustomTableModel(data, columnNames);
		ctm.musicRowIndex = musicRowIndex;
		ctm.rowMusicIndex = rowMusicIndex;
		return ctm;
	}
}
