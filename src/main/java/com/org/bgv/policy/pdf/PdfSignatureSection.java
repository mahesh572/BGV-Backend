package com.org.bgv.policy.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.TextAlignment;
import com.org.bgv.s3.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfSignatureSection {
	
	private final S3StorageService s3StorageService;

    /**
     * Builds the CONSENT EVIDENCE section
     * (Digital Signature + Timestamp)
     */
    public Cell createSignatureSection(
            String signatureKey,
            String acceptedAt
    ) {

        Cell cell = new Cell()
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(10);

        // =========================
        // SECTION TITLE
        // =========================
        Paragraph title = new Paragraph("CONSENT EVIDENCE")
                .setBold()
                .setFontSize(11)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(8);

        cell.add(title);

        // =========================
        // SIGNATURE LABEL
        // =========================
        cell.add(new Paragraph("Digital Signature")
                .setFontSize(9)
                .setBold()
                .setMarginBottom(5));

        // =========================
        // SIGNATURE IMAGE
        // =========================
        if (signatureKey != null && !signatureKey.isBlank()) {

            try {
            	
            	byte[] imageBytes = s3StorageService.downloadImageFromS3(signatureKey);

                if (imageBytes != null) {

                    ImageData imageData = ImageDataFactory.create(imageBytes);
                    Image signatureImage = new Image(imageData);

                    signatureImage.scaleToFit(160, 60);
                    signatureImage.setBorder(new SolidBorder(ColorConstants.GRAY, 1));

                    cell.add(signatureImage);

                } else {
                    cell.add(new Paragraph("Signature unavailable")
                            .setFontSize(9)
                            .setFontColor(ColorConstants.RED));
                }

            } catch (Exception ex) {
                log.warn("Unable to load signature image: {}", signatureKey, ex);

                cell.add(new Paragraph("Signature unavailable")
                        .setFontSize(9)
                        .setFontColor(ColorConstants.RED));
            }

        } else {
            cell.add(new Paragraph("No Signature Provided")
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY));
        }

        // =========================
        // TIMESTAMP
        // =========================
        cell.add(new Paragraph(" ")
                .setMarginTop(5));

        cell.add(new Paragraph(
                "Signed At: " + (acceptedAt == null ? "N/A" : acceptedAt)
        )
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY));

        return cell;
    }
    
    private byte[] downloadImage(String urlStr) {
        try {
        	
        	
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");

            try (InputStream in = connection.getInputStream()) {
                return in.readAllBytes();
            }

        } catch (Exception e) {
            log.warn("Failed to download image from URL: {}", urlStr, e);
            return null;
        }
    }
}