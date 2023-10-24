define({
 comment: "sbZABOLP (1,0) -> sbZAFlow (1,0) -> stdSARB (1,0) -> coreSARBExternal (1,0) -> coreSARB (1,0) -> coreSADC (1,0)", 
 lookups: {
  provinces: ["GAUTENG", "LIMPOPO", "NORTH WEST", "WESTERN CAPE", "EASTERN CAPE", "NORTHERN CAPE", "FREE STATE", "MPUMALANGA", "KWAZULU NATAL"], 
  accountIdentifiersResident: ["RESIDENT OTHER", "CFC RESIDENT", "FCA RESIDENT", "CASH", "EFT", "CARD PAYMENT", "VOSTRO", "DEBIT CARD", "CREDIT CARD", "FX DEBIT CARD"], 
  accountIdentifiersNonResident: ["NON RESIDENT OTHER", "NON RESIDENT RAND", "NON RESIDENT FCA", "CASH", "FCA RESIDENT", "RES FOREIGN BANK ACCOUNT", "VOSTRO", "VISA NET", "MASTER SEND"], 
  AuthIssuers: [
   {
    value: "02", 
    label: "FIRSTRAND BANK LTD"
   },
   {
    value: "08", 
    label: "NEDBANK LTD"
   },
   {
    value: "13", 
    label: "MERCHANTILE BANK LTD"
   },
   {
    value: "14", 
    label: "CREDIT AGRICOLE INDOSUEZ"
   },
   {
    value: "15", 
    label: "SA BANK OF ATHENS"
   },
   {
    value: "16", 
    label: "STANDARD BANK OF SOUTH AFRICA"
   },
   {
    value: "23", 
    label: "RENNIES BANK LTD"
   },
   {
    value: "24", 
    label: "RESERVE BANK"
   },
   {
    value: "25", 
    label: "MEEG BANK LTD"
   },
   {
    value: "33", 
    label: "SOCIETE GENERALE"
   },
   {
    value: "39", 
    label: "INVESTEC BANK LTD"
   },
   {
    value: "40", 
    label: "NEDTRAVEL"
   },
   {
    value: "42", 
    label: "ABSA BANK LTD"
   },
   {
    value: "56", 
    label: "BANK OF TAIWAN SA BRANCH"
   },
   {
    value: "57", 
    label: "COMMERZBANK AKTIENGESELLSCHAFT"
   },
   {
    value: "58", 
    label: "CITIBANK NA SOUTH AFRICA"
   },
   {
    value: "60", 
    label: "BARCLAYS BANK OF SA LTD"
   },
   {
    value: "61", 
    label: "ABN AMRO BANK"
   },
   {
    value: "63", 
    label: "ING BANK N.V SA BRANCH"
   },
   {
    value: "64", 
    label: "HBZ BANK LTD"
   },
   {
    value: "66", 
    label: "HABIB OVERSEAS BANK LTD"
   },
   {
    value: "67", 
    label: "REGAL TREASURY PRIVATE BANK"
   },
   {
    value: "68", 
    label: "STATA BANK OF INDIA"
   },
   {
    value: "70", 
    label: "MASTER CURRENCY PTY LTD"
   },
   {
    value: "71", 
    label: "JP MORGAN CHASE BANK JHB"
   },
   {
    value: "74", 
    label: "AFRICAN MERCHANT BANK LTD"
   },
   {
    value: "75", 
    label: "BANK OF BARODA"
   },
   {
    value: "76", 
    label: "GENSEC BANK LTD"
   },
   {
    value: "78", 
    label: "TOWER BUREAU DE CHANGE PTY LTD"
   },
   {
    value: "79", 
    label: "REAL AFRICA DUROLINK INVESTMENT BK LTD"
   },
   {
    value: "80", 
    label: "CORPCAPITAL BANK LTD"
   },
   {
    value: "82", 
    label: "IMALI EXPRESS PTY LTD"
   },
   {
    value: "83", 
    label: "DEUTSCHE BANK AG JHB BRANCH"
   },
   {
    value: "84", 
    label: "FXAFRICA FOREIGN EXCHANGE PTY LTD"
   },
   {
    value: "85", 
    label: "PSG INVESTMENT BANK LTD"
   },
   {
    value: "86", 
    label: "INTER AFRICA BUREAU DE CHANGE PTY LTD"
   },
   {
    value: "87", 
    label: "GLOBAL FOREIGN EXCHANGE"
   },
   {
    value: "88", 
    label: "CHINA CONSTRUCTION BANK JHB BRANCH"
   },
   {
    value: "89", 
    label: "BANK OF CHINA JHB BRANCH"
   },
   {
    value: "91", 
    label: "STANDARD CHARTERED JOHANNESBURG"
   },
   {
    value: "92", 
    label: "HSBC"
   },
   {
    value: "98", 
    label: "DTI CERTIFICATE OF APPROVAL"
   },
   {
    value: "99", 
    label: "SARB IMPORTERS/EXPORTERS LETTERS OF UNDERTAKING"
   }
  ], 
  gender: ["M", "F"], 
  cardTypes: ["AMEX", "DINERS", "ELECTRON", "MAESTRO", "MASTER", "VISA", "BOCEXPRESS"], 
  nonResidentExceptions: ["MUTUAL PARTY", "BULK INTEREST", "BULK VAT REFUNDS", "BULK BANK CHARGES", "BULK PENSIONS", "FCA RESIDENT NON REPORTABLE", "CFC RESIDENT NON REPORTABLE", "VOSTRO NON REPORTABLE", "VOSTRO INTERBANK", "STRATE", "NOSTRO INTERBANK", "NOSTRO NON REPORTABLE", "RTGS NON REPORTABLE", "RTGS INTERBANK", "IHQ"], 
  ReportingQualifier: ["BOPCUS", "NON REPORTABLE", "BOPCARD RESIDENT", "BOPCARD NON RESIDENT", "NON RESIDENT RAND", "INTERBANK", "BOPDIR"], 
  res_types: ["Individual", "Entity", "Exception"], 
  Flow: ["IN", "OUT"], 
  yesNo: ["Y", "N"], 
  residentExceptions: ["MUTUAL PARTY", "RAND CHEQUE", "BULK PENSIONS", "NON RESIDENT RAND", "UNCLAIMED DRAFTS", "BULK INTEREST", "BULK DIVIDENDS", "BULK BANK CHARGES", "FCA NON RESIDENT NON REPORTABLE", "VOSTRO NON REPORTABLE", "VOSTRO INTERBANK", "NOSTRO NON REPORTABLE", "RTGS NON REPORTABLE", "RTGS INTERBANK", "NOSTRO INTERBANK", "STRATE"], 
  institutionalSectors: [
   {
    code: "01", 
    description: "FINANCIAL CORPORATE"
   },
   {
    code: "02", 
    description: "NON FINANCIAL CORPORATE"
   },
   {
    code: "03", 
    description: "GENERAL GOVERNMENT"
   },
   {
    code: "04", 
    description: "HOUSEHOLD"
   }
  ], 
  industrialClassifications: [
   {
    code: "01", 
    description: "Agriculture, hunting, forestry and fishing"
   },
   {
    code: "02", 
    description: "Mining and quarrying"
   },
   {
    code: "03", 
    description: "Manufacturing"
   },
   {
    code: "04", 
    description: "Electricity, gas and water supply"
   },
   {
    code: "05", 
    description: "Construction"
   },
   {
    code: "06", 
    description: "Wholesale and retail trade; repair of motor vehicles, motor cycles and personal and household goods; hotels and restaurants"
   },
   {
    code: "07", 
    description: "Transport, storage and communication"
   },
   {
    code: "08", 
    description: "Financial intermediation, insurance, real estate and business services"
   },
   {
    code: "09", 
    description: "Community, social and personal services"
   },
   {
    code: "10", 
    description: "Private households, exterritorial organisations, representatives of foreign governments and other activities not adequately defined"
   }
  ], 
  customsOffices: [
   {
    code: "ALX", 
    name: "ALEXANDERBAY"
   },
   {
    code: "BBR", 
    name: "BEIT BRIDGE"
   },
   {
    code: "BFN", 
    name: "BLOEMFONTEIN"
   },
   {
    code: "BIA", 
    name: "BLOEMFONTEIN AIRPORT"
   },
   {
    code: "CLP", 
    name: "CALEDONSPOORT"
   },
   {
    code: "CTN", 
    name: "CAPE TOWN"
   },
   {
    code: "DBN", 
    name: "DURBAN"
   },
   {
    code: "DFM", 
    name: "CAPE TOWN INTERNATIONAL AIRPORT"
   },
   {
    code: "ELN", 
    name: "EAST LONDON"
   },
   {
    code: "FBB", 
    name: "FICKSBURG BRIDGE"
   },
   {
    code: "GMR", 
    name: "GERMISTON/ALBERTON"
   },
   {
    code: "GOL", 
    name: "GOLELA"
   },
   {
    code: "GRB", 
    name: "GROBLERS BRIDGE"
   },
   {
    code: "HFV", 
    name: "PEZ INTER AIRPORT"
   },
   {
    code: "JHB", 
    name: "JOHANNESBURG"
   },
   {
    code: "JPR", 
    name: "JEPPES REEF"
   },
   {
    code: "JSA", 
    name: "O R TAMBO INTERNATIONAL AIRPORT"
   },
   {
    code: "KBY", 
    name: "KIMBERLEY"
   },
   {
    code: "KFN", 
    name: "KOPFONTEIN"
   },
   {
    code: "KOM", 
    name: "KOMATIPOORT"
   },
   {
    code: "LBA", 
    name: "KING SHAKA INT AIRPORT"
   },
   {
    code: "LSA", 
    name: "LANSERIA AIRPORT"
   },
   {
    code: "MAF", 
    name: "MAFEKING/MMABATHO"
   },
   {
    code: "MAH", 
    name: "MAHAMBA"
   },
   {
    code: "MAN", 
    name: "MANANGA"
   },
   {
    code: "MND", 
    name: "MANDENI"
   },
   {
    code: "MOS", 
    name: "MOSSEL BAY"
   },
   {
    code: "MSB", 
    name: "MASERU BRIDGE"
   },
   {
    code: "NAR", 
    name: "NAKOP (NAROGAS)"
   },
   {
    code: "NRS", 
    name: "NERSTON"
   },
   {
    code: "NSA", 
    name: "NELSPRUIT AIRPORT"
   },
   {
    code: "OSH", 
    name: "OSHOEK"
   },
   {
    code: "OUD", 
    name: "OUDTSHOORN"
   },
   {
    code: "PEZ", 
    name: "PORT ELIZABETH"
   },
   {
    code: "PIA", 
    name: "PILANSBERG AIRPORT"
   },
   {
    code: "PMB", 
    name: "PIETERMARITZBURG"
   },
   {
    code: "PRL", 
    name: "PAARL"
   },
   {
    code: "PSB", 
    name: "PIETERSBURG"
   },
   {
    code: "PTA", 
    name: "PRETORIA"
   },
   {
    code: "QAC", 
    name: "QACHAS NEK"
   },
   {
    code: "RAM", 
    name: "RAMATLABAMA"
   },
   {
    code: "RBS", 
    name: "ROBERTSON"
   },
   {
    code: "RIA", 
    name: "RICHARDSBAY AIRPORT"
   },
   {
    code: "RIC", 
    name: "RICHARDS BAY"
   },
   {
    code: "SAL", 
    name: "SALDANHA BAY"
   },
   {
    code: "SKH", 
    name: "SKILPADSHEK"
   },
   {
    code: "STE", 
    name: "STELLENBOSCH"
   },
   {
    code: "UPS", 
    name: "UPINGTON AIRPORT/STATION"
   },
   {
    code: "UPT", 
    name: "UPINGTON"
   },
   {
    code: "VLD", 
    name: "VIOOLSDRIFT"
   },
   {
    code: "VRE", 
    name: "VREDENDAL"
   },
   {
    code: "VRH", 
    name: "VAN ROOYENHEK"
   },
   {
    code: "WOR", 
    name: "WORCESTER"
   }
  ], 
  ihqCompanies: [
   {
    companyName: "AMATO INVESTMENTS PTY LTD", 
    ihqCode: "IHQ012", 
    registrationNumber: "2014/166692/07"
   },
   {
    companyName: "BALCON INVESTMENTS AND LOGISTICS PTY LTD", 
    ihqCode: "IHQ011", 
    registrationNumber: "2014/154699/07"
   },
   {
    companyName: "CERROMED PTY LTD", 
    ihqCode: "IHQ014", 
    registrationNumber: "2014/156445/07"
   },
   {
    companyName: "FERRO PROPERTIES PTY LTD", 
    ihqCode: "IHQ003", 
    registrationNumber: "2011/102369/07"
   },
   {
    companyName: "ODEBRECHT AFRICA CINE SERVICES PTY LTD", 
    ihqCode: "IHQ009", 
    registrationNumber: "2013/167022/07"
   },
   {
    companyName: "ODEBRECHT AFRICA POULTRY SERVICES PTY LTD", 
    ihqCode: "IHQ010", 
    registrationNumber: "2013/132409/07"
   },
   {
    companyName: "ODEBRECHT AFRICA RETAIL SERVICES PTY LTD", 
    ihqCode: "IHQ008", 
    registrationNumber: "2013/124327/07"
   },
   {
    companyName: "ODEBRECHT AFRICA SHOPPING SERVICES PTY LTD", 
    ihqCode: "IHQ005", 
    registrationNumber: "2013/167018/07"
   },
   {
    companyName: "LAND O'LAKES KENYA HOLDINGS", 
    ihqCode: "IHQ023", 
    registrationNumber: "2015/421556/07"
   },
   {
    companyName: "ARIANE INVESTMENTS PTY LTD", 
    ihqCode: "IHQ024", 
    registrationNumber: "2017/206444/07"
   },
   {
    companyName: "FEDEX EXPRESS AFRICA HOLDINGS PTY LTD", 
    ihqCode: "IHQ004", 
    registrationNumber: "2013/069449/07"
   },
   {
    companyName: "MONEY EXCHANGE AFRICA (PTY) LTD", 
    ihqCode: "IHQ028", 
    registrationNumber: "2011/106710/07"
   },
   {
    companyName: "BAYPORT INTERNATIONAL HEADQUARTER COMPANY (PTY) LTD", 
    ihqCode: "IHQ022", 
    registrationNumber: "2014/225741/07"
   }
  ], 
  categories: [
   {
    flow: "IN", 
    code: "100", 
    section: "Merchandise", 
    description: "Adjustments / Reversals / Refunds applicable to merchandise"
   },
   {
    flow: "IN", 
    code: "101/01", 
    section: "Merchandise", 
    description: "Export advance payment"
   },
   {
    flow: "IN", 
    code: "101/02", 
    section: "Merchandise", 
    description: "Export advance payment - capital goods"
   },
   {
    flow: "IN", 
    code: "101/03", 
    section: "Merchandise", 
    description: "Export advance payment - gold"
   },
   {
    flow: "IN", 
    code: "101/04", 
    section: "Merchandise", 
    description: "Export advance payment - platinum"
   },
   {
    flow: "IN", 
    code: "101/05", 
    section: "Merchandise", 
    description: "Export advance payment - crude oil"
   },
   {
    flow: "IN", 
    code: "101/06", 
    section: "Merchandise", 
    description: "Export advance payment - refined petroleum products"
   },
   {
    flow: "IN", 
    code: "101/07", 
    section: "Merchandise", 
    description: "Export advance payment - diamonds"
   },
   {
    flow: "IN", 
    code: "101/08", 
    section: "Merchandise", 
    description: "Export advance payment - steel"
   },
   {
    flow: "IN", 
    code: "101/09", 
    section: "Merchandise", 
    description: "Export advance payment - coal"
   },
   {
    flow: "IN", 
    code: "101/10", 
    section: "Merchandise", 
    description: "Export advance payment - iron ore"
   },
   {
    flow: "IN", 
    code: "101/11", 
    section: "Merchandise", 
    description: "Export advance payment - goods exported via the South African Post Office"
   },
   {
    flow: "IN", 
    code: "103/01", 
    section: "Merchandise", 
    description: "Export payments"
   },
   {
    flow: "IN", 
    code: "103/02", 
    section: "Merchandise", 
    description: "Export payment - capital goods"
   },
   {
    flow: "IN", 
    code: "103/03", 
    section: "Merchandise", 
    description: "Export payment - gold"
   },
   {
    flow: "IN", 
    code: "103/04", 
    section: "Merchandise", 
    description: "Export payment - platinum"
   },
   {
    flow: "IN", 
    code: "103/05", 
    section: "Merchandise", 
    description: "Export payment - crude oil"
   },
   {
    flow: "IN", 
    code: "103/06", 
    section: "Merchandise", 
    description: "Export payment - refined petroleum products"
   },
   {
    flow: "IN", 
    code: "103/07", 
    section: "Merchandise", 
    description: "Export payment - diamonds"
   },
   {
    flow: "IN", 
    code: "103/08", 
    section: "Merchandise", 
    description: "Export payment - steel"
   },
   {
    flow: "IN", 
    code: "103/09", 
    section: "Merchandise", 
    description: "Export payment - coal"
   },
   {
    flow: "IN", 
    code: "103/10", 
    section: "Merchandise", 
    description: "Export payment - iron ore"
   },
   {
    flow: "IN", 
    code: "103/11", 
    section: "Merchandise", 
    description: "Export payment - goods exported via the South African Post Office"
   },
   {
    flow: "IN", 
    code: "105", 
    section: "Merchandise", 
    description: "Consumables acquired in port"
   },
   {
    flow: "IN", 
    code: "106", 
    section: "Merchandise", 
    description: "Trade finance repayments in respect of exports"
   },
   {
    flow: "IN", 
    code: "107", 
    section: "Merchandise", 
    description: "Export proceeds where the Customs value of the shipment is less than R500"
   },
   {
    flow: "IN", 
    code: "108", 
    section: "Merchandise", 
    description: "Export payments where goods were declared as part of passenger baggage and no UCR is available"
   },
   {
    flow: "IN", 
    code: "109/01", 
    section: "Merchandise", 
    description: "Proceeds for goods purchased by non residents where no physical export will take place, excluding the below"
   },
   {
    flow: "IN", 
    code: "109/02", 
    section: "Merchandise", 
    description: "Proceeds for gold purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/03", 
    section: "Merchandise", 
    description: "Proceeds for platinum purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/04", 
    section: "Merchandise", 
    description: "Proceeds for crude oil purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/05", 
    section: "Merchandise", 
    description: "Proceeds for refined petroleum products purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/06", 
    section: "Merchandise", 
    description: "Proceeds for diamonds purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/07", 
    section: "Merchandise", 
    description: "Proceeds for steel purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/08", 
    section: "Merchandise", 
    description: "Proceeds for coal purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "109/09", 
    section: "Merchandise", 
    description: "Proceeds for iron ore purchased by non residents where no physical export will take place, excluding merchanting transactions"
   },
   {
    flow: "IN", 
    code: "110", 
    section: "Merchandise", 
    description: "Merchanting transaction"
   },
   {
    flow: "IN", 
    code: "200", 
    section: "Intellectual property and other services", 
    description: "Adjustments / Reversals / Refunds applicable to intellectual property and service related items"
   },
   {
    flow: "IN", 
    code: "201", 
    section: "Intellectual property and other services", 
    description: "Rights assigned for licences to reproduce and/or distribute"
   },
   {
    flow: "IN", 
    code: "202", 
    section: "Intellectual property and other services", 
    description: "Rights assigned for using patents and inventions (licensing)"
   },
   {
    flow: "IN", 
    code: "203", 
    section: "Intellectual property and other services", 
    description: "Rights assigned for using patterns and designs (including industrial processes)"
   },
   {
    flow: "IN", 
    code: "204", 
    section: "Intellectual property and other services", 
    description: "Rights assigned for using copyrights"
   },
   {
    flow: "IN", 
    code: "205", 
    section: "Intellectual property and other services", 
    description: "Rights assigned for using franchises and trademarks"
   },
   {
    flow: "IN", 
    code: "210", 
    section: "Intellectual property and other services", 
    description: "Disposal of patents and inventions"
   },
   {
    flow: "IN", 
    code: "211", 
    section: "Intellectual property and other services", 
    description: "Disposal of patterns and designs (including industrial processes)"
   },
   {
    flow: "IN", 
    code: "212", 
    section: "Intellectual property and other services", 
    description: "Disposal of copyrights"
   },
   {
    flow: "IN", 
    code: "213", 
    section: "Intellectual property and other services", 
    description: "Disposal of franchises and trademarks"
   },
   {
    flow: "IN", 
    code: "220", 
    section: "Intellectual property and other services", 
    description: "Proceeds received for research and development services"
   },
   {
    flow: "IN", 
    code: "221", 
    section: "Intellectual property and other services", 
    description: "Funding received for research and development"
   },
   {
    flow: "IN", 
    code: "225", 
    section: "Intellectual property and other services", 
    description: "Sales of original manuscripts, sound recordings and films"
   },
   {
    flow: "IN", 
    code: "226", 
    section: "Intellectual property and other services", 
    description: "Receipt of funds relating to the production of motion pictures, radio and television programs and musical recordings"
   },
   {
    flow: "IN", 
    code: "230", 
    section: "Intellectual property and other services", 
    description: "The outright selling of ownership rights of software"
   },
   {
    flow: "IN", 
    code: "231", 
    section: "Intellectual property and other services", 
    description: "Computer-related services including maintenance, repair and consultancy"
   },
   {
    flow: "IN", 
    code: "232", 
    section: "Intellectual property and other services", 
    description: "Commercial sales of customised software and related licenses for use by customers"
   },
   {
    flow: "IN", 
    code: "233", 
    section: "Intellectual property and other services", 
    description: "Commercial sales of non-customised software on physical media with periodic licence to use"
   },
   {
    flow: "IN", 
    code: "234", 
    section: "Intellectual property and other services", 
    description: "Commercial sales of non-customised software provided on physical media with right to perpetual (ongoing) use"
   },
   {
    flow: "IN", 
    code: "235", 
    section: "Intellectual property and other services", 
    description: "Commercial sales of non-customised software provided for downloading or electronically made available with periodic license"
   },
   {
    flow: "IN", 
    code: "236", 
    section: "Intellectual property and other services", 
    description: "Commercial sales of non-customised software provided for downloading or electronically made available with single payment"
   },
   {
    flow: "IN", 
    code: "240/01", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on materials (excluding gold, platinum, crude oil, refined petroleum products, diamonds, steel, coal and iron ore)"
   },
   {
    flow: "IN", 
    code: "240/02", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on gold"
   },
   {
    flow: "IN", 
    code: "240/03", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on platinum"
   },
   {
    flow: "IN", 
    code: "240/04", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on crude oil"
   },
   {
    flow: "IN", 
    code: "240/05", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on refined petroleum products"
   },
   {
    flow: "IN", 
    code: "240/06", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on diamonds"
   },
   {
    flow: "IN", 
    code: "240/07", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on steel"
   },
   {
    flow: "IN", 
    code: "240/08", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on coal"
   },
   {
    flow: "IN", 
    code: "240/09", 
    section: "Intellectual property and other services", 
    description: "Fees for processing - processing done on iron ore"
   },
   {
    flow: "IN", 
    code: "241", 
    section: "Intellectual property and other services", 
    description: "Repairs and maintenance on machinery and equipment"
   },
   {
    flow: "IN", 
    code: "242", 
    section: "Intellectual property and other services", 
    description: "Architectural, engineering and other technical services"
   },
   {
    flow: "IN", 
    code: "243", 
    section: "Intellectual property and other services", 
    description: "Agricultural, mining, waste treatment and depollution services"
   },
   {
    flow: "IN", 
    code: "250", 
    section: "Intellectual property and other services", 
    description: "Travel services for non-residents - business travel"
   },
   {
    flow: "IN", 
    code: "251", 
    section: "Intellectual property and other services", 
    description: "Travel services for non-residents - holiday travel"
   },
   {
    flow: "IN", 
    code: "252", 
    section: "Intellectual property and other services", 
    description: "Foreign exchange accepted by residents from non-residents"
   },
   {
    flow: "IN", 
    code: "255", 
    section: "Intellectual property and other services", 
    description: "Travel services for residents - business travel"
   },
   {
    flow: "IN", 
    code: "256", 
    section: "Intellectual property and other services", 
    description: "Travel services for residents - holiday travel"
   },
   {
    flow: "IN", 
    code: "260", 
    section: "Intellectual property and other services", 
    description: "Proceeds for travel services in respect of third parties - business travel"
   },
   {
    flow: "IN", 
    code: "261", 
    section: "Intellectual property and other services", 
    description: "Proceeds for travel services in respect of third parties - holiday travel"
   },
   {
    flow: "IN", 
    code: "265", 
    section: "Intellectual property and other services", 
    description: "Proceeds for telecommunication services"
   },
   {
    flow: "IN", 
    code: "266", 
    section: "Intellectual property and other services", 
    description: "Proceeds for information services including data, news related and news agency fees"
   },
   {
    flow: "IN", 
    code: "270/01", 
    section: "Intellectual property and other services", 
    description: "Proceeds for passenger services - road"
   },
   {
    flow: "IN", 
    code: "270/02", 
    section: "Intellectual property and other services", 
    description: "Proceeds for passenger services - rail"
   },
   {
    flow: "IN", 
    code: "270/03", 
    section: "Intellectual property and other services", 
    description: "Proceeds for passenger services - sea"
   },
   {
    flow: "IN", 
    code: "270/04", 
    section: "Intellectual property and other services", 
    description: "Proceeds for passenger services - air"
   },
   {
    flow: "IN", 
    code: "271/01", 
    section: "Intellectual property and other services", 
    description: "Proceeds for freight services - road"
   },
   {
    flow: "IN", 
    code: "271/02", 
    section: "Intellectual property and other services", 
    description: "Proceeds for freight services - rail"
   },
   {
    flow: "IN", 
    code: "271/03", 
    section: "Intellectual property and other services", 
    description: "Proceeds for freight services - sea"
   },
   {
    flow: "IN", 
    code: "271/04", 
    section: "Intellectual property and other services", 
    description: "Proceeds for freight services - air"
   },
   {
    flow: "IN", 
    code: "272/01", 
    section: "Intellectual property and other services", 
    description: "Proceeds for other transport services - road"
   },
   {
    flow: "IN", 
    code: "272/02", 
    section: "Intellectual property and other services", 
    description: "Proceeds for other transport services - rail"
   },
   {
    flow: "IN", 
    code: "272/03", 
    section: "Intellectual property and other services", 
    description: "Proceeds for other transport services - sea"
   },
   {
    flow: "IN", 
    code: "272/04", 
    section: "Intellectual property and other services", 
    description: "Proceeds for other transport services - air"
   },
   {
    flow: "IN", 
    code: "273/01", 
    section: "Intellectual property and other services", 
    description: "Proceeds for postal and courier services - road"
   },
   {
    flow: "IN", 
    code: "273/02", 
    section: "Intellectual property and other services", 
    description: "Proceeds for postal and courier services - rail"
   },
   {
    flow: "IN", 
    code: "273/03", 
    section: "Intellectual property and other services", 
    description: "Proceeds for postal and courier services - sea"
   },
   {
    flow: "IN", 
    code: "273/04", 
    section: "Intellectual property and other services", 
    description: "Proceeds for postal and courier services - air"
   },
   {
    flow: "IN", 
    code: "275", 
    section: "Intellectual property and other services", 
    description: "Commission and fees"
   },
   {
    flow: "IN", 
    code: "276", 
    section: "Intellectual property and other services", 
    description: "Proceeds for financial services charged for advice provided"
   },
   {
    flow: "IN", 
    code: "280", 
    section: "Intellectual property and other services", 
    description: "Proceeds for construction services"
   },
   {
    flow: "IN", 
    code: "281", 
    section: "Intellectual property and other services", 
    description: "Proceeds for government services"
   },
   {
    flow: "IN", 
    code: "282", 
    section: "Intellectual property and other services", 
    description: "Diplomatic transfers"
   },
   {
    flow: "IN", 
    code: "285", 
    section: "Intellectual property and other services", 
    description: "Tuition fees"
   },
   {
    flow: "IN", 
    code: "287", 
    section: "Intellectual property and other services", 
    description: "Proceeds for legal services"
   },
   {
    flow: "IN", 
    code: "288", 
    section: "Intellectual property and other services", 
    description: "Proceeds for accounting services"
   },
   {
    flow: "IN", 
    code: "289", 
    section: "Intellectual property and other services", 
    description: "Proceeds for management consulting services"
   },
   {
    flow: "IN", 
    code: "290", 
    section: "Intellectual property and other services", 
    description: "Proceeds for public relation services"
   },
   {
    flow: "IN", 
    code: "291", 
    section: "Intellectual property and other services", 
    description: "Proceeds for advertising & market research services"
   },
   {
    flow: "IN", 
    code: "292", 
    section: "Intellectual property and other services", 
    description: "Proceeds for managerial services"
   },
   {
    flow: "IN", 
    code: "293", 
    section: "Intellectual property and other services", 
    description: "Proceeds for medical and dental services"
   },
   {
    flow: "IN", 
    code: "294", 
    section: "Intellectual property and other services", 
    description: "Proceeds for educational services"
   },
   {
    flow: "IN", 
    code: "295", 
    section: "Intellectual property and other services", 
    description: "Operational leasing"
   },
   {
    flow: "IN", 
    code: "296", 
    section: "Intellectual property and other services", 
    description: "Proceeds for cultural and recreational services"
   },
   {
    flow: "IN", 
    code: "297", 
    section: "Intellectual property and other services", 
    description: "Proceeds for other business services not included elsewhere"
   },
   {
    flow: "IN", 
    code: "300", 
    section: "Income and yields on financial assets", 
    description: "Adjustments / Reversals / Refunds related to income and yields on financial assets"
   },
   {
    flow: "IN", 
    code: "301", 
    section: "Income and yields on financial assets", 
    description: "Dividends"
   },
   {
    flow: "IN", 
    code: "302", 
    section: "Income and yields on financial assets", 
    description: "Branch profits"
   },
   {
    flow: "IN", 
    code: "303", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a non-resident to a resident employee temporarily abroad (excluding remittances)"
   },
   {
    flow: "IN", 
    code: "304", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a non-resident to a non-resident employee in South Africa (excluding remittances)"
   },
   {
    flow: "IN", 
    code: "305", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a non-resident to a migrant worker employee (excluding remittances)"
   },
   {
    flow: "IN", 
    code: "306", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a non-resident to a foreign national contract worker employee (excluding remittances)"
   },
   {
    flow: "IN", 
    code: "307", 
    section: "Income and yields on financial assets", 
    description: "Commission or brokerage"
   },
   {
    flow: "IN", 
    code: "308", 
    section: "Income and yields on financial assets", 
    description: "Rental"
   },
   {
    flow: "IN", 
    code: "309/01", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a resident temporarily abroad in respect of loans"
   },
   {
    flow: "IN", 
    code: "309/02", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of individual loans"
   },
   {
    flow: "IN", 
    code: "309/03", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of study loans"
   },
   {
    flow: "IN", 
    code: "309/04", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of shareholders loans"
   },
   {
    flow: "IN", 
    code: "309/05", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of third party loans"
   },
   {
    flow: "IN", 
    code: "309/06", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of trade finance loans"
   },
   {
    flow: "IN", 
    code: "309/07", 
    section: "Income and yields on financial assets", 
    description: "Interest received from a non-resident in respect of a bond"
   },
   {
    flow: "IN", 
    code: "309/08", 
    section: "Income and yields on financial assets", 
    description: "Interest received not in respect of loans"
   },
   {
    flow: "IN", 
    code: "310/01", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities equity individual"
   },
   {
    flow: "IN", 
    code: "310/02", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities equity corporate"
   },
   {
    flow: "IN", 
    code: "310/03", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities equity bank"
   },
   {
    flow: "IN", 
    code: "310/04", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities equity institution"
   },
   {
    flow: "IN", 
    code: "311/01", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities debt individual"
   },
   {
    flow: "IN", 
    code: "311/02", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities debt corporate"
   },
   {
    flow: "IN", 
    code: "311/03", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities debt bank"
   },
   {
    flow: "IN", 
    code: "311/04", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities debt institution"
   },
   {
    flow: "IN", 
    code: "312/01", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities derivatives individual"
   },
   {
    flow: "IN", 
    code: "312/02", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities derivatives corporate"
   },
   {
    flow: "IN", 
    code: "312/03", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities derivatives bank"
   },
   {
    flow: "IN", 
    code: "312/04", 
    section: "Income and yields on financial assets", 
    description: "Income in respect of inward listed securities derivatives institution"
   },
   {
    flow: "IN", 
    code: "313", 
    section: "Income and yields on financial assets", 
    description: "Income earned abroad by a resident on an individual investment"
   },
   {
    flow: "IN", 
    code: "400", 
    section: "Transfers of a current nature", 
    description: "Adjustments / Reversals / Refunds related to transfers of a current nature"
   },
   {
    flow: "IN", 
    code: "401", 
    section: "Transfers of a current nature", 
    description: "Gifts"
   },
   {
    flow: "IN", 
    code: "402", 
    section: "Transfers of a current nature", 
    description: "Annual contributions"
   },
   {
    flow: "IN", 
    code: "403", 
    section: "Transfers of a current nature", 
    description: "Contributions in respect of social security schemes"
   },
   {
    flow: "IN", 
    code: "404", 
    section: "Transfers of a current nature", 
    description: "Contributions in respect of charitable, religious and cultural (excluding research and development)"
   },
   {
    flow: "IN", 
    code: "405", 
    section: "Transfers of a current nature", 
    description: "Other donations / aid to Government (excluding research and development)"
   },
   {
    flow: "IN", 
    code: "406", 
    section: "Transfers of a current nature", 
    description: "Other donations / aid to private sector (excluding research and development)"
   },
   {
    flow: "IN", 
    code: "407", 
    section: "Transfers of a current nature", 
    description: "Pensions"
   },
   {
    flow: "IN", 
    code: "408", 
    section: "Transfers of a current nature", 
    description: "Annuities (pension related)"
   },
   {
    flow: "IN", 
    code: "409", 
    section: "Transfers of a current nature", 
    description: "Inheritances"
   },
   {
    flow: "IN", 
    code: "410", 
    section: "Transfers of a current nature", 
    description: "Alimony"
   },
   {
    flow: "IN", 
    code: "411/01", 
    section: "Transfers of a current nature", 
    description: "Tax - Income tax"
   },
   {
    flow: "IN", 
    code: "411/02", 
    section: "Transfers of a current nature", 
    description: "Tax - VAT refunds"
   },
   {
    flow: "IN", 
    code: "411/03", 
    section: "Transfers of a current nature", 
    description: "Tax - Other"
   },
   {
    flow: "IN", 
    code: "412", 
    section: "Transfers of a current nature", 
    description: "Insurance premiums (non life/short term)"
   },
   {
    flow: "IN", 
    code: "413", 
    section: "Transfers of a current nature", 
    description: "Insurance claims (non life/short term)"
   },
   {
    flow: "IN", 
    code: "414", 
    section: "Transfers of a current nature", 
    description: "Insurance premiums (life)"
   },
   {
    flow: "IN", 
    code: "415", 
    section: "Transfers of a current nature", 
    description: "Insurance claims (life)"
   },
   {
    flow: "IN", 
    code: "416", 
    section: "Transfers of a current nature", 
    description: "Migrant worker remittances (excluding compensation)"
   },
   {
    flow: "IN", 
    code: "417", 
    section: "Transfers of a current nature", 
    description: "Foreign national contract worker remittances (excluding compensation)"
   },
   {
    flow: "IN", 
    code: "500", 
    section: "Capital Transfers and immigrants", 
    description: "Adjustments / Reversals / Refunds related to capital transfers and immigrants"
   },
   {
    flow: "IN", 
    code: "501", 
    section: "Capital Transfers and immigrants", 
    description: "Donations to SA Government for fixed assets"
   },
   {
    flow: "IN", 
    code: "502", 
    section: "Capital Transfers and immigrants", 
    description: "Donations to corporate entities - fixed assets"
   },
   {
    flow: "IN", 
    code: "503", 
    section: "Capital Transfers and immigrants", 
    description: "Investment into property by a non-resident corporate entity"
   },
   {
    flow: "IN", 
    code: "504", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of property by a resident corporate entity"
   },
   {
    flow: "IN", 
    code: "510/01", 
    section: "Capital Transfers and immigrants", 
    description: "Investment into property by a non-resident individual"
   },
   {
    flow: "IN", 
    code: "510/02", 
    section: "Capital Transfers and immigrants", 
    description: "Investment by a non-resident individual - other"
   },
   {
    flow: "IN", 
    code: "511/01", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Shares"
   },
   {
    flow: "IN", 
    code: "511/02", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Bonds"
   },
   {
    flow: "IN", 
    code: "511/03", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Money market instruments"
   },
   {
    flow: "IN", 
    code: "511/04", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Deposits with a foreign bank"
   },
   {
    flow: "IN", 
    code: "511/05", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Mutual funds / collective investment schemes"
   },
   {
    flow: "IN", 
    code: "511/06", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Property"
   },
   {
    flow: "IN", 
    code: "511/07", 
    section: "Capital Transfers and immigrants", 
    description: "Disinvestment of capital by a resident individual - Other"
   },
   {
    flow: "IN", 
    code: "516", 
    section: "Capital Transfers and immigrants", 
    description: "Repatriation of capital, on instruction by the FSD, of a foreign investment by a resident individual in respect of cross-border flows"
   },
   {
    flow: "IN", 
    code: "517", 
    section: "Capital Transfers and immigrants", 
    description: "Repatriation of capital, on instruction by the FSD, of a foreign investment by a resident individual originating from an account conducted in foreign currency held at an AD in SA"
   },
   {
    flow: "IN", 
    code: "530/01", 
    section: "Capital Transfers and immigrants", 
    description: "Immigration"
   },
   {
    flow: "IN", 
    code: "600", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Adjustments / Reversals / Refunds related to financial investments/disinvestments and prudential investments"
   },
   {
    flow: "IN", 
    code: "601/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment in listed shares by a non-resident"
   },
   {
    flow: "IN", 
    code: "601/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment in non-listed shares by a non-resident"
   },
   {
    flow: "IN", 
    code: "602", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into money market instruments by a non-resident"
   },
   {
    flow: "IN", 
    code: "603/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into listed bonds by a non-resident (excluding loans)"
   },
   {
    flow: "IN", 
    code: "603/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into non-listed bonds by a non-resident (excluding loans)"
   },
   {
    flow: "IN", 
    code: "605/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Agriculture, hunting, forestry and fishing"
   },
   {
    flow: "IN", 
    code: "605/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Mining, quarrying and exploration"
   },
   {
    flow: "IN", 
    code: "605/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Manufacturing"
   },
   {
    flow: "IN", 
    code: "605/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Electricity, gas and water supply"
   },
   {
    flow: "IN", 
    code: "605/05", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Construction"
   },
   {
    flow: "IN", 
    code: "605/06", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Wholesale, retail, repairs, hotel and restaurants"
   },
   {
    flow: "IN", 
    code: "605/07", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Transport and communication"
   },
   {
    flow: "IN", 
    code: "605/08", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Financial services"
   },
   {
    flow: "IN", 
    code: "605/09", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of shares by resident - Community, social and personal services"
   },
   {
    flow: "IN", 
    code: "610/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity individual buy back"
   },
   {
    flow: "IN", 
    code: "610/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity corporate buy back"
   },
   {
    flow: "IN", 
    code: "610/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity bank buy back"
   },
   {
    flow: "IN", 
    code: "610/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity institution buy back"
   },
   {
    flow: "IN", 
    code: "611/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt individual redemption"
   },
   {
    flow: "IN", 
    code: "611/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt corporate redemption"
   },
   {
    flow: "IN", 
    code: "611/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt bank redemption"
   },
   {
    flow: "IN", 
    code: "611/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt institution redemption"
   },
   {
    flow: "IN", 
    code: "612/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives individual proceeds"
   },
   {
    flow: "IN", 
    code: "612/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives corporate proceeds"
   },
   {
    flow: "IN", 
    code: "612/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives bank proceeds"
   },
   {
    flow: "IN", 
    code: "612/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives institution proceeds"
   },
   {
    flow: "IN", 
    code: "615/01", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Disinvestment by resident institutional investor - Asset Manager"
   },
   {
    flow: "IN", 
    code: "615/02", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Disinvestment by resident institutional investor - Collective Investment Scheme"
   },
   {
    flow: "IN", 
    code: "615/03", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Disinvestment by resident institutional investor - Retirement Fund"
   },
   {
    flow: "IN", 
    code: "615/04", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Disinvestment by resident institutional investor - Life Linked"
   },
   {
    flow: "IN", 
    code: "615/05", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Disinvestment by resident institutional investor - Life Non Linked"
   },
   {
    flow: "IN", 
    code: "616", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Bank prudential disinvestment"
   },
   {
    flow: "IN", 
    code: "700", 
    section: "Derivatives", 
    description: "Adjustments / Reversals / Refunds related to derivatives"
   },
   {
    flow: "IN", 
    code: "701/01", 
    section: "Derivatives", 
    description: "Options - listed"
   },
   {
    flow: "IN", 
    code: "701/02", 
    section: "Derivatives", 
    description: "Options - unlisted"
   },
   {
    flow: "IN", 
    code: "702/01", 
    section: "Derivatives", 
    description: "Futures - listed"
   },
   {
    flow: "IN", 
    code: "702/02", 
    section: "Derivatives", 
    description: "Futures - unlisted"
   },
   {
    flow: "IN", 
    code: "703/01", 
    section: "Derivatives", 
    description: "Warrants - listed"
   },
   {
    flow: "IN", 
    code: "703/02", 
    section: "Derivatives", 
    description: "Warrants - unlisted"
   },
   {
    flow: "IN", 
    code: "704/01", 
    section: "Derivatives", 
    description: "Gold hedging - listed"
   },
   {
    flow: "IN", 
    code: "704/02", 
    section: "Derivatives", 
    description: "Gold hedging - unlisted"
   },
   {
    flow: "IN", 
    code: "705/01", 
    section: "Derivatives", 
    description: "Derivative not specified above - listed"
   },
   {
    flow: "IN", 
    code: "705/02", 
    section: "Derivatives", 
    description: "Derivative not specified above - unlisted"
   },
   {
    flow: "IN", 
    code: "800", 
    section: "Loan and Miscellaneous payments", 
    description: "Adjustments / Reversals / Refunds related to loan and miscellaneous payments"
   },
   {
    flow: "IN", 
    code: "801", 
    section: "Loan and Miscellaneous payments", 
    description: "Trade finance loan drawn down in South Africa"
   },
   {
    flow: "IN", 
    code: "802", 
    section: "Loan and Miscellaneous payments", 
    description: "International Bond drawn down"
   },
   {
    flow: "IN", 
    code: "803", 
    section: "Loan and Miscellaneous payments", 
    description: "Loan made to a resident by a non-resident shareholder"
   },
   {
    flow: "IN", 
    code: "804", 
    section: "Loan and Miscellaneous payments", 
    description: "Loan made to a resident by a non-resident third party"
   },
   {
    flow: "IN", 
    code: "810", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment by a resident temporarily abroad of a loan granted by a resident"
   },
   {
    flow: "IN", 
    code: "815", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of an individual loan to a resident"
   },
   {
    flow: "IN", 
    code: "816", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of a study loan to a resident"
   },
   {
    flow: "IN", 
    code: "817", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of a shareholders loan to a resident"
   },
   {
    flow: "IN", 
    code: "818", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of a third party loan to a resident (excluding shareholders)"
   },
   {
    flow: "IN", 
    code: "819", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of a trade finance loan to a resident"
   },
   {
    flow: "IN", 
    code: "830", 
    section: "Loan and Miscellaneous payments", 
    description: "Details of payments not classified"
   },
   {
    flow: "IN", 
    code: "832", 
    section: "Loan and Miscellaneous payments", 
    description: "Rand drafts/cheques drawn on vostro accounts (Only applicable if no description is available)"
   },
   {
    flow: "IN", 
    code: "833", 
    section: "Loan and Miscellaneous payments", 
    description: "Credit/Debit card company settlement as well as money remitter settlements"
   },
   {
    flow: "OUT", 
    code: "100", 
    section: "Merchandise", 
    description: "Adjustments / Reversals / Refunds applicable to merchandise"
   },
   {
    flow: "OUT", 
    code: "101/01", 
    section: "Merchandise", 
    description: "Import advance payment"
   },
   {
    flow: "OUT", 
    code: "101/02", 
    section: "Merchandise", 
    description: "Import advance payment - capital goods"
   },
   {
    flow: "OUT", 
    code: "101/03", 
    section: "Merchandise", 
    description: "Import advance payment - gold"
   },
   {
    flow: "OUT", 
    code: "101/04", 
    section: "Merchandise", 
    description: "Import advance payment - platinum"
   },
   {
    flow: "OUT", 
    code: "101/05", 
    section: "Merchandise", 
    description: "Import advance payment - crude oil"
   },
   {
    flow: "OUT", 
    code: "101/06", 
    section: "Merchandise", 
    description: "Import advance payment - refined petroleum products"
   },
   {
    flow: "OUT", 
    code: "101/07", 
    section: "Merchandise", 
    description: "Import advance payment - diamonds"
   },
   {
    flow: "OUT", 
    code: "101/08", 
    section: "Merchandise", 
    description: "Import advance payment - steel"
   },
   {
    flow: "OUT", 
    code: "101/09", 
    section: "Merchandise", 
    description: "Import advance payment - coal"
   },
   {
    flow: "OUT", 
    code: "101/10", 
    section: "Merchandise", 
    description: "Import advance payment - iron ore"
   },
   {
    flow: "OUT", 
    code: "101/11", 
    section: "Merchandise", 
    description: "Import advance payment - goods imported via the South African Post Office"
   },
   {
    flow: "OUT", 
    code: "102/01", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking "
   },
   {
    flow: "OUT", 
    code: "102/02", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - capital goods"
   },
   {
    flow: "OUT", 
    code: "102/03", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - gold"
   },
   {
    flow: "OUT", 
    code: "102/04", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - platinum"
   },
   {
    flow: "OUT", 
    code: "102/05", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - crude oil"
   },
   {
    flow: "OUT", 
    code: "102/06", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - refined petroleum products"
   },
   {
    flow: "OUT", 
    code: "102/07", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - diamonds"
   },
   {
    flow: "OUT", 
    code: "102/08", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - steel"
   },
   {
    flow: "OUT", 
    code: "102/09", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - coal"
   },
   {
    flow: "OUT", 
    code: "102/10", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - iron ore"
   },
   {
    flow: "OUT", 
    code: "102/11", 
    section: "Merchandise", 
    description: "Import advance payment i.t.o. import undertaking - goods imported via the South African Post Office"
   },
   {
    flow: "OUT", 
    code: "103/01", 
    section: "Merchandise", 
    description: "Import payment"
   },
   {
    flow: "OUT", 
    code: "103/02", 
    section: "Merchandise", 
    description: "Import payment - capital goods"
   },
   {
    flow: "OUT", 
    code: "103/03", 
    section: "Merchandise", 
    description: "Import payment - gold"
   },
   {
    flow: "OUT", 
    code: "103/04", 
    section: "Merchandise", 
    description: "Import payment - platinum"
   },
   {
    flow: "OUT", 
    code: "103/05", 
    section: "Merchandise", 
    description: "Import payment - crude oil"
   },
   {
    flow: "OUT", 
    code: "103/06", 
    section: "Merchandise", 
    description: "Import payment - refined petroleum products"
   },
   {
    flow: "OUT", 
    code: "103/07", 
    section: "Merchandise", 
    description: "Import payment - diamonds"
   },
   {
    flow: "OUT", 
    code: "103/08", 
    section: "Merchandise", 
    description: "Import payment - steel"
   },
   {
    flow: "OUT", 
    code: "103/09", 
    section: "Merchandise", 
    description: "Import payment - coal"
   },
   {
    flow: "OUT", 
    code: "103/10", 
    section: "Merchandise", 
    description: "Import payment - iron ore"
   },
   {
    flow: "OUT", 
    code: "103/11", 
    section: "Merchandise", 
    description: "Import payment - goods imported via the South African Post Office"
   },
   {
    flow: "OUT", 
    code: "104/01", 
    section: "Merchandise", 
    description: "Import payment (excluding capital goods, gold, platinum, crude oil, refined petroleum products, diamonds, steel, coal, iron ore and goods imported via the South African Post Office)"
   },
   {
    flow: "OUT", 
    code: "104/02", 
    section: "Merchandise", 
    description: "Import payment - capital goods"
   },
   {
    flow: "OUT", 
    code: "104/03", 
    section: "Merchandise", 
    description: "Import payment - gold"
   },
   {
    flow: "OUT", 
    code: "104/04", 
    section: "Merchandise", 
    description: "Import payment - platinum"
   },
   {
    flow: "OUT", 
    code: "104/05", 
    section: "Merchandise", 
    description: "Import payment- crude oil"
   },
   {
    flow: "OUT", 
    code: "104/06", 
    section: "Merchandise", 
    description: "Import payment- refined petroleum products"
   },
   {
    flow: "OUT", 
    code: "104/07", 
    section: "Merchandise", 
    description: "Import payment - diamonds"
   },
   {
    flow: "OUT", 
    code: "104/08", 
    section: "Merchandise", 
    description: "Import payment- steel"
   },
   {
    flow: "OUT", 
    code: "104/09", 
    section: "Merchandise", 
    description: "Import payment- coal"
   },
   {
    flow: "OUT", 
    code: "104/10", 
    section: "Merchandise", 
    description: "Import payment- iron ore"
   },
   {
    flow: "OUT", 
    code: "104/11", 
    section: "Merchandise", 
    description: "Import payment - goods imported via the South African Post Office"
   },
   {
    flow: "OUT", 
    code: "105", 
    section: "Merchandise", 
    description: "Consumables acquired in port"
   },
   {
    flow: "OUT", 
    code: "106", 
    section: "Merchandise", 
    description: "Repayment of trade finance for imports"
   },
   {
    flow: "OUT", 
    code: "107", 
    section: "Merchandise", 
    description: "Import payments where the Customs value of the shipment is less than R500"
   },
   {
    flow: "OUT", 
    code: "108", 
    section: "Merchandise", 
    description: "Import payments where goods were declared as part of passenger baggage and no MRN is available"
   },
   {
    flow: "OUT", 
    code: "109/01", 
    section: "Merchandise", 
    description: "Payments for goods purchased from non-residents in cases where no physical import will take place, excluding goods in the below categories"
   },
   {
    flow: "OUT", 
    code: "109/02", 
    section: "Merchandise", 
    description: "Payments for gold purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/03", 
    section: "Merchandise", 
    description: "Payments for platinum purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/04", 
    section: "Merchandise", 
    description: "Payments for crude oil purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/05", 
    section: "Merchandise", 
    description: "Payments for refined petroleum products purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/06", 
    section: "Merchandise", 
    description: "Payments for diamonds purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/07", 
    section: "Merchandise", 
    description: "Payments for steel purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/08", 
    section: "Merchandise", 
    description: "Payments for coal purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "109/09", 
    section: "Merchandise", 
    description: "Payments for iron ore purchased from non-residents in cases where no physical import will take place, excluding merchanting transactions"
   },
   {
    flow: "OUT", 
    code: "110", 
    section: "Merchandise", 
    description: "Imports Merchanting transaction"
   },
   {
    flow: "OUT", 
    code: "200", 
    section: "Intellectual property and other services", 
    description: "Adjustments / Reversals / Refunds applicable to intellectual property and service related items"
   },
   {
    flow: "OUT", 
    code: "201", 
    section: "Intellectual property and other services", 
    description: "Rights obtained for licences to reproduce and/or distribute"
   },
   {
    flow: "OUT", 
    code: "202", 
    section: "Intellectual property and other services", 
    description: "Rights obtained for using patents and inventions (licensing)"
   },
   {
    flow: "OUT", 
    code: "203", 
    section: "Intellectual property and other services", 
    description: "Rights obtained for using patterns and designs (including industrial processes)"
   },
   {
    flow: "OUT", 
    code: "204", 
    section: "Intellectual property and other services", 
    description: "Rights obtained for using copyrights"
   },
   {
    flow: "OUT", 
    code: "205", 
    section: "Intellectual property and other services", 
    description: "Rights obtained for using franchises and trademarks."
   },
   {
    flow: "OUT", 
    code: "210", 
    section: "Intellectual property and other services", 
    description: "Acquisition of patents and inventions"
   },
   {
    flow: "OUT", 
    code: "211", 
    section: "Intellectual property and other services", 
    description: "Acquisition of patterns and designs (including industrial processes)"
   },
   {
    flow: "OUT", 
    code: "212", 
    section: "Intellectual property and other services", 
    description: "Acquisition of copyrights"
   },
   {
    flow: "OUT", 
    code: "213", 
    section: "Intellectual property and other services", 
    description: "Acquisition of franchises and trademarks"
   },
   {
    flow: "OUT", 
    code: "220", 
    section: "Intellectual property and other services", 
    description: "Payments for research and development services"
   },
   {
    flow: "OUT", 
    code: "221", 
    section: "Intellectual property and other services", 
    description: "Funding for research and development"
   },
   {
    flow: "OUT", 
    code: "225", 
    section: "Intellectual property and other services", 
    description: "Acquisition of original manuscripts, sound recordings and films"
   },
   {
    flow: "OUT", 
    code: "226", 
    section: "Intellectual property and other services", 
    description: "Payment relating to the production of motion pictures, radio and television programs and musical recordings"
   },
   {
    flow: "OUT", 
    code: "230", 
    section: "Intellectual property and other services", 
    description: "The outright purchasing of ownership rights of software"
   },
   {
    flow: "OUT", 
    code: "231", 
    section: "Intellectual property and other services", 
    description: "Computer-related services including maintenance, repair and consultancy"
   },
   {
    flow: "OUT", 
    code: "232", 
    section: "Intellectual property and other services", 
    description: "Commercial purchases of customised software and related licenses to use"
   },
   {
    flow: "OUT", 
    code: "233", 
    section: "Intellectual property and other services", 
    description: "Commercial purchases of non-customised software on physical media with periodic licence to use"
   },
   {
    flow: "OUT", 
    code: "234", 
    section: "Intellectual property and other services", 
    description: "Commercial purchases of non-customised software provided on physical media with right to perpetual (ongoing) use"
   },
   {
    flow: "OUT", 
    code: "235", 
    section: "Intellectual property and other services", 
    description: "Commercial purchases of non-customised software downloaded or electronically acquired with periodic license."
   },
   {
    flow: "OUT", 
    code: "236", 
    section: "Intellectual property and other services", 
    description: "Commercial purchases of non-customised software downloaded or electronically acquired with single payment"
   },
   {
    flow: "OUT", 
    code: "240/01", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on materials (excluding gold, platinum, crude oil, refined petroleum products, diamonds, steel, coal and iron ore)"
   },
   {
    flow: "OUT", 
    code: "240/02", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on gold"
   },
   {
    flow: "OUT", 
    code: "240/03", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on platinum"
   },
   {
    flow: "OUT", 
    code: "240/04", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on crude oil"
   },
   {
    flow: "OUT", 
    code: "240/05", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on refined petroleum products"
   },
   {
    flow: "OUT", 
    code: "240/06", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on diamonds"
   },
   {
    flow: "OUT", 
    code: "240/07", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on steel"
   },
   {
    flow: "OUT", 
    code: "240/08", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on coal"
   },
   {
    flow: "OUT", 
    code: "240/09", 
    section: "Intellectual property and other services", 
    description: "Fees for processing done on iron ore"
   },
   {
    flow: "OUT", 
    code: "241", 
    section: "Intellectual property and other services", 
    description: "Repairs and maintenance on machinery and equipment"
   },
   {
    flow: "OUT", 
    code: "242", 
    section: "Intellectual property and other services", 
    description: "Architectural, engineering and other technical services"
   },
   {
    flow: "OUT", 
    code: "243", 
    section: "Intellectual property and other services", 
    description: "Agricultural, mining, waste treatment and depollution services"
   },
   {
    flow: "OUT", 
    code: "250", 
    section: "Intellectual property and other services", 
    description: "Travel services for non-residents - business travel"
   },
   {
    flow: "OUT", 
    code: "251", 
    section: "Intellectual property and other services", 
    description: "Travel services for non-residents - holiday travel"
   },
   {
    flow: "OUT", 
    code: "255", 
    section: "Intellectual property and other services", 
    description: "Travel services for residents - business travel"
   },
   {
    flow: "OUT", 
    code: "256", 
    section: "Intellectual property and other services", 
    description: "Travel services for residents - holiday travel"
   },
   {
    flow: "OUT", 
    code: "260", 
    section: "Intellectual property and other services", 
    description: "Payment for travel services in respect of third parties - business travel"
   },
   {
    flow: "OUT", 
    code: "261", 
    section: "Intellectual property and other services", 
    description: "Payment for travel services in respect of third parties - holiday travel"
   },
   {
    flow: "OUT", 
    code: "265", 
    section: "Intellectual property and other services", 
    description: "Payment for telecommunication services"
   },
   {
    flow: "OUT", 
    code: "266", 
    section: "Intellectual property and other services", 
    description: "Payment for information services including data, news related and news agency fees"
   },
   {
    flow: "OUT", 
    code: "270/01", 
    section: "Intellectual property and other services", 
    description: "Payment for passenger services - road"
   },
   {
    flow: "OUT", 
    code: "270/02", 
    section: "Intellectual property and other services", 
    description: "Payment for passenger services - rail"
   },
   {
    flow: "OUT", 
    code: "270/03", 
    section: "Intellectual property and other services", 
    description: "Payment for passenger services - sea"
   },
   {
    flow: "OUT", 
    code: "270/04", 
    section: "Intellectual property and other services", 
    description: "Payment for passenger services - air"
   },
   {
    flow: "OUT", 
    code: "271/01", 
    section: "Intellectual property and other services", 
    description: "Payment for freight services - road"
   },
   {
    flow: "OUT", 
    code: "271/02", 
    section: "Intellectual property and other services", 
    description: "Payment for freight services - rail"
   },
   {
    flow: "OUT", 
    code: "271/03", 
    section: "Intellectual property and other services", 
    description: "Payment for freight services - sea"
   },
   {
    flow: "OUT", 
    code: "271/04", 
    section: "Intellectual property and other services", 
    description: "Payment for freight services - air"
   },
   {
    flow: "OUT", 
    code: "272/01", 
    section: "Intellectual property and other services", 
    description: "Payment for other transport services - road"
   },
   {
    flow: "OUT", 
    code: "272/02", 
    section: "Intellectual property and other services", 
    description: "Payment for other transport services - rail"
   },
   {
    flow: "OUT", 
    code: "272/03", 
    section: "Intellectual property and other services", 
    description: "Payment for other transport services - sea"
   },
   {
    flow: "OUT", 
    code: "272/04", 
    section: "Intellectual property and other services", 
    description: "Payment for other transport services - air"
   },
   {
    flow: "OUT", 
    code: "273/01", 
    section: "Intellectual property and other services", 
    description: "Payment for postal and courier services - road"
   },
   {
    flow: "OUT", 
    code: "273/02", 
    section: "Intellectual property and other services", 
    description: "Payment for postal and courier services - rail"
   },
   {
    flow: "OUT", 
    code: "273/03", 
    section: "Intellectual property and other services", 
    description: "Payment for postal and courier services - sea"
   },
   {
    flow: "OUT", 
    code: "273/04", 
    section: "Intellectual property and other services", 
    description: "Payment for postal and courier services - air"
   },
   {
    flow: "OUT", 
    code: "275", 
    section: "Intellectual property and other services", 
    description: "Commission and fees"
   },
   {
    flow: "OUT", 
    code: "276", 
    section: "Intellectual property and other services", 
    description: "Financial service fees charged for advice provided"
   },
   {
    flow: "OUT", 
    code: "280", 
    section: "Intellectual property and other services", 
    description: "Payment for construction services"
   },
   {
    flow: "OUT", 
    code: "281", 
    section: "Intellectual property and other services", 
    description: "Payment for government services"
   },
   {
    flow: "OUT", 
    code: "282", 
    section: "Intellectual property and other services", 
    description: "Diplomatic transfers"
   },
   {
    flow: "OUT", 
    code: "285", 
    section: "Intellectual property and other services", 
    description: "Tuition fees"
   },
   {
    flow: "OUT", 
    code: "287", 
    section: "Intellectual property and other services", 
    description: "Payment for legal services"
   },
   {
    flow: "OUT", 
    code: "288", 
    section: "Intellectual property and other services", 
    description: "Payment for accounting services"
   },
   {
    flow: "OUT", 
    code: "289", 
    section: "Intellectual property and other services", 
    description: "Payment for management consulting services"
   },
   {
    flow: "OUT", 
    code: "290", 
    section: "Intellectual property and other services", 
    description: "Payment for public relation services"
   },
   {
    flow: "OUT", 
    code: "291", 
    section: "Intellectual property and other services", 
    description: "Payment for advertising & market research services"
   },
   {
    flow: "OUT", 
    code: "292", 
    section: "Intellectual property and other services", 
    description: "Payment for managerial services"
   },
   {
    flow: "OUT", 
    code: "293", 
    section: "Intellectual property and other services", 
    description: "Payment for medical and dental services"
   },
   {
    flow: "OUT", 
    code: "294", 
    section: "Intellectual property and other services", 
    description: "Payment for educational services"
   },
   {
    flow: "OUT", 
    code: "295", 
    section: "Intellectual property and other services", 
    description: "Operational leasing"
   },
   {
    flow: "OUT", 
    code: "296", 
    section: "Intellectual property and other services", 
    description: "Payment for cultural and recreational services"
   },
   {
    flow: "OUT", 
    code: "297", 
    section: "Intellectual property and other services", 
    description: "Payment for other business services not included elsewhere"
   },
   {
    flow: "OUT", 
    code: "300", 
    section: "Income and yields on financial assets", 
    description: "Adjustments / Reversals / Refunds related to income and yields on financial assets"
   },
   {
    flow: "OUT", 
    code: "301", 
    section: "Income and yields on financial assets", 
    description: "Dividends"
   },
   {
    flow: "OUT", 
    code: "302", 
    section: "Income and yields on financial assets", 
    description: "Branch profits"
   },
   {
    flow: "OUT", 
    code: "303", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a resident to a resident employee temporarily abroad (excluding remittances)"
   },
   {
    flow: "OUT", 
    code: "304", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a resident to a non-resident employee (excluding remittances)"
   },
   {
    flow: "OUT", 
    code: "305", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a resident to a migrant worker employee (excluding remittances)"
   },
   {
    flow: "OUT", 
    code: "306", 
    section: "Income and yields on financial assets", 
    description: "Compensation paid by a resident to a foreign national contract worker employee (excluding remittances)"
   },
   {
    flow: "OUT", 
    code: "307", 
    section: "Income and yields on financial assets", 
    description: "Commission or brokerage"
   },
   {
    flow: "OUT", 
    code: "308", 
    section: "Income and yields on financial assets", 
    description: "Rental"
   },
   {
    flow: "OUT", 
    code: "309/04", 
    section: "Income and yields on financial assets", 
    description: "Interest paid to a non-resident in respect of shareholders loans"
   },
   {
    flow: "OUT", 
    code: "309/05", 
    section: "Income and yields on financial assets", 
    description: "Interest paid to a non-resident in respect of third party loans"
   },
   {
    flow: "OUT", 
    code: "309/06", 
    section: "Income and yields on financial assets", 
    description: "Interest paid to a non-resident in respect of trade finance loans"
   },
   {
    flow: "OUT", 
    code: "309/07", 
    section: "Income and yields on financial assets", 
    description: "Interest paid to a non-resident in respect of a bond"
   },
   {
    flow: "OUT", 
    code: "309/08", 
    section: "Income and yields on financial assets", 
    description: "Interest paid not in respect of loans"
   },
   {
    flow: "OUT", 
    code: "312/01", 
    section: "Income and yields on financial assets", 
    description: "Fee in respect of inward listed securities derivatives individual"
   },
   {
    flow: "OUT", 
    code: "312/02", 
    section: "Income and yields on financial assets", 
    description: "Fee in respect of inward listed securities derivatives corporate"
   },
   {
    flow: "OUT", 
    code: "312/03", 
    section: "Income and yields on financial assets", 
    description: "Fee in respect of inward listed securities derivatives bank"
   },
   {
    flow: "OUT", 
    code: "312/04", 
    section: "Income and yields on financial assets", 
    description: "Fee in respect of inward listed securities derivatives institution"
   },
   {
    flow: "OUT", 
    code: "400", 
    section: "Transfers of a current nature", 
    description: "Adjustments / Reversals / Refunds related to transfers of a current nature"
   },
   {
    flow: "OUT", 
    code: "401", 
    section: "Transfers of a current nature", 
    description: "Gifts"
   },
   {
    flow: "OUT", 
    code: "402", 
    section: "Transfers of a current nature", 
    description: "Annual contributions"
   },
   {
    flow: "OUT", 
    code: "403", 
    section: "Transfers of a current nature", 
    description: "Contributions in respect of social security schemes"
   },
   {
    flow: "OUT", 
    code: "404", 
    section: "Transfers of a current nature", 
    description: "Contributions in respect of foreign charitable, religious and cultural (excluding research and development)"
   },
   {
    flow: "OUT", 
    code: "405", 
    section: "Transfers of a current nature", 
    description: "Other donations / aid to a foreign Government (excluding research and development)"
   },
   {
    flow: "OUT", 
    code: "406", 
    section: "Transfers of a current nature", 
    description: "Other donations / aid to a foreign private sector (excluding research and development)"
   },
   {
    flow: "OUT", 
    code: "407", 
    section: "Transfers of a current nature", 
    description: "Pensions"
   },
   {
    flow: "OUT", 
    code: "408", 
    section: "Transfers of a current nature", 
    description: "Annuities (pension related)"
   },
   {
    flow: "OUT", 
    code: "409", 
    section: "Transfers of a current nature", 
    description: "Inheritances"
   },
   {
    flow: "OUT", 
    code: "410", 
    section: "Transfers of a current nature", 
    description: "Alimony"
   },
   {
    flow: "OUT", 
    code: "411/01", 
    section: "Transfers of a current nature", 
    description: "Tax - Income tax"
   },
   {
    flow: "OUT", 
    code: "411/02", 
    section: "Transfers of a current nature", 
    description: "Tax - VAT refunds"
   },
   {
    flow: "OUT", 
    code: "411/03", 
    section: "Transfers of a current nature", 
    description: "Tax - Other"
   },
   {
    flow: "OUT", 
    code: "412", 
    section: "Transfers of a current nature", 
    description: "Insurance premiums (non life/short term)"
   },
   {
    flow: "OUT", 
    code: "413", 
    section: "Transfers of a current nature", 
    description: "Insurance claims (non life/short term)"
   },
   {
    flow: "OUT", 
    code: "414", 
    section: "Transfers of a current nature", 
    description: "Insurance premiums (life)"
   },
   {
    flow: "OUT", 
    code: "415", 
    section: "Transfers of a current nature", 
    description: "Insurance claims (life)"
   },
   {
    flow: "OUT", 
    code: "416", 
    section: "Transfers of a current nature", 
    description: "Migrant worker remittances (excluding compensation)"
   },
   {
    flow: "OUT", 
    code: "417", 
    section: "Transfers of a current nature", 
    description: "Foreign national contract worker remittances (excluding compensation)"
   },
   {
    flow: "OUT", 
    code: "500", 
    section: "Capital Transfers and emigrants", 
    description: "Adjustments / Reversals / Refunds related to capital transfers and emigrants"
   },
   {
    flow: "OUT", 
    code: "501", 
    section: "Capital Transfers and emigrants", 
    description: "Donations by SA Government for fixed assets"
   },
   {
    flow: "OUT", 
    code: "502", 
    section: "Capital Transfers and emigrants", 
    description: "Donations by corporate entities for fixed assets"
   },
   {
    flow: "OUT", 
    code: "503", 
    section: "Capital Transfers and emigrants", 
    description: "Disinvestment of property by a non-resident corporate entity"
   },
   {
    flow: "OUT", 
    code: "504", 
    section: "Capital Transfers and emigrants", 
    description: "Investment into property by a resident corporate entity"
   },
   {
    flow: "OUT", 
    code: "510/01", 
    section: "Capital Transfers and emigrants", 
    description: "Disinvestment of property by a non-resident individual"
   },
   {
    flow: "OUT", 
    code: "510/02", 
    section: "Capital Transfers and emigrants", 
    description: "Disinvestment by a non-resident individual - other"
   },
   {
    flow: "OUT", 
    code: "511/01", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Shares"
   },
   {
    flow: "OUT", 
    code: "511/02", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Bonds"
   },
   {
    flow: "OUT", 
    code: "511/03", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Money market instruments"
   },
   {
    flow: "OUT", 
    code: "511/04", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Deposits with a foreign bank"
   },
   {
    flow: "OUT", 
    code: "511/05", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Mutual funds / collective investment schemes"
   },
   {
    flow: "OUT", 
    code: "511/06", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Property"
   },
   {
    flow: "OUT", 
    code: "511/07", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual not related to the investment allowance - Other"
   },
   {
    flow: "OUT", 
    code: "512/01", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Shares"
   },
   {
    flow: "OUT", 
    code: "512/02", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Bonds"
   },
   {
    flow: "OUT", 
    code: "512/03", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Money market instruments"
   },
   {
    flow: "OUT", 
    code: "512/04", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Deposits with a foreign bank"
   },
   {
    flow: "OUT", 
    code: "512/05", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Mutual funds / collective investment schemes"
   },
   {
    flow: "OUT", 
    code: "512/06", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Property"
   },
   {
    flow: "OUT", 
    code: "512/07", 
    section: "Capital Transfers and emigrants", 
    description: "Foreign investment by a resident individual in respect of the investment allowance - Other"
   },
   {
    flow: "OUT", 
    code: "513", 
    section: "Capital Transfers and emigrants", 
    description: "Investment by a resident individual originating from a local source into an account conducted in foreign currency held at an Authorised Dealer in South Africa"
   },
   {
    flow: "OUT", 
    code: "530/01", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - fixed property"
   },
   {
    flow: "OUT", 
    code: "530/02", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - listed investments"
   },
   {
    flow: "OUT", 
    code: "530/03", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - unlisted investments"
   },
   {
    flow: "OUT", 
    code: "530/04", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - insurance policies"
   },
   {
    flow: "OUT", 
    code: "530/05", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - cash"
   },
   {
    flow: "OUT", 
    code: "530/06", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - debtors"
   },
   {
    flow: "OUT", 
    code: "530/07", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance - capital distribution from trusts"
   },
   {
    flow: "OUT", 
    code: "530/08", 
    section: "Capital Transfers and emigrants", 
    description: "Emigration foreign capital allowance -other assets"
   },
   {
    flow: "OUT", 
    code: "600", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Adjustments / Reversals / Refunds related to financial investments/disinvestments and prudential investments"
   },
   {
    flow: "OUT", 
    code: "601/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Listed shares - sale proceeds paid to a non-resident"
   },
   {
    flow: "OUT", 
    code: "601/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Non-listed shares - sale proceeds paid to a non-resident"
   },
   {
    flow: "OUT", 
    code: "602", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of money market instruments by a non-resident"
   },
   {
    flow: "OUT", 
    code: "603/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of listed bonds by a non-resident (excluding loans)"
   },
   {
    flow: "OUT", 
    code: "603/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Disinvestment of non-listed bonds by a non-resident (excluding loans)"
   },
   {
    flow: "OUT", 
    code: "605/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Agriculture, hunting, forestry and fishing"
   },
   {
    flow: "OUT", 
    code: "605/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Mining, quarrying and exploration"
   },
   {
    flow: "OUT", 
    code: "605/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Manufacturing"
   },
   {
    flow: "OUT", 
    code: "605/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Electricity, gas and water supply"
   },
   {
    flow: "OUT", 
    code: "605/05", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Construction"
   },
   {
    flow: "OUT", 
    code: "605/06", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Wholesale, retail, repairs, hotel and restaurants"
   },
   {
    flow: "OUT", 
    code: "605/07", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Transport and communication"
   },
   {
    flow: "OUT", 
    code: "605/08", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Financial services"
   },
   {
    flow: "OUT", 
    code: "605/09", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Investment into shares by a resident entity - Community, social and personal services"
   },
   {
    flow: "OUT", 
    code: "610/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity individual"
   },
   {
    flow: "OUT", 
    code: "610/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity corporate"
   },
   {
    flow: "OUT", 
    code: "610/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity bank"
   },
   {
    flow: "OUT", 
    code: "610/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities equity institution"
   },
   {
    flow: "OUT", 
    code: "611/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt individual"
   },
   {
    flow: "OUT", 
    code: "611/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt corporate"
   },
   {
    flow: "OUT", 
    code: "611/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt bank"
   },
   {
    flow: "OUT", 
    code: "611/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities debt institution"
   },
   {
    flow: "OUT", 
    code: "612/01", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives individual"
   },
   {
    flow: "OUT", 
    code: "612/02", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives corporate"
   },
   {
    flow: "OUT", 
    code: "612/03", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives bank"
   },
   {
    flow: "OUT", 
    code: "612/04", 
    section: "Financial investments/disinvestments (excluding local institutional investors)", 
    description: "Inward listed securities derivatives institution"
   },
   {
    flow: "OUT", 
    code: "615/01", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Investment by resident institutional investor - Asset Manager"
   },
   {
    flow: "OUT", 
    code: "615/02", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Investment by resident institutional investor - Collective Investment Scheme"
   },
   {
    flow: "OUT", 
    code: "615/03", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Investment by resident institutional investor - Retirement Fund"
   },
   {
    flow: "OUT", 
    code: "615/04", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Investment by resident institutional investor - Life Linked"
   },
   {
    flow: "OUT", 
    code: "615/05", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Investment by resident institutional investor - Life Non Linked"
   },
   {
    flow: "OUT", 
    code: "616", 
    section: "Financial investments/disinvestments and Prudential investments", 
    description: "Bank prudential investment"
   },
   {
    flow: "OUT", 
    code: "700", 
    section: "Derivatives", 
    description: "Adjustments / Reversals / Refunds related to derivatives"
   },
   {
    flow: "OUT", 
    code: "701/01", 
    section: "Derivatives", 
    description: "Options - listed"
   },
   {
    flow: "OUT", 
    code: "701/02", 
    section: "Derivatives", 
    description: "Options - unlisted"
   },
   {
    flow: "OUT", 
    code: "702/01", 
    section: "Derivatives", 
    description: "Futures - listed"
   },
   {
    flow: "OUT", 
    code: "702/02", 
    section: "Derivatives", 
    description: "Futures - unlisted"
   },
   {
    flow: "OUT", 
    code: "703/01", 
    section: "Derivatives", 
    description: "Warrants - listed"
   },
   {
    flow: "OUT", 
    code: "703/02", 
    section: "Derivatives", 
    description: "Warrants - unlisted"
   },
   {
    flow: "OUT", 
    code: "704/01", 
    section: "Derivatives", 
    description: "Gold hedging - listed"
   },
   {
    flow: "OUT", 
    code: "704/02", 
    section: "Derivatives", 
    description: "Gold hedging - unlisted"
   },
   {
    flow: "OUT", 
    code: "705/01", 
    section: "Derivatives", 
    description: "Derivative not specified above - listed"
   },
   {
    flow: "OUT", 
    code: "705/02", 
    section: "Derivatives", 
    description: "Derivative not specified above - unlisted"
   },
   {
    flow: "OUT", 
    code: "800", 
    section: "Loan and Miscellaneous payments", 
    description: "Adjustments / Reversals / Refunds related to loan and miscellaneous payments"
   },
   {
    flow: "OUT", 
    code: "801", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of trade finance drawn down in South Africa"
   },
   {
    flow: "OUT", 
    code: "802", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment of an international Bond drawn down"
   },
   {
    flow: "OUT", 
    code: "803", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment by a resident of a loan received from a non-resident shareholder"
   },
   {
    flow: "OUT", 
    code: "804", 
    section: "Loan and Miscellaneous payments", 
    description: "Repayment by a resident of a loan received from a non-resident third party"
   },
   {
    flow: "OUT", 
    code: "810", 
    section: "Loan and Miscellaneous payments", 
    description: "Loan made by a resident to a resident temporarily abroad"
   },
   {
    flow: "OUT", 
    code: "815", 
    section: "Loan and Miscellaneous payments", 
    description: "Individual loan to a non-resident"
   },
   {
    flow: "OUT", 
    code: "816", 
    section: "Loan and Miscellaneous payments", 
    description: "Study loan to a non-resident"
   },
   {
    flow: "OUT", 
    code: "817", 
    section: "Loan and Miscellaneous payments", 
    description: "Shareholders loan to a non-resident"
   },
   {
    flow: "OUT", 
    code: "818", 
    section: "Loan and Miscellaneous payments", 
    description: "Third party loan to a non-resident (excluding shareholders)"
   },
   {
    flow: "OUT", 
    code: "819", 
    section: "Loan and Miscellaneous payments", 
    description: "Trade finance to a non-resident"
   },
   {
    flow: "OUT", 
    code: "830", 
    section: "Loan and Miscellaneous payments", 
    description: "Details of payments not classified"
   },
   {
    flow: "OUT", 
    code: "831", 
    section: "Loan and Miscellaneous payments", 
    description: "Rand collections for the credit of vostro accounts"
   },
   {
    flow: "OUT", 
    code: "833", 
    section: "Loan and Miscellaneous payments", 
    description: "Credit/Debit card company settlement as well as money remitter settlements"
   },
   {
    flow: "IN", 
    code: "ZZ1", 
    section: "Special - Report", 
    description: "Reportable as Non Reportable"
   },
   {
    flow: "OUT", 
    code: "ZZ1", 
    section: "Special - Report", 
    description: "Reportable as Non Reportable"
   },
   {
    flow: "IN", 
    code: "ZZ2", 
    section: "Special - Do Not Report", 
    description: "Not Reportable (Excluded)"
   },
   {
    flow: "OUT", 
    code: "ZZ2", 
    section: "Special - Do Not Report", 
    description: "Not Reportable (Excluded)"
   },
   {
    flow: "OUT", 
    code: "999", 
    section: "Test", 
    description: "Testing"
   },
   {
    flow: "OUT", 
    code: "998/10", 
    section: "Test", 
    description: "Test"
   },
   {
    flow: "OUT", 
    code: "996", 
    section: "Testing Category", 
    description: "Testing BOP Cat"
   }
  ], 
  countries: [
   {
    code: "AD", 
    name: "Andorra"
   },
   {
    code: "AE", 
    name: "United Arab Emirates"
   },
   {
    code: "AF", 
    name: "Afghanistan"
   },
   {
    code: "AG", 
    name: "Antigua and Barbuda"
   },
   {
    code: "AI", 
    name: "Anguilla"
   },
   {
    code: "AL", 
    name: "Albania"
   },
   {
    code: "AM", 
    name: "Armenia"
   },
   {
    code: "AN", 
    name: "Netherlands Antilles"
   },
   {
    code: "AO", 
    name: "Angola"
   },
   {
    code: "AQ", 
    name: "Antarctica"
   },
   {
    code: "AR", 
    name: "Argentina"
   },
   {
    code: "AS", 
    name: "American Samoa"
   },
   {
    code: "AT", 
    name: "Austria"
   },
   {
    code: "AU", 
    name: "Australia"
   },
   {
    code: "AW", 
    name: "Aruba"
   },
   {
    code: "AX", 
    name: "Aland Islands"
   },
   {
    code: "AZ", 
    name: "Azerbaijan"
   },
   {
    code: "BA", 
    name: "Bosnia and Herzegovina"
   },
   {
    code: "BB", 
    name: "Barbados"
   },
   {
    code: "BD", 
    name: "Bangladesh"
   },
   {
    code: "BE", 
    name: "Belgium"
   },
   {
    code: "BF", 
    name: "Burkina Faso"
   },
   {
    code: "BG", 
    name: "Bulgaria"
   },
   {
    code: "BH", 
    name: "Bahrain"
   },
   {
    code: "BI", 
    name: "Burundi"
   },
   {
    code: "BJ", 
    name: "Benin"
   },
   {
    code: "BL", 
    name: "Saint Bartholemy"
   },
   {
    code: "BM", 
    name: "Bermuda"
   },
   {
    code: "BN", 
    name: "Brunei Darussalam"
   },
   {
    code: "BO", 
    name: "Bolivia, Plurinational State of"
   },
   {
    code: "BQ", 
    name: "Bonaire"
   },
   {
    code: "BR", 
    name: "Brazil"
   },
   {
    code: "BS", 
    name: "Bahamas"
   },
   {
    code: "BT", 
    name: "Bhutan"
   },
   {
    code: "BV", 
    name: "Bouvet Island"
   },
   {
    code: "BW", 
    name: "Botswana"
   },
   {
    code: "BY", 
    name: "Belarus"
   },
   {
    code: "BZ", 
    name: "Belize"
   },
   {
    code: "CA", 
    name: "Canada"
   },
   {
    code: "CC", 
    name: "Cocos (Keeling) Islands"
   },
   {
    code: "CD", 
    name: "Congo, the Democratic Republic of the"
   },
   {
    code: "CF", 
    name: "Central African Republic"
   },
   {
    code: "CG", 
    name: "Congo"
   },
   {
    code: "CH", 
    name: "Switzerland"
   },
   {
    code: "CI", 
    name: "Cote d'Ivoire"
   },
   {
    code: "CK", 
    name: "Cook Islands"
   },
   {
    code: "CL", 
    name: "Chile"
   },
   {
    code: "CM", 
    name: "Cameroon"
   },
   {
    code: "CN", 
    name: "China"
   },
   {
    code: "CO", 
    name: "Colombia"
   },
   {
    code: "CR", 
    name: "Costa Rica"
   },
   {
    code: "CS", 
    name: "Former Czechoslovak (Serbia and Montenegro)"
   },
   {
    code: "CU", 
    name: "Cuba"
   },
   {
    code: "CV", 
    name: "Cape Verde"
   },
   {
    code: "CX", 
    name: "Christmas Island"
   },
   {
    code: "CY", 
    name: "Cyprus"
   },
   {
    code: "CZ", 
    name: "Czech Republic"
   },
   {
    code: "DE", 
    name: "Germany"
   },
   {
    code: "DJ", 
    name: "Djibouti"
   },
   {
    code: "DK", 
    name: "Denmark"
   },
   {
    code: "DM", 
    name: "Dominica"
   },
   {
    code: "DO", 
    name: "Dominican Republic"
   },
   {
    code: "DZ", 
    name: "Algeria"
   },
   {
    code: "EC", 
    name: "Ecuador"
   },
   {
    code: "EE", 
    name: "Estonia"
   },
   {
    code: "EG", 
    name: "Egypt"
   },
   {
    code: "EH", 
    name: "Western Sahara"
   },
   {
    code: "ER", 
    name: "Eritrea"
   },
   {
    code: "ES", 
    name: "Spain"
   },
   {
    code: "ET", 
    name: "Ethiopia"
   },
   {
    code: "EU", 
    name: "European Union"
   },
   {
    code: "FI", 
    name: "Finland"
   },
   {
    code: "FJ", 
    name: "Fiji"
   },
   {
    code: "FK", 
    name: "Falkland Islands (Malvinas)"
   },
   {
    code: "FM", 
    name: "Micronesia, Federated States of"
   },
   {
    code: "FO", 
    name: "Faroe Islands"
   },
   {
    code: "FR", 
    name: "France"
   },
   {
    code: "GA", 
    name: "Gabon"
   },
   {
    code: "GB", 
    name: "United Kingdom"
   },
   {
    code: "GD", 
    name: "Grenada"
   },
   {
    code: "GE", 
    name: "Georgia"
   },
   {
    code: "GF", 
    name: "French Guiana"
   },
   {
    code: "GG", 
    name: "Guernsey"
   },
   {
    code: "GH", 
    name: "Ghana"
   },
   {
    code: "GI", 
    name: "Gibraltar"
   },
   {
    code: "GL", 
    name: "Greenland"
   },
   {
    code: "GM", 
    name: "Gambia"
   },
   {
    code: "GN", 
    name: "Guinea"
   },
   {
    code: "GP", 
    name: "Guadeloupe"
   },
   {
    code: "GQ", 
    name: "Equatorial Guinea"
   },
   {
    code: "GR", 
    name: "Greece"
   },
   {
    code: "GS", 
    name: "South Georgia and the South Sandwich Islands"
   },
   {
    code: "GT", 
    name: "Guatemala"
   },
   {
    code: "GU", 
    name: "Guam"
   },
   {
    code: "GW", 
    name: "Guinea-Bissau"
   },
   {
    code: "GY", 
    name: "Guyana"
   },
   {
    code: "HK", 
    name: "Hong Kong"
   },
   {
    code: "HM", 
    name: "Heard Island and McDonald Islands"
   },
   {
    code: "HN", 
    name: "Honduras"
   },
   {
    code: "HR", 
    name: "Croatia"
   },
   {
    code: "HT", 
    name: "Haiti"
   },
   {
    code: "HU", 
    name: "Hungary"
   },
   {
    code: "ID", 
    name: "Indonesia"
   },
   {
    code: "IE", 
    name: "Ireland"
   },
   {
    code: "IL", 
    name: "Israel"
   },
   {
    code: "IM", 
    name: "Isle of Man"
   },
   {
    code: "IN", 
    name: "India"
   },
   {
    code: "IO", 
    name: "British Indian Ocean Territory"
   },
   {
    code: "IQ", 
    name: "Iraq"
   },
   {
    code: "IR", 
    name: "Iran, Islamic Republic of"
   },
   {
    code: "IS", 
    name: "Iceland"
   },
   {
    code: "IT", 
    name: "Italy"
   },
   {
    code: "JE", 
    name: "Jersey"
   },
   {
    code: "JM", 
    name: "Jamaica"
   },
   {
    code: "JO", 
    name: "Jordan"
   },
   {
    code: "JP", 
    name: "Japan"
   },
   {
    code: "KE", 
    name: "Kenya"
   },
   {
    code: "KG", 
    name: "Kyrgyzstan"
   },
   {
    code: "KH", 
    name: "Cambodia"
   },
   {
    code: "KI", 
    name: "Kiribati"
   },
   {
    code: "KM", 
    name: "Comoros"
   },
   {
    code: "KN", 
    name: "Saint Kitts and Nevis"
   },
   {
    code: "KP", 
    name: "Korea, Democratic Peoples Republic of"
   },
   {
    code: "KR", 
    name: "Korea, Republic of"
   },
   {
    code: "KW", 
    name: "Kuwait"
   },
   {
    code: "KY", 
    name: "Cayman Islands"
   },
   {
    code: "KZ", 
    name: "Kazakhstan"
   },
   {
    code: "LA", 
    name: "Lao People's Democratic Republic"
   },
   {
    code: "LB", 
    name: "Lebanon"
   },
   {
    code: "LC", 
    name: "Saint Lucia"
   },
   {
    code: "LI", 
    name: "Liechtenstein"
   },
   {
    code: "LK", 
    name: "Sri Lanka"
   },
   {
    code: "LR", 
    name: "Liberia"
   },
   {
    code: "LS", 
    name: "Lesotho"
   },
   {
    code: "LT", 
    name: "Lithuania"
   },
   {
    code: "LU", 
    name: "Luxembourg"
   },
   {
    code: "LV", 
    name: "Latvia"
   },
   {
    code: "LY", 
    name: "Libya"
   },
   {
    code: "MA", 
    name: "Morocco"
   },
   {
    code: "MC", 
    name: "Monaco"
   },
   {
    code: "MD", 
    name: "Moldova, Republic of"
   },
   {
    code: "ME", 
    name: "Montenegro"
   },
   {
    code: "MF", 
    name: "Saint Martin (French part)"
   },
   {
    code: "MG", 
    name: "Madagascar"
   },
   {
    code: "MH", 
    name: "Marshall Islands"
   },
   {
    code: "MK", 
    name: "Macedonia, The Former Yugoslav Republic of"
   },
   {
    code: "ML", 
    name: "Mali"
   },
   {
    code: "MM", 
    name: "Myanmar"
   },
   {
    code: "MN", 
    name: "Mongolia"
   },
   {
    code: "MO", 
    name: "Macao"
   },
   {
    code: "MP", 
    name: "Northern Mariana Islands"
   },
   {
    code: "MQ", 
    name: "Martinique"
   },
   {
    code: "MR", 
    name: "Mauritania"
   },
   {
    code: "MS", 
    name: "Montserrat"
   },
   {
    code: "MT", 
    name: "Malta"
   },
   {
    code: "MU", 
    name: "Mauritius"
   },
   {
    code: "MV", 
    name: "Maldives"
   },
   {
    code: "MW", 
    name: "Malawi"
   },
   {
    code: "MX", 
    name: "Mexico"
   },
   {
    code: "MY", 
    name: "Malaysia"
   },
   {
    code: "MZ", 
    name: "Mozambique"
   },
   {
    code: "NA", 
    name: "Namibia"
   },
   {
    code: "NC", 
    name: "New Caledonia"
   },
   {
    code: "NE", 
    name: "Niger"
   },
   {
    code: "NF", 
    name: "Norfolk Island"
   },
   {
    code: "NG", 
    name: "Nigeria"
   },
   {
    code: "NI", 
    name: "Nicaragua"
   },
   {
    code: "NL", 
    name: "Netherlands"
   },
   {
    code: "NO", 
    name: "Norway"
   },
   {
    code: "NP", 
    name: "Nepal"
   },
   {
    code: "NR", 
    name: "Nauru"
   },
   {
    code: "NT", 
    name: "Neutral Zone"
   },
   {
    code: "NU", 
    name: "Niue"
   },
   {
    code: "NZ", 
    name: "New Zealand"
   },
   {
    code: "OM", 
    name: "Oman"
   },
   {
    code: "PA", 
    name: "Panama"
   },
   {
    code: "PB", 
    name: "Panama (VISA Card)"
   },
   {
    code: "PE", 
    name: "Peru"
   },
   {
    code: "PF", 
    name: "French Polynesia"
   },
   {
    code: "PG", 
    name: "Papua New Guinea"
   },
   {
    code: "PH", 
    name: "Philippines"
   },
   {
    code: "PK", 
    name: "Pakistan"
   },
   {
    code: "PL", 
    name: "Poland"
   },
   {
    code: "PM", 
    name: "Saint Pierre and Miquelon"
   },
   {
    code: "PN", 
    name: "Pitcairn"
   },
   {
    code: "PR", 
    name: "Puerto Rico"
   },
   {
    code: "PS", 
    name: "Palestine, State of"
   },
   {
    code: "PT", 
    name: "Portugal"
   },
   {
    code: "PW", 
    name: "Palau"
   },
   {
    code: "PY", 
    name: "Paraguay"
   },
   {
    code: "PZ", 
    name: "Panama Canal Zone"
   },
   {
    code: "QA", 
    name: "Qatar"
   },
   {
    code: "QZ", 
    name: "Kosovo"
   },
   {
    code: "RE", 
    name: "R\u00C3\u0192\u00C2\u00A9union"
   },
   {
    code: "RO", 
    name: "Romania"
   },
   {
    code: "RS", 
    name: "Serbia"
   },
   {
    code: "RU", 
    name: "Russian Federation"
   },
   {
    code: "RW", 
    name: "Rwanda"
   },
   {
    code: "SA", 
    name: "Saudi Arabia"
   },
   {
    code: "SB", 
    name: "Solomon Islands"
   },
   {
    code: "SC", 
    name: "Seychelles"
   },
   {
    code: "SD", 
    name: "Sudan"
   },
   {
    code: "SE", 
    name: "Sweden"
   },
   {
    code: "SG", 
    name: "Singapore"
   },
   {
    code: "SH", 
    name: "Saint Helena, Ascension and Tristan da Cunha"
   },
   {
    code: "SI", 
    name: "Slovenia"
   },
   {
    code: "SJ", 
    name: "Svalbard and Jan Mayen"
   },
   {
    code: "SK", 
    name: "Slovakia"
   },
   {
    code: "SL", 
    name: "Sierra Leone"
   },
   {
    code: "SM", 
    name: "San Marino"
   },
   {
    code: "SN", 
    name: "Senegal"
   },
   {
    code: "SO", 
    name: "Somalia"
   },
   {
    code: "SR", 
    name: "Suriname"
   },
   {
    code: "SS", 
    name: "South Sudan"
   },
   {
    code: "ST", 
    name: "Sao Tome and Principe"
   },
   {
    code: "SV", 
    name: "El Salvador"
   },
   {
    code: "SX", 
    name: "ST Maarten"
   },
   {
    code: "SY", 
    name: "Syrian Arab Republic"
   },
   {
    code: "SZ", 
    name: "Swaziland"
   },
   {
    code: "TC", 
    name: "Turks and Caicos Islands"
   },
   {
    code: "TD", 
    name: "Chad"
   },
   {
    code: "TF", 
    name: "French Southern Territories"
   },
   {
    code: "TG", 
    name: "Togo"
   },
   {
    code: "TH", 
    name: "Thailand"
   },
   {
    code: "TJ", 
    name: "Tajikistan"
   },
   {
    code: "TK", 
    name: "Tokelau"
   },
   {
    code: "TL", 
    name: "Timor-Leste"
   },
   {
    code: "TM", 
    name: "Turkmenistan"
   },
   {
    code: "TN", 
    name: "Tunisia"
   },
   {
    code: "TO", 
    name: "Tonga"
   },
   {
    code: "TP", 
    name: "East Timor"
   },
   {
    code: "TR", 
    name: "Turkey"
   },
   {
    code: "TT", 
    name: "Trinidad and Tobago"
   },
   {
    code: "TV", 
    name: "Tuvalu"
   },
   {
    code: "TW", 
    name: "Taiwan, Province of China"
   },
   {
    code: "TZ", 
    name: "Tanzania, United Republic of"
   },
   {
    code: "UA", 
    name: "Ukraine"
   },
   {
    code: "UG", 
    name: "Uganda"
   },
   {
    code: "UM", 
    name: "United States Minor Outlying Islands"
   },
   {
    code: "US", 
    name: "United States"
   },
   {
    code: "UT", 
    name: "Country"
   },
   {
    code: "UY", 
    name: "Uruguay"
   },
   {
    code: "UZ", 
    name: "Uzbekistan"
   },
   {
    code: "VA", 
    name: "Holy See (Vatican City State)"
   },
   {
    code: "VC", 
    name: "Saint Vincent and the Grenadines"
   },
   {
    code: "VE", 
    name: "Venezuela, Bolivarian Republic of"
   },
   {
    code: "VG", 
    name: "Virgin Islands, British"
   },
   {
    code: "VI", 
    name: "Virgin Islands, U.S."
   },
   {
    code: "VN", 
    name: "Vietnam"
   },
   {
    code: "VU", 
    name: "Vanuatu"
   },
   {
    code: "WF", 
    name: "Wallis and Futuna"
   },
   {
    code: "WS", 
    name: "Samoa"
   },
   {
    code: "XC", 
    name: "Leeward & Windward Islands"
   },
   {
    code: "XK", 
    name: "Kosovo"
   },
   {
    code: "XX", 
    name: "Various - For FDM"
   },
   {
    code: "YE", 
    name: "Yemen"
   },
   {
    code: "YT", 
    name: "Mayotte"
   },
   {
    code: "YU", 
    name: "Yugoslavia"
   },
   {
    code: "ZA", 
    name: "South Africa"
   },
   {
    code: "ZM", 
    name: "Zambia"
   },
   {
    code: "ZW", 
    name: "Zimbabwe"
   }
  ], 
  adhocSubjects: ["AIRPORT", "DEBT", "DERIVATIVES", "ECCIRCULARD445", "EQUITY", "EXPORTPROCEEDS", "FOREIGNPORTFOLIO", "HOLDCO", "IHQ", "IHQ002", "IHQ003", "IHQ004", "IHQ005", "IHQ008", "IHQ009", "IHQ010", "IHQ011", "IHQ012", "IHQ014", "IHQ022", "IHQ023", "IHQ024", "IHQ028", "INVALIDIDNUMBER", "INWARDLISTING", "NO", "REMITTANCE DISPENSATION", "SDA", "SETOFF", "YES", "ZAMBIAN GRAIN"], 
  branches: [
   {
    code: "00010255", 
    name: "GLENWOOD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "03414500", 
    name: "SBG  Mobile App", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01065300", 
    name: "TPS Special Transfers", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002233", 
    name: "ALIWAL NORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002565", 
    name: "AMANZIMTOTI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001982", 
    name: "ARCADIA", 
    validFrom: "2000-01-01 00:00:00.0", 
    validTo: "2020-09-01 00:00:00.0"
   },
   {
    code: "00026718", 
    name: "BALLITO", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002419", 
    name: "BARBERTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00033631", 
    name: "BARONGWA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002100", 
    name: "BAYSIDE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002222", 
    name: "BEAUFORT WEST", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002070", 
    name: "BEDFORD GARDENS", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002473", 
    name: "BELA BELA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002146", 
    name: "BELLVILLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00029680", 
    name: "BERRY'S CORNER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002508", 
    name: "BETHLEHEM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002120", 
    name: "BLUE ROUTE CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001996", 
    name: "BOKSBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001062", 
    name: "BRAAMFONTEIN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002535", 
    name: "BRANDWAG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002225", 
    name: "BREDASDORP", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002413", 
    name: "BRITS", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00035468", 
    name: "BRITS MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002457", 
    name: "BRONKHORSTSPRUIT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001986", 
    name: "BROOKLYN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002448", 
    name: "BURGERSFORT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002485", 
    name: "BUSHBUCKRIDGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002278", 
    name: "BUTTERWORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002247", 
    name: "CALEDON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024624", 
    name: "CAPEGATE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002052", 
    name: "CARLETONVILLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002696", 
    name: "CARRINGTON STREET", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00012447", 
    name: "CASTLE WALK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002463", 
    name: "CENTRAL CITY (MABOPANE)", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002015", 
    name: "CENTURION", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00025988", 
    name: "CENTURION MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00017853", 
    name: "CENTURY CITY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002221", 
    name: "CERES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002196", 
    name: "CHATSWORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002805", 
    name: "CIB:MONEY MKTS GM ", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002224", 
    name: "CITRUSDAL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002242", 
    name: "CLANWILLIAM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002115", 
    name: "CLAREMONT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00023460", 
    name: "CLEARWATER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002117", 
    name: "CONSTANTIA VILLAGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00043810", 
    name: "CRADLESTONE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002253", 
    name: "CRADOCK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00044139", 
    name: "CRESTA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002246", 
    name: "DARLING", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002266", 
    name: "DE AAR", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020528", 
    name: "DIRECT DELIVERY SERVICE, DBN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024974", 
    name: "DIRECT DELIVERY SERVICE, JHB", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002570", 
    name: "DUNDEE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002163", 
    name: "DURBAN ABC", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002183", 
    name: "DURBAN NORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002298", 
    name: "DUTYWA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002234", 
    name: "EAST LONDON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00006041", 
    name: "EAST RAND MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002074", 
    name: "EASTGATE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001022", 
    name: "ELLIS PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002568", 
    name: "EMPANGENI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002453", 
    name: "ERMELO", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002575", 
    name: "ESHOWE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002571", 
    name: "ESTCOURT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002519", 
    name: "FICKSBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002158", 
    name: "FISH HOEK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001142", 
    name: "FORDSBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00016433", 
    name: "FOURWAYS CROSSING", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002326", 
    name: "FRANSCHHOEK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00150900", 
    name: "FTS CAPE TOWN", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA02", 
    name: "FTS CAPE TOWN", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01152600", 
    name: "FTS DURBAN", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA04", 
    name: "FTS DURBAN", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "05342100", 
    name: "FTS EAST LONDON", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA05", 
    name: "FTS EAST LONDON", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01654243", 
    name: "FTS EAST RAND", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA06", 
    name: "FTS EAST RAND", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01776600", 
    name: "FTS JOHANNESBURG", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA07", 
    name: "FTS JOHANNESBURG", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "02414500", 
    name: "FTS MIDRAND", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA08", 
    name: "FTS MIDRAND", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "05231700", 
    name: "FTS PORT ELIZABETH", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA10", 
    name: "FTS PORT ELIZABETH", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01414500", 
    name: "FTS PRETORIA", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA11", 
    name: "FTS PRETORIA", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "02025300", 
    name: "FTS RANDBURG", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA12", 
    name: "FTS RANDBURG", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01025300", 
    name: "FTS SANDTON", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA13", 
    name: "FTS SANDTON", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "02776600", 
    name: "FTS VEREENIGING", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA14", 
    name: "FTS VEREENIGING", 
    hubCode: "ZA00", 
    hubName: "FOREIGN TRADE SERVICES SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002469", 
    name: "GA-RANKUWA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00019530", 
    name: "GATEWAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002271", 
    name: "GEORGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001992", 
    name: "GERMISTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002180", 
    name: "GLENWOOD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00003316", 
    name: "GOLDEN WALK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00005543", 
    name: "GRAAFF-REINET", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002269", 
    name: "GRABOUW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002376", 
    name: "GRAHAMSTOWN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002294", 
    name: "GREENACRES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002055", 
    name: "GREENSTONE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002569", 
    name: "GREYTOWN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002426", 
    name: "GROBLERSDAL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00016930", 
    name: "HAMMANSKRAAL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00028782", 
    name: "HARBOUR VIEW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002529", 
    name: "HARRISMITH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00006039", 
    name: "HARTBEESPOORT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002320", 
    name: "HARTSWATER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001991", 
    name: "HATFIELD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002844", 
    name: "HAZYVIEW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002452", 
    name: "HEIDELBERG (GP)", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002154", 
    name: "HELDERBERG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002290", 
    name: "HERMANUS", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000999", 
    name: "HILLBROW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002202", 
    name: "HILLCREST", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024034", 
    name: "HILLCREST BOULEVARD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002449", 
    name: "HOEDSPRUIT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002608", 
    name: "HOWICK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002228", 
    name: "HUMANSDORP", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001399", 
    name: "HYDE PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00405500", 
    name: "INVESTOR SERVICES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002236", 
    name: "IXOPO", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00022793", 
    name: "JANE FURSE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002690", 
    name: "JEFFREYS BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000035", 
    name: "JOHANNESBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00204200", 
    name: "KAY CAMAROONDEEN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00023987", 
    name: "KEMPTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002010", 
    name: "KEMPTON PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002049", 
    name: "KEY WEST", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00025994", 
    name: "KHAYELITSHA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001492", 
    name: "KILLARNEY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002217", 
    name: "KIMBERLEY MEGA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00005547", 
    name: "KING WILLIAMS TOWN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002162", 
    name: "KINGSMEAD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002420", 
    name: "KLERKSDORP", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002292", 
    name: "KNYSNA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002257", 
    name: "KOKSTAD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002850", 
    name: "KOMATIPOORT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002532", 
    name: "KROONSTAD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002283", 
    name: "KURUMAN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002539", 
    name: "LADYBRAND", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002577", 
    name: "LADYSMITH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002264", 
    name: "LAMBERTS BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00029145", 
    name: "LAMBTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002414", 
    name: "LEPHALALE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002409", 
    name: "LICHTENBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002416", 
    name: "LOUIS TRICHARDT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002279", 
    name: "LUSIKISIKI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002430", 
    name: "LYDENBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002011", 
    name: "LYNNWOOD RIDGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002237", 
    name: "MAFIKENG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002482", 
    name: "MALELANE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00035390", 
    name: "MALL OF THE NORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002324", 
    name: "MALMESBURY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002984", 
    name: "MAMELODI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00029004", 
    name: "MAPONYA MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002822", 
    name: "MARBLE HALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002566", 
    name: "MARGATE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002299", 
    name: "MATATIELE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00033065", 
    name: "MBOMBELA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001303", 
    name: "MELVILLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002009", 
    name: "MENLYN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002429", 
    name: "MIDDELBURG (MPUM)", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024278", 
    name: "MIDLANDS", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000250", 
    name: "MIDRAND", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002128", 
    name: "MILNERTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002129", 
    name: "MITCHELL'S PLAIN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002281", 
    name: "MMABATHO", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002444", 
    name: "MODIMOLLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00017697", 
    name: "MOGALE CITY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002427", 
    name: "MOKOPANE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002270", 
    name: "MONTAGU", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024035", 
    name: "MONTANA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002311", 
    name: "MOSSEL BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00033212", 
    name: "MSUNDUZI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002349", 
    name: "MTHATHA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020816", 
    name: "MTHATHA WEST", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002581", 
    name: "MTUBATUBA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002181", 
    name: "MUSGRAVE ROAD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002428", 
    name: "MUSINA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002458", 
    name: "NELSPRUIT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020704", 
    name: "NELSPRUIT CROSSING", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002576", 
    name: "NEWCASTLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00041082", 
    name: "NEWCASTLE MALL ", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002007", 
    name: "NIGEL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00013569", 
    name: "NORTHAM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020730", 
    name: "NORTHGATE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002865", 
    name: "NORWOOD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024975", 
    name: "OPS: JOHANNESBURG CASH CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002329", 
    name: "OUDTSHOORN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002267", 
    name: "PAARL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00032756", 
    name: "PARKVIEW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002145", 
    name: "PAROW CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002466", 
    name: "PHALABORWA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00003040", 
    name: "PHOENIX", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00030206", 
    name: "PHUTHADITJHABA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002274", 
    name: "PIER 14", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002484", 
    name: "PIET RETIEF", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002564", 
    name: "PIETERMARITZBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00003170", 
    name: "PINE CREST CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002201", 
    name: "PINETOWN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00023275", 
    name: "PLATINA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002356", 
    name: "PLETTENBERG BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002407", 
    name: "PONGOLA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002230", 
    name: "PORT ELIZABETH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00016613", 
    name: "PORT ELIZABETH CASH SERVICES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002450", 
    name: "POTCHEFSTROOM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001974", 
    name: "PRETORIA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001978", 
    name: "PRETORIA NORTH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002584", 
    name: "PROSPECTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001432", 
    name: "PROTEA GARDENS (JABULANI)", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002388", 
    name: "QUEENSTOWN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002061", 
    name: "RANDBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002597", 
    name: "RICHARDS BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002313", 
    name: "RINK STREET", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000276", 
    name: "RIVONIA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002310", 
    name: "ROBERTSON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002114", 
    name: "RONDEBOSCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000954", 
    name: "ROSEBANK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001995", 
    name: "ROSSLYN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002425", 
    name: "RUSTENBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01244200", 
    name: "SA AIRWAYS PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002478", 
    name: "SABIE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002075", 
    name: "SANDTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002064", 
    name: "SANDTON CITY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00024966", 
    name: "SANRIDGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002572", 
    name: "SCOTTBURGH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002105", 
    name: "SEA POINT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002486", 
    name: "SECUNDA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001979", 
    name: "SILVERTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002477", 
    name: "SIYABUSWA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00026709", 
    name: "SOSHANGUVE PLAZA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001363", 
    name: "SOUTHDALE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01380500", 
    name: "SPECIALISED TRADE", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002323", 
    name: "SPRINGBOK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002005", 
    name: "SPRINGS", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002460", 
    name: "STANDERTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002567", 
    name: "STANGER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002343", 
    name: "STELLENBOSCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00498400", 
    name: "STRUCTURED TRADE FINANCE", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002454", 
    name: "SUN CITY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002328", 
    name: "SWELLENDAM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00023136", 
    name: "THABAZIMBI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001285", 
    name: "THE GLEN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002089", 
    name: "THIBAULT SQUARE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002456", 
    name: "THOHOYANDOU", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00005531", 
    name: "TONGAAT", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00065300", 
    name: "TPS PAYMENTS HIGH CARE/ZAPS ", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "01015500", 
    name: "TPS PAYMENTS PROCESSING", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "02015500", 
    name: "TPS TRADE PROCESSING", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00038729", 
    name: "TRADE ROUTE MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002307", 
    name: "TYGER MANOR", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002446", 
    name: "TZANEEN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002312", 
    name: "UITENHAGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00028990", 
    name: "ULUNDI", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002585", 
    name: "UMHLANGA RIDGE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00003374", 
    name: "UNDERBERG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002352", 
    name: "UPINGTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00043971", 
    name: "V&A WATERFRONT SERVICE CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002037", 
    name: "VEREENIGING", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00005530", 
    name: "VERULAM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00019197", 
    name: "VINCENT PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002327", 
    name: "VREDENBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002353", 
    name: "VREDENDAL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002260", 
    name: "VRYBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002587", 
    name: "VRYHEID", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00029681", 
    name: "WALMER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00014388", 
    name: "WATERFALL MALL RUSTENBURG", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002536", 
    name: "WELKOM", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002355", 
    name: "WELLINGTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00011813", 
    name: "WESTGATE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002199", 
    name: "WESTVILLE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00009907", 
    name: "WESTVILLE PAVILION", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002474", 
    name: "WHITE RIVER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002447", 
    name: "WITBANK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000203", 
    name: "WOODMEAD RETAIL PARK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002305", 
    name: "WORCESTER", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002422", 
    name: "ZEERUST", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000892", 
    name: "INVESTOR SERVICES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00010265", 
    name: "TPS PAYMENTS HIGH CARE/ZAPS ", 
    hubCode: "00001400", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002188", 
    name: "BLUFF", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002573", 
    name: "HARBOUR VIEW", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00006282", 
    name: "MIDRAND (BOULDERS)", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000911", 
    name: "NORWOOD", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020085", 
    name: "OPS: BELVILLE CASH SERVICES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002387", 
    name: "PORT ALFRED", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00020088", 
    name: "PORT ELIZABETH CASH SERVICES", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00006035", 
    name: "MENLYN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA18", 
    name: "Itrade Hub Processing", 
    hubCode: "ZA00", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA51", 
    name: "EXLC Johannesburg", 
    hubCode: "ZA00", 
    hubName: "CIB Operations Processing Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00047449", 
    name: "Mall Of The South", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00046154", 
    name: "MALL OF AFRICA", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00000865", 
    name: "GOSS Settlements", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00048325", 
    name: "New Market Service Centre", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA01", 
    name: "TPS Payments and Trade Processing", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00048327", 
    name: "Melrose Arch", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00003307", 
    name: "BEACONS BAY", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002583", 
    name: "ACORNHOEK", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00002008", 
    name: "ALBERTON", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA77", 
    name: "TRADE SERVICES NON-RSA LETTERS OF CREDIT", 
    hubName: "ZA00", 
    validFrom: "2018-07-01 12:00:00.0"
   },
   {
    code: "ZA80", 
    name: "THE NON RESIDENT CENTRE", 
    validFrom: "2018-07-01 12:00:00.0"
   },
   {
    code: "00049262", 
    name: "DRAGON CITY SERVICE CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00051790", 
    name: "FOURWAYS MALL", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00048916", 
    name: "MENLYN MAIN", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00040795", 
    name: "NICOLWAY SERVICE CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001478", 
    name: "NON-RESIDENT CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00049877", 
    name: "SANRIDGE BRANCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001980", 
    name: "SUNNYSIDE BRANCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00036542", 
    name: "TEMBISA BRANCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00001975", 
    name: "TSHWANE MID CITY BRANCH", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00050094", 
    name: "Wilkoppies", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "00043811", 
    name: "WOODLANDS SERVICE CENTRE", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA98", 
    name: "FTS CENTRALISED OTT CROSS BORDER PAYMENTS", 
    hubCode: "ZA00", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA19", 
    name: "TPS - ICM", 
    hubCode: "ZA00", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA99", 
    name: "TPS NOSTRO CHARGES", 
    hubCode: "ZA00", 
    validFrom: "2000-01-01 00:00:00.0"
   },
   {
    code: "ZA67", 
    name: "GLOBAL MARKETS OPERATIONS", 
    validFrom: "2021-08-20 12:00:00.0"
   }
  ], 
  moneyTransferAgents: ["ACE", "AD", "ADLA", "AFROCOIN", "AYOBA", "CARD", "CASSAVA FINTECH", "DAYTONA", "ECONET", "ESKOM", "EXCHANGE4FREE", "FLASH", "FOREXWORLD", "GLOBAL", "HELLO PAISA", "IMALI", "INTERAFRICA", "INTERCHANGE", "MAMA MONEY", "MASTERCURRENCY", "MOMENTUM", "MONEYGRAM", "MONEYTRANS", "MUKURU", "PAYMENT PARTNER", "PAYPAL", "PEP", "SANLAM", "SHOPRITE", "SIKHONA", "SOUTH EAST", "TOURVEST", "TOWER", "TRAVEL CARD", "TRAVELEX", "TRAVELLERS CHEQUE", "WESTERNUNION", "XPRESSMONEY", "ZMT"], 
  mtaAccounts: [
   {
    accountNumber: "090092902", 
    MTA: "TOURVEST", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "002529726", 
    MTA: "SOUTH EAST", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90528484", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "70436010", 
    MTA: "GLOBAL", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "420982221", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00090751345", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "220028958", 
    MTA: "GLOBAL", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00070893853", 
    MTA: "IMALI", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "072532270", 
    MTA: "SIKHONA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90040678", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "201260972", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "090685849", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90528468", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "1299301", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "001299123", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "1299131", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "281053162", 
    MTA: "FOREXWORLD", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "332402029", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "7330430", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "009705767", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "001294210", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "072530979", 
    MTA: "SIKHONA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "410240672", 
    MTA: "INTERAFRICA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90515331", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "00009738665", 
    MTA: "MONEYGRAM", 
    rulingSection: "B.1(H)((2)(2.5)", 
    isADLA: "false"
   },
   {
    accountNumber: "7330005", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "002406616", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "1805371", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "54240", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "421043148", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "271576006", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90804155", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90804147", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90804163", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90822730", 
    MTA: "MAMA MONEY", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "70596964", 
    MTA: "MAMA MONEY", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "90724720", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90236335", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "9738150", 
    MTA: "SHOPRITE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "90724720", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90833210", 
    MTA: "CASSAVA FINTECH", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "00002529726", 
    MTA: "SOUTH EAST", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "00001299301", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "00201260972", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "00001294210", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "00410240672", 
    MTA: "INTERAFRICA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "90685849", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00281053162", 
    MTA: "FOREXWORLD", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00012252417", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00420982221", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00332402029", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "009738150", 
    MTA: "SHOPRITE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090004469", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000001299301", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000009738150", 
    MTA: "SHOPRITE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000001805371", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000421043148", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000001294210", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090751345", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000201260972", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000001299123", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000009705767", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000001299131", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000000054240", 
    MTA: "CARD", 
    rulingSection: "B.16(C)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090833210", 
    MTA: "CASSAVA FINTECH", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000002406616", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000007330005", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000007330430", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090040678", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090236335", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090515331", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090528468", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000090528484", 
    MTA: "ESKOM", 
    rulingSection: "B.1(H)((2)(2.4)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000281053162", 
    MTA: "FOREXWORLD ", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000220028958", 
    MTA: "GLOBAL", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000070436010", 
    MTA: "GLOBAL", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000271576006", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090804147", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090804155", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090804163", 
    MTA: "HELLO PAISA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000070893853", 
    MTA: "IMALI", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000410240672", 
    MTA: "INTERAFRICA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000420982221", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090685849", 
    MTA: "INTERCHANGE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000070596964", 
    MTA: "MAMA MONEY", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090822730", 
    MTA: "MAMA MONEY", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000009738665", 
    MTA: "MONEYGRAM", 
    rulingSection: "B.1(H)((2)(2.5)", 
    isADLA: "false"
   },
   {
    accountNumber: "0000000012252417", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000332402029", 
    MTA: "MUKURU", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000072530979", 
    MTA: "SIKHONA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000072532270", 
    MTA: "SIKHONA", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000002529726", 
    MTA: "SOUTH EAST", 
    rulingSection: "A.2(B)", 
    ADLALevel: "4", 
    isADLA: "true"
   },
   {
    accountNumber: "0000000090092902", 
    MTA: "TOURVEST", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   },
   {
    accountNumber: "00009738150", 
    MTA: "SHOPRITE", 
    rulingSection: "A.2(B)", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "090230183", 
    MTA: "ADLA", 
    rulingSection: "B.16 ( C )", 
    ADLALevel: "3", 
    isADLA: "true"
   },
   {
    accountNumber: "70893853", 
    MTA: "IMALI", 
    rulingSection: "A.2(B)", 
    ADLALevel: "2", 
    isADLA: "true"
   }
  ], 
  holdcoCompanies: [
   {
    companyName: "ASCENDIS FINANCIAL SERVICES", 
    accountNumber: "090752840"
   },
   {
    companyName: "ASCENDIS FINANCIAL SERVICES", 
    accountNumber: "090752767"
   },
   {
    companyName: "ASCENDIS FINANCIAL SERVICES", 
    accountNumber: "090752732"
   },
   {
    companyName: "LINTPALE INVESTMENTS (PTY LTD)", 
    accountNumber: "090755995"
   },
   {
    companyName: "ASCENDIS FIN SERV LTD", 
    accountNumber: "090767128"
   },
   {
    companyName: "LAND O LAKES KENYA HOLDINGS", 
    accountNumber: "090756452"
   },
   {
    companyName: "ASCENDIS FIN SERV LTD", 
    accountNumber: "09012345"
   },
   {
    companyName: "Land O Lakes Kenya Holdings", 
    accountNumber: "090123456"
   }
  ], 
  currencies: [
   {
    code: "ADP", 
    name: "Andorran Peseta"
   },
   {
    code: "AED", 
    name: "Arab Emirates Dirham"
   },
   {
    code: "AFA", 
    name: "Afghani"
   },
   {
    code: "AFN", 
    name: "Afghani"
   },
   {
    code: "ALL", 
    name: "Lek"
   },
   {
    code: "AMD", 
    name: "Armenian Dram"
   },
   {
    code: "ANG", 
    name: "Netherlans Antillan Guilder"
   },
   {
    code: "AOA", 
    name: "Kwanza"
   },
   {
    code: "AON", 
    name: "New Kwanza"
   },
   {
    code: "AOR", 
    name: "Kwanza Readjustado"
   },
   {
    code: "ARS", 
    name: "Argentine Peso"
   },
   {
    code: "ATS", 
    name: "Austrian Schilling"
   },
   {
    code: "AUD", 
    name: "Australian Dollar"
   },
   {
    code: "AWG", 
    name: "Aruban Guilder"
   },
   {
    code: "AZM", 
    name: "Azerbaijanian Manat"
   },
   {
    code: "AZN", 
    name: "Azerbaijanian Manat (ISO CODE INCORRECT)"
   },
   {
    code: "BAM", 
    name: "Convertible Marks"
   },
   {
    code: "BBD", 
    name: "Barbados Dollar"
   },
   {
    code: "BDT", 
    name: "Taka"
   },
   {
    code: "BEF", 
    name: "Belgian Franc"
   },
   {
    code: "BGL", 
    name: "Lev"
   },
   {
    code: "BGN", 
    name: "Bulgarian Lev"
   },
   {
    code: "BHD", 
    name: "Bahraini"
   },
   {
    code: "BIF", 
    name: "Burundi Franc"
   },
   {
    code: "BMD", 
    name: "Bermudian Dollat"
   },
   {
    code: "BND", 
    name: "Brunei Dollar"
   },
   {
    code: "BOB", 
    name: "Boliviano"
   },
   {
    code: "BOV", 
    name: "Mvdol"
   },
   {
    code: "BRL", 
    name: "Brazilian Real"
   },
   {
    code: "BSD", 
    name: "Bahamian Dollar"
   },
   {
    code: "BTN", 
    name: "Ngultrum"
   },
   {
    code: "BWP", 
    name: "Botswana Pula"
   },
   {
    code: "BYB", 
    name: "Belarussian Ruble"
   },
   {
    code: "BYR", 
    name: "Belarussian Ruble"
   },
   {
    code: "BZD", 
    name: "Belize Dollar"
   },
   {
    code: "CAD", 
    name: "Canadian Dollar"
   },
   {
    code: "CDF", 
    name: "Frank Congalais"
   },
   {
    code: "CHF", 
    name: "Swiss Frank"
   },
   {
    code: "CLF", 
    name: "Unidades De Fomento"
   },
   {
    code: "CLP", 
    name: "Chillean Peso"
   },
   {
    code: "CNY", 
    name: "Yuan Renminbi"
   },
   {
    code: "COP", 
    name: "Colombian Peso"
   },
   {
    code: "COU", 
    name: "Unidad De Valor Real"
   },
   {
    code: "CRC", 
    name: "Costa Rican Colon"
   },
   {
    code: "CSD", 
    name: "Serbian Dinar"
   },
   {
    code: "CUC", 
    name: "Peso Convertible"
   },
   {
    code: "CUP", 
    name: "Cuban Peso"
   },
   {
    code: "CVE", 
    name: "Cape Verde Escudo"
   },
   {
    code: "CYP", 
    name: "Cyprus Pound"
   },
   {
    code: "CZK", 
    name: "Czech Koruna"
   },
   {
    code: "DEM", 
    name: "German Deutsche Mark"
   },
   {
    code: "DJF", 
    name: "Djibouti Franc"
   },
   {
    code: "DKK", 
    name: "Danish Krone"
   },
   {
    code: "DOP", 
    name: "Dominican Peso"
   },
   {
    code: "DZD", 
    name: "Algerian Dinar"
   },
   {
    code: "ECS", 
    name: "Sucre"
   },
   {
    code: "ECV", 
    name: "Unidad De Valor Constante"
   },
   {
    code: "EEK", 
    name: "Kroon"
   },
   {
    code: "EGP", 
    name: "Egyptian Pound"
   },
   {
    code: "ERN", 
    name: "Nafka"
   },
   {
    code: "ESP", 
    name: "Spanish Peseta"
   },
   {
    code: "ETB", 
    name: "Ethiopian Birr"
   },
   {
    code: "EUR", 
    name: "Euro"
   },
   {
    code: "FIM", 
    name: "Finnish Markka"
   },
   {
    code: "FJD", 
    name: "Fiji Dollar"
   },
   {
    code: "FKP", 
    name: "Falkland Islands Pound"
   },
   {
    code: "FRF", 
    name: "French Frank"
   },
   {
    code: "GBP", 
    name: "British Pound Sterling"
   },
   {
    code: "GEL", 
    name: "Lari"
   },
   {
    code: "GHC", 
    name: "Cedi"
   },
   {
    code: "GHS", 
    name: "Ghana Cedi"
   },
   {
    code: "GIP", 
    name: "Gibraltar Pound"
   },
   {
    code: "GMD", 
    name: "Dalasi"
   },
   {
    code: "GNF", 
    name: "Guinea Franc"
   },
   {
    code: "GRD", 
    name: "Greek Drachma"
   },
   {
    code: "GTQ", 
    name: "Quetzal"
   },
   {
    code: "GWP", 
    name: "Guinea-Bissau Peso"
   },
   {
    code: "GYD", 
    name: "Guyana Dollor"
   },
   {
    code: "HKD", 
    name: "Hong Kong Dollar"
   },
   {
    code: "HNL", 
    name: "Lempira"
   },
   {
    code: "HRK", 
    name: "Kuna"
   },
   {
    code: "HTG", 
    name: "Gourde"
   },
   {
    code: "HUF", 
    name: "Hungarian Forint"
   },
   {
    code: "IDR", 
    name: "Rupiah"
   },
   {
    code: "IEP", 
    name: "Irish Pound"
   },
   {
    code: "ILS", 
    name: "New Israeli Shekel"
   },
   {
    code: "INR", 
    name: "Indian Rupee"
   },
   {
    code: "IQD", 
    name: "Iraqi Dinar"
   },
   {
    code: "IRR", 
    name: "Iranian Rial"
   },
   {
    code: "ISK", 
    name: "Iceland Krona"
   },
   {
    code: "ITL", 
    name: "Italian Lira"
   },
   {
    code: "JMD", 
    name: "Jamaican Dollar"
   },
   {
    code: "JOD", 
    name: "Jordian Dinar"
   },
   {
    code: "JPY", 
    name: "Japanese Yen"
   },
   {
    code: "KES", 
    name: "Kenyan Shilling"
   },
   {
    code: "KGS", 
    name: "Som"
   },
   {
    code: "KHR", 
    name: "Riel"
   },
   {
    code: "KMF", 
    name: "Comoro Franc"
   },
   {
    code: "KPW", 
    name: "North Korean Won"
   },
   {
    code: "KRW", 
    name: "Won"
   },
   {
    code: "KWD", 
    name: "Kuwaiti Dinr"
   },
   {
    code: "KYD", 
    name: "Cayman Islands Dollar"
   },
   {
    code: "KZT", 
    name: "Tenge"
   },
   {
    code: "LAK", 
    name: "Kip"
   },
   {
    code: "LBP", 
    name: "Lebanese Pound"
   },
   {
    code: "LKR", 
    name: "Sri Lanka Rupee"
   },
   {
    code: "LRD", 
    name: "Liberian Dollar"
   },
   {
    code: "LSL", 
    name: "Lesotho Loti"
   },
   {
    code: "LTL", 
    name: "Lithuanian Litas"
   },
   {
    code: "LUF", 
    name: "Luxembourg Franc"
   },
   {
    code: "LVL", 
    name: "Latvian Lats"
   },
   {
    code: "LYD", 
    name: "Libyan Dinar"
   },
   {
    code: "MAD", 
    name: "Moroccan Dirham"
   },
   {
    code: "MDL", 
    name: "Moldovan Leu"
   },
   {
    code: "MGA", 
    name: "Madagascar Ariary"
   },
   {
    code: "MGF", 
    name: "Malagasy Franc"
   },
   {
    code: "MKD", 
    name: "Denar"
   },
   {
    code: "MMK", 
    name: "Kyat"
   },
   {
    code: "MNT", 
    name: "Tugrik"
   },
   {
    code: "MOP", 
    name: "Pataca"
   },
   {
    code: "MRO", 
    name: "Ouguiya"
   },
   {
    code: "MRU", 
    name: "Ouguiya"
   },
   {
    code: "MTL", 
    name: "Maltese Lira"
   },
   {
    code: "MUR", 
    name: "Mauritius Rupee"
   },
   {
    code: "MVR", 
    name: "Rufiyaa"
   },
   {
    code: "MWK", 
    name: "Malawi Kwacha"
   },
   {
    code: "MXN", 
    name: "Mexican Peso"
   },
   {
    code: "MXV", 
    name: "Mexican Unidad De Inversion"
   },
   {
    code: "MYR", 
    name: "Malaysian Ringgit"
   },
   {
    code: "MZM", 
    name: "Metical"
   },
   {
    code: "MZN", 
    name: "New Mozambican Metical"
   },
   {
    code: "NAD", 
    name: "Namibian Dollar"
   },
   {
    code: "NGN", 
    name: "Naira"
   },
   {
    code: "NIO", 
    name: "Cordoba Oro"
   },
   {
    code: "NLG", 
    name: "Dutch Guilder"
   },
   {
    code: "NOK", 
    name: "Norwegian Krone"
   },
   {
    code: "NPR", 
    name: "Nepalese Rupee"
   },
   {
    code: "NZD", 
    name: "New Zealand Dollar"
   },
   {
    code: "OMR", 
    name: "Rial Omani"
   },
   {
    code: "PAB", 
    name: "Balboa"
   },
   {
    code: "PEN", 
    name: "Nuevo Sol"
   },
   {
    code: "PGK", 
    name: "Kina"
   },
   {
    code: "PHP", 
    name: "Philippine Peso"
   },
   {
    code: "PKR", 
    name: "Pakistan Rupee"
   },
   {
    code: "PLN", 
    name: "Polish Zloty"
   },
   {
    code: "PTE", 
    name: "Portuguese Escudo"
   },
   {
    code: "PYG", 
    name: "Guarani"
   },
   {
    code: "QAR", 
    name: "Qatari Rial"
   },
   {
    code: "RMB", 
    name: "Chinese Renminbi"
   },
   {
    code: "ROL", 
    name: "Leu"
   },
   {
    code: "RON", 
    name: "Romanian Leu"
   },
   {
    code: "RSD", 
    name: "Serbian Dinar"
   },
   {
    code: "RUB", 
    name: "Russian Ruble (New)"
   },
   {
    code: "RUR", 
    name: "Russian Rubble (Old)"
   },
   {
    code: "RWF", 
    name: "Rwanda Franc"
   },
   {
    code: "SAR", 
    name: "Saudi Riyal"
   },
   {
    code: "SBD", 
    name: "Soloman Island Dollar"
   },
   {
    code: "SCR", 
    name: "Seychelles Rupee"
   },
   {
    code: "SDD", 
    name: "Sudanese Dinar"
   },
   {
    code: "SDG", 
    name: "Sudanese Pound"
   },
   {
    code: "SEK", 
    name: "Swedish Krona"
   },
   {
    code: "SGD", 
    name: "Singapore Dollar"
   },
   {
    code: "SHP", 
    name: "St Helena Pound"
   },
   {
    code: "SIT", 
    name: "Tolar"
   },
   {
    code: "SKK", 
    name: "Slovak Koruna"
   },
   {
    code: "SLL", 
    name: "Leone"
   },
   {
    code: "SOS", 
    name: "Somali Shilling"
   },
   {
    code: "SRD", 
    name: "Suriname Dollar"
   },
   {
    code: "SRG", 
    name: "Suriname Guilder"
   },
   {
    code: "SSP", 
    name: "South Sudanese Pound"
   },
   {
    code: "STD", 
    name: "Dobra"
   },
   {
    code: "STN", 
    name: "Dobra"
   },
   {
    code: "SVC", 
    name: "El Salvador Colon"
   },
   {
    code: "SYP", 
    name: "Syrian Pound"
   },
   {
    code: "SZL", 
    name: "Swaziland Lilangeni"
   },
   {
    code: "THB", 
    name: "Thai Baht"
   },
   {
    code: "TJR", 
    name: "Tajik Ruble"
   },
   {
    code: "TJS", 
    name: "Somoni"
   },
   {
    code: "TMM", 
    name: "Manat"
   },
   {
    code: "TMT", 
    name: "Turkmenistan New Manat"
   },
   {
    code: "TND", 
    name: "Tunisian Dinar"
   },
   {
    code: "TOP", 
    name: "Pa Anga"
   },
   {
    code: "TPE", 
    name: "Timor Escudo"
   },
   {
    code: "TRL", 
    name: "Turkish Lira"
   },
   {
    code: "TRY", 
    name: "Turkish New Lira"
   },
   {
    code: "TTD", 
    name: "Trinidad And Tobago Dollar"
   },
   {
    code: "TWD", 
    name: "Taiwan Dollar"
   },
   {
    code: "TZS", 
    name: "Tanzanian Shilling"
   },
   {
    code: "UAF", 
    name: "Hryvnia"
   },
   {
    code: "UAH", 
    name: "Hryvnia"
   },
   {
    code: "UGX", 
    name: "Uganda Shilling"
   },
   {
    code: "USD", 
    name: "US Dollar"
   },
   {
    code: "USN", 
    name: "US Dollar Next Day Funds"
   },
   {
    code: "UYU", 
    name: "Peso Uruguayo"
   },
   {
    code: "UZS", 
    name: "Uzbekistan Sum"
   },
   {
    code: "VEB", 
    name: "Bolivar"
   },
   {
    code: "VEF", 
    name: "Bolivar Fuerte"
   },
   {
    code: "VND", 
    name: "Dong"
   },
   {
    code: "VUV", 
    name: "Vatu"
   },
   {
    code: "WST", 
    name: "Tala"
   },
   {
    code: "XAF", 
    name: "CFA Franc BEAC"
   },
   {
    code: "XCD", 
    name: "East Caribbean Dollar"
   },
   {
    code: "XDR", 
    name: "SDR (Special Drawing Right)"
   },
   {
    code: "XOF", 
    name: "CFA Franc BCEAO"
   },
   {
    code: "XPF", 
    name: "CFP Franc"
   },
   {
    code: "XSU", 
    name: "Sucre"
   },
   {
    code: "YER", 
    name: "Yemeni Rial"
   },
   {
    code: "YUM", 
    name: "New Yugoslavian Dinar"
   },
   {
    code: "ZAR", 
    name: "South African Rand"
   },
   {
    code: "ZMK", 
    name: "Zambian Kwacha"
   },
   {
    code: "ZMW", 
    name: "Zambian Kwacha New"
   },
   {
    code: "ZWD", 
    name: "Zimbabwe Dollar"
   },
   {
    code: "ZWL", 
    name: "New Zimbabwe Dollar"
   },
   {
    code: "ZWR", 
    name: "Zimbabwean Dollar"
   }
  ], 
  luClientCCNs: [
   {
    ccn: "00048611"
   },
   {
    ccn: "00030683"
   },
   {
    ccn: "00034344"
   },
   {
    ccn: "00089284"
   },
   {
    ccn: "00089578"
   },
   {
    ccn: "00038927"
   },
   {
    ccn: "00039583"
   },
   {
    ccn: "00039702"
   },
   {
    ccn: "00044632"
   },
   {
    ccn: "00045744"
   },
   {
    ccn: "00047178"
   },
   {
    ccn: "00047224"
   },
   {
    ccn: "00039710"
   },
   {
    ccn: "00076531"
   },
   {
    ccn: "00078437"
   },
   {
    ccn: "00079271"
   },
   {
    ccn: "00115477"
   },
   {
    ccn: "00053770"
   },
   {
    ccn: "00036479"
   },
   {
    ccn: "00061829"
   },
   {
    ccn: "00067088"
   },
   {
    ccn: "00032260"
   },
   {
    ccn: "00100381"
   },
   {
    ccn: "00107092"
   },
   {
    ccn: "00107785"
   },
   {
    ccn: "00170863"
   },
   {
    ccn: "00202574"
   },
   {
    ccn: "00320345"
   },
   {
    ccn: "00209188"
   },
   {
    ccn: "00330366"
   },
   {
    ccn: "00330714"
   },
   {
    ccn: "00293385"
   },
   {
    ccn: "00332598"
   },
   {
    ccn: "00379817"
   },
   {
    ccn: "00498414"
   },
   {
    ccn: "00491746"
   },
   {
    ccn: "00518128"
   },
   {
    ccn: "00586887"
   },
   {
    ccn: "00921823"
   },
   {
    ccn: "00989368"
   },
   {
    ccn: "00990713"
   },
   {
    ccn: "00932425"
   },
   {
    ccn: "00945054"
   },
   {
    ccn: "01019129"
   },
   {
    ccn: "01028747"
   },
   {
    ccn: "00094511"
   },
   {
    ccn: "00138956"
   },
   {
    ccn: "00763037"
   },
   {
    ccn: "00145810"
   },
   {
    ccn: "01354320"
   },
   {
    ccn: "00801219"
   },
   {
    ccn: "01488536"
   },
   {
    ccn: "00280861"
   },
   {
    ccn: "01151642"
   },
   {
    ccn: "00308525"
   },
   {
    ccn: "01190415"
   },
   {
    ccn: "01828746"
   },
   {
    ccn: "00329652"
   },
   {
    ccn: "01084218"
   },
   {
    ccn: "01656766"
   },
   {
    ccn: "01582159"
   },
   {
    ccn: "00382167"
   },
   {
    ccn: "01647881"
   },
   {
    ccn: "01706961"
   },
   {
    ccn: "00757100"
   },
   {
    ccn: "00446761"
   },
   {
    ccn: "20379527"
   },
   {
    ccn: "00523473"
   },
   {
    ccn: "20729464"
   },
   {
    ccn: "00874120"
   },
   {
    ccn: "21075108"
   },
   {
    ccn: "00021952"
   },
   {
    ccn: "00030284"
   },
   {
    ccn: "00030519"
   },
   {
    ccn: "00034395"
   },
   {
    ccn: "00035103"
   },
   {
    ccn: "00035944"
   },
   {
    ccn: "00036134"
   },
   {
    ccn: "00038242"
   },
   {
    ccn: "00088199"
   },
   {
    ccn: "00089659"
   },
   {
    ccn: "00090648"
   },
   {
    ccn: "00119049"
   },
   {
    ccn: "00120348"
   },
   {
    ccn: "00096980"
   },
   {
    ccn: "00041404"
   },
   {
    ccn: "00042168"
   },
   {
    ccn: "00044047"
   },
   {
    ccn: "00044624"
   },
   {
    ccn: "00045876"
   },
   {
    ccn: "00047046"
   },
   {
    ccn: "00075055"
   },
   {
    ccn: "00075357"
   },
   {
    ccn: "00076191"
   },
   {
    ccn: "00076590"
   },
   {
    ccn: "00077309"
   },
   {
    ccn: "00078569"
   },
   {
    ccn: "00078887"
   },
   {
    ccn: "00079506"
   },
   {
    ccn: "00984129"
   },
   {
    ccn: "00931658"
   },
   {
    ccn: "00932417"
   },
   {
    ccn: "00879653"
   },
   {
    ccn: "00936226"
   },
   {
    ccn: "00887035"
   },
   {
    ccn: "01004467"
   },
   {
    ccn: "01007199"
   },
   {
    ccn: "00066782"
   },
   {
    ccn: "00067061"
   },
   {
    ccn: "00953277"
   },
   {
    ccn: "00898575"
   },
   {
    ccn: "00078194"
   },
   {
    ccn: "00076949"
   },
   {
    ccn: "00961309"
   },
   {
    ccn: "00041102"
   },
   {
    ccn: "00963492"
   },
   {
    ccn: "00085521"
   },
   {
    ccn: "00080309"
   },
   {
    ccn: "00973683"
   },
   {
    ccn: "00749818"
   },
   {
    ccn: "00868512"
   },
   {
    ccn: "00572801"
   },
   {
    ccn: "00874112"
   },
   {
    ccn: "00554872"
   },
   {
    ccn: "00059442"
   },
   {
    ccn: "00560499"
   },
   {
    ccn: "00864274"
   },
   {
    ccn: "00584833"
   },
   {
    ccn: "00334779"
   },
   {
    ccn: "00336526"
   },
   {
    ccn: "00365663"
   },
   {
    ccn: "00391033"
   },
   {
    ccn: "00394040"
   },
   {
    ccn: "00375498"
   },
   {
    ccn: "00399611"
   },
   {
    ccn: "00454038"
   },
   {
    ccn: "00434802"
   },
   {
    ccn: "00435337"
   },
   {
    ccn: "00436732"
   },
   {
    ccn: "00442014"
   },
   {
    ccn: "00443916"
   },
   {
    ccn: "00483558"
   },
   {
    ccn: "00459528"
   },
   {
    ccn: "00516281"
   },
   {
    ccn: "00464822"
   },
   {
    ccn: "00470325"
   },
   {
    ccn: "00053606"
   },
   {
    ccn: "00055382"
   },
   {
    ccn: "00098207"
   },
   {
    ccn: "00319550"
   },
   {
    ccn: "00319658"
   },
   {
    ccn: "00251996"
   },
   {
    ccn: "00315652"
   },
   {
    ccn: "00062140"
   },
   {
    ccn: "00036843"
   },
   {
    ccn: "00215012"
   },
   {
    ccn: "00325185"
   },
   {
    ccn: "00325266"
   },
   {
    ccn: "00325487"
   },
   {
    ccn: "00329547"
   },
   {
    ccn: "00329717"
   },
   {
    ccn: "00100187"
   },
   {
    ccn: "00330633"
   },
   {
    ccn: "00331109"
   },
   {
    ccn: "00293512"
   },
   {
    ccn: "00094805"
   },
   {
    ccn: "00094821"
   },
   {
    ccn: "00098215"
   },
   {
    ccn: "00285154"
   },
   {
    ccn: "00102155"
   },
   {
    ccn: "00104883"
   },
   {
    ccn: "00262962"
   },
   {
    ccn: "00310413"
   },
   {
    ccn: "00109729"
   },
   {
    ccn: "00198995"
   },
   {
    ccn: "00241061"
   },
   {
    ccn: "00228806"
   },
   {
    ccn: "01185463"
   },
   {
    ccn: "01149932"
   },
   {
    ccn: "00343262"
   },
   {
    ccn: "01302627"
   },
   {
    ccn: "01160292"
   },
   {
    ccn: "00356738"
   },
   {
    ccn: "00397104"
   },
   {
    ccn: "01818768"
   },
   {
    ccn: "01249545"
   },
   {
    ccn: "01936814"
   },
   {
    ccn: "01105653"
   },
   {
    ccn: "20068444"
   },
   {
    ccn: "01288202"
   },
   {
    ccn: "01075724"
   },
   {
    ccn: "01503760"
   },
   {
    ccn: "01506344"
   },
   {
    ccn: "01427877"
   },
   {
    ccn: "01402701"
   },
   {
    ccn: "01324149"
   },
   {
    ccn: "01325765"
   },
   {
    ccn: "01383956"
   },
   {
    ccn: "00276539"
   },
   {
    ccn: "01528311"
   },
   {
    ccn: "01354967"
   },
   {
    ccn: "00147430"
   },
   {
    ccn: "00209862"
   },
   {
    ccn: "01536666"
   },
   {
    ccn: "00765803"
   },
   {
    ccn: "00075659"
   },
   {
    ccn: "00265031"
   },
   {
    ccn: "00168363"
   },
   {
    ccn: "00632866"
   },
   {
    ccn: "00635202"
   },
   {
    ccn: "01731629"
   },
   {
    ccn: "20348721"
   },
   {
    ccn: "01651268"
   },
   {
    ccn: "01673413"
   },
   {
    ccn: "00815618"
   },
   {
    ccn: "20440899"
   },
   {
    ccn: "01627815"
   },
   {
    ccn: "01561226"
   },
   {
    ccn: "01561773"
   },
   {
    ccn: "01747559"
   },
   {
    ccn: "01748792"
   },
   {
    ccn: "01715730"
   },
   {
    ccn: "20210375"
   },
   {
    ccn: "01793106"
   },
   {
    ccn: "01555405"
   },
   {
    ccn: "20392750"
   },
   {
    ccn: "00637981"
   },
   {
    ccn: "20062969"
   },
   {
    ccn: "01727312"
   },
   {
    ccn: "01551574"
   },
   {
    ccn: "20063701"
   },
   {
    ccn: "00465683"
   },
   {
    ccn: "01662463"
   },
   {
    ccn: "20400696"
   },
   {
    ccn: "01295216"
   },
   {
    ccn: "01597376"
   },
   {
    ccn: "01599786"
   },
   {
    ccn: "01682845"
   },
   {
    ccn: "01755437"
   },
   {
    ccn: "01113558"
   },
   {
    ccn: "01292497"
   },
   {
    ccn: "20004174"
   },
   {
    ccn: "01307726"
   },
   {
    ccn: "20884838"
   },
   {
    ccn: "20629983"
   },
   {
    ccn: "20815492"
   },
   {
    ccn: "20889759"
   },
   {
    ccn: "20581609"
   },
   {
    ccn: "20643437"
   },
   {
    ccn: "20859364"
   },
   {
    ccn: "20265230"
   },
   {
    ccn: "20146527"
   },
   {
    ccn: "20098820"
   },
   {
    ccn: "20259362"
   },
   {
    ccn: "20131318"
   },
   {
    ccn: "20362234"
   },
   {
    ccn: "00535071"
   },
   {
    ccn: "20140324"
   },
   {
    ccn: "20160803"
   },
   {
    ccn: "01551752"
   },
   {
    ccn: "00040746"
   },
   {
    ccn: "00846987"
   },
   {
    ccn: "01893916"
   },
   {
    ccn: "00781122"
   },
   {
    ccn: "00887264"
   },
   {
    ccn: "00565334"
   },
   {
    ccn: "00597950"
   },
   {
    ccn: "00735354"
   },
   {
    ccn: "00607252"
   },
   {
    ccn: "00660328"
   },
   {
    ccn: "00717846"
   },
   {
    ccn: "00622020"
   },
   {
    ccn: "00667837"
   },
   {
    ccn: "00767954"
   },
   {
    ccn: "00743216"
   },
   {
    ccn: "00748358"
   },
   {
    ccn: "00709682"
   },
   {
    ccn: "00843694"
   },
   {
    ccn: "00824888"
   },
   {
    ccn: "00770881"
   },
   {
    ccn: "00775131"
   },
   {
    ccn: "21077682"
   },
   {
    ccn: "21013043"
   },
   {
    ccn: "20895022"
   },
   {
    ccn: "20535385"
   },
   {
    ccn: "20968205"
   },
   {
    ccn: "21212292"
   },
   {
    ccn: "21269335"
   },
   {
    ccn: "00874104"
   },
   {
    ccn: "20824822"
   },
   {
    ccn: "21343730"
   },
   {
    ccn: "20745581"
   },
   {
    ccn: "20712023"
   },
   {
    ccn: "20104643"
   },
   {
    ccn: "01766942"
   },
   {
    ccn: "01745130"
   },
   {
    ccn: "01650482"
   },
   {
    ccn: "01921540"
   },
   {
    ccn: "20120937"
   },
   {
    ccn: "01631218"
   },
   {
    ccn: "01632788"
   },
   {
    ccn: "01802560"
   },
   {
    ccn: "01636635"
   },
   {
    ccn: "01806531"
   },
   {
    ccn: "01698678"
   },
   {
    ccn: "01812719"
   },
   {
    ccn: "01705043"
   },
   {
    ccn: "01787115"
   },
   {
    ccn: "01792649"
   },
   {
    ccn: "20061806"
   },
   {
    ccn: "01697019"
   },
   {
    ccn: "01671860"
   },
   {
    ccn: "01674940"
   },
   {
    ccn: "01856887"
   },
   {
    ccn: "01740163"
   },
   {
    ccn: "01824228"
   },
   {
    ccn: "01826972"
   },
   {
    ccn: "01721896"
   },
   {
    ccn: "01734660"
   },
   {
    ccn: "01708409"
   },
   {
    ccn: "01647091"
   },
   {
    ccn: "01450590"
   },
   {
    ccn: "01502233"
   },
   {
    ccn: "01445555"
   },
   {
    ccn: "01249189"
   },
   {
    ccn: "01544994"
   },
   {
    ccn: "01116115"
   },
   {
    ccn: "01536879"
   },
   {
    ccn: "01536933"
   },
   {
    ccn: "01303968"
   },
   {
    ccn: "00998986"
   },
   {
    ccn: "01009655"
   },
   {
    ccn: "01276671"
   },
   {
    ccn: "01280724"
   },
   {
    ccn: "01577814"
   },
   {
    ccn: "01551981"
   },
   {
    ccn: "01157322"
   },
   {
    ccn: "01055865"
   },
   {
    ccn: "01202755"
   },
   {
    ccn: "01307599"
   },
   {
    ccn: "01311587"
   },
   {
    ccn: "01029638"
   },
   {
    ccn: "01029972"
   },
   {
    ccn: "01221146"
   },
   {
    ccn: "01210919"
   },
   {
    ccn: "01611448"
   },
   {
    ccn: "01214477"
   },
   {
    ccn: "01615249"
   },
   {
    ccn: "01427427"
   },
   {
    ccn: "01593265"
   },
   {
    ccn: "01596418"
   },
   {
    ccn: "01195743"
   },
   {
    ccn: "01195980"
   },
   {
    ccn: "20976899"
   },
   {
    ccn: "20977097"
   },
   {
    ccn: "20822967"
   },
   {
    ccn: "20994119"
   },
   {
    ccn: "20994224"
   },
   {
    ccn: "20994992"
   },
   {
    ccn: "21023242"
   },
   {
    ccn: "21025105"
   },
   {
    ccn: "20731573"
   },
   {
    ccn: "20996677"
   },
   {
    ccn: "20996723"
   },
   {
    ccn: "20862887"
   },
   {
    ccn: "20944237"
   },
   {
    ccn: "20997622"
   },
   {
    ccn: "20999757"
   },
   {
    ccn: "20778317"
   },
   {
    ccn: "20747312"
   },
   {
    ccn: "21000295"
   },
   {
    ccn: "21000538"
   },
   {
    ccn: "21002034"
   },
   {
    ccn: "20986176"
   },
   {
    ccn: "20986222"
   },
   {
    ccn: "20987245"
   },
   {
    ccn: "20989094"
   },
   {
    ccn: "20989167"
   },
   {
    ccn: "20989542"
   },
   {
    ccn: "20990784"
   },
   {
    ccn: "20990806"
   },
   {
    ccn: "20991217"
   },
   {
    ccn: "20991292"
   },
   {
    ccn: "20991837"
   },
   {
    ccn: "20794618"
   },
   {
    ccn: "20950425"
   },
   {
    ccn: "20594696"
   },
   {
    ccn: "20595943"
   },
   {
    ccn: "20356669"
   },
   {
    ccn: "20246539"
   },
   {
    ccn: "20247810"
   },
   {
    ccn: "20367341"
   },
   {
    ccn: "20612501"
   },
   {
    ccn: "20621290"
   },
   {
    ccn: "20621311"
   },
   {
    ccn: "20628774"
   },
   {
    ccn: "20280075"
   },
   {
    ccn: "20390049"
   },
   {
    ccn: "20390677"
   },
   {
    ccn: "20311572"
   },
   {
    ccn: "20648544"
   },
   {
    ccn: "20534923"
   },
   {
    ccn: "20535652"
   },
   {
    ccn: "20536012"
   },
   {
    ccn: "20536039"
   },
   {
    ccn: "20674984"
   },
   {
    ccn: "20675913"
   },
   {
    ccn: "20327149"
   },
   {
    ccn: "20552245"
   },
   {
    ccn: "20330815"
   },
   {
    ccn: "00060601"
   },
   {
    ccn: "00375501"
   },
   {
    ccn: "00077058"
   },
   {
    ccn: "00045191"
   },
   {
    ccn: "00212242"
   },
   {
    ccn: "00042125"
   },
   {
    ccn: "00039796"
   },
   {
    ccn: "00104328"
   },
   {
    ccn: "21138072"
   },
   {
    ccn: "20623098"
   },
   {
    ccn: "00566446"
   },
   {
    ccn: "01259469"
   },
   {
    ccn: "00288250"
   },
   {
    ccn: "00331141"
   },
   {
    ccn: "00087702"
   },
   {
    ccn: "00170731"
   },
   {
    ccn: "00767660"
   },
   {
    ccn: "00903347"
   },
   {
    ccn: "00794622"
   },
   {
    ccn: "00971508"
   },
   {
    ccn: "00030128"
   },
   {
    ccn: "01607220"
   },
   {
    ccn: "01812204"
   },
   {
    ccn: "00585430"
   },
   {
    ccn: "00691107"
   },
   {
    ccn: "01858278"
   },
   {
    ccn: "00494281"
   },
   {
    ccn: "20381130"
   },
   {
    ccn: "01920846"
   },
   {
    ccn: "00043245"
   },
   {
    ccn: "01034495"
   },
   {
    ccn: "00093043"
   },
   {
    ccn: "00079603"
   },
   {
    ccn: "00304562"
   },
   {
    ccn: "21268347"
   },
   {
    ccn: "00032619"
   },
   {
    ccn: "00785969"
   },
   {
    ccn: "00497248"
   },
   {
    ccn: "20064694"
   },
   {
    ccn: "00455867"
   },
   {
    ccn: "00032716"
   },
   {
    ccn: "00039990"
   },
   {
    ccn: "20159539"
   },
   {
    ccn: "00977883"
   },
   {
    ccn: "01223521"
   },
   {
    ccn: "00041684"
   },
   {
    ccn: "20716037"
   },
   {
    ccn: "20337178"
   },
   {
    ccn: "00078356"
   },
   {
    ccn: "00158547"
   },
   {
    ccn: "00039524"
   },
   {
    ccn: "20915798"
   },
   {
    ccn: "00563412"
   },
   {
    ccn: "00985257"
   },
   {
    ccn: "21075140"
   },
   {
    ccn: "00085874"
   },
   {
    ccn: "00077317"
   },
   {
    ccn: "01494853"
   },
   {
    ccn: "00261524"
   },
   {
    ccn: "00049049"
   },
   {
    ccn: "20570012"
   },
   {
    ccn: "01361431"
   },
   {
    ccn: "00905269"
   },
   {
    ccn: "00114543"
   },
   {
    ccn: "01824171"
   },
   {
    ccn: "01042772"
   },
   {
    ccn: "00088881"
   },
   {
    ccn: "20272669"
   },
   {
    ccn: "20659790"
   },
   {
    ccn: "00039435"
   },
   {
    ccn: "01634047"
   },
   {
    ccn: "00719709"
   },
   {
    ccn: "20080299"
   },
   {
    ccn: "00083375"
   },
   {
    ccn: "01759580"
   },
   {
    ccn: "20282280"
   },
   {
    ccn: "00508646"
   },
   {
    ccn: "00084142"
   },
   {
    ccn: "00303477"
   },
   {
    ccn: "01797527"
   },
   {
    ccn: "01106234"
   },
   {
    ccn: "00323751"
   },
   {
    ccn: "00081976"
   },
   {
    ccn: "00039893"
   },
   {
    ccn: "20595346"
   },
   {
    ccn: "00049952"
   },
   {
    ccn: "00041331"
   },
   {
    ccn: "01123382"
   },
   {
    ccn: "01645102"
   },
   {
    ccn: "20616610"
   },
   {
    ccn: "01913565"
   },
   {
    ccn: "01162252"
   },
   {
    ccn: "00296023"
   },
   {
    ccn: "01782997"
   },
   {
    ccn: "00418108"
   },
   {
    ccn: "21445168"
   },
   {
    ccn: "01536712"
   },
   {
    ccn: "00717845"
   },
   {
    ccn: "21481567"
   },
   {
    ccn: "00110882"
   },
   {
    ccn: "20300823"
   },
   {
    ccn: "01823604"
   },
   {
    ccn: "00275915"
   },
   {
    ccn: "00081321"
   },
   {
    ccn: "00073761"
   },
   {
    ccn: "00488932"
   },
   {
    ccn: "20536020"
   },
   {
    ccn: "00040467"
   },
   {
    ccn: "00481237"
   },
   {
    ccn: "00628662"
   },
   {
    ccn: "00487596"
   },
   {
    ccn: "00470295"
   },
   {
    ccn: "00329210"
   },
   {
    ccn: "00324693"
   },
   {
    ccn: "00286444"
   },
   {
    ccn: "00286436"
   },
   {
    ccn: "00204062"
   },
   {
    ccn: "00082433"
   },
   {
    ccn: "00474517"
   },
   {
    ccn: "00770938"
   },
   {
    ccn: "00035642"
   },
   {
    ccn: "20496016"
   },
   {
    ccn: "00049499"
   },
   {
    ccn: "00253034"
   },
   {
    ccn: "00067665"
   },
   {
    ccn: "00116562"
   },
   {
    ccn: "00348531"
   },
   {
    ccn: "20610434"
   },
   {
    ccn: "00319135"
   },
   {
    ccn: "00082743"
   },
   {
    ccn: "00531602"
   },
   {
    ccn: "00100918"
   },
   {
    ccn: "01572928"
   },
   {
    ccn: "00090397"
   },
   {
    ccn: "20390235"
   },
   {
    ccn: "00507712"
   },
   {
    ccn: "00507720"
   },
   {
    ccn: "20153808"
   },
   {
    ccn: "01870144"
   },
   {
    ccn: "01761134"
   },
   {
    ccn: "00632718"
   },
   {
    ccn: "00595176"
   },
   {
    ccn: "00275605"
   },
   {
    ccn: "00175407"
   },
   {
    ccn: "00047100"
   },
   {
    ccn: "00501110"
   },
   {
    ccn: "01488285"
   },
   {
    ccn: "00765781"
   },
   {
    ccn: "00114446"
   },
   {
    ccn: "00375374"
   },
   {
    ccn: "00330927"
   },
   {
    ccn: "01638158"
   },
   {
    ccn: "01165502"
   },
   {
    ccn: "00333462"
   },
   {
    ccn: "00080937"
   },
   {
    ccn: "00037246"
   },
   {
    ccn: "00400749"
   },
   {
    ccn: "00105588"
   },
   {
    ccn: "21143172"
   },
   {
    ccn: "01370723"
   },
   {
    ccn: "20056617"
   },
   {
    ccn: "00060253"
   },
   {
    ccn: "01264836"
   },
   {
    ccn: "00100071"
   },
   {
    ccn: "00032392"
   },
   {
    ccn: "00108420"
   },
   {
    ccn: "00211696"
   },
   {
    ccn: "00408588"
   },
   {
    ccn: "00280683"
   },
   {
    ccn: "01404046"
   },
   {
    ccn: "01309044"
   },
   {
    ccn: "20487832"
   },
   {
    ccn: "01141532"
   },
   {
    ccn: "00050445"
   },
   {
    ccn: "00053304"
   },
   {
    ccn: "00947901"
   },
   {
    ccn: "00445161"
   },
   {
    ccn: "01268068"
   },
   {
    ccn: "00042842"
   },
   {
    ccn: "00169998"
   },
   {
    ccn: "00857074"
   },
   {
    ccn: "01859622"
   },
   {
    ccn: "00032228"
   },
   {
    ccn: "20093985"
   },
   {
    ccn: "00893395"
   },
   {
    ccn: "01388834"
   },
   {
    ccn: "00250329"
   },
   {
    ccn: "00831085"
   },
   {
    ccn: "00594919"
   },
   {
    ccn: "20961825"
   },
   {
    ccn: "20745921"
   },
   {
    ccn: "00427246"
   },
   {
    ccn: "00097936"
   },
   {
    ccn: "20563459"
   },
   {
    ccn: "00079166"
   },
   {
    ccn: "20845747"
   },
   {
    ccn: "00064380"
   },
   {
    ccn: "01203514"
   },
   {
    ccn: "01709375"
   },
   {
    ccn: "00812430"
   },
   {
    ccn: "00084193"
   },
   {
    ccn: "00480109"
   },
   {
    ccn: "00326564"
   },
   {
    ccn: "01793823"
   },
   {
    ccn: "20837338"
   },
   {
    ccn: "00874139"
   },
   {
    ccn: "00037548"
   },
   {
    ccn: "00098908"
   },
   {
    ccn: "01219967"
   },
   {
    ccn: "00813798"
   },
   {
    ccn: "00813801"
   },
   {
    ccn: "00997114"
   },
   {
    ccn: "00966904"
   },
   {
    ccn: "01354185"
   },
   {
    ccn: "00078577"
   },
   {
    ccn: "00145764"
   },
   {
    ccn: "01373633"
   },
   {
    ccn: "01062445"
   },
   {
    ccn: "00698047"
   },
   {
    ccn: "00526030"
   },
   {
    ccn: "00341847"
   },
   {
    ccn: "01718136"
   },
   {
    ccn: "00030055"
   },
   {
    ccn: "00181768"
   },
   {
    ccn: "01605538"
   },
   {
    ccn: "21479752"
   },
   {
    ccn: "00316845"
   },
   {
    ccn: "21625344"
   },
   {
    ccn: "21507948"
   },
   {
    ccn: "67665"
   },
   {
    ccn: "01884403"
   },
   {
    ccn: "21372419"
   },
   {
    ccn: "21201256"
   },
   {
    ccn: "21376474"
   },
   {
    ccn: "00524429"
   },
   {
    ccn: "21415342"
   },
   {
    ccn: "33619347"
   },
   {
    ccn: "99900007"
   },
   {
    ccn: "01402485"
   },
   {
    ccn: "20407801"
   },
   {
    ccn: "000945111"
   },
   {
    ccn: "00112346"
   },
   {
    ccn: "21305824"
   },
   {
    ccn: "01152665"
   },
   {
    ccn: "21558845"
   },
   {
    ccn: "21516717"
   },
   {
    ccn: "01108105"
   },
   {
    ccn: "00074679"
   },
   {
    ccn: "21536678"
   },
   {
    ccn: "21588554"
   },
   {
    ccn: "00893700"
   },
   {
    ccn: "00463826"
   },
   {
    ccn: "00183859"
   },
   {
    ccn: "00804234"
   },
   {
    ccn: "21319786"
   },
   {
    ccn: "01237254"
   },
   {
    ccn: "00455670"
   },
   {
    ccn: "01834141"
   },
   {
    ccn: "00052324"
   },
   {
    ccn: "01515563"
   },
   {
    ccn: "01485421"
   },
   {
    ccn: "01262671"
   },
   {
    ccn: "20519543"
   },
   {
    ccn: "01092708"
   },
   {
    ccn: "01688061"
   },
   {
    ccn: "00444661"
   },
   {
    ccn: "00579164"
   },
   {
    ccn: "01717903"
   },
   {
    ccn: "01486266"
   },
   {
    ccn: "00641139"
   },
   {
    ccn: "99999907"
   },
   {
    ccn: "20526190"
   },
   {
    ccn: "20516536"
   },
   {
    ccn: "01696039"
   },
   {
    ccn: "21461200"
   },
   {
    ccn: "01135126"
   },
   {
    ccn: "20257025"
   },
   {
    ccn: "00331095"
   },
   {
    ccn: "21619620"
   },
   {
    ccn: "21619604"
   },
   {
    ccn: "00465896"
   },
   {
    ccn: "01524219"
   },
   {
    ccn: "21361729"
   },
   {
    ccn: "21087571"
   },
   {
    ccn: "21088659"
   },
   {
    ccn: "21090539"
   },
   {
    ccn: "21283719"
   },
   {
    ccn: "21045759"
   },
   {
    ccn: "21196832"
   },
   {
    ccn: "21111958"
   },
   {
    ccn: "21378833"
   },
   {
    ccn: "21124854"
   },
   {
    ccn: "21099269"
   },
   {
    ccn: "21276942"
   },
   {
    ccn: "21144004"
   },
   {
    ccn: "21356270"
   },
   {
    ccn: "21360625"
   },
   {
    ccn: "21084955"
   },
   {
    ccn: "21155250"
   },
   {
    ccn: "21158322"
   },
   {
    ccn: "76767676"
   }
  ]
 }
})