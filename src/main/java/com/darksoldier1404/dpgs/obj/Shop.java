package com.darksoldier1404.dpgs.obj;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private boolean limitEnabled = false;
    private String limitType = "world"; // "world" or "perplayer"
    private Map<String, Integer> worldLimitData = new HashMap<>(); // "page:slot" -> total purchased count
    private Map<String, Map<String, Integer>> playerLimitData = new HashMap<>(); // UUID -> ("page:slot" -> count)
    private boolean autoResetEnabled = false;
    private String autoResetTime = "00:00"; // HH:mm format, default midnight
    private String lastResetDate = ""; // "yyyy-MM-dd" to prevent duplicate resets

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

    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public Map<String, Integer> getWorldLimitData() {
        return worldLimitData;
    }

    public Map<String, Map<String, Integer>> getPlayerLimitData() {
        return playerLimitData;
    }

    public int getWorldLimitCount(int page, int slot) {
        return worldLimitData.getOrDefault(page + ":" + slot, 0);
    }

    public int getPlayerLimitCount(int page, int slot, String uuid) {
        Map<String, Integer> pData = playerLimitData.getOrDefault(uuid, new HashMap<>());
        return pData.getOrDefault(page + ":" + slot, 0);
    }

    public void addWorldLimitCount(int page, int slot, int amount) {
        String key = page + ":" + slot;
        worldLimitData.put(key, worldLimitData.getOrDefault(key, 0) + amount);
    }

    public void addPlayerLimitCount(int page, int slot, String uuid, int amount) {
        String key = page + ":" + slot;
        Map<String, Integer> pData = playerLimitData.computeIfAbsent(uuid, k -> new HashMap<>());
        pData.put(key, pData.getOrDefault(key, 0) + amount);
    }

    public void resetLimitData() {
        worldLimitData.clear();
        playerLimitData.clear();
    }

    public void resetItemLimitData(int page, int slot) {
        String key = page + ":" + slot;
        worldLimitData.remove(key);
        for (Map<String, Integer> pData : playerLimitData.values()) {
            pData.remove(key);
        }
    }

    public boolean isAutoResetEnabled() {
        return autoResetEnabled;
    }

    public void setAutoResetEnabled(boolean autoResetEnabled) {
        this.autoResetEnabled = autoResetEnabled;
    }

    public String getAutoResetTime() {
        return autoResetTime;
    }

    public void setAutoResetTime(String autoResetTime) {
        this.autoResetTime = autoResetTime;
    }

    public String getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(String lastResetDate) {
        this.lastResetDate = lastResetDate;
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
        data.set("permission", premission);
        data.set("limitEnabled", limitEnabled);
        data.set("limitType", limitType);
        data.set("autoResetEnabled", autoResetEnabled);
        data.set("autoResetTime", autoResetTime);
        data.set("lastResetDate", lastResetDate);
        if (!prices.isEmpty()) {
            for (ShopPrices price : prices) {
                data.set("prices." + price.getPage() + "." + price.getSlot() + ".buyPrice", price.getBuyPrice().toString());
                data.set("prices." + price.getPage() + "." + price.getSlot() + ".sellPrice", price.getSellPrice().toString());
                data.set("prices." + price.getPage() + "." + price.getSlot() + ".limitAmount", price.getLimitAmount());
            }
        }
        // Save world limit data
        if (!worldLimitData.isEmpty()) {
            for (Map.Entry<String, Integer> entry : worldLimitData.entrySet()) {
                String[] parts = entry.getKey().split(":");
                data.set("limitData.world." + parts[0] + "." + parts[1], entry.getValue());
            }
        }
        // Save player limit data
        if (!playerLimitData.isEmpty()) {
            for (Map.Entry<String, Map<String, Integer>> playerEntry : playerLimitData.entrySet()) {
                String uuid = playerEntry.getKey().replace("-", "_");
                for (Map.Entry<String, Integer> slotEntry : playerEntry.getValue().entrySet()) {
                    String[] parts = slotEntry.getKey().split(":");
                    data.set("limitData.player." + uuid + "." + parts[0] + "." + parts[1], slotEntry.getValue());
                }
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
        boolean limitEnabled = data.getBoolean("limitEnabled", false);
        String limitType = data.getString("limitType", "world");
        boolean autoResetEnabled = data.getBoolean("autoResetEnabled", false);
        String autoResetTime = data.getString("autoResetTime", "00:00");
        String lastResetDate = data.getString("lastResetDate", "");
        if (name == null || title == null || size <= 0) {
            throw new IllegalArgumentException("Invalid shop data");
        }
        Set<ShopPrices> prices = new HashSet<>();
        if (data.contains("prices")) {
            for (String pageKey : data.getConfigurationSection("prices").getKeys(false)) {
                int page = Integer.parseInt(pageKey);
                for (String slotKey : data.getConfigurationSection("prices." + page).getKeys(false)) {
                    int slot = Integer.parseInt(slotKey);
                    BigInteger buyPrice = new BigInteger((data.getString("prices." + page + "." + slot + ".buyPrice", "0")));
                    BigInteger sellPrice = new BigInteger(data.getString("prices." + page + "." + slot + ".sellPrice", "0"));
                    int limitAmount = data.getInt("prices." + page + "." + slot + ".limitAmount", 0);
                    prices.add(new ShopPrices(page, slot, buyPrice, sellPrice, limitAmount));
                }
            }
        }
        Map<String, Integer> worldLimitData = new HashMap<>();
        Map<String, Map<String, Integer>> playerLimitData = new HashMap<>();
        if (data.contains("limitData.world")) {
            for (String pageKey : data.getConfigurationSection("limitData.world").getKeys(false)) {
                for (String slotKey : data.getConfigurationSection("limitData.world." + pageKey).getKeys(false)) {
                    int count = data.getInt("limitData.world." + pageKey + "." + slotKey);
                    worldLimitData.put(pageKey + ":" + slotKey, count);
                }
            }
        }
        if (data.contains("limitData.player")) {
            for (String uuidKey : data.getConfigurationSection("limitData.player").getKeys(false)) {
                Map<String, Integer> uuidData = new HashMap<>();
                for (String pageKey : data.getConfigurationSection("limitData.player." + uuidKey).getKeys(false)) {
                    for (String slotKey : data.getConfigurationSection("limitData.player." + uuidKey + "." + pageKey).getKeys(false)) {
                        int count = data.getInt("limitData.player." + uuidKey + "." + pageKey + "." + slotKey);
                        uuidData.put(pageKey + ":" + slotKey, count);
                    }
                }
                String originalUuid = uuidKey.replace("_", "-");
                playerLimitData.put(originalUuid, uuidData);
            }
        }
        DInventory inventory = new DInventory(title, size * 9, true, plugin);
        inventory = inventory.deserialize(data);
        Shop shop = new Shop(name, title, size, inventory, prices, isEnabled, permission);
        shop.setLimitEnabled(limitEnabled);
        shop.setLimitType(limitType);
        shop.setAutoResetEnabled(autoResetEnabled);
        shop.setAutoResetTime(autoResetTime);
        shop.setLastResetDate(lastResetDate);
        shop.getWorldLimitData().putAll(worldLimitData);
        shop.getPlayerLimitData().putAll(playerLimitData);
        return shop;
    }
}
