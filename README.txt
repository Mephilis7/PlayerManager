PlayerManager v1.8 by Mephilis7
=====================================

PlayerManager is a nice plugin i wrote for the Minecraft Bukkit server v1.2.5-R3.0
It lets you manage your players better. What it's able to do exactly should be read below.

License:
--------
Copyright 2012 Mephilis7

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
	 
	 
	 

Features:
---------

- /pman info shows information about the player:
    - Name
    - How long he's been playing on this server
    - IP address
    - World
    - first login
    - last Login
    - last Logout
    - Money [Vault required]
    - Groups [Vault required]
    - GameMode
    - Position
    - Distance to the command sender (player only, no console)
    - Whether he has accepted the /rules
    - And much more...
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
- Allows you to censor words you don't like...
	- Ingame adding/deleting of "bad words" possible.
- Allows you to fake-op people asking for admins ranks.
    - No commands are recognized
    - No blocks can be broken
    - Or whatever you wish PlayerManager to do. Just tell me!
- Show/Hide your players!
- Mute spammers!
- Supports Rei's Minimap
- Custom join/quit messages
- SourceCode on GitHub!
=> https://github.com/Mephilis7/PlayerManager


Commands:
---------
/pman - Shows help
/pman censor <argument> <value>
/pman hide <player>
/pman info <player|ip>
/pman list
/pman mute <player>
/pman set <property> <player> <value>
/pman srtp
/pman show <player>
/pman reload
/rules
/acceptrules
/report <player> <reason>
/check <player> [ReportNumber]
/checktp <player> <ReportNumber>
/fakeop <player>
/fakedeop <player>
/fakelist <player>
Use the in-game help to get to know more about them.

/pman censor arguments:
----------------------
add - Add words to the censor list
delete - Delete words from the censor list
disable - Disable the word censor
enable - Enable the word censor
list - List all words that will be censored

/pman set Properties:
--------------------
fly - Set AllowFlight per player
fire - Set a player on fire
food - Set player's food level
health - Set player's health
name - Set player's name
weather - Set the server weather
xp - Set player's xp level


Permissions:
------------
pman.censor.add
pman.censor.delete
pman.censor.disable
pman.censor.enable
pman.censor.list
pman.fakeop
pman.fakedeop
pman.fakelist
pman.help
pman.hide - Permission to use /pman hide and /pman show
pman.info
pman.info.name
pman.info.time
pman.info.ip
pman.info.firstLogin
pman.info.lastLogin
pman.info.lastLogout
pman.info.world
pman.info.money
pman.info.group
pman.info.health
pman.info.food
pman.info.xp
pman.info.gamemode
pman.info.position
pman.info.distance
pman.info.allowFlight
pman.info.hidden
pman.info.mute
pman.info.rules
pman.list
pman.mute
pman.rules
pman.rulestp - Permission to use /pman srtp
pman.set.fly 
pman.set.fire
pman.set.food
pman.set.health
pman.set.name
pman.set.weather
pman.set.xp
pman.report
pman.check
pman.checktp
pman.reload
pman.update - Permission to be notified when an update is available
pman.view - Permission to see hidden players
If not stated else, all these permissions grant access to one PlayerManager command.
The pman.info.____ permissions allow players to view the information.


Planned Features:
-----------------

==> See http://dev.bukkit.org/server-mods/playermanager


Changelog:
----------
v1.8
 [+] Added /pman censor commands
 [+] Added /fakeop commands
 [+] Added configurable commands that are executed when a player is reported a configurable amount of times
 [+] Added played time to /pman info and PlayerLog.yml
 [+] Added the very first login to /pman info and PlayerLog.yml
 [+] Added permission to see hidden players (pman.view)
 [+] Added /pman set weather
 [!] Updated for CB v1.3.1-R1.0
 [!] In-game help is now helpful
 [!] Checking for updates automatically is now optional.
 [!] Changed the License to Apache 2.0
 [!] Config.yml update to version 6
 [FIX] Fixed a bug that made muted players able to talk.
 [FIX] Fixed a bug that made it impossible to execute multiple commands from the config.yml when somebody typed /acceptrules. Why didn't anybody notice this?

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