package com.joiubas.prisongangs.utility.interaction;

import com.joiubas.prisongangs.PrisonGangs;
import com.joiubas.prisongangs.utility.ConfigManager;
import com.joiubas.prisongangs.utility.PrisonGang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player p = (Player) sender;

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length == 2) {

                        if (!PrisonGang.inGang(p)) {
                            long gangPrice = 10000000000L;

                            if (PrisonGangs.economy.getBalance(p) >= gangPrice) {
                                PrisonGangs.economy.withdrawPlayer(p, gangPrice);
                                ConfigManager.createGang(args[1], p);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.gang_created").replaceAll("%gang_name%", args[1])));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_enough_money")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.in_gang")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_enough_create_args")));
                    }
                } else if (args[0].equalsIgnoreCase("reward")) {

                    if (PrisonGang.inGang(p)) {
                        PrisonGang pGang = PrisonGang.getGang(p);

                        if (!ConfigManager.getConfig().contains("claimed." + p.getUniqueId().toString())) {
                            if (ConfigManager.getConfig().contains("rewards.level" + String.valueOf(pGang.level))) {
                                List<String> commands = ConfigManager.getConfig().getStringList("rewards.level" + String.valueOf(1));

                                for (String commandz : commands) {
                                    ConfigManager.getConfig().set("claimed." + p.getUniqueId().toString(), String.valueOf(System.currentTimeMillis()));

                                    ConfigManager.saveConfig();

                                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                    Bukkit.dispatchCommand(console, commandz.replaceAll("%player%", p.getName()));

                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.reward_claimed")));
                                }
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.no_reward_for_level")));
                            }
                        } else {

                            if (Long.valueOf(ConfigManager.getConfig().getString("claimed." + p.getUniqueId().toString())) < System.currentTimeMillis() - 86400000L ) {
                                if (ConfigManager.getConfig().contains("rewards.level" + String.valueOf(pGang.level))) {
                                    List<String> commands = ConfigManager.getConfig().getStringList("rewards.level" + String.valueOf(1));

                                    for (String commandz : commands) {
                                        ConfigManager.getConfig().set("claimed." + p.getUniqueId().toString(), String.valueOf(System.currentTimeMillis()));

                                        ConfigManager.saveConfig();

                                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                        Bukkit.dispatchCommand(console, commandz);

                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.reward_claimed")));

                                    }
                                } else {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.no_reward_for_level")));
                                }
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.hours_not_passed")));
                            }
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                    }

                } else if (args[0].equalsIgnoreCase("home")) {
                    if (PrisonGang.inGang(p)) {
                        PrisonGang pGang = PrisonGang.getGang(p);
                        if (pGang.location != null) {
                            p.teleport(pGang.location);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.teleporting_home")));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.home_not_set")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                    }
                } else if (args[0].equalsIgnoreCase("sethome")) {
                    if (PrisonGang.inGang(p)) {
                        if (PrisonGang.isOwner(p)) {
                            PrisonGang pGang = PrisonGang.getGang(p);
                            pGang.location = p.getLocation();
                            ConfigManager.getConfig().set("gangs." + pGang.name + ".home" , ConfigManager.getStringLocation(pGang.location));
                            ConfigManager.saveConfig();
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.home_set")));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_gang_owner")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length == 2) {
                        if (PrisonGang.inGang(p)) {
                            if (PrisonGang.isOwner(p)) {
                                PrisonGang pGang = PrisonGang.getGang(p);
                                OfflinePlayer kickedplayer = Bukkit.getOfflinePlayer(args[1]);
                                UUID kickeduuid = kickedplayer.getUniqueId();
                                if (kickeduuid != p.getUniqueId()) {
                                    if (PrisonGang.playerGang.containsKey(kickeduuid)) {
                                        PrisonGang.playerGang.remove(kickeduuid);
                                        pGang.gangPlayers.remove(kickeduuid);
                                        List<String> stringmembers = new ArrayList<>();

                                        for (UUID player : pGang.gangPlayers) {
                                            stringmembers.add(player.toString());
                                        }
                                        ConfigManager.getConfig().set("gangs." + pGang.name + ".members", stringmembers);
                                        ConfigManager.saveConfig();
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.player_kicked")));
                                    } else {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.player_not_gang")));
                                    }
                                }
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_gang_owner")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_enough_create_args")));
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (args.length == 2) {
                        if (PrisonGang.inGang(p)) {
                            if (!PrisonGang.isOwner(p)) {
                                UUID kickeduuid = p.getUniqueId();
                                PrisonGang.playerGang.remove(kickeduuid);
                                PrisonGang pGang = PrisonGang.getGang(p);
                                pGang.gangPlayers.remove(kickeduuid);
                                List<String> stringmembers = new ArrayList<>();

                                for (UUID player : pGang.gangPlayers) {
                                    stringmembers.add(player.toString());
                                }
                                ConfigManager.getConfig().set("gangs." + pGang.name + ".members", stringmembers);
                                ConfigManager.saveConfig();
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.left_gang")));
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.gang_owner")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_enough_create_args")));
                    }
                } else if (args[0].equalsIgnoreCase("invite")) {
                    if (args.length == 2) {
                        if (PrisonGang.inGang(p)) {
                            PrisonGang pGang = PrisonGang.getGang(p);
                            if (pGang.gangPlayers.size() < 15) {
                                Player invitedplayer = Bukkit.getPlayer(args[1]);
                                if (invitedplayer != null) {
                                    UUID inviteduuid = invitedplayer.getUniqueId();
                                    if (!PrisonGang.inGang(invitedplayer)) {
                                        PrisonGang.invite.put(inviteduuid, pGang);
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.invite_sent").replaceAll("%player%", invitedplayer.getName())));
                                        invitedplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.gang_invite").replaceAll("%gangname%", pGang.name)));
                                    } else {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.player_in_gang")));
                                    }
                                } else {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_online")));
                                }
                            } else {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.many_players")));
                            }
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_enough_create_args")));
                    }

                } else if (args[0].equalsIgnoreCase("accept")) {
                    if (PrisonGang.invite.containsKey(p.getUniqueId())) {
                        PrisonGang pGang = PrisonGang.invite.get(p.getUniqueId());
                        PrisonGang.playerGang.put(p.getUniqueId(), pGang);
                        pGang.gangPlayers.add(p.getUniqueId());
                        List<String> stringmembers = new ArrayList<>();

                        for (UUID player : pGang.gangPlayers) {
                            stringmembers.add(player.toString());
                        }
                        ConfigManager.getConfig().set("gangs." + pGang.name + ".members", stringmembers);
                        ConfigManager.saveConfig();
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.joined_gang")));
                    }
                } else if (args[0].equalsIgnoreCase("member")) {

                    if (PrisonGang.playerGang.containsKey(p.getUniqueId())) {

                        PrisonGang pGang = PrisonGang.playerGang.get(p.getUniqueId());

                        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.DARK_GRAY + "Gang Members");

                        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
                        ItemMeta glassMeta = glass.getItemMeta();

                        glassMeta.setDisplayName(" ");
                        glass.setItemMeta(glassMeta);

                        for (int x = 0; x < inv.getSize(); x++) {
                            if (inv.getItem(x) == null) {
                                inv.setItem(x, glass);
                            }
                        }

                        SkullMeta metas = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

                        metas.setOwner("MHF_Question");

                        metas.setDisplayName(" ");

                        ItemStack stacks = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

                        stacks.setItemMeta(metas);

                        for (int x = 1; x <= 15; x++) {
                            switch (x) {
                                case 1:
                                    inv.setItem(2, stacks);
                                    break;
                                case 2:
                                    inv.setItem(3, stacks);
                                    break;
                                case 3:

                                    inv.setItem(4, stacks);
                                    break;
                                case 4:
                                    inv.setItem(5, stacks);
                                    break;
                                case 5:
                                    inv.setItem(6, stacks);
                                    break;
                                case 6:
                                    inv.setItem(11, stacks);
                                    break;
                                case 7:
                                    inv.setItem(12, stacks);
                                    break;
                                case 8:
                                    inv.setItem(13, stacks);
                                    break;
                                case 9:
                                    inv.setItem(14, stacks);
                                    break;
                                case 10:
                                    inv.setItem(15, stacks);
                                    break;
                                case 11:
                                    inv.setItem(20, stacks);
                                    break;
                                case 12:
                                    inv.setItem(21, stacks);
                                    break;
                                case 13:
                                    inv.setItem(22, stacks);
                                    break;
                                case 14:
                                    inv.setItem(23, stacks);
                                    break;
                                case 15:
                                    inv.setItem(24, stacks);
                                    break;

                            }
                        }

                        int x = 1;

                        for (UUID player : pGang.gangPlayers) {
                            SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

                            meta.setOwner(Bukkit.getOfflinePlayer(player).getName());

                            ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

                            stack.setItemMeta(meta);

                            switch (x) {
                                case 1:
                                    inv.setItem(2, stack);
                                    break;
                                case 2:
                                    inv.setItem(3, stack);
                                    break;
                                case 3:
                                    inv.setItem(4, stack);
                                    break;
                                case 4:
                                    inv.setItem(5, stack);
                                    break;
                                case 5:
                                    inv.setItem(6, stack);
                                    break;
                                case 6:
                                    inv.setItem(11, stack);
                                    break;
                                case 7:
                                    inv.setItem(12, stack);
                                    break;
                                case 8:
                                    inv.setItem(13, stack);
                                    break;
                                case 9:
                                    inv.setItem(14, stack);
                                    break;
                                case 10:
                                    inv.setItem(15, stack);
                                    break;
                                case 11:
                                    inv.setItem(20, stack);
                                    break;
                                case 12:
                                    inv.setItem(21, stack);
                                    break;
                                case 13:
                                    inv.setItem(22, stack);
                                    break;
                                case 14:
                                    inv.setItem(23, stack);
                                    break;
                                case 15:
                                    inv.setItem(24, stack);
                                    break;

                            }
                            x++;
                        }

                        p.openInventory(inv);
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.not_in_gang")));
                    }
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.getLangConfig().getString("messages.unknown_command")));
            }
        } else {
            sender.sendMessage("PrisonGangs > Console commands are not supported, please execute commands as a Player.");
            return true;
        }
        return false;

    }
}

