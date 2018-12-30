package net.runelite.client.plugins.musicplayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MusicPlayerPluginPanel extends PluginPanel
{
	private static final Logger logger = LoggerFactory.getLogger(MusicPlayerPluginPanel.class);

	// Constants
	private final Color COLOR_TITLE = Color.WHITE;
	private final Color COLOR_SUBTITLE = Color.WHITE;
	private final String PLAY_FORMAT = "<html>%s<font color='rgb(55, 240, 70)'>%s</font></html>";
	private final String PLAY_PRE = "";

	// Package classes
	private final MusicPlayerPlugin plugin;
	private final MusicPlayer musicPlayer;
	private final List<Playlist> playlists = new ArrayList<>();
	private Playlist selectedPlaylist;

	// Components
	private final MusicPlayerPanel addPlaylistPanel = new MusicPlayerPanel(ImageManager.getImage(Images.PLUS_IMG), "Create New Playlist");
	private final MusicPlayerPanel deletePlaylistPanel = new MusicPlayerPanel(ImageManager.getImage(Images.MINUS_IMG), "Delete Playlist");
	private final CustomTable tableSongs = new CustomTable();
	private final JLabel subtitlePlaying = new JLabel(String.format(PLAY_FORMAT, PLAY_PRE, ""));
	private final ControlButton buttonBack = new ControlButton(ImageManager.getIcon(Images.BACK_IMG));
	private final ControlButton buttonPausePlay = new ControlButton(ImageManager.getIcon(Images.PLAY_IMG));
	private final ControlButton buttonNext = new ControlButton(ImageManager.getIcon(Images.NEXT_IMG));
	private final ControlButton buttonShuffle = new ControlButton(ImageManager.getIcon(Images.SHUFFLE_IMG));
	private final ControlButton buttonLoop = new ControlButton(ImageManager.getIcon(Images.LOOP_IMG));
	private final JSlider sliderProgress = new JSlider();
	private final JLabel labelProgress = new JLabel("00:00 / 00:00");
	private final JPanel panelPlaylists = new JPanel();
	private final List<PlaylistPanel> playlistPanels = new ArrayList<>();
	private PlaylistPanel playlistPanelSelected;

	// Atomic fields
	private boolean shuffle;
	private boolean loop;

	MusicPlayerPluginPanel(MusicPlayerPlugin plugin, MusicPlayer musicPlayer)
	{
		super();
		this.plugin = plugin;
		this.musicPlayer = musicPlayer;
		musicPlayer.addMetaEventListener(meta ->
			{
				if (meta.getType() == 0x2F)  // End of track
				{
					logger.debug("end of track, loading next song");
					loadNextSong();
					musicPlayer.play();
				}
			}
		);
		shuffle = false;
		loop = false;
		playlists.addAll(plugin.getSavedPlaylists());

		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(boxLayout);

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
		subtitlePlaying.setForeground(COLOR_SUBTITLE);
		subtitlePlaying.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitlePlaying);
		this.add(Box.createVerticalStrut(5));
	}

	private void initControlsSection()
	{
		JLabel subtitleControls = new JLabel("Controls:");
		subtitleControls.setForeground(COLOR_SUBTITLE);
		subtitleControls.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitleControls);
		this.add(Box.createVerticalStrut(5));

		JPanel panelButtons = new JPanel();
		panelButtons.setOpaque(false);
		BoxLayout layoutControls = new BoxLayout(panelButtons, BoxLayout.LINE_AXIS);
		panelButtons.setLayout(layoutControls);
		panelButtons.setBorder(new EmptyBorder(3, 3, 0, 3));

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
				buttonBack.setIcon(ImageManager.getIcon(Images.BACK_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonBack.setIcon(ImageManager.getIcon(Images.BACK_IMG));
				buttonBack.setPressed(false);
			}
		});
		buttonPausePlay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (musicPlayer.isPlaying())
				{
					musicPlayer.pause();
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PLAY_IMG));
				}
				else
				{
					musicPlayer.play();
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PAUSE_IMG));
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
				if (musicPlayer.isPlaying())
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PAUSE_IMG, true));
				}
				else
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PLAY_IMG, true));
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (musicPlayer.isPlaying())
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PAUSE_IMG));
				}
				else
				{
					buttonPausePlay.setIcon(ImageManager.getIcon(Images.PLAY_IMG));
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
				buttonNext.setIcon(ImageManager.getIcon(Images.NEXT_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonNext.setIcon(ImageManager.getIcon(Images.NEXT_IMG));
				buttonNext.setPressed(false);
			}
		});
		buttonShuffle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				shuffle = !shuffle;
				if (shuffle)
				{
					selectedPlaylist.shufflePlaylist();
				}
				selectedPlaylist.rebuildQueue(shuffle);
				buttonShuffle.setPressed(shuffle);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonShuffle.setIcon(ImageManager.getIcon(Images.SHUFFLE_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonShuffle.setIcon(ImageManager.getIcon(Images.SHUFFLE_IMG));
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
				buttonLoop.setIcon(ImageManager.getIcon(Images.LOOP_IMG, true));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonLoop.setIcon(ImageManager.getIcon(Images.LOOP_IMG));
			}
		});

		panelButtons.add(buttonBack);
		panelButtons.add(buttonPausePlay);
		panelButtons.add(buttonNext);
		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(buttonShuffle);
		panelButtons.add(Box.createHorizontalGlue());
		panelButtons.add(buttonLoop);

		sliderProgress.setAlignmentX(BoxLayout.X_AXIS);
		sliderProgress.setBackground(ColorScheme.BRAND_ORANGE);
		sliderProgress.setValue(0);
		sliderProgress.setFocusable(false);

		labelProgress.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		labelProgress.setBorder(new EmptyBorder(0, 8, 0, 0));

		sliderProgress.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				updateProgressLabel();
			}
		});
		sliderProgress.addMouseListener(new MouseAdapter()
		{
			boolean wasRunning;

			@Override
			public void mousePressed(MouseEvent e)
			{
				wasRunning =  musicPlayer.isPlaying();
				if (wasRunning)
					musicPlayer.pause();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				double percentProgress = (double) sliderProgress.getValue() / (double) sliderProgress.getMaximum();
				musicPlayer.setPercentProgress(percentProgress);
				if (wasRunning)
					musicPlayer.play();
			}
		});
		musicPlayer.addTickListener(e ->
		{
			double percentProgress = musicPlayer.getPercentProgress();
			sliderProgress.setValue((int) (sliderProgress.getMaximum() * percentProgress));
			SimpleDateFormat df = new SimpleDateFormat("mm:ss");
			long msProgress = musicPlayer.getMicrosecondPosition() / 1000;
			long msMaximum = musicPlayer.getMicrosecondLength() / 1000;
			labelProgress.setText(String.format("%s / %s", df.format(msProgress), df.format(msMaximum)));
		});

		JPanel panelControls = new JPanel();
		panelControls.setAlignmentX(BoxLayout.X_AXIS);
		panelControls.setLayout(new BoxLayout(panelControls, BoxLayout.PAGE_AXIS));
		panelControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		sliderProgress.setAlignmentX(BoxLayout.X_AXIS);
		labelProgress.setAlignmentX(BoxLayout.X_AXIS);
		panelButtons.setAlignmentX(BoxLayout.X_AXIS);
		panelControls.add(panelButtons);
		panelControls.add(sliderProgress);
		panelControls.add(labelProgress);
		panelControls.add(Box.createVerticalStrut(5));

		add(panelControls);
	}

	private void initSongListSection()
	{
		JLabel subtitleSongList = new JLabel("Song List:");
		subtitleSongList.setForeground(COLOR_SUBTITLE);
		subtitleSongList.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(subtitleSongList);
		add(Box.createVerticalStrut(5));

		tableSongs.setAlignmentX(BoxLayout.X_AXIS);
		tableSongs.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		tableSongs.setTableHeader(null);
		tableSongs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableSongs.setDefaultRenderer(String.class, new CustomTableStringCellRenderer());
		tableSongs.setDefaultRenderer(Boolean.class, new CustomTableBoolCellRenderer());
		InputMap im = tableSongs.getInputMap(CustomTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke("DOWN"), "none");
		im.put(KeyStroke.getKeyStroke("UP"), "none");
		im.put(KeyStroke.getKeyStroke("LEFT"), "none");
		im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

		tableSongs.addMouseListener(new MouseAdapter()
		{
			private void select(int row, int col)
			{
				if (col == 0)
				{
					String songId = tableSongs.getSongIdFromRow(row);
					if (selectedPlaylist.hasCurrentSongId() && selectedPlaylist.getCurrentSongId().equals(songId))
					{
						selectedPlaylist.setCurrentSongId(songId);
					}
					changeSong(songId);
				}
				else
				{
					boolean value = (Boolean) tableSongs.getValueAt(row, 1);
					tableSongs.setValueAt(!value, row, 1);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				int rowSelected = tableSongs.rowAtPoint(e.getPoint());
				int colSelected = tableSongs.columnAtPoint(e.getPoint());
				select(rowSelected, colSelected);
			}
		});

		JScrollPane scrollPane = new JScrollPane(tableSongs);
		scrollPane.setAlignmentX(BoxLayout.X_AXIS);
		scrollPane.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 250));
		add(scrollPane);
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
		panelPlaylists.setAlignmentX(BoxLayout.X_AXIS);
		BoxLayout boxLayout = new BoxLayout(panelPlaylists, BoxLayout.PAGE_AXIS);
		panelPlaylists.setLayout(boxLayout);
		add(panelPlaylists);

		// Add default playlists
		Playlist playlistAllSongs = new Playlist("All Songs", MusicPlayerPlugin.musicNameIndex.keySet());
		playlists.add(0, playlistAllSongs);
		PlaylistPanel allSongsPanel = new PlaylistPanel(this, playlistAllSongs);
		allSongsPanel.remove(allSongsPanel.getButton());
		addPlaylistPanel(allSongsPanel);
		setPlaylistPanelSelected(allSongsPanel);
		loadNextSong();

		for (Playlist playlist : this.plugin.getSavedPlaylists())
		{
			addPlaylistPanel(new PlaylistPanel(this, playlist));
		}

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

		setPlaylistPanelSelected(pItem);
		enterEditMode();
	}

	private void addPlaylistPanel(PlaylistPanel pPanel)
	{
		panelPlaylists.add(pPanel);
		panelPlaylists.add(pPanel.spacer);
		playlistPanels.add(pPanel);
		panelPlaylists.revalidate();
		panelPlaylists.repaint();
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
				panelPlaylists.remove(playlistPanelSelected);
				panelPlaylists.remove(playlistPanelSelected.spacer);
				playlistPanels.remove(playlistPanelSelected);
				setPlaylistPanelSelected(playlistPanels.get(0));
			}
		}
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
		musicPlayer.load(songId);
		sliderProgress.setValue(0);
		updateProgressLabel();
	}

	private void updateProgressLabel()
	{
		double percentProgress = (double) sliderProgress.getValue() / (double) sliderProgress.getMaximum();
		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		long msMaximum = musicPlayer.getMicrosecondLength() / 1000;
		long msProgress = (long) (msMaximum * percentProgress);
		labelProgress.setText(String.format("%s / %s", df.format(msProgress), df.format(msMaximum)));
	}

	void setPlaylistPanelSelected(PlaylistPanel panel)
	{
		panel.setSelected(true);
		Playlist panelPlaylist = panel.getPlaylist();
		if (selectedPlaylist == null || !panelPlaylist.title.equals(selectedPlaylist.title))
		{
			if (playlistPanelSelected != null)
			{
				playlistPanelSelected.setSelected(false);
			}
			selectedPlaylist = panelPlaylist;
			playlistPanelSelected = panel;
			tableSongs.setModel(CustomTableModel.getCustomTableModel(panelPlaylist));
		}

		if (shuffle)
		{
			selectedPlaylist.shufflePlaylist();
		}
	}

	void enterEditMode()
	{
		playlistPanelSelected.setEditMode(true);
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
		playlistPanelSelected.setEditMode(false);
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
