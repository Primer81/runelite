package net.runelite.client.plugins.musicplayer;

import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

class ImageManager
{
	enum Images
	{
		BACK_IMG("back.png"),
		CONFIG_IMG("config.png"),
		DONE_IMG("done.png"),
		LOOP_IMG("loop.png"),
		MINUS_IMG("minus.png"),
		MUSIC_IMG("music.png"),
		NEXT_IMG("next.png"),
		PAUSE_IMG("pause.png"),
		PLAY_IMG("play.png"),
		PLUS_IMG("plus.png"),
		SHUFFLE_IMG("shuffle.png");
		String fileName;
		Images(String fileName)
		{
			this.fileName = fileName;
		}
	}

	private static Map<Integer, ImageIcon> iconMap = new HashMap<>();
	private static Map<Integer, BufferedImage> imageMap = new HashMap<>();

	static BufferedImage getImage(Images image)
	{
		int imageKey = image.ordinal();

		if (!imageMap.containsKey(imageKey))
		{
			imageMap.put(imageKey, ImageUtil.getResourceStreamFromClass(ImageManager.class, image.fileName));
		}

		return imageMap.get(imageKey);
	}

	static ImageIcon getIcon(Images image)
	{
		return getIcon(image, false);
	}

	static ImageIcon getIcon(Images image, boolean hover)
	{
		int iconKey = image.ordinal() * 2;

		if (!iconMap.containsKey(iconKey))
		{
			iconMap.put(iconKey, new ImageIcon(getImage(image)));
		}

		if (!hover)
		{
			return iconMap.get(iconKey);
		}
		else
		{
			iconKey += 1;

			if (!iconMap.containsKey(iconKey))
			{
				iconMap.put(iconKey, new ImageIcon(ImageUtil.grayscaleImage(getImage(image))));
			}

			return iconMap.get(iconKey);
		}
	}
}
