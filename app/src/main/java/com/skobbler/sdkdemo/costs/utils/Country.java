package com.skobbler.sdkdemo.costs.utils;

public enum Country {

    ALBANIA ("Albania", "AL", 1.00),
    ANDORA ("Andora", "AD", 0.00),
    AUSTRIA ("Austria", "AT", 8.80),
    BELARUS ("Belarus", "BY", 0.00),
    BELGIUM ("Belgium", "BE", 0.00),
    BOSNIA_AND_HERZEGOVINA ("Bosnia and Herzegovina", "BA", 0.00),
    BULGARIA ("Bulgaria", "BG", 8.00),
    CROATIA ("Croatia", "HR", 0.00),
    CYPRUS ("Cyprus", "CY", 0.00),
    CZECH_REPUBLIC ("Czech Republic", "CZ", 11.47),
    DENMARK ("Denmark", "DK", 0.00),
    ESTONIA ("Estonia", "EE", 0.00),
    FINLAND ("Finland", "FI", 0.00),
    FRANCE ("France", "FR", 0.00),
    GERMANY ("Germany", "DE", 0.00),
    GIBRALTAR ("Gibraltar", "GI", 0.00),
    GREAT_BRITAIN ("Great Britain", "GB", 0.00),
    GREECE ("Greece", "GR", 0.00),
    HUNGARY ("Hungary", "HU", 9.58),
    ICELAND ("Iceland", "IS", 0.00),
    IRELAND ("Ireland", "IE", 0.00),
    ITALY ("Italy", "IT", 0.00),
    LATVIA ("Latvia", "LV", 0.00),
    LIECHTENSTEIN ("Liechtenstein", "LI", 0.00),
    LITHUANIA ("Lithuania", "LT", 0.00),
    LUXEMBOURG ("Luxembourg", "LU", 0.00),
    MACEDONIA ("Macedonia", "MK", 0.00),
    MALTA ("Malta", "MT", 0.00),
    MOLDOVA ("Moldova", "MD", 2.00),
    MONACO ("Monaco", "MC", 0.00),
    MONTENEGRO ("Montenegro", "ME", 0.00),
    NETHERLANDS ("Netherlands", "NL", 0.00),
    NORWAY ("Norway", "NO", 0.00),
    POLAND ("Poland", "PL", 0.00),
    PORTUGAL ("Portugal", "PT", 0.00),
    ROMANIA ("Romania", "RO", 3.00),
    RUSSIA ("Russia", "RU", 0.00),
    SAN_MARINO ("San Marino", "SM", 0.00),
    SERBIA ("Serbia", "RS", 0.00),
    SLOVAKIA ("Slovakia", "SK", 10.00),
    SLOVENIA ("Slovenia", "SI", 15.00),
    SPAIN ("Spain", "ES", 0.00),
    SWEDEN ("Sweden", "SE", 0.00),
    SWITZERLAND ("Switzerland", "CH", 36.51),
    TURKEY ("Turkey", "TR", 0.00),
    UKRAINE ("Ukraine", "UA", 17.00),
    VATICAN ("Vatican", "VA", 0.00);

    private final String name;
    private final String code;
    private final double vignetteCost; // in EUR

    Country(String name, String code, double vignetteCost) {
        this.name = name;
        this.code = code;
        this.vignetteCost = vignetteCost;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public double getVignetteCost(){
        return vignetteCost;
    }

}