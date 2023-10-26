
  "Entity": {
    "AccountIdentifier": <#if nres.AccountIdentifier??>"${nres.AccountIdentifier!''}"<#else>null</#if>,
    "AccountName": <#if nres.AccountName??>"${nres.AccountName!''}"<#else>null</#if>,
    "AccountNumber": <#if nres.AccountNumber??>"${nres.AccountNumber!''}"<#else>null</#if>,
    "EntityName": <#if nres.EntityName??>"${nres.EntityName!''}"<#else>null</#if>,
    "Address":
       <#if nres??>
         <#if nres.Address??>
           <#assign optTemp = .get_optional_template('NRAddressTemplate.ftl')>
           <#if optTemp.exists>
             <@optTemp.include />
           <#else>
              null
           </#if>
         <#else>
             <#if nres.StreetAddress??>
               <#assign optTemp = .get_optional_template('NRStreetAddressTemplate.ftl')>
               <#if optTemp.exists>
                 <@optTemp.include />
               <#else>
                  null
               </#if>
             <#else>
                null
             </#if>
         </#if>
       <#else>
          null
       </#if>
  }

