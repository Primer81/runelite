package net.runelite.client.plugins.musicplayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class CustomTableBoolCellRenderer extends JCheckBox implements TableCellRenderer
{
	CustomTableBoolCellRenderer()
	{
		super();
		setBorder(new EmptyBorder(1, 1, 1, 1));
		setName("Table.cellRenderer");
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (table == null)
		{
			return this;
		}

		this.setSelected((Boolean) value);

		return this;
	}

	/*
	 * The following methods are overridden as a performance measure to
	 * to prune code-paths are often called in the case of renders
	 * but which we know are unnecessary.  Great care should be taken
	 * when writing your own renderer to weigh the benefits and
	 * drawbacks of overriding methods like these.
	 */

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public boolean isOpaque()
	{
		Color back = getBackground();
		Component p = getParent();
		if (p != null)
		{
			p = p.getParent();
		}

		// p should now be the JTable.
		boolean colorMatch = (back != null) && (p != null) &&
			back.equals(p.getBackground()) &&
			p.isOpaque();
		return !colorMatch && super.isOpaque();
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 *
	 * @since 1.5
	 */
	public void invalidate()
	{

	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public void validate()
	{

	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public void revalidate()
	{

	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public void repaint(long tm, int x, int y, int width, int height)
	{

	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public void repaint(Rectangle r)
	{

	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 *
	 * @since 1.5
	 */
	public void repaint()
	{
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		// Strings get interned...
		if (propertyName.equals("text")
			|| propertyName.equals("labelFor")
			|| propertyName.equals("displayedMnemonic")
			|| ((propertyName.equals("font") || propertyName.equals("foreground"))
			&& oldValue != newValue
			&& getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null))
		{
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
	{

	}
}
