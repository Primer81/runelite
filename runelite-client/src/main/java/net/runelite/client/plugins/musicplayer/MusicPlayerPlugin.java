package net.runelite.client.plugins.musicplayer;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@PluginDescriptor(
	name = "Music Player",
	description = "Listen to OSRS music from the Music Player panel",
	tags = {"panel", "music"}
)
public class MusicPlayerPlugin extends Plugin
{
	private static final String PLUGIN_NAME = "Music Player";
	private static final String CONFIG_GROUP = "musicplayer";
	private static final String CONFIG_KEY = "savedPlaylists";

	@Getter
	private final List<Playlist> savedPlaylists =  new ArrayList<>();

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private MusicPlayerConfig config;

	private NavigationButton uiNavigationButton;

	private MusicPlayer musicPlayer;
	static Map<String, String> musicNameIndex = new HashMap<>();
	static List<String> songsOrderedAlpha = new ArrayList<>();

	@Provides
	MusicPlayerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MusicPlayerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		loadIndex();
		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(savedPlaylists::add);
		musicPlayer = new MusicPlayer();
		musicPlayer.addTickListener(actionEvent ->
		{
			int volume = config.volume();
			if (volume != musicPlayer.getVolume())
			{
				musicPlayer.setVolume(volume);
			}
		});
		MusicPlayerPluginPanel pluginPanel = new MusicPlayerPluginPanel(this, musicPlayer);
		uiNavigationButton = NavigationButton.builder()
			.tooltip(PLUGIN_NAME)
			.icon(ImageManager.getImage(Images.MUSIC_IMG))
			.priority(6)
			.panel(pluginPanel)
			.build();

		clientToolbar.addNavigation(uiNavigationButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(uiNavigationButton);
		musicPlayer.shutDown();
	}

	private void loadIndex()
	{
		final Gson gson = new Gson();
		final TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>()
		{
		};
		InputStream isMusicIndex = getClass().getResourceAsStream("music_index.json");
		musicNameIndex = gson.fromJson(new InputStreamReader(isMusicIndex), typeToken.getType());
		Collection<Map.Entry<String, String>> entryCollection = musicNameIndex.entrySet();
		List<Map.Entry<String, String>> musicSortByName = asSortedList(entryCollection, Comparator.comparing(Map.Entry::getValue));
		songsOrderedAlpha = new ArrayList<>();
		for (Map.Entry<String, String> entry : musicSortByName)
		{
			songsOrderedAlpha.add(entry.getKey());
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
		if (savedPlaylists.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
			return;
		}

		final Gson gson = new Gson();
		final String json = gson
			.toJson(savedPlaylists.stream().map(Playlist::getPlaylistData).collect(Collectors.toList()));
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
	}

	private Stream<Playlist> loadConfig(String json)
	{
		if (Strings.isNullOrEmpty(json))
		{
			return Stream.empty();
		}

		final Gson gson = new Gson();
		final List<PlaylistData> playlistData = gson.fromJson(json, new TypeToken<ArrayList<PlaylistData>>()
		{
		}.getType());

		return playlistData.stream().map(Playlist::new);
	}
}
