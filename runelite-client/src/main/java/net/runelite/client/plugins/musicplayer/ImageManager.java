package net.runelite.client.plugins.musicplayer;

import net.runelite.client.util.ImageUtil;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

class ImageManager
{
	private static final Map<Integer, ImageIcon> iconMap = new HashMap<>();
	private static final Map<Integer, BufferedImage> imageMap = new HashMap<>();

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
