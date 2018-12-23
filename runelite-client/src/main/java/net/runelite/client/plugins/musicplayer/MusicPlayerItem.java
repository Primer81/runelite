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

	public Component spacer;

	final Border defaultBorder = new EmptyBorder(1, 5, 1, 1);

	MusicPlayerItem(BufferedImage image, String description)
	{
		super();

		SpringLayout layout = new SpringLayout();
		//this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setLayout(layout);
		this.setBorder(defaultBorder);
		this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, ITEM_HEIGHT));
		this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		this.spacer = Box.createVerticalStrut(5);

		label = new JLabel(description);
		label.setForeground(Color.WHITE);
		//this.add(Box.createHorizontalStrut(5));
		this.add(label);

		// Add button
		// NOTE: the button icon should be roughly 20x20 px
		button = new JLabel();
		if (image != null)
		{
			ImageIcon icon = new ImageIcon(image);
			ImageIcon iconHover = new ImageIcon(ImageUtil.grayscaleImage(image));
			button.setIcon(icon);
			button.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseEntered(MouseEvent e)
				{
					button.setIcon(iconHover);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					button.setIcon(icon);
				}
			});
		}
		this.button.setPreferredSize(new Dimension(ITEM_HEIGHT, ITEM_HEIGHT));
		this.button.setMinimumSize(new Dimension(ITEM_HEIGHT, ITEM_HEIGHT));
		//this.add(Box.createHorizontalGlue());
		this.add(button);

		// Set alignment
		this.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Set constraints
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, label, 5, SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.EAST, button, 5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, button, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, button, 5, SpringLayout.SOUTH, this);

		layout.putConstraint(SpringLayout.EAST, label, 5, SpringLayout.WEST, button);
	}

	private void receiveMouseEvent(MouseEvent e)
	{
		e.setSource(this);
		this.dispatchEvent(e);
	}
}
