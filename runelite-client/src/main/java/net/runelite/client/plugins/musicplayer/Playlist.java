package net.runelite.client.plugins.musicplayer;

import lombok.Getter;

import java.util.*;

class Playlist
{
	@Getter
	PlaylistData playlistData;

	String title;

	private final int MAX_BACK = 100;

	private Set<String> songs;

	private Deque<String> songQueue;
	private Deque<String> songsPlayed;

	Playlist(PlaylistData playlistData)
	{
		super();
		this.playlistData = playlistData;
		this.title = playlistData.title;
		this.songs = playlistData.songs;
		this.songQueue = new ArrayDeque<>();
		this.songsPlayed = new ArrayDeque<>();
	}

	Playlist(String title, Set<String> songs)
	{
		this(new PlaylistData(title, songs));
	}

	void updatePlaylist(Set<String> songs)
	{
		this.playlistData.songs = songs;
		this.songs = songs;
	}

	void rebuildQueue(boolean shuffle)
	{
		if (!shuffle)
		{
			songQueue = new ArrayDeque<>();
			for (String key : MusicPlayerPlugin.songsOrderedAlpha)
			{
				if (songs.contains(key))
				{
					songQueue.addFirst(key);
				}
			}
		}
		else
		{
			Random rand = new Random();
			List<String> songList = new ArrayList<>();
			for (String songId : MusicPlayerPlugin.songsOrderedAlpha)
			{
				if (songs.contains(songId))
				{
					songList.add(rand.nextInt(songList.size() + 1), songId);
				}
			}
			songQueue = new ArrayDeque<>(songList);
		}
	}

	void setCurrentSongId(String songId)
	{
		if (!songs.contains(songId))
		{
			return;
		}

		songsPlayed.addLast(songId);
		if (songsPlayed.size() > MAX_BACK)
		{
			songsPlayed.removeFirst();
		}
	}

	private String skipNextSongId()
	{
		if (this.songs.isEmpty() || this.songQueue.isEmpty())
		{
			return "";
		}
		return this.songQueue.removeLast();
	}

	String getPrevSongId()
	{
		if (hasPrevSongId())
		{
			String song = songsPlayed.removeLast();
			songQueue.addLast(song);
			return songsPlayed.getLast();
		}
		return "";
	}

	String getCurrentSongId()
	{
		if (hasCurrentSongId())
		{
			return songsPlayed.getLast();
		}
		return "";
	}

	String getNextSongId()
	{
		String songId = skipNextSongId();
		if (songId.equals(""))
		{
			return "";
		}
		songsPlayed.addLast(songId);
		if (songsPlayed.size() > MAX_BACK)
		{
			songsPlayed.removeFirst();
		}
		return songId;
	}

	boolean hasPrevSongId()
	{
		return songsPlayed.size() > 1;
	}

	boolean hasCurrentSongId()
	{
		return songsPlayed.size() > 0;
	}

	boolean hasNextSongId()
	{
		return !songQueue.isEmpty();
	}

	boolean contains(String songId)
	{
		return this.songs.contains(songId);
	}

	boolean isEmpty()
	{
		return songs.isEmpty();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Playlist)
		{
			return ((Playlist) obj).title.equals(title);
		}
		return false;
	}
}
