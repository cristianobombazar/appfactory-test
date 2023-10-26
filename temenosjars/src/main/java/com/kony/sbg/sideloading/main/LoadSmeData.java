package com.kony.sbg.sideloading.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kony.sbg.sideloading.models.Accounts;
import com.kony.sbg.sideloading.models.Contract;
import com.kony.sbg.sideloading.models.Customer;
import com.kony.sbg.sideloading.models.UserContracts;
import com.kony.sbg.sideloading.models.UserDetails;
import com.kony.sbg.sideloading.network.ArrangementHelper;
import com.kony.sbg.sideloading.network.CreateDigitalProfile;
import com.kony.sbg.sideloading.network.PartyHelper;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.sideloading.utils.Console;

public class LoadSmeData {

	private	static final String	ENV_INT_DEV		= "INT.DEV";
	private	static final String	ENV_SBG_DEV		= "SBG.DEV";
	private	static final String	ENV_SBG_SIT		= "SBG.SIT";
	private	static final String	ENV_SBG_UAT		= "SBG.UAT";
	private	static final String	ENV_SBG_TRNG	= "SBG.TRNG";
	private	static final String	ENV_SBG_PROD	= "SBG.PROD";
	private	static final String	ENV_SBG_PREPROD	= "SBG.PREPROD";

	public	static final int	RETRY_ATTEMPTS	= 5;
	private	static String		ENVIRONMENT		= "";

	//private	static String 		CSV_FILE_PATH 	= "D:\\Kony\\SBG\\SideLoading\\data\\PROD\\18Nov2022";
	private	static String 		CSV_FILE_PATH 	= "";

	public	static final String COMPANY_ID		= "GB0010001";
	public	static String DELIMITER		= ";";

	public	static String DOMAIN	= "";

	public	static Map<String, Contract> contractMap 			= null;
	public	static Map<String, Customer> customersMap 			= null;
	public 	static List<Accounts> AccountsMap 					= null;
	public 	static List<UserDetails> userDetailsMap 			= null;
	public 	static List<UserContracts> userContractsMap 		= null;
	//public 	static List<DesignatedContact> designatedUsersMap	= null;

	private	static void setENV() {
		if(ENVIRONMENT.equals(ENV_INT_DEV)) {
			DOMAIN = "https://sbg-dev.temenos-cloud.net:443";
			PartyHelper.PARTY_AUTH_KEY = "cqa4aFqBCaaagll9VtU58b0AXf1bKTG5E2zEXHe8";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "LnEBgDljTC3UARLnCmhwo3nmxHqE4fUy56xVWSsC";
		} else if(ENVIRONMENT.equals(ENV_SBG_DEV)) {
			DOMAIN = "https://standardbankdev.temenos-cloud.net";
			PartyHelper.PARTY_AUTH_KEY = "plPksJ8hmY9XGMLmt9PKS7BHS9i16uQQ8VV2mjOB";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "tPir3u3P1h13gMBbBe0WR9SA78SXC3Gf8LFzZlUg";
		} else if(ENVIRONMENT.equals(ENV_SBG_SIT)) {
			DOMAIN = "https://standardbanksit.temenos-cloud.net";
			PartyHelper.PARTY_AUTH_KEY = "5718pvLdQi22qPM03WLFB6JWJdKRD6ig7Br7qysV";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "G23yyka8LC4i7lCLrj01B6scyGyU0q242SPYIzZx";
		} else if(ENVIRONMENT.equals(ENV_SBG_TRNG)) {
			DOMAIN = "https://standardbanktrng.temenos-cloud.net";
			PartyHelper.PARTY_AUTH_KEY = "w8ClaZlgUw1D4xo4kfO2D24AGQA4dOCz1lCEKeZH";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "sug158CCpz32SlKFwvcZY8Ny5gJTsMrk9Kwemtol";
		} else if(ENVIRONMENT.equals(ENV_SBG_UAT)) {
			DOMAIN = "https://standardbankuat.temenos-cloud.net";
			PartyHelper.PARTY_AUTH_KEY = "Y69TLeQ6Uv4qHRpHbs76D6u0cr1xRJcD8VfnvnQL";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "EMR0bqqzzA60rJewLLsku6rqoem594WS1ux0PFGK";
		} else if(ENVIRONMENT.equals(ENV_SBG_PROD)) {
			DOMAIN = "https://standardbank.temenos-cloud.net";
			PartyHelper.PARTY_AUTH_KEY = "BRrlisih5b5kAon3p8zDM67jeSp9LkRs3ffEmYh6";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "CqP6w2CwTp188VqvZBpu8aZ3SPUtyXUs6UuwWNBs";
		} else if(ENVIRONMENT.equals(ENV_SBG_PREPROD)) {
			DOMAIN = "https://standardbankpreprod.temenos-cloud.net/";
			PartyHelper.PARTY_AUTH_KEY = "AJlCfCvE8A4cVzK9EbpgU7Ct41bLKiad1oZvBFFD";
			ArrangementHelper.ARRANGEMENT_AUTH_KEY = "ahozh89Llu69RsWoMdCLH3Cs3MpYSMlY6HHz8Im9";
		} else {
			printError();
		}
	}

	public static void main(String[] args) {

		Console.print("==============STARTED @ "+new Date());

		if(args.length != 3) {
			printError();
		} else {
			ENVIRONMENT 	= args[0].trim();
			CSV_FILE_PATH	= args[1].trim();
			DELIMITER		= args[2].trim();

			setENV();
			startProcess();
		}
	}

	private	static void printError() {
		Console.print("==============INCORRECT EXECUTION FOR SIDELOADING THE DATA=================");
		Console.print("==============USE THE FORMAT AS EXPLAINED BELOW=================");
		Console.print();
		Console.print("===== Windows =====");
		Console.print("java -classpath json-20180813.jar;json-simple-1.1.jar;temenosjars-0.0.1-SNAPSHOT.jar com.kony.sbg.sideloading.main.LoadSmeData <ENVIRONMENT> <FOLDER_PATH> <DELIMITER>");
		Console.print("POSSIBLE VALUES FOR ENVIRONMENT ARE INT.DEV, SBG.DEV, SBG.SIT, SBG.UAT, SBG.TRNG, SBG.PROD");
		Console.print("===== Mac / Linux =====");
		Console.print("java -classpath json-20180813.jar:json-simple-1.1.jar:temenosjars-0.0.1-SNAPSHOT.jar com.kony.sbg.sideloading.main.LoadSmeData \"<ENVIRONMENT>\" \"<FOLDER_PATH>\" \"<DELIMITER>\"");
		Console.print("POSSIBLE VALUES FOR ENVIRONMENT ARE INT.DEV, SBG.DEV, SBG.SIT, SBG.UAT, SBG.TRNG, SBG.PROD");
		Console.print();

		Console.print("========SAMPLE RUN AS BELOW=========");
		Console.print();
		Console.print("===== Windows =====");
		Console.print("java -classpath json-20180813.jar;json-simple-1.1.jar;temenosjars-0.0.1-SNAPSHOT.jar com.kony.sbg.sideloading.main.LoadSmeData SBG.DEV D:\\files ;");
		Console.print("===== Mac / Linux =====");
		Console.print("java -classpath json-20180813.jar:json-simple-1.1.jar:temenosjars-0.0.1-SNAPSHOT.jar com.kony.sbg.sideloading.main.LoadSmeData \"SBG.DEV\" \"/Users/files\" \";\"");
		Console.print();
	}

	private	static void startProcess() {

		Console.print("==============ENVIRONMENT DETAILS=================");

		Console.print("ENVIRONMENT: "+ENVIRONMENT);
		Console.print("DOMAIN: "+DOMAIN);
		Console.print("PARTY_AUTH_KEY: "+PartyHelper.PARTY_AUTH_KEY);
		Console.print("ARRANGEMENT_AUTH_KEY: "+ArrangementHelper.ARRANGEMENT_AUTH_KEY);
		Console.print("DELIMITER Used is: "+DELIMITER+" ### CSV_FILE_PATH: "+CSV_FILE_PATH);
		Console.print();

		Console.print("==============Loading Files=================");

		LoadSmeData obj = new LoadSmeData();
		boolean validFiles	= obj.loadCsvFiles();
		Console.print("validFiles: "+validFiles);
		if(!validFiles) {
			Console.print("EXITING THE PROCESS AS THE FILES ARE NOT PROPER");
			return;
		}

		boolean validData = obj.verifyfiles();
		Console.print("validData: "+validData);
		if(!validData) {
			Console.print("EXITING THE PROCESS AS THE DATA IS INCORRECT");
			return;
		}

		Console.print("==============START @ "+new Date());
		{
			//THIS BLOCK IS TO RUN COMPLETE SIDELOADING INCLUDING CREATING PARTY, ARRANGEMENT, CONTRACT & USERS
			obj.processData_New();
		}

		{
			//THIS BLOCK IS TO TEST ARRANGEMENT MS
			//obj.testArrangmentCreation();
			//String partyId 		= "2229150810";
			//String coreCustId 	= "505011790";	//required for jar issue
			//int count = obj.processArrangementData(partyId, coreCustId);
			//Console.print("==============COMPLETED Loading "+count+" accounts @ "+new Date());
		}

		{
			//THIS BLOCK IS TO SIDELOADING CONTRACT & USERS FOR GIVEN PARTYID AND CORECUSTOMERID
//			String partyId 		= "2232247334";
//			String coreCustId 	= "100061496";	//required for jar issue
//			JSONObject contractResponse = obj.testUserCreation(partyId, coreCustId);
//			obj.testOlbUserCreation(contractResponse, partyId, coreCustId);
		}

		{
			//THIS BLOCK IS TO SIDELOADING USERS FOR GIVEN PARTYID, CORECUSTOMERID AND CONTRACT PAYLOAD
			//String partyId 		= "2230552468";
			//String coreCustId 	= "1111118200";	//required for jar issue
			//String contractResponse = "{\"encContractPayload\":\"eyJjb3JlQ3VzdG9tZXJJZEZyb21QYXJ0eSI6IjEwMDA3NjUwMiIsImFkZHJlc3MiOiJbe1wiYWRkcmVzc0xpbmUxXCI6bnVsbCxcImFkZHJlc3NsaW5lMlwiOlwiU3RhbmRhcmQgQmFuayBDZW50cmVcIixcImFkZHJlc3NsaW5lM1wiOlwiNSBTaW1tb25kcyBTdHJlZXRcIixcImNpdHlOYW1lXCI6bnVsbCxcInN0YXRlXCI6XCJHYXV0ZW5nXCIsXCJjb3VudHJ5XCI6XCJaQVwiLFwiemlwQ29kZVwiOm51bGwsXCJpc1ByZWZlcnJlZEFkZHJlc3NcIjpcInRydWVcIn1dIiwic2VydmljZURlZmluaXRpb25OYW1lIjoiU01FIG9ubGluZSBiYW5raW5nIiwiaXNEZWZhdWx0QWN0aW9uc0VuYWJsZWQiOiJ0cnVlIiwiYXV0aG9yaXplZFNpZ25hdG9yeSI6Ilt7XCJGaXJzdE5hbWVcIjpcIkNyYWlnXCIsXCJMYXN0TmFtZVwiOlwiVGFyclwiLFwiRGF0ZU9mQmlydGhcIjpcIjIwMDAtMDEtMDFcIixcIlNzblwiOlwiXCJ9XSIsImF1dGhvcml6ZWRTaWduYXRvcnlSb2xlcyI6Ilt7XCJhdXRob3JpemVkU2lnbmF0b3J5Um9sZUlkXCI6XCJHUk9VUF9BRE1JTklTVFJBVE9SXCIsXCJjb3JlQ3VzdG9tZXJJZFwiOlwiMTAwMDc2NTAyXCJ9XSIsInNlcnZpY2VEZWZpbml0aW9uSWQiOiI3MDdkZmVhOC1kMGZlLTQxNTQtODljMy1lN2Q3ZWYyZWUxNmEiLCJjb250cmFjdE5hbWUiOiJUaGUgU3RhbmRhcmQgQmFuayBvZiBTb3V0aCBBZnJpY2EgTHRkLiIsImNvbnRyYWN0Q3VzdG9tZXJzIjoiW3tcImlzQnVzaW5lc3NcIjp0cnVlLFwiY29yZUN1c3RvbWVyTmFtZVwiOlwiVGhlIFN0YW5kYXJkIEJhbmsgb2YgU291dGggQWZyaWNhIEx0ZC5cIixcImlzUHJpbWFyeVwiOlwidHJ1ZVwiLFwiY29yZUN1c3RvbWVySWRcIjpcIjEwMDA3NjUwMlwiLFwiYWNjb3VudHNcIjpbe1wiYWNjb3VudE5hbWVcIjpcIkN1cnJlbnQgQWNjb3VudFwiLFwiYWNjb3VudElkXCI6XCI5MDc3NTMyNVwiLFwiYWNjb3VudFR5cGVcIjpcIkN1cnJlbnRcIixcImFjY291bnRIb2xkZXJOYW1lXCI6XCJ7XFxcInVzZXJuYW1lXFxcIjpcXFwiXFxcIixcXFwiZnVsbG5hbWVcXFwiOlxcXCJcXFwifVwiLFwibWVtYmVyc2hpcElkXCI6XCIxMDAwNzY1MDJcIixcInR5cGVJZFwiOlwiN1wiLFwidGF4SWRcIjpudWxsLFwibWVtYmVyc2hpcE5hbWVcIjpudWxsLFwiYXJyYW5nZW1lbnRJZFwiOlwiQVJSMjIzMDAyQzFCRjJKN08wXCIsXCJvd25lcnNoaXBcIjpcIk93bmVyXCIsXCJhY2NvdW50U3RhdHVzXCI6XCJBQ1RJVkVcIixcIm1lbWJlcnNoaXBvd25lckRUT1wiOm51bGwsXCJpc0Fzc29jaWF0ZWRcIjpcImZhbHNlXCIsXCJpc1NlbGVjdGVkXCI6XCJmYWxzZVwiLFwiaXNOZXdcIjpcInRydWVcIixcIm93bmVyVHlwZVwiOlwiT3duZXJcIn0se1wiYWNjb3VudE5hbWVcIjpcIkN1cnJlbnQgQWNjb3VudFwiLFwiYWNjb3VudElkXCI6XCI5MDc3NTI4N1wiLFwiYWNjb3VudFR5cGVcIjpcIkN1cnJlbnRcIixcImFjY291bnRIb2xkZXJOYW1lXCI6XCJ7XFxcInVzZXJuYW1lXFxcIjpcXFwiXFxcIixcXFwiZnVsbG5hbWVcXFwiOlxcXCJcXFwifVwiLFwibWVtYmVyc2hpcElkXCI6XCIxMDAwNzY1MDJcIixcInR5cGVJZFwiOlwiN1wiLFwidGF4SWRcIjpudWxsLFwibWVtYmVyc2hpcE5hbWVcIjpudWxsLFwiYXJyYW5nZW1lbnRJZFwiOlwiQVJSMjIzMDAyRzMzNVpEUjdWXCIsXCJvd25lcnNoaXBcIjpcIk93bmVyXCIsXCJhY2NvdW50U3RhdHVzXCI6XCJBQ1RJVkVcIixcIm1lbWJlcnNoaXBvd25lckRUT1wiOm51bGwsXCJpc0Fzc29jaWF0ZWRcIjpcImZhbHNlXCIsXCJpc1NlbGVjdGVkXCI6XCJmYWxzZVwiLFwiaXNOZXdcIjpcInRydWVcIixcIm93bmVyVHlwZVwiOlwiT3duZXJcIn0se1wiYWNjb3VudE5hbWVcIjpcIkN1cnJlbnQgQWNjb3VudFwiLFwiYWNjb3VudElkXCI6XCI5MDM5MDUxMlwiLFwiYWNjb3VudFR5cGVcIjpcIkN1cnJlbnRcIixcImFjY291bnRIb2xkZXJOYW1lXCI6XCJ7XFxcInVzZXJuYW1lXFxcIjpcXFwiXFxcIixcXFwiZnVsbG5hbWVcXFwiOlxcXCJcXFwifVwiLFwibWVtYmVyc2hpcElkXCI6XCIxMDAwNzY1MDJcIixcInR5cGVJZFwiOlwiN1wiLFwidGF4SWRcIjpudWxsLFwibWVtYmVyc2hpcE5hbWVcIjpudWxsLFwiYXJyYW5nZW1lbnRJZFwiOlwiQVJSMjIzMDAyVzE5M0tMRlNGXCIsXCJvd25lcnNoaXBcIjpcIk93bmVyXCIsXCJhY2NvdW50U3RhdHVzXCI6XCJBQ1RJVkVcIixcIm1lbWJlcnNoaXBvd25lckRUT1wiOm51bGwsXCJpc0Fzc29jaWF0ZWRcIjpcImZhbHNlXCIsXCJpc1NlbGVjdGVkXCI6XCJmYWxzZVwiLFwiaXNOZXdcIjpcInRydWVcIixcIm93bmVyVHlwZVwiOlwiT3duZXJcIn0se1wiYWNjb3VudE5hbWVcIjpcIkN1cnJlbnQgQWNjb3VudFwiLFwiYWNjb3VudElkXCI6XCI5MDQ3NzkwMVwiLFwiYWNjb3VudFR5cGVcIjpcIkN1cnJlbnRcIixcImFjY291bnRIb2xkZXJOYW1lXCI6XCJ7XFxcInVzZXJuYW1lXFxcIjpcXFwiXFxcIixcXFwiZnVsbG5hbWVcXFwiOlxcXCJcXFwifVwiLFwibWVtYmVyc2hpcElkXCI6XCIxMDAwNzY1MDJcIixcInR5cGVJZFwiOlwiN1wiLFwidGF4SWRcIjpudWxsLFwibWVtYmVyc2hpcE5hbWVcIjpudWxsLFwiYXJyYW5nZW1lbnRJZFwiOlwiQVJSMjIzMDAzQzM3Q1FPQUhHXCIsXCJvd25lcnNoaXBcIjpcIk93bmVyXCIsXCJhY2NvdW50U3RhdHVzXCI6XCJBQ1RJVkVcIixcIm1lbWJlcnNoaXBvd25lckRUT1wiOm51bGwsXCJpc0Fzc29jaWF0ZWRcIjpcImZhbHNlXCIsXCJpc1NlbGVjdGVkXCI6XCJmYWxzZVwiLFwiaXNOZXdcIjpcInRydWVcIixcIm93bmVyVHlwZVwiOlwiT3duZXJcIn0se1wiYWNjb3VudE5hbWVcIjpcIkN1cnJlbnQgQWNjb3VudFwiLFwiYWNjb3VudElkXCI6XCIxMjk0MjEwXCIsXCJhY2NvdW50VHlwZVwiOlwiQ3VycmVudFwiLFwiYWNjb3VudEhvbGRlck5hbWVcIjpcIntcXFwidXNlcm5hbWVcXFwiOlxcXCJcXFwiLFxcXCJmdWxsbmFtZVxcXCI6XFxcIlxcXCJ9XCIsXCJtZW1iZXJzaGlwSWRcIjpcIjEwMDA3NjUwMlwiLFwidHlwZUlkXCI6XCI3XCIsXCJ0YXhJZFwiOm51bGwsXCJtZW1iZXJzaGlwTmFtZVwiOm51bGwsXCJhcnJhbmdlbWVudElkXCI6XCJBUlIyMjMwMENPNjU2N0xNMFVcIixcIm93bmVyc2hpcFwiOlwiT3duZXJcIixcImFjY291bnRTdGF0dXNcIjpcIkFDVElWRVwiLFwibWVtYmVyc2hpcG93bmVyRFRPXCI6bnVsbCxcImlzQXNzb2NpYXRlZFwiOlwiZmFsc2VcIixcImlzU2VsZWN0ZWRcIjpcImZhbHNlXCIsXCJpc05ld1wiOlwidHJ1ZVwiLFwib3duZXJUeXBlXCI6XCJPd25lclwifSx7XCJhY2NvdW50TmFtZVwiOlwiQ3VycmVudCBBY2NvdW50XCIsXCJhY2NvdW50SWRcIjpcIjkwNzc1MzA5XCIsXCJhY2NvdW50VHlwZVwiOlwiQ3VycmVudFwiLFwiYWNjb3VudEhvbGRlck5hbWVcIjpcIntcXFwidXNlcm5hbWVcXFwiOlxcXCJcXFwiLFxcXCJmdWxsbmFtZVxcXCI6XFxcIlxcXCJ9XCIsXCJtZW1iZXJzaGlwSWRcIjpcIjEwMDA3NjUwMlwiLFwidHlwZUlkXCI6XCI3XCIsXCJ0YXhJZFwiOm51bGwsXCJtZW1iZXJzaGlwTmFtZVwiOm51bGwsXCJhcnJhbmdlbWVudElkXCI6XCJBUlIyMjMwMEQyMkZGRFVVTTBcIixcIm93bmVyc2hpcFwiOlwiT3duZXJcIixcImFjY291bnRTdGF0dXNcIjpcIkFDVElWRVwiLFwibWVtYmVyc2hpcG93bmVyRFRPXCI6bnVsbCxcImlzQXNzb2NpYXRlZFwiOlwiZmFsc2VcIixcImlzU2VsZWN0ZWRcIjpcImZhbHNlXCIsXCJpc05ld1wiOlwidHJ1ZVwiLFwib3duZXJUeXBlXCI6XCJPd25lclwifSx7XCJhY2NvdW50TmFtZVwiOlwiQ3VycmVudCBBY2NvdW50XCIsXCJhY2NvdW50SWRcIjpcIjkwNzc1Mjk1XCIsXCJhY2NvdW50VHlwZVwiOlwiQ3VycmVudFwiLFwiYWNjb3VudEhvbGRlck5hbWVcIjpcIntcXFwidXNlcm5hbWVcXFwiOlxcXCJcXFwiLFxcXCJmdWxsbmFtZVxcXCI6XFxcIlxcXCJ9XCIsXCJtZW1iZXJzaGlwSWRcIjpcIjEwMDA3NjUwMlwiLFwidHlwZUlkXCI6XCI3XCIsXCJ0YXhJZFwiOm51bGwsXCJtZW1iZXJzaGlwTmFtZVwiOm51bGwsXCJhcnJhbmdlbWVudElkXCI6XCJBUlIyMjMwMEtFMUVCSEI4MVhcIixcIm93bmVyc2hpcFwiOlwiT3duZXJcIixcImFjY291bnRTdGF0dXNcIjpcIkFDVElWRVwiLFwibWVtYmVyc2hpcG93bmVyRFRPXCI6bnVsbCxcImlzQXNzb2NpYXRlZFwiOlwiZmFsc2VcIixcImlzU2VsZWN0ZWRcIjpcImZhbHNlXCIsXCJpc05ld1wiOlwidHJ1ZVwiLFwib3duZXJUeXBlXCI6XCJPd25lclwifSx7XCJhY2NvdW50TmFtZVwiOlwiQ3VycmVudCBBY2NvdW50XCIsXCJhY2NvdW50SWRcIjpcIjIxNjgxNDgxXCIsXCJhY2NvdW50VHlwZVwiOlwiQ3VycmVudFwiLFwiYWNjb3VudEhvbGRlck5hbWVcIjpcIntcXFwidXNlcm5hbWVcXFwiOlxcXCJcXFwiLFxcXCJmdWxsbmFtZVxcXCI6XFxcIlxcXCJ9XCIsXCJtZW1iZXJzaGlwSWRcIjpcIjEwMDA3NjUwMlwiLFwidHlwZUlkXCI6XCI3XCIsXCJ0YXhJZFwiOm51bGwsXCJtZW1iZXJzaGlwTmFtZVwiOm51bGwsXCJhcnJhbmdlbWVudElkXCI6XCJBUlIyMjMwME5OMzM1MjFDMk1cIixcIm93bmVyc2hpcFwiOlwiT3duZXJcIixcImFjY291bnRTdGF0dXNcIjpcIkFDVElWRVwiLFwibWVtYmVyc2hpcG93bmVyRFRPXCI6bnVsbCxcImlzQXNzb2NpYXRlZFwiOlwiZmFsc2VcIixcImlzU2VsZWN0ZWRcIjpcImZhbHNlXCIsXCJpc05ld1wiOlwidHJ1ZVwiLFwib3duZXJUeXBlXCI6XCJPd25lclwifSx7XCJhY2NvdW50TmFtZVwiOlwiQ3VycmVudCBBY2NvdW50XCIsXCJhY2NvdW50SWRcIjpcIjkwNzAyNTMwXCIsXCJhY2NvdW50VHlwZVwiOlwiQ3VycmVudFwiLFwiYWNjb3VudEhvbGRlck5hbWVcIjpcIntcXFwidXNlcm5hbWVcXFwiOlxcXCJcXFwiLFxcXCJmdWxsbmFtZVxcXCI6XFxcIlxcXCJ9XCIsXCJtZW1iZXJzaGlwSWRcIjpcIjEwMDA3NjUwMlwiLFwidHlwZUlkXCI6XCI3XCIsXCJ0YXhJZFwiOm51bGwsXCJtZW1iZXJzaGlwTmFtZVwiOm51bGwsXCJhcnJhbmdlbWVudElkXCI6XCJBUlIyMjMwMFoxSEVGUlM5MDVcIixcIm93bmVyc2hpcFwiOlwiT3duZXJcIixcImFjY291bnRTdGF0dXNcIjpcIkFDVElWRVwiLFwibWVtYmVyc2hpcG93bmVyRFRPXCI6bnVsbCxcImlzQXNzb2NpYXRlZFwiOlwiZmFsc2VcIixcImlzU2VsZWN0ZWRcIjpcImZhbHNlXCIsXCJpc05ld1wiOlwidHJ1ZVwiLFwib3duZXJUeXBlXCI6XCJPd25lclwifV0sXCJmZWF0dXJlc1wiOlt7XCJmZWF0dXJlSWRcIjpcIkFDQ0VTU19DQVNIX1BPU0lUSU9OXCJ9LHtcImZlYXR1cmVJZFwiOlwiQUNDRVNTX0VOR0FHRVwifSx7XCJmZWF0dXJlSWRcIjpcIkFDQ09VTlRfQUdHUkVHQVRJT05cIn0se1wiZmVhdHVyZUlkXCI6XCJBQ0NPVU5UX1NFVFRJTkdTXCJ9LHtcImZlYXR1cmVJZFwiOlwiQUNIX0NPTExFQ1RJT05cIn0se1wiZmVhdHVyZUlkXCI6XCJBQ0hfRklMRVNcIn0se1wiZmVhdHVyZUlkXCI6XCJBQ0hfUEFZTUVOVFwifSx7XCJmZWF0dXJlSWRcIjpcIkFMRVJUX01BTkFHRU1FTlRcIn0se1wiZmVhdHVyZUlkXCI6XCJBUFBMSUNBTlRfTUFOQUdFTUVOVFwifSx7XCJmZWF0dXJlSWRcIjpcIkFQUFJPVkFMX01BVFJJWFwifSx7XCJmZWF0dXJlSWRcIjpcIkJJTExfUEFZXCJ9LHtcImZlYXR1cmVJZFwiOlwiQlVER0VUX1BPVFwifSx7XCJmZWF0dXJlSWRcIjpcIkJVTEtfUEFZTUVOVF9GSUxFU1wifSx7XCJmZWF0dXJlSWRcIjpcIkJVTEtfUEFZTUVOVF9SRVFVRVNUXCJ9LHtcImZlYXR1cmVJZFwiOlwiQlVMS19QQVlNRU5UX1RFTVBMQVRFXCJ9LHtcImZlYXR1cmVJZFwiOlwiQ0FMTF9CQU5LXCJ9LHtcImZlYXR1cmVJZFwiOlwiQ0FSRF9NQU5BR0VNRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiQ0RQX0NPTlNFTlRcIn0se1wiZmVhdHVyZUlkXCI6XCJDSEVDS19NQU5BR0VNRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiQ0hFUVVFX0JPT0tfUkVRVUVTVFwifSx7XCJmZWF0dXJlSWRcIjpcIkNPTlZFUlNBVElPTkFMX0JBTktJTkdcIn0se1wiZmVhdHVyZUlkXCI6XCJDVVNUT01fUk9MRVNcIn0se1wiZmVhdHVyZUlkXCI6XCJDVVNUT01fVklFV1wifSx7XCJmZWF0dXJlSWRcIjpcIkRJUkVDVF9ERUJJVFwifSx7XCJmZWF0dXJlSWRcIjpcIkRJU1BVVEVfVFJBTlNBQ1RJT05TXCJ9LHtcImZlYXR1cmVJZFwiOlwiRE9NRVNUSUNfV0lSRV9UUkFOU0ZFUlwifSx7XCJmZWF0dXJlSWRcIjpcIkVYUE9SVF9MQ1wifSx7XCJmZWF0dXJlSWRcIjpcIkZFRURCQUNLXCJ9LHtcImZlYXR1cmVJZFwiOlwiRlhfUkFURVNcIn0se1wiZmVhdHVyZUlkXCI6XCJHT0FMX1BPVFwifSx7XCJmZWF0dXJlSWRcIjpcIklNUE9SVF9MQ1wifSx7XCJmZWF0dXJlSWRcIjpcIklOSVRJQVRFX0ZVTkRJTkdcIn0se1wiZmVhdHVyZUlkXCI6XCJJTlRFUk5BVElPTkFMX0FDQ09VTlRfRlVORF9UUkFOU0ZFUlwifSx7XCJmZWF0dXJlSWRcIjpcIklOVEVSTkFUSU9OQUxfV0lSRV9UUkFOU0ZFUlwifSx7XCJmZWF0dXJlSWRcIjpcIklOVEVSX0JBTktfQUNDT1VOVF9GVU5EX1RSQU5TRkVSXCJ9LHtcImZlYXR1cmVJZFwiOlwiSU5UUkFfQkFOS19GVU5EX1RSQU5TRkVSXCJ9LHtcImZlYXR1cmVJZFwiOlwiTE9BTl9TQ0hFRFVMRVwifSx7XCJmZWF0dXJlSWRcIjpcIkxPR0lOXCJ9LHtcImZlYXR1cmVJZFwiOlwiTUFOQUdFX0FDQ09VTlRfU1RBVEVNRU5UU1wifSx7XCJmZWF0dXJlSWRcIjpcIk1FU1NBR0VTXCJ9LHtcImZlYXR1cmVJZFwiOlwiTkFPXCJ9LHtcImZlYXR1cmVJZFwiOlwiTk9USUZJQ0FUSU9OXCJ9LHtcImZlYXR1cmVJZFwiOlwiT05MSU5FX0JBTktJTkdfQUNDRVNTXCJ9LHtcImZlYXR1cmVJZFwiOlwiT1BFTl9CQU5LSU5HX0FDQ0VTU1wifSx7XCJmZWF0dXJlSWRcIjpcIlAyUFwifSx7XCJmZWF0dXJlSWRcIjpcIlBBU1NXT1JEX1VQREFURVwifSx7XCJmZWF0dXJlSWRcIjpcIlBBWUVFX01BTkFHRU1FTlRcIn0se1wiZmVhdHVyZUlkXCI6XCJQQVlfTVVMVElQTEVfQkVORUZJQ0lBUklFU1wifSx7XCJmZWF0dXJlSWRcIjpcIlBFUlNPTkFMX0ZJTkFOQ0VfTUFOQUdFTUVOVFwifSx7XCJmZWF0dXJlSWRcIjpcIlBST0ZJTEVfU0VUVElOR1NcIn0se1wiZmVhdHVyZUlkXCI6XCJQUk9TUEVDVF9FWFBJUllcIn0se1wiZmVhdHVyZUlkXCI6XCJQU0QyX1RQUF9DT05TRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiUkRDXCJ9LHtcImZlYXR1cmVJZFwiOlwiUkVTRVRfU0VDVVJJVFlfUVVFU1RJT05TXCJ9LHtcImZlYXR1cmVJZFwiOlwiUkVTVU1FX0FVVEhFTlRJQ0FUSU9OXCJ9LHtcImZlYXR1cmVJZFwiOlwiUkVWT0tFX1NUT1BfUEFZTUVOVF9SRVFVRVNUXCJ9LHtcImZlYXR1cmVJZFwiOlwiU0lHTkFUT1JZX0dST1VQXCJ9LHtcImZlYXR1cmVJZFwiOlwiU1RPUF9QQVlNRU5UX1JFUVVFU1RcIn0se1wiZmVhdHVyZUlkXCI6XCJUUkFOU0FDVElPTl9NQU5BR0VNRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiVFJBTlNGRVJfQkVUV0VFTl9PV05fQUNDT1VOVFwifSx7XCJmZWF0dXJlSWRcIjpcIlVOSUZJRURfVFJBTlNGRVJcIn0se1wiZmVhdHVyZUlkXCI6XCJVU0VSTkFNRV9VUERBVEVcIn0se1wiZmVhdHVyZUlkXCI6XCJVU0VSX01BTkFHRU1FTlRcIn0se1wiZmVhdHVyZUlkXCI6XCJVU0VSX1ZFUklGSUNBVElPTlwifSx7XCJmZWF0dXJlSWRcIjpcIlZJRVdfQ0hFUVVFU1wifSx7XCJmZWF0dXJlSWRcIjpcIldFQUxUSF9DQU1QQUlHTl9NR01UXCJ9LHtcImZlYXR1cmVJZFwiOlwiV0VBTFRIX0NBU0hfTUFOQUdFTUVOVFwifSx7XCJmZWF0dXJlSWRcIjpcIldFQUxUSF9FTlJPTExNRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiV0VBTFRIX0lOVkVTVE1FTlRfREVUQUlMU1wifSx7XCJmZWF0dXJlSWRcIjpcIldFQUxUSF9NQVJLRVRfQU5EX05FV1NcIn0se1wiZmVhdHVyZUlkXCI6XCJXRUFMVEhfTkVXU19BTkRfRE9DVU1FTlRTXCJ9LHtcImZlYXR1cmVJZFwiOlwiV0VBTFRIX09SREVSX01BTkFHRU1FTlRcIn0se1wiZmVhdHVyZUlkXCI6XCJXRUFMVEhfUE9SVEZPTElPX0RFVEFJTFNcIn0se1wiZmVhdHVyZUlkXCI6XCJXRUFMVEhfUFJPRFVDVF9ERVRBSUxTXCJ9LHtcImZlYXR1cmVJZFwiOlwiV0VBTFRIX1JFUE9SVF9NQU5BR0VNRU5UXCJ9LHtcImZlYXR1cmVJZFwiOlwiV0VBTFRIX1JFVEFJTF9BQ0NPVU5UU1wifSx7XCJmZWF0dXJlSWRcIjpcIldFQUxUSF9XQVRDSExJU1RcIn0se1wiZmVhdHVyZUlkXCI6XCJXSVRIRFJBV19DQVNIXCJ9XX1dIiwiY29tbXVuaWNhdGlvbiI6Ilt7XCJwaG9uZUNvdW50cnlDb2RlXCI6XCIrMTFcIixcInBob25lTnVtYmVyXCI6XCI3MjE1Mjc0XCJ9LHtcImVtYWlsXCI6XCJDcmFpZy5UYXJyQHN0YW5kYXJkYmFuay5jby56YVwifV0ifQ==\",\"contractId\":\"5117704706\",\"opstatus\":0,\"status\":\"success\",\"httpStatusCode\":0}";
			//try {
			//	obj.testOlbUserCreation((JSONObject)(new JSONParser().parse(contractResponse)), partyId, coreCustId);
			//} catch (ParseException e) {
			//	e.printStackTrace();
			//}
		}

		Console.print("==============COMPLETED @ "+new Date());
	}

	private	boolean loadCsvFiles() {
		try {
			contractMap 		= DataReader.loadContractData(CSV_FILE_PATH+File.separator+"Contract.csv");
			customersMap 		= DataReader.loadCustomerData(CSV_FILE_PATH+File.separator+"Customers.csv");
			AccountsMap 		= DataReader.loadAccountsData(CSV_FILE_PATH+File.separator+"Accounts.csv");
			userDetailsMap 		= DataReader.loadUserDetailsData(CSV_FILE_PATH+File.separator+"UserDetails.csv");
			userContractsMap 	= DataReader.loadUserContractsData(CSV_FILE_PATH+File.separator+"UserContracts.csv");
			//designatedUsersMap	= DataReader.loadDesignatedContactData(CSV_FILE_PATH+File.separator+"designatedperson.csv");

			Console.print("Number of contractMap records: "+contractMap.size());
			Console.print("Number of customersMap records: "+customersMap.size());
			Console.print("Number of AccountsMap records: "+AccountsMap.size());
			Console.print("Number of userDetailsMap records: "+userDetailsMap.size());
			Console.print("Number of userContractsMap records: "+userContractsMap.size());
			//Console.print("Number of designatedUsersMap records: "+designatedUsersMap.size());
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		if(contractMap.size() == 0 || customersMap.size() == 0 || AccountsMap.size() == 0 ||
			userDetailsMap.size() == 0 || userContractsMap.size() == 0 || userDetailsMap.size() != userContractsMap.size())
		{
			return true; // TODO:: to take it back to false if all validations are required
		}
		return true;
	}

	private	boolean verifyfiles() {
		Console.print("LoadSmeData.verifyfiles ---> START verification ");
		Iterator<String> it = contractMap.keySet().iterator();
		while(it.hasNext()) {
			String contractName = it.next();
			Contract contract 	= contractMap.get(contractName);
			String corecustcif	= contract.getCoreCustId();
			Console.print("LoadSmeData.verifyfiles ---> contractName: "+contractName+"; corecustcif: "+corecustcif);

			//customer file check
			Customer customer = LoadSmeData.customersMap.get(corecustcif);
			if(customer == null) {
				Console.print("CUSTOMER NOT FOUND FOR ::: contractName: "+contractName+"; corecustcif: "+corecustcif);
				return false;
			} else {
				Console.print("CUSTOMER VERIFICATION PASSED ");
			}

			//accounts file check
			int accountCount = 0;
			for(Accounts account : LoadSmeData.AccountsMap) {
				if(corecustcif.equals(account.getCoreCustomerId())) {
					++accountCount;
				}
			}
			if(accountCount ==0) {
				Console.print("ACCOUNTS NOT MATCHING FOR contractName: "+contractName+"; corecustcif: "+corecustcif);
				return false;
			} else {
				Console.print("ACCOUNTS VERIFICATION PASSED");
			}

			//userdetails file check
			int usersCount = 0;
			List<UserDetails> users = new ArrayList<>();
			int count = userDetailsMap == null ? 0 : userDetailsMap.size();
			for(int i=0 ; i<count ; ++i) {
				UserDetails userDetails = userDetailsMap.get(i);
				if(userDetails != null && userDetails.getCoreCustomerId().equals(corecustcif)) {
					users.add(userDetails);
					++usersCount;
				}
			}
			//Console.print("# OF USER DETAILS FOUND IS "+usersCount);
			if(usersCount == 0) {
				Console.print("USER DETAILS NOT MATCHING FOR contractName: "+contractName+"; corecustcif: "+corecustcif);
				return true; //TODO:: to make false if UserDetails validations are required
			} else {
				Console.print("USERDETAILS VERIFICATION PASSED for corecustcif: "+corecustcif);
			}

			for(UserDetails user : users) {
				int uccount = 0;
				//Console.print("=========== verifying user details user: "+user);
				for(UserContracts uc : userContractsMap) {
					//Console.print("---------- User Contract Details: "+uc);
					if(uc.getCoreCustomerId().equals(user.getCoreCustomerId()) &&
							uc.getEmail().equalsIgnoreCase(user.getEmail()))
					{
						++uccount;
					}
				}
				//Console.print("=========== uccount: "+uccount);
				if(uccount != 1) {
					Console.print("USER CONTRACTS NOT MATCHING FOR corecustcif: "+user.getCoreCustomerId()+"; email: "+user.getEmail()+"; uccount: "+uccount);
					return true; //TODO:: to make false if UserContracts validations are required
				} else {
					Console.print("USERCONTRACT VERIFICATION PASSED for corecustcif: "+corecustcif +"; email: "+user.getEmail());
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private	void processData() {

		Iterator<String> it = contractMap.keySet().iterator();
		while(it.hasNext()) {
			String contractName = it.next();
			Contract contract 	= contractMap.get(contractName);
			String corecustcif	= contract.getCoreCustId();

			//JSONObject partyPayload		= partyPayloadCreator.getPayload4Contract(contractName, corecustcif);
			JSONObject partyPayload		= CommonUtils.getPayload4Party(contractName, corecustcif);
			Console.print("LoadSmeData.processData ---> party Payload: "+partyPayload);

			JSONObject partyResponse	= PartyHelper.createPartyRecord(partyPayload);
			Console.print("LoadSmeData.processData ---> party Response: "+partyResponse);

			if(CommonUtils.isValidPartyResponse(partyResponse)) {
				String partyId = (String) partyResponse.get("id");
				JSONObject physicalAddressPayload 	= CommonUtils.getPayload4PhysicalAddress(partyId, contractName, corecustcif);
				Console.print("LoadSmeData.processData ---> physicalAddress Payload: "+physicalAddressPayload);

				JSONObject physicalAddressResponse	= PartyHelper.createPhysicalAddress(physicalAddressPayload);
				Console.print("LoadSmeData.processData ---> physicalAddress Response: "+physicalAddressResponse);

				if(CommonUtils.isValidAddressResponse(physicalAddressResponse)) {
					String addressReference = (String)physicalAddressResponse.get("addressesReference");
					JSONObject commAddressPayload 	= CommonUtils.getPayload4CommunicationAddress(partyId, addressReference, contractName, corecustcif);
					Console.print("LoadSmeData.processData ---> commAddress Payoad: "+commAddressPayload);

					JSONObject commAddressResponse	= PartyHelper.createCommunicationAddress(commAddressPayload);
					Console.print("LoadSmeData.processData ---> commAddressResponse: "+commAddressResponse);

					if(CommonUtils.isValidAddressResponse(commAddressResponse)) {
						int counter = processArrangementData(partyId, corecustcif);
						Console.print("LoadSmeData.processData ---> counter: "+counter);

						if(counter > 0) {
							JSONObject contractPayload = getContractCreationPayload(partyId, corecustcif);
							Console.print("Digital Profile user creation payload: "+contractPayload);

							//JSONObject dpResponse = CreateDigitalProfile.createDigitalProfileRecord(userPayload);
							JSONObject dpResponse = CreateDigitalProfile.createDigitalContract(contractPayload);
							Console.print("Contract created for "+partyId +"; Response: "+dpResponse);

							if(CommonUtils.isValidResponse(dpResponse, "contractId") && "success".equals(dpResponse.get("status"))) {
								String encContractCreatedPayload = (String)dpResponse.get("encContractPayload");
								Console.print("LoadSmeData.processData() --- encContractCreatedPayload ..."+encContractCreatedPayload);

								if(encContractCreatedPayload != null) {
									int count = userDetailsMap == null ? 0 : userDetailsMap.size();
									for(int i=0 ; i<count ; ++i) {
										UserDetails userDetails = userDetailsMap.get(i);
										UserContracts userContract = getUserContractByEmail(corecustcif, userDetails.getEmail());
										Console.print("userContract: "+userContract);

										JSONObject userPartyPayload	= CommonUtils.getUserPayload4Party(customersMap.get(corecustcif), userDetails, userContract);
										Console.print("LoadSmeData.processData ---> userPartyPayload: "+userPartyPayload);

										JSONObject userPartyResponse= PartyHelper.createUserPartyRecord(userPartyPayload);
										Console.print("LoadSmeData.processData() --- userPartyResponse ..."+userPartyResponse);

										if(CommonUtils.isValidPartyResponse(userPartyResponse)) {

											String userPartyId = (String) partyResponse.get("id");

											//JSONObject userDataPL	= new JSONObject();
											userPartyPayload.put("username", userDetails.getEmail());
											userPartyPayload.put("roleId", userContract.getRoleId());
											userPartyPayload.put("userPartyId", userPartyId);

											JSONObject userPayload	= new JSONObject();
											userPayload.put("contractPayload",encContractCreatedPayload);
											userPayload.put("contractId", dpResponse.get("contractId"));
											userPayload.put("partyId", partyId);
											userPayload.put("coreCustomerNo", corecustcif);
											userPayload.put("userDataPayload", userPartyPayload.toJSONString());

											JSONObject dpUserResponse = CreateDigitalProfile.createDigitalUser(userPayload);
											Console.print("Infinty User created for "+userDetails.getEmail() +"; dpUserResponse: "+dpUserResponse);
										} else {
											Console.print("FAILED TO CREATE USER PARTY RECORD IN MS FOR "+userDetails.getEmail());
										}
									}
								} else {
									Console.print("Digital Profile created failed for "+partyId+" since contract payload is empty");
								}
							}
						}
					} else {
						Console.print("FAILED TO CREATE COMMUNICATION ADDRESS RECORD FOR PARTY: "+partyId +" AND ADD REF: "+addressReference);
					}
				} else {
					Console.print("FAILED TO CREATE PHYSICAL ADDRESS RECORD FOR PARTY: "+partyId);
				}
			} else {
				Console.print("FAILED TO CREATE PARTY RECORD FOR CONTRACT: "+contractName);
			}
			//break; // to run one full data
		}
	}

	@SuppressWarnings("unchecked")
	private	void processData_New() {

		Iterator<String> it = contractMap.keySet().iterator();
		while(it.hasNext()) {
			String contractName = it.next();
			Contract contract 	= contractMap.get(contractName);
			String corecustcif	= contract.getCoreCustId();
			Console.print("==========>>> START PROCESSING FOR "+corecustcif+" WITH CONTRACT NAME: "+contractName +" <<<==========");

			//JSONObject partyPayload		= partyPayloadCreator.getPayload4Contract(contractName, corecustcif);
			JSONObject partyPayload		= CommonUtils.getCustomerPayload4Party(contractName, corecustcif);
			Console.print("LoadSmeData.processData ---> party Payload: "+partyPayload);

			JSONObject partyResponse	= PartyHelper.createUserPartyRecord(partyPayload);
			Console.print("LoadSmeData.processData ---> party Response: "+partyResponse);

			if(CommonUtils.isValidPartyResponse(partyResponse)) {
				String partyId = (String) partyResponse.get("id");

				// new code
				//				JSONObject physicalAddressPayload 	= CommonUtils.getPayload4PhysicalAddress(partyId, contractName, corecustcif);
				//				Console.print("LoadSmeData.processData ---> physicalAddress Payload: "+physicalAddressPayload);
				//
				//				JSONObject physicalAddressResponse	= PartyHelper.createPhysicalAddress(physicalAddressPayload);
				//				Console.print("LoadSmeData.processData ---> physicalAddress Response: "+physicalAddressResponse);
				// new code

				int counter = processArrangementData(partyId, corecustcif);
				Console.print("LoadSmeData.processData ---> counter: "+counter);

				if(counter > 0) {
					JSONObject contractPayload = getContractCreationPayload(partyId, corecustcif);
					Console.print("Digital Profile user creation payload: "+contractPayload);

					//JSONObject dpResponse = CreateDigitalProfile.createDigitalProfileRecord(userPayload);
					JSONObject dpResponse = CreateDigitalProfile.createDigitalContract(contractPayload);
					Console.print("Contract created for "+partyId +"; Response: "+dpResponse);

					if(CommonUtils.isValidResponse(dpResponse, "contractId") && "success".equals(dpResponse.get("status"))) {
						String encContractCreatedPayload = (String)dpResponse.get("encContractPayload");
						Console.print("LoadSmeData.processData() --- encContractCreatedPayload ..."+encContractCreatedPayload);

						if(encContractCreatedPayload != null) {
							Customer customer = customersMap.get(corecustcif);
							JSONObject userPartyPayload	= CommonUtils.getUserPayload4Party(customer, null, null);
							Console.print("LoadSmeData.processData ---> userPartyPayload: "+userPartyPayload);

							JSONObject userPartyResponse= PartyHelper.createUserPartyRecord(userPartyPayload);
							Console.print("LoadSmeData.processData() --- userPartyResponse ..."+userPartyResponse);

							if(CommonUtils.isValidPartyResponse(userPartyResponse)) {

								String userPartyId = (String) partyResponse.get("id");

								//JSONObject userDataPL	= new JSONObject();
								userPartyPayload.put("username", customer.getContactEmail());
								userPartyPayload.put("roleId", "GROUP_ADMINISTRATOR");
								userPartyPayload.put("userPartyId", userPartyId);
								String allowedAccounts = AccountsMap.stream()
										.filter(account -> account.getCoreCustomerId().equals(corecustcif))
										.map(Accounts::getAccountId)
										.collect(Collectors.joining("|"));
								userPartyPayload.put("allowedAccounts", allowedAccounts);

								JSONObject userPayload	= new JSONObject();
								userPayload.put("contractPayload",encContractCreatedPayload);
								userPayload.put("contractId", dpResponse.get("contractId"));
								userPayload.put("partyId", partyId);
								userPayload.put("coreCustomerNo", corecustcif);
								userPayload.put("userDataPayload", userPartyPayload.toJSONString());

								JSONObject dpUserResponse = CreateDigitalProfile.createDigitalUser(userPayload);
								Console.print("Infinty User created for "+customer.getContactEmail() +"; dpUserResponse: "+dpUserResponse);
							} else {
								Console.print("FAILED TO CREATE USER PARTY RECORD IN MS FOR "+customer.getContactEmail());
							}
						} else {
							Console.print("Digital Profile created failed for "+partyId+" since contract payload is empty");
						}
					}
				}
			} else {
				Console.print("FAILED TO CREATE PARTY RECORD FOR CONTRACT: "+contractName);
			}
			//break; // to run one full data
		}
	}

	private	UserContracts getUserContractByEmail(String coreCustId, String email) {
		int count = userContractsMap.size();
		Console.print("LoadSmeData.getUserContractByEmail ---> count: "+count+"; coreCustId: "+coreCustId+"; email: "+email);

		for(int i=0 ; i<count ; ++i) {
			UserContracts uc = userContractsMap.get(i);
			if(uc != null && uc.getCoreCustomerId().equals(coreCustId) && uc.getEmail().equalsIgnoreCase(email)) {
				return uc;
			}
		}
		return null;
	}

	private	int processArrangementData(String partyId, String corecustcif) {

		int counter = 0;

		JSONObject amsPartyPayload 	= CommonUtils.getPayload4ArrangementParty(corecustcif);
		Console.print("LoadSmeData.processArrangementData ---> amsPartyPayload: "+amsPartyPayload);

		JSONObject amsPartyResponse	= ArrangementHelper.createArrangementParty(amsPartyPayload);
		Console.print("LoadSmeData.processArrangementData ---> amsPartyResponse: "+amsPartyResponse);

		if(CommonUtils.isValidAmsPartyResponse(amsPartyResponse)) {
			List<Accounts> accounts = getAccountsByCoreCustId(corecustcif);
			int count = accounts == null ? 0 : accounts.size();
			for(int i=0 ; i<count ; ++i) {
				Accounts account = accounts.get(i);
				JSONObject amsAccountPayload	= CommonUtils.getPayload4ArrangementAccount(partyId, corecustcif, account);
				Console.print("LoadSmeData.processArrangementData ---> amsAccount Payload: "+amsAccountPayload);

				JSONObject amsAccountResponse	= ArrangementHelper.createArrangementAccount(amsAccountPayload);
				Console.print("LoadSmeData.processArrangementData ---> amsAccountResponse: "+amsAccountResponse);

				if(CommonUtils.isValidAMSAccountResponse(amsAccountResponse)) {
					++counter;
				} else {
					Console.print("FAILED TO CREATE ARRANEMENT ACCOUNT RECORD FOR PARTY: "+partyId+" and account: "+account.getAccountId());
				}
			}
		} else {
			Console.print("FAILED TO CREATE ARRANEMENT PARTY RECORD FOR PARTY: "+partyId);
		}
		return counter;
	}

	private	void processArrangementAccountsOnly(String partyId, String corecustcif) {

		int counter = 0;

		List<Accounts> accounts = getAccountsByCoreCustId(corecustcif);
		int count = accounts == null ? 0 : accounts.size();
		for(int i=0 ; i<count ; ++i) {
			Accounts account = accounts.get(i);
			JSONObject amsAccountPayload	= CommonUtils.getPayload4ArrangementAccount(partyId, corecustcif, account);
			Console.print("LoadSmeData.processArrangementAccounts ---> amsAccount Payload: "+amsAccountPayload);

			JSONObject amsAccountResponse	= ArrangementHelper.createArrangementAccount(amsAccountPayload);
			Console.print("LoadSmeData.processArrangementAccounts ---> amsAccountResponse: "+amsAccountResponse);

			if(CommonUtils.isValidAMSAccountResponse(amsAccountResponse)) {
				++counter;
			} else {
				Console.print("FAILED TO CREATE ARRANEMENT ACCOUNT RECORD FOR PARTY: "+partyId+" and account: "+account.getAccountId());
			}
		}

		Console.print("LoadSmeData.processArrangementAccounts ---> counter: "+counter);
	}

	private	List<Accounts> getAccountsByCoreCustId(String coreCustId) {
		List<Accounts> accounts = new ArrayList<Accounts>();

		for(Accounts account : LoadSmeData.AccountsMap) {
			if(coreCustId.equals(account.getCoreCustomerId())) {
				accounts.add(account);
			}
		}

		return accounts;
	}

	@SuppressWarnings("unchecked")
	private	JSONObject getContractCreationPayload(String partyId, String corecustcif) {
		JSONObject payload	= new JSONObject();

		Customer customer = customersMap.get(corecustcif);

		String parties = "[\""+partyId+"\"]";
		payload.put("partyIdList",parties);
		payload.put("serviceType", "Business");
		payload.put("partyId", partyId);
		payload.put("coreCustId", corecustcif);

		payload.put("phoneCountryCode", customer.getContactPhoneCountryCode());
		payload.put("phoneNumber", customer.getContactPhoneNumber());

		payload.put("email", customer.getContactEmail());

		payload.put("addressLine1", customer.getLegaladdressLine1());
		payload.put("addressline2", customer.getLegaladdressLine2());
		payload.put("addressline3", customer.getLegaladdressLine3());
		payload.put("cityName", customer.getLegalcityName());
    	payload.put("state", customer.getLegalstate());
    	payload.put("country", customer.getLegalcountry());
    	payload.put("zipCode", customer.getLegalzipCode());

		payload.put("postalAddressLine1", customer.getPostalAddressLine1());
		payload.put("postalAddressLine2", customer.getPostalAddressLine2());
		payload.put("postalAddressLine3", customer.getPostalAddressLine3());
		payload.put("postalCityName", customer.getPostalCityName());
		payload.put("postalState", customer.getPostalState());
		payload.put("postalCountry", customer.getPostalCountry());
		payload.put("postalZipCode", customer.getPostalZipCode());

		return payload;
	}

	private	JSONObject getContractPayload4mResponse(String cRes) {

		try {
			JSONArray jarr = (JSONArray)new JSONParser().parse(cRes);
			int count = jarr == null ? 0 : jarr.size();
			Console.print("getContractPayload4mResponse ---> count: "+count);

			for(int i=0 ; i<count ; ++i) {
				JSONObject jo = (JSONObject)jarr.get(i);
				Console.print("getContractPayload4mResponse ---> obj: "+jo);
				if(jo.get("contractPayload") != null) {

					String cp = (String)jo.get("contractPayload");
					JSONObject cpObj = (JSONObject)new JSONParser().parse(cp);
					return cpObj;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private	JSONObject testUserCreation(String pid, String cid) {

		Console.print("==============Processing Infinity User only=================");

		//internal dev party id
		//String partyId = "2225066409";

		//sbg dev party id
		//String partyId = "2225039056";
		//String coreCustId = "4242424242";
		JSONObject contractPayload = getContractCreationPayload(pid, cid);
		Console.print("LoadSmeData.testUserCreation ---> payload: "+contractPayload);

		//JSONObject dpResponse = CreateDigitalProfile.createDigitalProfileRecord(userPayload);
		//Console.print("LoadSmeData.testUserCreation --- Digital Profile created for "+partyId +"; Response: "+dpResponse);

		JSONObject dpContractResponse = CreateDigitalProfile.createDigitalContract(contractPayload);
		Console.print("LoadSmeData.testUserCreation --- Digital contract created for "+pid +"; dpContractResponse: "+dpContractResponse);

		return dpContractResponse;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private	void testOlbUserCreation(JSONObject dpContractResponse, String pid, String cid) {

		Console.print("==============Processing testOlbUserCreation only=================START -- pid:"+pid+"; cid:"+cid);

		if(CommonUtils.isValidResponse(dpContractResponse, "contractId") && "success".equals(dpContractResponse.get("status"))) {
			String encContractCreatedPayload = (String)dpContractResponse.get("encContractPayload");
			Console.print("LoadSmeData.processData() --- encContractCreatedPayload ..."+encContractCreatedPayload);

			if(encContractCreatedPayload != null) {
				int count = userDetailsMap == null ? 0 : userDetailsMap.size();
				for(int i=0 ; i<count ; ++i) {

					UserDetails userDetails = userDetailsMap.get(i);
					if(cid.equals(userDetails.getCoreCustomerId())) {
						Console.print("LoadSmeData.processData() --- userDetails ..."+userDetails);

						UserContracts userContract = getUserContractByEmail(cid, userDetails.getEmail());

						JSONObject userPartyPayload	= CommonUtils.getUserPayload4Party(customersMap.get(cid), userDetails, userContract);
						Console.print("LoadSmeData.processData ---> userPartyPayload: "+userPartyPayload);

						JSONObject userPartyResponse= PartyHelper.createUserPartyRecord(userPartyPayload);
						Console.print("LoadSmeData.processData() --- userPartyResponse ..."+userPartyResponse);

						if(CommonUtils.isValidPartyResponse(userPartyResponse)) {

							String userPartyId = (String) userPartyResponse.get("id");

							//JSONObject userDataPL	= new JSONObject();
							userPartyPayload.put("username", userDetails.getEmail());
							userPartyPayload.put("roleId", userContract.getRoleId());
							userPartyPayload.put("userPartyId", userPartyId);
							userPartyPayload.put("allowedAccounts", userContract.getAccounts());

							JSONObject userPayload	= new JSONObject();
							userPayload.put("contractPayload",encContractCreatedPayload);
							userPayload.put("contractId", dpContractResponse.get("contractId"));
							userPayload.put("partyId", pid);
							userPayload.put("coreCustomerNo", cid);
							userPayload.put("userDataPayload", userPartyPayload.toJSONString());

							JSONObject dpUserResponse = CreateDigitalProfile.createDigitalUser(userPayload);
							Console.print("Infinty User created for "+userDetails.getEmail() +"; dpUserResponse: "+dpUserResponse);
						} else {
							Console.print("FAILED TO CREATE USER PARTY RECORD IN MS FOR "+userDetails.getEmail());
						}
					}
					//break;
				}
			} else {
				Console.print("Digital Profile created failed for "+pid+" since contract payload is empty");
			}
		}

		Console.print("==============Processing testOlbUserCreation only=================END");
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private	void testArrangmentCreation() {

		Console.print("==============Processing accounts only=================");
		JSONObject payload = new JSONObject();

		String corecustid = "2510901000";

		int extid = CommonUtils.RANDOM.nextInt();
		String extIdStr = new String(""+extid);
		if(extIdStr.startsWith("-")) {
			extIdStr = extIdStr.substring(1);
		}

		payload.put("extArrangementId", extIdStr);	//customer.getCoreCustomerId());
		payload.put("linkRef", "GB0010001-"+extIdStr); //companyid-account#
		payload.put("partyId", "GB0010001-"+corecustid); //companyid-corecustid
		payload.put("companyReference", "GB0010001");
		Console.print("LoadSmeData.testArrangmentCreation ---> payload: "+payload);

		JSONObject amsPartyPayload 	= CommonUtils.getPayload4ArrangementParty(corecustid);
		Console.print("LoadSmeData.testArrangmentCreation ---> amsPartyPayload: "+amsPartyPayload);

		JSONObject amsPartyResponse	= ArrangementHelper.createArrangementParty(amsPartyPayload);
		Console.print("LoadSmeData.testArrangmentCreation ---> amsPartyResponse: "+amsPartyResponse);

		if(CommonUtils.isValidAmsPartyResponse(amsPartyResponse)) {
//			JSONObject amsAccountResponse	= ArrangementHelper.createArrangementAccount(payload);
//			Console.print("LoadSmeData.testArrangmentCreation ---> amsAccountResponse: "+amsAccountResponse);			
		}
	}
}

