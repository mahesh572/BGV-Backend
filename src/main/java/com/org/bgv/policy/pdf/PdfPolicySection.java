package com.org.bgv.policy.pdf;


import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PdfPolicySection {

    /**
     * Builds POLICY DETAILS section (Page 2)
     */
    public void createPolicySection(Document document, String htmlContent) {

        // =========================
        // HEADER
        // =========================
        document.add(new Paragraph("POLICY DETAILS")
                .setBold()
                .setFontSize(14)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(10));

        if (StringUtils.isBlank(htmlContent)) {

            document.add(new Paragraph("Policy content unavailable.")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY));

            return;
        }

        // =========================
        // CLEAN HTML CONTENT
        // =========================
        String content = htmlContent
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</p>", "\n\n")
                .replaceAll("(?i)<li>", "• ")
                .replaceAll("(?i)</li>", "\n")
                .replaceAll("<[^>]*>", "") // remove all tags
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();

        String[] lines = content.split("\n");

        for (String line : lines) {

            String text = line.trim();

            if (text.isEmpty()) {
                continue;
            }

            // =========================
            // HEADINGS
            // =========================
            if (isHeading(text)) {

                document.add(new Paragraph(text)
                        .setBold()
                        .setFontSize(11)
                        .setFontColor(ColorConstants.BLACK)
                        .setMarginTop(8)
                        .setMarginBottom(4));

            }
            // =========================
            // BULLETS
            // =========================
            else if (text.startsWith("•")) {

                document.add(new Paragraph(text)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setMarginLeft(15)
                        .setMarginBottom(2));

            }
            // =========================
            // NORMAL PARAGRAPH
            // =========================
            else {

                document.add(new Paragraph(text)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setMarginBottom(5));
            }
        }
    }

    /**
     * Simple heuristic for headings
     */
    private boolean isHeading(String text) {

        return text.matches("^\\d+\\..*")   // 1. 2. 3.
                || text.length() < 60       // short lines
                || text.equals(text.toUpperCase()); // ALL CAPS headings
    }
}
