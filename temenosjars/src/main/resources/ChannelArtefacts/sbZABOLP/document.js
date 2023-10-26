define(function () {
 return function(predef) {
  var v1;
  var v2;
  var v3;
  with (predef) {
    v1 = {
      ruleset: "Transaction Documents", 
      scope: "transaction", 
      validations: [
        {
          field: ["Resident.Individual.TaxClearanceCertificateReference", "Resident.Entity.TaxClearanceCertificateReference"], 
          rules: [
            document("dtcc1", "TaxClearance", "Provide scan of the Tax Clearance certificate", notEmpty).onOutflow().onSection("AB").onCategory(["512", "513"])
          ]
        },
        {
          field: "Resident.Individual.IDNumber", 
          rules: [
            document("drid1", "ID_Document", "In the case where the ID number fails validation provide scan of ID Document", notEmpty.and(notValidRSAID)).onSection("AB").notOnCategory("833")
          ]
        },
        {
          field: "Resident.Individual.TempResPermitNumber", 
          rules: [
            document("drtp1", "Temporary_Resident_Permit", "Provide scan of Temporary Resident Permit", notEmpty).onSection("AB").notOnCategory("833")
          ]
        },
        {
          field: "Resident.Individual.ForeignIDNumber", 
          rules: [
            document("drfi1", "Foreign_ID_Document", "Provide scan of Foreign ID Document", notEmpty).onSection("AB").notOnCategory("833")
          ]
        },
        {
          field: "Resident.Individual.PassportNumber", 
          rules: [
            document("dripn1", "PassportDocument", "Provide scan of Passport Document for {{value}} - {{Resident.Individual.Name}} {{Resident.Individual.Surname}}", notEmpty.and(not(hasAllMoneyField("ThirdParty.Individual.PassportNumber")))).onSection("AB").onCategory("256"),
            document("dripn2", "ProofOfTravel", "Provide scan of air ticket or other travel ticket for {{Resident.Individual.Name}} {{Resident.Individual.Surname}}", notEmpty.and(not(hasAllMoneyField("ThirdParty.Individual.PassportNumber")))).onSection("AB").onCategory("256")
          ]
        },
        {
          field: "NonResident.Individual.PassportNumber", 
          rules: [
            document("dnripn1", "PassportDocument", "Provide scan of Passport Document {{value}} - {{NonResident.Individual.Name}} {{NonResident.Individual.Surname}}", notEmpty).onSection("AB").onCategory(["304", "305", "306", "409"])
          ]
        }
      ]
    };
    v2 = {
      ruleset: "Money Documents", 
      scope: "money", 
      validations: [
        {
          field: "SequenceNumber", 
          rules: [
            document("dmsned1", "ExportDeclaration", "A filled-in bank supplied export declaration", notEmpty).onSection("A").onInflow().onCategory("109"),
            document("dmsnci1", "ClientInvoice", "The client invoice issued to the buyer", notEmpty).onOutflow().onSection("AB").onCategory(["110"]),
            document("dmsnpf1", "ProofOfFunds", "Proof that funds have been received or confirmation/commitment that funds will be received", notEmpty).onOutflow().onSection("AB").onCategory(["110", "510/02"]),
            document("dmsnpf3", "ProofOfFunds", "Proof that funds have been received or confirmation/commitment that funds will be received", notEmpty).onInflow().onSection("AB").onCategory(["110"]),
            document("dmsnpf2", "ProofOfFunds", "Proof of introduction of non resident funds", notEmpty).onOutflow().onSection("AB").onCategory(["510/01", "510/02", "503", "416", "417", "601/02", "603/02"]),
            document("dmsna1", "Agreement", "The agreement bewtween the buyer and the supplier", notEmpty).onSection("AB").onCategory(["110"]),
            document("dmsna2", "Agreement", "Sales Agreement", notEmpty).onOutflow().onSection("AB").onCategory(["504", "503", "510/01", "601/02"]),
            document("dmsnsi1", "SupplierInvoice", "Payments for imports require the client to provide a SupplierInvoice", notEmpty).onOutflow().onSection("AB").onCategory(["108", "109"]),
            document("dmsnsi1b", "SupplierInvoice", "Payments for imports require the client to provide a SupplierInvoice", notEmpty.and(not(importUndertakingClient))).onOutflow().onSection("AB").onCategory(["106"]),
            document("dmsnsi2", "SupplierInvoice", "Supplier Invoice for the charges", notEmpty).onSection("AB").onCategory(["110", "201", "202", "203", "204", "205", "230", "231", "232", "233", "234", "235", "236", "240", "250", "251", "265", "266", "270", "271", "273", "274", "275", "276", "280", "281", "282", "285", "287", "288", "289", "290", "291", "292", "293", "294", "295", "296", "297", "307", "312", "402", "408"]),
            document("dmsnec1", "EnrollmentConfirmation", "Enrollment confirmation from Educational Institution", notEmpty).onCategory(["285"]),
            document("dmsnsi2", "SupplierInvoice", "The invoice for the service", notEmpty).onInflow().onCategory(["281", "282", "287"]),
            document("dmsnin1", "ImportInvoice", "Copy of Commercial Invoice related to the Import transaction (reflecting clearing agent, forwarder or carrier)", notEmpty).onOutflow().onSection("AB").onCategory(["271", "272"]),
            document("dmsnei1", "ExportInvoice", "Copy of Commercial Invoice related to the Export transaction (reflecting clearing agent, forwarder or carrier)", notEmpty).onInflow().onSection("AB").onCategory(["272", "271"]),
            document("dmsnsi2", "SupplierInvoice", "The invoice for the service", notEmpty).onOutflow().onCategory(["265", "266", "270", "273", "275", "276", "280", "288", "289", "290", "291", "292", "293", "294", "295", "296", "297"]),
            document("dmsnsi3", "SupplierInvoice", "The invoice for the acquisition", notEmpty).onSection("AB").onCategory(["210", "211", "212", "213", "225", "241", "242", "243"]),
            document("dmsnsi4", "SupplierInvoice", "The invoice for the charges", notEmpty).onSection("AB").onCategory(["220", "221", "226", "411"]),
            document("dmsnsi5", "SupplierInvoice", "The invoice for the goods", notEmpty).onSection("AB").onCategory(["272"]),
            document("dmsnsi5", "SupplierInvoice", "The invoice reflecting the income tax", notEmpty).onSection("AB").onCategory(["412"]),
            document("dmsnsi6", "SupplierInvoice", "The invoice, statement, bordereau, slip or debit note reflecting the details of the insurance transacted and premiums payable", notEmpty).onSection("AB").onCategory(["412", "414"]),
            document("dmsnsi7", "SupplierInvoice", "The invoice reflecting the details of the insurance transacted and premiums payable", notEmpty).onSection("AB").onCategory(["413", "415"]),
            document("dmsnct1", "Contract", "The contract for the transaction", notEmpty).onSection("AB").onCategory(["201", "202", "203", "204", "205", "210", "211", "212", "213", "220", "221", "225", "226", "230", "232", "233", "234", "235", "240", "241", "242", "243"]),
            document("dmsnct2", "Contract", "The contract for the service", notEmpty).onCategory(["265", "266", "276", "288", "289", "290", "291", "292", "293", "295", "297", "303", "304", "281"]),
            document("dmsnai1", "AgentInvoice", "The local agent invoice containing name and residential address of local or foreign travelers", notEmpty).onOutflow().onCategory(["260", "261"]),
            document("dmsnfi1", "ForeignInvoice", "Invoice or Pro forma invoice issued by foreign firm represented by the agent", notEmpty).onOutflow().onCategory(["260", "261"]),
            document("dmsnsc1", "ShareCertificate", "Copy(ies) of \u2018Non-resident\u2019 endorsed share certificate", notEmpty).onOutflow().onCategory(["301", "601/02"]),
            document("dmsnal1", "AuditorLetter", "That, if the company is 75% or more foreign owned (affected entity), the income distribution will not cause the entity to be placed in an over borrowed position in terms of Section I.1 of the Rulings", notEmpty).onOutflow().onCategory(["301", "302"]),
            document("dmsnal2", "AuditorLetter", "Auditor's Letter", notEmpty).onOutflow().onCategory(["601/02"]),
            document("dmsnbr1", "BoardResolution", "A resolution by Board of Directors declaring dividend", notEmpty).onOutflow().onCategory(["301", "302"]),
            document("dnsnpe1", "ProofOfEarnings", "Proof of earnings (Employment contract / salary advice)", notEmpty).onOutflow().onCategory(["305", "306"]),
            document("dnsndf1", "FTRDeclaration", "Foreigner Temporary Resident declaration", notEmpty).onOutflow().onCategory(["305", "306"]),
            document("dnsnra1", "RentalAgreement", "Copy of the rental agreement", notEmpty).onOutflow().onCategory(["308"]),
            document("dnsnic1", "InvoiceOrCalculations", "A fully descriptive Invoice or calculation of interest amount", notEmpty).onCategory(["309"]),
            document("dmsndc1", "DeathCertificate", "A scan of the death certificate", notEmpty).onCategory(["409"]),
            document("dmsnw1", "Will", "Last will and testament", notEmpty).onCategory(["409"]),
            document("dmsnel1", "ExecutorLetter", "Letters of Executor", notEmpty).onCategory(["409"]),
            document("dmsnco1", "CourtOrder", "Court Order or Letter from Civic Official", notEmpty).onCategory(["410"]),
            document("dmsnfa1", "FSBApproval", "Financial Service Board Approval", notEmpty).onCategory(["412", "413", "414", "415"]),
            document("dmsncs1", "ClaimSupport", "Claim supporting documents", notEmpty).onCategory(["413", "415"]),
            document("dmsnp1", "Policy", "A copy of policy", notEmpty).onCategory(["413", "415"]),
            document("dmsnpl1", "PINLetter", "SARS PIN Letter", notEmpty.and(notResidentFieldValue("TaxClearanceCertificateIndicator", "N"))).onOutflow().onCategory(["512", "513"]),
            document("dmsntc1", "TaxClearance", "Provide scan of the Tax Clearance certificate", notEmpty.and(hasResidentFieldValue("TaxClearanceCertificateIndicator", "Y"))).onOutflow().onCategory(["512/04"]),
            document("dmsntd1", "Transaction_Details_Document", "Documentation providing details of transaction", notEmpty).onOutflow().onCategory(["510/02"]),
            document("dmsnfv1", "FairValue", "Certificate of fair value", notEmpty).onOutflow().onCategory(["510/01", "503", "603/02"]),
            document("dmsnsa1", "StatementOfAccount", "Attorney's statement of account", notEmpty).onOutflow().onCategory(["510/01"]),
            document("dmsnbn1", "BrokerNote", "Broker's note", notEmpty).onOutflow().onCategory(["601/01", "603/02"]),
            document("dmsntd2", "TitleDocument", "Document of title", notEmpty).onOutflow().onCategory(["603/02"]),
            document("dmsnfsb1", "FSBCertificate", "FSB Certifcate", notEmpty).onOutflow().onCategory(["615", "701", "702", "703", "704", "705"]),
            document("dmsnaa1", "AssetAllocation", "Confirmation of last quarterly asset allocation", notEmpty).onOutflow().onCategory(["615", "701", "702", "703", "704", "705"]),
            document("dmsnsd1", "SupportDocumentation", "Supporting Documentation", notEmpty).onOutflow().onCategory(["830"]),
            document("dmsnadla1", "ADLASettlement", "ADLA settlement details", notEmpty).onOutflow().onCategory(["833"])
          ]
        },
        {
          field: "LoanRefNumber", 
          rules: [
            document("dlnr1", "Loan_Approval", "Exchange Control Approval for the loan", notEmpty).onSection("AB").onCategory(["801", "802", "803", "804"]),
            document("dlnr2", "Loan_Approval", "Exchange Control Approval for the loan", notEmpty).onSection("AB").onOutflow().onCategory(["309/04", "309/05", "309/06", "309/07"]),
            document("dlnr2b", "Loan_Approval", "Exchange Control Approval for the loan", notEmpty.and(not(importUndertakingClient))).onSection("AB").onOutflow().onCategory(["106"])
          ]
        },
        {
          field: "ReversalTrnRefNumber", 
          rules: [
            document("drtrn1", "OriginalTransaction", "Provide a copy of the Original Transaction", notEmpty).onSection("AB").onCategory(["100", "200", "300", "400", "500", "600", "700", "800"])
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber", 
          rules: [
            document("drar1", "RegulatorApproval", "A scan of the {{RegulatorPrefix}} Auth Reference", notEmpty).onSection("ABG").onCategory(["203", "265", "243", "241", "242", "281", "243", "240", "266", "276", "288", "289", "290", "291", "292", "293", "295", "297", "303", "309", "403", "404", "405", "406", "407", "408", "411", "412", "413", "414", "415", "501", "502", "504", "511", "530", "605", "616", "610", "611", "612", "616", "701", "702", "703", "704", "705", "801", "802", "803", "804", "810", "816", "817", "818", "819"]),
            document("drar1b", "RegulatorApproval", "A scan of the {{RegulatorPrefix}} Auth Reference", notEmpty.and(not(importUndertakingClient))).onSection("ABG").onCategory(["106"])
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", 
          rules: [
            document("dmba1", "BankApproval", "A scan of the Bank's issued reference", notEmpty).onSection("ABG").onCategory(["610", "611", "612", "616", "701", "702", "703", "704", "705"])
          ]
        },
        {
          field: "ThirdParty.Individual.IDNumber", 
          rules: [
            document("dtpid1", "ID_Document", "In the case where the ID number fails validation provide scan of ID Document", notEmpty.and(notValidRSAID)).onSection("AB").notOnCategory("833")
          ]
        },
        {
          field: "ThirdParty.Individual.TempResPermitNumber", 
          rules: [
            document("dtptp1", "Temporary_Resident_Permit", "Provide scan of Temporary Resident Permit", notEmpty).onSection("AB").notOnCategory("833")
          ]
        },
        {
          field: "ThirdParty.Individual.PassportNumber", 
          rules: [
            document("dtppn1", "PassportDocument", "Provide scan of Passport Document {{value}} - {{ThirdParty.Individual.Name}} {{ThirdParty.Individual.Surname}}", notEmpty).onSection("AB").onCategory("256"),
            document("dtppn2", "PassportDocument", "Provide scan of Passport Document {{value}} - {{ThirdParty.Individual.Name}} {{ThirdParty.Individual.Surname}}", notEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory("255"),
            document("dtppn3", "ProofOfTravel", "Provide scan of air ticket or other travel ticket for {{ThirdParty.Individual.Name}} {{ThirdParty.Individual.Surname}}", notEmpty).onSection("AB").onCategory("256"),
            document("dtppn4", "ProofOfTravel", "Provide scan of air ticket or other travel ticket for {{ThirdParty.Individual.Name}} {{ThirdParty.Individual.Surname}}", notEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory("255")
          ]
        },
        {
          field: "NonResident.Individual.PassportNumber", 
          rules: [
            document("dnrpn1", "PassportDocument", "Provide scan of Passport Document {{value}} - {{NonResident.Individual.Name}} {{NonResident.Individual.Surname}}", notEmpty).onCategory(["304", "305", "409"])
          ]
        }
      ]
    };
    v3 = {
      ruleset: "Import/Export Documents", 
      scope: "importexport", 
      validations: [
        {
          field: "ImportControlNumber", 
          rules: [
            document("dicn1", "SupplierInvoice", "Advance payments for imports require the client to provide a SupplierInvoice", notEmpty).onOutflow().onSection("ABG").onCategory("101"),
            document("dicn2", "SupplierInvoice", "Payments for imports require the client to provide a SupplierInvoice", notEmpty).onOutflow().onSection("ABG").onCategory(["103", "105", "108", "109"]),
            document("dicn3", "CustomsDeclaration", "SARS Customs Declaration bearing the MRN (EDI / SAD 500)", notEmpty).onOutflow().onSection("ABG").onCategory(["103"]),
            document("dicn4", "FinancierInvoice", "When using a trade financier the Trade Financier invoice must be provided", notEmpty.and(not(importUndertakingClient))).onSection("ABG").onCategory(["106"]),
            document("dicn5", "SupplierInvoice", "Payments for imports require the client to provide a SupplierInvoice", notEmpty.and(not(importUndertakingClient))).onOutflow().onSection("ABG").onCategory(["106"])
          ]
        },
        {
          field: "TransportDocumentNumber", 
          rules: [
            document("dtdn1", "TransportDocument", "Transport Documentation evidencing transportation of goods to SA", notEmpty.and(not(importUndertakingClient))).onOutflow().notOnCategory("103/11").onCategory(["103", "106"]).onSection("ABG"),
            document("dtdn2", "TransportDocument", "Transport Documentation evidencing transportation of goods to SA", notEmpty.and(not(importUndertakingClient)).and(notMoneyField("{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber"))).onOutflow().onCategory(["106"]).onSection("ABG")
          ]
        },
        {
          field: "UCR", 
          rules: [
            document("ducr1", "ConsignmentReference", "Proof of consignment reference required for exports", notEmpty).onSection("ABG").onInflow().notOnCategory(["101/11", "103/11"]).onCategory(["101", "103", "105"]),
            document("ducr1b", "ConsignmentReference", "Proof of consignment reference required for exports", notEmpty.and(not(importUndertakingClient))).onSection("ABG").onInflow().notOnCategory(["101/11", "103/11"]).onCategory(["106"])
          ]
        },
        {
          field: "PaymentValue", 
          rules: [
            document("dtsupplier", "Invoice_from_Supplier", "Please provide an invoice from the supplier", returnTrue).onOutflow().onSection("ABG").onCategory("110"),
            document("dtclient", "Invoice_to_buyer_from_client", "Please provide an invoice to the buyer", returnTrue).onOutflow().onSection("ABG").onCategory("110"),
            document("dtintrooffunds", "Proof_of_introduction_of_funds", "Please provide proof of introduction of funds", returnTrue).onOutflow().onSection("ABG").onCategory("110"),
            document("dtsarb", "SARB_Approval", "Please provide SARB approval for the transaction", returnTrue).onOutflow().onSection("ABG").onCategory(["110"]),
            document("dtsarbb", "SARB_Approval", "Please provide SARB approval for the transaction", not(importUndertakingClient)).onOutflow().onSection("ABG").onCategory(["106"])
          ]
        }
      ]
    };
  }
  return {
    comment: 'sbZABOLP (1,0) [featureMTAAccounts, featureLimit, featureEntity511] -> sbZAFlow (1,0) -> stdSARB (1,0) [featureHOLDCO, featureMTAAccounts, featureSchema, featureEntity511, featureBranchHub] -> coreSARBExternal (1,0) -> coreSARB (1,0) -> coreSADC (1,0)',
    engine: {major: '1', minor: '0'},
    rules: [v1, v2, v3],
    mappings: {Locale: "ZA", LocalCurrencySymbol: "R", RegulatorPrefix: "CB", StateName: "Province", DealerPrefix: "RE", LocalValue: "DomesticValue", LocalCurrency: "ZAR", Regulator: "Regulator", LocalCurrencyName: "Local Currency", _maxLenErrorType: "ERROR", _lenErrorType: "ERROR", _minLenErrorType: "ERROR"}
  };
 };
})