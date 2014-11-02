package be.maximvdw.spigotsite.utils.chat;

import org.bukkit.ChatColor;

/**
 * MVdW Software Color Utilities
 * 
 * @project BasePlugin
 * @version 1.0
 * @author Maxim Van de Wynckel (Maximvdw)
 * @site http://www.mvdw-software.be/
 */
public class ColorUtils {
	/**
	 * Convert string to colors
	 * 
	 * @param input
	 *            String
	 * @return Colored String
	 */
	public static String toColors(String input) {
		if (input == null) {
			return null;
		}
		return input.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	/**
	 * Get last color
	 * 
	 * @param input
	 *            Input string
	 * @return Chat color
	 */
	public static ChatColor getLastColor(String input) {
		if (input == null)
			return ChatColor.RESET;
		int length = input.length();
		// Search backwards from the end as it is faster
		for (int index = length - 1; index > -1; index--) {
			char section = input.charAt(index);
			if (section == ChatColor.COLOR_CHAR && index < length - 1) {
				char c = input.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);

				if (color != null) {
					return color;
				}
			}
		}
		return ChatColor.RESET;
	}

	/**
	 * Remove colors from string
	 * 
	 * @param input
	 *            String
	 * @return Cleared string
	 */
	public static String removeColors(String input) {
		if (input == null) {
			return null;
		}
		return ChatColor.stripColor(input.replaceAll("(&([a-fk-or0-9]))", ""));
	}
}
