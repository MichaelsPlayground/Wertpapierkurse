package de.androidcrypto.wertpapierkurse.models;

public class StockModelV2 {

    String isin;
    String isinName;
    String symbolYahooApi;
    String symbolApi;
    boolean active;
    String group;

    public StockModelV2(String isin, String isinName, String symbolYahooApi, String symbolApi,  boolean active, String group) {
        this.isin = isin;
        this.isinName = isinName;
        this.symbolYahooApi = symbolYahooApi;
        this.symbolApi = symbolApi;
        this.active = active;
        this.group = group;
    }

    public String getIsin() {
        return isin;
    }

    public String getIsinName() {
        return isinName;
    }

    public boolean getActive() {
        return active;
    }

    public String getGroup() {
        return group;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }


    public void setIsinName(String isinName) {
        this.isinName = isinName;
    }

    public String getSymbolYahooApi() {
        return symbolYahooApi;
    }

    public void setSymbolYahooApi(String symbolYahooApi) {
        this.symbolYahooApi = symbolYahooApi;
    }

    public String getSymbolApi() {
        return symbolApi;
    }

    public void setSymbolApi(String symbolApi) {
        this.symbolApi = symbolApi;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}