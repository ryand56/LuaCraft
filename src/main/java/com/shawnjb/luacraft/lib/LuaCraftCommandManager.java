package com.shawnjb.luacraft.lib;

import com.shawnjb.luacraft.LuaCraft;
import com.shawnjb.luacraft.utils.LuaValueConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaCraftCommandManager implements Listener {
	private final String commandPrefix = "luacraft"; // set just in case needs to be changed
	private final LuaCraft plugin;
	private final Map<String, Pair<LuaValue, Command>> commandList = new HashMap<>();

	public LuaCraftCommandManager(LuaCraft plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public LuaValue create() {
		LuaValue eventTable = LuaValue.tableOf();

		eventTable.set("registerCommand", new RegisterCommand());
		eventTable.set("getRegisteredCommands", new GetRegisteredCommands());
		eventTable.set("unregisterCommand", new UnregisterCommand());

		return eventTable;
	}

	class RegisterCommand extends VarArgFunction {
		@Override
		public Varargs invoke(Varargs args) {
			String commandName = args.checkjstring(1);
			LuaValue function = args.checkfunction(2);
			LuaValue tabCompleteFunction = args.optfunction(3, null);
			String description = args.optjstring(4, "");
			String usage = args.optjstring(5, "/"+commandName);
			List<String> aliases = ((List<?>) LuaValueConverter.toJava(args.opttable(6, LuaTable.tableOf())))
				.stream().map(Object::toString).toList();
			if (commandList.containsKey(commandName)) {
				Command command = commandList.get(commandName).getRight();
				if (command != null) {
					unregister(command);
				}
				commandList.remove(commandName);
			}
			Command command = new Command(commandName, description, usage, aliases) {
				@Override
				public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArgs) {
					LuaValue[] values = new LuaValue[cmdArgs.length];
					for (int i = 0; i < cmdArgs.length; i++) {
						values[i] = LuaValue.valueOf(cmdArgs[i]);
					}
					function.invoke(LuaValue.valueOf(commandLabel), LuaValue.valueOf(sender.getName()), LuaValue.listOf(values));
					return true;
				}

				@Override
				public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] cmdArgs) throws IllegalArgumentException {
					if (tabCompleteFunction != null && !tabCompleteFunction.isnil()) {
						LuaValue[] values = new LuaValue[cmdArgs.length];
						for (int i = 0; i < cmdArgs.length; i++) {
							values[i] = LuaValue.valueOf(cmdArgs[i]);
						}
						Varargs luaArgs = tabCompleteFunction.invoke(LuaValue.valueOf(alias), LuaValue.valueOf(sender.getName()), LuaValue.listOf(values));
						List<String> completions = new ArrayList<>();
						if (luaArgs.istable(1)) {
							LuaTable luaTable = luaArgs.checktable(1);
							for (int i = 1; i <= luaTable.length(); i++) {
								LuaValue value = luaTable.get(i);
								if (!value.isnil()) {
									completions.add(value.tojstring());
								}
							}
						}
						return completions;
					} else {
						return List.of();
					}
				}
			};
			Pair<LuaValue, Command> pair = Pair.of(function, command);
			Bukkit.getCommandMap().register(commandPrefix, command);
			commandList.put(commandName, pair);
			plugin.getLogger().info("Command '" + commandName + "' registered.");
			return LuaValue.NIL;
		}
	}

	class GetRegisteredCommands extends VarArgFunction {
		@Override
		public Varargs invoke(Varargs args) {
			LuaTable resultTable = LuaValue.tableOf();
			for (Map.Entry<String, Pair<LuaValue, Command>> eventEntry : commandList.entrySet()) {
				resultTable.set(resultTable.length()+1, eventEntry.getKey());
			}
			return resultTable;
		}
	}

	class UnregisterCommand extends VarArgFunction {
		@Override
		public Varargs invoke(Varargs args) {
			String commandName = args.checkjstring(1);
			if (commandList.containsKey(commandName)) {
				Command command = commandList.get(commandName).getRight();
				if (command != null) {
					unregister(command);
				}
				commandList.remove(commandName);
				plugin.getLogger().info("Command '" + commandName + "' unregistered.");
				return LuaValue.TRUE;
			} else {
				plugin.getLogger().info("Command '" + commandName + "' does not exist.");
				return LuaValue.FALSE;
			}
		}
	}

	/**
	 * @author <a href="https://gitlab.com/TauCu/bukkit-utils/-/blob/master/src/main/java/me/taucu/bukkitutils/commands/CommandUtil.java">TauCu</a>
	 */
	private void unregister(Command command) {
		CommandMap commandMap = Bukkit.getCommandMap();
		Map<String, Command> knownCommands = commandMap.getKnownCommands();
		HashMap<String, Command> commandsToCheck = new HashMap<>();
		commandsToCheck.put(command.getLabel().toLowerCase(), command);
		commandsToCheck.put(command.getName().toLowerCase(), command);
		command.getAliases().forEach(alias -> commandsToCheck.put(alias.toLowerCase(), command));
		for (Map.Entry<String, Command> entry : commandsToCheck.entrySet()) {
			Command mappedCommand = knownCommands.get(entry.getKey());
			if (entry.getValue().equals(mappedCommand)) {
				mappedCommand.unregister(commandMap);
				knownCommands.remove(entry.getKey());
			} else if (entry.getValue() instanceof PluginCommand pluginCommandEntry) {
				if (mappedCommand instanceof PluginCommand mappedPluginCommand) {
					CommandExecutor mappedExec = mappedPluginCommand.getExecutor();
					if (mappedExec.equals(pluginCommandEntry.getExecutor())) {
						mappedPluginCommand.setExecutor(null);
						mappedPluginCommand.setTabCompleter(null);
					}
				}
				pluginCommandEntry.setExecutor((sender, command1, label, args) -> false);
				pluginCommandEntry.setTabCompleter((sender, command1, label, args) -> null);
			}
		}
		Bukkit.getServer().reloadCommandAliases();
	}
}
