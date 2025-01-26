<div>
<h1 style="margin: 0;font-weight: 700;font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif,Apple Color Emoji,Segoe UI Emoji">OneCore</h1>

![licence](https://img.shields.io/badge/License-GPL-brightgreen)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20+-brightgreen.svg)]()
Core plugin for any type of server

A comprehensive essential commands plugin for Minecraft servers, providing core functionality and utilities.

## 🚀 Features

- 💬 Advanced chat management with mentions and formatting
- 💰 Integrated economy system
- 🏠 Complete teleportation system (homes, warps, tpa)
- 📝 Sign editing capabilities
- 🌍 Per Player World management (weather, time
- ⚙️ Offline support for most commands

## 📥 Installation

1. Download the latest OneCore.jar
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin

## ⚙️ Building
1. git clone https://github.com/Zoltus/OneCore.git
2. Make sure you have atleast jdk 21
3. mvn clean package
4. onecore.jar goes to your target folder

## Utilities
- Mentions
- Chat colors
- Minimessage support
- backup system (in progress)
- Chat formatting
- Placeholder API Support
- Sign edit (Shift right click sign)
- Teleporting with mounts & leashed animals
- Console filter
- Everything customizable including lang, command nodes/permissions/aliases

## ⚙️ Commands

| Command                     | Arguments                               | Permissions                                                           | Offline Support |
|-----------------------------|-----------------------------------------|-----------------------------------------------------------------------|-----------------|
| /back                       | \<player>                               | onecore.back<br>onecore.back.other                                    | false           |
| /broadcast                  | \<msg>                                  | onecore.broadcast                                                     | -               |
| /clearchat                  | -                                       | onecore.clearchat                                                     | -               |
| /setspawn                   | \<location>                             | onecore.setspawn                                                      | -               |
| /setwarp                    | \<name>                                 | onecore.setwarp                                                       | -               |
| /delwarp                    | \<name>                                 | onecore.delwarp                                                       | -               |
| /feed                       | \<player>                               | onecore.feed<br>onecore.feed.other                                    | true            |
| /fly                        | \<player> \<true/false>                 | onecore.fly<br>onecore.fly.other                                      | true            |
| /gamemode                   | \<mode> \<player>                       | onecore.gamemode<br>onecore.gamemode.other<br>onecore.gamemode.<mode> | true            |
| /god                        | \<player>                               | onecore.god<br>onecore.god.other                                      | true            |
| /heal                       | \<player>                               | onecore.heal<br>onecore.heal.other                                    | true            |
| /killall                    | \<type> \<range>                        | onecore.killall                                                       | -               |
| /ptime                      | \<player> \<time>                       | onecore.ptime<br>onecore.ptime.other                                  | false           |
| /pweather                   | \<player> \<weather>                    | onecore.pweather<br>onecore.pweather.other                            | false           |
| /reload                     | -                                       | onecore.reload                                                        | -               |
| /repair                     | \<hand/all> \<player>                   | onecore.repair                                                        | false           |
| /seen                       | \<player>                               | onecore.seen<br>onecore.seen.other                                    | true            |
| /setfirstjoinspawn          | -                                       | onecore.setfirstjoinspawn                                             | -               |
| /setmaxplayers              | \<amount>                               | onecore.setmaxplayers                                                 | -               |
| /setspawn                   | \<location>                             | onecore.setspawn                                                      | -               |
| /signedit                   | \<set/clear/copy/paste> \<line> \<text> | onecore.signedit.<set/clear/copy/paste>                               | -               |
| /speed                      | \<amount> \<player> \<fly/walk>         | onecore.speed<br>onecore.speed.other                                  | true            |
| /systeminfo                 | -                                       | onecore.systeminfo                                                    | -               |
| /time                       | \<time>                                 | onecore.time                                                          | -               |
| /top(Teleport to top block) | -                                       | onecore.top                                                           | false           |
| /tp                         | \<player> \<coord>                      | onecore.tp<br>onecore.tp.other                                        | -               |
| /vanish                     | \<player>                               | onecore.vanish<br>onecore.vanish.other                                | true            |
| /weather                    | \<weather>                              | onecore.weather                                                       | -               |
| /balance                    | \<player>                               | onecore.balance<br>onecore.balance.other                              | true            |
| /baltop                     | -                                       | onecore.baltop                                                        | true            |
| /give                       | \<player> \<amount>                     | onecore.give                                                          | true            |
| /pay                        | \<player> \<amount>                     | onecore.pay                                                           | true            |
| /set                        | \<player> \<amount>                     | onecore.set                                                           | true            |
| /take                       | \<player> \<amount>                     | onecore.take                                                          | true            |
| /transfer                   | \<player> \<amount>                     | onecore.transfer                                                      | true            |
| /enderchest                 | \<player>                               | onecore.enderchest<br>onecore.enderchest.other                        | true            |
| /back                       | -                                       | onecore.back                                                          | -               |
| /home                       | \<player> \<name>                       | onecore.home<br>onecore.home.other                                    | true            |
| /sethome                    | \<player> \<name>                       | onecore.sethome<br>onecore.sethome.other                              | true            |
| /delhome                    | \<player> \<name>                       | onecore.delhome<br>onecore.delhome.other                              | true            |
| /msg                        | \<player>                               | onecore.msg                                                           | -               |
| /invsee   (in progress)     | \<player>                               | onecore.invsee<br>onecore.invsee.edit                                 | true            |
| /ping                       | \<player>                               | onecore.ping<br>onecore.ping.other                                    | -               |
| /playtime                   | \<player>                               | onecore.playtime<br>onecore.playtime.other                            | true            |
| /spawn                      | \<player>                               | onecore.spawn<br>onecore.spawn.other                                  | true            |
| /tpa                        | \<player>                               | onecore.tpa                                                           | -               |
| /tpaccept                   | \<player>                               | onecore.tpaccept                                                      | -               |
| /tpahere                    | \<player>                               | onecore.tpahere                                                       | -               |
| /tpdeny                     | \<player>                               | onecore.tpdeny                                                        | -               |
| /tptoggle                   | \<player(todo)>                         | onecore.tptoggle                                                      | -               |
| /warp                       | \<warp> \<player>                       | onecore.warp<br>onecore.warp.other                                    | true            |

## ⚙️ Adding new Database systems
1. Create class and Implement Database (Look into how SQLiteImpl is done)
2. OneCore.java change this.database = `SQLiteImpl.init(this);` to `this.database = <yourImpl>.init(this);`

</div>

