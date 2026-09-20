package com.org.bgv.policy.pdf;


import org.springframework.stereotype.Component;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TextAlignment;

@Component
public final class PdfConstants {

    private PdfConstants() {
    }

    /*
     * =====================================================
     * COMPANY
     * =====================================================
     */

    public static final String COMPANY_NAME =
            "Verisyn";

    public static final String COMPANY_SUBTITLE =
            "Background Verification Platform";

    public static final String COMPANY_WEBSITE =
            "https://verisyn.in";

    /*
     * =====================================================
     * DOCUMENT
     * =====================================================
     */

    public static final String DOCUMENT_TITLE =
            "BACKGROUND VERIFICATION CONSENT";

    public static final String APPLICANT_DETAILS =
            "APPLICANT DETAILS";

    public static final String DECLARATION =
            "DECLARATION";

    public static final String CONSENT_EVIDENCE =
            "CONSENT EVIDENCE";

    public static final String DIGITAL_SIGNATURE =
            "Digital Signature";

    public static final String LIVE_PHOTO =
            "Live Photograph";

    public static final String POLICY_DETAILS =
            "POLICY DETAILS";

    /*
     * =====================================================
     * COLORS
     * =====================================================
     */

    public static final Color PRIMARY =
            new DeviceRgb(26, 35, 126);

    public static final Color SECONDARY =
            new DeviceRgb(66, 66, 66);

    public static final Color HEADER_BACKGROUND =
            new DeviceRgb(232, 240, 254);

    public static final Color TABLE_HEADER =
            new DeviceRgb(240, 240, 240);

    public static final Color BORDER =
            new DeviceRgb(210, 210, 210);

    public static final Color SUCCESS =
            new DeviceRgb(56, 142, 60);

    public static final Color LIGHT_BACKGROUND =
            new DeviceRgb(250, 250, 250);

    /*
     * =====================================================
     * FONT SIZES
     * =====================================================
     */

    public static final float TITLE_FONT = 20F;

    public static final float SUB_TITLE_FONT = 11F;

    public static final float SECTION_TITLE_FONT = 13F;

    public static final float BODY_FONT = 10F;

    public static final float SMALL_FONT = 9F;

    public static final float FOOTER_FONT = 8F;

    /*
     * =====================================================
     * IMAGE SIZE
     * =====================================================
     */

    public static final float PHOTO_WIDTH = 140F;

    public static final float PHOTO_HEIGHT = 170F;

    public static final float SIGNATURE_WIDTH = 180F;

    public static final float SIGNATURE_HEIGHT = 70F;

    public static final float QR_SIZE = 90F;

    /*
     * =====================================================
     * PAGE
     * =====================================================
     */

    public static final float PAGE_MARGIN_TOP = 35F;

    public static final float PAGE_MARGIN_BOTTOM = 35F;

    public static final float PAGE_MARGIN_LEFT = 35F;

    public static final float PAGE_MARGIN_RIGHT = 35F;

    /*
     * =====================================================
     * TABLE
     * =====================================================
     */

    public static final float[] DETAILS_TABLE =
            {30F, 70F};

    public static final float[] PHOTO_TABLE =
            {70F, 30F};

    public static final float[] SIGNATURE_TABLE =
            {50F, 50F};

    /*
     * =====================================================
     * TEXT ALIGNMENT
     * =====================================================
     */

    public static final TextAlignment CENTER =
            TextAlignment.CENTER;

    public static final TextAlignment LEFT =
            TextAlignment.LEFT;

    public static final TextAlignment RIGHT =
            TextAlignment.RIGHT;

    public static final TextAlignment JUSTIFIED =
            TextAlignment.JUSTIFIED;

    /*
     * =====================================================
     * DEFAULT TEXT
     * =====================================================
     */

    public static final String NA = "N/A";

    public static final String NO_SIGNATURE =
            "No Digital Signature";

    public static final String NO_PHOTO =
            "No Live Photograph";

    public static final String POLICY_UNAVAILABLE =
            "Policy content unavailable.";

    public static final String AUTHORIZATION_TEXT =
            "I hereby authorize Verisyn and its authorized "
                    + "representatives to collect, verify and "
                    + "process my personal information solely "
                    + "for the purpose of Background Verification. "
                    + "I confirm that all information furnished "
                    + "by me is true, accurate and complete to the "
                    + "best of my knowledge.";

    public static final String DECLARATION_TEXT =
            "I declare that the information provided by me is "
                    + "true and correct. I voluntarily provide my "
                    + "consent for Background Verification and "
                    + "confirm that I have read and understood "
                    + "the applicable policy.";

    /*
     * =====================================================
     * FOOTER
     * =====================================================
     */

    public static final String SYSTEM_GENERATED =
            "This is a system generated document.";

    public static final String VERIFY_TEXT =
            "Scan QR Code to verify this document.";

}
