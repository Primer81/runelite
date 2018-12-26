package net.runelite.client.plugins.musicplayer;

import net.runelite.client.ui.ColorScheme;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Dimension;
import java.awt.GridBagLayout;

class ControlButton extends JPanel
{
	private final Dimension SIZE = new Dimension(MusicPlayerPanel.ITEM_HEIGHT, MusicPlayerPanel.ITEM_HEIGHT);

	private final Border RAISED_BORDER = BorderFactory.createRaisedBevelBorder();
	private final Border LOWERED_BORDER = BorderFactory.createLoweredBevelBorder();

	private JLabel label;

	ControlButton(ImageIcon icon)
	{
		setLayout(new GridBagLayout());
		label = new JLabel(icon);
		add(label);
		setOpaque(true);
		setPressed(false);
	}

	void setIcon(ImageIcon icon)
	{
		label.setIcon(icon);
	}

	void setPressed(boolean pressed)
	{
		if (pressed)
		{
			setBorder(LOWERED_BORDER);
			setBackground(ColorScheme.DARK_GRAY_COLOR);
		}
		else
		{
			setBorder(RAISED_BORDER);
			setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return SIZE;
	}

	@Override
	public Dimension getMaximumSize()
	{
		return SIZE;
	}

	@Override
	public Dimension getMinimumSize()
	{
		return SIZE;
	}
}
