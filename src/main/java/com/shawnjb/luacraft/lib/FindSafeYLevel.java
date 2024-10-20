package com.shawnjb.luacraft.lib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class FindSafeYLevel extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
        LuaTable coordinatesTable = args.checktable(1);
        double x = coordinatesTable.get("x").checkdouble();
        double y = coordinatesTable.get("y").checkdouble();
        double z = coordinatesTable.get("z").checkdouble();
        World world = Bukkit.getWorlds().get(0);
        Location location = new Location(world, x, y, z);
        for (int safeY = world.getMaxHeight(); safeY > 0; safeY--) {
            location.setY(safeY);
            Material blockMaterial = location.getBlock().getType();
            Material blockAboveMaterial = location.clone().add(0, 1, 0).getBlock().getType();
            if (blockMaterial.isSolid() && blockAboveMaterial == Material.AIR) {
                return LuaValue.valueOf(safeY);
            }
        }
        return LuaValue.NIL;
    }
}
