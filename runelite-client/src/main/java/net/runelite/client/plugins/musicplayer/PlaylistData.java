package net.runelite.client.plugins.musicplayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PlaylistData
{
	String title;
	Set<String> songIds;
}
