package net.runelite.client.plugins.musicplayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("musicplayer")
public interface MusicPlayerConfig extends Config
{
	@ConfigItem(
		keyName = "volume",
		name = "Adjust Volume",
		description = "Adjust the volume of the music player in the range 0-100"
	)
	default int volume()
{
	return 50;
}
}
