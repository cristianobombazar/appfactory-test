package com.kony.sbg.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.kony.sbg.util.SBGConstants;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
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
import com.itextpdf.layout.element.Cell;
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
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.dataobject.Result;

public class GenerateBeneficiary extends PDFGenerator {

	private static final Logger logger = Logger.getLogger(GenerateBeneficiary.class);

	private static final String BLACK_FONT = "fonts/SourceSansPro-Black.ttf";
	private static final String REGULAR_FONT = "fonts/SourceSansPro-Regular.ttf";

	public byte[] generateFile(JSONArray data, List<String> headers, Result result) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(os, new WriterProperties());
		PdfDocument pdf = new PdfDocument(writer);
		// Document document = new Document(pdf);
		Document document = new Document(pdf, PageSize.A4, false);

		/*
		 * By default wrap in itext is done using space and other characters but email
		 * is not splitted so adding '@' to split and wrap email
		 */
		document.setProperty(Property.SPLIT_CHARACTERS, new CustomSplitCharacters());
		document.setBottomMargin(150);

		try {

			FontProgram blackFontPrgm = FontProgramFactory.createFont(BLACK_FONT);
			PdfFont blackFont = PdfFontFactory.createFont(blackFontPrgm);
			FontProgram regFontPrgm = FontProgramFactory.createFont(REGULAR_FONT);
			PdfFont regularFont = PdfFontFactory.createFont(regFontPrgm);

			/*
			 * If size of selected print fields is exceeding threshold then set landscape
			 * mode
			 */
			if (headers.size() > SbgURLConstants.MAX_COL_PORTRAIT) {
				logger.info("##Setting Landscape Mode and adding header");
				pdf.setDefaultPageSize(PageSize.A4.rotate());
				addHeaderDetailsLandscape(document, blackFont, regularFont);
			} else {
				logger.info("##Setting header details for Portrait");
				document.setBottomMargin(150);
				addHeaderDetailsPortrait(document, blackFont, regularFont);
			}

			Table beneTable = getBeneTable(headers);
			addBeneHeader(beneTable, headers, blackFont);
			addBeneData(data, beneTable, headers, regularFont);
			document.add(beneTable);
			if (headers.size() > SbgURLConstants.MAX_COL_PORTRAIT) {
				logger.info("##Setting Footer Details Landscape");
				addFooterDetailsLandscape(document, blackFont, regularFont);
			} else {
				logger.info("##Setting Footer Details Portrait");
				addFooterDetailsPortrait(document, blackFont, regularFont);
			}

			document.close();

		} catch (Exception exp) {
			logger.error("Error in GenerateBeneficiary: " + exp.getMessage());
			result = SbgErrorCodeEnum.ERR_100013.setErrorCode(result);
		}
		return os.toByteArray();

	}

	protected Table getBeneTable(List<String> headers) {
		/* Dynamically setting the number of columns and column width */
		float[] width = new float[headers.size()];
		int idx = 0;
		for (String element : headers) {
			Float val = SbgURLConstants.BENE_PDF_COL_WIDTH.get(element);
			width[idx] = val;
			idx++;
		}
		return new Table(UnitValue.createPercentArray(width));
	}

	protected void addBeneData(JSONArray data, Table beneTable, List<String> headers, PdfFont regularFont) {
		logger.info("##inside addBeneData");
		if (null != data && data.length() > 0) {
			for (Object element : data) {
				JSONObject rowData = (JSONObject) element;
				logger.info("##PayeeId:: " + rowData.getString("Id"));
				for (String fieldName : headers) {
					/*
					 * For phone number concat country code + phone number For address concat
					 * (addressLine1 + city + country + zipcode) fields For Beneficiary Name use
					 * entity type For Status always Active
					 */
					if (fieldName.equals("phoneNumber")) {
						if (rowData.has("phoneCountryCode")) {
							String phoneNumber = rowData.getString("phoneCountryCode") + " "
									+ rowData.getString(fieldName);
							beneTable.addCell(getNoBorderCell()
									.add(new Paragraph(getValue(phoneNumber)).setFont(regularFont).setFontSize(9f)));
						} else {
							beneTable.addCell(getNoBorderCell());
						}
					} else if (fieldName.equals("address")) {
						String address = getAddress(rowData);
						if (address.isEmpty() == false) {
							beneTable.addCell(
									getNoBorderCell().add(new Paragraph(getValue(address)).setFont(regularFont).setFontSize(9f)));
						} else {
							beneTable.addCell(getNoBorderCell());
						}
					} else if (fieldName.equals("beneficiaryName")) {
						String beneficiaryName = getBeneficiaryName(rowData);
						if (beneficiaryName.isEmpty() == false) {
							beneTable.addCell(getNoBorderCell()
									.add(new Paragraph(getValue(beneficiaryName)).setFont(regularFont).setFontSize(9f)));
						} else {
							beneTable.addCell(getNoBorderCell());
						}
					} else if (fieldName.equals("status")) {
						beneTable.addCell(getNoBorderCell().add(new Paragraph(SbgURLConstants.BENEFICIARY_STATUS)
								.setFont(regularFont).setFontSize(9f)));
					} else {
						if (rowData.has(fieldName) && rowData.getString(fieldName).isEmpty() == false) {
							beneTable.addCell(getNoBorderCell().add(
									new Paragraph(getValue(rowData.getString(fieldName))).setFont(regularFont).setFontSize(9f)));
						} else {
							beneTable.addCell(getNoBorderCell());
						}
					}
				}
			}
		}
		logger.info("##exit addBeneData");
	}

	private static String getValue(String value) {

		if (null == value || value.contains("undefined")) {
			return "--";
		}

		return value;
	}

	protected String getAddress(JSONObject rowData) {
		String address = "";

		if (rowData.has("addressLine1") && rowData.getString("addressLine1").isEmpty() == false) {
			address = address + rowData.getString("addressLine1") + ", ";
		}

		if (rowData.has("city") && rowData.getString("city").isEmpty() == false) {
			address = address + rowData.getString("city") + ", ";
		}

		if (rowData.has("country") && rowData.getString("country").isEmpty() == false) {
			address = address + rowData.getString("country") + ", ";
		}

		if (rowData.has("zipcode") && rowData.getString("zipcode").isEmpty() == false) {
			address = address + rowData.getString("zipcode");
		}

		if (address.endsWith(", ")) {
			address = address.substring(0, address.length() - 1);
		}

		return address;
	}

	protected String getBeneficiaryName(JSONObject rowData) {
		String beneficiaryName = "";

		if (rowData.has("entityType")) {
			if (rowData.getString("entityType").equals("INDIVIDUAL")) {
				if (rowData.has("firstName") && rowData.getString("firstName").isEmpty() == false) {
					beneficiaryName = rowData.getString("firstName") + " ";
				}

				if (rowData.has("lastName") && rowData.getString("lastName").isEmpty() == false) {
					beneficiaryName = beneficiaryName + rowData.getString("lastName");
				}
			} else if (rowData.getString("entityType").equals("ENTITY") && rowData.has("beneficiaryName")) {
				beneficiaryName = rowData.getString("beneficiaryName");
			}
		}

		beneficiaryName = beneficiaryName.trim();

		return beneficiaryName;
	}

	protected Table addBeneHeader(Table beneTable, List<String> headers, PdfFont blackFont) {
		logger.info("##inside addBeneHeader");
		for (int i = 0; i < headers.size(); i++) {
			beneTable.addHeaderCell(new Cell().setBackgroundColor(WebColors.getRGBColor("rgb(245,245,245)"))
					.add(new Paragraph(SbgURLConstants.BENE_HEADERS.get(headers.get(i)))).setBorder(Border.NO_BORDER)
					.setTextAlignment(TextAlignment.LEFT).setFontSize(9f).setFont(blackFont));
		}
		//beneTable.getHeader().setBorder(new SolidBorder(0 .1f));
		beneTable.setWidth(UnitValue.createPercentValue(100));
		return beneTable;
	}

	/*
	 * By default wrap in itext is done using space and other characters but email
	 * is not splitted so adding '@' to split and wrap email
	 */
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

	protected void addHeaderDetailsLandscape(Document document, PdfFont blackFont, PdfFont regularFont)
			throws Exception {
		logger.info("##inside addHeaderDetailsLandscape");
		// addLogoImageLandscape(document);
		addBankLogosLandscape(document);
		addBankNameLanscape(document, blackFont, regularFont);
		addHeadline(document, blackFont);
	}

	public void addBankLogosLandscape(Document document) throws IOException {
		Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[] {1, 1}));
		bankDetailsTable.addCell(getNoBorderCell().add(addLogoImageLandscape()));
		bankDetailsTable.addCell(getNoBorderCell().add(addATALogoImageLandscape()));
		bankDetailsTable.setMarginBottom(10);
		document.add(bankDetailsTable);
	}

	// public void addLogoImageLandscape(Document document) throws IOException {
	// 	logger.info("##inside addLogoImage");
	// 	InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/BrandLogoWithText.png");
	// 	if (is != null) {
	// 		Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
	// 		Div imageDiv = new Div();
	// 		// imageDiv.setMarginLeft(10);
	// 		imageDiv.setHeight(35f);
	// 		imageDiv.setWidth(UnitValue.createPercentValue(50));
	// 		konyLogo.setWidth(imageDiv.getWidth());
	// 		konyLogo.setHeight(imageDiv.getHeight());
	// 		imageDiv.add(konyLogo);
	// 		imageDiv.setMarginBottom(20);
	// 		document.add(imageDiv);
	// 	} else {
	// 		logger.error("##InputStream is null");
	// 	}
	// }

	public Div addLogoImageLandscape() throws IOException {
		InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/BrandLogoWithText.png");
		Div imageDiv = new Div();
		if (is != null) {
			Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
			imageDiv.setHeight(40f);
			imageDiv.setWidth(UnitValue.createPercentValue(75));
			konyLogo.setWidth(imageDiv.getWidth());
			konyLogo.setHeight(imageDiv.getHeight());
			imageDiv.add(konyLogo);
			imageDiv.setMarginBottom(10);
		} else {
			logger.error("##InputStream is null");
		}
		return imageDiv;
	}

	public Div addATALogoImageLandscape() throws IOException {
		InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/SB_ATA_PO_rgb.png");
		Div imageDiv = new Div();
		if (is != null){
			Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
			imageDiv.setMarginLeft(220);
			imageDiv.setHeight(40f);
			imageDiv.setWidth(UnitValue.createPercentValue(100));
			konyLogo.setWidth(imageDiv.getWidth());
			konyLogo.setHeight(imageDiv.getHeight());
			imageDiv.add(konyLogo);
			imageDiv.setMarginBottom(10);
		}else{
			logger.error("##Image not found");
		}
		return imageDiv;
	}

	public void addBankNameLanscape(Document document, PdfFont blackFont, PdfFont regularFont) throws IOException {
		logger.info("##inside addBankNameLanscape");
		Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
		bankDetailsTable.addCell(getNoBorderCell().add(getBankNameDivLanscape(blackFont)));
		bankDetailsTable.addCell(getNoBorderCell().add(getGeneratedCopyLanscape(regularFont)));
		bankDetailsTable.setMarginBottom(10);
		document.add(bankDetailsTable);
	}

	public Div getBankNameDivLanscape(PdfFont blackFont) {
		logger.info("##inside getBankNameDivLanscape");
		Div bankNameDiv = new Div();
		bankNameDiv.setHeight(50f);
		bankNameDiv.setWidth(UnitValue.createPercentValue(60));
		bankNameDiv.add(
				new Paragraph(" The Standard Bank of South Africa Limited " + "Company Registration No, 1962/000738/06")
						.setFont(blackFont).setFontSize(11f));
		return bankNameDiv;
	}

	public Div getGeneratedCopyLanscape(PdfFont regularFont) {
		logger.info("##inside getGeneratedCopyLanscape");
		Div bankNameDiv = new Div();
		bankNameDiv.setWidth(UnitValue.createPercentValue(60));
		bankNameDiv.setMarginLeft(255);
		bankNameDiv.setMarginTop(18);
		bankNameDiv.add(new Paragraph(" Computer generated copy ").setFont(regularFont).setFontSize(11f));
		return bankNameDiv;
	}

	public void addHeadline(Document document, PdfFont blackFont) {
		logger.info("##inside addHeadline");
		Div headLineDiv = new Div();
		headLineDiv.setBorderTop(new SolidBorder(ColorConstants.DARK_GRAY, 0.3f, 0.6f));
		headLineDiv.setBorderBottom(new SolidBorder(ColorConstants.DARK_GRAY, 0.3f, 0.6f));
		headLineDiv.add(new Paragraph("Beneficiary list summary report").setFont(blackFont).setFontSize(12f));
		headLineDiv.setMarginBottom(10);
		document.add(headLineDiv);
	}

	protected void addHeaderDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont)
			throws Exception {
		logger.info("##inside addHeaderDetailsPortrait");
		// addLogoImagePortrait(document);
		addBankLogosPortrait(document);
		addBankNamePortrait(document, blackFont, regularFont);
		addHeadline(document, blackFont);
	}

	public void addBankLogosPortrait(Document document) throws IOException {
		Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float []{1, 1}));
		bankDetailsTable.addCell(getNoBorderCell().add(addLogoImagePortrait()));
		bankDetailsTable.addCell(getNoBorderCell().add(addATALogoImagePortrait()));
		bankDetailsTable.setMarginBottom(10);
		document.add(bankDetailsTable);

	}

	// public void addLogoImagePortrait(Document document) throws IOException {
	// 	logger.info("##inside addLogoImagePortrait");
	// 	InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/BrandLogoWithText.png");
	// 	if (is != null) {
	// 		Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
	// 		Div imageDiv = new Div();
	// 		// imageDiv.setMarginLeft(10);
	// 		imageDiv.setHeight(35f);
	// 		imageDiv.setWidth(UnitValue.createPercentValue(60));
	// 		konyLogo.setWidth(imageDiv.getWidth());
	// 		konyLogo.setHeight(imageDiv.getHeight());
	// 		imageDiv.add(konyLogo);
	// 		imageDiv.setMarginBottom(20);
	// 		document.add(imageDiv);
	// 	} else {
	// 		logger.error("##Image not found");
	// 	}
	// }

	public Div addLogoImagePortrait() throws IOException {
		InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/BrandLogoWithText.png");
		Div imageDiv = new Div();
		if(is != null){
			Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
			imageDiv.setHeight(28f);
			imageDiv.setWidth(UnitValue.createPercentValue(75));
			konyLogo.setWidth(imageDiv.getWidth());
			konyLogo.setHeight(imageDiv.getHeight());
			imageDiv.add(konyLogo);
			imageDiv.setMarginBottom(20);

		}else{
			logger.error("##Image not found");
		}
		return imageDiv;
	}

	public Div addATALogoImagePortrait() throws IOException{
		InputStream is = GenerateBeneficiary.class.getClassLoader().getResourceAsStream("images/SB_ATA_PO_rgb.png");
		Div imageDiv = new Div();
		if (is != null){
			Image konyLogo = new Image(ImageDataFactory.create(IOUtils.toByteArray(is)));
			imageDiv.setMarginLeft(75);
			imageDiv.setHeight(40f);
			imageDiv.setWidth(UnitValue.createPercentValue(100));
			konyLogo.setWidth(imageDiv.getWidth());
			konyLogo.setHeight(imageDiv.getHeight());
			imageDiv.add(konyLogo);
		}else{
			logger.error("##Image not found");
		}
		return imageDiv;
	}

	public void addBankNamePortrait(Document document, PdfFont blackFont, PdfFont regularFont) throws IOException {
		logger.info("##inside addBankNamePortrait");
		Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
		bankDetailsTable.addCell(getNoBorderCell().add(getBankNameDivPortrait(blackFont)));
		bankDetailsTable.addCell(getNoBorderCell().add(getGeneratedCopyPortrait(regularFont)));
		bankDetailsTable.setMarginBottom(10);
		document.add(bankDetailsTable);

	}

	public Div getBankNameDivPortrait(PdfFont blackFont) {
		logger.info("##inside getBankNameDivPortrait");
		Div bankNameDiv = new Div();
		bankNameDiv.setHeight(50f);
		bankNameDiv.setWidth(UnitValue.createPercentValue(85));
		bankNameDiv.add(
				new Paragraph(" The Standard Bank of South Africa Limited " + "Company Registration No, 1962/000738/06")
						.setFont(blackFont).setFontSize(11f));
		return bankNameDiv;
	}

	public Div getGeneratedCopyPortrait(PdfFont regularFont) {
		logger.info("##inside getGeneratedCopyPortrait");
		Div bankNameDiv = new Div();
		bankNameDiv.setWidth(UnitValue.createPercentValue(70));
		bankNameDiv.setMarginLeft(125);
		bankNameDiv.setMarginTop(18);
		bankNameDiv.add(new Paragraph(" Computer generated copy ").setFont(regularFont).setFontSize(11f));
		return bankNameDiv;
	}

	protected void addFooterDetailsLandscape(Document document, PdfFont black, PdfFont regular) throws Exception {
		logger.info("##inside addFooterDetailsLandscape");
		try {
			int numberOfPages = document.getPdfDocument().getNumberOfPages();
			logger.info("##inside addFooterDetailsLandscape:numberOfPages" + numberOfPages);
			for (int i = 1; i <= numberOfPages; i++) {

				document.showTextAligned(new Paragraph("End of report").setFont(black).setFontSize(9f), 35, 105, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				document.flush();

				document.showTextAligned(
						new Paragraph(String.format("Page %s of %s", i, numberOfPages)).setFont(black).setFontSize(9f),
						800, 105, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				document.flush();

				document.showTextAligned(
						new Paragraph("Created " + getDateTimeDDMMYYYYHHMMSS() + " EAT").setFont(black).setFontSize(9f),
						800, 93, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
				document.flush();

				document.showTextAligned(new Paragraph("Disclaimer").setFont(black).setFontSize(9f), 35, 68, i,
						TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
				document.flush();

				document.showTextAligned(new Paragraph(
						"The Standard Bank Group provides the details as \"information only\". Based on that aforesaid, the Standard Bank Group makes no one representation or warranty, whether expressed or implied as to ")
						.setFont(regular).setFontSize(9.4f), 35, 52, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
						0);
				document.flush();

				document.showTextAligned(new Paragraph(
						"the integrity, accuracy completeness or reliability of any information contained herein.")
						.setFont(regular).setFontSize(9.4f), 35, 40, i, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
						0);
				document.flush();

			}
		} catch (Exception ae) {
			logger.error("##Error occured while generating pdf doc for bene" + ae);
		}
	}

	protected void addFooterDetailsPortrait(Document document, PdfFont blackFont, PdfFont regularFont)
			throws Exception {
		logger.info("##inside addFooterDetailsPortrait");
		int numberOfPages = document.getPdfDocument().getNumberOfPages();
		for (int i = 1; i <= numberOfPages; i++) {
			document.showTextAligned(new Paragraph("End of report").setFont(blackFont).setFontSize(9f), 35, 105, i,
					TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(
					new Paragraph(String.format("Page %s of %s", i, numberOfPages)).setFont(blackFont).setFontSize(9f),
					554, 105, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(
					new Paragraph("Created " + getDateTimeDDMMYYYYHHMMSS() + " EAT").setFont(blackFont).setFontSize(9f),
					554, 93, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(new Paragraph("Disclaimer").setFont(blackFont).setFontSize(9f), 35, 68, i,
					TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(new Paragraph(
					"The Standard Bank Group provides the details as \"information only\". Based on that aforesaid, the Standard Bank Group makes no ")
					.setWidth(800).setFont(regularFont).setFontSize(9f), 35, 52, i, TextAlignment.LEFT,
					VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(new Paragraph(
					"one representation or warranty, whether expressed or implied as to the integrity, accuracy completeness or reliability of any information ")
					.setWidth(800).setFont(regularFont).setFontSize(9f), 35, 40, i, TextAlignment.LEFT,
					VerticalAlignment.BOTTOM, 0);

			document.showTextAligned(
					new Paragraph("contained herein.").setWidth(800).setFont(regularFont).setFontSize(9f), 35, 28, i,
					TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

		}
	}

	protected String getDateTimeDDMMYYYYHHMMSS() {
		String timestamp = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date = new Date();
		timestamp = sdf.format(date);
		return timestamp;
	}

}
