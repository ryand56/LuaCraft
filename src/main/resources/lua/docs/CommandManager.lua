--- @meta
--- @class CommandManager
--- The class for managing commands in LuaCraft, allowing the registration, unregistration, and retrieval of commands.
CommandManager = {}

--- Registers a new command with the server.
--- @param commandName string The name of the command to register.
--- @param commandFunction fun(commandLabel:string, senderName:string, args:table) The function to execute when the command is run.
--- @param tabCompleteFunction? fun(commandLabel:string, senderName:string, args:table):table The optional function to handle tab completions for the command.
--- @param description? string A description of the command. Defaults to an empty string.
--- @param usage? string The usage string for the command. Defaults to "/<commandName>".
--- @param permission? string The permission required to run the command. Defaults to no required permission.
--- @param aliases? table A list of aliases for the command. Defaults to an empty list.
--- @return nil
--- ```lua
--- CommandManager.registerCommand(
---     "mycommand",
---     function(commandLabel, senderName, args)
---         print("Command executed by:", senderName)
---         for i, arg in ipairs(args) do
---             print("Arg " .. i .. ":", arg)
---         end
---     end,
---     function(commandLabel, senderName, args)
---         return {"suggestion1", "suggestion2"}
---     end,
---     "A custom command",
---     "/mycommand <args>",
---     "myplugin.permission.use",
---     {"alias1", "alias2"}
--- )
--- ```
function CommandManager.registerCommand(commandName, commandFunction, tabCompleteFunction, description, usage, permission, aliases) end

--- Unregisters a previously registered command.
--- @param commandName string The name of the command to unregister.
--- @return boolean @`true` if the command was successfully unregistered, `false` otherwise.
--- ```lua
--- local success = CommandManager.unregisterCommand("mycommand")
--- print("Command unregistered:", success)
--- ```
function CommandManager.unregisterCommand(commandName) end

--- Retrieves a list of all currently registered commands.
--- @return { [number]: string } @A list of the names of all registered commands.
--- ```lua
--- local commands = CommandManager.getRegisteredCommands()
--- for i, command in ipairs(commands) do
---     print("Registered command:", command)
--- end
--- ```
function CommandManager.getRegisteredCommands() end
