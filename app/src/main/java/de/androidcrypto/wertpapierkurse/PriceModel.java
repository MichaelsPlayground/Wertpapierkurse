package de.androidcrypto.wertpapierkurse;

public class PriceModel {

    String date; // yyyy-mm-tt, "2022-01-07"
    String unixDate; // unix timestamp
    String closePrice;

    public PriceModel(String date, String unixDate, String closePrice) {
        this.date = date;
        this.unixDate = unixDate;
        this.closePrice = closePrice;
    }

    public String getUnixDate() {
        return unixDate;
    }

    public void setUnixDate(String unixDate) {
        this.unixDate = unixDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClosePrice() {return closePrice; }

    public void setClosePrice(String closePrice) { this.closePrice = closePrice; }
}