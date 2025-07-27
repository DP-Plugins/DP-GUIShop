package com.darksoldier1404.dpgs.events;

import com.darksoldier1404.dpgs.functions.ShopFunction;
import com.darksoldier1404.dpgs.obj.Shop;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import static com.darksoldier1404.dpgs.GUIShop.*;

public class DPGSEvent implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() == null) return;
        if (e.getClickedInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getClickedInventory().getHolder();
            Player p = (Player) e.getWhoClicked();
            if (inv.isValidHandler(plugin)) {
                Shop shop = ShopFunction.getShop(inv.getObj().toString());
                ItemStack item = e.getCurrentItem();
                ClickType clickType = e.getClick();
                if (item != null) {
                    if (NBT.hasTagKey(item, "dpgs.prevpage")) {
                        inv.applyChanges();
                        inv.prevPage();
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(item, "dpgs.nextpage")) {
                        inv.applyChanges();
                        inv.nextPage();
                        e.setCancelled(true);
                        return;
                    }
                    if (NBT.hasTagKey(item, "dpgs.clickcancel")) {
                        e.setCancelled(true);
                        return;
                    }
                    if (inv.isValidChannel(0)) { // Main shop channel
                        e.setCancelled(true);
                        if (clickType == ClickType.LEFT) {
                            ShopFunction.buyItem(p, shop.getName(), inv.getCurrentPage(), e.getSlot(), false);
                            return;
                        } else if (clickType == ClickType.RIGHT) {
                            ShopFunction.sellItem(p, shop.getName(), inv.getCurrentPage(), e.getSlot(), false);
                            return;
                        }else if (clickType == ClickType.SHIFT_LEFT) {
                            ShopFunction.buyItem(p, shop.getName(), inv.getCurrentPage(), e.getSlot(), true);
                            return;
                        } else if (clickType == ClickType.SHIFT_RIGHT) {
                            ShopFunction.sellItem(p, shop.getName(), inv.getCurrentPage(), e.getSlot(), true);
                            return;
                        }
                    }
                    if (inv.isValidChannel(1)) { // Item setting channel
                        return;
                    }
                    if (inv.isValidChannel(2)) { // Price setting channel
                        if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
                            e.setCancelled(true);
                            currentEdit.put(p.getUniqueId(), Tuple.of(e.getSlot(), inv));
                            p.closeInventory();
                            return;
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof DInventory) {
            Player p = (Player) e.getPlayer();
            DInventory inv = (DInventory) e.getInventory().getHolder();
            if (inv.isValidHandler(plugin)) {
                if (inv.getChannel() == 1) {
                    ShopFunction.saveShopItems(p, inv);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (currentEdit.containsKey(p.getUniqueId())) {
            int slot = currentEdit.get(p.getUniqueId()).getA();
            DInventory inv = currentEdit.get(p.getUniqueId()).getB();
            if (inv != null && inv.isValidHandler(plugin) && inv.getChannel() == 2) {
                e.setCancelled(true);
                String shopName = (String) inv.getObj();
                String message = e.getMessage();
                if (message.matches("\\d+:\\d+")) {
                    int buyPrice = Integer.parseInt(message.split(":")[0]);
                    int sellPrice = Integer.parseInt(message.split(":")[1]);
                    ShopFunction.setShopPrice(inv.getCurrentPage(), shopName, buyPrice, sellPrice, inv.getCurrentPage(), slot);
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_set", shopName, String.valueOf(buyPrice), String.valueOf(sellPrice)));
                } else if (message.matches("\\d+")) {
                    int price = Integer.parseInt(message);
                    ShopFunction.setShopPrice(inv.getCurrentPage(), shopName, price, 0, inv.getCurrentPage(), slot);
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_set", shopName, String.valueOf(price), "0"));
                } else {
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("shop_price_setting_number_guide"));
                }
                currentEdit.remove(p.getUniqueId());
                Bukkit.getScheduler().runTask(plugin, () -> ShopFunction.openShopPriceSetting(p, shopName));
            }
        }
    }
}
