package com.darksoldier1404.dpgs;

import com.darksoldier1404.dpgs.commands.ShopCommand;
import com.darksoldier1404.dpgs.events.DPGSEvent;
import com.darksoldier1404.dpgs.functions.CommonFunction;
import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIShop extends DPlugin {
    public static GUIShop plugin;
    //    public static final Map<String, Shop> shops = new HashMap<>();
    public static DataContainer<String, Shop> shops;
    public static final Map<UUID, Tuple<Integer, DInventory>> currentEdit = new HashMap<>();

    public GUIShop() {
        super(true);
        plugin = this;
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(this, 26579);
        init();
        load(new DataContainer<String, Shop>(this, DataType.CUSTOM, "shops"), Shop.class);
        shops = get("shops");
    }

    @Override
    public void onEnable() {
//        CommonFunction.init();
        getServer().getPluginManager().registerEvents(new DPGSEvent(), plugin);
        getCommand("dpgs").setExecutor(new ShopCommand().getExecutor());
        System.out.println(shops.get("test").getName() + " loaded successfully.");
    }

    @Override
    public void onDisable() {
        save();
    }
}
