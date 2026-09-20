package com.org.bgv.policy.pdf;


import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.net.URL;

import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;

import com.org.bgv.common.UserDto;
import com.org.bgv.policy.entity.PolicyConsent;
import com.org.bgv.s3.S3StorageService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PdfApplicantSection {
	
	private final S3StorageService s3StorageService;

    /**
     * ENTRY POINT
     * This builds the entire Applicant Section (Table + Photo + Basic Info)
     */
    public void buildApplicantSection(
            Document document,
            PolicyConsent consent,
            UserDto user,
            String livePhotoKey
    ) {

        Paragraph heading = new Paragraph("APPLICANT DETAILS")
                .setBold()
                .setFontSize(13)
                .setFontColor(ColorConstants.BLACK)
                .setMarginBottom(10);

        document.add(heading);

        Table table = createApplicantTable(user, consent, livePhotoKey);

        document.add(table);

        document.add(new Paragraph(" "));
    }
    
    
    private Table createApplicantTable(
            UserDto user,
            PolicyConsent consent,
            String livePhotoKey
    ) {

        // 2-column layout: Left (details) | Right (photo)
        Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .useAllAvailableWidth();

        // =========================
        // LEFT SIDE - DETAILS
        // =========================
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();

        addRow(detailsTable, "Full Name",
                getFullName(user));

        addRow(detailsTable, "Email",
                user != null ? user.getEmail() : "N/A");

        addRow(detailsTable, "Mobile", user != null ? user.getPhoneNumber() : "N/A");

      //  addRow(detailsTable, "Entity Type", consent.getEntityType() != null ? consent.getEntityType().name() : "N/A");

      //  addRow(detailsTable, "Entity ID",  consent.getEntityId());

        addRow(detailsTable, "Reference No",
                consent.getReferenceNumber());

        addRow(detailsTable, "Policy",
                consent.getPolicyVersion().getPolicy().getName());

        addRow(detailsTable, "Version",
                consent.getPolicyVersion().getVersion());

        addRow(detailsTable, "Accepted On",
                consent.getAcceptedAt() != null
                        ? consent.getAcceptedAt().toString()
                        : "N/A");

        Cell leftCell = new Cell()
                .add(detailsTable)
                .setBorder(null)
                .setPadding(5);

        // =========================
        // RIGHT SIDE - PHOTO
        // =========================
        Cell rightCell = createPhotoCell(livePhotoKey);

        table.addCell(leftCell);
        table.addCell(rightCell);

        return table;
    }
    
    private String getFullName(UserDto user) {

        if (user == null) {
            return "N/A";
        }

        String first = user.getFirstName() == null ? "" : user.getFirstName();
        String last = user.getLastName() == null ? "" : user.getLastName();

        return (first + " " + last).trim().isEmpty()
                ? "N/A"
                : (first + " " + last).trim();
    }
    
    private void addRow(Table table, String label, String value) {

        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setFontSize(9)
                        .setBold()
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(PdfConstants.TABLE_HEADER)
                .setPadding(6);

        Cell valueCell = new Cell()
                .add(new Paragraph(value == null ? "N/A" : value)
                        .setFontSize(9))
                .setPadding(6);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    
    
    private Cell createPhotoCell(String livePhotoKey) {

        Cell cell = new Cell()
                .setBorder(null)
                .setPadding(5);

        Paragraph title = new Paragraph("LIVE PHOTO")
                .setBold()
                .setFontSize(10)
                .setMarginBottom(8);

        cell.add(title);

        try {

            if (livePhotoKey != null && !livePhotoKey.isBlank()) {

            	byte[] imageBytes = s3StorageService.downloadImageFromS3(livePhotoKey);
            	
            	 if (imageBytes != null) {

                     ImageData imageData = ImageDataFactory.create(imageBytes);
                     Image livephotoImage = new Image(imageData);

                     livephotoImage.scaleToFit(120, 150);
                     livephotoImage.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));

                     cell.add(livephotoImage);
                 }

            } else {

                cell.add(new Paragraph("No Live Photo")
                        .setFontSize(9)
                        .setFontColor(ColorConstants.GRAY));
            }
            
            

        } catch (Exception e) {

            cell.add(new Paragraph("Photo unavailable")
                    .setFontSize(9)
                    .setFontColor(ColorConstants.RED));
        }

        return cell;
    }
    
    private Cell createSignatureSection(
            String signatureUrl,
            String acceptedAt
    ) {

        Cell cell = new Cell()
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));

        Paragraph title = new Paragraph("CONSENT EVIDENCE")
                .setBold()
                .setFontSize(11)
                .setMarginBottom(8);

        cell.add(title);

        Paragraph sigLabel = new Paragraph("Digital Signature")
                .setFontSize(9)
                .setBold()
                .setMarginBottom(5);

        cell.add(sigLabel);

        try {

            if (signatureUrl != null && !signatureUrl.isBlank()) {

                Image signature = new Image(
                        com.itextpdf.io.image.ImageDataFactory.create(new URL(signatureUrl))
                );

                signature.scaleToFit(140, 60);

                cell.add(signature);

            } else {

                cell.add(new Paragraph("No Signature")
                        .setFontSize(9)
                        .setFontColor(ColorConstants.GRAY));
            }

        } catch (Exception e) {

            cell.add(new Paragraph("Signature unavailable")
                    .setFontSize(9)
                    .setFontColor(ColorConstants.RED));
        }

        cell.add(new Paragraph("\n"));

        cell.add(new Paragraph("Signed At: " + (acceptedAt == null ? "N/A" : acceptedAt))
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY));

        return cell;
    }
    
    private Paragraph createDeclarationSection() {

        Paragraph heading = new Paragraph("DECLARATION")
                .setBold()
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setMarginTop(10)
                .setMarginBottom(5);

        Paragraph text = new Paragraph(
                "I hereby declare that all the information provided by me is true and correct. " +
                "I understand that any false information may lead to rejection of my application. " +
                "I voluntarily consent to background verification as per the applicable policy.")
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(10);

        Paragraph container = new Paragraph()
                .add(heading)
                .add(text);

        return container;
    }
    
    private Paragraph createFooterSection(
            String referenceNo,
            String policyName,
            String policyVersion,
            String acceptedAt,
            int pageNumber,
            int totalPages
    ) {

        Paragraph divider = new Paragraph(
                "--------------------------------------------------------------")
                .setFontSize(8)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);

        Paragraph footer = new Paragraph()
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        footer.add("Ref: " + safe(referenceNo));
        footer.add("   |   ");
        footer.add("Policy: " + safe(policyName));
        footer.add("   |   ");
        footer.add("Version: " + safe(policyVersion));
        footer.add("   |   ");
        footer.add("Date: " + safe(acceptedAt));

        Paragraph page = new Paragraph(
                "Page " + pageNumber + " of " + totalPages)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(3);

        Paragraph container = new Paragraph()
                .add(divider)
                .add(footer)
                .add(page);

        return container;
    }
    
    private String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }
    
    private void createPolicySection(Document document, String htmlContent) {

        if (htmlContent == null || htmlContent.isBlank()) {

            document.add(new Paragraph("Policy content unavailable.")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY));

            return;
        }

        // ===============================
        // BASIC HTML CLEANING
        // ===============================
        String content = htmlContent
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</p>", "\n\n")
                .replaceAll("(?i)<li>", "• ")
                .replaceAll("(?i)</li>", "\n")
                .replaceAll("<[^>]*>", "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();

        String[] lines = content.split("\n");

        for (String line : lines) {

            String text = line.trim();

            if (text.isEmpty()) continue;

            // ===============================
            // HEADINGS (simple detection)
            // ===============================
            if (text.matches("^\\d+\\..*") || text.length() < 60) {

                document.add(new Paragraph(text)
                        .setBold()
                        .setFontSize(11)
                        .setMarginTop(8)
                        .setMarginBottom(4));

            }
            // ===============================
            // BULLETS
            // ===============================
            else if (text.startsWith("•")) {

                document.add(new Paragraph(text)
                        .setFontSize(10)
                        .setMarginLeft(15)
                        .setMarginBottom(2));

            }
            // ===============================
            // NORMAL PARAGRAPH
            // ===============================
            else {

                document.add(new Paragraph(text)
                        .setFontSize(10)
                        .setMarginBottom(5));
            }
        }
    }
    
    
}
