
    "Entity": {
        "AccountIdentifier": <#if res.AccountIdentifier??>"${res.AccountIdentifier!''}"<#else>null</#if>,
        "AccountName": <#if res.AccountName??>"${res.AccountName!''}"<#else>null</#if>,
        "AccountNumber": <#if res.AccountNumber??>"${res.AccountNumber!''}"<#else>null</#if>,
        "CardNumber": <#if res.CardNumber??>"${res.CardNumber!''}"<#else>null</#if>,

        "CustomsClientNumber": <#if res.CustomsClientNumber??>"${res.CustomsClientNumber!''}"<#else>null</#if>,

        "EntityName": <#if res.EntityName??>"${res.EntityName!''}"<#else>null</#if>,
        "TradingName": <#if res.TradingName??>"${res.TradingName!''}"<#else>null</#if>,
        "RegistrationNumber": <#if res.RegistrationNumber??>"${res.RegistrationNumber!''}"<#else>null</#if>,
        "IndustrialClassification": <#if res.IndustrialClassification??>"${res.IndustrialClassification!''}"<#else>null</#if>,
        "InstitutionalSector": <#if res.InstitutionalSector??>"${res.InstitutionalSector!''}"<#else>null</#if>,
        "SupplementaryCardIndicator": <#if res.SupplementaryCardIndicator??>"${res.SupplementaryCardIndicator!''}"<#else>null</#if>,

        "TaxClearanceCertificateIndicator": <#if res.TaxClearanceCertificateIndicator??>"${res.TaxClearanceCertificateIndicator!''}"<#else>null</#if>,
        "TaxClearanceCertificateReference": <#if res.TaxClearanceCertificateReference??>"${res.TaxClearanceCertificateReference!''}"<#else>null</#if>,

        "TaxNumber": <#if res.TaxNumber??>"${res.TaxNumber!''}"<#else>null</#if>,
        "VATNumber": <#if res.VATNumber??>"${res.VATNumber!''}"<#else>null</#if>,
        "StreetAddress":
          <#if res??>
            <#if res.StreetAddress??>
              <#assign optTemp = .get_optional_template('RStreetAddressTemplate.ftl')>
              <#if optTemp.exists>
                <@optTemp.include />
              </#if>
            <#else>
            null
            </#if>
          <#else>
           null
              </#if>
              ,
        "PostalAddress":
          <#if res??>
            <#if res.PostalAddress??>
              <#assign optTemp = .get_optional_template('RPostalAddressTemplate.ftl')>
              <#if optTemp.exists>
                <@optTemp.include />
              </#if>
            <#else>
             null
            </#if>
          <#else>
           null
          </#if>
        ,
        "ContactDetails":
            <#if res??>
              <#if res.ContactDetails??>
                <#assign optTemp = .get_optional_template('RContactDetailsTemplate.ftl')>
                <#if optTemp.exists>
                  <@optTemp.include />
                </#if>
              <#else>
                null
              </#if>
             <#else>
             null
            </#if>

    }

