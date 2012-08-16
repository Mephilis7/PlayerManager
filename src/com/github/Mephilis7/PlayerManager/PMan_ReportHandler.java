package com.github.Mephilis7.PlayerManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PMan_ReportHandler implements CommandExecutor{
	
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
	public PMan_ReportHandler(PMan_main plugin){
		this.plugin = plugin;
	}
	
	//This class is responsible for the commands concerning the report stuff.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		// Defining the command /report
		if (cmd.getName().equalsIgnoreCase("report")){
			if (sender.hasPermission("pman.report") || sender.isOp()){
				if (args.length == 1 && args[0].equalsIgnoreCase("?")){
					sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
					sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/report <player> <reason>");
					sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
					sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.report");
					sender.sendMessage(ChatColor.AQUA +"Report a player to the Admins/Mods.");
					sender.sendMessage(ChatColor.AQUA +"A reason is REQUIRED. Cooldown is "+VAR.config.getString("ReportCoolDown")+" seconds.");
					return true;
				}
				if (args.length > 1){
					//Cache file. Allows me to set the cooldown for the command.
					try{
						if (!VAR.f_cache.exists()){
							BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.f_cache, true));
							VAR.f_cache.createNewFile();
							writer.write("CreationTime "+getSeconds(getDate())+"\n");
							writer.close();
						}
						String input = "";
						String in = getSeconds(getDate()).toString();
						String header = "";
						Scanner scan = new Scanner(VAR.f_cache);
						while (scan.hasNextLine()){
							input = scan.nextLine();
							if (input.contains("CreationTime")){
								header = input.split(" ")[1];
								in = getSeconds(getDate()).toString();
							} else
								in = input.split(" ")[1];
							if (input.contains(sender.getName())){
								input = in;
								if ((getSeconds(getDate()) - Integer.parseInt(input)) < VAR.config.getInt("ReportCoolDown")){
									String msg = VAR.config.getString("ReportCoolDownMsg");
									msg = msg.replace("%NAME", sender.getName());
									msg = replaceColours(msg);
									sender.sendMessage(msg);
									return true;
								}
							}
						}
						scan.close();
						//Recreate the cache file if enough time is over.
						//I did this to keep the cache file small and fast, even on large servers.
						if ((getSeconds(getDate()) - Integer.parseInt(in)) > VAR.config.getInt("ReportCoolDown")
								&& (getSeconds(getDate()) - Integer.parseInt(header)) > 1800){
							BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.f_cache, true));
							VAR.f_cache.delete();
							VAR.f_cache.createNewFile();
							writer.write("CreationTime "+getSeconds(getDate())+"\n");
							writer.close();
						}
					} catch (Exception ex){
						ex.printStackTrace();
					}
					//Check whether the player exists
					Player p = this.plugin.checkPlayer(args[0]);
					if (p == null){
						sender.sendMessage(VAR.Header+ChatColor.RED+"Could not find the player you specified.");
						return true;
					}
					if (p.getName().equalsIgnoreCase(sender.getName())){
						sender.sendMessage(ChatColor.GRAY+"You can't report yourself...");
						return true;
					}
					//This little piece of code stores the reason for typing /report in a variable, even if it's more than one word.
					String reason = "";
					int a = 1;
					while (a < args.length){
						reason = reason +" "+ args[a];
						a++;
					}
					reason = reason.replaceFirst(" ", "");
					//Store the information in the PlayerLog.yml file.
					int i = 1;
					try {
						while (VAR.pLog.isSet("Reports."+p.getName()+".Number "+i)){
							i++;
						}
						String path = "Reports."+p.getName()+".Number "+i;
						VAR.pLog.set(path+".Reported by", sender.getName());
						VAR.pLog.set(path+".Date and Time", getDate().replace(" ", "").replace(",", " "));
						VAR.pLog.set(path+".Reason", reason);
						VAR.pLog.set(path+"."+p.getName()+" was at Location.World", p.getLocation().getWorld().getName());
						VAR.pLog.set(path+"."+p.getName()+" was at Location.X", p.getLocation().getBlockX());
						VAR.pLog.set(path+"."+p.getName()+" was at Location.Y", p.getLocation().getBlockY());
						VAR.pLog.set(path+"."+p.getName()+" was at Location.Z", p.getLocation().getBlockZ());
						if (sender instanceof Player){
							VAR.pLog.set(path+".Distance from reporter to "+p.getName(), calcDistance(Bukkit.getServer().getPlayer(sender.getName()), p));
						} else {
							VAR.pLog.set(path+".Distance from reporter to "+p.getName(), "Infinite. The player was reported by CONSOLE.");
						}
						//Write the name to the cache file, to prevent spamming the command.
							BufferedWriter writer = new BufferedWriter(new FileWriter(VAR.f_cache, true));
							writer.write(sender.getName()+" "+getSeconds(getDate())+"\n");
							writer.flush();
							writer.close();
						
						VAR.pLog.save(VAR.f_player);
						this.plugin.loadPlayerLog();
					} catch (Exception ex){
						ex.printStackTrace();
					}
					sender.sendMessage(VAR.Header+ChatColor.GRAY+"Successfully reported "+ChatColor.RED+p.getName()+ChatColor.GRAY+" for \""+reason+"\"");
					VAR.log.warning(VAR.logHeader + sender.getName()+" has reported "+p.getName()+" because \""+ reason+"\"");
					//Notify all players with pman.check permission that someone has been reported.
					for (Player p2: Bukkit.getServer().getOnlinePlayers()){
						if (p2.hasPermission("pman.check") || p2.isOp()){
							p2.sendMessage(VAR.Header +ChatColor.RED+ sender.getName() + " has reported "+p.getName()+"!!");
							p2.sendMessage(ChatColor.RED+"Type "+ChatColor.YELLOW+"/check "+p.getName()+" "+i+ChatColor.RED+" to see why and where.");
							p2.sendMessage(ChatColor.RED+"Type "+ChatColor.YELLOW+"/checktp "+p.getName()+" "+i+ChatColor.RED+" to teleport there.");
						}
					}
					//Punish the player if he has been reported too often.
					if (i >= VAR.config.getInt("ReportBan")){
						String method = VAR.config.getString("ReportBanMethod");
						String msg = VAR.config.getString("ReportBanMsg");
						String[] IP = p.getAddress().toString().split(":");
						p.kickPlayer(msg);
						if (method.equalsIgnoreCase("name") || method.equalsIgnoreCase("both"))
							p.setBanned(true);
						if (method.equalsIgnoreCase("ip") || method.equalsIgnoreCase("both")){
							IP[0] = IP[0].replace("/", "");
							Bukkit.getServer().banIP(IP[0]);
						}
					}
					if (i == VAR.config.getInt("ReportKick")){
						String msg = VAR.config.getString("ReportKickMsg");
						p.kickPlayer(msg);
					}
					//Execute the commands specified in the config.yml if the player has been reported too often.
					if (i == VAR.config.getInt("ReportCmd")){
						String cCmd = VAR.config.getString("ReportEXCmd").trim();
						cCmd = replace(cCmd, p);
						String[] ExCmd = cCmd.split("|");
						int r = 0;
						while (r < ExCmd.length){
							Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ExCmd[r]);
							r++;
						}
					}
				} else {
					Bukkit.getServer().dispatchCommand(sender,"report ?");
				}
			} else {this.plugin.denied(sender);}
		}
		//Defining the command /check
		if (cmd.getName().equalsIgnoreCase("check")){
			if (sender.hasPermission("pman.check") || sender.isOp()){
				if (args.length == 1 || args.length == 2){
					if (args[0].equalsIgnoreCase("?")){
						sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
						sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/check <player> [Report Number]");
						sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
						sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.check");
						sender.sendMessage(ChatColor.AQUA +"Check the report status of a player");
						sender.sendMessage(ChatColor.AQUA +"or one of his reports.");
						return true;
					}
					//Again, checking whether the player is online.
					Player p = this.plugin.checkPlayer(args[0]);
					if (p == null){
						sender.sendMessage(ChatColor.RED+"Could not find the specified player.");
						return true;
					}
					String path = "Reports."+p.getName();
					int i = 1;
					while (VAR.pLog.isSet(path+".Number "+i)){
						i++;
					}
					i--;
					if (args.length == 1){
						if (VAR.pLog.isSet(path)){
							moreInfo(sender, p, i);
						} else { sender.sendMessage(ChatColor.BLUE+p.getName()+ChatColor.GREEN+" has not been reported yet.");}
					}
					if (args.length == 2){
						int arg1 = Integer.parseInt(args[1]);
						path = path+".Number "+arg1;
						if (!VAR.pLog.isSet(path)){
							sender.sendMessage(ChatColor.RED+"A report with the number "+arg1+" does not exist.");
							return true;
						}
						sender.sendMessage(ChatColor.GOLD+"--------------------" + ChatColor.GREEN + " Report #"+arg1 + ChatColor.GOLD + " -------------------");
						sender.sendMessage(ChatColor.DARK_GREEN+"Name: "+ChatColor.AQUA+p.getName());
						sender.sendMessage(ChatColor.DARK_GREEN+"Reported by: "+ChatColor.AQUA+VAR.pLog.getString(path+".Reported by"));
						sender.sendMessage(ChatColor.DARK_GREEN+"Date/Time: "+ChatColor.AQUA+"["+VAR.pLog.getString(path+".Date and Time")+"]");
						sender.sendMessage(ChatColor.DARK_GREEN+"Reason: "+ChatColor.AQUA+VAR.pLog.getString(path+".Reason"));
						path = path + "." + p.getName()+" was at Location";
						sender.sendMessage(ChatColor.DARK_GREEN+"World: "+ChatColor.AQUA+VAR.pLog.getString(path+".World"));
						sender.sendMessage(ChatColor.DARK_GREEN+"Location:  "+ChatColor.AQUA+"X: "+VAR.pLog.getInt(path+".X")+"  Y: "+VAR.pLog.getInt(path+".Y")+"  Z: "+VAR.pLog.getInt(path+".Z"));
						sender.sendMessage(ChatColor.GOLD+"--------------------------------------------------");
					}
				} else {
					Bukkit.getServer().dispatchCommand(sender,"check ?");
				}
			} else {this.plugin.denied(sender);}
		}
		//Defining the command /checktp
		if (cmd.getName().equalsIgnoreCase("checktp")){
			if (sender.hasPermission("pman.checktp") || sender.isOp()){
				if (sender instanceof Player){
					if (args.length == 1 || args.length == 2){
						if (args[0].equalsIgnoreCase("?") || (args.length == 2 && args[1].equalsIgnoreCase("?"))){
							sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
							sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/checktp <player> [Report Number]");
							sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
							sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.checktp");
							sender.sendMessage(ChatColor.AQUA +"Teleports you to the location where the reported player");
							sender.sendMessage(ChatColor.AQUA +"had been when he got reported.");
							return true;
						}
						//And once more... Is the player online?
						Player p = this.plugin.checkPlayer(args[0]);
						if (p == null){
							sender.sendMessage(ChatColor.RED+"Could not find the specified player.");
							return true;
						}
						String path = "Reports."+p.getName();
						int i = 1;
						while (VAR.pLog.isSet(path+".Number "+i)){
							i++;
						}
						i--;
						if (args.length == 1){
							if (VAR.pLog.isSet(path)){
								moreInfo(sender, p, i);
							} else { sender.sendMessage(ChatColor.BLUE+p.getName()+ChatColor.GREEN+" has not been reported yet.");}
						}
						if (args.length == 2){
							int a = Integer.parseInt(args[1]);
							if (VAR.pLog.isSet(path+".Number "+a)){
								path = path+".Number "+a+"."+p.getName()+" was at Location";
								World world = Bukkit.getServer().getWorld(VAR.pLog.getString(path+".World"));
								int x = VAR.pLog.getInt(path+".X");
								int y = VAR.pLog.getInt(path+".Y");
								int z = VAR.pLog.getInt(path+".Z");
								float yaw = Bukkit.getServer().getPlayer(sender.getName()).getLocation().getYaw();
								float pitch = Bukkit.getServer().getPlayer(sender.getName()).getLocation().getPitch();
								Location loc = new Location(world, x, y, z, yaw, pitch);
								Bukkit.getServer().getPlayer(sender.getName()).teleport(loc);
								
								String num = getEnding(a);
								sender.sendMessage(ChatColor.GOLD+"Teleported to "+ChatColor.BLUE+p.getName()+"'s "+ChatColor.GRAY+a+num+ChatColor.GOLD+" report location.");
							} else {
								sender.sendMessage(ChatColor.RED+"A report with the number "+a+" does not exist.");
							}
						}
					} else {
						Bukkit.getServer().dispatchCommand(sender,"checktp ?");
					}
				} else { sender.sendMessage(ChatColor.RED+"Sorry, but you have to be a player to execute this command.");}
			} else { this.plugin.denied(sender);}
		}
		//Delete the report of a player
		if (cmd.getName().equalsIgnoreCase("apologise")){
			if (sender.hasPermission("pman.apologise") || sender.isOp()){
				if (args.length == 1 && args[0].equalsIgnoreCase("?")){
					sender.sendMessage(ChatColor.DARK_AQUA+"---------- Command Help ----------");
					sender.sendMessage(ChatColor.GOLD +"Command: "+ChatColor.GREEN+"/apologise <player> <ReportNumber|all>");
					sender.sendMessage(ChatColor.GOLD +"Aliases: "+ChatColor.GREEN+"None");
					sender.sendMessage(ChatColor.GOLD +"Permission: "+ChatColor.GREEN+"pman.apologise");
					sender.sendMessage(ChatColor.AQUA +"Deletes one or all report(s) of a player.");
					return true;
				}
				if (args.length == 2){
					Player p = this.plugin.checkPlayer(args[0]);
					if (p == null){
						this.plugin.notFound(sender, args[0]);
						return true;
					}
					//Delete all reports
					if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")){
						try{
							VAR.pLog.set("Reports."+p.getName(), null);
							VAR.pLog.save(VAR.f_player);
							
							this.plugin.loadPlayerLog();
						} catch (Exception ex){
							ex.printStackTrace();
						}
						sender.sendMessage(ChatColor.GOLD+"You have forgiven "+p.getName());
						for (Player p2: Bukkit.getServer().getOnlinePlayers()){
							if (p2.hasPermission("pman.apologise") || p2.isOp()){
								if (p2 != p)
									p2.sendMessage(VAR.Header+ChatColor.RED+sender.getName()+" has forgiven "+p.getName()+" all of his reports!");
							}
						}
						if (VAR.logit)
							if (sender instanceof Player)
								VAR.log.info(VAR.logHeader+sender.getName()+" has forgiven "+p.getName()+" that he had been reported.");
						return true;
					}
					//Only delete one report
					int arg1 = Integer.parseInt(args[1]);
					String path = "Reports."+p.getName();
					if (VAR.pLog.isSet(path+".Number "+arg1)){
						int i = arg1;
						while (VAR.pLog.isSet(path+".Number "+(i+1))){
							path = "Reports."+p.getName()+".Number "+i;
							String path2 = "Reports."+p.getName()+".Number "+(i+1);
							try{
								VAR.pLog.set(path+".Reported by", VAR.pLog.getString(path2+".Reported by"));
								VAR.pLog.set(path+".Date and Time", VAR.pLog.getString(path2+".Date and Time"));
								VAR.pLog.set(path+".Reason", VAR.pLog.getString(path2+".Reason"));
								path = path+"."+p.getName()+" was at Location";
								path2 = path2+"."+p.getName()+" was at Location";
								VAR.pLog.set(path+".World", VAR.pLog.getString(path2+".World"));
								VAR.pLog.set(path+".X", VAR.pLog.getInt(path2+".X"));
								VAR.pLog.set(path+".Y", VAR.pLog.getInt(path2+".Y"));
								VAR.pLog.set(path+".Z", VAR.pLog.getInt(path2+".Z"));
								VAR.pLog.set("Reports."+p.getName()+".Number "+i+".Distance from reporter to "+p.getName(), VAR.pLog.getString("Reports."+p.getName()+".Number "+(i+1)+".Distance from reporter to "+p.getName()));
								VAR.pLog.save(VAR.f_player);
								
								this.plugin.loadPlayerLog();
							} catch (Exception ex){
								ex.printStackTrace();
							}
							i++;
						}
						try {
							VAR.pLog.set("Reports."+p.getName()+".Number "+i, null);
							VAR.pLog.save(VAR.f_player);
							
							this.plugin.loadPlayerLog();
						} catch (Exception ex){
							ex.printStackTrace();
						}
						sender.sendMessage(ChatColor.GOLD+"You have forgiven "+p.getName()+" his "+i+getEnding(i)+" report.");
						for (Player p2: Bukkit.getServer().getOnlinePlayers()){
							if (p2.hasPermission("pman.apologise") || p2.isOp()){
								if (p2 != p)
									p2.sendMessage(VAR.Header+ChatColor.RED+sender.getName()+" has forgiven "+p.getName()+" one of his reports!");
							}
						}
						if (VAR.logit)
							if (sender instanceof Player)
								VAR.log.info(VAR.logHeader+sender.getName()+" has forgiven "+p.getName()+" his "+i+getEnding(i)+" report.");
					} else { sender.sendMessage(VAR.Header+ChatColor.RED+"A report with the number "+arg1+" does not exist for "+p.getName()+".");}
				} else {
					Bukkit.getServer().dispatchCommand(sender,"/apologise ?");
				}
			} else { this.plugin.denied(sender);}
		}
		return true;
	}
	public void moreInfo(CommandSender sender, Player p, int i){
		String path = "Reports."+p.getName();
		sender.sendMessage(ChatColor.GOLD+"------------------" + ChatColor.GREEN + " PlayerManager " + ChatColor.GOLD + "-----------------");
		sender.sendMessage(ChatColor.DARK_GREEN+"Name: "+ChatColor.AQUA+p.getName());
		sender.sendMessage(ChatColor.DARK_GREEN+"Reported: "+ChatColor.AQUA+i+" times");
		sender.sendMessage(ChatColor.DARK_GREEN+"Report Status: "+getStatus(i));
		sender.sendMessage(ChatColor.DARK_GREEN+"Last Reported: "+ChatColor.AQUA+"["+VAR.pLog.getString(path+".Number "+i+".Date and Time")+"]");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.DARK_GRAY+"To view more information about a report,");
		sender.sendMessage(ChatColor.DARK_GRAY+"type "+ChatColor.GRAY+"/check "+p.getName()+" <Number>");
		sender.sendMessage(ChatColor.GOLD+"--------------------------------------------------");
	}
	public static String getStatus(int i){
		String str = "";
		if (VAR.config.getInt("ReportBan") > 0){
			if (i > VAR.config.getInt("ReportBan"))
				str = ChatColor.DARK_RED + "BANNED!!";
			if (i < VAR.config.getInt("ReportBan"))
				str = ChatColor.RED + "DANGEROUS!";
		}
		if (VAR.config.getInt("ReportKick") > 0)
			if (i < VAR.config.getInt("ReportKick"))
				str = ChatColor.GOLD + "Threatening";
		if (VAR.config.getInt("ReportCmd") > 0)
			if (i < VAR.config.getInt("ReportCmd"))
				str = ChatColor.GREEN + "Okay!";
		return str;
	}
	public static String getDate(){
	    Calendar c = Calendar.getInstance();
	    int month = c.get(2) + 1;
	    String date = Integer.toString(c.get(5));
	    date = date + "/";
	    date = date + month + "/";
	    date = date + c.get(1) + ",";
	    date = date + c.get(11) + ":";
	    date = date + c.get(12) + ".";
	    date = date + c.get(13);
	    return date;
	}
	public static Integer getSeconds(String s){
		String[] str = new String[6];
		str[0] = s.split("/")[0];
		str[1] = s.split("/")[1].split("/")[0];
		str[2] = s.split("/")[2].split(",")[0];
		str[3] = s.split(",")[1].split(":")[0];
		str[4] = s.split(":")[1].split("\\.")[0];
		str[5] = s.split("\\.")[1];
		int sec = Integer.parseInt(str[5]);
		sec = sec + (Integer.parseInt(str[4]) * 60);
		sec = sec + (Integer.parseInt(str[3]) * 3600);
		sec = sec + (Integer.parseInt(str[0]) * 3600 * 24);
		sec = sec + (Integer.parseInt(str[1]) * 3600 * 24 * 30);
		while (sec > 1000000)
			sec = sec - 1000000;
		return sec;
	}
	public static Integer calcDistance(Player sender, Player target){
		int x = abs(abs(sender.getLocation().getBlockX()) - abs(target.getLocation().getBlockX()));
		int distance = x;
		x = abs(abs(sender.getLocation().getBlockY()) - abs(target.getLocation().getBlockY()));
		distance = distance + x;
		x = abs(abs(sender.getLocation().getBlockZ()) - abs(target.getLocation().getBlockZ()));
		distance = distance + x;
		return distance;
	}
	public static int abs(int a){
		if (a < 0)
			a = a*(-1);
		return a;
	}
	public String replace(String str, Player p){			
		str = str.replace("%NAME", p.getName());
		str = str.replace("%IP", Bukkit.getServer().getPlayer(p.getName()).getAddress().toString());
		str = str.replace("%WORLD", Bukkit.getServer().getPlayer(p.getName()).getWorld().getName());
		str = str.replace("%GAMEMODE", Bukkit.getServer().getPlayer(p.getName()).getGameMode().toString());
		str = str.replace("%ONLINEPLAYERS", Integer.toString(Bukkit.getServer().getOnlinePlayers().length));
		str = str.replace("%MAXPLAYERS", Integer.toString(Bukkit.getServer().getMaxPlayers()));
		String s = "";
		for (Player r: Bukkit.getServer().getOnlinePlayers()){
			s = s + r.getDisplayName()+ ", ";
		}
		str = str.replace("%ONLINELIST", s);
		str = str.replace("%SERVERNAME", Bukkit.getServer().getName());
		replaceColours(str);
		return str;
	}
	public String replaceColours(String str){
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
	public static String getEnding(int i){
		String str = "";
		if (i == 1)
			str = "st";
		if (i == 2)
			str = "nd";
		if (i == 3)
			str = "rd";
		if (i >= 4)
			str = "th";
		if (i == 21 || i == 31)
			str = "st";
		if (i == 22 || i == 32)
			str = "nd";
		if (i == 23 || i == 33)
			str = "rd";
		return str;
	}
}
