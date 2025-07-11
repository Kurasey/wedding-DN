package io.github.kaurami.wems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "assets")
public class AssetLocations {

    private String baseUrl;
    private String mainBackground;
    private String venuePhoto;
    private String backgroundMusic;

    public String getMainBackground() {
        return baseUrl + mainBackground;
    }

    public String getVenuePhoto() {
        return baseUrl + venuePhoto;
    }

    public String getBackgroundMusic() {
        return baseUrl + backgroundMusic;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setMainBackground(String mainBackground) {
        this.mainBackground = mainBackground;
    }

    public void setVenuePhoto(String venuePhoto) {
        this.venuePhoto = venuePhoto;
    }

    public void setBackgroundMusic(String backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }
}