package com.kony.sbg.sideloading.javaservices;

import com.kony.sbg.resources.impl.RetrieveDocumentResourceImpl;
import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.network.ArrangementHelper;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.sideloading.utils.Console;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UpdateExtensionDataService implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(UpdateExtensionDataService.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {

        LOG.debug("#### Starting --> UpdateExtensionDataService");
        Result result = new Result();
        try {
            String endpoint="https://standardbankdev.temenos-cloud.net/services/SbgArrangementMS/SbgUpdateArrangementAccount";
            JSONObject updateArrangementAccountRsp = updateArrangementAccount(null,endpoint);
            LOG.debug("##### updateArrangementAccountRsp:"+updateArrangementAccountRsp);
            return updateArrangementAccountRsp;
        } catch (Exception exp) {
            LOG.error("Exception occured in UpdateExtensionDataService: ", exp);
            LOG.error("Exception occured in UpdateExtensionDataService: " + exp.getStackTrace());
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
        }
        return result;
    }

    public static JSONObject updateArrangementAccount(JSONObject payload, String URL) {

        LOG.debug("##### Inside updateArrangementAccount()");

        try {
            String response		= invokeApiUpdateAccounts(URL, payload);
            JSONParser parser 	= new JSONParser();
            JSONObject retval	= (JSONObject)parser.parse(response);

            return retval;

        } catch (ParseException e) {
            LOG.debug("##### updateArrangementAccount() failed ",e);
        }

        return null;
    }

    private	static String invokeApiUpdateAccounts(String serviceURL, JSONObject payload) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(serviceURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-api-key", "tPir3u3P1h13gMBbBe0WR9SA78SXC3Gf8LFzZlUg");
//            conn.setRequestProperty("x-functions-key", ARRANGEMENT_AUTH_KEY);
            conn.setRequestProperty("Authorization", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGYWJyaWMiLCJleHAiOjE4MTkxNzA1NTUsImlhdCI6MTU4NzU0NzEwNn0.chZWZ4KPduQaATRh3EWKM4pXkk_VpzHnISIkGitb5OAPYYDq740eVdo_aeqyiQbLrzk74JBnMJx7XI4PzrQfW7ZzHeGff4Xkx_7fiKWCuyx0cc_T8f_a2GX9zRibj42ahd1mV7A8neg1HbEAsZS4X2RN_RrLRBf6jduigU2YSIkJhN6wx0XHlzbUryxIZchCKQ74p4q8HOb77XbtToJXfBGRJMwONk1TRObMEbSZJUr488vQlgj6Iq8lCQEY_NMaAI1P-YHGxgD6jLxmkdAYt7ho63B7DhvNCw6kUJjM-zkbJ5sZCPXA-jPE8nbXrLnePvecfej2rqL9LxFJyhaxdA");
            conn.setRequestProperty("roleId", "ADMIN");

            conn.setDoOutput(true);
            //conn.setDoInput(true);
            //conn.setUseCaches(false);

            StringBuilder postData = new StringBuilder();
            postData.append(payload);
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            conn.getOutputStream().write(postDataBytes);

            if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            //Console.print("CreateArrangement.invokeApi() ---> Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                //Console.print(output);
                response.append(output);
            }
            LOG.debug("ArrangementHelper.invokeApi() ---> Response .... "+response);

            br.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            LOG.debug("##### invokeApiUpdateAccounts() failed ",e);
        } catch (IOException e) {
            LOG.debug("##### invokeApiUpdateAccounts() failed ",e);
        }
        return response.toString();
    }

}
