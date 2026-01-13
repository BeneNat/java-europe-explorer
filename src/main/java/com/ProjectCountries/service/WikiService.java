package com.ProjectCountries.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.mom.JmsAppender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WikiService {
    // Logger for debugging
    private static final Logger logger = LogManager.getLogger(WikiService.class);

    // Cache to store previously fetched results
    private static final Map<String, String> cache = new HashMap<>();

    // JSON file to persist cache between sessions
    private static final File cacheFile = new File("wiki_cache.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    // Load cache from file on class load
    static {
        if(cacheFile.exists()){
            try{
                Map<String, String> savedCache = mapper.readValue(cacheFile, new TypeReference<>() {});
                cache.putAll(savedCache);
                logger.info("Loaded cache from file.");
            } catch (Exception e) {
                logger.warn("Error during loading cache from file", e);
            }
        }
    }

    // Fetch image URL of a country from Wikipedia Infobox
    public String fetchCountryImageURL(String countryName) {
        try {
            String url = "https://en.wikipedia.org/wiki/" + countryName.replace(" ", "_");
            Document doc = Jsoup.connect(url).get();
            // Znajdź pierwszy obrazek z infoboxa
            var img = doc.select("table.infobox img").first();
            if (img != null) {
                String src = img.attr("src");
                if (!src.startsWith("http")) {
                    src = "https:" + src;
                }
                return src;
            }
        } catch (IOException e) {
            logger.warn("Failed to fetch image for: " + countryName, e);
        }
        return null;
    }

    // Fetch introductory paragraph from Wikipedia
    public String fetchIntroParagraph(String countryName){
        // Return cached result if exists
        if(cache.containsKey(countryName)){
            logger.info("Data from cashe returned for " + countryName);
            return cache.get(countryName);
        }
        try {
            String url = "https://en.wikipedia.org/wiki/" + countryName.replace(" ", "_");
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");

            // Look for the first paragraph
            for (var p : paragraphs) {
                String text = p.text();
                // If paragraph text is longer than 100
                if(text.length() > 100) {
                    cache.put(countryName, text);
                    saveCacheToFile();
                    logger.info("Text for country " + countryName + " was downloaded.");
                    return text;
                }
            }

            // No valid paragraph found
            cache.put(countryName, "Data not found.");
            saveCacheToFile();
            return "Specific details was not found!";
        } catch (Exception e) {
            logger.warn("Could not fetch Wikipedia information for: " + countryName);
            return "No data.";
        }
    }

    // Save current cache to JSON file
    private void saveCacheToFile(){
        try{
            mapper.writerWithDefaultPrettyPrinter().writeValue(cacheFile, cache);
            logger.info("Cash was saved to file.");
        } catch (IOException e) {
            logger.warn("Error saving cache to file", e);
        }
    }
}
