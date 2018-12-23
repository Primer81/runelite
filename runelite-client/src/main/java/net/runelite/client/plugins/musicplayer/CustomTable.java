package net.runelite.client.plugins.musicplayer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

class CustomTable extends JTable
{
	private int hoveredRow;
	private int hoveredColumn;
	private boolean inEditMode;

	CustomTable(TableModel model)
	{
		super(model);
		inEditMode = false;
		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				JTable aTable =  (JTable)e.getSource();
				hoveredRow = aTable.rowAtPoint(e.getPoint());
				hoveredColumn = aTable.columnAtPoint(e.getPoint());
				aTable.repaint();
			}
		});

		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				JTable aTable =  (JTable)e.getSource();
				hoveredRow = -1;
				hoveredColumn = -1;
				aTable.repaint();
			}
		});
	}

	int getHoveredRow()
	{
		return hoveredRow;
	}

	int getHoveredColumn()
	{
		return hoveredColumn;
	}

	boolean isInEditMode()
	{
		return inEditMode;
	}

	void setInEditMode(boolean inEditMode)
	{
		this.inEditMode = inEditMode;
		this.repaint();
	}
}
