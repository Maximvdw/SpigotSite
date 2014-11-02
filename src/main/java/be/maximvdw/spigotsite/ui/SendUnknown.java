package be.maximvdw.spigotsite.ui;

import org.bukkit.entity.Player;

/**
 * SendUnknown
 * 
 * Send a message to an unknown receiver.
 * 
 * @project BasePlugin
 * @version 1.0
 * @author Maxim Van de Wynckel (Maximvdw)
 * @site http://www.mvdw-software.be/
 */
public class SendUnknown {
	/**
	 * Send a message to an unkown receiver
	 * 
	 * @param message
	 *            Message to send
	 * @param sender
	 *            Reciever
	 */
	public static void toSender(String message, Object sender) {
		if (sender instanceof Player) {
			SendGame.toPlayer(message, (Player) sender); // Send to game
		} else {
			SendConsole.message(message); // Send to console
		}
	}
}
