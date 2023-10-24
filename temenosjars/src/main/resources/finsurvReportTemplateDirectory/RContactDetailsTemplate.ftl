{
  "ContactName": <#if res.ContactDetails.ContactName??>"${res.ContactDetails.ContactName!''}"<#else>null</#if>,
  "ContactSurname": <#if res.ContactDetails.ContactSurname??>"${res.ContactDetails.ContactSurname!''}"<#else>null</#if>,
  "Email": <#if res.ContactDetails.Email??>"${res.ContactDetails.Email!''}"<#else>null</#if>,
  "Fax": <#if res.ContactDetails.Fax??>"${res.ContactDetails.Fax!''}"<#else>null</#if>,
  "Telephone": <#if res.ContactDetails.Telephone??>"${res.ContactDetails.Telephone!''}"<#else>null</#if>
}