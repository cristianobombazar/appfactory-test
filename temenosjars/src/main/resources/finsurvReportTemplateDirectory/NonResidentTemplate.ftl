{
<#if nres??>
  <#if nres.ExceptionName?? || (Evaluation.Evaluations?? && Evaluation.Evaluations[0]?? && Evaluation.Evaluations[0].NonResException??)>
        <#assign optTemp = .get_optional_template('NonResidentExceptionTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Exception":null
        </#if>
  <#elseif nres.EntityName??>
        <#assign optTemp = .get_optional_template('NonResidentEntityTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Entity":null
        </#if>
  <#elseif nres.Surname??>
        <#assign optTemp = .get_optional_template('NonResidentIndividualTemplate.ftl')>
        <#if optTemp.exists>
          <@optTemp.include />
        <#else>
            "Individual":null
        </#if>
  <#else>
        "Individual":{ "Message":"Defaulted due to no NonResident.[Entity/Individual/Exception] obj" }
  </#if>
<#else>
    "Individual":{ "Message":"Defaulted due to no NonResident obj" }
</#if>
}