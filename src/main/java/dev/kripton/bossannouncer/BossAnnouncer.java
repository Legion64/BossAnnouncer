package dev.kripton.bossannouncer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.kripton.bossannouncer.modules.PluginModule;
import dev.kripton.bossannouncer.tasks.BossBarTask;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
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

    private void initialize(){
        // Initialize Injector
        injector = Guice.createInjector(new PluginModule() {
            @Override
            protected void otherConfigure() {
                bind(BossAnnouncer.class).toInstance(instance);
            }
        });

        registerEvents();
    }

    private void registerEvents(){

    }

    @Override
    public void onDisable() {
        bossBarTask.stopTask();
    }
}
