package net.runelite.client.plugins.musicplayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawPlaylist
{
	String title;
	Set<String> songs;
}
