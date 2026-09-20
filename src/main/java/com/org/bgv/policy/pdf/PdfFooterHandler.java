package com.org.bgv.policy.pdf;


import org.springframework.stereotype.Component;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PdfFooterHandler {

    /**
     * Creates footer section for PDF pages
     */
    public Paragraph createFooter(
            String referenceNo,
            String policyName,
            String policyVersion,
            String acceptedAt,
            int pageNumber,
            int totalPages
    ) {

        Paragraph divider = new Paragraph(
                "------------------------------------------------------------"
        )
                .setFontSize(8)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);

        Paragraph meta = new Paragraph()
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        meta.add("Ref: " + safe(referenceNo));
        meta.add("   |   ");
        meta.add("Policy: " + safe(policyName));
        meta.add("   |   ");
        meta.add("Version: " + safe(policyVersion));
        meta.add("   |   ");
        meta.add("Date: " + safe(acceptedAt));

        Paragraph pageInfo = new Paragraph(
                "Page " + pageNumber + " of " + totalPages
        )
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(3);

        return new Paragraph()
                .add(divider)
                .add(meta)
                .add(pageInfo);
    }

    /**
     * Null-safe value handler
     */
    private String safe(String value) {

        return (value == null || value.isBlank())
                ? "N/A"
                : value;
    }
}
