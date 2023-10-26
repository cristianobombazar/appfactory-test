
  "Exception": {
    "ExceptionName":
        <#if nres.ExceptionName??>
            "${nres.ExceptionName!''}"
        <#elseif (Evaluation.Evaluations?? && Evaluation.Evaluations[0]?? && Evaluation.Evaluations[0].NonResException??)>
            "${Evaluation.Evaluations[0].NonResException}"
        <#else>
            null
        </#if>,
    "Country":
        <#if nres.StreetAddress?? && nres.StreetAddress.Country??>
            "${nres.StreetAddress.Country!''}"
        <#else>
            null
        </#if>
  }

