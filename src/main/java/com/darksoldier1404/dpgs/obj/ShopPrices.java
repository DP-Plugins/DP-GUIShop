package com.darksoldier1404.dpgs.obj;

import java.math.BigInteger;

public class ShopPrices {
    private int page;
    private int slot;
    private BigInteger buyPrice;
    private BigInteger sellPrice;

    public ShopPrices(int page, int slot, BigInteger buyPrice, BigInteger sellPrice) {
        this.page = page;
        this.slot = slot;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public BigInteger getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigInteger buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigInteger getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigInteger sellPrice) {
        this.sellPrice = sellPrice;
    }
}
