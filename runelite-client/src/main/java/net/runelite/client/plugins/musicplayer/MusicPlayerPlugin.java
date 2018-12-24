package net.runelite.client.plugins.musicplayer;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;


import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginDescriptor(
	name = "Music Player",
	description = "Listen to OSRS music from the Music Player panel",
	tags = {"panel", "music"}
)
public class MusicPlayerPlugin extends Plugin
{
	private static final String PLUGIN_NAME = "Music Player";
	private static final String CONFIG_GROUP = "musicplayer";
	private static final String CONFIG_KEY = "playlists";
	private static final String ICON_FILE = "music.png";

	@Getter
	private final List<Playlist> playlists =  new ArrayList<>();

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	private NavigationButton uiNavigationButton;

	Map<String, String> musicNameIndex = new HashMap<>();
	static List<String> musicIndicesInAlphaOrder = new ArrayList<>();

	@Override
	protected void startUp() throws Exception
	{
		loadIndex();
		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(playlists::add);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);

		MusicPlayerPanel pluginPanel = new MusicPlayerPanel(this);

		uiNavigationButton = NavigationButton.builder()
			.tooltip(PLUGIN_NAME)
			.icon(icon)
			.priority(6)
			.panel(pluginPanel)
			.build();

		clientToolbar.addNavigation(uiNavigationButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(uiNavigationButton);
	}

	void loadIndex()
	{
		final Gson gson = new Gson();
		final TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>()
		{
		};
		InputStream isMusicIndex = getClass().getResourceAsStream("music_index.json");
		musicNameIndex = gson.fromJson(new InputStreamReader(isMusicIndex), typeToken.getType());
		Collection<Map.Entry<String, String>> entryCollection = musicNameIndex.entrySet();
		List<Map.Entry<String, String>> musicSortByName = asSortedList(entryCollection, Comparator.comparing(Map.Entry::getValue));
		musicIndicesInAlphaOrder = new ArrayList<>();
		for (Map.Entry<String, String> entry : musicSortByName)
		{
			musicIndicesInAlphaOrder.add(entry.getKey());
		}
	}

	private static <T> List<T> asSortedList(Collection<T> c, Comparator<T> comp)
	{
		List<T> list = new ArrayList<>(c);
		list.sort(comp);
		return list;
	}

	public void updateConfig()
	{
		if (playlists.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
			return;
		}

		final Gson gson = new Gson();
		final String json = gson
			.toJson(playlists.stream().map(Playlist::getRawPlaylist).collect(Collectors.toList()));
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
	}

	private Stream<Playlist> loadConfig(String json)
	{
		if (Strings.isNullOrEmpty(json))
		{
			return Stream.empty();
		}

		final Gson gson = new Gson();
		final List<RawPlaylist> rawPlaylistData = gson.fromJson(json, new TypeToken<ArrayList<RawPlaylist>>()
		{
		}.getType());

		return rawPlaylistData.stream().map(Playlist::new);
	}
}
