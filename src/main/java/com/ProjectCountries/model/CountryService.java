package com.ProjectCountries.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Class to fetch data from the REST Countries API
public class CountryService {
    // Logger for debugging
    private static final Logger logger = LogManager.getLogger(CountryService.class);

    // URL to API for European countries
    private static final String API_URL = "https://restcountries.com/v3.1/region/europe";

    // Fetches a list of countries from the API and maps them to Country objects
    public List<Country> fetchCountries(){
        List<Country> countries = new ArrayList<>();

        try{
            logger.info("XDownloading data from API...");

            // Connect to API endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("GET");

            // Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String inputLine;

            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.info("Parsing data...");
            // Parse response as a JSON array
            JSONArray jsonArray = new JSONArray(response.toString());

            // Extract country data
            for (int i=0; i< jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);

                String name = obj.getJSONObject("name").getString("common");
                String capital = obj.has("capital") ? obj.getJSONArray("capital").optString(0, "N/A") : "N/A";
                String flagURL = obj.getJSONObject("flags").getString("png");
                long population = obj.getLong("population");

                // Parse languages
                JSONObject languagesObj = obj.optJSONObject("languages");
                StringBuilder langs = new StringBuilder();
                if (languagesObj != null) {
                    for (String key : languagesObj.keySet()) {
                        langs.append(languagesObj.getString(key)).append(", ");
                    }
                }
                String languages = langs.length() > 0 ? langs.substring(0, langs.length() - 2) : "N/A";

                // Parse currencies
                JSONObject currencyObj = obj.optJSONObject("currencies");
                StringBuilder currs = new StringBuilder();
                if (currencyObj != null) {
                    for (String key : currencyObj.keySet()) {
                        JSONObject currency = currencyObj.optJSONObject(key);
                        if (currency != null && currency.has("name")) {
                            currs.append(currency.getString("name")).append(", ");
                        }
                    }
                }
                String currencies = currs.length() > 0 ? currs.substring(0, currs.length() - 2) : "N/A";

                // Get region and subregion
                String region = obj.optString("region", "N/A");
                String subregion = obj.optString("subregion", "N/A");

                countries.add(new Country(name, capital, flagURL, population, region, subregion, currencies, languages));
            }
            logger.info(countries);
            countries.sort(Comparator.comparing(Country::getName));
            logger.info("xdLoad " + countries.size() + " countries.");
        } catch (Exception e) {
            logger.error("Error during downloading data: ", e);
        }

        //countries.sort(null);
        logger.info(countries);

        return countries;
    }
}
