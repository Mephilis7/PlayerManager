package com.github.Mephilis7.PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PMan_RulesEventHandler 
implements Listener{
	
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
	
	private PMan_IPLogger ip = new PMan_IPLogger();
	String RNAMsg = "";
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		//If the rules are enabled, check whether the player has accepted the rules and the permission to speak.
		if (VAR.config.getBoolean("enableRules")){
			if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
				if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("chat")){
					RNAMsg = VAR.config.getString("RulesNotAcceptedMsg");
					RNAMsg = ip.replace(RNAMsg, event.getPlayer());
					event.getPlayer().sendMessage(RNAMsg);
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event){
		//Check whether the player has accepted the rules and is allowed to pick up drops. If not, cancel the event.
		if (VAR.config.getBoolean("enableRules")){
			if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
				if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("pickupdrops")){
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onPlayerBlockInteract(PlayerInteractEvent event){
		if (VAR.config.getBoolean("enableRules")){
			if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
				if (event.getAction().equals(Action.LEFT_CLICK_BLOCK))
					return;
				String msg = VAR.config.getString("RulesNotAcceptedMsg");
				msg = ip.replace(msg, event.getPlayer());
				if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("chest")){
					if (event.getClickedBlock().getTypeId() == 54){
						event.getPlayer().sendMessage(msg);
						event.setCancelled(true);
						return;
					}
				}
				if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("redstone")){
					int ID = event.getClickedBlock().getTypeId();
					if (ID == 69 || ID == 70 || ID == 72 || ID == 77){
						event.getPlayer().sendMessage(msg);
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		if (VAR.config.getBoolean("enableRules")){
			if (event.getDamager() instanceof Player){
				String name = getPlayerByID(event.getDamager().getEntityId());
				if (!VAR.pLog.getString("players."+name+".Has accepted rules").equalsIgnoreCase("true")){
					if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("damageothers")){
						String msg = VAR.config.getString("RulesNotAcceptedMsg");
						msg = ip.replace(msg, Bukkit.getServer().getPlayer(name));
						Bukkit.getServer().getPlayer(name).sendMessage(msg);
						event.setCancelled(true);
						return;
					}
				}
				if (event.getEntity() instanceof Player){
					String damagedName = getPlayerByID(event.getEntity().getEntityId());
					if (!VAR.pLog.getString("players."+damagedName+".Has accepted rules").equalsIgnoreCase("true")){
						if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("damageself")){
							String msg = VAR.config.getString("RulesNotAcceptedDmgSelfMsg");
							msg = ip.replace(msg, Bukkit.getServer().getPlayer(name));
							Bukkit.getServer().getPlayer(name).sendMessage(msg);
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			if (event.getEntity() instanceof Player){
				String name = getPlayerByID(event.getEntity().getEntityId());
				if (!VAR.pLog.getString("players."+name+".Has accepted rules").equalsIgnoreCase("true")){
					if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("damageself")){
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event){
		if (VAR.config.getBoolean("enableRules")){
			String config = VAR.config.getString("PreventNotAccepted");
			if (config.toLowerCase().contains("move[") && config.contains("]")){
				if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
					String aDistance = "";
					for (String str: config.toLowerCase().split(";")){
						if (str.contains("move[") && str.contains("]"))
							aDistance = str;
					}
					aDistance = aDistance.replace("move", "");
					aDistance = aDistance.replace("[", "");
					aDistance = aDistance.replace("]", "");
					int aDist = Integer.parseInt(aDistance.trim());
					int i = 0;
					while(i < 3){
						String direction = "";
						if (i == 0){
							int Sx = event.getPlayer().getWorld().getSpawnLocation().getBlockX();
							int x = event.getPlayer().getLocation().getBlockX();
							x = abs(Sx) - abs(x);
							if (abs(x) > aDist){
								if (x > 0)
									direction = "West";
								if (x < 0)
									direction = "East";
							}
						}
						if (i == 1){
							int Sy = event.getPlayer().getWorld().getSpawnLocation().getBlockY();
							int y = event.getPlayer().getLocation().getBlockY();
							y = abs(Sy) - abs(y);
							if (abs(y) > aDist){
								if (y < 0)
									direction = "Up";
								if (y > 0)
									direction = "Down";
							}
						}
						if (i == 2){
							int Sz = event.getPlayer().getWorld().getSpawnLocation().getBlockZ();
							int z = event.getPlayer().getLocation().getBlockZ();
							z = abs(Sz) - abs(z);
							if (abs(z) > aDist){
								if (z > 0)
									direction = "North";
								if (z < 0)
									direction = "South";
							}
						}
						if (!direction.equalsIgnoreCase("")){
							event.getPlayer().sendMessage(ChatColor.RED+"You have to accept the /rules before");
							event.getPlayer().sendMessage(ChatColor.RED+"going any further!");
							double x = event.getPlayer().getLocation().getX();
							double y = event.getPlayer().getLocation().getY();
							double z = event.getPlayer().getLocation().getZ();
							if (direction.equalsIgnoreCase("West"))
								x = x+1;
							if (direction.equalsIgnoreCase("East"))
							x = x-1;
							if (direction.equalsIgnoreCase("North"))
								z = z+1;
							if (direction.equalsIgnoreCase("South"))
								z = z-1;
							if (direction.equalsIgnoreCase("Up"))
								y = y-1;
							if (direction.equalsIgnoreCase("Down"))
								y = y+1;
							Location loc = new Location(event.getPlayer().getWorld(), x, y, z, event.getPlayer().getLocation().getYaw(), event.getPlayer().getLocation().getPitch());
							event.getPlayer().teleport(loc);
							return;
						}
						i++;
					}
					return;
				}
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if (VAR.config.getBoolean("enableRules")){
			if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("blockbreak")){
				if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
					String msg = VAR.config.getString("RulesNotAcceptedMsg");
					msg = ip.replace(msg, event.getPlayer());
					event.getPlayer().sendMessage(msg);
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if (VAR.config.getBoolean("enableRules")){
			if (VAR.config.getString("PreventNotAccepted").toLowerCase().contains("blockplace")){
				if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
					String msg = VAR.config.getString("RulesNotAcceptedMsg");
					msg = ip.replace(msg, event.getPlayer());
					event.getPlayer().sendMessage(msg);
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		if (VAR.config.getBoolean("enableRules")){
			if (!VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("true")){
				String wl = VAR.config.getList("RulesWhiteList").toString();
				wl = wl.replace("[", "");
				wl = wl.replace("]", "");
				wl = wl.replace(" ", "");
				String[] list = wl.split(",");
				int i = 0;
				boolean onList = false;
				while (i < list.length){
					if (event.getMessage().startsWith("/"+list[i]))
						onList = true;
					i++;
				}
				if (!onList){
					String msg = VAR.config.getString("RulesNotAcceptedWLMsg");
					msg = ip.replace(msg, event.getPlayer());
					event.getPlayer().sendMessage(msg);
					event.setCancelled(true);
					return;
				}
			}
			if (event.getMessage().trim().equalsIgnoreCase("/rules")){
				if (VAR.pLog.getString("players."+event.getPlayer().getName()+".Has accepted rules").equalsIgnoreCase("false")){
					try{
						VAR.pLog.set("players."+event.getPlayer().getName()+".Has accepted rules", "hasTyped");
						VAR.pLog.save(VAR.f_player);
						ip.loadPlayerLog();
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
	}
	public String getPlayerByID(int i){
		String name = "";
		for (Player p: Bukkit.getServer().getOnlinePlayers()){
			if (i == p.getEntityId())
				name = p.getName();
		}
		return name;
	}
	
	Integer abs(int i){
		if (i < 0)
			i = i*(-1);
		return i;
	}
}
