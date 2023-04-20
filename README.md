# GlobalRealms Utilities: Noob Protection
Original Plugin by FredaShay | Forked by [Harold](https://harold.sh)

This is a plugin that prevents inexperienced new players from being discouraged by lowering the difficulty gap.
This plugi disables damage (invulnerability) for a set amount of time for new players, and a shorter amount of time for returning players.
(All of these are configurable.)

### Installation
> **Plugin is written for Spigot version 1.19.3. Compatibility for legacy versions is not guaranteed, and support will not be offered.**

You can either self compile the plugin, or download the latest release from the [releases page]()

#### Self Compiling
1. Clone the repository
2. Run `gradle jar` in the root directory
3. The compiled jar will be located in `build/libs/`

### Usage
After the plugin launches, you will be able to configure the plugin. 
Time options are provided in minutes. 
By default, New players have an invincibility window of `1440` minutes, 
while returning players have an invincibility window of `5` minutes. 
Set the value to `-1` to disable.

#### Commands
`/noob off` - Turns off protection for players.

### Contributing
If by any means, you want to improve my bowl of spaghetti, feel free to submit a pull request, or open an issue. I will try to respond as soon as possible. Please keep in mind that this repository primarily serves as an archive for projects done for GlobalRealms.

_also i like the funny ⭐, so if you are willing to hand me a spare star, i'll gladly take it!_

### Credits:
This is a fork of an older plugin from [SpigotMC.org](https://www.spigotmc.org/resources/fredashays-noob-protect.48152/) by FredaShay.