package net.runelite.client.plugins.musicplayer;

import lombok.Getter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Set;

class Playlist
{
	@Getter
	PlaylistData playlistData;

	String title;

	private final int MAX_BACK = 100;

	private Set<String> songIds;

	private Deque<String> songQueue;
	private Deque<String> songsPlayed;

	private List<String> shuffledList = new ArrayList<>();

	Playlist(PlaylistData playlistData)
	{
		super();
		this.playlistData = playlistData;
		this.title = playlistData.title;
		this.songIds = playlistData.songIds;
		this.songQueue = new ArrayDeque<>();
		this.songsPlayed = new ArrayDeque<>();
	}

	Playlist(String title, Set<String> songIds)
	{
		this(new PlaylistData(title, songIds));
	}

	void updatePlaylist(Set<String> songs)
	{
		this.playlistData.songIds = songs;
		this.songIds = songs;
	}

	void shufflePlaylist()
	{
		Random rand = new Random();
		shuffledList = new ArrayList<>();
		for (String songId : MusicPlayerPlugin.songsOrderedAlpha)
		{
			if (songIds.contains(songId))
			{
				shuffledList.add(rand.nextInt(shuffledList.size() + 1), songId);
			}
		}
		if (!shuffledList.isEmpty() && !songsPlayed.isEmpty() && shuffledList.get(0).equals(songsPlayed.peekLast()))
		{
			String first = shuffledList.get(0);
			String last = shuffledList.get(shuffledList.size() - 1);
			shuffledList.set(shuffledList.size() - 1, first);
			shuffledList.set(0, last);
		}
	}

	void rebuildQueue(boolean shuffle)
	{
		if (!shuffle)
		{
			songQueue = new ArrayDeque<>();
			for (String key : MusicPlayerPlugin.songsOrderedAlpha)
			{
				if (songIds.contains(key))
				{
					songQueue.addFirst(key);
				}
			}
		}
		else
		{
			if (shuffledList.isEmpty())
			{
				shufflePlaylist();
			}
			songQueue = new ArrayDeque<>(shuffledList);
		}
	}

	void setCurrentSongId(String songId)
	{
		if (!songIds.contains(songId))
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
		if (this.songIds.isEmpty() || this.songQueue.isEmpty())
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
		return this.songIds.contains(songId);
	}

	boolean isEmpty()
	{
		return songIds.isEmpty();
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
