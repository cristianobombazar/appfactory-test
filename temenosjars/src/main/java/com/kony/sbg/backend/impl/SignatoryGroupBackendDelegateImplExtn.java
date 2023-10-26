package com.kony.sbg.backend.impl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;
import com.temenos.dbx.product.signatorygroupservices.backenddelegate.impl.SignatoryGroupBackendDelegateImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SignatoryGroupBackendDelegateImplExtn  extends SignatoryGroupBackendDelegateImpl{
	@Override
    public List<SignatoryGroupDTO> getSignatoryUsers(String coreCustomerId, String contractId, Map<String, Object> headersMap, DataControllerRequest request){
        List<SignatoryGroupDTO> signatoryGroupDTO = null;
        try {
//
            JSONArray signatoryUsers = new JSONArray();
            JSONArray signatoryUsersArray = fetchSignatoryEligibleUsers(coreCustomerId, contractId);
            if (signatoryUsersArray.length() > 0) {
                for (int i = 0; i < signatoryUsersArray.length(); i++) {
                    JSONObject userDetail = new JSONObject();
                    JSONObject jsonObject2 = signatoryUsersArray.getJSONObject(i);
                    String Name= (jsonObject2.get("lastName")!=null) ? jsonObject2.get("firstName").toString().trim()+" "+jsonObject2.get("lastName").toString().trim():
                            jsonObject2.get("firstName").toString().trim();
                    String Id = jsonObject2.get("userId").toString();
                    String UserName = jsonObject2.get("userName").toString();
                    String role = jsonObject2.get("role").toString();
                    userDetail.put("userId", Id);
                    userDetail.put("userName", UserName);
                    userDetail.put("fullName", Name);
                    userDetail.put("role", role);
                    signatoryUsers.put(userDetail);
                }
            }
            signatoryGroupDTO = JSONUtils.parseAsList(signatoryUsers.toString(), SignatoryGroupDTO.class);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return signatoryGroupDTO;
    }
    private JSONArray fetchSignatoryEligibleUsers(String coreCustomerId, String contractId){
        JSONArray responseArr = new JSONArray();
        try{
            Map<String, Object> reqParams = new HashMap<>();
            reqParams.put("_coreCustomerId", coreCustomerId);
            reqParams.put("_contractId", contractId);
            String response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(ServiceId.DBPRBLOCALSERVICEDB)
                    .withObjectId(null)
             //       .withOperationId(OperationName.DB_FETCH_SIGNATORY_ELIGIBLE_USERS_PROC)
                    .withOperationId("dbxdb_fetch_signatory_eligible_users_proc")
                    .withRequestParameters(reqParams)
                    .withRequestHeaders(null)
                    .build().getResponse();
            JSONObject responseObj = new JSONObject(response);
            if(responseObj.has("opstatus") && responseObj.getInt("opstatus") == 0){
                if(responseObj.has(Constants.RECORDS)) {
                    responseArr = responseObj.getJSONArray(Constants.RECORDS);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return responseArr;
    }


}
