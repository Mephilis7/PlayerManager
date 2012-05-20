package com.github.Mephilis7.PlayerManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Mephilis7.PlayerManager.VAR;

public class PMan_IPLogger
  implements Listener
{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		//whenever a player joins the server, do this...
		String[] playerip = event.getPlayer().getAddress().toString().split(":");
		if (playerip[0].startsWith("/"))
			playerip[0] = playerip[0].replaceFirst("/", "");
		String player = event.getPlayer().getName();
		
		File botLog = new File(VAR.directory + File.separator + "Duplicated IP's.txt");
		
		//join messages
		if (VAR.config.getBoolean("customJQ")){
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
		}
		
		//PlayerLog file
		try
		{
			loadPlayerLog();
			String path = "players."+player;
			if (!VAR.pLog.isSet(path+".lastLogin"))
				VAR.pLog.set(path+".lastLogin", Boolean.valueOf(null));
			if (!VAR.pLog.isSet(path+".lastLogout"))
				VAR.pLog.set(path+".lastLogout", Boolean.valueOf(null));
			if (!VAR.pLog.isSet(path+".Allowed to fly"))
				VAR.pLog.set(path+".Allowed to fly", Boolean.valueOf(Bukkit.getServer().getAllowFlight()));
			if (!VAR.pLog.isSet(path+".Displayed Name"))
				VAR.pLog.set(path+".Displayed Name", Bukkit.getServer().getPlayer(player).getDisplayName());
			if (!VAR.pLog.isSet(path+".Hidden"))
				VAR.pLog.set(path+".Hidden", Boolean.valueOf(false));
			if (!VAR.pLog.isSet(path+".Muted"))
				VAR.pLog.set(path+".Muted", Boolean.valueOf(false));
			if (!VAR.pLog.isSet(path+".Has accepted rules") && VAR.config.getBoolean("enableRules"))
				VAR.pLog.set(path+".Has accepted rules", Boolean.valueOf(false));
				
			VAR.pLog.set(path+".lastLogin", "["+getDate()+"]");
			
			
			if (VAR.config.getBoolean("LogIP")){
				if (!VAR.pLog.isSet(path+".IP Address")){
					VAR.pLog.set(path+".IP Address", null);
					VAR.pLog.save(VAR.f_player);
					loadPlayerLog();
				}
				boolean there = false;
				if (VAR.pLog.getList(path+".IP Adress".isEmpty()) != null){
					String s = VAR.pLog.getList(path+".IP Address").toString();
					s = s.replace("[", "");
					s = s.replace("]", "");
					s = s.replace(" ", "");
					for (String str: s.split(",")){
						if (str.equalsIgnoreCase(playerip[0]))
							there = true;
					}
					String[] str = s.split(",");
					if (!there){
						String[] st = new String[str.length+1];
						int i = 0;
						while (i < str.length){
							st[i] = str[i];
							i++;
						}
						st[str.length] = playerip[0].toString();
						VAR.pLog.set(path+".IP Address", Arrays.asList(st));
					}
				} else {
					String[] st = {playerip[0]};
					VAR.pLog.set(path+".IP Address", Arrays.asList(st));	
				}
			}
			VAR.pLog.save(VAR.f_player);
			
			//resetting the values, as defined in the config file
			
			if (VAR.config.getString("reset").toLowerCase().contains("fly")){
				Bukkit.getServer().getPlayer(player).setAllowFlight(Bukkit.getServer().getAllowFlight());
				VAR.pLog.set(path+".Allowed to fly", Boolean.valueOf(Bukkit.getServer().getAllowFlight()));
			} else {
				Bukkit.getServer().getPlayer(player).setAllowFlight(VAR.pLog.getBoolean(path+".Allowed to fly"));
			}
			if (VAR.config.getString("reset").toLowerCase().contains("name")){
				Bukkit.getServer().getPlayer(player).setDisplayName(player);
				Bukkit.getServer().getPlayer(player).setPlayerListName(player);
				VAR.pLog.set(path+".Displayed Name", player);
			} else { 
				Bukkit.getServer().getPlayer(player).setDisplayName(VAR.pLog.getString(path+".Displayed Name"));
				Bukkit.getServer().getPlayer(player).setPlayerListName(VAR.pLog.getString(path+".Displayed Name"));
			}
			if (VAR.config.getString("reset").toLowerCase().contains("hidden")){
				Bukkit.getServer().getPlayer(player).showPlayer(event.getPlayer());
				VAR.pLog.set(path+".Hidden", Boolean.valueOf(false));
			} else if (VAR.pLog.getBoolean(path+".Hidden")){
				Bukkit.getServer().getPlayer(player).hidePlayer(event.getPlayer());
			} else { Bukkit.getServer().getPlayer(player).showPlayer(event.getPlayer()); }
			if (VAR.config.getString("reset").toLowerCase().contains("muted"))
				VAR.pLog.set(path+".Muted", Boolean.valueOf(false));
				
				
			VAR.pLog.save(VAR.f_player);
		} catch (IOException ex){
			ex.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		//BotBlocker
		if (VAR.config.getBoolean("enableBotBlock")){
				for(Player online: Bukkit.getServer().getOnlinePlayers()){
				String[] onlineip = online.getAddress().toString().split(":");
				if ((onlineip[0].equalsIgnoreCase(playerip[0])) && (!onlineip[1].equalsIgnoreCase(playerip[1]))){
					VAR.log.info(VAR.logHeader +ChatColor.RED+"Found duplicated ip: "+player+" and "+online.getName());
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
					Bukkit.banIP(playerip[0]);
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
						writer.write("["+getDate()+"] "+player+" - '"+playerip[0]+"'");
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
		Player player = event.getPlayer();
		if (VAR.config.getBoolean("customJQ")){
			VAR.msg = VAR.config.getString("quitMsg");
			VAR.msg = replace(VAR.msg, event.getPlayer());
			event.setQuitMessage(VAR.msg);
		}
		try
		{
			loadPlayerLog();
			VAR.pLog.set("players."+player.getName()+".lastLogout", "["+getDate()+"]");
			VAR.pLog.save(VAR.f_player);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return;
	}
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event){
		Boolean muted = null;
		try {
			loadPlayerLog();
			muted = VAR.pLog.getBoolean("players."+event.getPlayer().getName()+".Muted");
		} catch (Exception ex){
			ex.printStackTrace();
			return;
		}
		if (muted){
			String msg = VAR.config.getString("mutedMsg");
			msg = replace(msg, event.getPlayer());
			event.getPlayer().sendMessage(VAR.config.getString(msg));
			event.setCancelled(true);
		}
	}
	private void loadPlayerLog() throws Exception{
		if (!VAR.f_player.exists()){
			VAR.f_player.createNewFile();
			VAR.log.info(VAR.logHeader + "Creating PlayerLog file.");
		}
		VAR.pLog = YamlConfiguration.loadConfiguration(VAR.f_player);
		VAR.pLog.options().header("PlayerLogs");
		VAR.pLog.addDefault("players", null);
		VAR.pLog.save(VAR.f_player);
	}
	public static String getDate(){
	    Calendar c = Calendar.getInstance();
	    int month = c.get(2) + 1;
	    String date = Integer.toString(c.get(5));
	    date = date + "/";
	    date = date + month + "/";
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
		str = str.replace("%SERVERNAME", Bukkit.getServer().getName());
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

