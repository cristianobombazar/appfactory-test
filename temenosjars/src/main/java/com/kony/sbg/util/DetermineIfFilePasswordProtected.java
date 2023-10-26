package com.kony.sbg.util;

import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.HWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DetermineIfFilePasswordProtected {

    private static final Logger LOG = Logger.getLogger(DetermineIfFilePasswordProtected.class);

    public Result checkIfFilePasswordProtected(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws IOException, InvalidFormatException {
        LOG.debug("checkIfFilePasswordProtected() ----------------> Entered");
        Map<String, String> inputParamMap = HelperMethods.getInputParamMap(inputArray);
        String contents = inputParamMap.get("contents");
        String fileType = inputParamMap.get("fileType");
        Result result = new Result();

        if (StringUtils.isNotBlank(contents) && StringUtils.isNotBlank(fileType)) {
            boolean isPasswordProtected = false;
            byte[] data = convertFromBase64(contents);

            switch (fileType) {
                case "pdf":
                    isPasswordProtected = pdfPasswordProtected(data);
                    break;
                case "xls":
                    isPasswordProtected = xlsPasswordProtected(data);
                    break;
                case "xlsx":
                    isPasswordProtected = xlsxFilePasswordProtected(data);
                    break;
                case "docx":
                    isPasswordProtected = docxFilePasswordProtected(data);
                    break;
                case "doc":
                    isPasswordProtected = docFilePasswordProtected(data);
                    break;
                default:
                    result.addParam("errMsg", "Unsupported file type");
            }
            result.addParam("passwordProtected", String.valueOf(isPasswordProtected));
        } else {
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100010.getErrorCode()));
            result.addParam("errMsg", "Requested file type or contents not found.");
        }
        return result;
    }
    private byte[] convertFromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    private boolean pdfPasswordProtected(byte[] bytes) throws IOException {
        LOG.debug("Entered pdfPasswordProtected() ----->");
        boolean pdfProtected;

        Path filePath = Files.createTempFile(null, ".pdf");
        Files.write(filePath, bytes);
        pdfProtected = checkIfPdfPasswordProtected(filePath.toFile());
        deleteTempFile(filePath);

        return pdfProtected;
    }

    private boolean checkIfPdfPasswordProtected(File file) throws IOException {
        boolean encrypted = false;
        try {
            PDDocument doc = PDDocument.load(file);
            if (doc.isEncrypted()) {
                LOG.debug("checkIfPdfPasswordProtected() ------------> In doc.isEncrypted()");
                encrypted = true;
            }
            doc.close();
        } catch (InvalidPasswordException e) {
            LOG.debug("checkIfPdfPasswordProtected() ------------> In exception");
            encrypted = true;
        }
        return encrypted;
    }

    public boolean xlsPasswordProtected(byte[] bytes) throws IOException{
        LOG.debug("Entered xlsPasswordProtected() ----->");
        boolean xlsProtected;

        Path filePath = Files.createTempFile(null, ".xls");
        Files.write(filePath, bytes);
        xlsProtected = checkIfXlsFilePasswordProtected(filePath.toFile());
        deleteTempFile(filePath);

        return xlsProtected;
    }

    private Path createTempFileFromBase64(byte[] base64, String extension) throws IOException {

        Path temp = Files.createTempFile("", extension);
        FileUtils.writeByteArrayToFile(temp.toFile(), base64);

        return temp;
    }

    private boolean checkIfXlsFilePasswordProtected(File file){
        boolean xlsProtected = false;
        try {
            Workbook workbook = WorkbookFactory.create(file);
            LOG.debug("Opened file in checkIfXlsFilePasswordProtected() ----->");
            //workbook.close();
        } catch (EncryptedDocumentException | IOException e) {
            LOG.debug("xls file is password protected");
            xlsProtected = true;
        }
        return xlsProtected;
    }

    private boolean xlsxFilePasswordProtected(byte [] bytes) throws IOException{
        LOG.debug("Entered xlsxFilePasswordProtected() ----->");
        boolean xlsxProtected;

        Path filePath = Files.createTempFile(null, ".xlsx");
        Files.write(filePath, bytes);
        xlsxProtected = checkIfXlsxFilePasswordProtected(filePath.toFile());
        deleteTempFile(filePath);

        return xlsxProtected;
    }
    private boolean checkIfXlsxFilePasswordProtected(File file){
        boolean xlsxProtected = false;
        try {
            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(file);
            LOG.debug("Opened file in checkIfXlsxFilePasswordProtected() ----->");
            //workbook.close();
        } catch (EncryptedDocumentException e) {
            LOG.debug("xls file is password protected");
            xlsxProtected = true;
        } finally {
            return xlsxProtected;
        }
    }

    public boolean docFilePasswordProtected(byte [] bytes) throws IOException {
        LOG.debug("Entered docFilePasswordProtected() ----------->");
        boolean docProtected;

        Path filePath = createTempFileFromBase64(bytes,".doc");
        LOG.debug("Temp file path after creation-------------->"+filePath);
        docProtected = checkIfDocFilePasswordProtected(filePath.toFile());
        deleteTempFile(filePath);
        LOG.debug("Should return true/false ----------->");
        return docProtected;
    }

    private boolean checkIfDocFilePasswordProtected(File file) throws FileNotFoundException {
        LOG.debug("Entered checkIfDocFilePasswordProtected() ----------->");
        LOG.debug("Absolute file path--------------"+file.getAbsolutePath());
        FileInputStream fis=new FileInputStream(file.getAbsolutePath());
        try{
            HWPFDocument document = new HWPFDocument(fis);
            LOG.debug("The doc file is not password protected ----------->");
            return false;
        }catch (Exception ex){
            LOG.debug("The docx file IS password protected ----------->");
            return true;
        }

    }

    private boolean checkIfDocxFilePasswordProtected(File file) {
        LOG.debug("Entered checkIfDocxFilePasswordProtected() --------------->");
        try {
            //XWPFDocument doc = new XWPFDocument(OPCPackage.open(file));
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            POIFSFileSystem doc = new POIFSFileSystem(fis);
            LOG.debug("The docx file is not password protected ----------->");
            return true;
        } catch (Exception ex) {
            LOG.debug("The docx file IS password protected ----------->");
            return false;
        }
    }
    private boolean docxFilePasswordProtected(byte [] bytes) throws IOException {
        LOG.debug("docxFilePasswordProtected() ----->");
        boolean docxProtected;

        Path filePath = createTempFileFromBase64(bytes,".docx");
        docxProtected = checkIfDocxFilePasswordProtected(filePath.toFile());
        deleteTempFile(filePath);
        LOG.debug("Should be returning docx result ----------->");
        return docxProtected;
    }

    private void deleteTempFile(Path filePath) {
        try {
            boolean tempFileExists = Files.deleteIfExists(filePath);
            if (tempFileExists) {
                LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Temp file successfully deleted.");
            } else {
                LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Temp file doesn't exist.");
            }
        } catch (IOException e) {
            LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Unable to delete temp file");
            e.printStackTrace();
        }
    }
}
