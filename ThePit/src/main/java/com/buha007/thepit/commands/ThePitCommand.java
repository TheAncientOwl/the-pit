package com.buha007.thepit.commands;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.playerData.PlayerData;
import com.buha007.thepit.tab.Tab;
import com.buha007.thepit.utilities.Msg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ThePitCommand implements CommandExecutor {

    private Main main;

    public ThePitCommand(Main instance) {
        main = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("thepit"))
            return true;
        FileConfiguration cfg = Msg.getMessages();

        if (!sender.hasPermission("pit.admin")) {
            Msg.send(sender, cfg.getString("prefix") + cfg.getString("noPermission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args.length == 1) {
            // HELP
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender);
                return true;
            }
            // RELOAD_CONFIG
            if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                main.loadConstants();
                Data.save();
                Data.reload();
                Msg.reloadMessages();
                Tab.load(Main.getPlugin());
                Msg.send(sender, cfg.getString("prefix") + cfg.getString("reload"));
                return true;
            }
            Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
            return true;
        }

        /**
         * -> Gold add/set <br>
         * -> Renwon add/set <br>
         * -> XpNeed add/set <br>
         */
        if (args.length == 4) {
            Player player = Bukkit.getPlayerExact(args[2]);
            if (!(player instanceof Player)) {
                Msg.send(sender, cfg.getString("prefix") + cfg.getString("playerNotOnline"));
                return true;
            }
            PlayerData playerData = Data.get(player.getUniqueId());
            double number = 0;
            try {
                number = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                Msg.send(sender, cfg.getString("prefix") + cfg.getString("notANumber"));
                return true;
            }

            // -> GOLD
            if (args[0].equalsIgnoreCase("gold")) {
                double oldGold = playerData.getGold();
                double newGold = 0;

                if (args[1].equalsIgnoreCase("add"))
                    newGold = oldGold + number;
                else if (args[1].equalsIgnoreCase("set"))
                    newGold = number;
                else {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
                    return true;
                }

                if (newGold > Main.MAX_CURRENCY)
                    newGold = Main.MAX_CURRENCY;

                playerData.setGold(newGold);
                if (args[1].equalsIgnoreCase("add"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("goldAdded"));
                else if (args[1].equalsIgnoreCase("set"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("goldSet"));

                return true;
            }

            if (args[0].equalsIgnoreCase("neededXP")) {
                double newNeededXP = 0;

                if (args[1].equalsIgnoreCase("add"))
                    newNeededXP = playerData.getNeededXP() + number;
                else if (args[1].equalsIgnoreCase("set"))
                    newNeededXP = (int) number;
                else {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
                    return true;
                }

                if (newNeededXP < 0)
                    newNeededXP = 0;

                String oldLevelPrefix = playerData.getLevelPrefix();
                playerData.setNeededXP(newNeededXP);
                if (newNeededXP == 0) {
                    Msg.titleChatLevelUp(player, oldLevelPrefix, playerData.getLevelPrefix());
                    Tab.setPrefix(player, playerData.getLevelPrefix());
                }

                if (args[1].equalsIgnoreCase("add"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("xpAdded"));
                else if (args[1].equalsIgnoreCase("set"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("xpSet"));
                return true;
            }

            if (args[0].equalsIgnoreCase("renown")) {
                int newRenown = 0;
                if (args[1].equalsIgnoreCase("add"))
                    newRenown = playerData.getRenown() + (int) number;
                else if (args[1].equalsIgnoreCase("set"))
                    newRenown = (int) number;
                else {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
                    return true;
                }

                if (newRenown > Main.MAX_CURRENCY)
                    newRenown = Main.MAX_CURRENCY;
                playerData.setRenown(newRenown);

                if (args[1].equalsIgnoreCase("add"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("renownAdded"));
                else if (args[1].equalsIgnoreCase("set"))
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("renownSet"));
                return true;
            }

        }

        if (args.length == 3) {
            Player player = Bukkit.getPlayerExact(args[1]);
            if (!(player instanceof Player)) {
                Msg.send(sender, cfg.getString("prefix") + cfg.getString("playerNotOnline"));
                return true;
            }
            PlayerData playerData = Data.get(player.getUniqueId());
            double number = 0;
            try {
                number = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                Msg.send(sender, cfg.getString("prefix") + cfg.getString("notANumber"));
                return true;
            }

            if (args[0].equalsIgnoreCase("setlevel")) {
                if (number > 120) {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("maxLevel"));
                    return true;
                }
                if (number <= 0) {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("minLevel"));
                    return true;
                }

                String oldLevelPrefix = playerData.getLevelPrefix();
                playerData.setLevel((int) number);
                String newLevelPrefix = playerData.getLevelPrefix();
                Msg.titleChatLevelUp(player, oldLevelPrefix, newLevelPrefix);
                Tab.setPrefix(player, playerData.getLevelPrefix());

                Msg.send(sender, cfg.getString("prefix") + cfg.getString("levelSet"));
                return true;
            }

            if (args[0].equalsIgnoreCase("setprestige")) {
                if (number > 30) {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("maxPrestige"));
                    return true;
                }
                if (number < 0) {
                    Msg.send(sender, cfg.getString("prefix") + cfg.getString("minPrestige"));
                    return true;
                }

                String oldLevelPrefix = playerData.getLevelPrefix();
                playerData.setPrestige(player, (int) number);
                Msg.titleChatLevelUp(player, oldLevelPrefix, playerData.getLevelPrefix());
                Tab.setPrefix(player, playerData.getLevelPrefix());

                Msg.send(sender, cfg.getString("prefix") + cfg.getString("prestigeSet"));
                return true;
            }

            Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
            return true;
        }

        Msg.send(sender, cfg.getString("prefix") + cfg.getString("wrongCommand"));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        Msg.send(sender, "&7&l&m                    &r  &e&lTHE PIT HELP  &7&l&m                    ");
        String cmdPrefix = "&8/&6thepit &e";
        String playerNumber = "&8player &7number";
        String addSet = "&9add&b/&9set ";
        Msg.send(sender, cmdPrefix + "help");
        Msg.send(sender, cmdPrefix + "reload");
        Msg.send(sender, " ");
        Msg.send(sender, cmdPrefix + "gold " + addSet + playerNumber);
        Msg.send(sender, cmdPrefix + "neededXP " + addSet + playerNumber);
        Msg.send(sender, cmdPrefix + "renown " + addSet + playerNumber);
        Msg.send(sender, " ");
        Msg.send(sender, cmdPrefix + "setLevel " + playerNumber);
        Msg.send(sender, cmdPrefix + "setPrestige " + playerNumber);
        Msg.send(sender, " ");
        Msg.send(sender, "&8&l>> &7&oThePit developed by Bufnita");
    }

}