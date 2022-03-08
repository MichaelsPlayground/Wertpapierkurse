package de.androidcrypto.wertpapierkurse;

import java.io.Serializable;

public class StockMovementsModalV2 implements Serializable {
    // V2 06.03.2022 appended value fields as float
    // V1 original version
    //below line is use for date (yyyy-mm-dd).
    private String date;
    //below line is use for date as unix timestamp.
    private float dateUnix;
    //below line is a variable for stock name.
    private String stockName;
    //below line is use for stock isin.
    private String stockIsin;
    //below line is use for direction (+ or -).
    private String direction;
    //below line is use for amountEuro.
    private String amountEuro;
    //below line is use for number of shares (positive when bying, negative when selling).
    private String numberShares;
    //below line is use for the bank of the account.
    private String bank;
    // wertpapierdepot, e.g. Maxblue 01
    private String securitiesAccount;
    // summe der anteile im bestand
    private float totalNumberShares;
    // summe der kaufkosten
    private float totalPurchaseCosts;
    // schlusskurs des tages
    //private String closePrice;
    private float closePrice;
    // depotwert
    private float securitiesAccountAmountEuro;
    private float winLossEuro;
    //below line is use for a note.
    private String note;
    // gültigkeit der daten für monat bzw jahr
    private String dataYear, dataMonth;
    // ist der datensatz aktiv
    private String active; // true or false

    //below line we are creating constructor class.
    public StockMovementsModalV2(String date, float dateUnix,
                                 String stockName, String stockIsin,
                                 String direction, String amountEuro,
                                 String numberShares, String bank,
                                 String securitiesAccount,
                                 String note, float totalNumberShares,
                                 float totalPurchaseCosts, float closePrice,
                                 float securitiesAccountAmountEuro,
                                 float winLossEuro,
                                 String dataYear, String dataMonth,
                                 String active) {
        this.date = date;
        this.dateUnix = dateUnix;
        this.stockName = stockName;
        this.stockIsin = stockIsin;
        this.direction = direction;
        this.amountEuro = amountEuro;
        this.numberShares = numberShares;
        this.bank = bank;
        this.securitiesAccount = securitiesAccount;
        this.note = note;
        this.totalNumberShares = totalNumberShares;
        this.totalPurchaseCosts = totalPurchaseCosts;
        this.closePrice = closePrice;
        this.securitiesAccountAmountEuro = securitiesAccountAmountEuro;
        this.winLossEuro = winLossEuro;
        this.dataYear = dataYear;
        this.dataMonth = dataMonth;
        this.active = active;
    }

    //on below line we are creating getter and setter methods.

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getSecuritiesAccountAmountEuro() {
        return securitiesAccountAmountEuro;
    }

    public void setSecuritiesAccountAmountEuro(float securitiesAccountAmountEuro) {
        this.securitiesAccountAmountEuro = securitiesAccountAmountEuro;
    }

    public String getSecuritiesAccount() {
        return securitiesAccount;
    }

    public void setSecuritiesAccount(String securitiesAccount) {
        this.securitiesAccount = securitiesAccount;
    }

    public float getTotalNumberShares() {
        return totalNumberShares;
    }

    public void setTotalNumberShares(float totalNumberShares) {
        this.totalNumberShares = totalNumberShares;
    }

    public float getTotalPurchaseCosts() {
        return totalPurchaseCosts;
    }

    public void setTotalPurchaseCosts(float totalPurchaseCosts) {
        this.totalPurchaseCosts = totalPurchaseCosts;
    }

    public String getDataYear() {
        return dataYear;
    }

    public void setDataYear(String dataYear) {
        this.dataYear = dataYear;
    }

    public String getDataMonth() {
        return dataMonth;
    }

    public void setDataMonth(String dataMonth) {
        this.dataMonth = dataMonth;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getDateUnix() {
        return dateUnix;
    }

    public void setDateUnix(float dateUnix) {
        this.dateUnix = dateUnix;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockIsin() {
        return stockIsin;
    }

    public void setStockIsin(String stockIsin) {
        this.stockIsin = stockIsin;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAmountEuro() {
        return amountEuro;
    }

    public void setAmountEuro(String amountEuro) {
        this.amountEuro = amountEuro;
    }

    public String getNumberShares() {
        return numberShares;
    }

    public void setNumberShares(String numberShares) {
        this.numberShares = numberShares;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public float getWinLossEuro() {
        return winLossEuro;
    }

    public void setWinLossEuro(float winLossEuro) {
        this.winLossEuro = winLossEuro;
    }
}
