package net.runelite.client.plugins.musicplayer;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
	name = "Music Player",
	description = "Listen to OSRS music from the Music Player panel",
	tags = {"panel", "music"}
)
public class MusicPlayerPlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	private NavigationButton uiNavigationButton;

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "music.png");
		final MusicPlayerPanel uiPanel = new MusicPlayerPanel();

		uiNavigationButton = NavigationButton.builder()
			.tooltip("Music Player")
			.icon(icon)
			.priority(6)
			.panel(uiPanel)
			.build();

		clientToolbar.addNavigation(uiNavigationButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(uiNavigationButton);
	}
}
