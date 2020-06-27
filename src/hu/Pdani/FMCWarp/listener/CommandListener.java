package hu.Pdani.FMCWarp.listener;

import hu.Pdani.FMCWarp.FMCWarpPlugin;
import hu.Pdani.FMCWarp.manager.GuiManager;
import hu.Pdani.FMCWarp.manager.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static hu.Pdani.FMCWarp.FMCWarpPlugin.c;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("In-game only.");
            return true;
        }
        String[] user = {"about","exit"};
        if(cmd.getName().equalsIgnoreCase("fmcwarp")) {
            String version = FMCWarpPlugin.getPlugin().getDescription().getVersion();
            List<String> authors = FMCWarpPlugin.getPlugin().getDescription().getAuthors();
            String longauthors = FMCWarpPlugin.getPlugin().getDescription().getAuthors().stream().collect(Collectors.joining(", ", "", ""));
            String description = FMCWarpPlugin.getPlugin().getDescription().getDescription();
            Player player = (Player) sender;
            if (!sender.hasPermission("fmcwarp.admin")) {
                if(args.length == 0 || (args.length == 1 && !Arrays.asList(user).contains(args[0].toLowerCase()))){
                    player.sendMessage(c("&eFMCWarp plugin v"+version+", created by "+authors.get(0)));
                }
                if(args[0].equalsIgnoreCase("about")) {
                    player.sendMessage(c("&6&lFMCWarp plugin"));
                    player.sendMessage(c("&a " + description));
                    player.sendMessage(c("&eVersion: " + version));
                    player.sendMessage(c("&eAuthors: " + longauthors));
                } else if(args[0].equalsIgnoreCase("exit")){
                    WarpManager wm = FMCWarpPlugin.getWarpManager();
                    if(!wm.lastpos.containsKey(player))
                        return true;
                    if(!FMCWarpPlugin.getPlugin().getConfig().getBoolean("gui.disableexit",false))
                        player.getInventory().setItem(8,null);
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player.teleport(wm.lastpos.remove(player));
                }
                return true;
            }
            if(args.length == 0 || (args[0].equalsIgnoreCase("help"))){
                player.sendMessage(c("&eFMCWarp plugin v"+version+", created by "+authors.get(0)));
                player.sendMessage(c("&7- &9/fmcwarp setup &7- &6Register a new block/npc as a menu opener"));
                player.sendMessage(c("&7- &9/fmcwarp add <name> &7- &6Add a new warp"));
                player.sendMessage(c("&7- &9/fmcwarp del <name> &7- &6Delete a warp"));
                player.sendMessage(c("&7- &9/fmcwarp list &7- &6List all warps"));
                player.sendMessage(c("&7- &9/fmcwarp tp <name> &7- &6Teleport to a warp"));
                player.sendMessage(c("&7- &9/fmcwarp reload &7- &6Reload all files (warps,interacts)"));
                player.sendMessage(c("&7- &9/fmcwarp help &7- &6Display this list"));
                player.sendMessage(c("&7- &9/fmcwarp about &7- &6Information about the plugin"));
                return true;
            }
            WarpManager wm = FMCWarpPlugin.getWarpManager();
            if(args[0].equalsIgnoreCase("setup")) {
                GuiManager gm = FMCWarpPlugin.getGuiManager();
                if (!gm.setuplist.contains(player)) {
                    gm.setuplist.add(player);
                    player.sendMessage(c("&4Entering setup mode..."));
                    if (FMCWarpPlugin.hasCitizens()) {
                        player.sendMessage(c("&eRight click on a block or a Citizens NPC to register it as a menu opener!"));
                        player.sendMessage(c("&eLeft click on a block or a Citizens NPC to remove it!"));
                    } else {
                        player.sendMessage(c("&eRight click on a block to register it as a menu opener!"));
                        player.sendMessage(c("&eLeft click on a block to remove it!"));
                    }
                    player.sendMessage(c("&cTo cancel the setup enter &f/"+s+" setup&c again!"));
                } else {
                    gm.setuplist.remove(player);
                    player.sendMessage(c("&cSetup cancelled."));
                }
            } else if(args[0].equalsIgnoreCase("about")){
                player.sendMessage(c("&6&lFMCWarp plugin"));
                player.sendMessage(c("&a "+description));
                player.sendMessage(c("&eVersion: "+version));
                player.sendMessage(c("&eAuthors: "+longauthors));
            } else if(args[0].equalsIgnoreCase("reload")){
                wm.reloadWarps();
                FMCWarpPlugin.getGuiManager().reloadInteracts();
                FMCWarpPlugin.getPlugin().reloadConfig();
                player.sendMessage(c("&aAll files are reloaded!"));
            } else if(args[0].equalsIgnoreCase("add")){
                if(args.length < 2){
                    player.sendMessage(c("&aUsage: /"+s+" add <name>"));
                } else {
                    if(!wm.isWarp(args[1].toLowerCase())) {
                        wm.addWarp(args[1].toLowerCase(),player.getLocation());
                        player.sendMessage(c("&aWarp '"+args[1].toLowerCase()+"' created!"));
                    } else {
                        player.sendMessage(c("&cThis warp already exists"));
                    }
                }
            } else if(args[0].equalsIgnoreCase("del")){
                if(args.length < 2){
                    player.sendMessage(c("&aUsage: /"+s+" del <name>"));
                } else {
                    if(wm.isWarp(args[1].toLowerCase())) {
                        wm.delWarp(args[1].toLowerCase());
                        player.sendMessage(c("&aWarp '"+args[1].toLowerCase()+"' deleted!"));
                    } else {
                        player.sendMessage(c("&cThis warp does not exists"));
                    }
                }
            } else if(args[0].equalsIgnoreCase("tp")){
                if(args.length < 2){
                    player.sendMessage(c("&aUsage: /"+s+" tp <name>"));
                } else {
                    if(!wm.isWarp(args[1])){
                        player.sendMessage(c("&cWarp not found: "+args[1]));
                        return true;
                    }
                    Location loc = wm.getWarpLocation(args[1]);
                    if(!FMCWarpPlugin.getPlugin().getServer().getWorlds().contains(loc.getWorld())){
                        player.sendMessage(c("&cInvalid world: "+loc.getWorld().getName()));
                        return true;
                    }
                    player.sendMessage(c("&aTeleporting..."));
                    player.teleport(loc);
                }
            } else if(args[0].equalsIgnoreCase("list")){
                StringBuilder sb = new StringBuilder();
                for(String n : wm.getWarplist().keySet()){
                    if(sb.length() == 0){
                        sb.append(c("&d"+n));
                    } else {
                        sb.append(c("&7, ")).append(c("&d"+n));
                    }
                }
                if(sb.length() > 0) {
                    player.sendMessage(c("&eWarps:"));
                    player.sendMessage(sb.toString());
                } else {
                    player.sendMessage(c("&cNo warps are found."));
                }
            } else if(args[0].equalsIgnoreCase("exit")){
                if(!wm.lastpos.containsKey(player))
                    return true;
                if(!FMCWarpPlugin.getPlugin().getConfig().getBoolean("gui.disableexit",false))
                    player.getInventory().setItem(8,null);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                player.teleport(wm.lastpos.remove(player));
            }
            return true;
        }
        return true;
    }
}
