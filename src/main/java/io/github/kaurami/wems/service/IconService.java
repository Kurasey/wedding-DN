package io.github.kaurami.wems.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kaurami.wems.config.AssetLocations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IconService {

    private static final Logger logger = LoggerFactory.getLogger(IconService.class);
    private static final String LOCAL_ICONS_PATH_PREFIX = "/images/icons/";

    private final AssetLocations assetLocations;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IconService(AssetLocations assetLocations, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.assetLocations = assetLocations;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, String>> getAvailablePacks() {
        List<Map<String, String>> allPacks = new ArrayList<>();
        allPacks.addAll(getLocalPacks());
        allPacks.addAll(getRemotePacks());
        return allPacks;
    }

    public List<String> getIconUrls(String type, String packName) {
        if ("local".equals(type)) {
            return getLocalIconUrls(packName);
        } else if ("remote".equals(type)) {
            return getRemoteIconUrls(packName);
        }
        return List.of();
    }

    private List<Map<String, String>> getLocalPacks() {
        try {
            Resource manifestResource = new PathMatchingResourcePatternResolver()
                    .getResource("classpath:/static/images/icons/manifest.json");

            if (manifestResource.exists()) {
                return List.of(Map.of("type", "local", "name", "Embedded Icons", "path", "local"));
            } else {
                return List.of();
            }
        } catch (Exception e) {
            logger.warn("Could not find local icon manifest", e);
            return List.of();
        }
    }

    private List<String> getLocalIconUrls(String packName) {
        try {
            Resource manifestResource = new PathMatchingResourcePatternResolver()
                    .getResource("classpath:/static/images/icons/manifest.json");

            if (!manifestResource.exists()) {
                logger.warn("Local manifest.json not found in icons folder");
                return List.of();
            }

            Map<String, List<String>> manifest = objectMapper.readValue(
                    manifestResource.getInputStream(),
                    new TypeReference<>() {}
            );
            return manifest.getOrDefault("icons", List.of()).stream()
                    .map(file -> LOCAL_ICONS_PATH_PREFIX + file)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error("Error reading local icon manifest", e);
            return List.of();
        }
    }


    private List<Map<String, String>> getRemotePacks() {
        String manifestUrl = assetLocations.getBaseUrl() + "images/icons/master-manifest.json";
        try {
            String json = cleanJsonString(restTemplate.getForObject(manifestUrl, String.class));
            List<Map<String, String>> packs = objectMapper.readValue(json, new TypeReference<>() {});
            return packs.stream()
                    .map(pack -> Map.of("type", "remote", "name", "CDN: " + pack.get("name"), "path", pack.get("folder")))
                    .collect(Collectors.toList());
        } catch (RestClientException | IOException e) {
            logger.error("Failed to fetch or parse remote master manifest from {}", manifestUrl, e.getMessage());
            return List.of();
        }
    }

    private List<String> getRemoteIconUrls(String packName) {
        try {
            String manifestUrl = assetLocations.getBaseUrl() + "images/icons/" + packName + "/manifest.json";
            String json = cleanJsonString(restTemplate.getForObject(manifestUrl, String.class));
            Map<String, List<String>> manifest = objectMapper.readValue(json, new TypeReference<>() {});

            return manifest.getOrDefault("icons", List.of()).stream()
                    .map(iconFile -> {
                        String encodedIconFile = URLEncoder.encode(iconFile, StandardCharsets.UTF_8).replace("+", "%20");
                        return assetLocations.getBaseUrl() + "images/icons/" + packName + "/" + encodedIconFile;
                    })
                    .collect(Collectors.toList());
        } catch (RestClientException | IOException e) {
            logger.error("Failed to fetch or parse remote pack manifest for pack '{}'", packName, e.getMessage());
            return List.of();
        }
    }

    private String cleanJsonString(String json) {
        if (json != null && json.startsWith("\uFEFF")) {
            return json.substring(1);
        }
        return json;
    }
}