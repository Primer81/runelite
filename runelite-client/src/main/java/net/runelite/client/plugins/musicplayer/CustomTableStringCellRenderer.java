package net.runelite.client.plugins.musicplayer;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CustomTableStringCellRenderer extends JLabel implements TableCellRenderer
{
	CustomTableStringCellRenderer()
	{
		super();
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		setName("Table.cellRenderer");
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (table == null)
		{
			return this;
		}

		CustomTable cTable = (CustomTable) table;

		setFont(table.getFont());

		if (isSelected)
		{
			super.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			super.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
		}
		else if (cTable.getHoveredRow() == row && cTable.getHoveredColumn() == column)
		{
			super.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			super.setForeground(Color.WHITE);
		}
		else
		{
			super.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			super.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}

		setValue(value);

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

	/**
	 * Sets the <code>String</code> object for the cell being rendered to
	 * <code>value</code>.
	 *
	 * @param value  the string value for this cell; if value is
	 *          <code>null</code> it sets the text value to an empty string
	 * @see JLabel#setText
	 *
	 */
	protected void setValue(Object value)
	{
		setText((value == null) ? "" : value.toString());
	}
}
