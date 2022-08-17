package dev.kripton.bossannouncer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.kripton.bossannouncer.modules.PluginModule;
import dev.kripton.bossannouncer.tasks.BossBarTask;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BossAnnouncer extends JavaPlugin implements Listener {

    private final Logger LOGGER = Logger.getLogger("BossAnnouncer");

    private BossAnnouncer instance;

    private Injector injector;

    private BossBarTask bossBarTask;

    @Override
    public void onEnable() {
        // Initialize plugins
        saveDefaultConfig();
        instance = this;

        // Initialize Plugin
        initialize();

        // Run needed tasks
        bossBarTask = injector.getInstance(BossBarTask.class);
        bossBarTask.startTask();
    }

    private void initialize() {
        // Initialize Injector
        injector = Guice.createInjector(new PluginModule() {
            @Override
            protected void otherConfigure() {
                bind(BossAnnouncer.class).toInstance(instance);
            }
        });

        registerEvents();
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        bossBarTask.stopTask();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        String joinText = "%player_name% &ajoined the server! They are rank &f%vault_rank%";

        /*
         * We parse the placeholders using "setPlaceholders"
         * This would turn %vault_rank% into the name of the Group, that the
         * joining player has.
         */
        joinText = PlaceholderAPI.setPlaceholders(event.getPlayer(), joinText);

        event.setJoinMessage(joinText);
    }
}
