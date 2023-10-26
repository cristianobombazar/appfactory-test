 {
      "AddressLine1": <#if res.StreetAddress.AddressLine1??>"${res.StreetAddress.AddressLine1!''}"<#else>null</#if>,
      "AddressLine2": <#if res.StreetAddress.AddressLine2??>"${res.StreetAddress.AddressLine2!''}"<#else>null</#if>,
      "Suburb": <#if res.StreetAddress.Suburb??>"${res.StreetAddress.Suburb!''}"<#else>null</#if>,
      "City": <#if res.StreetAddress.City??>"${res.StreetAddress.City!''}"<#else>null</#if>,
      "PostalCode": <#if res.StreetAddress.PostalCode??>"${res.StreetAddress.PostalCode!''}"<#else>null</#if>,
      "Province": <#if res.StreetAddress.Province??>"${res.StreetAddress.Province!''}"<#else>null</#if>
 }