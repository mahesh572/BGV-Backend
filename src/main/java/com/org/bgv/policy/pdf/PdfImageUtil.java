package com.org.bgv.policy.pdf;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PdfImageUtil {

    /**
     * Load image from URL safely
     */
    public static Image loadImageFromUrl(String url) {

        try {

            if (url == null || url.isBlank()) {
                return null;
            }

            ImageData imageData =
                    ImageDataFactory.create(new URL(url));

            return new Image(imageData);

        } catch (Exception ex) {

            log.warn("Unable to load image from URL: {}", url, ex);

            return null;
        }
    }

    /**
     * Load and scale image (recommended for PDF usage)
     */
    public static Image loadScaledImage(
            String url,
            float width,
            float height
    ) {

        Image img = loadImageFromUrl(url);

        if (img == null) {
            return null;
        }

        img.scaleToFit(width, height);

        return img;
    }

    /**
     * Safe fallback image text (if image fails)
     */
    public static String fallbackText(String label) {

        return label == null ? "Image unavailable" : label + " unavailable";
    }
}
