package com.github.Mephilis7.PlayerManager;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class VAR
{
	/* Copyright 2012 Mephilis7
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 * 
	 *     http://www.apache.org/licenses/LICENSE-2.0
	 *     
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
	
	//one class with the most important variables
	public static Permission permission = null;
	public static Economy economy = null;
	public static Chat chat = null;
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
	//chache:
	static File f_cache = new File("cache" + File.separator + "PlayerManager");
}
