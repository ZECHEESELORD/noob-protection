package sh.harold.globalrealmutil;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class NoobInvincibilityPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private static final Properties props = new Properties();
    private static PluginDescriptionFile pdfFile = null;
    private static Logger logger = null;
    private int newProtectMinutes = 1440;
    private int returnProtectMinutes = -1;

    @Override
    public void onEnable() {
        pdfFile = getDescription();
        getServer().getPluginManager().registerEvents(this, this);
        logger = Logger.getLogger("Minecraft");
        logger.info("[" + pdfFile.getName() + "] Protects new players for a while. ");
        String fileName = pdfFile.getName() + ".properties";
        getProperties(fileName);
        logger.info("[" + pdfFile.getName() + "] New players are protected for " + newProtectMinutes + " minutes.  ");
        logger.info("[" + pdfFile.getName() + "] Returning players are protected for " + returnProtectMinutes + " minutes.  ");
    }

    @Override
    public void onDisable() {
        OfflinePlayer[] players = getServer().getOfflinePlayers();
        int playerIndex = 0;
        Player player = null;
        while (playerIndex < players.length) {
            if (players[playerIndex].isOnline()) {
                player = (Player) players[playerIndex];
                player.setInvulnerable(false);
                player.setGlowing(false);
                player.saveData();
            }
            playerIndex = playerIndex + 1;
        }
    }

    private void getProperties(String fileName) {
        try (InputStream input = new FileInputStream(fileName)) {
            props.load(input);

            String newProtectStr = props.getProperty("NEWPROTECT");
            if (newProtectStr != null && isNumeric(newProtectStr)) {
                newProtectMinutes = Math.max(1, toInteger(newProtectStr));
            }

            String returnProtectStr = props.getProperty("RETURNPROTECT");
            if (returnProtectStr != null && isNumeric(returnProtectStr)) {
                returnProtectMinutes = Math.max(0, toInteger(returnProtectStr));
            }
        } catch (FileNotFoundException e) {
            saveProperties(fileName);
        } catch (IOException e) {
            logger.severe("[" + pdfFile.getName() + "] Error reading properties file '" + fileName + "'. ");
            e.printStackTrace(System.err);
        }
    }

    private void saveProperties(String fileName) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(fileName);
            props.setProperty("NEWPROTECT", Long.toString(newProtectMinutes));
            props.setProperty("RETURNPROTECT", Long.toString(returnProtectMinutes));
            props.store(output, null);
        } catch (IOException oops) {
            logger.severe("[" + pdfFile.getName() + "] Error writing properties file '" + fileName + "'. ");
            oops.printStackTrace(System.err);
        } finally {
            try {
                assert output != null;
                output.close();
            } catch (IOException oops) {
                oops.printStackTrace(System.err);
            }
        }
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+");
    }

    private int toInteger(String str) {
        return Integer.parseInt(str);
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isInvulnerable()) {
            long timeNow = System.currentTimeMillis();
            long timeSinceFirstJoined = timeNow - player.getFirstPlayed();
            long minutesSinceFirstJoined = Math.abs((timeSinceFirstJoined / 1000) / 60);
            long timeSinceLastJoined = timeNow - player.getLastPlayed();
            long minutesSinceLastJoined = Math.abs((timeSinceLastJoined / 1000) / 60);

            if ((minutesSinceFirstJoined > newProtectMinutes) && (minutesSinceLastJoined > returnProtectMinutes)) {
                player.setInvulnerable(false);
                player.setGlowing(false);
                player.sendMessage(ChatColor.RED + "Your protection is now over!" + ChatColor.DARK_GRAY + ChatColor.ITALIC + " Beware of the monsters in the dark... ");
            }
        }
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        noob(player);
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.setInvulnerable(false);
        player.setGlowing(false);
        player.saveData();
    }

    private void noob(Player player) {
        long timeNow = System.currentTimeMillis();
        long timeSinceFirstPlayed = timeNow - player.getFirstPlayed();
        long minutesSinceFirstPlayed = Math.abs((timeSinceFirstPlayed / 1000) / 60);

        if (minutesSinceFirstPlayed < newProtectMinutes) {
            setNoobProtection(player, "new", minutesSinceFirstPlayed);
        } else {
            long timeSinceLastPlayed = timeNow - player.getLastPlayed();
            long minutesSinceLastPlayed = Math.abs((timeSinceLastPlayed / 1000) / 60);
            if (minutesSinceLastPlayed < returnProtectMinutes) {
                setNoobProtection(player, "returning", minutesSinceLastPlayed);
            } else {
                player.sendMessage(ChatColor.RED + "You are no longer protected! You will be damaged by monsters and other players.");
            }
        }
    }

    private void setNoobProtection(Player player, String type, long minutesSince) {
        player.setInvulnerable(true);
        player.setGlowing(true);

        long remainingMinutes = type.equals("new") ? newProtectMinutes - minutesSince : returnProtectMinutes - minutesSince;
        if (remainingMinutes > 60) {
            long remainingHours = Math.abs(remainingMinutes / 60);
            player.sendMessage("As a " + type + " player on this server, you are protected from damage for the next " + remainingHours + " hours. " + ChatColor.DARK_GRAY + ChatColor.ITALIC + "Use /noob off to turn this off! ");
        } else {
            player.sendMessage("As a " + type + " player on this server, you are protected from damage for the next " + remainingMinutes + " minutes. " + ChatColor.DARK_GRAY + ChatColor.ITALIC + "Use /noob off to turn this off! ");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player attackedPlayer)) {
            return;
        }
        Entity attacker = event.getDamager();
        if (attacker instanceof Player attackingPlayer && attackingPlayer.isInvulnerable()) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("noob"))
            return false;

        if (!(sender instanceof Player player))
            return false;


        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("off")) {
                player.setInvulnerable(false);
                player.setGlowing(false);
                player.saveData();
                player.sendMessage("You have turned your protection off! Beware of mobs! ");
                return true;
            }

            if (player.isInvulnerable()) {
                player.sendMessage(ChatColor.RED + "That's not a valid argument! Use /noob off to turn your protection off. ");
            } else {
                player.sendMessage(ChatColor.RED + "What are you doing? Your protection is over!");
            }
        } else {
            noob(player);
        }

        return true;
    }


    @SuppressWarnings("unused")
    private void doNothing() {
    }

}
