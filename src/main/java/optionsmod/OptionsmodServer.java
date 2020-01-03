package optionsmod;


import optionsmod.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class OptionsmodServer // static for now - easier to handle all around the code, its one anyways
{
    public static MinecraftServer minecraft_server;
    private static CommandDispatcher<ServerCommandSource> currentCommandDispatcher;
    public static List<OptionsmodExtension> extensions = new ArrayList<>();

    // Separate from onServerLoaded, because a server can be loaded multiple times in singleplayer
    public static void manageExtension(OptionsmodExtension extension)
    {
        extensions.add(extension);
        // for extensions that come late to the party, after server is created / loaded
        // we will handle them now.
        // that would handle all extensions, even these that add themselves really late to the party
        if (currentCommandDispatcher != null)
        {
            extension.registerCommands(currentCommandDispatcher);
        }
    }

    public static void onGameStarted()
    {
        extensions.forEach(OptionsmodExtension::onGameStarted);
    }

    public static void onServerLoaded(MinecraftServer server)
    {
        OptionsmodServer.minecraft_server = server;
        extensions.forEach(e -> {
            SettingsManager sm = e.customSettingsManager();
            if (sm != null) sm.attachServer(server);
            e.onServerLoaded(server);
        });
    }

    public static void tick(MinecraftServer server)
    {
        extensions.forEach(e -> e.onTick(server));
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        // registering command of extensions that has registered before either server is created
        // for all other, they will have them registered when they add themselves
        currentCommandDispatcher = dispatcher;
        extensions.forEach(e -> e.registerCommands(dispatcher));
    }

    public static void onPlayerLoggedIn(ServerPlayerEntity player)
    {
        extensions.forEach(e -> e.onPlayerLoggedIn(player));
    }

    public static void onPlayerLoggedOut(ServerPlayerEntity player)
    {
        extensions.forEach(e -> e.onPlayerLoggedOut(player));
    }

    public static void onServerClosed(MinecraftServer server)
    {
        currentCommandDispatcher = null;

        extensions.forEach(e -> e.onServerClosed(server));
    }
}

