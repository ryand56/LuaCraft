package com.shawnjb.luacraft.lib;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LoadStringFunction extends VarArgFunction {
    private final Globals globals;

    public LoadStringFunction(Globals globals) {
        this.globals = globals;
    }

    @Override
    public Varargs invoke(Varargs args) {
        String luaCode = args.checkjstring(1);
        try {
            LuaValue chunk = globals.load(luaCode, "loadstring");
            return chunk.isfunction() ? chunk : LuaValue.NIL;
        } catch (Exception e) {
            return LuaValue.error("Error in loadstring: " + e.getMessage());
        }
    }

    public static void register(Globals globals) {
        globals.set("loadstring", new LoadStringFunction(globals));
    }
}
