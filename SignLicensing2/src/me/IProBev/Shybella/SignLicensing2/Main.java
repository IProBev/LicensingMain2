package me.IProBev.Shybella.SignLicensing2;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Main extends JavaPlugin implements Listener{
      
       public static Main plugin;
       public final HashMap<Location, String> signs = new HashMap<Location, String>();
      
       //-----------------//
       //Setting up Vault.//
       //-----------------//
       public static Economy economy = null;

   private boolean setupEconomy()
   {
       RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
       if (economyProvider != null) {
           economy = economyProvider.getProvider();
       }

       return (economy != null);
   }
      
       @Override
       public void onEnable() {
               if(!setupEconomy()){
                       getLogger().severe("PLUGIN REQUIRES VAULT, ERROR STARTING!");
                       Bukkit.getPluginManager().disablePlugin(this);
               }else{
                       getLogger().info("SignLicensing Has Been Enabled");
                       getServer().getPluginManager().registerEvents(this, this);

               }
       }
       @Override
       public void onDisable() {
               getLogger().info("SignLicensing Has Been Disabled");
       }
      
       String Prefix = ChatColor.BLACK + "[" + ChatColor.AQUA + "SignLicenseing" + ChatColor.BLACK + "]" + ChatColor.RED;
       String BalLow = Prefix + "Transaction Unsuccessful! You do not have enough money to purchase that License";
       String Sccuessful = Prefix + "Trasnaction Sccuessful! You now have permission to craft: ";

       @EventHandler
       public void onSignChange(SignChangeEvent sign){
               Player player = sign.getPlayer();
               player.chat("1");
               if(sign.getLine(0).equalsIgnoreCase("License")){
                       if(player.hasPermission("License.create") || player.isOp()){
                               signs.put(sign.getBlock().getLocation(), sign.getPlayer().getName());
                               sign.setLine(0, "§0[§bLicense§0]");
                               String price = sign.getLine(1);
                               sign.setLine(1, "$" + price);
                            		   
                               player.sendMessage(Prefix + "Sign Created sucessfully!");
             
                       }else{
                               sign.setCancelled(true);
                               player.sendMessage(Prefix + "You DO NOT have permission to do that!");
                       }
               }
       }

       
       @EventHandler
       public void onBlockBreak(BlockBreakEvent event){
               Player player = event.getPlayer();
               if(signs.containsKey(event.getBlock().getLocation())
                               && !signs.containsValue(event.getPlayer().getName())
                               || !player.isOp()
                               || !player.hasPermission("License.remove")){
                       event.setCancelled(true);
                       player.sendMessage(Prefix + "You DO NOT have permission to do that!");
               }else{
                       signs.remove(event.getBlock().getLocation());
                       player.sendMessage(Prefix + "Sign Sucessfully removed!");
               }
       }
       @EventHandler
   		public void onSignClick(PlayerInteractEvent event) {
   				Player player = event.getPlayer();
   				if (event.getClickedBlock().getType() == Material.SIGN) {	  
   					if (!player.hasPermission("license.use")) {
   						Sign s = (Sign)(event.getClickedBlock().getState());
   						String[] lines = s.getLines();
   						for (String line : lines) {
   						if (s.getLine(0).contains("[License]")) {	
   							EconomyResponse er = economy.withdrawPlayer(player.getName(),Integer.parseInt(s.getLine(1)));
   							if (er.transactionSuccess()){
   								player.sendMessage(Sccuessful + s.getLine(3));
   								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user "+ player.getName().toString() + " add tekkitcustomizer." + s.getLine(2).toString() + ":*");		
   							}else{
   								player.sendMessage(BalLow);
   							}
                                      
   						}
   						
   						}
   					}
   				
   				}
   			
       }
       
}
