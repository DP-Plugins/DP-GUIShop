package com.darksoldier1404.dpgs.functions;

import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.utils.ConfigUtils;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class CommonFunction {
    public static void init() {
        plugin.init();
        loadShops();
    }

    public static void loadShops() {
        ConfigUtils.loadCustomDataList(plugin, "shops").forEach(data -> {
            String shopName = data.getString("name");
            if (shopName != null) {
                Shop shop = new Shop().deserialize(data);
                shops.put(shopName, shop);
            }
        });
    }

    public static void saveConfig() {
        plugin.saveDataContainer();
    }
}
