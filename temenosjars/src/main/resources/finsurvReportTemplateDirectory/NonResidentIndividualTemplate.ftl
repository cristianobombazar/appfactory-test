
    "Individual": {
        "Name":
            <#if nres.Name??>
                "${nres.Name!''}"
            <#else>
                null
            </#if>,
        "Surname":
            <#if nres.Surname??>
                "${nres.Surname!''}"
            <#else>
                null
            </#if>,
        "Gender":
            <#if nres.Gender??>
                "${nres.Gender!''}"
            <#else>
                null
            </#if>,
        "AccountIdentifier":
            <#if nres.AccountIdentifier??>
                "${nres.AccountIdentifier!''}"
            <#else>
                null
            </#if>,
        "AccountName":
            <#if nres.AccountName??>
                "${nres.AccountName!''}"
            <#else>
                null
            </#if>,
        "AccountNumber":
            <#if nres.AccountNumber??>
                "${nres.AccountNumber!''}"
            <#else>
                null
            </#if>,

        "PassportCountry":
            <#if nres.PassportCountry??>
                "${nres.PassportCountry!''}"
            <#else>
                null
            </#if>,
        "PassportNumber":
            <#if nres.PassportNumber??>
                "${nres.PassportNumber!''}"
            <#else>
                null
            </#if>,

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
