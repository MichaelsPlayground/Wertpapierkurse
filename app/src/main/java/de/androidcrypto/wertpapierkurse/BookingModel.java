package de.androidcrypto.wertpapierkurse;

public class BookingModel {

    String isin;
    String isinName;
    String date; // yyyy-mm-tt, "2022-01-07"
    String bookingYear; // "2022"
    String numberOfShares; // "234,5678"
    String kind; // "buy" or "sell"
    String amountEuro; // complete costs "9584,30"
    String depot; // "consorsbank"
    String note; // optional, e.g. "Sparplan"
    String group;
    boolean active;

    public BookingModel(String isin, String isinName, String date, String bookingYear, String numberOfShares, String kind, String amountEuro, String depot, String note, String group, boolean active) {
        this.isin = isin;
        this.isinName = isinName;
        this.date = date;
        this.bookingYear = bookingYear;
        this.numberOfShares = numberOfShares;
        this.kind = kind;
        this.amountEuro = amountEuro;
        this.depot = depot;
        this.note = note;
        this.group = group;
        this.active = active;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getIsinName() {
        return isinName;
    }

    public void setIsinName(String isinName) {
        this.isinName = isinName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBookingYear() {
        return bookingYear;
    }

    public void setBookingYear(String bookingYear) {
        this.bookingYear = bookingYear;
    }

    public String getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(String numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAmountEuro() {
        return amountEuro;
    }

    public void setAmountEuro(String amountEuro) {
        this.amountEuro = amountEuro;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}