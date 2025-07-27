package com.darksoldier1404.dpgs.obj;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.darksoldier1404.dpgs.GUIShop.plugin;

public class Shop implements DataCargo {
    private String name;
    private String title;
    private int size;
    private DInventory inventory;
    private Set<ShopPrices> prices = new HashSet<>();
    private boolean isEnabled = true;
    private String premission = null;

    public Shop() {
    }

    public Shop(String name, String title, int size) {
        this.name = name;
        this.title = title;
        this.size = size;
    }

    public Shop(String name, String title, int size, DInventory inventory) {
        this.name = name;
        this.title = title;
        this.size = size;
        this.inventory = inventory;
    }

    public Shop(String name, String title, int size, DInventory inventory, Set<ShopPrices> prices, boolean isEnabled, String premission) {
        this.name = name;
        this.title = title;
        this.size = size;
        this.inventory = inventory;
        this.prices = prices;
        this.isEnabled = isEnabled;
        this.premission = premission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    public Set<ShopPrices> getPrices() {
        return prices;
    }

    public void setPrices(Set<ShopPrices> prices) {
        this.prices = prices;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getPremission() {
        return premission;
    }

    public void setPremission(String premission) {
        this.premission = premission;
    }

    @Nullable
    public ShopPrices findPrice(int page, int slot) {
        for (ShopPrices price : prices) {
            if (price.getPage() == page && price.getSlot() == slot) {
                return price;
            }
        }
        return null;
    }

    @Nullable
    public ItemStack findItem(int page, int slot) {
        if (inventory != null) {
            ItemStack item = inventory.getPageItems().get(page)[slot];
            if (item != null) {
                return item.clone();
            }
        }
        return null;
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("name", name);
        data.set("title", title);
        data.set("size", size);
        data.set("enabled", isEnabled);
        if (!prices.isEmpty()) {
            for (ShopPrices price : prices) {
                data.set("prices." + price.getPage() + "." + price.getSlot() + ".buyPrice", price.getBuyPrice());
                data.set("prices." + price.getPage() + "." + price.getSlot() + ".sellPrice", price.getSellPrice());
            }
        }
        data = inventory.serialize(data);
        return data;
    }

    @Override
    public Shop deserialize(YamlConfiguration data) {
        String name = data.getString("name");
        String title = data.getString("title");
        int size = data.getInt("size");
        boolean isEnabled = data.getBoolean("enabled", true);
        String permission = data.getString("permission", null);
        if (name == null || title == null || size <= 0) {
            throw new IllegalArgumentException("Invalid shop data");
        }
        Set<ShopPrices> prices = new HashSet<>();
        if (data.contains("prices")) {
            for (String pageKey : data.getConfigurationSection("prices").getKeys(false)) {
                int page = Integer.parseInt(pageKey);
                for (String slotKey : data.getConfigurationSection("prices." + page).getKeys(false)) {
                    int slot = Integer.parseInt(slotKey);
                    int buyPrice = data.getInt("prices." + page + "." + slot + ".buyPrice", 0);
                    int sellPrice = data.getInt("prices." + page + "." + slot + ".sellPrice", 0);
                    prices.add(new ShopPrices(page, slot, buyPrice, sellPrice));
                }
            }
        }
        DInventory inventory = new DInventory(title, size * 9, true, plugin);
        inventory = inventory.deserialize(data);
        System.out.println(data.saveToString());
        System.out.println(plugin.getPrefix() + "Shop " + name + " loaded with title: " + title + ", size: " + size + ", enabled: " + isEnabled);
        return new Shop(name, title, size, inventory, prices, isEnabled, permission);
    }
}
