package com.darksoldier1404.dpgs.functions;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class CommonFunction {
    public static void init() {
        plugin.reload();
        plugin.loreFormat = plugin.getConfig().getString("Settings.itemLore");
        plugin.limitLore = plugin.getConfig().getString("Settings.limitLore", "&eLimit: &f<limit_remaining>&e/&f<limit_total>");
    }

    public static void saveConfig() {
        plugin.saveDataContainer();
    }
}
