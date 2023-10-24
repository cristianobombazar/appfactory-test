
        "Individual": {
            "Name": <#if res.Name??>"${res.Name!''}"<#else>null</#if>,
            "Surname": <#if res.Surname??>"${res.Surname!''}"<#else>null</#if>,
            "Gender": <#if res.Gender??>"${res.Gender!''}"<#else>null</#if>,
            "DateOfBirth": <#if res.DateOfBirth??>"${res.DateOfBirth!''}"<#else>null</#if>,
            "IDNumber": <#if res.IDNumber??>"${res.IDNumber!''}"<#else>null</#if>,

            "AccountIdentifier": <#if res.AccountIdentifier??>"${res.AccountIdentifier!''}"<#else>null</#if>,
            "AccountName": <#if res.AccountName??>"${res.AccountName!''}"<#else>null</#if>,
            "AccountNumber": <#if res.AccountNumber??>"${res.AccountNumber!''}"<#else>null</#if>,

            "CustomsClientNumber": <#if res.CustomsClientNumber??>"${res.CustomsClientNumber!''}"<#else>null</#if>,
            "ForeignIDCountry": <#if res.ForeignIdCountry??>"${res.ForeignIdCountry!''}"<#else>null</#if>,
            "ForeignIDNumber": <#if res.ForeignIdNumber??>"${res.ForeignIdNumber!''}"<#else>null</#if>,
            "PassportCountry": <#if res.PassportCountry??>"${res.PassportCountry!''}"<#else>null</#if>,
            "PassportNumber": <#if res.PassportNumber??>"${res.PassportNumber!''}"<#else>null</#if>,

            "TaxNumber": <#if res.TaxNumber??>"${res.TaxNumber!''}"<#else>null</#if>,
            "TempResPermitNumber": <#if res.TempResPermitNumber??>"${res.TempResPermitNumber!''}"<#else>null</#if>,
            "VATNumber": <#if res.VatNumber??>"${res.VatNumber!''}"<#else>null</#if>,

            "TaxClearanceCertificateIndicator": <#if res.TaxClearanceCertificateIndicator??>"${res.TaxClearanceCertificateIndicator!''}"<#else>null</#if>,
            "TaxClearanceCertificateReference": <#if res.TaxClearanceCertificateReference??>"${res.TaxClearanceCertificateReference!''}"<#else>null</#if>,

            "SupplementaryCardIndicator": <#if res.SupplementaryCardIndicator??>"${res.SupplementaryCardIndicator!''}"<#else>null</#if>,
            "CardNumber": <#if res.CardNumber??>"${res.CardNumber!''}"<#else>null</#if>,

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
            </#if>
            ,
            "PostalAddress":
                <#if res??>
                    <#if res.PostalAddress??>
                      <#assign optTemp = .get_optional_template('RPostalAddressTemplate.ftl')>
                      <#if optTemp.exists>
                        <@optTemp.include />
                      <#else>
                        null
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
                <#else>
                    null
                </#if>
              <#else>
                null
              </#if>
            <#else>
              null
            </#if>
        }

