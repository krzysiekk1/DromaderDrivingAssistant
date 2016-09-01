package com.skobbler.ngx.sdktools.navigationui.costs.utils;

public enum Country {

    ALBANIA ("Albania", "AL", 0.0),
    ANDORA ("Andora", "AD", 0.0),
    AUSTRIA ("Austria", "AT", 8.8),
    BELARUS ("Białoruś", "BY", 0.0),
    BELGIUM ("Belgia", "BE", 0.0),
    BOSNIA_AND_HERZEGOVINA ("Bośnia i Hercegowina", "BA", 0.0),
    BULGARIA ("Bułgaria", "BG", 8.0),
    CROATIA ("Chorwacja", "HR", 0.0),
    CYPRUS ("Cypr", "CY", 0.0),
    CZECH_REPUBLIC ("Czechy", "CZ", 11.47),
    DENMARK ("Dania", "DK", 0.0),
    ESTONIA ("Estonia", "EE", 0.0),
    FINLAND ("Finlandia", "FI", 0.0),
    FRANCE ("Francja", "FR", 0.0),
    GERMANY ("Niemcy", "DE", 0.0),
    GIBRALTAR ("Gibraltar", "GI", 0.0),
    GREAT_BRITAIN ("Wielka Brytania", "GB", 0.0),
    GREECE ("Grecja", "GR", 0.0),
    HUNGARY ("Węgry", "HU", 9.58),
    ICELAND ("Islandia", "IS", 0.0),
    IRELAND ("Irlandia", "IE", 0.0),
    ITALY ("Włochy", "IT", 0.0),
    LATVIA ("Łotwa", "LV", 0.0),
    LIECHTENSTEIN ("Liechtenstein", "LI", 0.0),
    LITHUANIA ("Litwa", "LT", 0.0),
    LUXEMBOURG ("Luksemburg", "LU", 0.0),
    MACEDONIA ("Macedonia", "MK", 0.0),
    MALTA ("Malta", "MT", 0.0),
    MOLDOVA ("Mołdawia", "MD", 2.0),
    MONACO ("Monako", "MC", 0.0),
    MONTENEGRO ("Czarnogóra", "ME", 0.0),
    NETHERLANDS ("Holandia", "NL", 0.0),
    NORWAY ("Norwegia", "NO", 0.0),
    POLAND ("Polska", "PL", 0.0),
    PORTUGAL ("Portugalia", "PT", 0.0),
    ROMANIA ("Rumunia", "RO", 3.0),
    RUSSIA ("Rosja", "RU", 0.0),
    SAN_MARINO ("San Marino", "SM", 0.0),
    SERBIA ("Serbia", "RS", 0.0),
    SLOVAKIA ("Słowacja", "SK", 10.0),
    SLOVENIA ("Słowenia", "SI", 15.0),
    SPAIN ("Hiszpania", "ES", 0.0),
    SWEDEN ("Szwecja", "SE", 0.0),
    SWITZERLAND ("Szwajcaria", "CH", 36.51),
    TURKEY ("Turcja", "TR", 0.0),
    UKRAINE ("Ukraina", "UA", 0.0),
    VATICAN ("Watykan", "VA", 0.0);

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
