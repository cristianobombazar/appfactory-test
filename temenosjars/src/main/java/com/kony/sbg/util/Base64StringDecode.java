package com.kony.sbg.util;

import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;

public class Base64StringDecode {

    private static final Logger LOG = Logger.getLogger(Base64StringDecode.class);

    public Result determineIfFilePasswordProtected(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws IOException {
        LOG.debug("determineIfFilePasswordProtected() ----------------> Entered");
        Map<String, String> inputParamMap = HelperMethods.getInputParamMap(inputArray);
        String contents = inputParamMap.get("contents");
        String fileType = inputParamMap.get("fileType");
        Result result = new Result();

        if (StringUtils.isNotBlank(contents) && StringUtils.isNotBlank(fileType)) {
            boolean isPasswordProtected = false;

            switch (fileType) {
                case "pdf":
                    byte[] data = convertFromBase64(contents);
                    isPasswordProtected = convertByteToPdfAndCheckIfProtected(data);
                    break;
                    //TODO: Add the rest of the file types according to scope
                default: result.addParam("errMsg", "Unsupported file type");
            }
            result.addParam("passwordProtected", String.valueOf(isPasswordProtected));
        } else {
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100010.getErrorCode()));
            result.addParam("errMsg", "Requested file type or contents not found.");
        }
        return result;
    }

    public boolean isDocumentPasswordProtected(String content, String fileType) {
        LOG.debug("##### Base64StringDecode.isDocumentPasswordProtected() ----------------> Starting");
        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(fileType)) {
            boolean isPasswordProtected = false;

            switch (fileType) {
                case "pdf":
                    byte[] data = convertFromBase64(content);
                    isPasswordProtected = convertByteToPdfAndCheckIfProtected(data);
                    LOG.debug("##### Base64StringDecode.isDocumentPasswordProtected() isPasswordProtected:"+isPasswordProtected);
                    break;
            }
            return isPasswordProtected;
        } else {
            return false;
        }
    }

    private byte[] convertFromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    private boolean convertByteToPdfAndCheckIfProtected(byte[] bytes) {
        boolean fileProtected = false;
        try {

            Path temp = Files.createTempFile(null, ".pdf");
            Files.write(temp, bytes);
            if (checkIfPdfPasswordProtected(temp.toFile()))
                fileProtected = true;
            boolean tempFileExists = Files.deleteIfExists(temp);
            if (tempFileExists) {
                LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Temp file successfully deleted.");
            } else {
                LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Temp file doesn't exist.");
            }
        } catch (IOException e) {
            LOG.debug("convertByteToPdfAndCheckIfProtected() ------> Unable to delete temp file");
            e.printStackTrace();
        }
            return fileProtected;
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
}
