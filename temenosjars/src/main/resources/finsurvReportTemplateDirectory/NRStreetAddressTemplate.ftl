{
    "AddressLine1": <#if nres.StreetAddress.AddressLine1??>"${nres.StreetAddress.AddressLine1!''}"<#else>null</#if>,
    "AddressLine2": <#if nres.StreetAddress.AddressLine2??>"${nres.StreetAddress.AddressLine2!''}"<#else>null</#if>,
    "Suburb": <#if nres.StreetAddress.Suburb??>"${nres.StreetAddress.Suburb!''}"<#else>null</#if>,
    "City": <#if nres.StreetAddress.City??>"${nres.StreetAddress.City!''}"<#else>null</#if>,
    "PostalCode": <#if nres.StreetAddress.PostalCode??>"${nres.StreetAddress.PostalCode!''}"<#else>null</#if>,
    "State": <#if nres.StreetAddress.State??>"${nres.StreetAddress.State!''}"<#else>null</#if>,
    "Country": <#if nres.StreetAddress.Country??>"${nres.StreetAddress.Country!''}"<#else>null</#if>
}