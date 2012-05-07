package com.github.Mephilis7.PlayerManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Mephilis7.PlayerManager.VAR;

/*TODO
 * - Make the IPLog a nice file where the data only gets updatet, not newly written... 
 * Example:
 * 
 * Mephilis7:
 *     lastLogin: [18/4/2012 14:12:47]
 *     lastLogout: [26/4/2012 23:56:29]
 *     IPs: 
 *         - /127.0.0.1
 *         - /123.12.3.123
 */

public class PMan_IPLogger
  implements Listener
{
	private FileConfiguration log = null;
	private File logFile = new File(VAR.directory + File.separator + "PlayerLog.txt");
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		//whenever a player joins the server, do this...
		String playerip = event.getPlayer().getAddress().toString();
		String player = event.getPlayer().getName();
		
		File botLog = new File(VAR.directory + File.separator + "Duplicated IP's.txt");
		
		//join messages
		VAR.msg = VAR.config.getString("joinMsg");
		VAR.msg = replace(VAR.msg, event.getPlayer());
		//support for Rei's Minimap
		String map = VAR.config.getString("supportReiMinimap");
		if (!map.toLowerCase().contains("false")){
			VAR.msg = minimap(map) + VAR.msg;
			event.getPlayer().sendMessage(""+ChatColor.BLACK+ChatColor.BLACK+ChatColor.DARK_GREEN+ChatColor.DARK_AQUA+ChatColor.DARK_RED+ChatColor.DARK_PURPLE+ChatColor.GOLD+ChatColor.GRAY+ChatColor.YELLOW+ChatColor.WHITE+ VAR.msg);
		} else {event.getPlayer().sendMessage(VAR.msg);}
		VAR.msg = VAR.config.getString("joinMsgOther");
		VAR.msg = replace(VAR.msg, event.getPlayer());
		event.setJoinMessage(VAR.msg);
		
		//IP Logger
		if (VAR.config.getBoolean("LogIP")){
			try {
				//log the player's IP address to plugins/PlayerManager/PlayerLogs.txt
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write("["+getDate()+"] "+player+" - '"+playerip+"'");
				writer.newLine();
				writer.flush();
				writer.close();
				} catch (IOException e){
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
		}
		//BotBlocker
		String playerIp[] = playerip.split(":");
		if (VAR.config.getBoolean("enableBotBlock")){
				for(Player online: Bukkit.getServer().getOnlinePlayers()){
				String[] onlineip = online.getAddress().toString().split(":");
				if ((onlineip[0].equalsIgnoreCase(playerIp[0])) && (!onlineip[1].equalsIgnoreCase(playerIp[1]))){
					VAR.log.info(VAR.logHeader +"Found duplicated ip: "+player+" and "+online.getName());
					VAR.doubleIP = true;
				}
			}
			if (VAR.doubleIP){
				if (VAR.config.getString("punishment").equalsIgnoreCase("kick")){
					event.getPlayer().kickPlayer("You have been kicked for using a bot. Or being one.");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED +player+" has been KICKED for logging in");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED+"with a duplicated IP address! (Bot?)");
					VAR.log.info(VAR.logHeader +player+" has been kicked (duplicated ip)");
				}
				if (VAR.config.getString("punishment").equalsIgnoreCase("ban")){
					event.getPlayer().kickPlayer("You have been banned for using a bot. Or being one.");
					Bukkit.banIP(playerIp[0]);
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED +player+" has been BANNED for logging in");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED+"with a duplicated IP address! (Bot?)");
					VAR.log.info(VAR.logHeader +player+" has been banned (duplicated ip)");
				}
				if (VAR.config.getBoolean("logDuplicatedIps")){
					try {
						if (!botLog.exists()){
							botLog.createNewFile();
							VAR.log.info(VAR.logHeader + "Creating IPLog file.");
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(botLog, true));
						writer.write("["+getDate()+"] "+player+" - '"+playerIp[0]+"'");
						writer.newLine();
						writer.flush();
						writer.close();
						} catch (IOException e){
							e.printStackTrace();
						}
				}
			} else { return;}
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if (VAR.config.getBoolean("customJQ")){
			VAR.msg = VAR.config.getString("quitMsg");
			VAR.msg = replace(VAR.msg, event.getPlayer());
			event.setQuitMessage(VAR.msg);
		}
		return;
	}
	public void loadPlayerLog() throws Exception{
		if (!logFile.exists()){
			logFile.createNewFile();
			VAR.log.info(VAR.logHeader + "Creating PlayerLog file.");
		}
		this.log = YamlConfiguration.loadConfiguration(this.logFile);
		this.log.options().header("PlayerLogs");
		this.log.addDefault("players", null);
		this.log.save(this.logFile);
	}
	public static String getDate(){
	    Calendar c = Calendar.getInstance();
	    int month = c.get(2) + 1;
	    String date = Integer.toString(month);
	    date = date + "/";
	    date = date + c.get(5) + "/";
	    date = date + c.get(1) + " ";
	    date = date + c.get(11) + ":";
	    date = date + c.get(12) + ".";
	    date = date + c.get(13);
	    return date;
	}
	public String replace(String str, Player player){
		str = str.replace("%NAME", player.getName());
		str = str.replace("%IP", player.getAddress().toString());
		str = str.replace("%WORLD", player.getWorld().getName());
		str = str.replace("%GAMEMODE", player.getGameMode().toString());
		str = str.replace("%ONLINEPLAYERS", Integer.toString(Bukkit.getServer().getOnlinePlayers().length));
		str = str.replace("%MAXPLAYERS", Integer.toString(Bukkit.getServer().getMaxPlayers()));
		String s = "";
		for (Player p: Bukkit.getServer().getOnlinePlayers()){
			s = s + p.getDisplayName()+ ", ";
		}
		str = str.replace("%ONLINELIST", s);
		String[] Colours = { "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
			      "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
		ChatColor[] cCode = { ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, 
			      ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
		for (int i=0; i<Colours.length; i++){
			if (str.contains(Colours[i]))
				str = str.replace(Colours[i], cCode[i].toString());
		}
		return str;
	}
	public String minimap(String str){
		str = str.toLowerCase();
		String result = (ChatColor.BLACK.toString()+ChatColor.BLACK.toString());
		if (str.contains("cave"))
			result = result + ChatColor.DARK_BLUE.toString();
		if (str.contains("player"))
			result = result + ChatColor.DARK_GREEN.toString();
		if (str.contains("animal"))
			result = result + ChatColor.DARK_AQUA.toString();
		if (str.contains("mob"))
			result = result + ChatColor.DARK_RED.toString();
		if (str.contains("slime"))
			result = result + ChatColor.DARK_PURPLE.toString();
		if (str.contains("squid"))
			result = result + ChatColor.GOLD.toString();
		if (str.contains("other"))
			result = result + ChatColor.GRAY.toString();
		result = result + ChatColor.YELLOW.toString() + ChatColor.WHITE.toString();
		return result;
	}
}

