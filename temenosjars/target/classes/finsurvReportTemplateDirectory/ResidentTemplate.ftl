{
<#if res??>
    <#if res.ExceptionName?? || (Evaluation.Evaluations?? && Evaluation.Evaluations[0]?? && Evaluation.Evaluations[0].ResException??)>
        <#assign optTemp = .get_optional_template('ResidentExceptionTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Exception":null
        </#if>
    <#elseif res.EntityName??>
        <#assign optTemp = .get_optional_template('ResidentEntityTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Entity":null
        </#if>
    <#elseif res.Surname??>
        <#assign optTemp = .get_optional_template('ResidentIndividualTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Individual":null
        </#if>
    <#else>
        "Individual":{ "Message":"Defaulted due to no Resident.[Entity/Individual/Exception] obj" }
    </#if>
<#else>
    "Individual":{ "Message":"Defaulted due to no Resident obj" }
</#if>
}