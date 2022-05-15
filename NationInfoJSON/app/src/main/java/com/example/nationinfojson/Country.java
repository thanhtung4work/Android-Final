package com.example.nationinfojson;

public class Country {
    private final String IMAGE_API = "https://img.geonames.org/flags/x/";
    int id = 1;
    String name;
    String population;
    String countryCode;
    String capital;
    String continent;
    String areaInKm2;
    String currency;
    String imageURL;

    Country(){}

    public Country(String name, String population, String countryCode, String capital, String continent, String areaInKm2, String currency) {
        this.name = name;
        this.population = population;
        this.countryCode = countryCode;
        this.capital = capital;
        this.continent = continent;
        this.areaInKm2 = areaInKm2;
        this.currency = currency;
        imageURL =  IMAGE_API + countryCode.toLowerCase() + ".gif";
    }
    private final int THOUSAND = 1000;
    private final int MILLION = 1000000;
    private final int BILLION = 1000000000;
    public String getFormattedPopulation(){
        int p = Integer.parseInt(population);
        if(p / BILLION > 0){
            double fPopulation = (float) p*1.0 / BILLION;
            return String.format("%.2f billion(s)", fPopulation);
        }else if(p / MILLION > 0){
            double fPopulation = (float) p*1.0 / MILLION;
            return String.format("%.2f million(s)", fPopulation);
        }
        else if (p / THOUSAND > 0){
            double fPopulation = (float) p*1.0 / THOUSAND;
            return String.format("%.2f thousand(s)", fPopulation);
        }
        return String.valueOf(p);
    }

}
