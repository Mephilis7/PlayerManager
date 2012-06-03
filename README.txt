PlayerManager v1.7 by Mephilis7
=====================================

PlayerManager is a nice plugin i wrote for the Minecraft Bukkit server v1.2.5-R2.0
It lets you manage your players better. What it's able to do exactly should be read below.


Features:
---------

- /pman info shows information about the player:
    - Name
    - IP address
    - World
    - last Login
    - last Logout
    - Money [Vault required]
    - Groups [Vault required]
    - Health
    - Food
    - Exp level
    - GameMode
    - Position
    - Distance to the command sender (player only)
    - Whether flying is allowed or not.
    - Whether he is hidden
    - Whether he is muted
    - Whether he has accepted the /rules
- Allows forcing people to read the rules, by typing /rules and then /acceptrules.
    - Configurable prevention of actions, if the /rules have not been accepted yet.
    - Command whitelist.
    - Also supports other plugins that already include the /rules command!
- Allows your players to /report other people, i.e. if they see somebody griefing a house...
    - The reported player's name, location and distance to the command sender will be saved in the PlayerLog.yml
    - Your players will have to write a reason aswell.
    - Allows people with the right permission to use /check to see how many times a player had been reported... and why.
    - Allows teleporting to the location where the reported player was by typing /checktp
    - When somebody reports a player, all Ops/people with the permission "pman.check" will be notified.
- Set various properties per player by typing /pman set!
- Show/Hide your players!
- Mute spammers!
- Supports Rei's Minimap
- Custom join/quit messages
- Fully configurable
- Logs all IPs and more information to plugins/PlayerManager/PlayerLog.yml
- Blocks bots from joining the server
- SourceCode on GitHub!
=> https://github.com/Mephilis7/PlayerManager


Commands:
---------
/pman - Shows help
/pman hide <player> - Hides a player. Not able to be seen anywhere.
/pman info <player|ip> - Shows information about the specified player.
/pman list - Shows all connected players with their gamemode.
/pman mute <player> - Mutes a player.
/pman set <property> <player> <value> - Setting of various properties per player.
/pman srtp - Set the point to teleport players to when they type /acceptrules for the first time.
/pman show <player> - Shows a hidden player again.
/pman reload - Reloads the config.yml
/rules - View the rules, as customized in config.yml
/acceptrules- Accept the server rules
/report <player> <reason> - Reports a player to the Admins
/check <player> [ReportNumber] - Look up information about a player concerning the /report command
/checktp <player> <ReportNumber> - Teleport to the location where the reported player had been when he was reported


/pman set Properties:
--------------------
fly - Set AllowFlight per player
fire - Set a player on fire
food - Set player's food level
health - Set player's health
name - Set player's name
xp - Set player's xp level


Permissions:
------------
pman.help - Permission to use /pman
pman.hide - Permission to use /pman hide and /pman show
pman.info - Permission to use /pman info
pman.info.name - Permission to be shown the name on /pman info
pman.info.ip - Permission to be shown the IP address on /pman info
pman.info.lastLogin - Permission to be shown the date and time of the last login
pman.info.lastLogout - Permission to be shown the date and time of the last logout
pman.info.world - Permission to be shown the world on /pman info
pman.info.money - Permission to be shown the money on /pman info
pman.info.group - Permission to be shown the groups on /pman info
pman.info.health - Permission to be shown health on /pman info
pman.info.food - Permission to be shown food level on /pman info
pman.info.xp - Permission to be shown Exp Level on /pman info
pman.info.gamemode - Permission to be shown gamemode on /pman info
pman.info.position - Permission to be shown position on /pman info
pman.info.distance - Permission to be shown distance between target and command executor on /pman info
pman.info.allowFlight - Permission to be shown whether the target is allowed to fly or not.
pman.info.hidden - Permission to be shown whether the target is hidden
pman.info.mute - Permission to be shown whether the target is muted
pman.info.rules - Permission to be shown whether the target has accepted the rules
pman.list - Permission to use /pman list
pman.mute - Permission to /pman mute somebody
pman.rules - Permission to use /rules and /acceptrules
pman.rulestp - Permission to use /pman srtp
pman.set - Permission to use /pman set
pman.set.fly - Permission to set AllowFlight per player
pman.set.fire - Permission to set fire to a player
pman.set.food - Permission to set food level of a player
pman.set.health - Permission to set health of a player
pman.set.name - Permission to set name of a player
pman.set.xp - Permission to set xp level of a player
pman.report - Permission to /report a player
pman.check - Permission to /check the /report state of a player
pman.checktp - Permission to use /checktp
pman.reload - Permission to use /pman reload
pman.update - Permission to be notified when an update is available


Planned Features:
-----------------

- Server maintenance command: keeps server online, but kicks every player without a special permission.
- Configurable polls, you players could then /vote within a defined amount of time for an option.
- improve the BotBlocker (one IP bound to one name and vice versa)
==> See http://dev.bukkit.org/server-mods/playermanager 


Changelog:
----------
v1.7
- Added /report, /check, /checktp and /apologise
- Config.yml update to version 5
- Cleaned up the code a bit:
	- Fixed two annoying bugs concerning the show/hide command. How could I miss them?
	- Fixed some logging messages.
	- Partial name recognition fixed.
	- Other small fixes

v1.6
- UpdateChecker notifies you when an update is available
- Optional depency on Vault to display group/money when typing /pman info
- Added chests and redstone to the prevention of actions if the player hasn't accepted the rules yet
- You can't set strange values as the food/health level anymore. (i.e. 230719)
- Players will now be notified when their food/health/EXP level, permission to fly or nickname changes
- Config.yml update to version 4
- BugFixes

v1.5
- Got the support for other plugins using /rules working
- Configurable prevention of actions if the player has not accepted the rules yet
- Teleporting on /acceptrules
- Config.yml update to version 3

v1.4
- Added /rules and /acceptrules
- Updatet the config.yml to version 2
- Config.yml now saves your changes when it regenerates/updates
- Built against CB v1.2.5-R2.0

v1.3 stable
- "/pman set" now tells you when your arguments where wrong
- Minor Bugs from v1.3_Beta3 fixed
- Added real name to /pman list
- Added /pman hide and /pman show to hide/show your players
- Added /pman mute to mute spammers
- Made the PlayerLog.yml waaay better
- Fixed the IP Logger not working as it should
- Fixed a problem with the config file

v1.3_Beta3
- Severe bugs occuring in Beta2 fixed

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