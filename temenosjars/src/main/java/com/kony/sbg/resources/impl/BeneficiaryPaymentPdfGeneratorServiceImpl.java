package com.kony.sbg.resources.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.kony.dbputilities.fileutil.PDFGenerator;
import com.kony.sbg.fileutil.GenerateBeneficiary;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BeneficiaryPaymentPdfGeneratorServiceImpl extends PDFGenerator {

    private static final Logger LOG = Logger.getLogger(BeneficiaryPaymentPdfGeneratorServiceImpl.class);
    final String BLACK_FONT = "fonts/SourceSansPro-Black.ttf";
    final String REGULAR_FONT = "fonts/SourceSansPro-Regular.ttf";

    public byte[] printPDFForBeneficiary(String transferDataJson) throws Exception {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(os, new WriterProperties());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        try {
            document.setProperty(Property.SPLIT_CHARACTERS, new BeneficiaryPaymentPdfGeneratorServiceImpl.CustomSplitCharacters());

            FontProgram blackFontPrgm = FontProgramFactory.createFont(BLACK_FONT);
            PdfFont blackFont = PdfFontFactory.createFont(blackFontPrgm);
            FontProgram regFontPrgm = FontProgramFactory.createFont(REGULAR_FONT);
            PdfFont regularFont = PdfFontFactory.createFont(regFontPrgm);


            callPageOne(document, blackFont, regularFont, transferDataJson);
            addFooterDetailsPortrait(document, blackFont, regularFont, 3);
            document.close();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return os.toByteArray();
    }

    protected class CustomSplitCharacters extends DefaultSplitCharacters {
        @Override
        public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
            if (!text.get(glyphPos).hasValidUnicode()) {
                return false;
            }
            boolean baseResult = super.isSplitCharacter(text, glyphPos);
            boolean myResult = false;
            Glyph glyph = text.get(glyphPos);
            if (glyph.getUnicode() == '@') {
                myResult = true;
            }
            return myResult || baseResult;
        }
    }

    public void callPageOne(Document document, PdfFont blackFont, PdfFont regularFont, String transferDataJson) throws Exception {
        try {
            document.setBottomMargin(150);
            addHeaderDetailsPortrait(document, blackFont, regularFont);

            Table paymentTable = getBeneTable();
            paymentTableHeader(paymentTable, regularFont);
            document.add(paymentTable);
            Table paymentTableDescription = getBeneTable();
            Table paymentTableDescriptionTable2 = getBeneTable();
            paymentTableDescription(paymentTableDescription, paymentTableDescriptionTable2, regularFont, transferDataJson);
            document.add(paymentTableDescription);


            paymentTableData(document, regularFont, transferDataJson);

        } catch (Exception e) {
            LOG.error("\nEnter to Exception of callPageOne of BeneficiaryPaymentPdfGeneratorServiceImpl"+e.getMessage());
        }
    }

    public void addBankLogosPortrait(Document document) throws IOException {
        try {
            Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            bankDetailsTable.addCell(getNoBorderCell().add(addLogoImagePortrait()));
            bankDetailsTable.addCell(getNoBorderCell().add(addATALogoImagePortrait()));
            bankDetailsTable.setMarginBottom(10);
            document.add(bankDetailsTable);
        } catch (Exception e) {
            LOG.error("\n Exception inside addBankLogosPortrait:- \n"+e.getMessage());
        }

    }

    public void addBankNamePortrait(Document document, PdfFont blackFont, PdfFont regularFont) throws IOException {
        try {
            Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            bankDetailsTable.addCell(getNoBorderCell().add(addAddressImage()));
//            bankDetailsTable.addCell(getNoBorderCell().add(getGeneratedCopyPortrait(regularFont)));
            bankDetailsTable.setMarginBottom(10);
            document.add(bankDetailsTable);
        } catch (Exception e) {
            LOG.error("\n Exception inside addBankNamePortrait:- \n"+e.getMessage());
        }

    }

    public void addHeadline(Document document, PdfFont blackFont) {
        try {
            Div headLineDiv = new Div();
            headLineDiv.setBorderTop(new SolidBorder(WebColors.getRGBColor("#A0A0A0"), 1f, 0.6f));
            headLineDiv.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#A0A0A0"), 1f, 0.6f));
            headLineDiv.add(new Paragraph("Payment notification").setFont(blackFont).setFontSize(12f).setBold()).setFontColor(WebColors.getRGBColor("#424242"));
            headLineDiv.setMarginBottom(10);
            document.add(headLineDiv);
        } catch (Exception e) {
            LOG.error("\n Exception inside addHeadline:- \n"+e.getMessage());
        }
    }

    protected void addHeaderDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont)
            throws Exception {
        addBankLogosPortrait(document);
        addBankNamePortrait(document, blackFont, regularFont);
        addHeadline(document, blackFont);
    }

    protected Table paymentTableHeader(Table paymentTable, PdfFont regularFont) {
        try {
            paymentTable.addCell(getNoBorderCell()
                    .setTextAlignment(TextAlignment.LEFT).setMarginTop(5)
                    .add(new Paragraph("Dear User").setFont(regularFont).setFontSize(12f).setFontColor(WebColors.getRGBColor("#727272"))));
            paymentTable.setHeight(30);
            paymentTable.setWidth(UnitValue.createPercentValue(100));
        } catch (Exception e) {
            LOG.error("\n Exception inside paymentTableHeader:- \n"+e.getMessage());
        }
        return paymentTable;
    }

    protected Table paymentTableDescription(Table paymentTableDescription, Table paymentTableDescriptionTable2, PdfFont regularFont, String jsonData) {
        try {
            Optional<JSONObject> validatedJsonRsp = validateJsonObject(jsonData);
            String payeeName = validatedJsonRsp.map(jsonObj -> extractJsonValue("payeeName", jsonObj)).get();
            String fromAccount = validatedJsonRsp.map(jsonObj -> extractJsonValue("fromAccountNumber", jsonObj)).get();
            String transactionCurrency = validatedJsonRsp.map(jsonObj -> extractJsonValue("transactionCurrency", jsonObj)).get();
            // String payerName = payeeName + " " + fromAccount + " (" + transactionCurrency +")";
            String payerName = "****"+ " " + payeeName;

            paymentTableDescription.addCell(getNoBorderCell()
                    .setTextAlignment(TextAlignment.LEFT).setMarginTop(5)
                    .add(new Paragraph("We confirm that the following payment has been made to your account from " + payerName + ". \n The details are as follows:" ).setFont(regularFont).setFontSize(10f).setFontColor(WebColors.getRGBColor("#727272"))));

        } catch (Exception e) {
            LOG.error("\n Exception inside paymentTableDescription:- \n"+e.getMessage());
        }
        return paymentTableDescription;
    }

    protected Table getBeneTable() {
        /* Dynamically setting the number of columns and column width */
        float[] width = new float[2];
        return new Table(UnitValue.createPercentArray(width));
    }

    protected Table getTable() {
        /* Dynamically setting the number of columns and column width */
//        float[] width = new float[2];
        float width[] = {4f, 4f};
        return new Table(UnitValue.createPercentArray(width));
    }

    public Div addLogoImagePortrait() throws IOException {
        InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/BrandLogoWithText.png");
        Div imageDiv = new Div();
        if (is != null) {
            Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
            //imageDiv.setMarginLeft(10);
            imageDiv.setHeight(28f);
            imageDiv.setWidth(UnitValue.createPercentValue(75));
            konyLogo.setWidth(imageDiv.getWidth());
            konyLogo.setHeight(imageDiv.getHeight());
            imageDiv.add(konyLogo);
            imageDiv.setMarginBottom(20);
//            document.add(imageDiv);
        } else {
            LOG.error("\n##Image not found");
        }
        return imageDiv;
    }

    public Div addAddressImage() throws IOException {
        InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/address.png");
        Div imageDiv = new Div();
        if (is != null) {
            Image address = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
            //imageDiv.setMarginLeft(10);
//            imageDiv.setHeight(28f);
            imageDiv.setWidth(UnitValue.createPercentValue(75));
            address.setWidth(imageDiv.getWidth());
            address.setHeight(imageDiv.getHeight());
            imageDiv.add(address);
            imageDiv.setMarginBottom(10);
//            document.add(imageDiv);
        } else {
            LOG.error("\n##Image not found");
        }
        return imageDiv;
    }

    public Div addATALogoImagePortrait() throws IOException {
        InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/SB_ATA_PO_rgb.png");
        Div imageDiv = new Div();
        if (is != null) {
            Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
            imageDiv.setMarginLeft(75);
//            imageDiv.setMarginTop(3);
            imageDiv.setHeight(40f);
            imageDiv.setWidth(UnitValue.createPercentValue(100));
            konyLogo.setWidth(imageDiv.getWidth());
            konyLogo.setHeight(imageDiv.getHeight());
            imageDiv.add(konyLogo);
//            imageDiv.setMarginBottom(20);
//            document.add(imageDiv);
        } else {
            LOG.error("\n##Image not found");
        }
        return imageDiv;
    }

    public Div getBankNameDivPortrait(PdfFont blackFont) {
        Div bankNameDiv = new Div();
        bankNameDiv.setHeight(50f);
        bankNameDiv.setWidth(UnitValue.createPercentValue(85));
        bankNameDiv.add(
                new Paragraph(" The Standard Bank of South Africa Limited " + "Company Registration No, 1962/000738/06")
                        .setFont(blackFont).setFontSize(11f).setBold()).setFontColor(WebColors.getRGBColor("#424242"));
        return bankNameDiv;
    }

    public Div getGeneratedCopyPortrait(PdfFont regularFont) {
        Div bankNameDiv = new Div();
        bankNameDiv.setWidth(UnitValue.createPercentValue(70));
        bankNameDiv.setMarginLeft(125);
        bankNameDiv.setMarginTop(18);
        bankNameDiv.add(new Paragraph(" Computer generated copy ").setFont(regularFont).setFontSize(11f));
        return bankNameDiv;
    }

    protected void addFooterDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont, Integer number)
            throws Exception {
        try {
            int numberOfPages = document.getPdfDocument().getNumberOfPages();

//            if (numberOfPages == 3) {
//                document.showTextAligned(new Paragraph("End of report").setFont(blackFont).setFontSize(9f).setBold(), 35, 105, numberOfPages,
//                        TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
//            }

//            document.showTextAligned(
//                    new Paragraph(String.format("Page %s of %s", numberOfPages, number)).setFont(blackFont).setFontSize(9f).setBold(),
//                    554, 105, numberOfPages, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

//            document.showTextAligned(
//                    new Paragraph("Created " + getDateTimeDDMMYYYYHHMMSS()).setFont(blackFont).setFontSize(9f).setBold(),
//                    554, 93, numberOfPages, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0).setBorderBottom(new SolidBorder(WebColors.getRGBColor("#424242"), 1f, 0.6f));

            //---------------------------//
//            div.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#424242"), 1f, 0.6f));
//            document.
//            document.setBorderTop(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            div.setBorder(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setUnderline(5f, 75);

//            document.showTextAligned(new Paragraph("Disclaimer").setFont(blackFont).setFontSize(9f).setBold(), 35, 68, numberOfPages,
//                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(new Paragraph(
                            "Payments to Standard Bank accounts may take one business day to reflect while payments to other banks may take up to three ")
                            .setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")),
                    35, 360, numberOfPages, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(new Paragraph(
                            "business days. Please check your account to confirm that you have received this payment. If you have any questions or if you")
                            .setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")),
                    35, 350, numberOfPages, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("would like more information, please contact the payer. Alternatively, you can contact us at <email address> or call our Client").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 340, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("Contact Centre on 0860 123 000.").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 330, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            document.showTextAligned(
                    new Paragraph("This document is intended for use by the addressee and is privileged and confidential. If the transmission has been misdirected").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 310, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            document.showTextAligned(
                    new Paragraph("to you, please contact us immediately.").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 303, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);


            document.showTextAligned(
                    new Paragraph("Kind regards").setWidth(800).setFont(blackFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 270, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            document.showTextAligned(
                    new Paragraph("Transaction Banking").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 260, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            addFootline(document, blackFont);

            document.showTextAligned(
                    new Paragraph("Disclaimer").setWidth(800).setFont(blackFont).setFontSize(12f), 35, 128, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("The Standard Bank Group provides the above details as “information only” and makes no representation or warranty as to the").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 110, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("integrity, completeness or accuracy of any information contained herein.\n").setWidth(800).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272")), 35, 100, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        } catch (Exception e) {
            LOG.error("\nEnter to Exception of addFooterDetailsPortrait of GeneratePayment"+e.getMessage());
        }
    }

    private void addFootline(Document document, PdfFont blackFont) {
        try {
            Div headLineDiv = new Div();
            headLineDiv.setHeight(200);
            headLineDiv.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#A0A0A0"), 1f, 0.6f));
            document.add(headLineDiv);
        } catch (Exception e) {
            LOG.error("\n Exception inside addFootline:- \n"+e.getMessage());
        }
    }

    protected String getDateTimeDDMMYYYYHHMMSS() {
        String timestamp = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        Date date = new Date();
        timestamp = sdf.format(date);
        return timestamp;
    }


    private static String extractJsonValue(String jsonKey, JSONObject transactionObject) {
        try {
            return transactionObject.get(jsonKey).toString();
        } catch (Exception e) {
            return "";
        }
    }

    protected String roundingDecimal(Double inputNumber) {
        final DecimalFormat dfZero = new DecimalFormat("0.00");
        String format = dfZero.format(inputNumber);
        return format;
    }

    protected void paymentTableData(Document document, PdfFont regularFont, String transferDataJson) throws Exception {
        Table paymentTable = getTable();
        paymentTable.setWidth(UnitValue.createPercentValue(100));
        paymentTable.setMarginTop(20);
        Optional<JSONObject> validatedJsonRsp = null;
        String referenceNumber = "";
        String beneficiaryName = "";
        String bankName = "";
        String beneficiaryAccountNumber = "";
        String beneficiaryBranchNumber = "";
        String beneficiaryReference = "";
        String amount = "";
        String paymentDateAndTime = "";
        String transactionCurrency= "";

        try {
            validatedJsonRsp = validateJsonObject(transferDataJson);
//            Object beneficiaryPayload = validatedJsonRsp.get().get("beneficiaryPayload");
//            Optional<JSONObject> jsonObject = validateJsonObjectPayload(transferDataJson);


            referenceNumber = validatedJsonRsp.map(jsonObj -> extractJsonValue("referenceId", jsonObj)).get();

            String valueDate = validatedJsonRsp.map(jsonObj -> extractJsonValue("scheduledDate", jsonObj)).get();
            paymentDateAndTime = getDate(valueDate);

            beneficiaryName = validatedJsonRsp.map(jsonObj -> extractJsonValue("beneficiaryName", jsonObj)).get();
            bankName = validatedJsonRsp.map(jsonObj -> extractJsonValue("bankName", jsonObj)).get();
            beneficiaryAccountNumber = validatedJsonRsp.map(jsonObj -> extractJsonValue("toAccountNumber", jsonObj)).get();
            beneficiaryBranchNumber = validatedJsonRsp.map(jsonObj -> extractJsonValue("bicCode", jsonObj)).get();
            beneficiaryReference = validatedJsonRsp.map(jsonObj -> extractJsonValue("beneficiaryReference", jsonObj)).get();
            amount = validatedJsonRsp.map(jsonObj -> extractJsonValue("amount", jsonObj)).get();
            transactionCurrency = validatedJsonRsp.map(jsonObj -> extractJsonValue("transactionCurrency", jsonObj)).get();

        } catch (Exception e) {
            LOG.error("\n Exception inside paymentTableData:- \n"+e.getMessage());
        }
//        String foreignExchangeRate = validatedJsonRsp.map(jsonObj -> extractJsonValue("foreignExchangeRate", jsonObj)).get();
//        double foreignExchangeRateDouble = Double.parseDouble(amount);
//        String stringForeignExchangeRate = roundingDecimal(amountDouble);


        Map<Integer, String> attributeList = new HashMap<>();
        if (null != validatedJsonRsp) {
            attributeList.put(1, referenceNumber);
            attributeList.put(2, beneficiaryName);
            attributeList.put(3, bankName);
            attributeList.put(4, getMaskedValue(beneficiaryAccountNumber));
            attributeList.put(5, beneficiaryBranchNumber);
            attributeList.put(6, beneficiaryReference);
            attributeList.put(7, transactionCurrency + SBGConstants.SPACE_STRING + getAmount(amount));
            attributeList.put(8, paymentDateAndTime);
        } else {
            attributeList.put(1, " ");
            attributeList.put(2, " ");
            attributeList.put(3, " ");
            attributeList.put(4, " ");
            attributeList.put(5, " ");
            attributeList.put(6, " ");
            attributeList.put(7, " ");
            attributeList.put(8, " ");
        }
        LOG.debug("BeneficiaryPaymentPdfGeneratorServiceImpl.paymentTableData() --> attributeList" + attributeList);

        try {
            List<Map.Entry<Integer, String>> list = new ArrayList<>(attributeList.entrySet());
            list.sort(Map.Entry.comparingByKey());

            for (Map.Entry<Integer, String> element : attributeList.entrySet()) {
                if (element.getKey() != null) {
                    if (element.getKey().equals(1)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Reference number").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));

                    } else if (element.getKey().equals(2)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Beneficiary name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(3)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Bank name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(4)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Beneficiary account number").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(5)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Beneficiary branch code").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(6)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Beneficiary reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(7)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Amount").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    } else if (element.getKey().equals(8)) {
                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph("Payment date and time").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                        paymentTable.addCell(getNoBorderCell()
                                .setTextAlignment(TextAlignment.LEFT)
                                .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                    }

                }
            }

        } catch (Exception e) {
            LOG.error("\npdf generation error =" + e.getMessage());
        }
        document.add(paymentTable);
    }

    private String getDate(String valueDate) {
        if (SBGCommonUtils.isStringNotEmpty(valueDate) && valueDate.length() > 15) {


            String subDate = valueDate.substring(0, 16);
            if (SBGCommonUtils.isStringNotEmpty(subDate)) {
                return subDate.replace("T", " ").replace(":", "h");
            }

        }
        return SBGConstants.EMPTY_STRING;
    }

    private String getAmount(String amount){
        if(SBGCommonUtils.isStringNotEmpty(amount) && !amount.contains(".")){
            return amount + ".00";
        }
        return amount;
    }

    private String getMaskedValue(String accNumber){
        String lastFourDigits;
        if(StringUtils.isNotBlank(accNumber)){
            if(accNumber.length() > 4){
                lastFourDigits = accNumber.substring(accNumber.length() - 4);
                accNumber = "XXXXX" + lastFourDigits;
            }else{
                accNumber = "XXXXX" + accNumber;
            }
        }
        return accNumber;
    }

    private static Optional<JSONObject> validateJsonObject(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return Optional.ofNullable(jsonObject);
        } catch (Exception e) {
            LOG.error("\n Exception inside validateJsonObject:- \n"+e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<JSONObject> validateJsonObjectPayload(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String beneficiaryPayloadString = jsonObject.getString("beneficiaryPayload");
            JSONArray beneficiaryPayload = jsonObject.getJSONArray("beneficiaryPayload");
            JSONObject jsonArray = beneficiaryPayload.getJSONObject(0);
            return Optional.ofNullable(jsonArray);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static HashMap<String, String> arrayToObject(String jsonString) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (!jsonString.equals("")) {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                map.put("coverNumber", object.getString("coverNumber"));
                map.put("exchangeRate", Double.toString(object.getDouble("exchangeRate")));
            }
        } else {
            map.put("coverNumber", "");
            map.put("exchangeRate", "");
        }

        return map;
    }

}

