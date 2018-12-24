package net.runelite.client.plugins.musicplayer;

import lombok.Getter;

import java.util.*;

class Playlist
{
	final int MAX_BACK = 100;

	@Getter
	RawPlaylist rawPlaylist;

	String title;
	private Set<String> songs;

	private Deque<String> songQueue;
	private Deque<String> songsPlayed;

	Playlist(RawPlaylist rawPlaylist)
	{
		super();
		this.rawPlaylist = rawPlaylist;
		this.title = rawPlaylist.title;
		this.songs = rawPlaylist.songs;
		this.songQueue = new ArrayDeque<>();
		this.songsPlayed = new ArrayDeque<>();
		rebuildQueue();
	}

	Playlist(String title, Set<String> songs)
	{
		this(new RawPlaylist(title, songs));
	}

	void updatePlaylist(Set<String> songs)
	{
		this.rawPlaylist.songs = songs;
		this.songs = songs;
		rebuildQueue();
	}

	private void rebuildQueue()
	{
		this.songQueue = new ArrayDeque<>();
		for (String key : MusicPlayerPlugin.musicIndicesInAlphaOrder)
		{
			if (songs.contains(key))
			{
				this.songQueue.addFirst(key);
			}
		}
	}

	void setCurrentSongId(String songId)
	{
		if (!songs.contains(songId))
		{
			return;
		}

		while (!skipNextSongId().equals(songId))
		{
		}

		songsPlayed.addLast(songId);
		if (songsPlayed.size() > MAX_BACK)
		{
			songsPlayed.removeFirst();
		}
	}

	private String skipNextSongId()
	{
		if (this.songs.isEmpty())
		{
			return "";
		}
		if (this.songQueue.isEmpty())
		{
			rebuildQueue();
		}
		return this.songQueue.removeLast();
	}

	String getCurrentSongId()
	{
		if (hasCurrentSongId())
		{
			return songsPlayed.getLast();
		}
		return null;
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

	String getPrevSongId()
	{
		if (hasPrevSongId())
		{
			String song = songsPlayed.removeLast();
			songQueue.addLast(song);
			return songsPlayed.getLast();
		}
		return null;
	}

	boolean hasPrevSongId()
	{
		return songsPlayed.size() > 1;
	}

	boolean hasCurrentSongId()
	{
		return songsPlayed.size() > 0;
	}

	boolean contains(String songId)
	{
		return this.songs.contains(songId);
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
