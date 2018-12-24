package net.runelite.client.plugins.musicplayer;

import com.google.inject.Singleton;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Singleton
class MusicPlayerPanel extends PluginPanel
{
	MusicPlayerPlugin plugin;

	ImageIcon iconBack;
	ImageIcon iconBackHover;

	ImageIcon iconPause;
	ImageIcon iconPauseHover;

	ImageIcon iconPlay;
	ImageIcon iconPlayHover;

	ImageIcon iconNext;
	ImageIcon iconNextHover;

	ImageIcon iconShuffle;
	ImageIcon iconShuffleHover;

	ImageIcon iconLoop;
	ImageIcon iconLoopHover;

	private final Color COLOR_TITLE = Color.WHITE;
	private final Color COLOR_SUBTITLE = Color.WHITE;

	MusicPlayer musicPlayer;

	static Map<String, Integer> musicRowIndex = new HashMap<>();
	static Map<Integer, String> rowMusicIndex = new HashMap<>();
	CustomTable tableSongs;

	Playlist playlistAllSongs;
	Playlist selectedPlaylist;
	List<Playlist> playlists;
	List<PlaylistItem> playlistItems;
	JPanel playlistPanel;
	MusicPlayerItem itemAddPlaylist;
	MusicPlayerItem itemDeletePlaylist;

	private boolean loop;
	private boolean shuffle;

	private String songPlaying;

	private JLabel subtitlePlaying;
	private final String PLAY_FORMAT = "<html>%s<font color='rgb(55, 240, 70)'>%s</font></html>";
	private final String PLAY_PRE = "";
	private final String PLAY_DEFAULT = "No Playlist Selected";

	PlaylistItem selectedPlaylistItem;

	JLabel buttonBack;
	JLabel buttonPausePlay;
	JLabel buttonNext;
	private JLabel buttonShuffle;
	private JLabel buttonLoop;

	MusicPlayerPanel(MusicPlayerPlugin plugin)
	{
		super();

		// Initialize fields
		musicPlayer = new MusicPlayer(meta ->
			{
				if (meta.getType() == 0x2F)  // End of track
				{
					if (shuffle)
					{
						loadNextSong();
						this.musicPlayer.play();
					}
					else if (loop)
					{
						this.changeSong(songPlaying);
						this.musicPlayer.play();
					}
					else
					{
						loadNextSong();
						this.musicPlayer.play();
					}
				}
			}
		);
		this.plugin = plugin;
		this.playlists = new ArrayList<>(plugin.getPlaylists());
		playlistItems = new ArrayList<>();
		loop = false;
		shuffle = true;

		// Set layout
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(boxLayout);

		// Set scroll bar policy
		getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Add all components to the panel
		initTitleSection();
		this.add(Box.createVerticalStrut(10));
		initPlayingSection();
		this.add(Box.createVerticalStrut(10));
		initControlsSection();
		this.add(Box.createVerticalStrut(10));
		initSongListSection();
		this.add(Box.createVerticalStrut(10));
		initPlaylistSection();
	}

	private void initTitleSection()
	{
		// Add title
		JLabel title = new JLabel("<html>Music Player</html>");
		title.setForeground(COLOR_TITLE);
		title.setBorder(new MatteBorder(0, 0, 1, 0, ColorScheme.BRAND_ORANGE));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);

		this.add(title);
		this.add(Box.createVerticalStrut(5));
	}

	private void initPlayingSection()
	{
		// Add playing subtitle
		subtitlePlaying = new JLabel(String.format(PLAY_FORMAT, PLAY_PRE, PLAY_DEFAULT));
		subtitlePlaying.setForeground(COLOR_SUBTITLE);
		subtitlePlaying.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitlePlaying);
		this.add(Box.createVerticalStrut(5));
	}

	private void initControlsSection()
	{
		// Define images
		final BufferedImage imageBack = ImageUtil.getResourceStreamFromClass(getClass(), "back.png");
		iconBack = new ImageIcon(imageBack);
		iconBackHover = new ImageIcon(ImageUtil.grayscaleImage(imageBack));

		final BufferedImage imagePause = ImageUtil.getResourceStreamFromClass(getClass(), "pause.png");
		iconPause = new ImageIcon(imagePause);
		iconPauseHover = new ImageIcon(ImageUtil.grayscaleImage(imagePause));

		final BufferedImage imagePlay = ImageUtil.getResourceStreamFromClass(getClass(), "play.png");
		iconPlay = new ImageIcon(imagePlay);
		iconPlayHover = new ImageIcon(ImageUtil.grayscaleImage(imagePlay));

		final BufferedImage imageNext = ImageUtil.getResourceStreamFromClass(getClass(), "next.png");
		iconNext = new ImageIcon(imageNext);
		iconNextHover = new ImageIcon(ImageUtil.grayscaleImage(imageNext));

		final BufferedImage imageShuffle = ImageUtil.getResourceStreamFromClass(getClass(), "shuffle.png");
		iconShuffle = new ImageIcon(imageShuffle);
		iconShuffleHover = new ImageIcon(ImageUtil.grayscaleImage(imageShuffle));

		final BufferedImage imageLoop = ImageUtil.getResourceStreamFromClass(getClass(), "loop.png");
		iconLoop = new ImageIcon(imageLoop);
		iconLoopHover = new ImageIcon(ImageUtil.grayscaleImage(imageLoop));

		// Add controls subtitle
		JLabel subtitleControls = new JLabel("Controls:");
		subtitleControls.setForeground(COLOR_SUBTITLE);
		subtitleControls.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitleControls);
		this.add(Box.createVerticalStrut(5));

		// Add controls
		JPanel panelControls = new JPanel();
		panelControls.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, MusicPlayerItem.ITEM_HEIGHT));
		panelControls.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		BoxLayout layoutControls = new BoxLayout(panelControls, BoxLayout.LINE_AXIS);
		panelControls.setLayout(layoutControls);
		panelControls.setAlignmentX(Component.LEFT_ALIGNMENT);

		buttonBack = new JLabel(iconBack);
		buttonPausePlay = new JLabel(iconPlay);
		buttonNext = new JLabel(iconNext);
		buttonShuffle = new JLabel(iconShuffle);
		buttonLoop = new JLabel(iconLoop);

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
				double seconds = musicPlayer.sequencer.getMicrosecondPosition() / 1000000.0;
				if (seconds > 5 || !selectedPlaylist.hasPrevSongId())
				{
					musicPlayer.sequencer.setTickPosition(0);
				}
				else
				{
					boolean wasRunning = musicPlayer.sequencer.isRunning();
					if (wasRunning)
						musicPlayer.pause();
					loadPrevSong();
					if (wasRunning)
						musicPlayer.play();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonBack.setIcon(iconBackHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonBack.setIcon(iconBack);
			}
		});
		buttonPausePlay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (selectedPlaylistItem.isActionPause)
				{
					musicPlayer.pause();
					buttonPausePlay.setIcon(iconPlay);
				}
				else
				{
					musicPlayer.play();
					buttonPausePlay.setIcon(iconPause);
				}
				selectedPlaylistItem.isActionPause = !selectedPlaylistItem.isActionPause;
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (selectedPlaylistItem.isActionPause)
				{
					buttonPausePlay.setIcon(iconPauseHover);
				}
				else
				{
					buttonPausePlay.setIcon(iconPlayHover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (selectedPlaylistItem.isActionPause)
				{
					buttonPausePlay.setIcon(iconPause);
				}
				else
				{
					buttonPausePlay.setIcon(iconPlay);
				}
			}
		});
		buttonNext.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				musicPlayer.skip();
				if (!musicPlayer.sequencer.isRunning())
				{
					loadNextSong();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonNext.setIcon(iconNextHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonNext.setIcon(iconNext);
			}
		});
		buttonShuffle.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				buttonShuffle.setEnabled(false);
				shuffle = true;
				buttonLoop.setEnabled(true);
				loop = false;
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonShuffle.setIcon(iconShuffleHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonShuffle.setIcon(iconShuffle);
			}
		});
		buttonLoop.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				buttonShuffle.setEnabled(true);
				shuffle = false;
				buttonLoop.setEnabled(false);
				loop = true;
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonLoop.setIcon(iconLoopHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonLoop.setIcon(iconLoop);
			}
		});

		Border buttonBorder = new EmptyBorder(8, 8, 8, 8);
		buttonBack.setBorder(buttonBorder);
		buttonPausePlay.setBorder(buttonBorder);
		buttonNext.setBorder(buttonBorder);
		buttonShuffle.setBorder(buttonBorder);
		buttonLoop.setBorder(buttonBorder);

		panelControls.add(Box.createHorizontalStrut(3));

		panelControls.add(buttonBack);
		panelControls.add(Box.createHorizontalStrut(9));
		panelControls.add(buttonPausePlay);
		panelControls.add(Box.createHorizontalStrut(9));
		panelControls.add(buttonNext);
		panelControls.add(Box.createHorizontalStrut(9));
		panelControls.add(buttonShuffle);
		panelControls.add(Box.createHorizontalStrut(9));
		panelControls.add(buttonLoop);
		panelControls.add(Box.createHorizontalStrut(4));

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
		tableSongs = new CustomTable(new CustomTableModel(new Vector<>(), new Vector<>()));
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

				if (!tableSongs.isInEditMode())
				{
					String songId = rowMusicIndex.get(rowSelected);
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
		// Define images
		final BufferedImage imagePlus = ImageUtil.getResourceStreamFromClass(getClass(), "plus.png");
		final BufferedImage imageMinus = ImageUtil.getResourceStreamFromClass(getClass(), "minus.png");

		// Add playlist subtitle
		JLabel subtitlePlaylists = new JLabel("Playlists:");
		subtitlePlaylists.setForeground(COLOR_SUBTITLE);
		subtitlePlaylists.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(subtitlePlaylists);
		this.add(Box.createVerticalStrut(5));

		// Create playlist panel
		playlistPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(playlistPanel, BoxLayout.PAGE_AXIS);
		playlistPanel.setLayout(boxLayout);
		this.add(playlistPanel);

		// Add default playlists
		playlistAllSongs = new Playlist("All Songs", plugin.musicNameIndex.keySet());
		PlaylistItem itemAllSongs = new PlaylistItem(this, playlistAllSongs);
		itemAllSongs.remove(itemAllSongs.button);

		this.playlists.add(0, playlistAllSongs);

		addNewPlaylistItem(itemAllSongs);

		itemAllSongs.selectPlaylist();

		// Load user playlists
		for (Playlist playlist : this.plugin.getPlaylists()/*this.playlists.subList(1, this.playlists.size()*/)
		{
			addNewPlaylistItem(new PlaylistItem(this, playlist));
		}

		// Add playlist options
		itemAddPlaylist = new MusicPlayerItem(imagePlus, "Create New Playlist");
		itemAddPlaylist.button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!itemAddPlaylist.isEnabled() || !itemAddPlaylist.button.isEnabled())
				{
					return;
				}

				addNewPlaylist();
			}
		});
		this.add(itemAddPlaylist);
		this.add(Box.createVerticalStrut(5));
		itemDeletePlaylist = new MusicPlayerItem(imageMinus, "Delete Playlist");
		itemDeletePlaylist.button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!itemDeletePlaylist.isEnabled() || !itemDeletePlaylist.button.isEnabled())
				{
					return;
				}

				deleteSelectedPlaylist();
			}
		});
		this.add(itemDeletePlaylist);
		this.add(Box.createVerticalStrut(5));
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
		PlaylistItem pItem = new PlaylistItem(this, playlist);

		this.plugin.getPlaylists().add(playlist);
		this.plugin.updateConfig();
		this.playlists.add(playlist);
		addNewPlaylistItem(pItem);

		pItem.selectPlaylist();
		pItem.enterEditMode();
	}

	private void deleteSelectedPlaylist()
	{
		if (!selectedPlaylist.equals(playlistAllSongs))  // Check not equal to default playlist
		{
			String msg = String.format("Deleting the \"%s\" playlist.\nAre you sure you wish to continue?",
				selectedPlaylist.title);
			String title = String.format("Delete %s?", selectedPlaylist.title);
			int result = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION)
			{
				this.plugin.getPlaylists().remove(selectedPlaylist);
				this.plugin.updateConfig();
				playlists.remove(selectedPlaylist);
				playlistPanel.remove(selectedPlaylistItem);
				playlistPanel.remove(selectedPlaylistItem.spacer);
				playlistItems.remove(selectedPlaylistItem);
				playlistItems.get(0).selectPlaylist();
			}
		}
	}

	private String indexToFilePath(String index)
	{
		return "music/" + index + " - " + plugin.musicNameIndex.get(index) + ".mid";
	}

	void reloadSong()
	{
		changeSong(selectedPlaylist.getCurrentSongId());
	}

	void loadNextSong()
	{
		String songId = selectedPlaylist.getNextSongId();
		if (!songId.equals(""))
		{
			changeSong(songId);
		}
	}

	void loadPrevSong()
	{
		if (selectedPlaylist.hasPrevSongId())
		{
			changeSong(selectedPlaylist.getPrevSongId());
		}
	}

	void changeSong(String songId)
	{
		songPlaying = songId;
		int rowInTable = musicRowIndex.get(songId);
		SwingUtilities.invokeLater(() -> tableSongs.setRowSelectionInterval(rowInTable, rowInTable));
		subtitlePlaying.setText(String.format(PLAY_FORMAT, PLAY_PRE, plugin.musicNameIndex.get(songPlaying)));
		this.musicPlayer.load(indexToFilePath(songPlaying));
	}

	private void addNewPlaylistItem(PlaylistItem pItem)
	{
		playlistPanel.add(pItem);
		playlistPanel.add(pItem.spacer);
		playlistItems.add(pItem);
		playlistPanel.revalidate();
		playlistPanel.repaint();
	}
}
