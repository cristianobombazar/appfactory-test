<#macro EmptyObjFallBackResult>
  null,
</#macro>
    <#assign res = dr>
    <#assign nres = cr>
    <#assign transactionReference = TrnReference>
    <#if ComposeFor?? && ComposeFor?upper_case?starts_with("DR") && EvaluationDr??>
        <#assign Evaluation = EvaluationDr>
    <#elseif ComposeFor?? && ComposeFor?upper_case?starts_with("CR") && EvaluationCr??>
        <#assign Evaluation = EvaluationCr>
    </#if>

<#if Evaluation?? && Evaluation.Evaluations?? && Evaluation.Evaluations[0]??>
    <#if Evaluation.Evaluations[0].ResSide?? || Evaluation.Evaluations[0].NonResSide??>
        <#if ((Evaluation.Evaluations[0].ResSide)?upper_case?starts_with('DR')) || ((Evaluation.Evaluations[0].NonResSide!'UNKNOWN')?upper_case?starts_with('CR'))>
            <#assign res = dr>
            <#assign nres = cr>
        <#else>
            <#assign res = cr>
            <#assign nres = dr>
        </#if>
    </#if>
    <#--Catering for scenarios where both sides are reportable - TRN reference should be unique-->
    <#if Evaluation.Evaluations[0].ReportingSide??>
        <#if ReportForBothSides??>
            <#assign transactionReference = TrnReference + '_' + Evaluation.Evaluations[0].ReportingSide>
        <#else>
            <#assign transactionReference = TrnReference>
        </#if>
    </#if>
    <#if Evaluation.Evaluations[0].ResAccountType??>
        <#assign resAccountType = Evaluation.Evaluations[0].ResAccountType>
    </#if>
    <#if Evaluation.Evaluations[0].NonResAccountType??>
        <#assign nresAccountType = Evaluation.Evaluations[0].NonResAccountType>
    </#if>
    <#if Evaluation.Evaluations[0].ReportingQualifier??>
        <#assign ReportingQualifier = Evaluation.Evaluations[0].ReportingQualifier>
    </#if>
    <#if Evaluation.Evaluations[0].Flow??>
        <#assign Flow = Evaluation.Evaluations[0].Flow?upper_case?starts_with('OUT')?string('OUT', 'IN')>
    </#if>
<#else>
    /*SOMETHING WRONG HERE!*/
</#if>
<#--  TODO: This template assumes ONLY BOPCUS reports will be generated... it needs to have logic to check the reporting qualifier and generate the correct report as needed.  If NOT reportable, then NOTHING should be generated - blank report. -->
{
  "Meta": {
    "readOnly": <#if readOnly??>"${readOnly!''}"<#else>null</#if>,
    "SourceReference": <#if TrnReference??>"${TrnReference!''}"<#else>null</#if>,
    "TotalRandAmount":<#if TotalDomesticValue??>"${TotalDomesticValue!''}"<#else>null</#if>,
    "SourceSystem":<#if SourceSystem??>"${SourceSystem!''}"<#else>null</#if>
  },
  "Report": {
    "TotalValue": <#if CalculatedTotalValue??>"${CalculatedTotalValue!''}"<#else>null</#if>,
    "TrnReference": "${transactionReference!''}",
    "Version": "FINSURV",
    "ReportingQualifier": <#if ReportingQualifier??>"${ReportingQualifier!'BOPCUS'}"<#else>null</#if>,
    "BranchCode": <#if BranchCode??>"${BranchCode!''}"<#else>null</#if>,
    "BranchName": <#if BranchName??>"${BranchName!''}"<#else>null</#if>,

    "Flow": <#if Flow??>"${Flow!''}"<#else>null</#if>,
    "FlowCurrency": <#if FlowCurrency??>"${FlowCurrency!''}"<#else>"<#if Flow?? && Flow?upper_case?starts_with('IN')>${drCurrency!''}<#else>${crCurrency!''}</#if>"</#if>,
    "TotalForeignValue": <#if TotalForeignValue??>"${TotalForeignValue!''}"<#else><#if Flow?? && Flow?upper_case?starts_with('IN')>${drAmount!''}<#else>${crAmount!''}</#if></#if>,
    "TotalDomesticValue": <#if TotalDomesticValue??>"${TotalDomesticValue!''}"<#else>null</#if>,
    "DomesticCurrency": <#if DomesticCurrency??>"${DomesticCurrency!''}"<#else>"<#if Flow?? && Flow?upper_case?starts_with('IN')>${crCurrency!''}<#else>${drCurrency!''}</#if>"</#if>,
    "ValueDate": <#if ValueDate??>"${ValueDate!''}"<#else>null</#if>,
    "OriginatingBank": <#if drBIC??>"${drBIC!''}"<#else>null</#if>,
    "OriginatingCountry": <#if drBIC??>"${drBIC[4..5]}"<#else>null</#if>,
    "ReceivingCountry":<#if crBIC??>"${crBIC[4..5]}"<#else>null</#if>,
    "ReceivingBank": <#if crBIC??>"${crBIC!''}"<#else>null</#if>,
    "CorrespondentBank": <#if CorrespondentBank??>"${CorrespondentBank!''}"<#else>null</#if>,
    "CorrespondentCountry": <#if CorrespondentCountry??>"${CorrespondentCountry!''}"<#else>null</#if>,



    "Resident":
        <#assign optTemp = .get_optional_template('ResidentTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
null
        </#if>
    ,
    "NonResident":
        <#assign optTemp = .get_optional_template('NonResidentTemplate.ftl')>
        <#if optTemp.exists>
              <@optTemp.include />
        <#else>
null
        </#if>
    ,

    "MonetaryAmount":
        <#assign optTemp = .get_optional_template('MonetaryAmountTemplate.ftl')>
        <#if optTemp.exists>
              <@optTemp.include />
        <#else>
null
        </#if>

  }
}
