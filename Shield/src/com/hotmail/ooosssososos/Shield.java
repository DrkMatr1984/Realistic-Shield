package com.hotmail.ooosssososos;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import net.minecraft.server.v1_6_R2.DataWatcher;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.PotionBrewer;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Shield extends JavaPlugin implements Listener{
    ShapedRecipe grinderRecipe;
    ItemStack rec;
	Plugin plugin;
	protected Logger log;
	private static ArrayList<String> blocking = new ArrayList<String>();
	public int duration = 10;
	public int hungerCost = 1;
	public String name = "Shield";
	int Color = 0x00FF3C;
	public FireworkEffectPlayer fwp = new FireworkEffectPlayer();
	protected UpdateCheck check;
    public void onDisable(){
        this.saveConfig();
    }
	public void onEnable(){
		FileConfiguration conf = this.getConfig();
		conf.addDefault("enableUpdateDetect", true);
		conf.addDefault("blockDuration", 1);
		conf.addDefault("hungerCost", 1);
		conf.addDefault("nameOfItem", "Shield");
		conf.addDefault("Color", "0x00FF3C");
		conf.addDefault("sound", true);
		conf.addDefault("Fireworks", true);
		conf.addDefault("enableUpdateDetect", true);
		conf.addDefault("damageAfterHunger", true);
		conf.addDefault("Tiers", 1);

		conf.options().copyDefaults(true);
		this.saveConfig();
		log = this.getLogger();
		if(conf.getBoolean("enableUpdateDetect")){
		this.check = new UpdateCheck(this,"http://dev.bukkit.org/server-mods/shield-plus/files.rss");
		if(check.updateNeeded()){
			this.log.info("A new version is available: " + this.check.getVersion());
			this.log.info("get it here : " + this.check.getLink());
		}
		}
		
		duration = conf.getInt("blockDuration");
		hungerCost = conf.getInt("hungerCost");
		name = conf.getString("nameOfItem");
		//Color = conf.getInt("Color");
		
		
		plugin = this;
        for(int i = 1; i <= conf.getInt("Tiers");i++){
            int t = conf.getInt("data.Tier"+i+".ID");
            if(t != 0){
                rec = new ItemStack(t,1);
                ItemMeta a = rec.getItemMeta();
                ArrayList<String> temp = new ArrayList<String>();
                for(String b :conf.getString("data.Tier"+i+".lore").split("/n")){
                    temp.add(ChatColor.BOLD + b);
                }
                temp.add("Durability: " + conf.getInt("data.Tier" + i+".Durability") + " / "+ conf.getInt("data.Tier" + i+".Durability") + " ◄");
                a.setDisplayName(ChatColor.BOLD + conf.getString("data.Tier"+i+".name"));
                a.setLore(temp);


                rec.setItemMeta(a);
                grinderRecipe = new ShapedRecipe(rec).shape("abc", "def", "ghi").setIngredient('a', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.1")))
                        .setIngredient('b',Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.2")))
                        .setIngredient('c', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.3")))
                        .setIngredient('d', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.4")))
                        .setIngredient('e', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.5")))
                        .setIngredient('f', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.6")))
                        .setIngredient('g', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.7")))
                        .setIngredient('h', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.8")))
                        .setIngredient('i', Material.getMaterial(conf.getInt("data.Tier" + i + ".recipe.9")));

                getServer().addRecipe(grinderRecipe);
            }else{
                this.getLogger().info("generated defualts");
                conf.set("data.Tier"+i+".ID",34);
                conf.set("data.Tier"+i+".name","Shield");
                conf.set("data.Tier"+i+".Durability",10);
                conf.set("data.Tier"+i+".lore", "");
                conf.set("data.Tier"+i+".recipe.1",336);
                conf.set("data.Tier"+i+".recipe.2",265);
                conf.set("data.Tier"+i+".recipe.3",336);
                conf.set("data.Tier"+i+".recipe.4",265);
                conf.set("data.Tier"+i+".recipe.5",331);
                conf.set("data.Tier"+i+".recipe.6",265);
                conf.set("data.Tier"+i+".recipe.7",365);
                conf.set("data.Tier"+i+".recipe.8",265);
                conf.set("data.Tier"+i+".recipe.9",336);

            }

        }


		
		this.getServer().getPluginManager().registerEvents(this, this);	
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if(args[0] == null){
            p.getInventory().addItem(rec);
        }else{
            try{
            Bukkit.getServer().getPlayer(args[0]).getInventory().addItem(rec);
            }catch(NullPointerException e){
                sender.sendMessage(ChatColor.RED + "[RealisticShield] The Target Player doesn't exist or is not online");
            }
        }
        return true;
    }
	@EventHandler
	public void onClick(final PlayerInteractEvent e){
		
		if(blocking.contains(e.getPlayer().getName()))return;
		boolean d = false;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if( e.getPlayer().hasPermission("Shield.block")){
			if(e.getItem()!= null){
			if(e.getItem().getItemMeta().hasLore() &&e.getItem().getItemMeta().getLore().get(e.getItem().getItemMeta().getLore().size()-1).endsWith("◄")){
				if(e.getPlayer().getFoodLevel() >= 1){
			e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel()-hungerCost);

			d = true;
				}else{
					if(getConfig().getBoolean("damageAfterHunger")){
					e.getPlayer().setHealth((double)(((CraftPlayer)e.getPlayer()).getHandle().getHealth() -hungerCost));

					d = true;
					}
					
				}

				if(d == true){
					blocking.add(e.getPlayer().getName());
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
		            public void run() {
		               blocking.remove(e.getPlayer().getName());
		            }
		        }, duration*20);
            try{
			addPotionGraphicalEffect(e.getPlayer(),0x00FF3C,duration*20);
            }catch(Exception be){
            }

			}
			}
			}
			}
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent e){
		if(!(e.getEntity() instanceof Player))return;
			
			Player p = (Player)e.getEntity();
			
			if(p.getItemInHand().getTypeId() == 34){
				if(e.getDamager() instanceof Arrow){
					if(blocking.contains(p.getName())){
					int dura = Integer.parseInt(p.getItemInHand().getItemMeta().getLore().get(p.getItemInHand().getItemMeta().getLore().size()-1).split(" ")[1]);
					String durab = p.getItemInHand().getItemMeta().getLore().get(p.getItemInHand().getItemMeta().getLore().size() - 1).split(" ")[2];
					ItemStack rec = p.getItemInHand();
					ItemMeta a = rec.getItemMeta();
					dura --;
                        List<String> dattemp = a.getLore().subList(0,a.getLore().size()-1);
                        dattemp.add("Durability: " + dura + " / " + durab + " ◄");

                        a.setLore(dattemp);
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
                        int dura = Integer.parseInt(p.getItemInHand().getItemMeta().getLore().get(p.getItemInHand().getItemMeta().getLore().size()-1).split(" ")[1]);
                        String durab = p.getItemInHand().getItemMeta().getLore().get(p.getItemInHand().getItemMeta().getLore().size() - 1).split(" ")[3];
					ItemStack rec = p.getItemInHand();
					ItemMeta a = rec.getItemMeta();
					dura --;
                        List<String> dattemp = a.getLore().subList(0,a.getLore().size()-1);
                        dattemp.add("Durability: " + dura + " / " + durab + " ◄");

                        a.setLore(dattemp);


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
					p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() - 5));
			}
			}
		
	}
    @EventHandler
    public void craftEvent(CraftItemEvent event){
        if(event.getRecipe().getResult().equals(rec)){
            if(!event.getView().getPlayer().hasPermission("Shield.craft")){
                ((Player)event.getView().getPlayer()).sendMessage("You do not have permission to craft a shield");
                event.setCancelled(true);
            }else{
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
        dw.watch(7, Integer.valueOf(color));

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                int c = 0;
                if (!el.effects.isEmpty()) {

                    c = PotionBrewer.a(el.effects.values());
                }
                dw.watch(7, Integer.valueOf(c));
            }
        }, duration);
    }
	 
}
