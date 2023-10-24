<#if MonetaryAmount??>
        [
            <#list MonetaryAmount as m>
                <#if m?counter gt 1>,</#if>
                {
                    "SequenceNumber": ${m?counter},
                    "DomesticValue": <#if m.DomesticValue??>"${m.DomesticValue!''}"<#else><#if m.RandValue??>"${m.RandValue!''}"<#else>null</#if></#if>,
                    "ForeignValue": <#if m.ForeignValue??>"${m.ForeignValue!''}"<#else>null</#if>,
                    "LocationCountry": <#if m.LocationCountry??>"${m.LocationCountry!''}"<#else><#if LocationCountry??>"${LocationCountry!''}"<#else>null</#if></#if>,
                    "MoneyTransferAgentIndicator": <#if m.MoneyTransferAgentIndicator??>"${m.MoneyTransferAgentIndicator!'AD'}"<#else><#if MoneyTransferAgentIndicator??>"${MoneyTransferAgentIndicator!'AD'}"<#else>"AD"</#if></#if>,

                    "CategoryCode" : <#if m.CategoryCode??>"${m.CategoryCode!''}"<#else>null</#if>,
                    "Category" : <#if m.Category??>"${m.Category!''}"<#else>null</#if>,
                    "CategorySubCode" : <#if m.CategorySubCode??>"${m.CategorySubCode!''}"<#else>null</#if>,
                    "LoanRefNumber" : <#if m.LoanRefNumber??>"${m.LoanRefNumber!''}"<#else>null</#if>,

                    "RegulatorAuth": {
                        "RulingsSection" : <#if m.RulingsSection??>"${m.RulingsSection!''}"<#else>null</#if>,
                        "AuthFacilitator" : <#if m.AuthFacilitator??>"${m.AuthFacilitator!''}"<#else>null</#if>,
                        "AuthIssuer" : <#if m.AuthIssuer??>"${m.AuthIssuer!''}"<#else>null</#if>,
                        "REInternalAuthNumber" : <#if m.REInternalAuthNumber??>"${m.REInternalAuthNumber!''}"<#else>null</#if>,
                        "REInternalAuthNumberDate" : <#if m.REInternalAuthNumberDate??>"${m.REInternalAuthNumberDate!''}"<#else>null</#if>
                    }
                }
            </#list>
        ]
<#else>
        [
            {
              "DomesticValue": <#if TotalDomesticValue??>"${TotalDomesticValue!''}"<#else>null</#if>,
              "ForeignValue": <#if TotalForeignValue??>"${TotalForeignValue!''}"<#else><#if Flow?? && Flow?upper_case?starts_with('IN')>${drAmount!''}<#else>${crAmount!''}</#if></#if>,

              "LocationCountry": <#if Flow?? && Flow?upper_case?starts_with('IN')><#if drBIC??>"${drBIC[4..5]}"<#else>null</#if><#else><#if crBIC??>"${crBIC[4..5]}"<#else>null</#if></#if>,
              "MoneyTransferAgentIndicator": "${MAMoneyTransferAgentIndicator!'AD'}",

              "CategoryCode" : ${MACategoryCode!'null'},
              "Category" : ${MACategory!'null'},
              "CategorySubCode" : ${MACategorySubCode!'null'},

              "LoanInterestRate": null,
              "LoanRefNumber": null,
              "LoanTenor": null,

              "RegulatorAuth": null,

              "AdHocRequirement": null,

              "ThirdParty": ${MAThirdParty!'null'},

              "ImportExport": ${MAImportExport!'null'},

              "ReversalTrnRefNumber": null,
              "ReversalTrnSeqNumber": null,

              "StrateRefNumber": null
            }
        ]
</#if>