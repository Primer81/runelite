package net.runelite.client.plugins.musicplayer;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class MusicPlayerItem extends JPanel
{
	static final int ITEM_HEIGHT = 40;

	public JLabel button;
	public JLabel label;

	private ImageIcon icon;
	private ImageIcon iconHover;

	Component spacer;

	final Border defaultBorder = new EmptyBorder(1, 5, 1, 1);

	MusicPlayerItem(BufferedImage image, String description)
	{
		super();

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setBorder(defaultBorder);
		this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, ITEM_HEIGHT));
		this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		this.spacer = Box.createVerticalStrut(5);

		label = new JLabel(description)
		{
			@Override
			public Dimension getMaximumSize()
			{
				Dimension size = super.getMaximumSize();
				size.width = PluginPanel.PANEL_WIDTH - ITEM_HEIGHT - 10;
				return size;
			}
			@Override
			public Dimension getPreferredSize()
			{
				return getMaximumSize();
			}
			@Override
			public Dimension getMinimumSize()
			{
				return getMaximumSize();
			}
		};
		label.setForeground(Color.WHITE);
		this.add(Box.createHorizontalStrut(5));
		this.add(label);

		// Add button
		// NOTE: the button icon should be roughly 20x20 px
		button = new JLabel();
		if (image != null)
		{
			icon = new ImageIcon(image);
			iconHover = new ImageIcon(ImageUtil.grayscaleImage(image));
			button.setIcon(icon);
			button.addMouseListener(new MouseAdapter()
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
			});
		}
		this.add(Box.createHorizontalGlue());
		this.add(button);
		this.add(Box.createHorizontalStrut(10));

		// Set alignment
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	private void onMouseEnter()
	{
		if (iconHover != null && this.isEnabled() && button.isEnabled())
		{
			button.setIcon(iconHover);
		}
	}

	private void onMouseExit()
	{
		if (icon != null && this.isEnabled() && button.isEnabled())
		{
			button.setIcon(icon);
		}
	}
}
