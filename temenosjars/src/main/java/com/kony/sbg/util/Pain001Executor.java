package com.kony.sbg.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pain001Executor {
	
	private static final Logger logger = LogManager.getLogger(Pain001Executor.class);
	
	public static void main(String s[]) {
		String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>0QcviGid1fWsimq9goY0</MsgId><CreDtTm>2023-04-03T12:13:01.639</CreDtTm><NbOfTxs>1</NbOfTxs><InitgPty><Nm>Hamjad Hussain</Nm><Id><OrgId><Othr><Id>101077502</Id></Othr></OrgId></Id></InitgPty><InitnSrc><Nm>BOLP</Nm><Prvdr>Temenos Infinity</Prvdr></InitnSrc></GrpHdr><PmtInf><PmtInfId>WHEGPNLKDKT2P101</PmtInfId><PmtMtd>TRF</PmtMtd><ReqdExctnDt><Dt>2023-04-03</Dt></ReqdExctnDt><Dbtr><Nm>Hamjad Hussain</Nm><Id><OrgId><Othr><Id>101077502</Id></Othr></OrgId></Id><CtctDtls><Nm>Craig Tarr</Nm><EmailAdr>Craig.Tarr@standardbank.co.za</EmailAdr></CtctDtls></Dbtr><DbtrAcct><Id><Othr><Id>90775325</Id></Othr></Id><Ccy>USD</Ccy></DbtrAcct><DbtrAgt><FinInstnId><BICFI>SBZAZAJ0</BICFI></FinInstnId></DbtrAgt><CdtTrfTxInf><PmtId><InstrId>WHEGPNLKDKT2P101</InstrId><EndToEndId>WHEGPNLKDKT2P101</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"USD\">1.0</InstdAmt></Amt><ChrgBr>SHAR</ChrgBr><CdtrAgt><FinInstnId><BICFI>APPIUS50</BICFI><Nm>ALPINE BANK</Nm></FinInstnId></CdtrAgt><Cdtr><Nm>Nike Sports USA</Nm><PstlAdr><PstCd>29801</PstCd><TwnNm>Vienna</TwnNm><CtrySubDvsn>Washington</CtrySubDvsn><Ctry>US</Ctry><AdrLine>1220 Street</AdrLine><AdrLine>Nike Street</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><Othr><Id>9879879870</Id></Othr></Id></CdtrAcct><RmtInf><Ustrd>asdfasdf</Ustrd><Strd><AddtlRmtInf>asdf</AddtlRmtInf></Strd></RmtInf><SplmtryData><PlcAndNm>DocumentRequired</PlcAndNm><Envlp><any>Y</any></Envlp></SplmtryData><SplmtryData><PlcAndNm>DocumentCount</PlcAndNm><Envlp><any>0</any></Envlp></SplmtryData><SplmtryData><PlcAndNm>BOPDetails</PlcAndNm><Envlp><any</any></Envlp></SplmtryData></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>";
		String auth = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImRpZC0yIiwicGkuYXRtIjoieHkwaCJ9.eyJzY3AiOltdLCJjbGllbnRfaWQiOiJkYTNhM2FhNi05MWZkLTRmNjgtOGVkOC1hNWM2ODNlM2U3ZTMiLCJpc3MiOiJodHRwczovL2VudGVycHJpc2VzdHNzaXQuc3RhbmRhcmRiYW5rLmNvLnphIiwianRpIjoidHBHalFBSkdOc0sxS052cEV3eEtnTCIsInN1YiI6ImRhM2EzYWE2LTkxZmQtNGY2OC04ZWQ4LWE1YzY4M2UzZTdlMyIsIm5iZiI6MTY4MDUyNDc2OCwiaWQiOiJkYTNhM2FhNi05MWZkLTRmNjgtOGVkOC1hNWM2ODNlM2U3ZTMiLCJpYXQiOjE2ODA1MjQ3NjgsImV4cCI6MTY4MDUyODM2OH0.MajvLZew9MUGquYou-wEgfMIeuLD4lPgsAWoIw7KKNwfUXub1XW7EWT2VOl5kJAcTbJxlcG5lmkuj7jj-Wq_X3784kgpjX8aZrFgFEau0dMJgT6e20Zm6emXz_YBMCz9VoWgRSrgNGhVGvWLkrni0_gs1ty8jYrVpfXRUDdajqIrXWNHn4jSgaWII-G9j5LDU4HejXUtB5YwfDbb04Se9DO2fiJBN_5zD0RBYA7JrPYxPOSR3faCjyyAHLo5j2KiY2AVV_zEE1zZ5unwyXIjtp1Gjmo-y_7J8g5JTzJIYy5eW1Y1R6OrAI0O1CqveCMouGdGPWwKr0LE_P4RxcD6sA";
		
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put("Authorization", auth);
		requestHeaders.put("X-IBM-Client-Id", "c192fa96ca29d252b48d9ca27ec4a7ed");
		requestHeaders.put("X-IBM-Client-Secret", "971bd37cbec558ec49d6a83a8651f401");
		requestHeaders.put("x-fapi-interaction-id", "qwewqeqwewqqwewqewqe");
		
		String url = "https://api-gatewaynp.standardbank.co.za/npextorg/extnonprod/sit/qpay/payment/submissions/instruction/uploadraw";
		
		String res = executePain001Request(url, requestHeaders, payload);
		logger.debug(res);
	}
	
	public	static String executePain001Request(String painurl, Map<String, Object> requestHeaders, String payload) {
		try {
			
			String authVal = (String)requestHeaders.get("Authorization");
			logger.debug("executePain001Request ---> Authorization value : " + authVal);
			
			String clientID = (String)requestHeaders.get("X-IBM-Client-Id");
			String clientSecret = (String)requestHeaders.get("X-IBM-Client-Secret");
			logger.debug("executePain001Request ---> clientID: " + clientID + " clientSecret: " + clientSecret);
			
			URL url = new URL(painurl);
			logger.debug("executePain001Request ---> url: " + url);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "text/xml");
			
			conn.setRequestProperty("transacted", "false"); //TODO: value to be picked from runtime property
			conn.setRequestProperty("X-IBM-Client-Id", clientID);
			conn.setRequestProperty("X-IBM-Client-Secret", clientSecret);
			conn.setRequestProperty("x-fapi-interaction-id", (String)requestHeaders.get("x-fapi-interaction-id"));
			conn.setRequestProperty("Authorization", authVal);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			logger.debug("executePain001Request ---> post do output ");
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.debug("response:" + response.toString());
	
		} catch (MalformedURLException e) {
			logger.debug("executePain001Request ---> MalformedURLException :: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.debug("executePain001Request ---> IOException :: "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.debug("executePain001Request ---> Exception :: "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}	

	public	static String createPain001Request(Map<String, Object> params) {
		
		try {
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
			sb.append("<CstmrCdtTrfInitn>");
			sb.append(createGrpHdr(params));
			sb.append(createPmtInf(params));
			sb.append(createSplmtryData_Deals(params));
			sb.append("</CstmrCdtTrfInitn>");
			sb.append("</Document>");
			
			logger.debug("Pain001Executor --> Pain001 Request ::: "+sb.toString());
			
			//String req = Base64.getEncoder().encodeToString(sb.toString().getBytes());
			//logger.debug("Pain001Executor --> Pain001 Request encoded ::: "+req);
			
			return sb.toString();
		}catch(Exception e) {
			return e.getMessage();
		}
	}
	
	private	static String createGrpHdr(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<GrpHdr>");

		sb.append("<MsgId>"+params.get("msgId")+"</MsgId>");
		sb.append("<CreDtTm>"+params.get("createdDtTm")+"</CreDtTm>");
		sb.append("<NbOfTxs>1</NbOfTxs>");
		
		sb.append("<InitgPty>");
		sb.append("<Nm>"+params.get("customerName")+"</Nm>");
		sb.append("<Id><OrgId><Othr>");
		sb.append("<Id>"+params.get("customerId")+"</Id>");
		sb.append("</Othr></OrgId></Id>");
		sb.append("</InitgPty>");
		
		sb.append("<InitnSrc>");
		sb.append("<Nm>"+params.get("paymentName")+"</Nm>");
		sb.append("<Prvdr>"+params.get("paymentProvider")+"</Prvdr>");
		sb.append("</InitnSrc>");

		sb.append("</GrpHdr>");
		
		return sb.toString();
	}
	
	private	static String createPmtInf(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<PmtInf>");
		sb.append("<PmtInfId>"+params.get("confirmationNumber")+"</PmtInfId>");
		sb.append("<PmtMtd>TRF</PmtMtd>");
		sb.append("<ReqdExctnDt>");
		sb.append("<Dt>"+params.get("scheduledDate")+"</Dt>");
		sb.append("</ReqdExctnDt>");
		
		sb.append(createDbtr(params));
		sb.append(createDbtrAcct(params));
		sb.append(createDbtrAgt(params));
		sb.append(createCdtTrfTxInf(params));
		
		sb.append("</PmtInf>");
		
		return sb.toString();
	}
	
	private	static String createDbtr(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<Dbtr>");
		sb.append("<Nm>"+params.get("customerName")+"</Nm>");
		sb.append("<Id><OrgId><Othr>");
		sb.append("<Id>"+params.get("customerId")+"</Id>");
		sb.append("</Othr></OrgId></Id>");
		
		sb.append("<CtctDtls>");
		sb.append("<Nm>"+params.get("userName")+"</Nm>");
		sb.append("<EmailAdr>"+params.get("userMailId")+"</EmailAdr>");
		sb.append("</CtctDtls>");
		sb.append("</Dbtr>");

		return sb.toString();
	}

	private	static String createDbtrAcct(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<DbtrAcct>");
		sb.append("<Id><Othr>");
		sb.append("<Id>"+params.get("fromAccountNumber")+"</Id>");
		sb.append("</Othr></Id>");
		sb.append("<Ccy>"+params.get("fromAccountCurrency")+"</Ccy>");
		sb.append("</DbtrAcct>");

		return sb.toString();
	}

	private	static String createDbtrAgt(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<DbtrAgt>");
		sb.append("<FinInstnId>");
		sb.append("<BICFI>"+params.get("fromAccountswiftcode")+"</BICFI>");
		sb.append("</FinInstnId>");
		sb.append("</DbtrAgt>");

		return sb.toString();
	}
	
	private	static String createCdtTrfTxInf(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<CdtTrfTxInf>");
		sb.append("<PmtId>");
		sb.append("<InstrId>"+params.get("confirmationNumber")+"</InstrId>");
		sb.append("<EndToEndId>"+params.get("confirmationNumber")+"</EndToEndId>");
		sb.append("</PmtId>");
		
		String transCurr = (String)params.get("transactionCurrency");
		
		sb.append("<Amt>");
		sb.append("<InstdAmt Ccy=\""+transCurr+"\">"+params.get("transactionAmount")+"</InstdAmt>");
		sb.append("</Amt>");
		
		sb.append(createXchgRateInf(params));
		
		sb.append("<ChrgBr>"+params.get("paidBy")+"</ChrgBr>");
		sb.append(createCdtrAgt(params));
		sb.append(createCdtr(params));
		sb.append(createCdtrAcct(params));
		sb.append(createPurp(params));
		sb.append(createRgltryRptg(params));
		sb.append(createRmtInf(params));
		sb.append(createSplmtryData(params));
		
		sb.append("</CdtTrfTxInf>");

		return sb.toString();
	}
	
	private	static String createXchgRateInf(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		Object exchangeRate 	= (String)params.get("exchangeRate");
		Object fxCoverNumber	= (String)params.get("fxCoverNumber");
		boolean bool = !SBGCommonUtils.isStringEmpty(exchangeRate) && SBGCommonUtils.isStringEmpty(fxCoverNumber);
		
		if(bool) {
			sb.append("<XchgRateInf>");
			sb.append("<XchgRate>"+exchangeRate+"</XchgRate>");
			sb.append("<CtrctId>"+fxCoverNumber+"</CtrctId>");
			sb.append("</XchgRateInf>");
		}

		return sb.toString();
	}

	private	static String createCdtrAgt(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<CdtrAgt>");
		sb.append("<FinInstnId>");
		sb.append("<BICFI>"+params.get("beneSwiftCode")+"</BICFI>");
		sb.append("<Nm>"+params.get("beneBankName")+"</Nm>");
		sb.append("</FinInstnId>");

		//TODO: #if ($routingNumber != '')
		Object routingNumber 	= (String)params.get("routingNumber");
		boolean bool = !SBGCommonUtils.isStringEmpty(routingNumber);
		
		if(bool) {
			sb.append("<BrnchId>");
			sb.append("<Id>"+routingNumber+"</Id>");
			sb.append("</BrnchId>");
		}
		sb.append("</CdtrAgt>");
		
		return sb.toString();
	}

	private	static String createCdtr(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<Cdtr>");
		sb.append("<Nm>"+params.get("beneficiaryName")+"</Nm>");
		sb.append("<PstlAdr>");
		sb.append("<PstCd>"+params.get("beneZipCode")+"</PstCd>");
		sb.append("<TwnNm>"+params.get("beneCity")+"</TwnNm>");
		sb.append("<CtrySubDvsn>"+params.get("beneState")+"</CtrySubDvsn>");
		sb.append("<Ctry>"+params.get("beneCountryName")+"</Ctry>");
		sb.append("<AdrLine>"+params.get("beneAddressLine1")+"</AdrLine>");

		//TODO: #if ($beneAddressLine2 != '')
		Object beneAddressLine2 	= (String)params.get("beneAddressLine2");
		boolean bool = !SBGCommonUtils.isStringEmpty(beneAddressLine2);
		
		if(bool) {
			sb.append("<AdrLine>"+beneAddressLine2+"</AdrLine>");
		}
		
		sb.append("</PstlAdr>");
		sb.append("</Cdtr>");
		
		return sb.toString();
	}

	private	static String createCdtrAcct(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<CdtrAcct>");
		sb.append("<Id>");

		//TODO: #if ($iban != '')
		Object iban	= (String)params.get("iban");
		boolean bool1 = !SBGCommonUtils.isStringEmpty(iban);
		
		if(bool1) {
			sb.append("<IBAN>"+iban+"</IBAN>");
		}

		//TODO: #if ($toAccountNumber != '')
		Object toAccountNumber	= (String)params.get("toAccountNumber");
		boolean bool2 = !SBGCommonUtils.isStringEmpty(toAccountNumber);
		
		if(bool2) {
			sb.append("<Othr>");
			sb.append("<Id>"+toAccountNumber+"</Id>");
			sb.append("</Othr>");
		}
		
		sb.append("</Id>");
		sb.append("</CdtrAcct>");
		
		return sb.toString();
	}

	private	static String createPurp(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		//TODO: #if ($purposeCode != '')
		Object purposeCode	= (String)params.get("purposeCode");
		boolean bool = !SBGCommonUtils.isStringEmpty(purposeCode);
		
		if(bool) {
			sb.append("<Purp>");
			sb.append("<Prtry>"+purposeCode+"</Prtry>");
			sb.append("</Purp>");
		}

		return sb.toString();
	}

	private	static String createRgltryRptg(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		//TODO: #if ($complianceType != '')
		Object complianceType	= (String)params.get("complianceType");
		boolean bool = !SBGCommonUtils.isStringEmpty(complianceType);
		
		if(bool) {
			sb.append("<RgltryRptg>");
			sb.append("<Authrty>");
			sb.append("<Nm>Compliance</Nm>");
			sb.append("</Authrty>");
			sb.append("<Dtls>");
			sb.append("<Tp>"+complianceType+"</Tp>");
			sb.append("</Dtls>");
			sb.append("</RgltryRptg>");
		}

		return sb.toString();
	}

	private	static String createRmtInf(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<RmtInf>");
		sb.append("<Ustrd>"+params.get("beneficiaryRefCode")+"</Ustrd>");
		sb.append("<Strd>");
		sb.append("<AddtlRmtInf>"+params.get("statementRefCode")+"</AddtlRmtInf>");
		sb.append("</Strd>");
		sb.append("</RmtInf>");

		return sb.toString();
	}

	private	static String createSplmtryData(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		Object isDocumentRequired	= (String)params.get("isDocumentRequired");

		sb.append("<SplmtryData>");
		sb.append("<PlcAndNm>DocumentRequired</PlcAndNm>");
		sb.append("<Envlp>");
		sb.append("<any>"+isDocumentRequired+"</any>");
		sb.append("</Envlp>");
		sb.append("</SplmtryData>");
		
		//TODO: #if ($isDocumentRequired == 'Y')
		boolean bool1 = !SBGCommonUtils.isStringEmpty(isDocumentRequired) && isDocumentRequired.equals("Y");
		if(bool1) {
			sb.append("<SplmtryData>");
			sb.append("<PlcAndNm>DocumentCount</PlcAndNm>");
			sb.append("<Envlp>");
			sb.append("<any>"+params.get("documentCount")+"</any>");
			sb.append("</Envlp>");
			sb.append("</SplmtryData>");
		}

		//TODO: #if ($bopDetails != '')
		Object bopDetails	= params.get("bopDetails");
		boolean bool2 = !SBGCommonUtils.isStringEmpty(bopDetails);
		if(bool2) {
			sb.append("<SplmtryData>");
			sb.append("<PlcAndNm>BOPDetails</PlcAndNm>");
			sb.append("<Envlp>");
			sb.append("<any>"+bopDetails+"</any>");
			sb.append("</Envlp>");
			sb.append("</SplmtryData>");
		}

		return sb.toString();
	}

	private	static String createSplmtryData_Deals(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		
		//TODO: #if ($multipleDealsDetails != '')
		Object multipleDealsDetails	= params.get("multipleDealsDetails");
		boolean bool1 = !SBGCommonUtils.isStringEmpty(multipleDealsDetails);
		if(bool1) {
			sb.append("<SplmtryData>");
			sb.append("<PlcAndNm>MultipleDeals</PlcAndNm>");
			sb.append("<Envlp>");
			sb.append("<any>"+multipleDealsDetails+"</any>");
			sb.append("</Envlp>");
			sb.append("</SplmtryData>");
		}

		return sb.toString();
	}
}
