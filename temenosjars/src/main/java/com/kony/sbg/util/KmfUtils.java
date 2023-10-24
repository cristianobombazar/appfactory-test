package com.kony.sbg.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;

public class KmfUtils {

    private static LoggerUtil logger = new LoggerUtil(KmfUtils.class);
    
    public  static void printInputs(Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
        if(request != null) 
        {
            {
                logger.debug("FabricUtils.printInputs () ---> PRINTING HEADER MAP ::: START ");      
                Map<String, Object> map = request.getHeaderMap();
                Iterator<String> itr = map.keySet().iterator();
                StringBuffer sb = new StringBuffer();
                while(itr.hasNext()) {
                    String key = itr.next();
                    String val = (String)map.get(key);
                    sb.append("key: "+key+"; val: "+val+" :: ");
                    //logger.debug("FabricUtils.printInputs () ---> key: "+key+"; val: "+val);
                }
                logger.debug("FabricUtils.printInputs () ---> PRINTING HEADER MAP ::: "+sb.toString());        
            }
            {
                logger.debug("FabricUtils.printInputs () ---> PRINTING ATTRIBUTE NAMES ::: START ");     
                Iterator<String> itr = request.getAttributeNames();
                StringBuffer sb = new StringBuffer();
                while(itr.hasNext()) {
                    String key = itr.next();
                    Object val = request.getAttribute(key);
                    sb.append("key: "+key+"; val: "+val+" :: ");
                    //logger.debug("FabricUtils.printInputs () ---> key: "+key+"; val: "+val);
                }
                logger.debug("FabricUtils.printInputs () ---> PRINTING ATTRIBUTE NAMES ::: "+sb.toString());       
            }
            {
                logger.debug("FabricUtils.printInputs () ---> PRINTING PARAMETER NAMES ::: START ");     
                Iterator<String> itr = request.getParameterNames();
                StringBuffer sb = new StringBuffer();
                while(itr.hasNext()) {
                    String key = itr.next();
                    String val = request.getParameter(key);
                    sb.append("key: "+key+"; val: "+val+" :: ");
                    //logger.debug("FabricUtils.printInputs () ---> key: "+key+"; val: "+val);
                }
                logger.debug("FabricUtils.printInputs () ---> PRINTING PARAMETER NAMES ::: "+sb.toString());       
            }
            {
                logger.debug("FabricUtils.printInputs () ---> PRINTING SESSION PARAMS/VALUES ::: START ");   
                
                Enumeration<?> it = request.getSession().getAttributeNames();
                StringBuffer sb = new StringBuffer();
                while(it != null && it.hasMoreElements()) {
                    Object key = it.nextElement();
                    Object val = request.getSession().getAttribute(key.toString());
                    sb.append("key: "+key+"; val: "+val+" :: ");
                    //logger.debug("FabricUtils.printInputs () ---> key: "+key+"; val: "+val);
                }
                
                logger.debug("FabricUtils.printInputs () ---> PRINTING SESSION PARAMS/VALUES ::: "+sb.toString());       
            }
        }
        {
            logger.debug("FabricUtils.printInputs () ---> PRINTING INPUTARRAY ::: START ");   
            StringBuffer sb = new StringBuffer();
            int count = inputArray == null ? 0 : inputArray.length;
            for(int i = 0 ; i<count ; ++i) {
            	sb.append("VALUE: "+inputArray[i] +" :: ");
                logger.debug("FabricUtils.printInputs () ---> VALUE: "+inputArray[i]);
            }
            logger.debug("FabricUtils.printInputs () ---> PRINTING INPUTARRAY ::: "+sb.toString());       
        }
        if(response != null) 
        {
            {
                logger.debug("FabricUtils.printInputs () ---> PRINTING RESPONSE PARAMS/VALUES ::: START ");   
                StringBuffer sb = new StringBuffer();
                Iterator<String> it = response.getAttributeNames();
                while(it.hasNext()) {
                    String attName  = it.next();
                    String attValue = response.getAttribute(attName);
                    sb.append("name: "+attName+"; val: "+attValue+" :: ");
                    logger.debug("FabricUtils.printInputs () ---> name: "+attName+"; value: "+attValue);
                }
                logger.debug("FabricUtils.printInputs () ---> PRINTING RESPONSE PARAMS/VALUES ::: "+sb.toString());       
            }
        }
    }   
}
