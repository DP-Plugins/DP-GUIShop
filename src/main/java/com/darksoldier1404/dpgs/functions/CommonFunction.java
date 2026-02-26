package com.darksoldier1404.dpgs.functions;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class CommonFunction {
    public static void init() {
        plugin.reload();
        plugin.loreFormat = plugin.getConfig().getString("Settings.itemLore");
    }

    public static void saveConfig() {
        plugin.saveDataContainer();
    }
}
