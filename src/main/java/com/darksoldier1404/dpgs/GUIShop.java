package com.darksoldier1404.dpgs;

import com.darksoldier1404.dpgs.commands.ShopCommand;
import com.darksoldier1404.dpgs.events.DPGSEvent;
import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIShop extends DPlugin {
    public static GUIShop plugin;
    public static DataContainer<String, Shop> shops;
    public static DataContainer<String, YamlConfiguration> udata;
    public static final Map<UUID, Tuple<Integer, DInventory>> currentEdit = new HashMap<>();

    public GUIShop() {
        super(true);
        plugin = this;
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(this, 26579);
        init();
        shops = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "shops"), Shop.class);
        udata = loadDataContainer(new DataContainer<>(this, DataType.USER));
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DPGSEvent(), plugin);
        getCommand("dpgs").setExecutor(new ShopCommand().getExecutor());
    }

    @Override
    public void onDisable() {
        saveDataContainer();
    }
}
