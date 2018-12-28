package net.runelite.client.plugins.musicplayer;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PlaylistPanel extends MusicPlayerPanel
{
	@Getter
	private final Playlist playlist;

	private final Border selectedBorder = new MatteBorder(1, 5, 1, 1, ColorScheme.BRAND_ORANGE);
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
		button.setIcon(ImageManager.getIcon(Images.CONFIG_IMG));
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
					button.setIcon(ImageManager.getIcon(Images.DONE_IMG, true));
				}
				else
				{
					button.setIcon(ImageManager.getIcon(Images.CONFIG_IMG, true));
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
					button.setIcon(ImageManager.getIcon(Images.DONE_IMG));
				}
				else
				{
					button.setIcon(ImageManager.getIcon(Images.CONFIG_IMG));
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
				parent.setPlaylistPanelSelected((PlaylistPanel) e.getSource());
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
			setBorder(defaultBorder);
			label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			button.setEnabled(false);
		}
	}

	void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
		if (editMode)
		{
			button.setIcon(ImageManager.getIcon(Images.DONE_IMG, true));
		}
		else
		{
			button.setIcon(ImageManager.getIcon(Images.CONFIG_IMG, true));
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
