package com.github.Mephilis7.PlayerManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Mephilis7.PlayerManager.VAR;

public class PMan_IPLogger
  implements Listener
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
	
	/* Well actually this isn't an IPLogger class anymore.
	 * It's used for updating the PlayerLog.yml, the BotBlocker
	 * and various events.
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		//whenever a player joins the server, do this...
		String[] playerip = event.getPlayer().getAddress().toString().split(":");
		if (playerip[0].startsWith("/"))
			playerip[0] = playerip[0].replaceFirst("/", "");
		Player p = event.getPlayer();
		
		File botLog = new File(VAR.directory + File.separator + "Bots.txt");
		
		//join messages
		if (VAR.config.getBoolean("customJQ")){
			VAR.msg = VAR.config.getString("joinMsg");
			VAR.msg = replace(VAR.msg, p);
			//support for Rei's Minimap
			String map = VAR.config.getString("supportReiMinimap");
			if (!map.toLowerCase().contains("false")){
				VAR.msg = minimap(map) + VAR.msg;
				p.sendMessage(""+ChatColor.BLACK+ChatColor.BLACK+ChatColor.DARK_GREEN+ChatColor.DARK_AQUA+ChatColor.DARK_RED+ChatColor.DARK_PURPLE+ChatColor.GOLD+ChatColor.GRAY+ChatColor.YELLOW+ChatColor.WHITE+ VAR.msg);
			} else {p.sendMessage(VAR.msg);}
			VAR.msg = VAR.config.getString("joinMsgOther");
			VAR.msg = replace(VAR.msg, p);
			event.setJoinMessage(VAR.msg);
		}
		
		//PlayerLog file
		try
		{
			loadPlayerLog();
			String path = "players."+p.getName();
			if (!VAR.pLog.isSet(path+".firstLogin"))
				VAR.pLog.set(path+".firstLogin", "["+getDate()+"]");
			if (!VAR.pLog.isSet(path+".lastLogin"))
				VAR.pLog.set(path+".lastLogin", Boolean.valueOf(null));
			if (!VAR.pLog.isSet(path+".lastLogout"))
				VAR.pLog.set(path+".lastLogout", Boolean.valueOf(null));
			if (!VAR.pLog.isSet(path+".Played Time"))
				VAR.pLog.set(path+".Played Time", "0 hours, 0 minutes or 0 minutes");
			if (!VAR.pLog.isSet(path+".Allowed to fly"))
				VAR.pLog.set(path+".Allowed to fly", Boolean.valueOf(Bukkit.getServer().getAllowFlight()));
			if (!VAR.pLog.isSet(path+".Displayed Name"))
				VAR.pLog.set(path+".Displayed Name", p.getDisplayName());
			if (!VAR.pLog.isSet(path+".Hidden"))
				VAR.pLog.set(path+".Hidden", Boolean.valueOf(false));
			if (!VAR.pLog.isSet(path+".Muted"))
				VAR.pLog.set(path+".Muted", Boolean.valueOf(false));
			if (!VAR.pLog.isSet(path+".Has accepted rules") && VAR.config.getBoolean("enableRules"))
				VAR.pLog.set(path+".Has accepted rules", Boolean.valueOf(false));
			if (!VAR.pLog.isSet("FakeOps")){
				List<String> list = new ArrayList<String>();
				String str = "PlayerManager";
				list = Arrays.asList(str);
				list.remove(str);
				VAR.pLog.set("FakeOps", list);
			}
				
			VAR.pLog.set(path+".lastLogin", "["+getDate()+"]");
			
			
				if (!VAR.pLog.isSet(path+".IP Address")){
					VAR.pLog.set(path+".IP Address", null);
					VAR.pLog.save(VAR.f_player);
					loadPlayerLog();
				}
				boolean there = false;
				if (VAR.pLog.getList(path+".IP Adress") != null){
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
				
			VAR.pLog.save(VAR.f_player);
			
			if (VAR.config.getBoolean("EnableReport")){
				if (!VAR.pLog.isSet("Reports."+p.getName()))
					VAR.pLog.set("Reports."+p.getName(), null);
			}
			if (!VAR.pLog.isSet("FakeOps"))
				VAR.pLog.set("FakeOps", null);
			
			//resetting the values, as defined in the config file
			
			if (VAR.config.getString("reset").toLowerCase().contains("fly")){
				p.setAllowFlight(Bukkit.getServer().getAllowFlight());
				VAR.pLog.set(path+".Allowed to fly", Boolean.valueOf(Bukkit.getServer().getAllowFlight()));
			} else {
				p.setAllowFlight(VAR.pLog.getBoolean(path+".Allowed to fly"));
			}
			if (VAR.config.getString("reset").toLowerCase().contains("name")){
				p.setDisplayName(p.getName());
				p.setPlayerListName(p.getName());
				VAR.pLog.set(path+".Displayed Name", p.getName());
			} else { 
				p.setDisplayName(VAR.pLog.getString(path+".Displayed Name"));
				p.setPlayerListName(VAR.pLog.getString(path+".Displayed Name"));
			}
			if (VAR.config.getString("reset").toLowerCase().contains("hidden")){
				for (Player p2: Bukkit.getServer().getOnlinePlayers()){
					p2.showPlayer(p);
				}
				VAR.pLog.set(path+".Hidden", Boolean.valueOf(false));
			} else if (VAR.pLog.getBoolean(path+".Hidden")){
				for (Player p2: Bukkit.getServer().getOnlinePlayers()){
					p2.hidePlayer(p);
				}
			} else {
				for (Player p2: Bukkit.getServer().getOnlinePlayers()){
					p2.showPlayer(p); 
				}
			}
			if (VAR.config.getString("reset").toLowerCase().contains("muted"))
				VAR.pLog.set(path+".Muted", Boolean.valueOf(false));
				
			VAR.pLog.save(VAR.f_player);
		} catch (IOException ex){
			ex.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		if ((p.hasPermission("pman.update") || p.isOp()) && VAR.config.getBoolean("CheckUpdate")){
			checkVersion(p);
		}
		//BotBlocker
		if (VAR.config.getBoolean("enableBotBlock")){
			for(Player online: Bukkit.getServer().getOnlinePlayers()){
				String[] onlineip = online.getAddress().toString().split(":");
				if ((onlineip[0].equalsIgnoreCase("/"+playerip[0])) && (!onlineip[1].equalsIgnoreCase(playerip[1]))){
					VAR.log.info(VAR.logHeader +ChatColor.RED+"Found duplicated ip: "+p.getName()+" and "+online.getName());
					VAR.doubleIP = true;
				}
			}
			if (VAR.doubleIP){
				if (VAR.config.getString("punishment").equalsIgnoreCase("kick")){
					p.kickPlayer("You have been kicked for using a bot. Or being one.");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED +p.getName()+" has been KICKED for logging in");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED+"with a duplicated IP address! (Bot?)");
					if (VAR.logit)
						VAR.log.info(VAR.logHeader +p.getName()+" has been kicked (duplicated ip)");
				}
				if (VAR.config.getString("punishment").equalsIgnoreCase("ban")){
					p.kickPlayer("You have been banned for using a bot. Or being one.");
					Bukkit.banIP(playerip[0]);
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED +p.getName()+" has been BANNED for logging in");
					Bukkit.getServer().broadcastMessage(VAR.Header+ ChatColor.RED+"with a duplicated IP address! (Bot?)");
					if (VAR.logit)
						VAR.log.info(VAR.logHeader +p.getName()+" has been banned (duplicated ip)");
				}
				if (VAR.config.getBoolean("logDuplicatedIps")){
					try {
						if (!botLog.exists()){
							botLog.createNewFile();
							VAR.log.info(VAR.logHeader + "Creating BotLog file.");
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(botLog, true));
						writer.write("["+getDate()+"] "+p.getName()+" - '"+playerip[0]+"'");
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
		// Update the amount of time a player has played on the server.
		try
		{
			loadPlayerLog();
			String path = "players."+player.getName();
			VAR.pLog.set(path+".lastLogout", "["+getDate()+"]");
			
			String logTime = VAR.pLog.getString(path+".Played Time").split(" or ")[1];
			logTime = logTime.replace(" minutes", "").trim();
			int mins = Integer.valueOf(logTime);
			// Update hours
			int now = Integer.valueOf(getDate().split(" ")[1].split(":")[0]);
			int login = Integer.valueOf(VAR.pLog.getString(path+".lastLogin").split(" ")[1].split(":")[0]);
			if ((now - login) > 0){
				mins = mins + ((now-login)*60);
			}
			if ((now - login) < 0){
				mins = mins + (((24-login) + now)*60);
			}
			// Update minutes
			now = Integer.valueOf(getDate().split(":")[1].split("\\.")[0]);
			login = Integer.valueOf(VAR.pLog.getString(path+".lastLogin").split(":")[1].split("\\.")[0]);
			if ((now - login) > 0){
				mins = mins + (now-login);
			}
			if ((now - login) < 0){
				mins = mins + ((60-login) + now);
			}
			logTime = "";
			now = 0;
			login = 0;
			while ((now + 60) < mins){
				now = now + 60;
				login++;
			}
			now = mins - now;
			logTime = login + " hours and " + now + " minutes or " + mins + " minutes";
			VAR.pLog.set(path+".Played Time", logTime);
			
			VAR.pLog.save(VAR.f_player);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return;
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		//Check whether the player has been muted.
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
			event.getPlayer().sendMessage(msg);
			event.setCancelled(true);
		}
		if (VAR.config.getBoolean("EnableCensor")){
			String msg = event.getMessage();
			
			String s = VAR.config.getList("CensorWords").toString();
			s = s.replace("[", "").replace("]", "").replace(" ", "");
			String[] CW = s.split(",");
			String[] MSG = msg.trim().split(" ");
			int i = 0;
			//look out for every single word defined in the config.yml
			while (i < CW.length){
				if (msg.toLowerCase().contains(CW[i].toLowerCase())){
					int a = 0;
					String replace = "";
					while (a < MSG.length){
						if (MSG[a].equalsIgnoreCase(CW[i])){
							int b = 0;
							while (b < MSG[a].length()){
								replace = replace + VAR.config.getString("CensorChar");
								b++;
							}
							MSG[a] = replace;
						}
						a++;
					}
				}
				i++;
			}
			i = 0;
			msg = "";
			while (i < MSG.length){
				msg = msg + " " + MSG[i];
				i++;
			}
			event.setMessage(msg.trim());
		}
		// FakeOP part
		if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("mute")){
			Player p = event.getPlayer();
			if (VAR.pLog.getList("FakeOps").toString().contains(p.getName())){
				event.setCancelled(true);
				String prefix = "";
				int i = 0;
				String[] pre = VAR.config.getString("fakeOpAnnoy").split(";");
				while (i < pre.length){
					if (pre[i].toLowerCase().contains("mute")){
						prefix = pre[i].split("\\{")[1].split("\\}")[0].replace("'", "");
						break;
					}
					i++;
				}
				prefix = replace(prefix, p);
				p.sendMessage(prefix + event.getMessage());
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		//FakeOP
		if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("gohelp")){
			if (VAR.pLog.getList("FakeOps").toString().contains(event.getPlayer().getName())){
				String msg = VAR.config.getString("fakeOpAnnoy");
				String[] m = msg.split(";");
				int i = 0;
				while(i < m.length){
					if (m[i].toLowerCase().contains("gohelp")){
						msg = m[i].split("\\[")[1].split("\\]")[0].replace("'", "");
						break;
					}
					i++;
				}
				msg = replace(msg, event.getPlayer());
				event.setCancelled(true);
				event.getPlayer().sendMessage(msg);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onCommandPrepare(PlayerCommandPreprocessEvent event){
		//FakeOP
		if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("dontknow")){
			if (VAR.pLog.getList("FakeOps").toString().contains(event.getPlayer().getName())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.WHITE + "Unknown command. Type \"help\" for help.");
			}
		}
		if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("broadcast")){
			if (VAR.pLog.getList("FakeOps").toString().contains(event.getPlayer().getName())){
				if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("[whitelist]")){
					String[] list = VAR.config.getList("fakeList").toString().toLowerCase().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("/", "").split(",");
					int i = 0;
					boolean onList = false;
					while(i < list.length){
						if (event.getMessage().toLowerCase().equalsIgnoreCase("/" + list[i])){
							onList = true;
						}
						i++;
					}
					if (!onList){
						event.setCancelled(true);
						broadcastCommand(event.getMessage(), event.getPlayer());
					}
					return;
				}
				if (VAR.config.getString("fakeOpAnnoy").toLowerCase().contains("[blacklist]")){
					String[] list = VAR.config.getList("fakeList").toString().toLowerCase().replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("/", "").split(",");
					int i = 0;
					boolean onList = false;
					while(i < list.length){
						if (event.getMessage().toLowerCase().equalsIgnoreCase("/" + list[i])){
							onList = true;
						}
						i++;
					}
					if (onList){
						event.setCancelled(true);
						broadcastCommand(event.getMessage(), event.getPlayer());
					}
					return;
				}
				event.setCancelled(true);
				broadcastCommand(event.getMessage(), event.getPlayer());
			}
		}
	}
	public void broadcastCommand(String str, Player fake){
		for(Player p: Bukkit.getServer().getOnlinePlayers()){
			if (!p.getName().equalsIgnoreCase(fake.getName())){
				p.sendMessage(ChatColor.RED + fake.getName() + " tried to execute the command "+str+"!");
				p.sendMessage(ChatColor.RED + "It was NOT executed. He's not trustworthy.");
			}
		}
	}
	
	public void loadPlayerLog() throws Exception{
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
	
	//Replace the variables in the string
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
		str = str.replace("%SERVERNAME", Bukkit.getServer().getName());
		//Code below is taken from MCDocs
		String[] Colours = { "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
			      "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
		ChatColor[] cCode = { ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, 
			      ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
		for (int i=0; i<Colours.length; i++){
			if (str.contains(Colours[i]))
				str = str.replace(Colours[i], cCode[i].toString());
		}
		//End of MCDocs code
		return str;
	}
	
	//Support for Rei's minimap
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
	//Check whether the plugin is upToDate.
	public void checkVersion(Player p){
		try{
			URL url = new URL("http://dev.bukkit.org/server-mods/playermanager/files");
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine = "";
			
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("col-file\"")){
					String version = inputLine.split("PlayerManager ")[1].split("<")[0];
					String thisVersion = 
							Bukkit.getServer().getPluginManager().getPlugin("PlayerManager").getDescription().getVersion();
					if (!version.equalsIgnoreCase("v"+thisVersion)){
						VAR.log.info("");
						VAR.log.info("------------- Found an update for PlayerManager -------------");
						VAR.log.info("Please visit http://dev.bukkit.org/server-mods/playermanager/");
						VAR.log.info("and download "+version+". You are running v"+thisVersion+".");
						VAR.log.info("-------------------------------------------------------------");
						VAR.log.info("");
						p.sendMessage(ChatColor.AQUA+"An update for "+ChatColor.GOLD+"PlayerManager"+ChatColor.AQUA+" is out! Please visit");
						p.sendMessage(ChatColor.AQUA+"http://dev.bukkit.org/server-mods/playermanager/");
						return;
					}
					break;
				}
			}
			VAR.log.info("PlayerManager is UpToDate (v"+Bukkit.getServer().getPluginManager().getPlugin("PlayerManager").getDescription().getVersion()+").");
			
		} catch (IOException ex){
			VAR.log.info(VAR.logHeader+"Error while looking for updates.");
		}
	}
}

