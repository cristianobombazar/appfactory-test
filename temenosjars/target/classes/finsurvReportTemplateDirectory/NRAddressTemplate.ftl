{
    "AddressLine1":
        <#if nres.Address.AddressLine1??>
            "${nres.Address.AddressLine1!''}"
        <#else>
            null
        </#if>,
    "AddressLine2":
        <#if nres.Address.AddressLine2??>
            "${nres.Address.AddressLine2!''}"
        <#else>
            null
        </#if>,
    "Suburb":
        <#if nres.Address.Suburb??>
            "${nres.Address.Suburb!''}"
        <#else>
            null
        </#if>,
    "City":
        <#if nres.Address.City??>
            "${nres.Address.City!''}"
        <#else>
            null
        </#if>,
    "PostalCode":
        <#if nres.Address.PostalCode??>
            "${nres.Address.PostalCode!''}"
        <#else>
            null
        </#if>,
    "State":
        <#if nres.Address.State??>
            "${nres.Address.State!''}"
        <#else>
            null
        </#if>,
    "Country":
        <#if nres.Address.Country??>
            "${nres.Address.Country!''}"
        <#else>
            null
        </#if>
}