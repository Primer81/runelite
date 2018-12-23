package net.runelite.client.plugins.musicplayer;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class CustomTableModel extends DefaultTableModel
{
	private List<Class<?>> columnClasses;

	CustomTableModel(Vector<? extends Vector> data, Vector<?> columnNames)
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
}
