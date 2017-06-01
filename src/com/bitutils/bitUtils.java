package TestID.bitutils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BitUtils extends JavaPlugin{
	public HashMap <String, String> teleport = new HashMap <String, String>();
	public HashMap <String, Location> back = new HashMap <String, Location>();
	
	public void onEnable() {
		//logic on enable
		this.saveDefaultConfig();
	}
	public void onDisable() {
		//logic on disable
		this.saveConfig();
	}
	
	//on death
	
	
	//commands
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args) {
		if(cmd.getName().equalsIgnoreCase("bit")) {
			sender.sendMessage("You called?");
		}
		else if(cmd.getName().equalsIgnoreCase("tpa")) {
			if(args.length != 1){
				sender.sendMessage(ChatColor.AQUA + "Please specify a specific player.");
				sender.sendMessage(ChatColor.AQUA + "/tpa <player>");
				return false;
			}
			Player player = (Player) sender;
			Player player1 = (Player) getServer().getPlayer(args[0]);
			if(player1 == null){
				player.sendMessage(ChatColor.AQUA + "Player "+ args[0] + " is not online");
				return false;
			}
			teleport.put(player1.getName(), player.getName());
			
			
			player.sendMessage(ChatColor.BLUE + "Request sent to " + player1.getName());
			player1.sendMessage(ChatColor.BLUE + player.getName() + " has requested teleportation");
			player1.sendMessage(ChatColor.BLUE + "/tpaccept or /tpdeny");
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("tpaccept")){
			Player player1 = (Player) sender;
			if(teleport.containsKey(player1.getName())){
				String op = player1.getName();
				String requester = teleport.get(op);
				Location location = player1.getLocation();
				back.put(getServer().getPlayer(requester).getName(), getServer().getPlayer(requester).getLocation());
				getServer().getPlayer(requester).teleport(location);
				player1.sendMessage(ChatColor.BLUE + "Teleport request accepted");
				getServer().getPlayer(requester).sendMessage(ChatColor.BLUE +  "Teleporting...");
				teleport.remove(player1.getName());
			}
			else{
				sender.sendMessage(ChatColor.AQUA + "No teleport requests to accept");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("tpdeny")){
			Player player = (Player) sender;
			if(teleport.containsValue(player.getName())){
				sender.sendMessage(ChatColor.BLUE + "Teleportation request denied");
				String player1 = teleport.get(player.getName());
				getServer().getPlayer(player1).sendMessage(ChatColor.AQUA + "Your teleportation request was denied");
				teleport.remove(player.getName());
				return true;
			}else{
				player.sendMessage(ChatColor.AQUA + "No teleportation requests to deny");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("sethome")) {
			Player player = (Player) sender;
			Location home = player.getLocation();
			this.getConfig().set("homes." + sender.getName() + ".x", home.getBlockX());
			this.getConfig().set("homes." + sender.getName() + ".y", home.getBlockY());
			this.getConfig().set("homes." + sender.getName() + ".z", home.getBlockZ());
			this.getConfig().set("homes." + sender.getName() + ".world", home.getWorld().getName());
			player.sendMessage(ChatColor.BLUE + "Home set");
			this.saveConfig();
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("home")) {
			Player player = (Player) sender;
			if(this.getConfig().getString("homes." + sender.getName() + ".world") != null) {
				int x = this.getConfig().getInt("homes." + sender.getName() + ".x");
				int y = this.getConfig().getInt("homes." + sender.getName() + ".y");
				int z = this.getConfig().getInt("homes." + sender.getName() + ".z");
				String worldin = this.getConfig().getString("homes." + sender.getName() + ".world");
				World world = getServer().getWorld(worldin);
				
				Location home = new Location(world,x,y,z);
				back.put(player.getName(), player.getLocation());
				player.teleport(home);
				player.sendMessage(ChatColor.BLUE + "Teleporting...");
				
				return true;
			}
			else {
				player.sendMessage(ChatColor.AQUA + "You haven't set a home");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("buildfly")){
			Player user = (Player) sender;
			if(user.getAllowFlight() == true) {
				user.sendMessage(ChatColor.BLUE + "Flight Disabled");
				user.setAllowFlight(false);
				return true;
			}else{
				user.sendMessage(ChatColor.BLUE + "Flight Enabled");
				user.setAllowFlight(true);
				return true;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("back")){
			Player player = (Player) sender;
			if(back.containsKey(player.getName())){
				Location x = player.getLocation();
				player.teleport(back.get(player.getName()));
				back.put(player.getName(), x);
				player.sendMessage(ChatColor.BLUE + "Teleporting...");
				return true;
			}else{
				player.sendMessage(ChatColor.AQUA + "Your last location wasn't found");
				return false;
			}
		}
		else if(cmd.getName().equalsIgnoreCase("setwarp")){
			if(args.length != 1){
				sender.sendMessage(ChatColor.AQUA + "Please specify a name");
				return false;
			}
			Player player = (Player) sender;
			Location warp = player.getLocation();
			this.getConfig().set("warps." + args[0] + ".x", warp.getBlockX());
			this.getConfig().set("warps." + args[0] + ".y", warp.getBlockY());
			this.getConfig().set("warps." + args[0] + ".z", warp.getBlockZ());
			this.getConfig().set("warps." + args[0] + ".world", warp.getWorld().getName());
			player.sendMessage(ChatColor.BLUE + "Warp set");
			this.saveConfig();
		}
		else if(cmd.getName().equalsIgnoreCase("warp")){
			if(args.length != 1){
				StringBuilder warplist = new StringBuilder();
				for(String key : this.getConfig().getConfigurationSection("warps").getKeys(false)){
					warplist.append("- " + key + " ");
					}
				String messagecont = warplist.toString();
				sender.sendMessage(ChatColor.BLUE + messagecont);
				return true;
			}
				Player player = (Player) sender;
				if(this.getConfig().getString("warps." + args[0] + ".world") != null) {
					int x = this.getConfig().getInt("warps." + args[0] + ".x");
					int y = this.getConfig().getInt("warps." + args[0] + ".y");
					int z = this.getConfig().getInt("warps." + args[0] + ".z");
					String worldin = this.getConfig().getString("warps." + args[0] + ".world");
					World world = getServer().getWorld(worldin);
					
					Location warp = new Location(world,x,y,z);
					back.put(player.getName(), player.getLocation());
					player.teleport(warp);
					player.sendMessage(ChatColor.BLUE + "Teleporting...");
					
					return true;
				}
				else {
					player.sendMessage(ChatColor.AQUA + "There isn't a warp for \'" + args[0] + "\'");
				}
			
		}
		else if(cmd.getName().equalsIgnoreCase("delwarp")){
			if(args.length != 1){
				sender.sendMessage(ChatColor.AQUA + "Please specify a warp");
				return false;
			}
			if(this.getConfig().getString("warps." + args[0]) != null) {
				this.getConfig().set("warps." + args[0], null);
				return true;
			}
		}
		return true;
	}
}

