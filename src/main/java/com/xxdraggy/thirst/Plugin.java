package com.xxdraggy.thirst;

import com.xxdraggy.datamanager.DataManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
    public Plugin() {
        DataManager.register(this);
        ThirstManager.init(this);
    }

    @Override
    public void onEnable() {
        new ThirstListener().register(this);

        ThirstManager.register();
    }
}
