# MXEessentials

##### Note: At the moment this Plugin is only awayable in german!

MXE is a Minecraft 1.19.2 Spigot Essentials Plugin developed by me in my free time.
This plugin is most likely very buggy and not intended for public use.
Older versions are most likely not usable so don't bother trying them out.


### Features
- **21 commands**
- Changes default **player join/leave/chat messages**
- **Chat coloring** using &-Color-Codes ([Image](https://camo.skyblock.net/4898234defc4ebd6680475bc7729223d3ba78577?url=https:%2F%2Fi.imgur.com%2FxkgOs7u.jpg))
- Uses SQLite Database
- Uses and depends on [LuckPerms](https://github.com/LuckPerms/LuckPerms) as well as [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) *(api)*
- **Ban and mute system** with ban messages and temporary bans/mutes
- **Role prefixes** using LuckPerms and **colored names**
- **/nick command** using ProtocolLib
- 10 **homes** for each player
- **/back command** for when a player dies
- Replaces default /help command
- Adds header and footer to tablist 

### To do
- **Make /nick using ProtocolLib optional**
- **Changable language**
- Fix many bugs
- Make /nick also change a players skin
- Make Permissions using LuckPerms optional
- More utilities
- Add support for other databases such as MySQL
- Make /help replacement optional
- /home and /back tweaks
- Custom join/leave/chat messages and more custom replacements
- Custom ban messages
- Custom amount of homes
- Add support for other permissions plugins such as PermissionsEx
- More settings
- Make every feature optional

## Installation

### Installing MXE

To install MXE simply download the [Latest Version](https://github.com/mgsmemebook/MXE/releases/latest) and place the .jar file inside your server's /plugins folder.

As for now you will also need to download the latest [LuckPerms](https://luckperms.net/) version and the latest [ProtocolLib](https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/) dev build *(ProtocolLib doesn't fully support 1.19 yet)* and place both .jar files inside your /plugins folder as well. 

Once you start the Server, MXE will automatically create a MXE folder inside of which the database is stored. In future version you will also find the plugins configuration file there.

### Configuring LuckPerms

As for now MXE doesn't check for op permissions, so you will have to give users permissions to use every plugin command.
I recommend using groups to manage permissions. 

In LuckPerms you can easily create groups using the LuckPerms Web-Editor (/lp editor) or simply using commands.
If you prefer using commands you can create groups using `/lp creategroup [group] [weight] [displayname]`. 
*Please note that you will have to set a group weight in order for commands such as /vanish or /ban to work.* MXE doesn't use group displaynames at the moment.

To set group permissions via commands use `/lp group [group] permission set [permission] [true/false] [cotext]` ([context info](https://luckperms.net/wiki/Context)).
You can also add group prefixes *(MXE doesn't use suffixes yet)* using `/lp group [group] meta setprefix [priority] [prefix]`. MXE also uses Color-Codes here.
To set the nametag color (independend of prefix) you will need to add a group meta node using `/lp group [group] meta set [key] [value] [context]`.
*(In the Web-Editor add a meta.key.value node)*

For default groups i suggest making and configuring a player group and setting it as the default group's parent.
To set a players group you can use LuckPerm's `/lp user [user] parent set [group]` or MXE's `/setrank [user] [group]` command.

For additional help on LuckPerms visit their official [Wiki](https://luckperms.net/wiki/Home).

##### Examples: 
```
/lp creategroup admin 99
/lp group admin permission set mxe.ban true
/lp group admin meta setprefix 99 &a[Admin]
/lp group admin meta set color &a
/lp group default parent set user
/lp user mgsmemebook parent set admin
/setrank PhoenixSC moderator
```


After configuring LuckPerms and restarting the Server you should be good to go!

If you need help you can always contact me on Discord *(mgsmemebook#2636)*. Have fun!



##### Commands
```
/back /ban /fly /gm /god /help /home /kick /kill
/mute /nick /setrank /tpa /tpaccept /tpahere /tpall
/tpdeny /tphere /unban /unmute /vanish
```

#### Known Issues on V.1.3.1
- When players don't see you /nick doesn't work
