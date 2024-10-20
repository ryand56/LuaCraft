package com.shawnjb.luacraft.lib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class IsLocationSubmerged extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
        LuaTable coordinatesTable = args.checktable(1);
        double x = coordinatesTable.get("x").checkdouble();
        double y = coordinatesTable.get("y").checkdouble();
        double z = coordinatesTable.get("z").checkdouble();
        World world = Bukkit.getWorlds().get(0);
        Location location = new Location(world, x, y, z);
        Material blockMaterial = location.getBlock().getType();
        if (blockMaterial == Material.WATER || blockMaterial == Material.LAVA) {
            return LuaValue.TRUE;
        }
        return LuaValue.FALSE;
    }
}
