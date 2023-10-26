    {
      "AddressLine1": <#if res.PostalAddress.AddressLine1??>"${res.PostalAddress.AddressLine1!''}"<#else>null</#if>,
      "AddressLine2": <#if res.PostalAddress.AddressLine2??>"${res.PostalAddress.AddressLine2!''}"<#else>null</#if>,
      "Suburb": <#if res.PostalAddress.Suburb??>"${res.PostalAddress.Suburb!''}"<#else>null</#if>,
      "City": <#if res.PostalAddress.City??>"${res.PostalAddress.City!''}"<#else>null</#if>,
      "PostalCode": <#if res.PostalAddress.PostalCode??>"${res.PostalAddress.PostalCode!''}"<#else>null</#if>,
      "Province": <#if res.PostalAddress.Province??>"${res.PostalAddress.Province!''}"<#else>null</#if>
    }