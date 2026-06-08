package com.darksoldier1404.dpgs.events;

import com.darksoldier1404.dpgs.functions.ShopFunction;
import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.dinventory.DInventoryClickEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryCloseEvent;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.BigInteger;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class DPGSEvent implements Listener {
    @EventHandler
    public void onInventoryClick(DInventoryClickEvent e) {
        DInventory inv = e.getDInventory();
        Player p = (Player) e.getWhoClicked();
        if (inv.isValidHandler(plugin)) {
            Shop shop = ShopFunction.getShop(inv.getObj().toString());
            ClickType clickType = e.getClick();
            if (inv.isValidChannel(0)) { // Main shop channel
                e.setCancelled(true);
                if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
                    return;
                }
                int clickedSlot = e.getSlot();
                int currentPage = inv.getCurrentPage();
                if (clickType == ClickType.LEFT) {
                    if (ShopFunction.buyItem(p, shop.getName(), currentPage, clickedSlot, false)) {
                        ShopFunction.refreshShopSlot(p, inv, shop.getName(), currentPage, clickedSlot);
                    }
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    ShopFunction.sellItem(p, shop.getName(), currentPage, clickedSlot, false);
                    return;
                } else if (clickType == ClickType.SHIFT_LEFT) {
                    if (ShopFunction.buyItem(p, shop.getName(), currentPage, clickedSlot, true)) {
                        ShopFunction.refreshShopSlot(p, inv, shop.getName(), currentPage, clickedSlot);
                    }
                    return;
                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    ShopFunction.sellItem(p, shop.getName(), currentPage, clickedSlot, true);
                    return;
                }
            }
            if (inv.isValidChannel(2)) { // Price setting channel
                if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
                    e.setCancelled(true);
                    currentEdit.put(p.getUniqueId(), Tuple.of(e.getSlot(), inv));
                    p.closeInventory();
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_setting_number_guide"));
                } else {
                    e.setCancelled(true);
                }
            }
            if (inv.isValidChannel(3)) { // Limit setting channel
                if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
                    e.setCancelled(true);
                    currentEdit.put(p.getUniqueId(), Tuple.of(e.getSlot(), inv));
                    p.closeInventory();
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_limit_setting_guide"));
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(DInventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (inv.isValidHandler(plugin)) {
            if (inv.getChannel() == 1) {
                ShopFunction.saveShopItems(p, inv);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (currentEdit.containsKey(p.getUniqueId())) {
            int slot = currentEdit.get(p.getUniqueId()).getA();
            DInventory inv = currentEdit.get(p.getUniqueId()).getB();
            if (inv != null && inv.isValidHandler(plugin)) {
                if (inv.getChannel() == 2) { // Price setting
                    e.setCancelled(true);
                    String shopName = (String) inv.getObj();
                    String message = e.getMessage();
                    if (message.matches("\\d+:\\d+")) {
                        BigInteger buyPrice = new BigInteger(message.split(":")[0]);
                        BigInteger sellPrice = new BigInteger(message.split(":")[1]);
                        ShopFunction.setShopPrice(inv.getCurrentPage(), shopName, buyPrice, sellPrice, inv.getCurrentPage(), slot);
                        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_set", shopName, String.valueOf(buyPrice), String.valueOf(sellPrice)));
                    } else if (message.matches("\\d+")) {
                        BigInteger price = new BigInteger(message);
                        ShopFunction.setShopPrice(inv.getCurrentPage(), shopName, price, BigInteger.ZERO, inv.getCurrentPage(), slot);
                        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_set", shopName, String.valueOf(price), "0"));
                    } else {
                        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_setting_number_guide"));
                    }
                    currentEdit.remove(p.getUniqueId());
                    Bukkit.getScheduler().runTask(plugin, () -> ShopFunction.openShopPriceSetting(p, shopName));
                } else if (inv.getChannel() == 3) { // Limit setting
                    e.setCancelled(true);
                    String shopName = (String) inv.getObj();
                    String message = e.getMessage();
                    if (message.matches("\\d+")) {
                        int limitAmount = Integer.parseInt(message);
                        ShopFunction.setItemLimit(p, shopName, inv.getCurrentPage(), inv.getCurrentPage(), slot, limitAmount);
                        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_limit_set", shopName, String.valueOf(limitAmount)));
                    } else {
                        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_limit_setting_guide"));
                    }
                    currentEdit.remove(p.getUniqueId());
                    Bukkit.getScheduler().runTask(plugin, () -> ShopFunction.openShopLimitSetting(p, shopName));
                }
            }
        }
    }
}
