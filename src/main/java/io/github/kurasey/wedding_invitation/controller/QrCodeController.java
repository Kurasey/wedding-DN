package io.github.kurasey.wedding_invitation.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.github.kurasey.wedding_invitation.config.InvitationParametersHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class QrCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeController.class);
    private final InvitationParametersHolder parametersHolder;

    public QrCodeController(InvitationParametersHolder parametersHolder) {
        this.parametersHolder = parametersHolder;
    }

    @GetMapping(value = "/qr-code.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode() {
        String url = parametersHolder.getTelegramGroupUrl();
        if (url == null || url.isBlank()) {
            logger.warn("Telegram group URL is not configured. Cannot generate QR code.");
            return ResponseEntity.noContent().build();
        }

        try {
            byte[] pngData = generateQrCodeImage(url, 500, 500);
            return ResponseEntity.ok(pngData);
        } catch (WriterException | IOException e) {
            logger.error("Failed to generate QR code for URL: {}", url, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generateQrCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }
}