package com.darksoldier1404.dpgs.functions;

import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dpgs.obj.ShopPrices;
import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.placeholder.PlaceholderUtils;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.enums.DependPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        DInventory inv = new DInventory(plugin.getLang().getWithArgs("shop_title", shopName), size * 9, true, true, plugin);
        inv.applyDefaultPageTools();
        shop.setInventory(inv);
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
        inv.setTitle(ColorUtils.applyColor(shop.getTitle()));
        inv.setObj(name);
        inv.setChannel(0);
        inv.setCurrentPage(0);
        inv.applyAllItemChanges(
                (Consumer<DInventory.PageItemSet>) item -> applyPlaceholderForPriceSetting(p, shop, item)
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
        Shop shop = shops.get(name);
        DInventory inv = shop.getInventory().clone();
        inv.setTitle(ColorUtils.applyColor("Item Edit : " + shop.getTitle()));
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
        inv.setTitle(ColorUtils.applyColor("Price Setting : " + shop.getTitle()));
        inv.setObj(name);
        inv.setChannel(2);
        inv.applyAllItemChanges(
                (Consumer<DInventory.PageItemSet>) item -> applyPlaceholderForPriceSetting(p, shop, item)
        );
        inv.setPageTools(getPageTools());
        inv.update();
        inv.openInventory(p);
    }

    public static void applyPlaceholderForPriceSetting(Player p, Shop shop, DInventory.PageItemSet set) {
        ItemStack item = set.getItem();
        int page = set.getPage();
        int slot = set.getSlot();
        ShopPrices price = shop.findPrice(page, slot);
        if (item != null && item.getType() != org.bukkit.Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore != null) {
                String format = plugin.loreFormat;
                format = format.replace("<buy_price>", price != null ? formatWithCommas(price.getBuyPrice()) : plugin.getLang().get("shop_lore_cant_buy"));
                format = format.replace("<sell_price>", price != null ? formatWithCommas(price.getSellPrice()) : plugin.getLang().get("shop_lore_cant_sell"));
                format = format.replace("<buy_stack_price>", price != null ? formatWithCommas(price.getBuyPrice().multiply(BigInteger.valueOf(item.getMaxStackSize()))) : plugin.getLang().get("shop_lore_cant_buy_stack"));
                format = format.replace("<sell_stack_price>", price != null ? formatWithCommas(price.getSellPrice().multiply(BigInteger.valueOf(item.getMaxStackSize()))) : plugin.getLang().get("shop_lore_cant_sell_stack"));
                format = ColorUtils.applyColor(format);
                if (PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
                    format = PlaceholderUtils.applyPlaceholder(p, format);
                }
                String[] lines = format.split("\n");
                for (String line : lines) {
                    lore.add(line);
                }
                // Append limit lore if limit is enabled
                if (shop.isLimitEnabled() && price != null && price.getLimitAmount() > 0) {
                    int totalLimit = price.getLimitAmount();
                    int usedCount;
                    if ("perplayer".equalsIgnoreCase(shop.getLimitType())) {
                        usedCount = shop.getPlayerLimitCount(page, slot, p.getUniqueId().toString());
                    } else {
                        usedCount = shop.getWorldLimitCount(page, slot);
                    }
                    int remaining = Math.max(0, totalLimit - usedCount);
                    String limitFormat = plugin.limitLore;
                    limitFormat = limitFormat.replace("<limit_remaining>", String.valueOf(remaining));
                    limitFormat = limitFormat.replace("<limit_total>", String.valueOf(totalLimit));
                    limitFormat = ColorUtils.applyColor(limitFormat);
                    String[] limitLines = limitFormat.split("\n");
                    for (String line : limitLines) {
                        lore.add(line);
                    }
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void setShopPrice(int currentPage, String name, BigInteger buyPrice, BigInteger sellPrice, int page, int slot) {
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
        if (price.getBuyPrice().compareTo(BigInteger.ZERO) <= 0) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_cant_buy"));
            return false;
        }
        ItemStack item = shop.findItem(page, slot);
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_item"));
            return false;
        }
        int buyAmount = isStackBuy ? item.getMaxStackSize() : 1;
        // Limit check
        if (shop.isLimitEnabled() && price.getLimitAmount() > 0) {
            int limitAmount = price.getLimitAmount();
            int usedCount;
            if ("perplayer".equalsIgnoreCase(shop.getLimitType())) {
                usedCount = shop.getPlayerLimitCount(page, slot, p.getUniqueId().toString());
            } else {
                usedCount = shop.getWorldLimitCount(page, slot);
            }
            if (usedCount + buyAmount > limitAmount) {
                int remaining = Math.max(0, limitAmount - usedCount);
                p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_limit_exceeded", String.valueOf(remaining), String.valueOf(limitAmount)));
                return false;
            }
        }
        if (isStackBuy) {
            BigInteger totalPrice = price.getBuyPrice().multiply(BigInteger.valueOf(buyAmount));
            if (!MoneyAPI.hasEnoughMoney(p, new BigDecimal(totalPrice))) {
                p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_money", String.valueOf(totalPrice)));
                return false;
            }
        } else {
            if (!MoneyAPI.hasEnoughMoney(p, new BigDecimal(price.getBuyPrice()))) {
                p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_money", String.valueOf(price.getBuyPrice())));
                return false;
            }
        }
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), item)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_no_space"));
            return false;
        }
        item = item.clone();
        if (isStackBuy) {
            item.setAmount(buyAmount);
            MoneyAPI.takeMoney(p, new BigDecimal(price.getBuyPrice().multiply(BigInteger.valueOf(buyAmount))));
        } else {
            MoneyAPI.takeMoney(p, new BigDecimal(price.getBuyPrice()));
        }
        p.getInventory().addItem(item);
        // Update limit data
        if (shop.isLimitEnabled() && price.getLimitAmount() > 0) {
            if ("perplayer".equalsIgnoreCase(shop.getLimitType())) {
                shop.addPlayerLimitCount(page, slot, p.getUniqueId().toString(), buyAmount);
            } else {
                shop.addWorldLimitCount(page, slot, buyAmount);
            }
            shops.put(shopName, shop);
            saveShops();
        }
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
        if (price.getSellPrice().compareTo(BigInteger.ZERO) <= 0) {
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
        MoneyAPI.addMoney(p, new BigDecimal(price.getSellPrice().multiply(BigInteger.valueOf(sellAmount))));
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
        for (int i = 2; i < args.length; i++) {
            titleBuilder.append(args[i]).append(" ");
        }
        String title = titleBuilder.toString();
        if (title.isEmpty()) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_title"));
            return;
        }
        shop.setTitle(title);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_set_title", shopName, title));
    }

    public static void addShopPrice(Player p, String name, String price) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        if (!price.matches("^\\d+:\\d+$")) {
            p.sendMessage(plugin.getPrefix() + "price is invalid format. use <buyPrice>:<sellPrice>");
            return;
        }
        int buyPrice;
        int sellPrice;
        if (price.contains(":")) {
            buyPrice = Integer.parseInt(price.split(":")[0]);
            sellPrice = Integer.parseInt(price.split(":")[1]);
            Shop shop = shops.get(name);
            for (ShopPrices sp : shop.getPrices()) {
                if (sp.getBuyPrice().compareTo(BigInteger.ZERO) != 0) {
                    sp.setBuyPrice(sp.getBuyPrice().add(BigInteger.valueOf(buyPrice)));
                }
                if (sp.getSellPrice().compareTo(BigInteger.ZERO) != 0) {
                    sp.setSellPrice(sp.getSellPrice().add(BigInteger.valueOf(sellPrice)));
                }
            }
            shops.put(name, shop);
            saveShops();
            p.sendMessage(plugin.getPrefix() + "shop prices have been increased by " + buyPrice + " (buy) and " + sellPrice + " (sell) for all items in shop " + name + ".");
        }
    }

    public static void subShopPrice(Player p, String name, String price) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        if (!price.matches("^\\d+:\\d+$")) {
            p.sendMessage(plugin.getPrefix() + "price is invalid format. use <buyPrice>:<sellPrice>");
            return;
        }
        int buyPrice;
        int sellPrice;
        if (price.contains(":")) {
            buyPrice = Integer.parseInt(price.split(":")[0]);
            sellPrice = Integer.parseInt(price.split(":")[1]);
            Shop shop = shops.get(name);
            for (ShopPrices sp : shop.getPrices()) {
                if (sp.getBuyPrice().compareTo(BigInteger.ZERO) != 0) {
                    sp.setBuyPrice(sp.getBuyPrice().subtract(BigInteger.valueOf(buyPrice)).max(BigInteger.ZERO));
                }
                if (sp.getSellPrice().compareTo(BigInteger.ZERO) != 0) {
                    sp.setSellPrice(sp.getSellPrice().subtract(BigInteger.valueOf(sellPrice)).max(BigInteger.ZERO));
                }
            }
            shops.put(name, shop);
            saveShops();
            p.sendMessage(plugin.getPrefix() + "shop prices have been decreased by " + buyPrice + " (buy) and " + sellPrice + " (sell) for all items in shop " + name + ".");
        }
    }

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###");

    public static String formatWithCommas(BigInteger number) {
        if (number == null) return "0";
        return COMMA_FORMAT.format(number);
    }

    // ============ Limit Functions ============

    public static void openShopLimitSetting(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", name));
            return;
        }
        Shop shop = shops.get(name);
        DInventory inv = shop.getInventory().clone();
        String limitStatus = shop.isLimitEnabled() ? "§aON" : "§cOFF";
        String limitType = shop.getLimitType().equalsIgnoreCase("perplayer") ? "§bPerPlayer" : "§eWorld";
        inv.setTitle(ColorUtils.applyColor("§6Limit [" + limitStatus + "§6] [" + limitType + "§6] : " + shop.getTitle()));
        inv.setObj(name);
        inv.setChannel(3);
        inv.applyAllItemChanges(
                (Consumer<DInventory.PageItemSet>) item -> applyLimitLoreForSetting(shop, item)
        );
        inv.setPageTools(getPageTools());
        inv.update();
        inv.openInventory(p);
    }

    public static void applyLimitLoreForSetting(Shop shop, DInventory.PageItemSet set) {
        ItemStack item = set.getItem();
        int page = set.getPage();
        int slot = set.getSlot();
        ShopPrices price = shop.findPrice(page, slot);
        if (item != null && item.getType() != org.bukkit.Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore != null) {
                int limitAmount = price != null ? price.getLimitAmount() : 0;
                int worldCount = shop.getWorldLimitCount(page, slot);
                lore.add(ColorUtils.applyColor(plugin.getLang().get("shop_limit_gui_separator")));
                if (limitAmount > 0) {
                    lore.add(ColorUtils.applyColor(plugin.getLang().getWithArgs("shop_limit_gui_amount", String.valueOf(limitAmount))));
                    lore.add(ColorUtils.applyColor(plugin.getLang().getWithArgs("shop_limit_gui_world_count", String.valueOf(worldCount))));
                } else {
                    lore.add(ColorUtils.applyColor(plugin.getLang().get("shop_limit_gui_not_set")));
                }
                lore.add(ColorUtils.applyColor(plugin.getLang().get("shop_limit_gui_click")));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void setItemLimit(Player p, String shopName, int currentPage, int page, int slot, int limitAmount) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.getInventory().setCurrentPage(currentPage);
        ShopPrices price = shop.findPrice(page, slot);
        if (price != null) {
            price.setLimitAmount(limitAmount);
        } else {
            price = new ShopPrices(page, slot, BigInteger.ZERO, BigInteger.ZERO, limitAmount);
            shop.getPrices().add(price);
        }
        shops.put(shopName, shop);
        saveShops();
    }

    public static void setShopLimitEnable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setLimitEnabled(true);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_limit_enabled", shopName));
    }

    public static void setShopLimitDisable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setLimitEnabled(false);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_limit_disabled", shopName));
    }

    public static void setShopLimitType(Player p, String shopName, String limitType) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        if (!limitType.equalsIgnoreCase("world") && !limitType.equalsIgnoreCase("perplayer")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_limit_type"));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setLimitType(limitType.toLowerCase());
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_limit_type_set", shopName, limitType));
    }

    public static void resetShopLimit(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.resetLimitData();
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_limit_reset", shopName));
    }

    /**
     * 구매 성공 후 열려 있는 인벤토리의 해당 슬롯 lore를 최신 limit 수치로 즉시 갱신한다.
     * shop.getInventory()의 원본 아이템(lore 없음)을 가져와 price/limit lore를 재적용한 후
     * 현재 열려있는 DInventory의 pageItems에 덮어쓰고 update()를 호출한다.
     */
    public static void refreshShopSlot(Player p, DInventory inv, String shopName, int page, int slot) {
        Shop freshShop = shops.get(shopName);
        if (freshShop == null) return;

        // 상점 원본 인벤토리에서 lore가 추가되지 않은 깨끗한 아이템을 가져온다
        ItemStack baseItem = freshShop.findItem(page, slot);
        if (baseItem == null || baseItem.getType() == org.bukkit.Material.AIR) return;

        ShopPrices price = freshShop.findPrice(page, slot);
        ItemMeta meta = baseItem.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        // 가격 lore 재적용
        String format = plugin.loreFormat;
        format = format.replace("<buy_price>", price != null ? formatWithCommas(price.getBuyPrice()) : plugin.getLang().get("shop_lore_cant_buy"));
        format = format.replace("<sell_price>", price != null ? formatWithCommas(price.getSellPrice()) : plugin.getLang().get("shop_lore_cant_sell"));
        format = format.replace("<buy_stack_price>", price != null ? formatWithCommas(price.getBuyPrice().multiply(BigInteger.valueOf(baseItem.getMaxStackSize()))) : plugin.getLang().get("shop_lore_cant_buy_stack"));
        format = format.replace("<sell_stack_price>", price != null ? formatWithCommas(price.getSellPrice().multiply(BigInteger.valueOf(baseItem.getMaxStackSize()))) : plugin.getLang().get("shop_lore_cant_sell_stack"));
        format = ColorUtils.applyColor(format);
        if (PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
            format = PlaceholderUtils.applyPlaceholder(p, format);
        }
        for (String line : format.split("\n")) {
            lore.add(line);
        }

        // limit lore 재적용 (최신 구매 횟수 반영)
        if (freshShop.isLimitEnabled() && price != null && price.getLimitAmount() > 0) {
            int totalLimit = price.getLimitAmount();
            int usedCount;
            if ("perplayer".equalsIgnoreCase(freshShop.getLimitType())) {
                usedCount = freshShop.getPlayerLimitCount(page, slot, p.getUniqueId().toString());
            } else {
                usedCount = freshShop.getWorldLimitCount(page, slot);
            }
            int remaining = Math.max(0, totalLimit - usedCount);
            String limitFormat = plugin.limitLore;
            limitFormat = limitFormat.replace("<limit_remaining>", String.valueOf(remaining));
            limitFormat = limitFormat.replace("<limit_total>", String.valueOf(totalLimit));
            limitFormat = ColorUtils.applyColor(limitFormat);
            for (String line : limitFormat.split("\n")) {
                lore.add(line);
            }
        }

        meta.setLore(lore);
        baseItem.setItemMeta(meta);

        // 현재 열려있는 DInventory의 해당 페이지 슬롯을 갱신하고 표시를 업데이트한다
        ItemStack[] pageItems = inv.getPageItems().get(page);
        if (pageItems != null) {
            pageItems[slot] = baseItem;
            inv.update();
        }
    }

    // ============ Auto Reset Functions ============

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static void setShopAutoResetEnable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setAutoResetEnabled(true);
        if (shop.getLastResetDate().isEmpty()) {
            shop.setLastResetDate(DATE_FORMAT.format(new Date()));
        }
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_auto_reset_enabled", shopName, shop.getAutoResetTime()));
    }

    public static void setShopAutoResetDisable(Player p, String shopName) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        Shop shop = shops.get(shopName);
        shop.setAutoResetEnabled(false);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_auto_reset_disabled", shopName));
    }

    public static void setShopAutoResetTime(Player p, String shopName, String time) {
        if (!isShopExists(shopName)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_not_exist", shopName));
            return;
        }
        if (!time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_err_invalid_reset_time"));
            return;
        }
        // Normalize to HH:mm (zero-pad hours)
        String[] parts = time.split(":");
        String normalized = String.format("%02d:%s", Integer.parseInt(parts[0]), parts[1]);
        Shop shop = shops.get(shopName);
        shop.setAutoResetTime(normalized);
        shops.put(shopName, shop);
        saveShops();
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_msg_auto_reset_time_set", shopName, normalized));
    }

    /**
     * Called every minute by the scheduler. Checks if any shop needs to be auto-reset.
     */
    public static void tickAutoReset() {
        Date now = new Date();
        String currentTime = TIME_FORMAT.format(now);
        String currentDate = DATE_FORMAT.format(now);
        boolean anyReset = false;
        for (Map.Entry<String, Shop> entry : shops.entrySet()) {
            Shop shop = entry.getValue();
            if (shop.isAutoResetEnabled()
                    && shop.getAutoResetTime().equals(currentTime)
                    && !shop.getLastResetDate().equals(currentDate)) {
                shop.resetLimitData();
                shop.setLastResetDate(currentDate);
                shops.put(entry.getKey(), shop);
                anyReset = true;
                Bukkit.broadcastMessage(plugin.getPrefix()
                        + plugin.getLang().getWithArgs("shop_msg_auto_reset_done", shop.getName(), currentTime));
            }
        }
        if (anyReset) {
            saveShops();
        }
    }

    /**
     * Called on server startup. Resets shops whose auto-reset time has already passed today and haven't been reset yet.
     */
    public static void checkAutoResetOnStartup() {
        Date now = new Date();
        String currentTime = TIME_FORMAT.format(now);
        String currentDate = DATE_FORMAT.format(now);
        boolean anyReset = false;
        for (Map.Entry<String, Shop> entry : shops.entrySet()) {
            Shop shop = entry.getValue();
            if (shop.isAutoResetEnabled()
                    && !shop.getLastResetDate().isEmpty()
                    && !shop.getLastResetDate().equals(currentDate)
                    && currentTime.compareTo(shop.getAutoResetTime()) >= 0) {
                shop.resetLimitData();
                shop.setLastResetDate(currentDate);
                shops.put(entry.getKey(), shop);
                anyReset = true;
                plugin.getLogger().info("[DP-GUIShop] Auto-reset triggered on startup for shop: " + shop.getName());
            }
        }
        if (anyReset) {
            saveShops();
        }
    }
}
