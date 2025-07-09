package io.github.kurasey.wedding_invitation.controller;

import io.github.kurasey.wedding_invitation.config.AssetLocations;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final AssetLocations assetLocations;

    public GlobalControllerAdvice(AssetLocations assetLocations) {
        this.assetLocations = assetLocations;
    }

    @ModelAttribute("assetLocations")
    public AssetLocations getAssetLocations() {
        return assetLocations;
    }
}