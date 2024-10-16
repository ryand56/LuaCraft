package com.shawnjb.luacraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.shawnjb.luacraft.utils.Vec3;

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
                LuaValue vec3Table = args.checktable(1);
                Vec3 position = Vec3.fromLua(vec3Table);
                setPosition(position);
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
        Vec3 position = new Vec3(loc.getX(), loc.getY(), loc.getZ());
        return position.toLua();
    }

    public void setPosition(Vec3 position) {
        player.teleport(new Location(player.getWorld(), position.x, position.y, position.z));
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

    public void runCommand(String command) {
        Bukkit.dispatchCommand(player, command);
    }
}
