package com.org.bgv.invoice.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.org.bgv.invoice.dto.InvoiceDTO;
import com.org.bgv.invoice.dto.InvoiceItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfGeneratorService {

    private static final String COMPANY_NAME = "BGV Solutions Pvt Ltd";
    private static final String COMPANY_ADDRESS = "123, Tech Park, Bangalore - 560001";
    private static final String COMPANY_PHONE = "+91 80 1234 5678";
    private static final String COMPANY_EMAIL = "billing@bgvsolutions.com";
    private static final String COMPANY_GST = "29ABCDE1234F1Z5";
    private static final String COMPANY_PAN = "AAACB1234F";

    public byte[] generateInvoicePdf(InvoiceDTO invoice) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // Load fonts
            PdfFont normalFont = PdfFontFactory.createFont();
            PdfFont boldFont = PdfFontFactory.createFont();
            PdfFont italicFont = PdfFontFactory.createFont();

            // Add content
            addHeader(document, boldFont, normalFont);
            addCompanyAndCustomerInfo(document, invoice, normalFont, boldFont);
            addInvoiceDetails(document, invoice, normalFont, boldFont);
            addInvoiceItemsTable(document, invoice, normalFont, boldFont);
            addTotalsSection(document, invoice, normalFont, boldFont);
            addFooter(document, normalFont, italicFont);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for invoice: {}", invoice.getInvoiceNumber(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addHeader(Document document, PdfFont boldFont, PdfFont normalFont) throws Exception {
        // Company Name
        Paragraph title = new Paragraph(COMPANY_NAME)
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);

        // Company Address
        Paragraph address = new Paragraph(COMPANY_ADDRESS)
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2);
        document.add(address);

        // Contact
        Paragraph contact = new Paragraph("Phone: " + COMPANY_PHONE + " | Email: " + COMPANY_EMAIL)
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2);
        document.add(contact);

        // GST & PAN
        Paragraph taxInfo = new Paragraph("GST No: " + COMPANY_GST + " | PAN No: " + COMPANY_PAN)
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(taxInfo);

        // Separator line
        addLineSeparator(document);
        document.add(new Paragraph("\n"));
    }

    private void addCompanyAndCustomerInfo(Document document, InvoiceDTO invoice, 
                                          PdfFont normalFont, PdfFont boldFont) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(20);

        // Bill To Section
        Cell billToCell = new Cell();
        billToCell.setBorder(Border.NO_BORDER);
        
        Paragraph billToTitle = new Paragraph("Bill To:")
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginBottom(5);
        billToCell.add(billToTitle);
        
        Paragraph companyName = new Paragraph(invoice.getCompanyName())
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        billToCell.add(companyName);
        
        Paragraph attention = new Paragraph("Attention: HR Department")
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        billToCell.add(attention);
        
        if (invoice.getCandidateName() != null) {
            Paragraph candidate = new Paragraph("Candidate: " + invoice.getCandidateName())
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setMarginBottom(2);
            billToCell.add(candidate);
        }
        
        infoTable.addCell(billToCell);

        // Invoice Info Section
        Cell infoCell = new Cell();
        infoCell.setBorder(Border.NO_BORDER);
        infoCell.setTextAlignment(TextAlignment.RIGHT);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        Paragraph invoiceTitle = new Paragraph("INVOICE")
                .setFont(boldFont)
                .setFontSize(16)
                .setMarginBottom(5);
        infoCell.add(invoiceTitle);
        
        Paragraph invoiceNo = new Paragraph("Invoice No: " + invoice.getInvoiceNumber())
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        infoCell.add(invoiceNo);
        
        Paragraph invoiceDate = new Paragraph("Invoice Date: " + invoice.getInvoiceDate().format(formatter))
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        infoCell.add(invoiceDate);
        
        Paragraph dueDate = new Paragraph("Due Date: " + invoice.getDueDate().format(formatter))
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        infoCell.add(dueDate);
        
        Paragraph caseNo = new Paragraph("Case No: " + invoice.getCaseReferenceNumber())
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(2);
        infoCell.add(caseNo);
        
        infoTable.addCell(infoCell);
        
        document.add(infoTable);
    }

    private void addInvoiceDetails(Document document, InvoiceDTO invoice, 
                                   PdfFont normalFont, PdfFont boldFont) {
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        detailsTable.setWidth(UnitValue.createPercentValue(100));
        detailsTable.setMarginBottom(15);

        // Payment Status
        Cell statusLabel = new Cell()
                .add(new Paragraph("Payment Status:").setFont(boldFont).setFontSize(10))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(statusLabel);
        
        String statusText = getStatusText(invoice.getStatus());
        Color statusColor = getStatusColor(invoice.getStatus());
        
        Cell statusValue = new Cell()
                .add(new Paragraph(statusText).setFont(boldFont).setFontSize(10).setFontColor(statusColor))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(statusValue);
        
        // Payment Terms
        Cell termsLabel = new Cell()
                .add(new Paragraph("Payment Terms:").setFont(boldFont).setFontSize(10))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(termsLabel);
        
        Cell termsValue = new Cell()
                .add(new Paragraph("Net 30 days").setFont(normalFont).setFontSize(10))
                .setBorder(Border.NO_BORDER);
        detailsTable.addCell(termsValue);
        
        document.add(detailsTable);
    }

    private void addInvoiceItemsTable(Document document, InvoiceDTO invoice,
                                      PdfFont normalFont, PdfFont boldFont) {
        // Create table with 5 columns
        float[] columnWidths = {5, 40, 15, 10, 30};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(20);

        // Header Row
        addTableHeader(table, "Sl No", boldFont);
        addTableHeader(table, "Description", boldFont);
        addTableHeader(table, "Category", boldFont);
        addTableHeader(table, "Qty", boldFont);
        addTableHeader(table, "Amount (₹)", boldFont);

        // Data Rows
        List<InvoiceItemDTO> items = invoice.getItems();
        int slNo = 1;
        
        for (InvoiceItemDTO item : items) {
            addTableCell(table, String.valueOf(slNo++), normalFont, TextAlignment.CENTER);
            addTableCell(table, item.getDescription(), normalFont, TextAlignment.LEFT);
            addTableCell(table, item.getCategory(), normalFont, TextAlignment.CENTER);
            addTableCell(table, item.getQuantity().toString(), normalFont, TextAlignment.CENTER);
            addTableCell(table, formatAmount(item.getTotalPrice()), normalFont, TextAlignment.RIGHT);
        }

        document.add(table);
    }

    private void addTotalsSection(Document document, InvoiceDTO invoice,
                                  PdfFont normalFont, PdfFont boldFont) {
        Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
        totalsTable.setWidth(UnitValue.createPercentValue(100));
        totalsTable.setTextAlignment(TextAlignment.RIGHT);

        // Empty cells for spacing
        Cell emptyCell = new Cell().setBorder(Border.NO_BORDER);
        emptyCell.setHeight(10);
        
        // Add-on Total
        addTotalRow(totalsTable, "Add-on Services:", formatAmount(invoice.getAddonTotal()), normalFont);
        
        // Subtotal
        BigDecimal subtotal = invoice.getAddonTotal();
        addTotalRow(totalsTable, "Subtotal:", formatAmount(subtotal), boldFont);
        
        // Tax
        addTotalRow(totalsTable, "GST (18%):", formatAmount(invoice.getTaxAmount()), normalFont);
        
        // Separator
        Cell separatorCell = new Cell(1, 2).setBorder(Border.NO_BORDER);
        separatorCell.add(new Paragraph(" "));
        totalsTable.addCell(separatorCell);
        
        // Grand Total
        Cell grandTotalLabel = new Cell()
                .add(new Paragraph("Grand Total:").setFont(boldFont).setFontSize(14))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        totalsTable.addCell(grandTotalLabel);
        
        Cell grandTotalValue = new Cell()
                .add(new Paragraph(formatAmount(invoice.getGrandTotal())).setFont(boldFont).setFontSize(14))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        totalsTable.addCell(grandTotalValue);
        
        document.add(totalsTable);
        document.add(new Paragraph("\n"));
        
        // Amount in words
        Paragraph amountInWords = new Paragraph("Amount in words: " + convertToWords(invoice.getGrandTotal()))
                .setFont(normalFont)
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(amountInWords);
        document.add(new Paragraph("\n"));
    }

    private void addFooter(Document document, PdfFont normalFont, PdfFont italicFont) {
        // Separator
        addLineSeparator(document);
        document.add(new Paragraph("\n"));
        
        // Bank Details
        Paragraph bankTitle = new Paragraph("Bank Details:")
                .setFont(normalFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(bankTitle);
        
        Paragraph bankInfo = new Paragraph("Bank Name: HDFC Bank | Account Name: BGV Solutions | Account No: 1234567890 | IFSC: HDFC0001234")
                .setFont(normalFont)
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(bankInfo);
        
        document.add(new Paragraph("\n"));
        
        // Terms & Conditions
        Paragraph termsTitle = new Paragraph("Terms & Conditions:")
                .setFont(normalFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(termsTitle);
        
        Paragraph termsText = new Paragraph("1. Payment is due within 30 days of invoice date.\n2. Late payments may incur additional charges.\n3. For any queries, please contact billing@bgvsolutions.com")
                .setFont(italicFont)
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(termsText);
        
        document.add(new Paragraph("\n"));
        
        // Thank you message
        Paragraph thanks = new Paragraph("Thank you for your business!")
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(thanks);
    }

    // Helper methods
    private void addLineSeparator(Document document) {
        Paragraph line = new Paragraph("__________________________________________________")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.LIGHT_GRAY);
        document.add(line);
    }

    private void addTableHeader(Table table, String text, PdfFont boldFont) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        table.addCell(cell);
    }

    private void addTableCell(Table table, String text, PdfFont font, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setTextAlignment(alignment)
                .setPadding(6);
        table.addCell(cell);
    }

    private void addTotalRow(Table table, String label, String value, PdfFont font) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(labelCell);
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value).setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(valueCell);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "₹ 0.00";
        return "₹ " + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "PAID": return "✓ PAID";
            case "PENDING": return "⏳ PENDING - Awaiting Payment";
            case "OVERDUE": return "⚠ OVERDUE - Payment Past Due";
            case "CANCELLED": return "✗ CANCELLED";
            default: return status;
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "PAID": return ColorConstants.GREEN;
            case "OVERDUE": return ColorConstants.RED;
            case "PENDING": return ColorConstants.ORANGE;
            default: return ColorConstants.DARK_GRAY;
        }
    }

    private String convertToWords(BigDecimal amount) {
        long rupees = amount.longValue();
        int paise = amount.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        
        String words = NumberToWordsConverter.convert(rupees) + " Rupees";
        if (paise > 0) {
            words += " and " + NumberToWordsConverter.convert(paise) + " Paise";
        }
        words += " Only";
        
        return words;
    }
}

// Helper class for number to words conversion
class NumberToWordsConverter {
    private static final String[] units = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    };
    
    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };
    
    public static String convert(long n) {
        if (n < 0) return "Minus " + convert(-n);
        if (n < 20) return units[(int) n];
        if (n < 100) return tens[(int) n / 10] + ((n % 10 != 0) ? " " + units[(int) n % 10] : "");
        if (n < 1000) return units[(int) n / 100] + " Hundred" + ((n % 100 != 0) ? " " + convert(n % 100) : "");
        if (n < 100000) return convert(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " + convert(n % 1000) : "");
        if (n < 10000000) return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " + convert(n % 100000) : "");
        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " + convert(n % 10000000) : "");
    }
}