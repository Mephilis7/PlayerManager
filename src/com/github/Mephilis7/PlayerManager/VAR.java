package com.github.Mephilis7.PlayerManager;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class VAR
{
	//one class with the most important variables
	public static Permission permission = null;
	public static Economy economy = null;
	public static Logger log = Logger.getLogger("Minecraft");
	
	public static String logHeader = "[PlayerManager] ";
	public static String Header = ChatColor.GOLD + "[PlayerManager] ";
	public static boolean doubleIP;
	public static String msg = "";
	public static boolean logit = false;
	//config file:
	public static String directory = "plugins" + File.separator + "PlayerManager";
	static File f_config = new File(directory + File.separator + "config.yml");
	static YamlConfiguration config;
	//player file:
	static File f_player = new File(directory + File.separator + "PlayerLog.yml");
	static YamlConfiguration pLog;
}
