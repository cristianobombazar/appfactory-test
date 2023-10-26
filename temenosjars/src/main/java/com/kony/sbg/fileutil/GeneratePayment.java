package com.kony.sbg.fileutil;

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
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.util.*;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GeneratePayment extends PDFGenerator {

    private static final Logger logger = Logger.getLogger(GeneratePayment.class);

    private static final String BLACK_FONT = "fonts/SourceSansPro-Black.ttf";
    private static final String REGULAR_FONT = "fonts/SourceSansPro-Regular.ttf";

    private static final String  SPACE = "                                                                                         ";

    //    JSONArray data,
    public byte[] generateFile(Result result, String transferDataJson, String auditDataJson, String documentsDataJson, String proofOfPayment) throws Exception {
        logger.error("Enter to Generate Payment Class generate file generateFile()");
        logger.error("Enter to Generate Payment Class generate file generateFile()");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(os, new WriterProperties());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        PageSize ps = PageSize.A4;
        /*
         * By default wrap in itext is done using space and other characters but email
         * is not splitted so adding '@' to split and wrap email
         */
        document.setProperty(Property.SPLIT_CHARACTERS, new GeneratePayment.CustomSplitCharacters());

        try {
            logger.error("Enter to first try block of GeneratePayment");
            FontProgram blackFontPrgm = FontProgramFactory.createFont(BLACK_FONT);
            PdfFont blackFont = PdfFontFactory.createFont(blackFontPrgm);
            FontProgram regFontPrgm = FontProgramFactory.createFont(REGULAR_FONT);
            PdfFont regularFont = PdfFontFactory.createFont(regFontPrgm);

            logger.error("Enter to first try block of GeneratePayment");

            callPageOne(document, blackFont, regularFont, transferDataJson, auditDataJson);
            addFooterDetailsPortrait(document, blackFont, regularFont, 3);

            if (!isDomestic(transferDataJson)) {
                callPageTwo(document, blackFont, regularFont, ps, transferDataJson, documentsDataJson);
            } else {
                // new domestic report 2nd page proof of payment details
                callPageTwoDomestic(document, blackFont, regularFont, ps, proofOfPayment);
            }
            addFooterDetailsPortrait(document, blackFont, regularFont, 3);


            callPageThree(document, blackFont, regularFont, ps, transferDataJson, auditDataJson);
            addFooterDetailsPortrait(document, blackFont, regularFont, 3);

            document.close();
            logger.error("Exit successfully on generateFile of GeneratePayment");
        } catch (Exception exp) {
            logger.error("Error Exception in GeneratePayment of generateFile(Result result) : " + exp.getMessage());
            result = SbgErrorCodeEnum.ERR_100081.setErrorCode(result);
        }
        return os.toByteArray();

    }

    public void callPageOne(Document document, PdfFont blackFont, PdfFont regularFont, String transferDataJson, String auditDataJson) throws Exception {
        try {
            logger.error("Enter to callPageOne of GeneratePayment");
            logger.error("Enter to callPageOne of GeneratePayment on error mode");
            document.setBottomMargin(150);
            addHeaderDetailsPortrait(document, blackFont, regularFont);

            Table paymentTable = getBeneTable();
            paymentTableHeader(paymentTable, blackFont);
            document.add(paymentTable);
            paymentTableData(document, regularFont, transferDataJson);

            Table auditTable = getBeneTable();
            auditTableHeader(auditTable, blackFont, transferDataJson);
            document.add(auditTable);
            auditTableData(document, regularFont, auditDataJson, transferDataJson);
            logger.error("Exit successfully on callPageOne of GeneratePayment");
        } catch (Exception e) {
            logger.error("Enter to Exception of callPageOne of GeneratePayment");
            System.out.println("Exception in :- " + e.getMessage());
        }
    }

    public void callPageTwo(Document document, PdfFont blackFont, PdfFont regularFont, PageSize ps, String transferDataJson, String documentsDataJson) throws Exception {

        try {
            logger.error("Enter to callPageTwo of GeneratePayment");
            document.getPdfDocument().addNewPage(2, ps);
            document.setBottomMargin(150);
            addHeaderDetailsPortrait(document, blackFont, regularFont);
            Table paymentTablePage2 = getBeneTable();
            paymentTableHeaderPageTwo(paymentTablePage2, blackFont);
            document.add(paymentTablePage2);

            Table paymentTable = getBeneTable();
            addRegulatoryHeader(paymentTable, blackFont);
            document.add(paymentTable);
            Integer spaceRegTable = regulatoryTableData(document, regularFont, transferDataJson);

            Table supportingDocTable = getBeneTable();
            supportingDocumentHeaderPageTwo(supportingDocTable, blackFont);
            document.add(supportingDocTable);
            Integer spaceSupTable = supportingTableData(document, regularFont, transferDataJson, documentsDataJson);

            if (spaceRegTable == 0 && spaceSupTable == 0) {
                addEmptyLine(document, 7);
            } else {
                addEmptyLine(document, spaceSupTable + spaceRegTable + 10);
            }

            logger.error("Exit successfully on callPageTwo of GeneratePayment");
        } catch (Exception e) {
            logger.error("Enter to Exception of callPageTwo of GeneratePayment");
            System.out.println("Exception in :- " + e.getMessage());
        }
    }

    private void callPageTwoDomestic(Document document, PdfFont blackFont, PdfFont regularFont, PageSize ps, String proofOfPayment) throws Exception {
        try {
            logger.error("Enter to callPage2 of GeneratePayment domestic");
            logger.error("Enter to callPage2 of GeneratePayment on error mode domestic");
            document.setBottomMargin(150);
            addHeaderDetailsPortrait(document, blackFont, regularFont);

            Table headlineTable = getBeneTable();
            proofOfPaymentHeader(headlineTable, blackFont);
            document.add(headlineTable);
            proofOfPaymentDomestic(document, regularFont, proofOfPayment);

            addEmptyLine(document, 17);

        } catch (Exception e) {
            logger.error("Enter to Exception of callPageTwo of GeneratePayment");
            System.out.println("Exception in :- " + e.getMessage());
        }
    }

    public void callPageThree(Document document, PdfFont blackFont, PdfFont regularFont, PageSize ps, String transferDataJson, String auditDataJson) throws Exception {
        try {
            logger.error("Enter to callPageThree of GeneratePayment");
            document.getPdfDocument().addNewPage(3, ps);
            document.setBottomMargin(150);
            addHeaderDetailsPortrait(document, blackFont, regularFont);
            Table paymentTablePage3 = getBeneTable();
            paymentTableHeader(paymentTablePage3, blackFont);
            document.add(paymentTablePage3);

            subHeadingCapture(document, blackFont);
            Table subCaptureTable = getBeneTable();
            subTableUnderCapture(document, subCaptureTable, blackFont, transferDataJson, auditDataJson);

            subHeadingApproval(document, blackFont);
            Table subApprovalTable = getBeneTable();
            subTableUnderApproval(document, subApprovalTable, blackFont, transferDataJson, auditDataJson);
            logger.error("Exit successfully on callPageThree of GeneratePayment");
        } catch (Exception e) {
            logger.error("Enter to Exception of callPageThree of GeneratePayment");
            System.out.println("Exception in :- " + e.getMessage());
        }
    }

    protected Table paymentTableHeaderPageTwo(Table paymentTable, PdfFont blackFont) {
        logger.error("##inside addBeneHeader");
        paymentTable.addCell(getNoBorderCell().add(new Paragraph("Regulatory Reporting").setMarginTop(5))).setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
                .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont).setBold().setBorder(Border.NO_BORDER).setFontColor(WebColors.getRGBColor("#424242"));
        paymentTable.setHeight(30);
        paymentTable.setMarginTop(0);
        paymentTable.setWidth(UnitValue.createPercentValue(100));
        return paymentTable;
    }

    protected Integer regulatoryTableData(Document document, PdfFont regularFont, String transferDataJson) throws Exception {
        Table regulatoryTable = getBeneTable();
        regulatoryTable.setWidth(UnitValue.createPercentValue(95));//
        regulatoryTable.setMarginTop(0);

        Optional<JSONObject> validatedJsonRsp = validateJsonObject(transferDataJson);
        String bopdata = validatedJsonRsp.map(jsonObj -> extractJsonValue("bopData", jsonObj)).orElse("");
        String bopdataJson = new String(Base64.getDecoder().decode(bopdata));
        JSONArray monetaryDataArray = validateBopDataJsonArray(bopdataJson);
        String currency = validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentCurrency", jsonObj)).orElse("");
        Map<String, String> regulatoryData = getRegulatoryData(monetaryDataArray, currency);

        regulatoryData.forEach((k, v) -> {
            regulatoryTable.addCell(getNoBorderCell()
                    .add(new Paragraph(k+SPACE).setTextAlignment(TextAlignment.LEFT).setFont(regularFont).setFontSize(9f)));
            regulatoryTable.addCell(getNoBorderCell()
                    .add(new Paragraph(String.valueOf(v)).setTextAlignment(TextAlignment.LEFT).setFont(regularFont).setFontSize(9f)));
        });

        int space = 0;
        if (regulatoryData.size() <= 0) {
            space = 1;
        } else {
            space = 0;
        }

        document.add(regulatoryTable);
        logger.error("##exit addBeneData");
        return space;
    }

    protected Table addRegulatoryHeader(Table regulatoryTable, PdfFont blackFont) {
        logger.info("##inside addBeneHeader");
        regulatoryTable.setHeight(30);
        regulatoryTable.setMarginTop(5);
        regulatoryTable.setWidth(UnitValue.createPercentValue(100));
        List<String> headers = Arrays.asList(new String[]{"Regulatory Code" + SPACE, "Amount"});
        for (int i = 0; i < headers.size(); i++) {

            regulatoryTable.addCell(getNoBorderCell().add(new Paragraph(headers.get(i)).setMarginTop(5)))
                    .setBackgroundColor(WebColors.getRGBColor("#F9F9F9"))
                    .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont);
        }
        return regulatoryTable;
    }

    protected Table supportingDocumentHeaderPageTwo(Table supportingDocTable, PdfFont blackFont) {
        logger.error("##inside addBeneHeader");
        supportingDocTable.setMarginTop(40);
        supportingDocTable.addCell(getNoBorderCell().add(new Paragraph("Supporting Documents").setMarginTop(5))).setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
                .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont).setBold().setBorder(Border.NO_BORDER).setFontColor(WebColors.getRGBColor("#424242"));
        supportingDocTable.setHeight(30);
        supportingDocTable.setWidth(UnitValue.createPercentValue(100));
        return supportingDocTable;
    }

    protected Integer supportingTableData(Document document, PdfFont regularFont, String transferDataJson, String documentsDataJson) throws Exception {
        Table supportingTable = getBeneTable();
        supportingTable.setWidth(UnitValue.createPercentValue(100));
        supportingTable.setMarginTop(10);

        Optional<JSONObject> validatedJsonRsp = validateJsonObject(transferDataJson);
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String createdts = validatedJsonRsp.map(jsonObj -> extractJsonValue("transactionDate", jsonObj)).get();
        String formatedDate = newFormat.format(myFormat.parse(createdts));
        JSONArray documentDataJsonArray = validateDocumentJsonArray(documentsDataJson);
        Map<String, String> attributeList = new HashMap<>();
        int space = 0;

        if (documentDataJsonArray != null) {
            for (int i = 0; i < documentDataJsonArray.length(); i++) {
                Optional<JSONObject> jsonArray = Optional.ofNullable(documentDataJsonArray.getJSONObject(i));
                attributeList.put(jsonArray.map(jsonObj -> extractJsonValue("fileName", jsonObj)).orElse(""),
                        formatedDate);
            }
        }

        for (Map.Entry<String, String> element : attributeList.entrySet()) {

            supportingTable.addCell(getNoBorderCell()
                    .add(new Paragraph(element.getKey()).setTextAlignment(TextAlignment.LEFT).setFont(regularFont).setFontSize(9f)));
            supportingTable.addCell(getNoBorderCell()
                    .add(new Paragraph(element.getValue()).setTextAlignment(TextAlignment.RIGHT).setFont(regularFont).setFontSize(9f)));
        }

        if (documentDataJsonArray != null && documentDataJsonArray.length() <= 0) {
            space = 2;
        } else {
            space = 0;
        }

        document.add(supportingTable);
        logger.error("##exit addBeneData");
        return space;
    }

    protected void addFooterDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont, Integer number)
            throws Exception {
        try {
            logger.error("Enter to addFooterDetailsPortrait of GeneratePayment");
            int numberOfPages = document.getPdfDocument().getNumberOfPages();

            if (numberOfPages == 3) {
                document.showTextAligned(new Paragraph("End of report").setFont(blackFont).setFontSize(9f).setBold(), 35, 105, numberOfPages,
                        TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            }

            document.showTextAligned(
                    new Paragraph(String.format("Page %s of %s", numberOfPages, number)).setFont(blackFont).setFontSize(9f).setBold(),
                    554, 105, numberOfPages, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("Created " + getDateTimeDDMMYYYYHHMMSS()).setFont(blackFont).setFontSize(9f).setBold(),
                    554, 93, numberOfPages, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0).setBorderBottom(new SolidBorder(WebColors.getRGBColor("#424242"), 1f, 0.6f));

            //---------------------------//
//            div.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#424242"), 1f, 0.6f));
//            document.
//            document.setBorderTop(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            div.setBorder(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
//            document.setUnderline(5f, 75);

            document.showTextAligned(new Paragraph("Disclaimer").setFont(blackFont).setFontSize(9f).setBold(), 35, 68, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(new Paragraph(
                            "The Standard Bank Group provides the details as \"information only\", based on that aforeside, the Standard Bank Group makes no ")
                            .setWidth(800).setFont(regularFont).setFontSize(9f),
                    35, 52, numberOfPages, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(new Paragraph(
                            "one representation or warranty, whether expressed or implied as to the integrity, accuracy completeness or reliability of any information ")
                            .setWidth(800).setFont(regularFont).setFontSize(9f),
                    35, 40, numberOfPages, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            document.showTextAligned(
                    new Paragraph("contained herein.").setWidth(800).setFont(regularFont).setFontSize(9f), 35, 28, numberOfPages,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            logger.error("Exit successfully on addFooterDetailsPortrait of GeneratePayment");
        } catch (Exception e) {
            logger.error("Enter to Exception of addFooterDetailsPortrait of GeneratePayment");
            System.out.println("Exception in footer section:- " + e.getMessage());
        }
    }

    public void subHeadingCapture(Document document, PdfFont blackFont) {
        Div headLineDiv = new Div();
//        headLineDiv.setBorderTop(new SolidBorder(ColorConstants.BLACK, 1f, 0.6f));
        headLineDiv.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
        headLineDiv.add(new Paragraph("Capture").setFont(blackFont).setFontSize(12f).setBold().setFontColor(WebColors.getRGBColor("#424242")));
        document.add(headLineDiv);
    }

    protected void subTableUnderCapture(Document document, Table subCaptureTable, PdfFont blackFont, String transferDataJson, String auditDataJson) throws ParseException {
//        List<String> headers = Arrays.asList(new String[]{"Capture Status", " 0 of Received"});
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Optional<JSONObject> validatedJsonRsp = validateJsonObject(transferDataJson);
        Optional<JSONObject> validatedAuditJsonRsp = validateJsonObject(auditDataJson);

        String captureName = validatedJsonRsp.map(jsonObj -> extractJsonValue("captureName", jsonObj)).orElse("").trim().replaceAll("\\s{2,}", " ");
        String createdts = validatedAuditJsonRsp.map(jsonObj -> extractJsonValue("captureDateTime", jsonObj)).orElse("");
        String captureDate =  newFormat.format(myFormat.parse(createdts));
        String captureStatus = validatedAuditJsonRsp.map(jsonObj -> extractJsonValue("captureStatus", jsonObj)).orElse("");
        String captureComment = "N/A";

        List<String[]> dataList = new ArrayList<>();
        String[] dataset = {captureName, captureStatus, captureDate, captureComment};
        dataList.add(dataset);
        int param = dataList.size();

        List<String> headers = Arrays.asList(new String[]{"Capture Status", String.format(" 1 of %s Received", param)});

        for (int i = 0; i < headers.size(); i++) {
            subCaptureTable.addCell(getNoBorderCell().add(new Paragraph(headers.get(i))))
                    .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont);
            subCaptureTable.setHeight(30);
            subCaptureTable.setMarginTop(20);
            subCaptureTable.setWidth(UnitValue.createPercentValue(100));
            subCaptureTable.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
        }
        document.add(subCaptureTable);
        Table table = getFourColumnTable();
        List<String> dataHeading = Arrays.asList(new String[]{"User", "Status", "Date & time", "Comment"});
        for (int i = 0; i < dataHeading.size(); i++) {
            table.addHeaderCell(getNoBorderCell().add(new Paragraph(dataHeading.get(i)).setMarginTop(5)))
                    .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont)
                    .setBackgroundColor(WebColors.getRGBColor("#F9F9F9"));
            table.setHeight(30);
            table.setMarginTop(20);
            table.setWidth(UnitValue.createPercentValue(100));
        }
        document.add(table);
        Table tableData = getFourColumnTable();

        for (int i = 0; i < dataset.length; i++) {
            tableData.addCell(getNoBorderCell().add(new Paragraph(dataset[i])))
                    .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont);
            tableData.setHeight(30);
            tableData.setMarginTop(20);
            tableData.setWidth(UnitValue.createPercentValue(100));
            tableData.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
        }
        document.add(tableData);
    }

    public void subHeadingApproval(Document document, PdfFont blackFont) {
        Div headLineDiv = new Div();
        headLineDiv.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
        headLineDiv.add(new Paragraph("Approval").setFont(blackFont).setFontSize(12f).setBold().setFontColor(WebColors.getRGBColor("#424242")));
        document.add(headLineDiv);
    }

    protected void subTableUnderApproval(Document document, Table subApprovalTable, PdfFont blackFont, String transferDataJson, String auditDataJson) throws ParseException {

        JSONArray auditDataJsonArray = validateJsonArray(auditDataJson);
    
        if (auditDataJsonArray != null) {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
            int size = auditDataJsonArray.length();
            int numOfApprovals = 0;
            String approvalsValue = "N/A";
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = auditDataJsonArray.optJSONObject(i);
                String approverStatus = extractJsonValue("approverStatus", jsonObject);

                if (approverStatus.equalsIgnoreCase("Approved")) {
                    numOfApprovals++;
                }                
            }
            approvalsValue = numOfApprovals + String.format(" of %s Received", size);
            List<String> headers = Arrays.asList("Approval Status", approvalsValue);

    
            addTableHeaders(subApprovalTable, headers, 10f, blackFont);
            subApprovalTable.setHeight(30);
            subApprovalTable.setMarginTop(20);
            subApprovalTable.setWidth(UnitValue.createPercentValue(100));
            subApprovalTable.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
            document.add(subApprovalTable);
    
            Table table = getFourColumnTable();
            List<String> dataHeading = Arrays.asList("User", "Status", "Date & time", "Comment");
            addTableHeaders(table, dataHeading, 10f, blackFont);
            table.setHeight(30);
            table.setMarginTop(20);
            table.setWidth(UnitValue.createPercentValue(100));
            document.add(table);
    
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = auditDataJsonArray.optJSONObject(i);
    
                String approverName = extractJsonValue("approverName", jsonObject).trim().replaceAll("\\s{2,}", " ");
                String approverStatus = extractJsonValue("approverStatus", jsonObject);
                String createdts = extractJsonValue("approverDateTime", jsonObject);
                String approverDate = outputDateFormat.format(inputDateFormat.parse(createdts));
                String approverComment = extractJsonValue("comment", jsonObject);
    
                String[] dataset = {approverName, approverStatus, approverDate, approverComment};
                Table tableData = getFourColumnTable(); // Create a new table for each row of data
    
                for (String cellData : dataset) {
                    tableData.addCell(getNoBorderCell().add(new Paragraph(cellData))
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(10f)
                            .setFont(blackFont));
                }
    
                tableData.setHeight(30);
                tableData.setMarginTop(20);
                tableData.setWidth(UnitValue.createPercentValue(100));
                tableData.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E3E3E3"), 1f, 0.6f));
                document.add(tableData);
            }
        }
    }
    
    private void addTableHeaders(Table table, List<String> headers, float fontSize, PdfFont font) {
        for (String header : headers) {
            table.addHeaderCell(getNoBorderCell().add(new Paragraph(header).setMarginTop(5))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(fontSize)
                    .setFont(font)
                    .setBackgroundColor(WebColors.getRGBColor("#F9F9F9")));
        }
    }    

    protected void addHeaderDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont)
            throws Exception {
        logger.error("##inside addHeaderDetailsPortrait");
        addBankLogosPortrait(document);
        addBankNamePortrait(document, blackFont, regularFont);
        addHeadline(document, blackFont);
    }

    public void addBankLogosPortrait(Document document) throws IOException {
        logger.error("##inside addBankLogosPortrait");
        Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        bankDetailsTable.addCell(getNoBorderCell().add(addLogoImagePortrait()));
        bankDetailsTable.addCell(getNoBorderCell().add(addATALogoImagePortrait()));
        bankDetailsTable.setMarginBottom(10);
        document.add(bankDetailsTable);

    }

    public void addBankNamePortrait(Document document, PdfFont blackFont, PdfFont regularFont) throws IOException {
        logger.error("##inside addBankNamePortrait");
        Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        bankDetailsTable.addCell(getNoBorderCell().add(getBankNameDivPortrait(blackFont)));
        bankDetailsTable.addCell(getNoBorderCell().add(getGeneratedCopyPortrait(regularFont)));
        bankDetailsTable.setMarginBottom(10);
        document.add(bankDetailsTable);

    }

    public Div addLogoImagePortrait() throws IOException {
        logger.error("##inside addLogoImagePortrait");
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
            logger.error("##Image not found");
        }
        return imageDiv;
    }

    public Div addATALogoImagePortrait() throws IOException {
        logger.error("##inside addATALogoImagePortrait");
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
            logger.error("##Image not found");
        }
        return imageDiv;
    }

    public Div getBankNameDivPortrait(PdfFont blackFont) {
        logger.error("##inside getBankNameDivPortrait");
        Div bankNameDiv = new Div();
        bankNameDiv.setHeight(50f);
        bankNameDiv.setWidth(UnitValue.createPercentValue(85));
        bankNameDiv.add(
                new Paragraph(" The Standard Bank of South Africa Limited " + "Company Registration No, 1962/000738/06")
                        .setFont(blackFont).setFontSize(11f).setBold()).setFontColor(WebColors.getRGBColor("#424242"));
        return bankNameDiv;
    }

    public Div getGeneratedCopyPortrait(PdfFont regularFont) {
        logger.error("##inside getGeneratedCopyPortrait");
        Div bankNameDiv = new Div();
        bankNameDiv.setWidth(UnitValue.createPercentValue(70));
        bankNameDiv.setMarginLeft(125);
        bankNameDiv.setMarginTop(15);
        bankNameDiv.add(new Paragraph(" Computer generated copy ").setFont(regularFont).setFontSize(11f));
        return bankNameDiv;
    }

    protected Table getBeneTable() {
        /* Dynamically setting the number of columns and column width */
        logger.error("Enter to Generate Payment Class getBeneTable");
        float[] width = new float[2];
        return new Table(UnitValue.createPercentArray(width));
    }

    protected Table getTable() {
        /* Dynamically setting the number of columns and column width */
        logger.error("Enter to Generate Payment Class getBeneTable");
//        float[] width = new float[2];
        float width[] = {4f, 4f};
        return new Table(UnitValue.createPercentArray(width));
    }

    protected Table getFourColumnTable() {
        /* Dynamically setting the number of columns and column width */
        logger.error("Enter to Generate Payment Class getBeneTable");
        float width[] = {2f, 2f, 2f, 2f};
        return new Table(UnitValue.createPercentArray(width));
    }

    protected Table paymentTableHeader(Table paymentTable, PdfFont blackFont) {
        logger.error("##inside addBeneHeader");
        paymentTable.addCell(getNoBorderCell().add(new Paragraph("Payment Details").setMarginTop(10))).setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
//        paymentTable.addCell("Payments Details").setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
                .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont).setBold().setBorder(Border.NO_BORDER).setFontColor(WebColors.getRGBColor("#424242"));
        paymentTable.setHeight(30);
        paymentTable.setWidth(UnitValue.createPercentValue(100));
        return paymentTable;
    }

    protected Table proofOfPaymentHeader(Table paymentTable, PdfFont blackFont) {
        paymentTable.addCell(getNoBorderCell().add(new Paragraph("Proof of payment details").setMarginTop(5))).setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
//        paymentTable.addCell("Payments Details").setBackgroundColor(WebColors.getRGBColor("#F4F5F7"))
                .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont).setBold().setBorder(Border.NO_BORDER).setFontColor(WebColors.getRGBColor("#424242"));
        paymentTable.setHeight(30);
        paymentTable.setWidth(UnitValue.createPercentValue(100));
        return paymentTable;
    }

    private static boolean isDomestic(String transferDataJson) {
        Optional<JSONObject> validatedJsonRsp = validateJsonObject(transferDataJson);
        String transferType = validatedJsonRsp.map(jsonObj -> extractJsonValue("transferType", jsonObj)).get();

        return isDomesticByType(transferType);
    }

    private static boolean isDomesticByType(String transferType) {
        return "domestic".equalsIgnoreCase(transferType);
    }

    protected void paymentTableData(Document document, PdfFont regularFont, String transferDataJson) throws Exception {
        logger.error("paymentTableData");
        Table paymentTable = getTable();
        paymentTable.setWidth(UnitValue.createPercentValue(100));        
        paymentTable.setMarginTop(15);
        
        Optional<JSONObject> validatedJsonRsp = null;
        String stringAmount = "";
        String reformattedStr = null;
        String fromAccountNumber = "";
        String paymentCurrency = "";
        String transferType = "";
        String swiftCode = "";

        try {
            validatedJsonRsp = validateJsonObject(transferDataJson);

            logger.error("validatedJsonRsp =" + validatedJsonRsp);

            String amount = validatedJsonRsp.map(jsonObj -> extractJsonValue("transactionAmount", jsonObj)).get();
            double amountDouble = Double.parseDouble(amount);
            stringAmount = roundingDecimal(amountDouble);

            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
            String valueDate = validatedJsonRsp.map(jsonObj -> extractJsonValue("valueDate", jsonObj)).get();           
            reformattedStr =  newFormat.format(oldFormat.parse(valueDate));

            fromAccountNumber = validatedJsonRsp.map(jsonObj -> extractJsonValue("acc_no_or_iban", jsonObj)).get();
            paymentCurrency = validatedJsonRsp.map(jsonObj -> extractJsonValue("fromAccountCurrency", jsonObj)).get();
            transferType = validatedJsonRsp.map(jsonObj -> extractJsonValue("transferType", jsonObj)).get();
            swiftCode = validatedJsonRsp.map(jsonObj -> extractJsonValue("swift", jsonObj)).get();

            String lastDigitAccountNumber;
            if (fromAccountNumber == null || fromAccountNumber.length() < 4) {
                lastDigitAccountNumber = fromAccountNumber;
                logger.error("From account number:- "+fromAccountNumber);
            } else {
                lastDigitAccountNumber = fromAccountNumber.substring(fromAccountNumber.length() - 4);
            }
            System.out.println("Last 4 numbers:- "+lastDigitAccountNumber);
        } catch (Exception e) {
            logger.error("error1 =" + e.getMessage());
        }
//        String foreignExchangeRate = validatedJsonRsp.map(jsonObj -> extractJsonValue("foreignExchangeRate", jsonObj)).get();
//        double foreignExchangeRateDouble = Double.parseDouble(amount);
//        String stringForeignExchangeRate = roundingDecimal(amountDouble);


        Map<Integer, String> attributeList = new HashMap<>();
        if (null != validatedJsonRsp) {
                
            if (isDomesticByType(transferType)){
                attributeList.put(1, validatedJsonRsp.map(jsonObj -> extractJsonValue("displayAccName", jsonObj)).orElse(""));
                attributeList.put(2, validatedJsonRsp.map(jsonObj -> extractJsonValue("toName", jsonObj)).orElse(""));
                attributeList.put(3, validatedJsonRsp.map(jsonObj -> extractJsonValue("toAccountNo", jsonObj)).orElse(""));
                attributeList.put(4, validatedJsonRsp.map(jsonObj -> extractJsonValue("bankname", jsonObj)).orElse(""));
                attributeList.put(5, validatedJsonRsp.map(jsonObj -> extractJsonValue("branchCode", jsonObj)).orElse(""));
                attributeList.put(6, validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentCurrency", jsonObj)).orElse(""));
                attributeList.put(7, stringAmount);
                attributeList.put(8, reformattedStr);      
                attributeList.put(9, validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentType", jsonObj)).orElse(""));          
                attributeList.put(10, validatedJsonRsp.map(jsonObj -> extractJsonValue("beneficiaryReference", jsonObj)).orElse(""));
                attributeList.put(11, validatedJsonRsp.map(jsonObj -> extractJsonValue("statementReference", jsonObj)).orElse(""));
                attributeList.put(12, validatedJsonRsp.map(jsonObj -> extractJsonValue("customerName", jsonObj)).orElse(""));
                attributeList.put(13, validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentReference", jsonObj)).orElse(""));                
                
            } else {
                attributeList.put(1, validatedJsonRsp.map(jsonObj -> extractJsonValue("displayAccName", jsonObj)).orElse(""));
                attributeList.put(2, validatedJsonRsp.map(jsonObj -> extractJsonValue("toName", jsonObj)).orElse(""));
                attributeList.put(3, validatedJsonRsp.map(jsonObj -> extractJsonValue("toAccountNo", jsonObj)).orElse(""));
                attributeList.put(4, validatedJsonRsp.map(jsonObj -> extractJsonValue("bankname", jsonObj)).orElse(""));
                attributeList.put(5, validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentCurrency", jsonObj)).orElse(""));
                attributeList.put(6, stringAmount);
                attributeList.put(7, reformattedStr);
                attributeList.put(8, validatedJsonRsp.map(jsonObj -> extractJsonValue("feesPaidBy", jsonObj)).orElse(""));
                attributeList.put(9, validatedJsonRsp.map(jsonObj -> extractJsonValue("beneficiaryReference", jsonObj)).orElse(""));
                attributeList.put(10, validatedJsonRsp.map(jsonObj -> extractJsonValue("statementReference", jsonObj)).orElse(""));
                attributeList.put(11, validatedJsonRsp.map(jsonObj -> extractJsonValue("customerName", jsonObj)).orElse(""));
                attributeList.put(12, validatedJsonRsp.map(jsonObj -> extractJsonValue("paymentReference", jsonObj)).orElse(""));
                attributeList.put(13, validatedJsonRsp.map(jsonObj -> extractJsonValue("complianceType", jsonObj)).orElse(""));
                attributeList.put(14, validatedJsonRsp.map(jsonObj -> extractJsonValue("foreignExchangeRate", jsonObj)).orElse(""));
                attributeList.put(15, validatedJsonRsp.map(jsonObj -> extractJsonValue("noOfDocument", jsonObj)).orElse(""));
            }
        } else{
            attributeList.put(1, " ");
            attributeList.put(2, " ");
            attributeList.put(3, " ");
            attributeList.put(4, " ");
            attributeList.put(5, " ");
            attributeList.put(6, " ");
            attributeList.put(7, " ");
            attributeList.put(8, " ");
            attributeList.put(9, " ");
            attributeList.put(10, " ");
            attributeList.put(11, " ");
            attributeList.put(12, " ");
            attributeList.put(13, " ");
            attributeList.put(14, " ");
            attributeList.put(15, " ");
        }


        try {
            List<Map.Entry<Integer, String>> list = new ArrayList<>(attributeList.entrySet());
            list.sort(Map.Entry.comparingByKey());

            if(isDomesticByType(transferType)){
                for (Map.Entry<Integer, String> element : attributeList.entrySet()) {
                    System.out.println("Element key:- " + element.getKey());
                    System.out.println("Element Value:- " + element.getValue());
                    if (element.getKey() != null) {
                        if (element.getKey().equals(1)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("From account name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()+" "+fromAccountNumber+"  "+paymentCurrency).setFont(regularFont).setFontSize(9f)));
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("From account number").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(fromAccountNumber).setFont(regularFont).setFontSize(9f)));

                        } else if (element.getKey().equals(2)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("To account name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(3)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("To account number").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(4)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Bank name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));                            
                        }else if (element.getKey().equals(5)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Branch code").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(6)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Payment currency").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

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
                                    .add(new Paragraph("Value date").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        }else if (element.getKey().equals(9)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Payment method").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(10)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Beneficiary statement reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(11)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Your statement reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f))); // uditha add your statement reference
                        } else if (element.getKey().equals(12)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Customer name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(13)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Payment reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        }
                    }
                }
            } else {
                for (Map.Entry<Integer, String> element : attributeList.entrySet()) {
                    System.out.println("Element key:- " + element.getKey());
                    System.out.println("Element Value:- " + element.getValue());
                    if (element.getKey() != null) {
                        if (element.getKey().equals(1)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("From account name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()+" "+fromAccountNumber+"  "+paymentCurrency).setFont(regularFont).setFontSize(9f)));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("From account number / IBAN").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(fromAccountNumber).setFont(regularFont).setFontSize(9f)));

                        } else if (element.getKey().equals(2)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("To account name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(3)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("To account number / IBAN").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("BIC / SWIFT").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(swiftCode).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(4)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Bank name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(5)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Payment currency").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(6)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Amount").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(7)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Value date").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(8)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Fees paid by").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(9)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Beneficiary statement reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(10)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Your statement reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(11)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Customer name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(12)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Payment reference").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(13)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Compliance type").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        } else if (element.getKey().equals(14)) {
                            HashMap<String, String> map = arrayToObject(element.getValue());
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Foreign exchange rate").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            if (null != map) {
                                paymentTable.addCell(getNoBorderCell()
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .add(new Paragraph(map.get("coverNumber")).setFont(regularFont).setFontSize(9f)));
                            }

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(" ").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            if (null != map) {
                                paymentTable.addCell(getNoBorderCell()
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .add(new Paragraph(map.get("exchangeRate")).setFont(regularFont).setFontSize(9f)));
                            }

                        } else if (element.getKey().equals(15)) {
                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph("Number of documents").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                            paymentTable.addCell(getNoBorderCell()
                                    .setTextAlignment(TextAlignment.LEFT)
                                    .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                        }

                    }
                }
            }

        } catch (Exception e) {
            logger.error("pdf generation error =" + e.getMessage());
        }
        document.add(paymentTable);
        logger.error("pdf generation method finished");
        logger.error("##exit addBeneData");
    }

    ////////////// uditha start
    protected void proofOfPaymentDomestic(Document document, PdfFont regularFont, String proofOfPayment) throws Exception {
        Table proofOfPaymentTable = getTable();
        proofOfPaymentTable.setWidth(UnitValue.createPercentValue(100));
        proofOfPaymentTable.setMarginTop(15);

        proofOfPaymentTable.addCell(getNoBorderCell()
                .setTextAlignment(TextAlignment.LEFT)
                .add(new Paragraph("Name").setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

        proofOfPaymentTable.addCell(getNoBorderCell()
                .setTextAlignment(TextAlignment.LEFT)
                .add(new Paragraph("Details").setFont(regularFont).setFontSize(9f)));

        getPopEmailOrPhoneList(proofOfPayment).forEach(
                (k, v) -> {
                    proofOfPaymentTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph(k).setFont(regularFont).setFontSize(9f).setFontColor(WebColors.getRGBColor("#727272"))));

                    proofOfPaymentTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph(v).setFont(regularFont).setFontSize(9f)));
                }
        );

        document.add(proofOfPaymentTable);
    }

    private static Map<String, String> getPopEmailOrPhoneList(String proofOfPayment) {
//        LOG.debug(" start getPopValue");
        Map values = new HashMap<String, String>();
        try {
            if (SBGCommonUtils.isStringNotEmpty(proofOfPayment)) {
                JSONArray list = new JSONArray(proofOfPayment.replace("\\", ""));
                if (null != list) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject pop = list.getJSONObject(i);
                        if (null != pop) {
                            String name = pop.getString("name");
                            String type = pop.getString("popType");
                            String value = pop.getString("popValue");
                            values.put(name, type + "  " + value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("SbgAlertUtil getPopValue error " + e.getMessage());
        }
        return values;
    }

    ////////////////// uditha end

    protected String roundingDecimal(Double inputNumber) {
        final DecimalFormat dfZero = new DecimalFormat("0.00");
        String format = dfZero.format(inputNumber);
        return format;
    }

    protected Table auditTableHeader(Table auditTable, PdfFont blackFont, String transferDataJson) {
        logger.error("##inside addBeneHeader");
        
        if (!isDomestic(transferDataJson)) {
            auditTable.setMarginTop(10);
            auditTable.setHeight(20);
        } else {           
            auditTable.setMarginTop(22); 
            auditTable.setHeight(35);
        }
        auditTable.addCell(getNoBorderCell().add(new Paragraph("Audit Details")))
                .setTextAlignment(TextAlignment.LEFT).setFontSize(10f).setFont(blackFont).setBold().setBorder(Border.NO_BORDER).setFontColor(WebColors.getRGBColor("#424242"));

        
        
        auditTable.setWidth(UnitValue.createPercentValue(50));
        return auditTable;
    }

    protected void auditTableData(Document document, PdfFont regularFont, String auditDataJson, String transferDataJson) throws Exception {
        int space;
        Table auditTable = getBeneTable();
        auditTable.setWidth(UnitValue.createPercentValue(50));
        auditTable.setMarginTop(10);

        Optional<JSONObject> validatedJsonRsp = validateJsonObject(transferDataJson);
        JSONArray auditDataJsonArray = validateJsonArray(auditDataJson);
        Map<Integer, String> attributeList = new HashMap<>();

        if (auditDataJsonArray.length() <= 0) {
            auditDataJson = null;
        }

        if (auditDataJsonArray != null) {
            for (int i = 0; i < auditDataJsonArray.length(); i++) {
                Optional<JSONObject> jsonArray = Optional.ofNullable(auditDataJsonArray.getJSONObject(i));
                if (i == auditDataJsonArray.length() - 1) {
                    attributeList.put(1, validatedJsonRsp.map(jsonObj -> extractJsonValue("captureName", jsonObj)).orElse(""));
                    attributeList.put(2, jsonArray.map(jsonObj -> extractJsonValue("captureDateTime", jsonObj)).orElse(""));
                }
                attributeList.put(3, jsonArray.map(jsonObj -> extractJsonValue("approverName", jsonObj)).orElse(""));
            }
        }

        for (Map.Entry<Integer, String> element : attributeList.entrySet()) {
            System.out.println("Element key:- " + element.getKey());
            System.out.println("Element Value:- " + element.getValue());
            if (element.getKey() != null) {
                if (element.getKey().equals(1)) {
                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph("Capture").setFont(regularFont).setFontSize(9f)));

                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                } else if (element.getKey().equals(2)) {
                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph("Date & Time").setFont(regularFont).setFontSize(9f)));

                    String valueDate = element.getValue();
                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    valueDate = newFormat.format(myFormat.parse(valueDate));

                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph(valueDate).setFont(regularFont).setFontSize(9f)));
                } else {
                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph("Approver/s").setFont(regularFont).setFontSize(9f)));

                    auditTable.addCell(getNoBorderCell()
                            .setTextAlignment(TextAlignment.LEFT)
                            .add(new Paragraph(element.getValue()).setFont(regularFont).setFontSize(9f)));
                }
            }
        }

        if (auditDataJson == null) {
            space = 3;
            addEmptyLine(document, space);
        } else {
            space = 0;
            addEmptyLine(document, space);
        }
        document.add(auditTable);
        logger.error("##exit addBeneData");
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

    public void addHeadline(Document document, PdfFont blackFont) {
        logger.error("##inside addHeadline");
        Div headLineDiv = new Div();
        headLineDiv.setBorderTop(new SolidBorder(WebColors.getRGBColor("#A0A0A0"), 1f, 0.6f));
        headLineDiv.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#A0A0A0"), 1f, 0.6f));
        headLineDiv.add(new Paragraph("Payment activities summary report").setFont(blackFont).setFontSize(12f).setBold()).setFontColor(WebColors.getRGBColor("#424242"));
        headLineDiv.setMarginBottom(10);
        document.add(headLineDiv);
    }

    private static void addEmptyLine(Document document, int number) {
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
            document.add(paragraph);
        }
    }

    protected String getDateTimeDDMMYYYYHHMMSS() {
        String timestamp = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        Date date = new Date();
        timestamp = sdf.format(date);
        return timestamp;
    }

    private Map<String, String> getRegulatoryData(JSONArray jsonArray, String paymentCurrency) {

        Map<String, String> attributeList = new HashMap<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonRegulatoryObj = jsonArray.getJSONObject(i);
                String code = extractJsonValue("CategoryCode", jsonRegulatoryObj);
                String subCode = extractJsonValue("SubCategoryCode", jsonRegulatoryObj);
                String fullCode = code + (StringUtils.isNotBlank(subCode) ? "/" + subCode : "");
                String regulatoryCode = fullCode + " " + BOPcategoryMetaDataEnum.Code_100.getDescriptionbycode(fullCode);
                String stringForeignValue = extractJsonValue("ForeignValue", jsonRegulatoryObj);
                double fIntAmount = 0.00;
                if (stringForeignValue != null && stringForeignValue.equals("") != true){
                    double foreignValueDouble = Double.parseDouble(stringForeignValue);
                    String fAmount = roundingDecimal(foreignValueDouble);
                    fIntAmount = Double.parseDouble(fAmount);
                }

                String stringDomesticValue = extractJsonValue("DomesticValue", jsonRegulatoryObj);
                double dIntAmount = 0.00;
                if (stringDomesticValue != null && stringDomesticValue.equals("") != true){
                    double domesticValueDouble = Double.parseDouble(stringDomesticValue);
                    String dAmount = roundingDecimal(domesticValueDouble);
                    dIntAmount = Double.parseDouble(dAmount);
                }

                double conbineVal = fIntAmount + dIntAmount;

                attributeList.put(regulatoryCode,paymentCurrency + " " + roundingDecimal(conbineVal));
            }
        }

        return attributeList;
    }

    private static Optional<JSONObject> validateJsonObject(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray records = jsonObject.getJSONArray("records");
            JSONObject jsonArray = records.getJSONObject(0);
            return Optional.ofNullable(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static JSONArray validateJsonArray(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray records = jsonObject.getJSONArray("records");
            return records;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONArray validateDocumentJsonArray(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray records = jsonObject.getJSONArray("sbg_documents");
            return records;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONArray validateBopDataJsonArray(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonReportArray = jsonObject.getJSONObject("Report").getJSONArray("MonetaryAmount");
            return jsonReportArray;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static String extractJsonValue(String jsonKey, JSONObject transactionObject) {
        try {
            return transactionObject.get(jsonKey).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private static HashMap<String, String> arrayToObject(String jsonString) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (!jsonString.equals("")){
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
