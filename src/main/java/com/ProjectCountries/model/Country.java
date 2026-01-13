package com.ProjectCountries.model;

// Represents a country with basic info
public class Country {
    private final String name;
    private final String capital;
    private final String flagURL;
    private final long population;
    private final String region;
    private final String subregion;
    private final String currencies;
    private final String languages;

    // Constructor for creating a Country object
    public Country(String name, String capital, String flagURL, long population, String region, String subregion, String currencies, String languages){
        this.name = name;
        this.capital = capital;
        this.flagURL = flagURL;
        this.population = population;
        this.region = region;
        this.subregion = subregion;
        this.currencies = currencies;
        this.languages = languages;
    }

    // Getters for getting Country data
    public String getName(){
        return name;
    }

    public String getCapital(){
        return capital;
    }

    public String getFlagURL() {
        return flagURL;
    }

    public long getPopulation() {
        return population;
    }

    public String getRegion(){
        return region;
    }

    public String getSubregion(){
        return subregion;
    }

    public String getCurrencies(){
        return currencies;
    }

    public String getLanguages(){
        return languages;
    }

    // Override toString() to return's the country's name
    @Override
    public String toString(){
        return name;
    }
}
