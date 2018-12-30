package net.runelite.client.plugins.musicplayer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JTable;
import lombok.Getter;

class CustomTable extends JTable
{
	@Getter
	private int hoveredRow;

	@Getter
	private int hoveredColumn;

	CustomTable()
	{
		super();
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

	int getRowFromSongId(String songId)
	{
		CustomTableModel model = (CustomTableModel) getModel();
		return model.getMusicRowIndex().get(songId);
	}

	String getSongIdFromRow(int row)
	{
		CustomTableModel model = (CustomTableModel) getModel();
		return model.getRowMusicIndex().get(row);
	}
}
