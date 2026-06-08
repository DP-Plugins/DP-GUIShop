package com.darksoldier1404.dpgs;

import com.darksoldier1404.dpgs.commands.ShopCommand;
import com.darksoldier1404.dpgs.events.DPGSEvent;
import com.darksoldier1404.dpgs.functions.ShopFunction;
import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.Tuple;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@DPPCoreVersion(since = "5.3.0")
public class GUIShop extends DPlugin {
    public static GUIShop plugin;
    public static DataContainer<String, Shop> shops;
    public static final Map<UUID, Tuple<Integer, DInventory>> currentEdit = new ConcurrentHashMap<>();
    public static String loreFormat;
    public static String limitLore;

    public GUIShop() {
        super(true);
        plugin = this;
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(this, 26579);
        init();
        shops = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "shops"), Shop.class);
    }

    @Override
    public void onEnable() {
        plugin.loreFormat = plugin.getConfig().getString("Settings.itemLore");
        plugin.limitLore = plugin.getConfig().getString("Settings.limitLore", "&eLimit: &f<limit_remaining>&e/&f<limit_total>");
        getServer().getPluginManager().registerEvents(new DPGSEvent(), plugin);
        getCommand("dpgs").setExecutor(new ShopCommand().getExecutor());

        // Startup auto-reset check (delayed by 1 tick to ensure everything is loaded)
        getServer().getScheduler().runTaskLater(plugin, ShopFunction::checkAutoResetOnStartup, 1L);

        // Per-minute scheduler for auto-reset (starts after 1 minute, repeats every minute)
        getServer().getScheduler().runTaskTimer(plugin, ShopFunction::tickAutoReset, 1200L, 1200L);
    }

    @Override
    public void onDisable() {
        saveAllData();
    }
}
