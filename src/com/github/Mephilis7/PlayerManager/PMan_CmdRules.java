package com.github.Mephilis7.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PMan_CmdRules implements CommandExecutor{
	
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
	
	private PMan_main plugin;
	public PMan_CmdRules(PMan_main plugin){
		this.plugin = plugin;
	}
	boolean console = false;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		console = false;
		if (sender.getName().equalsIgnoreCase("console")){
			console = true;
		}
		//Shows the rules
		if (cmd.getName().equalsIgnoreCase("rules")){
			if (sender.hasPermission("pman.rules")){
				if (args.length >= 1){
					sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
					sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/rules");
					sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
					sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.rules");
					sender.sendMessage(ChatColor.AQUA +"Shows the server rules.");
					return true;
				}
				int i = 1;
				String msg = "";
				//While there's another rules message set in the config.yml, replace the color codes and send the result to sender.
				while (VAR.config.isSet("Rules"+i)){
					msg = VAR.config.getString("Rules"+i);
					msg = replaceRules(msg);
					sender.sendMessage(msg);
					i++;
				}
				//Set the player's rules state from "has not read" to "has read, may accept the rules"
				if (!console){
					if (VAR.pLog.getString("players."+sender.getName()+".Has accepted rules").equalsIgnoreCase("false")){
						try{
							VAR.pLog.set("players."+sender.getName()+".Has accepted rules", "hasTyped");
							VAR.pLog.save(VAR.f_player);
							VAR.pLog = YamlConfiguration.loadConfiguration(VAR.f_player);
						} catch (Exception ex){
							ex.printStackTrace();
						}
					}
				}
			} else this.plugin.denied(sender);
		}
		//Command to accept the server rules
		if (cmd.getName().equalsIgnoreCase("acceptrules")){
			if (sender.hasPermission("pman.rules")){
				if (args.length >= 1){
					sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
					sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/acceptrules");
					sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
					sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.rules");
					sender.sendMessage(ChatColor.AQUA +"Confirm that you've read the server rules and agree with them.");
					return true;
				}
				if (!console){
					//If the player has not read the rules yet.
					if (VAR.pLog.getString("players."+sender.getName()+".Has accepted rules").equalsIgnoreCase("false")){
						sender.sendMessage(ChatColor.RED + "You have to read the rules first!! Please type "+ChatColor.DARK_GREEN+"/rules.");
						return true;
					}
					if (VAR.pLog.getString("players."+sender.getName()+".Has accepted rules").equalsIgnoreCase("hasTyped")){
						try {
							VAR.pLog.set("players."+sender.getName()+".Has accepted rules", Boolean.valueOf(true));
							VAR.pLog.save(VAR.f_player);
							VAR.pLog = YamlConfiguration.loadConfiguration(VAR.f_player);
						} catch (Exception ex){
							ex.printStackTrace();
						}
						if (VAR.logit)
							VAR.log.info(VAR.logHeader + sender.getName() + " has accepted the rules!");
						//Teleport the player to the specified location
						if (VAR.config.getBoolean("RulesTeleport")){
							Player p = Bukkit.getServer().getPlayer(sender.getName());
							World world = Bukkit.getServer().getWorld(VAR.config.getString("RulesTpWorld"));
							double x = VAR.config.getDouble("RulesTpX");
							double y = VAR.config.getDouble("RulesTpY");
							double z = VAR.config.getDouble("RulesTpZ");
							float pitch = Float.valueOf(VAR.config.getString("RulesTpPitch"));
							float yaw = Float.valueOf(VAR.config.getString("RulesTpYaw"));
							Location loc = new Location(world, x, y, z, yaw, pitch);
							p.teleport(loc);
						}
						//Execute the commands specified in the config.yml
						String AcceptCmd = VAR.config.getString("RulesExCmd");
						AcceptCmd = replaceAccept(AcceptCmd, Bukkit.getServer().getPlayer(sender.getName()));
						String[] ExCmd = AcceptCmd.split("\\|");
						int i = 0;
						while (i < ExCmd.length){
							Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ExCmd[i]);
							i++;
						}
						sender.sendMessage(ChatColor.GREEN+"Thank you for accepting the server rules.");
					} else sender.sendMessage(VAR.Header + ChatColor.BLUE + "You've already accepted the rules. Thanks anyway.");
				} else sender.sendMessage(VAR.Header+ChatColor.YELLOW + "Sorry, you have to be a player to execute this command.");
			} else this.plugin.denied(sender);
		}
		return true;
	}
	public String replaceAccept(String str, Player p){
		str = str.replace("%NAME", p.getName());
		str = str.replace("%IP", p.getAddress().toString());
		str = str.replace("%WORLD", p.getWorld().getName());
		str = str.replace("%GAMEMODE", p.getGameMode().name());
		str = replaceCcode(str);
		return str;
	}
	public String replaceRules(String str){			
		str = str.replace("%IP", Bukkit.getServer().getIp());
		str = replaceCcode(str);
		return str;
	}
	public String replaceCcode(String str){
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
			      "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&bold", "&italic", "&strike", "&under", "&magic", "&reset"};
		ChatColor[] cCode = { ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, 
			      ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE,
			      ChatColor.BOLD, ChatColor.ITALIC, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.MAGIC, ChatColor.RESET};
		for (int i=0; i<Colours.length; i++){
			if (str.contains(Colours[i]))
				str = str.replace(Colours[i], cCode[i].toString());
		}
		//End of MCDocs code
		return str;
	}
}
