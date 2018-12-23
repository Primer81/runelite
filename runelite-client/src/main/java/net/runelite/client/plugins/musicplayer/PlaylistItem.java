package net.runelite.client.plugins.musicplayer;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

class PlaylistItem extends MusicPlayerItem
{
	private Playlist playlist;
	private MusicPlayerPanel parent;
	boolean isActionPause;

	private final Border selectedBorder = new MatteBorder(1, 5, 1, 1, ColorScheme.BRAND_ORANGE);

	private ImageIcon iconConfig;
	private ImageIcon iconConfigHover;
	private ImageIcon iconDone;
	private ImageIcon iconDoneHover;

	PlaylistItem(MusicPlayerPanel parent, Playlist playlist)
	{
		super(null, playlist.title);
		this.reset();
		this.playlist = playlist;
		this.parent = parent;

		BufferedImage imageConfig = ImageUtil.getResourceStreamFromClass(PlaylistItem.class, "config.png");
		iconConfig = new ImageIcon(imageConfig);
		iconConfigHover = new ImageIcon(ImageUtil.grayscaleImage(imageConfig));

		BufferedImage imageDone = ImageUtil.getResourceStreamFromClass(PlaylistItem.class, "done.png");
		iconDone = new ImageIcon(imageDone);
		iconDoneHover = new ImageIcon(ImageUtil.grayscaleImage(imageDone));

		this.button.setIcon(iconConfig);
		this.button.setToolTipText("Edit Playlist");

		this.button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (!parent.tableSongs.isInEditMode())
				{
					enterEditMode();
				}
				else
				{
					exitEditMode();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (parent.tableSongs.isInEditMode())
				{
					button.setIcon(iconDoneHover);
				}
				else
				{
					button.setIcon(iconConfigHover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (parent.tableSongs.isInEditMode())
				{
					button.setIcon(iconDone);
				}
				else
				{
					button.setIcon(iconConfig);
				}
			}
		});

		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (!isEnabled())
				{
					return;
				}

				label.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (!isEnabled())
				{
					return;
				}

				PlaylistItem pItem = (PlaylistItem) e.getComponent();
				if (parent.selectedPlaylist == null || !pItem.playlist.title.equals(parent.selectedPlaylist.title))
				{
					label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				selectPlaylist();
			}
		});
	}

	private void reset()
	{
		this.label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		isActionPause = false;
		this.setBorder(defaultBorder);
		this.button.setEnabled(false);
	}

	public void selectPlaylist()
	{
		if (!isEnabled())
		{
			return;
		}

		if (parent.selectedPlaylist == null || !this.playlist.title.equals(parent.selectedPlaylist.title))
		{
			// Reset old playlist item
			if (parent.selectedPlaylistItem != null)
			{
				parent.selectedPlaylistItem.reset();
			}
			// Update parent to recognize this item as selected
			parent.selectedPlaylist = this.playlist;
			parent.selectedPlaylistItem = this;

			Vector<String> columnNames = new Vector<>();
			columnNames.add("Song Name");
			Vector<Vector<String>> data = new Vector<>();

			MusicPlayerPanel.musicRowIndex = new HashMap<>();
			MusicPlayerPanel.rowMusicIndex = new HashMap<>();
			int rowCount = 0;
			for (String key : MusicPlayerPanel.musicIndicesInAlphaOrder)
			{
				if (playlist.contains(key))
				{
					Vector<String> row = new Vector<>();
					row.add(MusicPlayerPanel.musicNameIndex.get(key));
					data.add(row);
					MusicPlayerPanel.musicRowIndex.put(key, rowCount);
					MusicPlayerPanel.rowMusicIndex.put(rowCount, key);
					rowCount++;
				}
			}

			parent.tableSongs.setModel(new CustomTableModel(data, columnNames));
			// Update appearance
			this.setBorder(selectedBorder);
			this.label.setForeground(Color.WHITE);
			parent.buttonPausePlay.setIcon(parent.iconPlay);
			// Enable edit button
			button.setEnabled(true);
			// Pause playback and skip the rest of the currently loaded song
			parent.musicPlayer.pause();
			parent.musicPlayer.skip();
			// If no song is already loaded, load the next song, else load active song
			if (playlist.hasCurrentSongId())
			{
				parent.reloadSong();
			}
			else
			{
				parent.loadNextSong();
			}
		}
	}

	void enterEditMode()
	{
		button.setIcon(iconDoneHover);
		button.setToolTipText("Confirm Changes");

		for (PlaylistItem pItem : parent.playlistItems)
		{
			if (!pItem.playlist.title.equals(playlist.title))
				pItem.setEnabled(false);
		}

		parent.itemAddPlaylist.button.setEnabled(false);
		parent.itemDeletePlaylist.button.setEnabled(false);
		parent.itemAddPlaylist.setEnabled(false);
		parent.itemDeletePlaylist.setEnabled(false);

		// Update song listing
		Vector<String> columnNames = new Vector<>();
		columnNames.add("Song Name");
		columnNames.add("In Playlist");
		Vector<Vector<Object>> data = new Vector<>();

		MusicPlayerPanel.musicRowIndex = new HashMap<>();
		MusicPlayerPanel.rowMusicIndex = new HashMap<>();
		int rowCount = 0;
		for (String key : MusicPlayerPanel.musicIndicesInAlphaOrder)
		{
			if (playlist.contains(key))
			{
				Vector<Object> row = new Vector<>();
				row.add(MusicPlayerPanel.musicNameIndex.get(key));
				row.add(true);
				data.add(row);
				MusicPlayerPanel.musicRowIndex.put(key, rowCount);
				MusicPlayerPanel.rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}
		for (String key : MusicPlayerPanel.musicIndicesInAlphaOrder)
		{
			if (!playlist.contains(key))
			{
				Vector<Object> row = new Vector<>();
				row.add(MusicPlayerPanel.musicNameIndex.get(key));
				row.add(false);
				data.add(row);
				MusicPlayerPanel.musicRowIndex.put(key, rowCount);
				MusicPlayerPanel.rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}

		parent.tableSongs.setModel(new CustomTableModel(data, columnNames));
		parent.tableSongs.getColumnModel().getColumn(1).setMaxWidth(20);
		parent.tableSongs.setInEditMode(true);
	}

	private void exitEditMode()
	{
		// Update playlist with changes
		Set<String> songIds = new HashSet<>();
		for (int i = 0; i < parent.tableSongs.getRowCount(); i++)
		{
			if ((Boolean) parent.tableSongs.getValueAt(i, 1))
			{
				songIds.add(MusicPlayerPanel.rowMusicIndex.get(i));
			}
		}
		if (!songIds.isEmpty())
		{
			this.playlist.updatePlaylist(songIds);
		}
		else
		{
			JOptionPane.showMessageDialog(parent, "A playlist must have at least one song.", "Info", JOptionPane.PLAIN_MESSAGE);
			return;
		}

		// Revert to normal mode
		button.setIcon(iconConfigHover);
		button.setToolTipText("Edit Playlist");

		for (PlaylistItem pItem : parent.playlistItems)
		{
			if (!pItem.playlist.title.equals(playlist.title))
				pItem.setEnabled(true);
		}

		parent.itemAddPlaylist.button.setEnabled(true);
		parent.itemDeletePlaylist.button.setEnabled(true);
		parent.itemAddPlaylist.setEnabled(true);
		parent.itemDeletePlaylist.setEnabled(true);

		Vector<String> columnNames = new Vector<>();
		columnNames.add("Song Name");
		Vector<Vector<String>> data = new Vector<>();

		MusicPlayerPanel.musicRowIndex = new HashMap<>();
		MusicPlayerPanel.rowMusicIndex = new HashMap<>();
		int rowCount = 0;
		for (String key : MusicPlayerPanel.musicIndicesInAlphaOrder)
		{
			if (playlist.contains(key))
			{
				Vector<String> row = new Vector<>();
				row.add(MusicPlayerPanel.musicNameIndex.get(key));
				data.add(row);
				MusicPlayerPanel.musicRowIndex.put(key, rowCount);
				MusicPlayerPanel.rowMusicIndex.put(rowCount, key);
				rowCount++;
			}
		}

		parent.tableSongs.setModel(new CustomTableModel(data, columnNames));
		parent.tableSongs.setInEditMode(false);
	}
}
