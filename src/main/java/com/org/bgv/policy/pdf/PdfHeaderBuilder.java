package com.org.bgv.policy.pdf;


import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;


@Component
public class PdfHeaderBuilder {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

    /**
     * Builds Page-1 Header
     */
    public void buildHeader(Document document,
                            String referenceNumber,
                            LocalDateTime acceptedAt) {

        addCompanyHeader(document);
        addDocumentTitle(document);
        addReferenceInformation(document, referenceNumber, acceptedAt);

        document.add(
                new LineSeparator(
                        new SolidLine())
                        .setMarginTop(8)
                        .setMarginBottom(15));
    }

    /**
     * Builds Page-2 Header
     */
    public void buildPolicyHeader(Document document,
                                  String referenceNumber,
                                  String policyName,
                                  String policyVersion) {

        addCompanyHeader(document);

        Paragraph title =
                new Paragraph(PdfConstants.POLICY_DETAILS)
                        .setBold()
                        .setFontSize(16)
                        .setFontColor(PdfConstants.PRIMARY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);

        document.add(title);

        Paragraph info =
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY);

        info.add("Reference : ");
        info.add(referenceNumber);
        info.add("     |     ");
        info.add("Policy : ");
        info.add(policyName);
        info.add("     |     ");
        info.add("Version : ");
        info.add(policyVersion);

        document.add(info);

        document.add(
                new LineSeparator(
                        new SolidLine())
                        .setMarginTop(10)
                        .setMarginBottom(20));
    }

    /**
     * Company Name + Subtitle
     */
    private void addCompanyHeader(Document document) {

        Paragraph company =
                new Paragraph(PdfConstants.COMPANY_NAME)
                        .setBold()
                        .setFontSize(PdfConstants.TITLE_FONT)
                        .setFontColor(PdfConstants.PRIMARY)
                        .setTextAlignment(TextAlignment.CENTER);

        document.add(company);

        Paragraph subtitle =
                new Paragraph(PdfConstants.COMPANY_SUBTITLE)
                        .setFontSize(PdfConstants.SUB_TITLE_FONT)
                        .setFontColor(PdfConstants.SECONDARY)
                        .setTextAlignment(TextAlignment.CENTER);

        document.add(subtitle);

        Paragraph website =
                new Paragraph(PdfConstants.COMPANY_WEBSITE)
                        .setFontSize(9)
                        .setFontColor(ColorConstants.BLUE)
                        .setTextAlignment(TextAlignment.CENTER);

        document.add(website);

        document.add(new Paragraph(" "));
    }

    /**
     * Main Document Title
     */
    private void addDocumentTitle(Document document) {

        Paragraph title =
                new Paragraph(PdfConstants.DOCUMENT_TITLE)
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(PdfConstants.PRIMARY)
                        .setMarginBottom(8);

        document.add(title);

        Paragraph subTitle =
                new Paragraph(
                        "Verification Consent Form")
                        .setFontSize(11)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER);

        document.add(subTitle);

        document.add(new Paragraph(" "));
    }

    /**
     * Reference Details
     */
    private void addReferenceInformation(Document document,
                                         String referenceNumber,
                                         LocalDateTime acceptedAt) {

        String acceptedDate =
                acceptedAt == null
                        ? "-"
                        : acceptedAt.format(DATE_FORMATTER);

        String acceptedDateTime =
                acceptedAt == null
                        ? "-"
                        : acceptedAt.format(DATE_TIME_FORMATTER);

        Paragraph ref =
                new Paragraph()
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT);

        ref.add("Reference No : ");
        ref.add(referenceNumber);

        document.add(ref);

        Paragraph date =
                new Paragraph()
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT);

        date.add("Date : ");
        date.add(acceptedDate);

        document.add(date);

        Paragraph dateTime =
                new Paragraph()
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT);

        dateTime.add("Accepted On : ");
        dateTime.add(acceptedDateTime);

        document.add(dateTime);

        document.add(new Paragraph(" "));
    }

    /**
     * Authorization Paragraph
     */
    public void addAuthorization(Document document) {

        Paragraph p =
                new Paragraph(PdfConstants.AUTHORIZATION_TEXT)
                        .setFontSize(PdfConstants.BODY_FONT)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setMarginBottom(15);

        document.add(p);
    }

    /**
     * Declaration Heading
     */
    public void addDeclarationHeading(Document document) {

        Paragraph heading =
                new Paragraph(PdfConstants.DECLARATION)
                        .setBold()
                        .setFontSize(PdfConstants.SECTION_TITLE_FONT)
                        .setFontColor(PdfConstants.PRIMARY)
                        .setMarginTop(15)
                        .setMarginBottom(8);

        document.add(heading);
    }

    /**
     * Declaration Text
     */
    public void addDeclaration(Document document) {

        Paragraph declaration =
                new Paragraph(PdfConstants.DECLARATION_TEXT)
                        .setFontSize(PdfConstants.BODY_FONT)
                        .setTextAlignment(TextAlignment.JUSTIFIED);

        document.add(declaration);

        document.add(new Paragraph(" "));
    }

}
