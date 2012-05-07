PlayerManager v1.3_Beta1 by Mephilis7
=====================================

PlayerManager is a nice plugin i wrote for the Minecraft Bukkit server v1.2.5-R1.3
It lets you manage your players better. At least that's what it will be good for one day.


Features:
---------

- /pinfo shows information about the player:
    - Name
    - IP address
    - World
    - Health
    - Food
    - Exp level
    - GameMode
    - Position
    - Distance to the command sender (player only)
- Console support
- Supports Rei's minimap
- Custom join/quit messages
- Fully configurable
- Logs all IPs to plugins/PlayerInfo/IPs.txt
- Blocks bots from joining the server
- SourceCode on GitHub!
=> https://github.com/Mephilis7/PlayerInfo


Commands:
---------
/pman - Shows help
/pman info <player|ip> - Shows information about the specified player.
/pman list - Shows all connected players with their gamemode.
/pman set <property> <player> <value> - Setting of various properties per player.
/pman reload - Reloads the config.yml


/pman set Properties:
--------------------
fly - Set AllowFlight per player
food - Set player's food level
health - Set player's health
name - Set player's name
xp - Set player's xp level


Permissions:
------------
pman.help - Permission to use /pman
pman.info - Permission to use /pman info
pman.info.name - Permission to be shown the name on /pman info
pman.info.ip - Permission to be shown the IP address on /pman info
pman.info.world - Permission to be shown the world on /pman info
pman.info.health - Permission to be shown health on /pman info
pman.info.food - Permission to be shown food level on /pman info
pman.info.xp - Permission to be shown Exp Level on /pman info
pman.info.gamemode - Permission to be shown gamemode on /pman info
pman.info.position - Permission to be shown position on /pman info
pman.info.distance - Permission to be shown distance between target and command executor on /pman info
pman.list - Permission to use /pman list
pman.set - Permission to use /pman set
pman.set.fly - Permission to set AllowFlight per player
pman.set.food - Permission to set food level of a player
pman.set.health - Permission to set health of a player
pman.set.name - Permission to set name of a player
pman.set.xp - Permission to set xp level of a player
pman.reload - Permission to use /pman reload


Planned Features:
-----------------

- Hooking into Vault to display group and money
- Add first/last login and last logout to /pman info
- Server maintenance command: keeps server online, but kicks every player without a special permission.
- improve the BotBlocker (one IP bound to one name and vice versa)


Changelog:
----------

v1.3_Beta2
- Added /pman set fly, health, food, xp and name
- Added optional logging of used commands to console
- Added many permissions
- Made the support for Rei's Minimap fully configurable

v1.3_Beta1
- Configurable join/quit messages
- Added /pman list
- Distance does not show a negative number anymore
- Other small bug fixes

v1.2
- Added BotBlocker
- Added Health, Food, Xplevel, GameMode, Position and Distance info
- Added configurable order of the informations
- Code cleanup

v1.1
- Added the IPLogger
- Added custom join messages and Rei's Minimap support

v1.0
- Initial Release