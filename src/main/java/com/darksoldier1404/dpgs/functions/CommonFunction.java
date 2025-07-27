package com.darksoldier1404.dpgs.functions;

import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dpgs.obj.ShopPrices;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class CommonFunction {
    public static void init() {
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
        plugin.save();
    }

    public static void migration() {
        for (YamlConfiguration data : ConfigUtils.loadCustomDataList(plugin, "migration")) {
            String shopName = data.getString("Shop.NAME");
            String shopTitle = data.getString("Shop.Title") == null ? shopName : data.getString("Shop.Title");
            int maxPage = data.getInt("Shop.MaxPage");
            boolean isEnabled = !data.getBoolean("Shop.Disabled");
            Shop shop = new Shop(shopName, shopTitle, 6);
            shop.setEnabled(isEnabled);
            if (shopName != null && !shops.containsKey(shopName)) {
                DInventory inv = new DInventory(shopTitle, 54, true, plugin);
                inv.setPages(maxPage);
                if (data.getConfigurationSection("Shop.Items") != null) { // item migration
                    data.getConfigurationSection("Shop.Items").getKeys(false).forEach(page -> {
                        inv.setCurrentPage(Integer.parseInt(page));
                        inv.update();
                        data.getConfigurationSection("Shop.Items." + page).getKeys(false).forEach(slot -> {
                            ItemStack item = data.getItemStack("Shop.Items." + page + "." + slot);
                            if (item != null) {
                                inv.setItem(Integer.parseInt(slot), item);
                            }
                        });
                        inv.applyChanges();
                    });
                }
                // price migration
                Set<ShopPrices> prices = new HashSet<>();
                if (data.getConfigurationSection("Shop.Price") != null) {
                    data.getConfigurationSection("Shop.Price").getKeys(false).forEach(page -> {
                        data.getConfigurationSection("Shop.Price." + page).getKeys(false).forEach(slot -> {
                            ShopPrices price = new ShopPrices(Integer.parseInt(page), Integer.parseInt(slot), data.getInt("Shop.Price." + page + "." + slot + ".BuyPrice", 0),
                                    data.getInt("Shop.Price." + page + "." + slot + ".SellPrice", 0));
                            prices.add(price);
                        });
                    });
                }
                shop.setInventory(inv);
                shop.setPrices(prices);
                shops.put(shopName, shop);
            }
            saveConfig();
        }
    }
}
