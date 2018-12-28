package net.runelite.client.plugins.musicplayer;

import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class MusicPlayerPanel extends JPanel
{
	private static final int ITEM_HEIGHT = 40;

	@Getter
	protected JLabel button;

	@Getter
	protected JLabel label;

	private ImageIcon icon;
	private ImageIcon iconHover;

	Component spacer;

	final Border defaultBorder = new EmptyBorder(1, 5, 1, 1);

	MusicPlayerPanel(BufferedImage image, String description)
	{
		super();

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(defaultBorder);
		setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, ITEM_HEIGHT));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		spacer = Box.createVerticalStrut(5);

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
		add(Box.createHorizontalStrut(5));
		add(label);

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
		add(Box.createHorizontalGlue());
		add(button);
		add(Box.createHorizontalStrut(10));

		setAlignmentX(Component.LEFT_ALIGNMENT);
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
