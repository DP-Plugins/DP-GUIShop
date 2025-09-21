package com.darksoldier1404.dpgs.functions;

import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dpgs.obj.ShopPrices;
import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.darksoldier1404.dpgs.GUIShop.*;

@SuppressWarnings("all")
public class ShopFunction {
    public static void createShop(Player p, String shopName, String shopSize) {
        if (isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_exists", shopName));
            return;
        }
        if (!shopSize.matches("^[0-9]+$")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_size"));
            return;
        }
        int size = Integer.parseInt(shopSize);
        if (size < 2 || size > 6) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_size_range"));
            return;
        }
        YamlConfiguration data = new YamlConfiguration();
        data.set("name", shopName);
        data.set("size", size);
        Shop shop = new Shop(shopName, plugin.getLang().getWithArgs("shop_title", shopName), size);
        shop.setInventory(new DInventory(plugin.getLang().getWithArgs("shop_title", shopName), size * 9, true, plugin));
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_create_success", shopName, String.valueOf(size)));
    }

    public static boolean isShopExists(String shopName) {
        return shops.containsKey(shopName);
    }

    public static Shop getShop(String shopName) {
        return shops.get(shopName);
    }

    public static DInventory getShopInventory(String shopName) {
        return shops.get(shopName).getInventory();
    }

    public static void saveShops() {
        plugin.saveDataContainer();
    }

    public static void openShop(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        Shop shop = (Shop) shops.get(name);
        if (shop.getPremission() != null && !p.hasPermission(shop.getPremission())) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_permission", shop.getPremission()));
            return;
        }
        if (!shop.isEnabled()) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_disabled", name));
            return;
        }
        DInventory inv = shop.getInventory().clone();
        inv.updateTitle(ColorUtils.applyColor(shop.getTitle()));
        inv.setObj(name);
        inv.setChannel(0);
        inv.setCurrentPage(0);
        inv.applyAllItemChanges(
                item -> applyPlaceholderForPriceSetting(shop, item)
        );
        inv.setPageTools(getPageTools());
        inv.update();
        inv.openInventory(p);
    }

    public static void openShopItemSetting(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        DInventory inv = shops.get(name).getInventory().clone();
        inv.updateTitle(plugin.getLang().getWithArgs("shop_item_setting_title", name));
        inv.setObj(name);
        inv.setChannel(1);
        inv.setCurrentPage(0);
        inv.setPageTools(getPageTools());
        inv.update();
        inv.openInventory(p);
    }

    public static void saveShopItems(Player p, DInventory inv) {
        String shopName = (String) inv.getObj();
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        inv.applyChanges();
        Shop shop = shops.get(shopName);
        shop.setInventory(inv);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_save_success"));
    }

    public static ItemStack[] getPageTools() {
        ItemStack pane = new ItemStack(org.bukkit.Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(meta);
        pane = NBT.setStringTag(pane, "dpgs.clickcancel", "true");
        ItemStack nextPage = new ItemStack(org.bukkit.Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName("§aNext Page");
        nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nextPage.setItemMeta(nextMeta);
        nextPage = NBT.setStringTag(NBT.setStringTag(nextPage, "dpgs.clickcancel", "true"), "dpgs.nextpage", "true");
        ItemStack prevPage = new ItemStack(org.bukkit.Material.ARROW);
        ItemMeta prevMeta = prevPage.getItemMeta();
        prevMeta.setDisplayName("§aPrevious Page");
        prevMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        prevPage.setItemMeta(prevMeta);
        prevPage = NBT.setStringTag(NBT.setStringTag(prevPage, "dpgs.clickcancel", "true"), "dpgs.prevpage", "true");
        return new ItemStack[]{pane, prevPage, pane, pane, pane, pane, pane, nextPage, pane};
    }


    public static void openShopPriceSetting(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        Shop shop = shops.get(name);
        DInventory inv = shop.getInventory().clone();
        inv.setObj(name);
        inv.setChannel(2);
        inv.applyAllItemChanges(
                item -> applyPlaceholderForPriceSetting(shop, item)
        );
        inv.setPageTools(getPageTools());
        inv.update();
        inv.openInventory(p);
    }

    public static void applyPlaceholderForPriceSetting(Shop shop, DInventory.PageItemSet set) {
        ItemStack item = set.getItem();
        int page = set.getPage();
        int slot = set.getSlot();
        ShopPrices price = shop.findPrice(page, slot);
        if (item != null && item.getType() != org.bukkit.Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore != null) {
                lore.add(plugin.getLang().getWithArgs("shop_lore_buy", price != null && price.getBuyPrice() != 0 ? String.valueOf(price.getBuyPrice()) : plugin.getLang().getWithArgs("shop_lore_cant_buy")));
                lore.add(plugin.getLang().getWithArgs("shop_lore_sell", price != null && price.getSellPrice() != 0 ? String.valueOf(price.getSellPrice()) : plugin.getLang().getWithArgs("shop_lore_cant_sell")));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void setShopPrice(int currentPage, String name, int buyPrice, int sellPrice, int page, int slot) {
        if (!isShopExists(name)) {
            throw new IllegalArgumentException("Shop does not exist: " + name);
        }
        Shop shop = shops.get(name);
        shop.getInventory().setCurrentPage(currentPage);
        ShopPrices price = shop.findPrice(page, slot);
        if (price != null) {
            price.setBuyPrice(buyPrice);
            price.setSellPrice(sellPrice);
        } else {
            price = new ShopPrices(page, slot, buyPrice, sellPrice);
            shop.getPrices().add(price);
        }
        shops.put(name, shop);
        saveShops();
    }

    public static void setShopMaxPage(Player p, String name, String maxPage) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        if (!maxPage.matches("^[0-9]+$")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_maxpage"));
            return;
        }
        int page = Integer.parseInt(maxPage);
        if (page < 0) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_maxpage_range"));
            return;
        }
        Shop shop = shops.get(name);
        shop.getInventory().setPages(page);
        shops.put(name, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_maxpage_set", name, String.valueOf(page)));
    }

    @Nullable
    public static ItemStack getShopItem(String shopName, int page, int slot) {
        if (!isShopExists(shopName)) {
            return null;
        }
        Shop shop = shops.get(shopName);
        ItemStack item = shop.findItem(page, slot);
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            return null;
        }
        return item;
    }

    public static boolean buyItem(Player p, String shopName, int page, int slot, boolean isStackBuy) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return false;
        }
        Shop shop = shops.get(shopName);
        ShopPrices price = shop.findPrice(page, slot);
        if (price == null) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_price"));
            return false;
        }
        if (price.getBuyPrice() <= 0) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_cant_buy"));
            return false;
        }
        if (isStackBuy) {
            if (!MoneyAPI.hasEnoughMoney(p, price.getBuyPrice() * shop.findItem(page, slot).getMaxStackSize())) {
                p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_money", String.valueOf(price.getBuyPrice() * shop.findItem(page, slot).getMaxStackSize())));
                return false;
            }
        } else {
            if (!MoneyAPI.hasEnoughMoney(p, price.getBuyPrice())) {
                p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_money", String.valueOf(price.getBuyPrice())));
                return false;
            }
        }
        ItemStack item = shop.findItem(page, slot).clone();
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_item"));
            return false;
        }
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), item)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_space"));
            return false;
        }
        if (isStackBuy) {
            item.setAmount(item.getMaxStackSize());
            MoneyAPI.takeMoney(p, price.getBuyPrice() * item.getMaxStackSize());
        } else {
            MoneyAPI.takeMoney(p, price.getBuyPrice());
        }
        p.getInventory().addItem(item);
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_buy", String.valueOf(item.getAmount()), item.getType().name(), shopName));
        return true;
    }

    public static boolean sellItem(Player p, String shopName, int page, int slot, boolean isStackSell) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return false;
        }
        Shop shop = shops.get(shopName);
        ShopPrices price = shop.findPrice(page, slot);
        if (price == null) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_price"));
            return false;
        }
        if (price.getSellPrice() <= 0) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_cant_sell"));
            return false;
        }
        ItemStack item = shop.findItem(page, slot);
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_item"));
            return false;
        }
        item = item.clone();
        int availableAmount = InventoryUtils.getSimlarItemCount(p.getInventory().getStorageContents(), item);
        int sellAmount = isStackSell ? Math.min(item.getMaxStackSize(), availableAmount) : (availableAmount > 0 ? 1 : 0);
        if (sellAmount < 1) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_have_item"));
            return false;
        }
        item.setAmount(sellAmount);
        MoneyAPI.addMoney(p, price.getSellPrice() * sellAmount);
        p.getInventory().removeItem(item);
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_sell", String.valueOf(sellAmount), item.getType().name(), shopName));
        return true;
    }

    public static void deleteShop(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        new File(plugin.getDataFolder(), "shops/" + name + ".yml").delete();
        shops.remove(name);
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_delete_success", name));
    }

    public static void setShopEnable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
        }
        Shop shop = shops.get(shopName);
        shop.setEnabled(true);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_set_enable", shopName));
    }

    public static void setShopDisable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
        }
        Shop shop = shops.get(shopName);
        shop.setEnabled(false);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_set_disable", shopName));
    }

    public static void setShopPermission(Player p, String shopName, String permission) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setPremission(permission);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_set_permission", shopName, permission));
    }

    public static void removeShopPermission(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setPremission(null);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_remove_permission", shopName));
    }

    public static void listShops(Player p) {
        if (shops.isEmpty()) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_no_shops"));
            return;
        }
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_shop_list_header"));
        for (Shop shop : shops.values()) {
            String status = shop.isEnabled() ? "§aEnabled§f" : "§cDisabled§f";
            String permission = shop.getPremission() != null ? "§e" + shop.getPremission() : "§eNone";
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_shop_list_item", shop.getName(), status, permission));
        }
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_shop_list_footer", String.valueOf(shops.size())));
    }

    public static void setShopTitle(Player p, String shopName, String[] args) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        if (args.length < 2) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_title"));
            return;
        }
        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            titleBuilder.append(args[i]).append(" ");
        }
        String title = titleBuilder.toString().trim();
        if (title.isEmpty()) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_title"));
            return;
        }
        shop.setTitle(title);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_set_title", shopName, title));
    }
}
