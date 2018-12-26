package net.runelite.client.plugins.musicplayer;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class PlaylistPanel extends MusicPlayerPanel
{
	@Getter
	private Playlist playlist;

	private final Border selectedBorder = new MatteBorder(1, 5, 1, 1, ColorScheme.BRAND_ORANGE);

	private ImageIcon iconConfig;
	private ImageIcon iconConfigHover;
	private ImageIcon iconDone;
	private ImageIcon iconDoneHover;

	private boolean selected;
	private boolean editMode;

	PlaylistPanel(MusicPlayerPluginPanel parent, Playlist playlist)
	{
		super(null, playlist.title);
		this.setSelected(false);
		this.playlist = playlist;
		selected = false;
		editMode = false;

		setToolTipText(playlist.title);

		BufferedImage imageConfig = ImageUtil.getResourceStreamFromClass(PlaylistPanel.class, "config.png");
		iconConfig = new ImageIcon(imageConfig);
		iconConfigHover = new ImageIcon(ImageUtil.grayscaleImage(imageConfig));

		BufferedImage imageDone = ImageUtil.getResourceStreamFromClass(PlaylistPanel.class, "done.png");
		iconDone = new ImageIcon(imageDone);
		iconDoneHover = new ImageIcon(ImageUtil.grayscaleImage(imageDone));

		button.setIcon(iconConfig);

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (!editMode)
				{
					parent.enterEditMode();
				}
				else
				{
					parent.exitEditMode();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (editMode)
				{
					button.setIcon(iconDoneHover);
				}
				else
				{
					button.setIcon(iconConfigHover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (!button.isEnabled())
				{
					return;
				}

				if (editMode)
				{
					button.setIcon(iconDone);
				}
				else
				{
					button.setIcon(iconConfig);
				}
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				onMouseEnter();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				onMouseExit();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!isEnabled())
				{
					return;
				}
				parent.setSelectedPlaylistPanel((PlaylistPanel) e.getSource());
			}
		});
	}

	void setSelected(boolean selected)
	{
		this.selected = selected;
		if (selected)
		{
			setBorder(selectedBorder);
			label.setForeground(Color.WHITE);
			button.setEnabled(true);
		}
		else
		{
			this.label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			this.setBorder(defaultBorder);
			this.button.setEnabled(false);
		}
	}

	void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
		if (editMode)
		{
			button.setIcon(iconDoneHover);
		}
		else
		{
			button.setIcon(iconConfigHover);
		}
	}

	private void onMouseEnter()
	{
		if (!isEnabled())
		{
			return;
		}

		label.setForeground(Color.WHITE);
	}

	private void onMouseExit()
	{
		if (!isEnabled())
		{
			return;
		}

		if (!selected)
		{
			label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		}
	}
}
