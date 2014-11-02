package be.maximvdw.spigotsite.ui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import be.maximvdw.spigotsite.utils.chat.ColorUtils;

/**
 * SendConsole
 * 
 * Log to the console.
 * 
 * @project BasePlugin
 * @version 1.0
 * @author Maxim Van de Wynckel (Maximvdw)
 * @site http://www.mvdw-software.be/
 */
public class SendConsole {
	static boolean enableLogging = false; // Enable logging
	static String prefix = "[MVdW] "; // Message prefix
	static Plugin plugin = null; // Plugin instance

	/**
	 * Initialize the console logger
	 * 
	 * @param plugin
	 *            Plugin
	 */
	public SendConsole(Plugin plugin) {
		SendConsole.plugin = plugin;
		SendConsole.prefix = "[" + plugin.getName() + "] ";
	}

	/**
	 * Send a colored message to the console
	 * 
	 * @param message
	 *            Message
	 */
	public static void message(String message) {
		ConsoleCommandSender console = SendConsole.plugin.getServer()
				.getConsoleSender();
		console.sendMessage(ChatColor
				.translateAlternateColorCodes('&', message));
	}

	/**
	 * Send an INFO message to the console
	 * 
	 * @param message
	 *            Info message
	 */
	public static void info(String message) {
		Bukkit.getLogger().info(prefix + message);
	}

	/**
	 * Send an WARNING message to the console
	 * 
	 * @param message
	 *            Warning message
	 */
	public static void warning(String message) {
		Bukkit.getLogger().warning(prefix + message);
	}

	/**
	 * Send an SEVERE message to the console
	 * 
	 * @param message
	 *            Severe message
	 */
	public static void severe(String message) {
		message = ColorUtils.removeColors(message); // Remove colors
		Bukkit.getLogger().severe(prefix + message);
	}
}
