package com.github.Mephilis7.PlayerManager;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class VAR
{
	//one class with the most important variables
	public static Logger log = Logger.getLogger("Minecraft");
	
	public static String logHeader = "[PlayerManager] ";
	public static String Header = ChatColor.GOLD + "[PlayerManager] ";
	public static boolean doubleIP;
	public static String msg = "";
	//config file:
	public static String directory = "plugins" + File.separator + "PlayerManager";
	static File f_config = new File(directory + File.separator + "config.yml");
	static YamlConfiguration config;
}
