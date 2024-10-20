package com.shawnjb.luacraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Locale;
import java.util.UUID;

public class LuaCraftPlayer {
    private final Player player;

    public LuaCraftPlayer(Player player) {
        this.player = player;
    }

	public Player getPlayer() {
        return this.player;
    }

    public LuaValue toLuaValue() {
        LuaValue playerTable = LuaValue.tableOf();

        playerTable.set("giveItem", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                giveItem(args.checkjstring(1), args.checkint(2));
                return LuaValue.NIL;
            }
        });

        playerTable.set("sendMessage", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                sendMessage(args.checkjstring(1));
                return LuaValue.NIL;
            }
        });

        playerTable.set("getPosition", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return getPosition();
            }
        });

		playerTable.set("setPosition", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaValue positionTable = args.checktable(1);
				
				double x = positionTable.get("x").checkdouble();
				double y = positionTable.get("y").checkdouble();
				double z = positionTable.get("z").checkdouble();
				
				setPosition(x, y, z);
				
				return LuaValue.NIL;
			}
		});

		playerTable.set("applyEffect", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				String effectName = args.checkjstring(1);
				
				int duration = args.narg() >= 2 ? args.optint(2, Integer.MAX_VALUE) : Integer.MAX_VALUE;
				int amplifier = args.narg() >= 3 ? args.optint(3, 1) : 1;  // Default amplifier: 1
		
				applyEffect(effectName, duration, amplifier);
				return LuaValue.NIL;
			}
		});		
		
		playerTable.set("clearEffects", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				clearEffects();
				return LuaValue.NIL;
			}
		});

		playerTable.set("explode", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                explode();
                return LuaValue.NIL;
            }
        });

        playerTable.set("ignite", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                int seconds = args.optint(1, 5);
                ignite(seconds);
                return LuaValue.NIL;
            }
        });

        playerTable.set("name", LuaValue.valueOf(player.getName()));
        playerTable.set("uuid", LuaValue.valueOf(player.getUniqueId().toString()));
        playerTable.set("isOnline", LuaValue.valueOf(player.isOnline()));

        return playerTable;
    }

    public static LuaCraftPlayer fromLuaValue(LuaValue value) {
        if (value.istable()) {
            LuaValue uuidValue = value.get("uuid");
            if (!uuidValue.isnil()) {
                try {
                    UUID uuid = UUID.fromString(uuidValue.tojstring());
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        return new LuaCraftPlayer(player);
                    }
                } catch (IllegalArgumentException e) {}
            }

            LuaValue playerName = value.get("name");
            if (!playerName.isnil()) {
                Player player = Bukkit.getPlayer(playerName.tojstring());
                if (player != null) {
                    return new LuaCraftPlayer(player);
                }
            }
        }
        return null;
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

	public LuaValue getPosition() {
		Location loc = player.getLocation();
		
		LuaTable positionTable = LuaValue.tableOf();
		positionTable.set("x", LuaValue.valueOf(loc.getX()));
		positionTable.set("y", LuaValue.valueOf(loc.getY()));
		positionTable.set("z", LuaValue.valueOf(loc.getZ()));
		
		return positionTable;
	}

	public void setPosition(double x, double y, double z) {
		Location newLocation = new Location(player.getWorld(), x, y, z);
		player.teleport(newLocation);
	}	

    public void giveItem(String itemName, int amount) {
        Material material = Material.getMaterial(itemName.toUpperCase());
        if (material != null) {
            player.getInventory().addItem(new ItemStack(material, amount));
            player.sendMessage("You received " + amount + " " + itemName + "(s).");
        } else {
            player.sendMessage("Invalid item: " + itemName);
        }
    }

	public void applyEffect(String effectName) {
		applyEffect(effectName, Integer.MAX_VALUE, 1);
	}
	
	public void applyEffect(String effectName, int duration) {
		applyEffect(effectName, duration, 1);
	}
	
	public void applyEffect(String effectName, int duration, int amplifier) {
		NamespacedKey key = NamespacedKey.minecraft(effectName.toLowerCase(Locale.ROOT));
		PotionEffectType effectType = Registry.EFFECT.get(key);
	
		if (effectType != null) {
			PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
			player.addPotionEffect(potionEffect);
			player.sendMessage("Effect " + effectName + " applied for " + (duration == Integer.MAX_VALUE ? "infinite" : (duration / 20) + " seconds") + ".");
		} else {
			player.sendMessage("Invalid effect: " + effectName);
		}
	}	

	public void clearEffects() {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.sendMessage("All effects cleared.");
	}

    public void explode() {
        Location loc = player.getLocation();
        player.getWorld().createExplosion(loc, 4.0F, false, false);
        player.sendMessage("Boom! You have been exploded.");
    }

    public void ignite(int seconds) {
        player.setFireTicks(seconds * 20);
        player.sendMessage("You are now on fire for " + seconds + " seconds.");
    }

    public void runCommand(String command) {
        Bukkit.dispatchCommand(player, command);
    }
}
