package com.hotmail.ooosssososos;

import java.util.ArrayList;
import java.util.logging.Logger;





import net.minecraft.server.v1_5_R2.DataWatcher;
import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.PotionBrewer;

import org.bukkit.Color;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Shield extends JavaPlugin implements Listener{
	Plugin plugin;
	protected Logger log;
	private static ArrayList<String> blocking = new ArrayList<String>();
	public int duration = 10;
	public int hungerCost = 1;
	public int durability = 25;
	public String name = "Shield";
	int Color = 0x00FF3C;
	public FireworkEffectPlayer fwp = new FireworkEffectPlayer();
	protected UpdateCheck check;
	public void onEnable(){
		
		log = this.getLogger();
		this.check = new UpdateCheck(this,"http://dev.bukkit.org/server-mods/shield-plus/files.rss");
		if(check.updateNeeded()){
			this.log.info("A new version is available: " + this.check.getVersion());
			this.log.info("get it here : " + this.check.getLink());
		}
		FileConfiguration conf = this.getConfig();
		conf.addDefault("blockDuration", 1);
		conf.addDefault("hungerCost", 1);
		conf.addDefault("Default Durability", 25);
		conf.addDefault("nameOfItem", "Shield");
		conf.addDefault("Color", 0x00FF3C);
		conf.addDefault("sound", true);
		conf.addDefault("Fireworks", true);
		conf.addDefault("damageAfterHunger", true);
		conf.options().copyDefaults(true);
		this.saveConfig();
		duration = conf.getInt("blockDuration");
		hungerCost = conf.getInt("hungerCost");
		durability = conf.getInt("Default Durability");
		name = conf.getString("nameOfItem");
		Color = conf.getInt("Color");
		
		
		plugin = this;
		ItemStack rec = new ItemStack(34, 1);
		ItemMeta a = rec.getItemMeta();
		a.setDisplayName(name +" " + durability +" /" + durability);
		rec.setItemMeta(a);
		
		ShapedRecipe grinderRecipe = new ShapedRecipe(rec).shape("bib", "iri", "bib").setIngredient('b', Material.CLAY_BRICK).setIngredient('i', Material.IRON_INGOT).setIngredient('r', Material.REDSTONE);
		getServer().addRecipe(grinderRecipe);
		this.getServer().getPluginManager().registerEvents(this, this);	
	}
	@EventHandler
	public void onClick(final PlayerInteractEvent e){
		boolean d = false;
		if(blocking.contains(e.getPlayer().getName()))return;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if( e.getPlayer().hasPermission(new Permission("Shield.block"))){
			if(e.getItem()!= null){
			if(e.getItem().getTypeId() == 34){
				if(e.getPlayer().getFoodLevel() >= 1){
			e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel()-hungerCost);
			d = true;
				}else{
					if(getConfig().getBoolean("damageAfterHunger")){
					e.getPlayer().setHealth(e.getPlayer().getHealth() -hungerCost);
					d = true;
					}
					
				}
				if(d == true){
					blocking.add(e.getPlayer().getName());
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		            public void run() {
		               blocking.remove(e.getPlayer().getName());
		            }
		        }, duration*20);
			addPotionGraphicalEffect(e.getPlayer(),Color,duration*20);
			}
			}
			}
			}
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			
			Player p = (Player)e.getEntity();
			
			if(p.getItemInHand().getTypeId() == 34){
				if(e.getDamager() instanceof Arrow){
					if(blocking.contains(p.getName())){
					int dura = Integer.parseInt(p.getItemInHand().getItemMeta().getDisplayName().split(" ")[1]);
					String durab = p.getItemInHand().getItemMeta().getDisplayName().split(" ")[2];
					ItemStack rec = p.getItemInHand();
					ItemMeta a = rec.getItemMeta();
					dura --;
					a.setDisplayName(name + " " + dura + " "+ durab);
					rec.setItemMeta(a);
					if(dura <= 0){
						p.setItemInHand(null);
					}else{
					p.setItemInHand(rec);
					}
					e.setCancelled(true);
					playEffects(p);
					}
				}else if(p.getLocation().getDirection().dot(e.getDamager().getLocation().getDirection()) < 0){
					if(blocking.contains(p.getName())){
					int dura = Integer.parseInt(p.getItemInHand().getItemMeta().getDisplayName().split(" ")[1]);
					String durab = p.getItemInHand().getItemMeta().getDisplayName().split(" ")[2];
					ItemStack rec = p.getItemInHand();
					ItemMeta a = rec.getItemMeta();
					dura --;
					a.setDisplayName(name + " " + dura + " "+ durab);
					rec.setItemMeta(a);
					if(dura <= 0){
						p.setItemInHand(null);
					}else{
					p.setItemInHand(rec);
					}
					e.setCancelled(true);
					playEffects(p);
					}
				}
			}
			if(!p.hasPermission(new Permission("Shield.Sblock")))return;
			if(p.isBlocking()){
				if(p.getLocation().getDirection().dot(e.getDamager().getLocation().getDirection()) < 0){
					e.setCancelled(true);
					playEffects(p);
			}
			}
		}
	}
	public void playEffects(Player p){
		if(this.getConfig().getBoolean("sound")){
		p.getWorld().playEffect(p.getLocation(), Effect.ZOMBIE_DESTROY_DOOR,0);
		}
		if(this.getConfig().getBoolean("Fireworks")){
		try {
			fwp.playFirework(p.getWorld(), p.getLocation().add(0, 1, 0), FireworkEffect.builder().withColor(org.bukkit.Color.fromRGB(255, 213, 0)).withFlicker().with(Type.BALL).build());
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}
	   public void addPotionGraphicalEffect(LivingEntity entity, int color, int duration) {
	        final EntityLiving el = ((CraftLivingEntity)entity).getHandle();
	        final DataWatcher dw = el.getDataWatcher();
	        dw.watch(8, Integer.valueOf(color));
	 
	        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	            public void run() {
	                int c = 0;
	                if (!el.effects.isEmpty()) {
	                    c = PotionBrewer.a(el.effects.values());
	                }
	                dw.watch(8, Integer.valueOf(c));
	                
	            }
	        }, duration);
	    }
	 
}
