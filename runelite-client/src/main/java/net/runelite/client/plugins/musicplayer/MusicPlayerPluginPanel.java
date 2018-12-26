package net.runelite.client.plugins.musicplayer;

import com.google.inject.Singleton;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
class MusicPlayerPluginPanel extends PluginPanel
{
	// Constants
	private final Color COLOR_TITLE = Color.WHITE;
	private final Color COLOR_SUBTITLE = Color.WHITE;

	private final String PLAY_FORMAT = "<html>%s<font color='rgb(55, 240, 70)'>%s</font></html>";
	private final String PLAY_PRE = "";

	// Package classes
	private MusicPlayerPlugin plugin;
	private MusicPlayer musicPlayer;

	private Playlist selectedPlaylist;
	private List<Playlist> playlists;

	// Components and related
	private JPanel playlistPanel;
	private List<PlaylistPanel> playlistPanels;
	private MusicPlayerPanel addPlaylistPanel;
	private MusicPlayerPanel deletePlaylistPanel;
	private CustomTable tableSongs;
	private JLabel subtitlePlaying;
	private PlaylistPanel selectedPlaylistPanel;
	private ControlButton buttonBack;
	private ControlButton buttonPausePlay;
	private ControlButton buttonNext;
	private ControlButton buttonShuffle;
	private ControlButton buttonLoop;

	// Atomic fields
	private boolean shuffle;
	private boolean loop;

	MusicPlayerPluginPanel(MusicPlayerPlugin plugin)
	{
		super();
		this.plugin = plugin;
		musicPlayer = new MusicPlayer();
		musicPlayer.sequencer.addMetaEventListener(meta ->
			{
				if (meta.getType() == 0x2F)  // End of track
				{
					loadNextSong();
				}
			}
		);
		shuffle = false;
		loop = false;
		playlists = new ArrayList<>(plugin.getSavedPlaylists());
		playlistPanels = new ArrayList<>();

		// Set layout
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(boxLayout);

		// Add all components to the panel
		initTitleSection();
		add(Box.createVerticalStrut(10));
		initPlayingSection();
		add(Box.createVerticalStrut(10));
		initControlsSection();
		add(Box.createVerticalStrut(10));
		initSongListSection();
		add(Box.createVerticalStrut(10));
		initPlaylistSection();
	}

	private void initTitleSection()
	{
		JLabel title = new JLabel("<html>Music Player</html>");
		title.setForeground(COLOR_TITLE);
		title.setBorder(new MatteBorder(0, 0, 1, 0, ColorScheme.BRAND_ORANGE));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);

		this.add(title);
		this.add(Box.createVerticalStrut(5));
	}

	private void initPlayingSection()
	{
		subtitlePlaying = new JLabel(String.format(PLAY_FORMAT, PLAY_PRE, ""));
		subtitlePlaying.setForeground(COLOR_SUBTITLE);
		subtitlePlaying.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitlePlaying);
		this.add(Box.createVerticalStrut(5));
	}

	private void initControlsSection()
	{
		// Add controls subtitle
		JLabel subtitleControls = new JLabel("Controls:");
		subtitleControls.setForeground(COLOR_SUBTITLE);
		subtitleControls.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitleControls);
		this.add(Box.createVerticalStrut(5));

		// Add controls
		JPanel panelControls = new JPanel();
		panelControls.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, MusicPlayerPanel.ITEM_HEIGHT));
		panelControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		BoxLayout layoutControls = new BoxLayout(panelControls, BoxLayout.LINE_AXIS);
		panelControls.setLayout(layoutControls);
		panelControls.setAlignmentX(Component.LEFT_ALIGNMENT);

		buttonBack = new ControlButton(ImageManager.getIcon(ImageManager.Images.BACK_IMG));
		buttonPausePlay = new ControlButton(ImageManager.getIcon(ImageManager.Images.PLAY_IMG));
		buttonNext = new ControlButton(ImageManager.getIcon(ImageManager.Images.NEXT_IMG));
		buttonShuffle = new ControlButton(ImageManager.getIcon(ImageManager.Images.SHUFFLE_IMG));
		buttonLoop = new ControlButton(ImageManager.getIcon(ImageManager.Images.LOOP_IMG));

		buttonBack.setToolTipText("Back");
		buttonPausePlay.setToolTipText("Pause/Play");
		buttonNext.setToolTipText("Skip");
		buttonShuffle.setToolTipText("Shuffle");
		buttonLoop.setToolTipText("Loop");

		buttonBack.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!selectedPlaylist.hasPrevSongId())
				{
					musicPlayer.restart();
				}
				else
				{
					loadPrevSong();
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				buttonBack.setPressed(true);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonBack.setPressed(false);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonBack.setIcon(ImageManager.getIcon(ImageManager.Images.BACK_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonBack.setIcon(ImageManager.getIcon(ImageManager.Images.BACK_IMG));
				buttonBack.setPressed(false);
			}
		});
		buttonPausePlay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (musicPlayer.sequencer.isRunning())
				{
					musicPlayer.pause();
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PLAY_IMG));
				}
				else
				{
					musicPlayer.play();
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PAUSE_IMG));
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				buttonPausePlay.setPressed(true);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonPausePlay.setPressed(false);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (musicPlayer.sequencer.isRunning())
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PAUSE_IMG, true));
				}
				else
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PLAY_IMG, true));
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (musicPlayer.sequencer.isRunning())
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PAUSE_IMG));
				}
				else
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PLAY_IMG));
				}
				buttonPausePlay.setPressed(false);
			}
		});
		buttonNext.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				loadNextSong();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				buttonNext.setPressed(true);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonNext.setPressed(false);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonNext.setIcon(ImageManager.getIcon(ImageManager.Images.NEXT_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonNext.setIcon(ImageManager.getIcon(ImageManager.Images.NEXT_IMG));
				buttonNext.setPressed(false);
			}
		});
		buttonShuffle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				shuffle = !shuffle;
				selectedPlaylist.rebuildQueue(shuffle);
				buttonShuffle.setPressed(shuffle);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonShuffle.setIcon(ImageManager.getIcon(ImageManager.Images.SHUFFLE_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonShuffle.setIcon(ImageManager.getIcon(ImageManager.Images.SHUFFLE_IMG));
			}
		});
		buttonLoop.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				loop = !loop;
				buttonLoop.setPressed(loop);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonLoop.setIcon(ImageManager.getIcon(ImageManager.Images.LOOP_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonLoop.setIcon(ImageManager.getIcon(ImageManager.Images.LOOP_IMG));
			}
		});

		panelControls.add(buttonBack);
		panelControls.add(buttonPausePlay);
		panelControls.add(buttonNext);
		panelControls.add(buttonShuffle);
		panelControls.add(buttonLoop);

		this.add(panelControls);
	}

	private void initSongListSection()
	{
		// Add song list subtitle
		JLabel subtitleSongList = new JLabel("Song List:");
		subtitleSongList.setForeground(COLOR_SUBTITLE);
		subtitleSongList.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitleSongList);
		this.add(Box.createVerticalStrut(5));

		// Create panel to display songs
		tableSongs = new CustomTable();
		tableSongs.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		tableSongs.setTableHeader(null);
		tableSongs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableSongs.setDefaultRenderer(String.class, new CustomTableStringCellRenderer());
		tableSongs.setDefaultRenderer(Boolean.class, new CustomTableBoolCellRenderer());

		tableSongs.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int rowSelected = tableSongs.rowAtPoint(e.getPoint());
				int colSelected = tableSongs.columnAtPoint(e.getPoint());

				if (colSelected == 0)
				{
					String songId = tableSongs.getSongIdFromRow(rowSelected);
					if (selectedPlaylist.hasCurrentSongId() && selectedPlaylist.getCurrentSongId().equals(songId))
					{
						selectedPlaylist.setCurrentSongId(songId);
					}
					changeSong(songId);
				}
				else
				{
					boolean value = (Boolean) tableSongs.getValueAt(rowSelected, 1);
					tableSongs.setValueAt(!value, rowSelected, 1);
				}
			}
		});

		// Create scroll pane for panelSongs
		JScrollPane scrollPane = new JScrollPane(tableSongs);
		scrollPane.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 250));
		this.add(scrollPane);
	}

	private void initPlaylistSection()
	{
		// Add playlist subtitle
		JLabel subtitlePlaylists = new JLabel("Playlists:");
		subtitlePlaylists.setForeground(COLOR_SUBTITLE);
		subtitlePlaylists.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(subtitlePlaylists);
		add(Box.createVerticalStrut(5));

		// Create playlist panel
		playlistPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(playlistPanel, BoxLayout.PAGE_AXIS);
		playlistPanel.setLayout(boxLayout);
		add(playlistPanel);

		// Add default playlists
		Playlist playlistAllSongs = new Playlist("All Songs", MusicPlayerPlugin.musicNameIndex.keySet());
		PlaylistPanel allSongsPanel = new PlaylistPanel(this, playlistAllSongs);
		allSongsPanel.remove(allSongsPanel.getButton());

		playlists.add(0, playlistAllSongs);

		addPlaylistPanel(allSongsPanel);

		setSelectedPlaylistPanel(allSongsPanel);

		// Load user playlists
		for (Playlist playlist : this.plugin.getSavedPlaylists())
		{
			addPlaylistPanel(new PlaylistPanel(this, playlist));
		}

		// Add playlist options
		addPlaylistPanel = new MusicPlayerPanel(ImageManager.getImage(ImageManager.Images.PLUS_IMG), "Create New Playlist");
		addPlaylistPanel.getButton().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!addPlaylistPanel.isEnabled() || !addPlaylistPanel.getButton().isEnabled())
				{
					return;
				}

				addNewPlaylist();
			}
		});
		add(addPlaylistPanel);
		add(Box.createVerticalStrut(5));
		deletePlaylistPanel = new MusicPlayerPanel(ImageManager.getImage(ImageManager.Images.MINUS_IMG), "Delete Playlist");
		deletePlaylistPanel.getButton().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!deletePlaylistPanel.isEnabled() || !deletePlaylistPanel.getButton().isEnabled())
				{
					return;
				}

				deleteSelectedPlaylist();
			}
		});
		add(deletePlaylistPanel);
		add(Box.createVerticalStrut(5));
	}

	private void addNewPlaylist()
	{
		boolean goodName = false;
		String playlistTitle = "ERROR";
		while (!goodName)
		{
			goodName = true;
			playlistTitle = JOptionPane.showInputDialog(this, "Enter a name for your new playlist:");
			if (playlistTitle == null || playlistTitle.equals(""))
			{
				return;
			}
			for (Playlist playlist : playlists)
			{
				if (playlistTitle.equals(playlist.title))
				{
					JOptionPane.showMessageDialog(this, "A playlist with that name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
					goodName = false;
				}
			}
		}

		Playlist playlist = new Playlist(playlistTitle, new HashSet<>());
		PlaylistPanel pItem = new PlaylistPanel(this, playlist);

		this.plugin.getSavedPlaylists().add(playlist);
		this.plugin.updateConfig();
		this.playlists.add(playlist);
		addPlaylistPanel(pItem);

		setSelectedPlaylistPanel(pItem);
		enterEditMode();
	}

	private void addPlaylistPanel(PlaylistPanel pPanel)
	{
		playlistPanel.add(pPanel);
		playlistPanel.add(pPanel.spacer);
		playlistPanels.add(pPanel);
		playlistPanel.revalidate();
		playlistPanel.repaint();
	}

	private void deleteSelectedPlaylist()
	{
		if (!selectedPlaylist.equals(playlists.get(0)))  // Check not equal to default playlist
		{
			String msg = String.format("Deleting the \"%s\" playlist.\nAre you sure you wish to continue?",
				selectedPlaylist.title);
			String title = String.format("Delete %s?", selectedPlaylist.title);
			int result = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION)
			{
				this.plugin.getSavedPlaylists().remove(selectedPlaylist);
				this.plugin.updateConfig();
				playlists.remove(selectedPlaylist);
				playlistPanel.remove(selectedPlaylistPanel);
				playlistPanel.remove(selectedPlaylistPanel.spacer);
				playlistPanels.remove(selectedPlaylistPanel);
				setSelectedPlaylistPanel(playlistPanels.get(0));
			}
		}
	}

	private void reloadSong()
	{
		changeSong(selectedPlaylist.getCurrentSongId());
	}

	private void loadNextSong()
	{
		if (loop)
		{
			musicPlayer.restart();
		}
		else
		{
			if (selectedPlaylist.hasNextSongId())
			{
				changeSong(selectedPlaylist.getNextSongId());
			}
			else if (!selectedPlaylist.isEmpty())
			{
				selectedPlaylist.rebuildQueue(shuffle);
				loadNextSong();
			}
		}
	}

	private void loadPrevSong()
	{
		if (selectedPlaylist.hasPrevSongId())
		{
			changeSong(selectedPlaylist.getPrevSongId());
		}
	}

	private void changeSong(String songId)
	{
		int rowInTable = tableSongs.getRowFromSongId(songId);
		SwingUtilities.invokeLater(() -> tableSongs.setRowSelectionInterval(rowInTable, rowInTable));
		subtitlePlaying.setText(String.format(PLAY_FORMAT, PLAY_PRE, MusicPlayerPlugin.musicNameIndex.get(songId)));
		this.musicPlayer.load(indexToFilePath(songId));
	}

	private String indexToFilePath(String index)
	{
		return "music/" + index + " - " + MusicPlayerPlugin.musicNameIndex.get(index) + ".mid";
	}

	void setSelectedPlaylistPanel(PlaylistPanel panel)
	{
		panel.setSelected(true);
		Playlist panelPlaylist = panel.getPlaylist();
		if (selectedPlaylist == null || !panelPlaylist.title.equals(selectedPlaylist.title))
		{
			if (selectedPlaylistPanel != null)
			{
				selectedPlaylistPanel.setSelected(false);
			}
			selectedPlaylist = panelPlaylist;
			selectedPlaylistPanel = panel;
			tableSongs.setModel(CustomTableModel.getCustomTableModel(panelPlaylist));
			buttonPausePlay.setIcon(ImageManager.getIcon(ImageManager.Images.PLAY_IMG));
			musicPlayer.pause();
			if (panelPlaylist.hasCurrentSongId())
			{
				reloadSong();
			}
			else
			{
				loadNextSong();
			}
		}
	}

	void enterEditMode()
	{
		selectedPlaylistPanel.setEditMode(true);
		for (PlaylistPanel pItem : playlistPanels)
		{
			if (!pItem.getPlaylist().title.equals(selectedPlaylist.title))
				pItem.setEnabled(false);
		}
		addPlaylistPanel.getButton().setEnabled(false);
		deletePlaylistPanel.getButton().setEnabled(false);
		addPlaylistPanel.setEnabled(false);
		deletePlaylistPanel.setEnabled(false);
		tableSongs.setModel(CustomTableModel.getCustomTableModel(selectedPlaylist, true));
		tableSongs.getColumnModel().getColumn(1).setMaxWidth(20);
		tableSongs.repaint();
	}

	void exitEditMode()
	{
		selectedPlaylistPanel.setEditMode(false);
		Set<String> songIds = new HashSet<>();
		for (int i = 0; i < tableSongs.getRowCount(); i++)
		{
			if ((Boolean) tableSongs.getValueAt(i, 1))
			{
				songIds.add(tableSongs.getSongIdFromRow(i));
			}
		}
		selectedPlaylist.updatePlaylist(songIds);
		selectedPlaylist.rebuildQueue(shuffle);
		for (PlaylistPanel pItem : playlistPanels)
		{
			if (!pItem.getPlaylist().title.equals(selectedPlaylist.title))
				pItem.setEnabled(true);
		}
		addPlaylistPanel.getButton().setEnabled(true);
		deletePlaylistPanel.getButton().setEnabled(true);
		addPlaylistPanel.setEnabled(true);
		deletePlaylistPanel.setEnabled(true);
		tableSongs.setModel(CustomTableModel.getCustomTableModel(selectedPlaylist));
		tableSongs.repaint();
		plugin.updateConfig();
	}
}
