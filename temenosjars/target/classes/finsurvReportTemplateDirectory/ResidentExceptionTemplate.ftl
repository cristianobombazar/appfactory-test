
  "Exception": {
    "ExceptionName":
        <#if res.ExceptionName??>"${res.ExceptionName!''}"
        <#elseif (Evaluation.Evaluations?? && Evaluation.Evaluations[0]?? && Evaluation.Evaluations[0].ResException??)>
            "${Evaluation.Evaluations[0].ResException}"
        <#else>
            null
        </#if>,
    "Country":
        <#if res.StreetAddress?? && res.StreetAddress.Country??>
            "${res.StreetAddress.Country!''}"
        <#else>
            null
        </#if>
  }

