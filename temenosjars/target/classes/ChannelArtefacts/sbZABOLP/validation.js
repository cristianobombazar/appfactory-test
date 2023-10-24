define(function () {
 return function(predef) {
  var v1;
  var v2;
  var v3;
  var v4;
  var v5;
  var v6;
  var v7;
  var v8;
  var v9;
  var v10;
  var v11;
  var v12;
  var v13;
  var v14;
  var v15;
  var v16;
  var v17;
  var v18;
  var v19;
  var v20;
  var v21;
  var v22;
  var v23;
  var v24;
  var v25;
  var v26;
  var v27;
  var v28;
  with (predef) {
    v1 = {
      ruleset: "Transaction Rules for Flow form", 
      scope: "transaction", 
      validations: [
        {
          field: "", 
          rules: [
            message("tfv1", null, "The category amounts do not add up to the total amount"),
            message("tfv2", null, "The category amounts do not add up to the total amount"),
            message("tn1", null, "Please enter a valid Tax number"),
            message("vn1", null, "Please enter a valid VAT number"),
            message("a2_1", null, "Not required"),
            message("accid3", null, "Not required"),
            message("accno1", null, "Please enter the required information"),
            message("accno2", null, "Not required"),
            message("accno3", null, "Please enter a valid Account number"),
            message("accno4", null, "Please enter the required information"),
            message("c2", null, "Not required"),
            message("cbank2", null, "Not required"),
            message("ccn2", null, "Please enter the required information"),
            message("ccn3", null, "CustomsClientNumber must be numeric and contain between 8 and 13 digits. If your number is 5 digits, please add 000 before your number i.e. 00012345"),
            message("ccn4", null, "CustomsClientNumber must be numeric and contain between 8 and 13 digits. If your number is 5 digits, please add 000 before your number i.e. 00012345"),
            message("ccn5", null, "Your importers code (CCN) exceeds your total transaction cost of R50 000.00"),
            message("ccn6", null, "Not required"),
            message("ccntry1", null, "Please enter the required information"),
            message("ccntry2", null, "Not required"),
            message("ccntry3", null, "Please enter a valid SWIFT code"),
            message("cn1", null, "Please enter the required information"),
            message("cn2", null, "Not required"),
            message("cnte1", null, "Please enter either an email, fax or telephone number"),
            message("cnte2", null, "Not required"),
            message("cnte4", null, "Please enter a valid email address"),
            message("cntft1", null, "Please enter either an email, fax or telephone number"),
            message("cntft2", null, "Not required"),
            message("cntft3", null, "Please enter a phone number in 10 or 15 digits format"),
            message("crd1", null, "Please enter the required information"),
            message("crd2", null, "Not required"),
            message("crdi1", null, "Please enter the required information"),
            message("crdi2", null, "Not required"),
            message("eaccid1", null, "Your registration number is invalid"),
            message("fcurr1", null, "Please enter the required information"),
            message("fcurr2", null, "Please enter a Valid SWIFT currency code"),
            message("flow1", null, "Please enter the required information"),
            message("flw_a1_1", null, "Please enter the required information"),
            message("flw_a1_2", null, "Address shouldn't exceed 35 characters"),
            message("flw_a1_4", null, "Please enter the required information"),
            message("flw_a2_1", null, "Address shouldn't exceed 35 characters"),
            message("flw_addr_l1", null, "This field shouldn't exceed 35 characters"),
            message("flw_ahs1", null, "Please enter the required information"),
            message("flw_ahs2", null, "Please use BOP 250 or 251 for travel"),
            message("flw_bba1", null, "Please enter the required information"),
            message("flw_bbbc1", null, "Please enter the required information"),
            message("flw_bbc", null, "Please enter the required information"),
            message("flw_bbc1", null, "Please enter the required information"),
            message("flw_bbnm1", null, "Please enter the required information"),
            message("flw_c1", null, "Please enter the required information"),
            message("flw_c2", null, "City shouldn't exceed 35 characters"),
            message("flw_c4", null, "Please enter the required information"),
            message("flw_cbc1", null, "Please enter the required information"),
            message("flw_cn1", null, "Contact name shouldn't exceed 50 characters"),
            message("flw_cnte1", null, "Please provide a valid email address"),
            message("flw_cps1", null, "Please enter the required information"),
            message("flw_csn1", null, "Surname shouldn't exceed 35 characters"),
            message("flw_iisa1", null, "Please enter the required information"),
            message("flw_iisa2", null, "Not required for ZAR payments"),
            message("flw_isa1", null, "Please enter the required information"),
            message("flw_isa2", null, "Foreign Currency Account (FCA) is not required, please remove"),
            message("flw_isa3", null, "Foreign Currency Account and Account to be Debitted must not match"),
            message("flw_lc1", null, "Please enter the required information"),
            message("flw_lc5", null, "Not required"),
            message("flw_masad2", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_masan1", null, "Please enter the required information"),
            message("flw_masan2", null, "Please enter the required information"),
            message("flw_mcc1", null, "You must be a South African Resident to use codes 511, 512, 513, or 516"),
            message("flw_mexc1", null, "Please enter the required information for BOP category 830"),
            message("flw_mexc2", null, "The required information shouldn't exceed 100 characters"),
            message("flw_mlibr1", null, "Please provide the base rate for this BOP category"),
            message("flw_mlipm1", null, "Please enter the required information for loans"),
            message("flw_mlipm12", null, "Only provide the required information if using a Interest Base Rate"),
            message("flw_mlir1", null, "Please provide the base rate for this BOP category"),
            message("flw_mlir3", null, "Please provide the base rate for this BOP category"),
            message("flw_mlir7", null, "Please enter the required information"),
            message("flw_mlit2", null, "Only provide the required information if using a Interest Base Rate (and this rate needs a term)"),
            message("flw_mlrn1", null, "Reference number shouldn't exceed 20 characters"),
            message("flw_mlt1", null, "Please provide a Loan Tenor for this BOP category"),
            message("flw_mtpcd_l1", null, "Surname shouldn't exceed 35 characters"),
            message("flw_mtpcdn_l1", null, "Contact name shouldn't exceed 50 characters"),
            message("flw_mtpibd3", null, "The date of birth should match the first 6 letters of your ID number"),
            message("flw_mtpibd4", null, "Please enter the required information"),
            message("flw_mtpibd5", null, "The date of birth does not match the first 6 letters of your ID number"),
            message("flw_mtpiid3", null, "Please enter a valid ID number"),
            message("flw_mtpinm_l1", null, "Name shouldn't exceed 50 characters"),
            message("flw_mtpinm0", null, "Please enter the required information"),
            message("flw_mtpinm3", null, "Please enter the required information"),
            message("flw_mtpipe1", null, "Please enter the required information"),
            message("flw_mtpipe2", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_mtpipe5", null, "Your passport has expired"),
            message("flw_mtpipn_l1", null, "Passport number shouldn't exceed 15 characters"),
            message("flw_mtpisn_l1", null, "Surname shouldn't exceed 35 characters"),
            message("flw_mtpisn3", null, "Not required"),
            message("flw_mtpisn4", null, "Please enter the required information"),
            message("flw_mtpite1", null, "Please enter the required information"),
            message("flw_mtpite2", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_mtpitp_l1", null, "Permit number is limited to 35 characters"),
            message("flw_nrian1", null, "Please enter the required information"),
            message("flw_nrian4", null, "Account number shouldn't exceed 34 characters"),
            message("flw_nrian5", null, "Not required"),
            message("flw_nrlen_l1", null, "Company name shouldn't exceed 70 characters"),
            message("flw_nrnm_l1", null, "Name shouldn't exceed 50 characters"),
            message("flw_nrnm1", null, "Please enter the required information"),
            message("flw_nrsn_l1", null, "Surname shouldn't exceed 35 characters"),
            message("flw_p10", null, "Please enter a valid South African province"),
            message("flw_p2", null, "Please enter the required information"),
            message("flw_p3", null, "Province shouldn't exceed 35 characters"),
            message("flw_relen1", null, "Company name shouldn't exceed 70 characters"),
            message("flw_resap1", null, "Please enter a valid South African province"),
            message("flw_ridob1", null, "Please enter a valid Date of birth"),
            message("flw_riidn1", null, "Not required"),
            message("flw_rimn1", null, "Middle name shouldn't exceed 50 characters"),
            message("flw_rin1", null, "Name shouldn't exceed 50 characters"),
            message("flw_ripc1", null, "Please provide the required information for BOP category 250 or 251"),
            message("flw_ripc2", null, "Please enter the required information"),
            message("flw_ripc3", null, "Please enter the required information"),
            message("flw_ripe1", null, "Please enter the required information"),
            message("flw_ripe2", null, "Please enter the required information"),
            message("flw_ripe3", null, "Please enter the required information"),
            message("flw_ripe4", null, "Please enter the required information"),
            message("flw_ripe5", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_ripe6", null, "Your passport has expired"),
            message("flw_ripn1", null, "Please enter the required information"),
            message("flw_ripn2", null, "Please enter the required information"),
            message("flw_ripn3", null, "Please enter the required information"),
            message("flw_ripn4", null, "Passport number shouldn't exceed 15 characters"),
            message("flw_risap1", null, "Please enter a valid South African province"),
            message("flw_risn1", null, "Surname shouldn't exceed 35 characters"),
            message("flw_ritrpe1", null, "Please enter the required information"),
            message("flw_ritrpe3", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_ritrpe4", null, "Your temporary resident permit number has expired"),
            message("flw_ritrpn2", null, "Permit number shouldn't exceed 35 characters"),
            message("flw_ritrpn3o", null, "Please enter the required information"),
            message("flw_rsac1", null, "Please enter the required information"),
            message("flw_rsam1", null, "Please enter the required information"),
            message("flw_rsam3", null, "not required"),
            message("flw_s1", null, "Please enter the required information"),
            message("flw_s2", null, "Suburb shouldn't exceed 35 characters"),
            message("flw_s4", null, "Please enter the required information"),
            message("flw_spc_l1", null, "Postal code shouldn't exceed 10 characters"),
            message("flw_spc1", null, "Please enter the required information"),
            message("flw_tccr1", null, "Not required"),
            message("flw_tccr3", null, "Please enter the required information"),
            message("flw_tn1", null, "Tax number shouldn't exceed 30 characters"),
            message("flw_tpits1", null, "Please enter the required information"),
            message("flw_tpits2", null, "Please enter the required information for BOP category 256"),
            message("flw_tpits3", null, "not required"),
            message("flw_tpits5", null, "not required"),
            message("flw_tvdc1", null, "Please enter the required information for BOP cateogry 225 and 256"),
            message("flw_tvdc2", null, "not required"),
            message("flw_tvlbp1", null, "Please enter the required information when traveling by road"),
            message("flw_tvlbp2", null, "not required"),
            message("flw_tvlbp3", null, "not required"),
            message("flw_tvldd1", null, "Please enter the required information"),
            message("flw_tvldd2", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("flw_tvldd3", null, "Not required"),
            message("flw_tvlm1", null, "Please enter the required information"),
            message("flw_tvlm2", null, "Not required"),
            message("flw_tvltn1", null, "Please enter the required information"),
            message("flw_tvltn2", null, "Not required"),
            message("g1", null, "Please enter the required information"),
            message("ieicn1", null, "Please enter the required information"),
            message("ieicn2", null, "Not required"),
            message("ieicn3", null, "Please enter a valid Import control number: INV followed by number, minimum 4 characters"),
            message("ieicn4", null, "Please enter the format as follows: AAACCYYMMDD0000000 where AAA is a valid customs office code in alpha format; CC is the century of import, YY is the year of import, MM is the month of import, DD is the day of import, and 0000000 is the 7 digit unique bill of entry number allocated by SARS as part of the MRN"),
            message("ieicn5", null, "No additional spaces or comma's (,) allowed"),
            message("ieicn6", null, "Please enter the required information"),
            message("iepcc1", null, "Please enter the required information"),
            message("iepcc2", null, "The currency code should match the transaction currency"),
            message("iepv1", null, "Please enter the required information"),
            message("ietdn1", null, "Please enter the required information"),
            message("ietdn2", null, "Not required"),
            message("ietdn3", null, "Please enter the required information"),
            message("ietdn4", null, "Not required"),
            message("ieucr4", null, "Not required"),
            message("madhd1", null, "Please enter the required information"),
            message("madhd2", null, "Not required"),
            message("madhd3", null, "Not required"),
            message("madhs12", null, "Amount shouldn't exceed R3 000"),
            message("madhs13", null, "Not allowed for Remittance dispensation"),
            message("madhs17", null, "Please enter a valid IHQ number"),
            message("madhs2", null, "Not required"),
            message("maiad2", null, "Not required"),
            message("maiad3", null, "Please enter a valid date format: CCYY-MM-DD"),
            message("maian2", null, "Not required"),
            message("mars2", null, "Not required"),
            message("masan3", null, "Not required"),
            message("masan4", null, "Please enter the required information"),
            message("masar3", null, "Not required"),
            message("mcc1", null, "Please enter the required information"),
            message("mcc2", null, "Not required"),
            message("mcc3", null, "Please enter a valid Finsurv category"),
            message("mcc6", null, "Please enter the required information"),
            message("mcc8", null, "Only required for Import undertaking customers"),
            message("mcc9", null, "Only required for Import undertaking customers"),
            message("mcrdcb1", null, "Not required"),
            message("mcrdci1", null, "Not required"),
            message("mcrdec1", null, "Please enter the required information"),
            message("mcrdec2", null, "Not required"),
            message("mcrdem1", null, "Not required"),
            message("mcrdem2", null, "Please enter the required information"),
            message("mcrdfp1", null, "Not required"),
            message("mcrdfp2", null, "Please enter the required information"),
            message("mcrdfp3", null, "This field shouldn't contain a negative value"),
            message("mcrdft1", null, "Not required"),
            message("mcrdfw1", null, "Not required"),
            message("mcrdfw2", null, "Please enter the required information"),
            message("mcrdfw3", null, "This field shouldn't contain a negative value"),
            message("mdircd1", null, "Not required"),
            message("mdirtr1", null, "Not required"),
            message("mexc1", null, "Please enter the required information"),
            message("mexc2", null, "Not required"),
            message("mfv2", null, "This field shouldn't contain a negative value"),
            message("mfv3", null, "Please enter the required information"),
            message("mfv4", null, "Not required"),
            message("mfv5", null, "Foreign value shouldn't exceed 20 digits"),
            message("mfv6", null, "Please enter the required information"),
            message("mlc5", null, "Not required"),
            message("mlc6", null, "Please enter the required information"),
            message("mlc7", null, "Please enter the required information"),
            message("mlir2", null, "Not required"),
            message("mlir4", null, "Please enter the required information in the format 0.00"),
            message("mlir5", null, "Interest rate cannot be greater than 100%"),
            message("mlir7", null, "Not required"),
            message("mlrn1", null, "Please enter the required information"),
            message("mlrn10", null, "Not required"),
            message("mlrn11", null, "Not required"),
            message("mlrn2", null, "Loan reference number must be 99012301230123"),
            message("mlrn3", null, "Loan reference number must be 99456745674567"),
            message("mlrn4", null, "Loan reference number must be 99789078907890"),
            message("mlrn5", null, "Loan reference number must be 99012301230123"),
            message("mlrn6", null, "Loan reference number must be 99456745674567"),
            message("mlrn7", null, "Loan reference number must be 99789078907890"),
            message("mlrn8", null, "Please enter the required information"),
            message("mlrn9", null, "Not required"),
            message("mlt1", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("mlt2", null, "Please enter a future date in the format: CCYY-MM-DD"),
            message("mlt3", null, "Not required"),
            message("mrtrn1", null, "Please enter the required information"),
            message("mrtrn3", null, "Not required"),
            message("mrtrn5", null, "Not required"),
            message("mrtrs1", null, "Please enter the required information"),
            message("mrtrs2", null, "Please enter the required information"),
            message("mrtrs3", null, "Not required"),
            message("mrv5", null, "Not required"),
            message("mrv6", null, "The amount is too high for the BOP cateogry selected"),
            message("mrv7", null, "This field shouldn't exceed 1 000"),
            message("mrv8", null, "This field shouldn't exceed 20 digits"),
            message("mrv9", null, "Please enter the required information"),
            message("msc1", null, "Please enter a valid sub category code"),
            message("msc2", null, "Not required"),
            message("mseq1", null, "Please enter the required information"),
            message("msrn3", null, "Not required"),
            message("mswd1", null, "Not required"),
            message("mta5", null, "Please enter the required information"),
            message("mtie1", null, "Please enter the required information"),
            message("mtie2", null, "Not required"),
            message("mtie5", null, "Not required"),
            message("mtpccn1", null, "Customs Client Number must be numeric and contain between 8 and 13 digits. If your number is 5 digits, please add 000 before your number i.e. 00012345"),
            message("mtpccn2", null, "Please enter a valid Customs client number"),
            message("mtpccn3", null, "Not required"),
            message("mtpccn4", null, "Not required"),
            message("mtpcde1", null, "Please enter the required information"),
            message("mtpcde2", null, "Not required"),
            message("mtpcde3", null, "Not required"),
            message("mtpcdf1", null, "Please enter the required information"),
            message("mtpcdf2", null, "Not required"),
            message("mtpcdf3", null, "Not required"),
            message("mtpcdn1", null, "Not required"),
            message("mtpcdn2", null, "Not required"),
            message("mtpcdn3", null, "Not required"),
            message("mtpcds1", null, "Please enter the required information"),
            message("mtpcds2", null, "Not required"),
            message("mtpcds3", null, "Not required"),
            message("mtpcdt1", null, "Please enter the required information"),
            message("mtpcdt2", null, "Not required"),
            message("mtpcdt3", null, "Not required"),
            message("mtpenm2", null, "Not required"),
            message("mtpenm3", null, "Not required"),
            message("mtpern1", null, "Not required"),
            message("mtpern2", null, "Registration number shouldn't be the same as the companies registration number"),
            message("mtpern3", null, "Not required"),
            message("mtpi1", null, "Please enter the required information"),
            message("mtpibd1", null, "Please enter the required information"),
            message("mtpibd2", null, "Please enter the required information"),
            message("mtpibd3", null, "Please enter the required information"),
            message("mtpibd4", null, "Not required"),
            message("mtpibd5", null, "Please enter the required information"),
            message("mtpibd6", null, "Please enter a valid date format: YYYY-MM-DD"),
            message("mtpibd7", null, "Not required"),
            message("mtpig1", null, "Please enter the required information"),
            message("mtpig2", null, "Please enter valid gender"),
            message("mtpig3", null, "Please enter the required information"),
            message("mtpig4", null, "Please enter the required information"),
            message("mtpig5", null, "Not required"),
            message("mtpig6", null, "Please enter the required information"),
            message("mtpig7", null, "Please enter the required information"),
            message("mtpiid1", null, "Please enter the required information"),
            message("mtpiid2", null, "Please enter the required information"),
            message("mtpiid3", null, "Please enter a valid ID number"),
            message("mtpiid4", null, "Not required"),
            message("mtpiid5", null, "Please enter the required information"),
            message("mtpiid6", null, "ID number shouldn't be the same as the applicant"),
            message("mtpiid7", null, "Not required"),
            message("mtpiid8", null, "Please enter the required information"),
            message("mtpinm1", null, "Please enter the required information"),
            message("mtpinm2", null, "Please enter the required information"),
            message("mtpinm3", null, "Please enter the required information"),
            message("mtpinm4", null, "Name shouldn't be the same as the applicant"),
            message("mtpinm5", null, "Not required"),
            message("mtpinm6", null, "Please enter the required information"),
            message("mtpinm7", null, "Not required"),
            message("mtpipc1", null, "Please enter the required information"),
            message("mtpipc2", null, "Not required"),
            message("mtpipc3", null, "Please enter a valid SWIFT code"),
            message("mtpipc4", null, "Not required"),
            message("mtpipn1", null, "Please enter the required information"),
            message("mtpipn3", null, "Not required"),
            message("mtpipn4", null, "Passport number shouldn't be the same as the applicant"),
            message("mtpipn5", null, "Not required"),
            message("mtpipn6", null, "Please enter the required information"),
            message("mtpisn1", null, "Please enter the required information"),
            message("mtpisn10", null, "Not required"),
            message("mtpisn2", null, "Not required"),
            message("mtpisn3", null, "Please enter the required information"),
            message("mtpisn4", null, "Please enter the required information"),
            message("mtpisn5", null, "Surname shouldn't be the same as the applicant"),
            message("mtpisn6", null, "Please enter the required information"),
            message("mtpisn7", null, "Not required"),
            message("mtpitp1", null, "Please enter the required information"),
            message("mtpitp2", null, "Not required"),
            message("mtpitp3", null, "Please enter the required information"),
            message("mtpitp4", null, "not required"),
            message("mtpitp5", null, "Temporary resident permit number shouldn't be the same as the applicants"),
            message("mtpitp6", null, "Not required"),
            message("mtpitp7", null, "Please enter the required information"),
            message("mtppac1", null, "Please enter the required information"),
            message("mtppac2", null, "Not required"),
            message("mtppac3", null, "Not required"),
            message("mtppal11", null, "Please enter the required information"),
            message("fe_mtppal11", null, "Please enter the required information"),
            message("mtppal12", null, "Not required"),
            message("mtppal14", null, "Not required"),
            message("mtppal21", null, "Not required"),
            message("mtppal22", null, "Please enter the required information"),
            message("mtppap1", null, "Please enter the required information"),
            message("mtppap2", null, "Not required"),
            message("mtppap3", null, "Please enter a valid province"),
            message("mtppap4", null, "Not required"),
            message("mtppas1", null, "Please enter the required information"),
            message("mtppas2", null, "not required"),
            message("mtppas3", null, "not required"),
            message("mtppaz1", null, "Please enter the required information"),
            message("mtppaz2", null, "not required"),
            message("mtppaz3", null, "Please enter a valid postal code"),
            message("mtppaz4", null, "not required"),
            message("mtpsac1", null, "Please enter the required information"),
            message("mtpsac2", null, "not required"),
            message("mtpsac3", null, "not required"),
            message("mtpsal11", null, "Please enter the required information"),
            message("fe_mtpsal11", null, "Please enter the required information"),
            message("mtpsal12", null, "not required"),
            message("mtpsal14", null, "not required"),
            message("mtpsal21", null, "not required"),
            message("mtpsal22", null, "not required"),
            message("mtpsap1", null, "Please enter the required information"),
            message("mtpsap2", null, "not required"),
            message("mtpsap3", null, "Please enter a valid province"),
            message("mtpsap4", null, "not required"),
            message("mtpsas1", null, "Please enter the required information"),
            message("mtpsas2", null, "not required"),
            message("mtpsas3", null, "not required"),
            message("mtpsaz1", null, "Please enter the required information"),
            message("mtpsaz2", null, "not required"),
            message("mtpsaz3", null, "Please enter a valid postal code"),
            message("mtpsaz4", null, "not required"),
            message("mtptx2", null, "not required"),
            message("mtptx3", null, "Tax number shouldn't be the same as the applicant"),
            message("mtptx5", null, "not required"),
            message("mtpvn1", null, "not required"),
            message("mtpvn2", null, "VAT number shouldn't be the same as the applicants"),
            message("mtpvn3", null, "Not required"),
            message("mtvl1", null, "Please add third party details for new Finsurv"),
            message("NonResident.Entity.AccountNumber.minLen", null, "Number entered is too short"),
            message("NonResident.Entity.Address.AddressLine1.minLen", null, "Address entered is too short"),
            message("NonResident.Entity.Address.AddressLine2.minLen", null, "Address entered is too short"),
            message("NonResident.Entity.Address.City.minLen", null, "City entered is too short"),
            message("NonResident.Entity.Address.Country.minLen", null, "Country code entered is too short"),
            message("NonResident.Entity.Address.PostalCode.minLen", null, "Code entered is too short"),
            message("NonResident.Entity.Address.State.minLen", null, "State entered is too short"),
            message("NonResident.Entity.Address.Suburb.minLen", null, "Suburb entered is too short"),
            message("NonResident.Entity.EntityName.minLen", null, "Name entered is too short"),
            message("NonResident.Individual.AccountNumber.minLen", null, "Number entered is too short"),
            message("NonResident.Individual.Address.AddressLine1.minLen", null, "Address entered is too short"),
            message("NonResident.Individual.Address.AddressLine2.minLen", null, "Address entered is too short"),
            message("NonResident.Individual.Address.City.minLen", null, "City entered is too short"),
            message("NonResident.Individual.Address.Country.minLen", null, "Country code entered is too short"),
            message("NonResident.Individual.Address.PostalCode.minLen", null, "Code entered is too short"),
            message("NonResident.Individual.Address.State.minLen", null, "State entered is too short"),
            message("NonResident.Individual.Address.Suburb.minLen", null, "Suburb entered is too short"),
            message("NonResident.Individual.PassportNumber.minLen", null, "Passport number entered is too short"),
            message("nr3", null, "Please enter the required information"),
            message("nr4", null, "Please enter the required information"),
            message("nr5", null, "Not required"),
            message("nr6", null, "This category is only allowed for individuals."),
            message("flw_CCnr6", null, "This category is only allowed for individuals."),
            message("nrcmc1", null, "Please enter the required information"),
            message("nrcmc2", null, "Not required"),
            message("nrcmn1", null, "Please enter the required information"),
            message("nrcmn2", null, "Not required"),
            message("nrcmn3", null, "No additional spaces allowed"),
            message("nrcmn4", null, "Please enter the required information"),
            message("nrex1", null, "Not required"),
            message("nrex2", null, "Not required"),
            message("nrgn1", null, "Please enter a valid gender"),
            message("nriaid13", null, "Please enter the required information"),
            message("nriaid14", null, "Please enter the required information"),
            message("nrial11", null, "Not required"),
            message("nrial21", null, "Not required"),
            message("nrial31", null, "Not required"),
            message("nrian2", null, "Company account number shouldn't be the same as match the beneficiaries"),
            message("nrian3", null, "Special characters ' or & are not allowed"),
            message("nrian4", null, "Not required"),
            message("nric2", null, "Not required"),
            message("nrictry1", null, "Please enter the required information"),
            message("nrictry2", null, "Please enter a valid SWIFT country code"),
            message("nrictry5", null, "EU is not a valid county"),
            message("nrictry9", null, "Not required"),
            message("nris2", null, "Not required"),
            message("nriz2", null, "Not required"),
            message("nriz3", null, "Postal code length is too long"),
            message("nrlen1", null, "Please enter the required information"),
            message("nrlen2", null, "Not required"),
            message("nrlen4", null, "Not required"),
            message("nrlen5", null, "Please enter the required information"),
            message("nrnm1", null, "Please enter the required information"),
            message("nrpc2", null, "Please enter a valid country code"),
            message("nrpn2", null, "No additional spaces allowed"),
            message("nrsn1", null, "Please enter the required information"),
            message("nrsn3", null, "Please enter the required information"),
            message("obank2", null, "Not required"),
            message("ocntry1", null, "Please enter the required information"),
            message("ocntry2", null, "Not required"),
            message("ocntry5", null, "Please enter a valid SWIFT country code"),
            message("OriginatingBank.minLen", null, "Bank name entered is too short"),
            message("p2", null, "Not required"),
            message("p4", null, "Please enter a valid province"),
            message("p5", null, "Province is Namibia"),
            message("p6", null, "Province is Lesotho"),
            message("p7", null, "Province is Swaziland"),
            message("PaymentDetail.BeneficiaryBank.Address.minLen", null, "Address entered is too short"),
            message("PaymentDetail.BeneficiaryBank.BankName.minLen", null, "Bank name entered is too short"),
            message("PaymentDetail.BeneficiaryBank.BranchCode.minLen", null, "Branch code entered is too short"),
            message("PaymentDetail.BeneficiaryBank.City.minLen", null, "City entered is too short"),
            message("PaymentDetail.BeneficiaryBank.SWIFTBIC.len", null, "SWIFT BIC should be 11 characters"),
            message("PaymentDetail.CorrespondentBank.Address.minLen", null, "Address entered is too short"),
            message("PaymentDetail.CorrespondentBank.BankName.minLen", null, "Bank name entered is too short"),
            message("PaymentDetail.CorrespondentBank.BranchCode.minLen", null, "Branch code entered is too short"),
            message("PaymentDetail.CorrespondentBank.City.minLen", null, "City entered is too short"),
            message("PaymentDetail.CorrespondentBank.SWIFTBIC.minLen", null, "SWIFT BIC should be 11 characters"),
            message("pc1", null, "Please enter a valid postal code"),
            message("pc3", null, "Not required"),
            message("pc4", null, "The postal code for this province is 9999"),
            message("rbank2", null, "Not required"),
            message("rbank3", null, "Please enter the required information"),
            message("rcntry1", null, "Please enter the required information"),
            message("rcntry2", null, "Not required"),
            message("rcntry3", null, "Please enter a valid SWIFT code"),
            message("rec1", null, "Please enter the required information"),
            message("rec2", null, "Country cannot be a local country"),
            message("rec3", null, "Please enter a valid country code"),
            message("rec4", null, "Not required"),
            message("reic2", null, "Not required"),
            message("reic3", null, "Please enter a valid industrial classification code"),
            message("reis2", null, "Not required"),
            message("reis3", null, "Please enter a valid sector code"),
            message("relen1", null, "Please enter the required information"),
            message("relen2", null, "Not required"),
            message("relen3", null, "Not required"),
            message("ren10", null, "Not required"),
            message("ren13", null, "Not required"),
            message("ren14", null, "Not required"),
            message("ren15", null, "Not required"),
            message("ren17", null, "Not required"),
            message("ren18", null, "Not required"),
            message("ren19", null, "Not required"),
            message("ren20", null, "Not required"),
            message("ren21", null, "Not required"),
            message("ren22", null, "Not required"),
            message("ren23", null, "Not required"),
            message("ren24", null, "Not required"),
            message("ren25", null, "Not required"),
            message("ren26", null, "Not required"),
            message("ren27", null, "Not required"),
            message("ren3", null, "Not required"),
            message("ren4", null, "Not required"),
            message("ren5", null, "Not required"),
            message("ren6", null, "Please enter the required information"),
            message("ren7", null, "Not required"),
            message("ren8", null, "Not required"),
            message("ren9", null, "Not required"),
            message("repor1", null, "Please enter the required information"),
            message("repor2", null, "Please enter the required information"),
            message("repor3", null, "No additional spaces allowed"),
            message("repq1", null, "Please enter the required information"),
            message("repyn1", null, "Please enter the required information"),
            message("rern1", null, "Please enter the required information"),
            message("rern3", null, "Registration number shouldn't be the same as SARB registration number"),
            message("rern4", null, "No additional spaces allowed"),
            message("Resident.Entity.AccountName.minLen", null, "Name entered is too short"),
            message("Resident.Entity.AccountNumber.minLen", null, "Account number entered is too short"),
            message("Resident.Entity.ContactDetails.ContactName.minLen", null, "Name entered is too short"),
            message("Resident.Entity.ContactDetails.ContactSurname.minLen", null, "Surname must not match the applicant"),
            message("Resident.Entity.ContactDetails.Email.minLen", null, "Email entered is too short"),
            message("Resident.Entity.ContactDetails.Fax.minLen", null, "Fax number entered is too short"),
            message("Resident.Entity.ContactDetails.Telephone.minLen", null, "A minimum amount of 10 numbers is required"),
            message("Resident.Individual.ForeignIDCountry.minLen", null, "Country entered is too short"),
            message("Resident.Individual.ForeignIDNumber.minLen", null, "ID number entered is too short"),
            message("Resident.Individual.IDNumber.minLen", null, "ID number entered is too short"),
            message("Resident.Individual.PassportCountry.minLen", null, "Country entered is too short"),
            message("Resident.Individual.PassportNumber.minLen", null, "Passport number entered is too short"),
            message("Resident.Entity.StreetAddress.AddressLine1.minLen", null, "Address entered is too short"),
            message("Resident.Entity.StreetAddress.AddressLine2.minLen", null, "Address entered is too short"),
            message("Resident.Entity.StreetAddress.City.minLen", null, "City entered is too short"),
            message("Resident.Entity.StreetAddress.PostalCode.minLen", null, "Code entered is too short"),
            message("Resident.Entity.StreetAddress.State.minLen", null, "State entered is too short"),
            message("Resident.Entity.StreetAddress.Suburb.minLen", null, "Suburb entered is too short"),
            message("Resident.Entity.TaxClearanceCertificateReference.minLen", null, "Reference entered is too short"),
            message("Resident.Entity.TaxNumber.minLen", null, "Tax number entered is too short"),
            message("Resident.Entity.TempResPermitNumber.minLen", null, "Permit number is too short"),
            message("Resident.Entity.VATNumber.minLen", null, "VAT number entered is too short"),
            message("rg2", null, "Please enter the required information"),
            message("rg5", null, "Not required"),
            message("rg7", null, "Not required"),
            message("ridob1", null, "Please enter the required information"),
            message("ridob2", null, "Please enter a valid date of birth"),
            message("rifidc1", null, "Please enter the required information"),
            message("tni1", null, "Please enter the required information"),
            message("tne1", null, "Please enter the required information"),
            message("nrpn1", null, "Please enter the required information"),
            message("nrpc1", null, "Please enter the required information"),
            message("vne1", null, "Please enter the required information"),
            message("tne1", null, "Please enter the required information"),
            ignore("bh_brc1"),
            ignore("bh_brc2"),
            ignore("bh_brc3"),
            ignore("bh_brn1"),
            ignore("bh_brn2"),
            ignore("bh_brn3"),
            ignore("vd2")
          ]
        }
      ]
    };
    v2 = {
      ruleset: "Money Rules for Flow Form", 
      scope: "money", 
      validations: [
        {
          field: "", 
          rules: [
            ignore("mtie1"),
            ignore("mtie4"),
            ignore("mtie5"),
            ignore("mars1"),
            ignore("maian1"),
            ignore("masan1")
          ]
        },
        {
          field: "CategoryCode", 
          rules: []
        },
        {
          field: "ThirdParty.Individual.Surname", 
          rules: [
            failure("ibr_tpisn1", "IB3", "This is required", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow(),
            failure("ibr_tpisn2", "IB4", "This is required", isEmpty.and(hasMoneyFieldValue("ThirdPartyKind", "Individual")).and(hasMoneyField("ThirdParty")))
          ]
        },
        {
          field: "ThirdParty.Entity.Name", 
          rules: [
            failure("ibr_tpeen1", "IB5", "This is required", isEmpty.and(hasMoneyFieldValue("ThirdPartyKind", "Entity")).and(hasMoneyField("ThirdParty")))
          ]
        },
        {
          field: "{{Regulator}}Auth.IsSARBAuth", 
          rules: [
            failure("ibr_sa1", "IB7", "This is required", hasValue(undefined).and(hasMoneyFieldValue("haveAuth", true)))
          ]
        },
        {
          field: "ImportExport", 
          rules: [
            failure("ibr_mtie1", "490", "If Inflow and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, the ImportExport element must be completed", emptyImportExport.and(not(hasMoneyFieldValue("CategoryCode", "106").and(not(notImportUndertakingClient))))).onInflow().onSection("AB").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"]),
            failure("ibr_mtie4", "490", "If outflow and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, the ImportExport element must be completed unless a) the Subject is SDA or REMITTANCE DISPENSATION or b) category 106 and import undertaking client", emptyImportExport.and(notMoneyFieldValue("AdHocRequirement.Subject", ["SDA", "REMITTANCE DISPENSATION"]).and(not(hasMoneyFieldValue("CategoryCode", "106").and(importUndertakingClient))))).onOutflow().onSection("A").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"]),
            failure("ibr_mtie5", "491", "For any category other than 101/01 to 101/11 or 103/01 to 103/11 or 105 or 106, the ImportExport element must not be completed", notEmptyImportExport).onSection("ABG").notOnCategory(["101", "103", "105", "106"])
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber", 
          rules: [
            failure("bol_masarn1", "ARN", "Must be completed if the authorisation type is the regulator", isEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "Y")))
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", 
          rules: [
            failure("bol_masan1", "AAN", "Must be completed if the authorisation type is the regulator", isEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "Y"))),
            failure("bol_masan2", "AAN", "AuthAppNumber needs to be 8 numbers", notEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "Y")).and(notPattern(/^\d{8}$/))),
            validate("bol_arm2", "Verify_ARM", notEmpty.and(hasMoneyField(map("{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber"))).and(hasMoneyField(map("{{Regulator}}Auth.{{RegulatorPrefix}}AuthDate"))).and(hasMoneyFieldValue(map("{{Regulator}}Auth.AuthIssuer"), "16")).and(hasPattern(/^\d{8}$/))).onSection("ABCDEFG")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthDate", 
          rules: [
            failure("bol_maad", "RAD", "Must be completed if the authorisation type is the regulator and the Auth Issues is Standard Bank", isEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "Y")).and(hasMoneyFieldValue(map("{{Regulator}}Auth.AuthIssuer"), "16")))
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", 
          rules: [
            failure("bol_maian1", "IAN", "Must be completed if the authorisation type is the Authorised Dealer", isEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "N"))),
            failure("bol_maian2", "IAN", "InternalAuthNumber needs to be 8 numbers", notEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "N")).and(notPattern(/^\d{8}$/))),
            validate("bol_arm1", "Verify_ARM", notEmpty.and(hasMoneyField(map("{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumberDate"))).and(hasMoneyFieldValue(map("{{Regulator}}Auth.AuthIssuer"), "16")).and(hasPattern(/^\d{8}$/))).onSection("ABCDEFG")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumberDate", 
          rules: [
            failure("bol_maiand1", "ARN", "Must be completed if the authorisation type is the Authorised Dealer", isEmpty.and(hasMoneyFieldValue(map("{{Regulator}}Auth.IsSARBAuth"), "N")))
          ]
        }
      ]
    };
    v3 = {
      ruleset: "IVS sbZA Import/Export Rules", 
      scope: "importexport", 
      validations: [
        {
          field: "IVSResponseCodes", 
          rules: [
            validate("bol_ieivs1", "Validate_IVS", evalIEField("ImportControlNumber", isValidICN).and(evalIEField("TransportDocumentNumber", isTooLong("1"))).and(evalIEField("TransportDocumentNumber", isTooShort("36"))).and(evalTransactionField("Resident.Individual.CustomsClientNumber", hasPattern(/^\d{8,13}$/)).or(evalTransactionField("Resident.Entity.CustomsClientNumber", hasPattern(/^\d{8,13}$/))).or(evalMoneyField("ThirdParty.CustomsClientNumber", hasPattern(/^\d{8,13}$/)))).and(not(hasMoneyField("ThirdParty.CustomsClientNumber").and(evalMoneyField("ThirdParty.CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))).and(not(evalResidentField("CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))))).onOutflow().onSection("ABG").notOnCategory("103/11").onCategory(["103", "105", "106"])
          ]
        }
      ]
    };
    v4 = {
      ruleset: "Money Rules for MTA Accounts", 
      scope: "money", 
      validations: [
        {
          field: "CategoryCode", 
          rules: [
            failure("mtaa_cc1", "CC1", "Category 833 cannot be used for this transaction since the Resident Account Number is not an MTA account", notEmpty.and(not(evalResidentField("AccountNumber", isInLookup("mtaAccounts", "accountNumber"))))).onSection("A").onCategory(["833"]),
            failure("mtaa_cc2", "CC2", "Category 833 (or 800) must be used for this transaction since the Resident Account Number is an MTA (ADLA) account", evalResidentField("AccountNumber", hasLookupValue("mtaAccounts", "accountNumber", "isADLA", true)).and(evalResidentField("AccountNumber", isInLookup("mtaAccounts", "accountNumber")))).onSection("A").notOnCategory(["833", "800"])
          ]
        }
      ]
    };
    v5 = {
      ruleset: "External Transaction Rules for Limits", 
      scope: "transaction", 
      validations: [
        {
          field: "Resident.Individual.IDNumber", 
          rules: [
            validate("ext_mlval1", "Validate_IndividualSDA", notEmpty.and(hasTransactionField("ValueDate")).and(hasSDAMonetaryValue)).onOutflow().onSection("A"),
            validate("ext_mlval3", "Validate_IndividualFIA", notEmpty.and(hasTransactionField("ValueDate")).and(hasFIAMonetaryValue)).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("fl_riidn1", "DAL", "ID Number has exceeded the discretionary amount limit of R1,000,000 within this transaction", notEmpty.and(not(isValidSDAAmount))).onOutflow()
          ]
        }
      ]
    };
    v6 = {
      ruleset: "External Money Rules for Limits", 
      scope: "money", 
      validations: [
        {
          field: "ThirdParty.Individual.IDNumber", 
          rules: [
            validate("ext_mlval2", "Validate_IndividualSDA", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA")).and(hasTransactionField("ValueDate")).and(hasSDAMonetaryValue)).onOutflow().onSection("A"),
            validate("ext_mlval4", "Validate_IndividualFIA", notEmpty.and(hasTransactionField("ValueDate")).and(hasFIAMonetaryValue)).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("fl_mtpiid1", "DAL", "ID Number has exceeded the discretionary amount limit of R1,000,000 within this transaction", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA")).and(not(isValidSDAAmount))).onOutflow()
          ]
        },
        {
          field: "{{LocalValue}}", 
          rules: []
        }
      ]
    };
    v7 = {
      ruleset: "Money Rules for 511 Entities", 
      scope: "money", 
      validations: [
        {
          field: "CategoryCode", 
          rules: [
            failure("mcc6", "416", "If CategoryCode 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, the third party individual and address details must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname").or(notMoneyField("ThirdParty.StreetAddress.AddressLine1")).or(notMoneyField("ThirdParty.PostalAddress.AddressLine1"))).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "AdHocRequirement.Subject", 
          rules: [
            failure("madhs20", "292", "If Flow is OUT and category is 511/01 to 511/07 and Resident Entity is used and Third Party Individual details are provided then Subject must be SDA", notValue("SDA").and(hasTransactionField("Resident.Entity")).and(hasMoneyField("ThirdParty.Individual"))).onOutflow().onCategory("511").onSection("A")
          ]
        },
        {
          field: "ThirdParty.Individual.Surname", 
          rules: [
            failure("mtpisn1", "416", "If CategoryCode 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, it must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("AB").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.Individual.IDNumber", 
          rules: [
            failure("mtpiid2", "425", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("AB").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.StreetAddress.AddressLine1", 
          rules: [
            failure("fe_mtpsal11", "456", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.PostalAddress.AddressLine1", 
          rules: [
            failure("fe_mtppal11", "512", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        }
      ]
    };
    v8 = {
      ruleset: "Transaction Rules for Flow form", 
      scope: "transaction", 
      validations: [
        {
          field: "OriginatingBank", 
          rules: [
            ignore("obank1"),
            ignore("obank3"),
            ignore("obank4"),
            ignore("obank5"),
            ignore("obank6"),
            ignore("obank7"),
            ignore("obank8"),
            ignore("obank9")
          ]
        },
        {
          field: "ReceivingBank", 
          rules: [
            ignore("rbank1"),
            ignore("rbank3"),
            ignore("rbank4"),
            ignore("rbank5"),
            ignore("rbank6"),
            ignore("rbank7"),
            ignore("rbank8"),
            ignore("rbank9")
          ]
        },
        {
          field: "CorrespondentBank", 
          rules: [
            ignore("cbank1")
          ]
        },
        {
          field: "ReceivingCountry", 
          rules: [
            ignore("rcntry4")
          ]
        },
        {
          field: ["NonResident.Individual.AccountIdentifier", "NonResident.Entity.AccountIdentifier"], 
          rules: [
            ignore("nriaid1"),
            ignore("nriaid4"),
            ignore("nriaid5"),
            ignore("nriaid6"),
            ignore("nriaid7"),
            ignore("nriaid9"),
            ignore("nriaid10"),
            ignore("nriaid11")
          ]
        }
      ]
    };
    v9 = {
      ruleset: "Money Rules for Flow Form", 
      scope: "money", 
      validations: [
        {
          field: "CategoryCode", 
          rules: [
            failure("flw_CCnr6", "253", "If category 250 or 251 is used, Non Resident Entity, or NonResident Exception element may not be completed", notTransactionField("NonResident.Individual")).onSection("A").onCategory(["250", "251"])
          ]
        },
        {
          field: "LoanInterestRate", 
          rules: [
            ignore("mlir1"),
            ignore("mlir3"),
            ignore("mlir4")
          ]
        },
        {
          field: "LoanTenor", 
          rules: [
            failure("flw_mlt1", "I??", "Loan Tenor required due to category selected", notEmpty).onOutflow().onSection("AB").notOnCategory(["810", "815", "816", "817", "818", "819"])
          ]
        },
        {
          field: "LoanInterest.BaseRate", 
          rules: [
            failure("flw_mlibr1", "I??", "Base rate required due to category selected", notEmpty).onOutflow().onSection("AB").notOnCategory(["810", "815", "816", "817", "818", "819"])
          ]
        },
        {
          field: "LoanInterest.Term", 
          rules: [
            failure("flw_mlit1", "I??", "Term required as Base Rate is not Prime Rate", isEmpty.and(hasMoneyField("LoanInterest.BaseRate").and(notMoneyFieldValue("LoanInterest.BaseRate", ["FIXED", "PRIME"])))),
            failure("flw_mlit2", "I??", "Unless the Interest Base Rate is used (and this rate needs a term) this must not be provided", notEmpty.and(notMoneyField("LoanInterest.BaseRate").or(hasMoneyFieldValue("LoanInterest.BaseRate", ["FIXED", "PRIME"]))))
          ]
        },
        {
          field: "LoanInterest.PlusMinus", 
          rules: [
            failure("flw_mlipm1", "I??", "Required field for loans", isEmpty.and(hasMoneyField("LoanInterest.BaseRate")).and(notMoneyFieldValue("LoanInterest.BaseRate", ["FIXED", "PRIME"]))),
            failure("flw_mlipm12", "I??", "Unless the Interest Base Rate is used this must not be provided", notEmpty.and(notMoneyField("LoanInterest.BaseRate").or(hasMoneyFieldValue("LoanInterest.BaseRate", ["FIXED", "PRIME"]))))
          ]
        },
        {
          field: "LoanInterest.Rate", 
          rules: [
            failure("flw_mlir1", "378", "Rate required due to category selected", notPattern("^\\d+(\\.\\d{2})$")).onOutflow().onSection("AB").onCategory(["810", "815", "816", "817", "818", "819"]),
            failure("flw_mlir3", "380", "Rate required due to category selected", notPattern("^\\d{1,3}\\.\\d{2}?$")).onOutflow().onSection("ABG").onCategory(["309/04", "309/05", "309/06", "309/07"]),
            failure("flw_mlir4", "380", "If the Flow is In and CategoryCode 309/01 to 309/07 is used, must be completed reflecting the percentage interest paid e.g. 7.20", notPattern("^\\d{1,3}\\.\\d{2}?$")).onInflow().onSection("ABG").onCategory(["309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"]),
            warning("flw_mlir5", "S12", "Rate should not be greater than 100%", hasPattern("^\\d{3}\\.\\d{2}?$")).onOutflow().onSection("ABG").onCategory(["309/04", "309/05", "309/06", "309/07"]),
            warning("flw_mlir6", "S12", "It is unlikely that an interest rate greater than 100% is being charged", hasPattern("^\\d{3}\\.\\d{2}?$")).onInflow().onSection("ABG").onCategory(["309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"]),
            failure("flw_mlir7", "I??", "Unless the category 810, 815, 816, 817, 818, 819, 309/04, 309/05, 309/06 or 309/07 is used is used this must not be provided", notEmpty).onOutflow().onSection("AB").notOnCategory(["810", "815", "816", "817", "818", "819", "309/04", "309/05", "309/06", "309/07"]),
            failure("flw_mlir8", "I??", "Unless the category 309/01, 309/02, 309/03, 309/04, 309/05, 309/06 or 309/07 is used is used this must not be provided", notEmpty).onInflow().onSection("ABG").notOnCategory(["309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"])
          ]
        },
        {
          field: "LocationCountry", 
          rules: []
        },
        {
          field: "{{Regulator}}Auth.RulingsSection", 
          rules: [
            ignore("mars1")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", 
          rules: [
            ignore("maian1")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumberDate", 
          rules: [
            ignore("maiad1")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", 
          rules: [
            ignore("masan1"),
            failure("flw_masan1", "I33", "If the SARB Auth Date is captured then SARB Auth Number must be provided", isEmpty.and(hasMoneyField("{{Regulator}}Auth.SARBAuthDate"))).onSection("ABG")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber", 
          rules: [
            ignore("masar1")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthDate", 
          rules: [
            failure("flw_masad1", "I12", "If the SARB Auth Number is captured then SARB Auth Date must be provided", isEmpty.and(hasMoneyField("{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber"))).onSection("ABG"),
            failure("flw_masad2", "I26", "Must be in a valid date format: YYYY-MM-DD", notEmpty.and(notPattern(/^(19|20)\d{2}-(0\d|10|11|12)-(0[1-9]|1\d|2\d|3[01])$/))).onSection("ABG"),
            warning("flw_masad3", "I27", "This SARB Auth Date is in the future", notEmpty.and(isDaysInFuture("0"))).onSection("ABG")
          ]
        }
      ]
    };
    v10 = {
      ruleset: "Import/Export Rules for Flow Form", 
      scope: "importexport", 
      validations: []
    };
    v11 = {
      ruleset: "Standard Transaction Rules for Standard Bank", 
      scope: "transaction", 
      validations: [
        {
          field: "", 
          rules: [
            ignore("ext_ccn1"),
            ignore("ext_ccn2")
          ]
        }
      ]
    };
    v12 = {
      ruleset: "Standard Money Rules for Standard Bank", 
      scope: "money", 
      validations: [
        {
          field: "", 
          rules: [
            ignore("ext_tpccn1")
          ]
        },
        {
          field: "CategoryCode", 
          rules: [
            failure("mcc7", "416", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN or OUT in cases where the Resident Entity element is completed, the third party individual and address details must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname").or(notMoneyField("ThirdParty.StreetAddress.AddressLine1")).or(notMoneyField("ThirdParty.PostalAddress.AddressLine1")))).onSection("A").onCategory(["511", "516"]),
            failure("mcc10", "425", "If Bop Category 511/01 to 511/07 or 516 is used and flow is IN or OUT in cases where the EntityCustomer element is completed, IndividualThirdPartyIDNumber must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.IDNumber"))).onSection("A").onCategory(["511", "516"])
          ]
        },
        {
          field: "ThirdParty.Entity.Name", 
          rules: [
            failure("mtpenm4", "S416", "This must be completed", isEmpty.and(hasMoneyField("ThirdParty.Entity"))).onSection("AB")
          ]
        },
        {
          field: "ThirdParty.Individual.Surname", 
          rules: [
            failure("mtpisn9", "416", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN or OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("A").onCategory(["511", "516"]),
            failure("mtpisn12", "S416", "This must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual"))).onSection("AB")
          ]
        },
        {
          field: "ThirdParty.TaxNumber", 
          rules: [
            failure("mtptx4", "439", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("AB").onCategory(["511", "516"]),
            failure("mtptx6", "546", "ThirdPartyTaxNumber must be completed for registered agents", isEmpty.and(hasMoneyField("ThirdParty.Individual").or(hasMoneyField("ThirdParty.Entity")))).onOutflow().onSection("AB").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"]),
            failure("mtptx7", "325", "Invalid tax number", notEmpty.and(not(isValidZATaxNumber))).onCategory("512").onSection("AB")
          ]
        },
        {
          field: "ThirdParty.CustomsClientNumber", 
          rules: [
            failure("mtpccn6", "396", "ThirdPartyCustomsClientNumber must be completed for registered agents", isEmpty.and(hasMoneyField("ThirdParty.Individual").or(hasMoneyField("ThirdParty.Entity")))).onOutflow().onSection("AB").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"])
          ]
        },
        {
          field: "ThirdParty.StreetAddress.AddressLine1", 
          rules: [
            failure("mtpsal13", "456", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN or OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("A").onCategory(["511", "516"])
          ]
        },
        {
          field: "ThirdParty.PostalAddress.AddressLine1", 
          rules: [
            failure("mtppal13", "512", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN or OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("A").onCategory(["511", "516"])
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", 
          rules: [
            failure("std_masan1", "AAN", "The SARB Auth Application Number must be completed when Bop Cat is 106", isEmpty).onSection("A").onCategory("106")
          ]
        }
      ]
    };
    v13 = {
      ruleset: "HOLDCO Rules", 
      scope: "transaction", 
      validations: [
        {
          field: "NonResident.Entity.EntityName", 
          rules: [
            failure("hc_nren1", "H01", "The Resident Legal Entity name is not equal to the name of HOLDCO in the HOLDCO list", notEmpty.and(evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))).and(not(hasLookupTransactionFieldValue("holdcoCompanies", "companyName", "accountNumber", "NonResident.Entity.AccountNumber"))).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity")))).onSection("A"),
            failure("hc_nren2", "H01", "The Resident Legal Entity name is not equal to the name of HOLDCO in the HOLDCO list", notEmpty.and(evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))).and(not(hasLookupTransactionFieldValue("holdcoCompanies", "companyName", "accountNumber", "NonResident.Entity.AccountNumber"))).and(hasResException("FCA NON RESIDENT NON REPORTABLE"))).onSection("C")
          ]
        },
        {
          field: "NonResident.Entity.AccountIdentifier", 
          rules: [
            failure("hc_nrea1", "H02", "Non Resident Account Identifier must be NON RESIDENT FCA for reporting of HOLDCO transactions", notValue("NON RESIDENT FCA").and(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity")))).onSection("A"),
            failure("hc_nrea2", "H02", "Non Resident Account Identifier must be NON RESIDENT FCA for reporting of HOLDCO transactions", notValue("NON RESIDENT FCA").and(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))).and(hasResException("FCA NON RESIDENT NON REPORTABLE"))).onSection("C")
          ]
        },
        {
          field: "Resident.Individual.AccountIdentifier", 
          rules: [
            failure("hc_accid1", "H03", "Resident Account Identifier must be FCA RESIDENT for reporting of HOLDCO transactions", notValue("FCA RESIDENT").and(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber")))).onSection("A")
          ]
        },
        {
          field: "Resident.Entity.AccountIdentifier", 
          rules: [
            failure("hc_accid2", "H03", "Resident Account Identifier must be FCA RESIDENT for reporting of HOLDCO transactions", notValue("FCA RESIDENT").and(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber")))).onSection("A")
          ]
        },
        {
          field: "Resident.Exception.Country", 
          rules: [
            failure("hc_rec1", "H04", "Country must be linked to the currency used when reporting HOLDCO transactions (EU must be used for EUR payments)", notMatchToCurrency.and(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))).and(hasResException("FCA NON RESIDENT NON REPORTABLE"))).onSection("C")
          ]
        },
        {
          field: "NonResident.Individual.AccountIdentifier", 
          rules: [
            failure("hc_nraid1", "H05", "Non Resident Account Identifier must be 'NON RESIDENT RAND' for reporting of HOLDCO transactions", notEmpty.and(notValue("NON RESIDENT RAND")).and(hasAnyMoneyFieldValue("AdHocRequirement.Subject", "HOLDCO")).and(isCurrencyIn("ZAR")).and(hasResException("NON RESIDENT RAND")).and(not(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))))).onSection("A")
          ]
        },
        {
          field: "NonResident.Entity.AccountIdentifier", 
          rules: [
            failure("hc_nraid2", "H05", "Non Resident Account Identifier must be 'NON RESIDENT RAND' for reporting of HOLDCO transactions", notEmpty.and(notValue("NON RESIDENT RAND")).and(hasAnyMoneyFieldValue("AdHocRequirement.Subject", "HOLDCO")).and(isCurrencyIn("ZAR")).and(hasResException("NON RESIDENT RAND")).and(not(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))))).onSection("A")
          ]
        }
      ]
    };
    v14 = {
      ruleset: "Money Rules for HOLDCO Accounts", 
      scope: "money", 
      validations: [
        {
          field: "AdHocRequirement.Subject", 
          rules: [
            failure("hc_madhs1", "H06", "Subject must contain HOLDCO when reporting transactions for HOLDCO companies", evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber")).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity"))).and(isEmpty.or(notValue("HOLDCO")))).onSection("A"),
            failure("hc_madhs2", "H07", "Resident Exception name must be 'NON RESIDENT RAND' for reporting of ZAR related HOLDCO transactions", hasValue("HOLDCO").and(notResException("NON RESIDENT RAND")).and(isCurrencyIn("ZAR")).and(not(evalNonResidentField("AccountNumber", isInLookup("holdcoCompanies", "accountNumber"))))).onSection("A")
          ]
        },
        {
          field: "AdHocRequirement.Description", 
          rules: [
            failure("hc_madhd1", "H08", "Description must contain the registration number of the HOLDCO as listed in the HOLDCO table", evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber")).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity"))).and(isEmpty.or(not(hasLookupTransactionFieldValue("holdcoCompanies", "registrationNumber", "accountNumber", "NonResident.Entity.AccountNumber"))))).onSection("A")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", 
          rules: [
            failure("hc_maian1", "H09", "{{DealerPrefix}}Internal Auth Number is mandatory for reporting of a HOLDCO transaction", evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber")).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity"))).and(isEmpty)).onSection("A")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", 
          rules: [
            failure("hc_masan1", "H10", "{{RegulatorPrefix}}AuthAppNumber is mandatory for reporting of a HOLDCO transaction", evalTransactionField("NonResident.Entity.AccountNumber", isInLookup("holdcoCompanies", "accountNumber")).and(hasTransactionField("Resident.Individual").or(hasTransactionField("Resident.Entity"))).and(isEmpty)).onSection("A")
          ]
        }
      ]
    };
    v15 = {
      ruleset: "Money Rules for MTA Accounts", 
      scope: "money", 
      validations: [
        {
          field: "CategoryCode", 
          rules: [
            failure("mtaa_cc1", "CC1", "Category 833 cannot be used for this transaction since the Resident Account Number is not an MTA account", notEmpty.and(not(evalResidentField("AccountNumber", isInLookup("mtaAccounts", "accountNumber"))))).onSection("A").onCategory(["833"]),
            failure("mtaa_cc2", "CC2", "Category 833 (or 800) must be used for this transaction since the Resident Account Number is an MTA (ADLA) account", evalResidentField("AccountNumber", hasLookupValue("mtaAccounts", "accountNumber", "isADLA", true)).and(evalResidentField("AccountNumber", isInLookup("mtaAccounts", "accountNumber")))).onSection("A").notOnCategory(["833", "800"])
          ]
        }
      ]
    };
    v16 = {
      ruleset: "Schema-based Transaction Rules", 
      scope: "transaction", 
      validations: [
        {
          field: ["ReplacementTransaction", "ReplacementOriginalReference", "ReportingQualifier", "Flow", "FlowCurrency", "TrnReference", "OriginatingBank", "OriginatingCountry", "CorrespondentBank", "CorrespondentCountry", "ReceivingBank", "ReceivingCountry", "NonResident.Individual.Surname", "NonResident.Individual.Name", "NonResident.Individual.Gender", "NonResident.Individual.PassportNumber", "NonResident.Individual.PassportCountry", "NonResident.Individual.AccountIdentifier", "NonResident.Individual.AccountNumber", "NonResident.Individual.Address.AddressLine1", "NonResident.Individual.Address.AddressLine2", "NonResident.Individual.Address.Suburb", "NonResident.Individual.Address.City", "NonResident.Individual.Address.State", "NonResident.Individual.Address.PostalCode", "NonResident.Individual.Address.Country", "NonResident.Entity.EntityName", "NonResident.Entity.CardMerchantName", "NonResident.Entity.CardMerchantCode", "NonResident.Entity.AccountIdentifier", "NonResident.Entity.AccountNumber", "NonResident.Entity.Address.AddressLine1", "NonResident.Entity.Address.AddressLine2", "NonResident.Entity.Address.Suburb", "NonResident.Entity.Address.City", "NonResident.Entity.Address.State", "NonResident.Entity.Address.PostalCode", "NonResident.Entity.Address.Country", "NonResident.Exception.ExceptionName", "Resident.Individual.Surname", "Resident.Individual.Name", "Resident.Individual.Gender", "Resident.Individual.TempResPermitNumber", "Resident.Individual.ForeignIDNumber", "Resident.Individual.ForeignIDCountry", "Resident.Individual.PassportNumber", "Resident.Individual.PassportCountry", "Resident.Individual.AccountName", "Resident.Individual.AccountIdentifier", "Resident.Individual.AccountNumber", "Resident.Individual.CustomsClientNumber", "Resident.Individual.TaxNumber", "Resident.Individual.VATNumber", "Resident.Individual.TaxClearanceCertificateIndicator", "Resident.Individual.TaxClearanceCertificateReference", "Resident.Individual.StreetAddress.AddressLine1", "Resident.Individual.StreetAddress.AddressLine2", "Resident.Individual.StreetAddress.Suburb", "Resident.Individual.StreetAddress.City", "Resident.Individual.StreetAddress.State", "Resident.Individual.StreetAddress.PostalCode", "Resident.Individual.PostalAddress.AddressLine1", "Resident.Individual.PostalAddress.AddressLine2", "Resident.Individual.PostalAddress.Suburb", "Resident.Individual.PostalAddress.City", "Resident.Individual.PostalAddress.State", "Resident.Individual.PostalAddress.PostalCode", "Resident.Individual.ContactDetails.ContactSurname", "Resident.Individual.ContactDetails.ContactName", "Resident.Individual.ContactDetails.Email", "Resident.Individual.ContactDetails.Fax", "Resident.Individual.ContactDetails.Telephone", "Resident.Individual.CardNumber", "Resident.Individual.SupplementaryCardIndicator", "Resident.Entity.EntityName", "Resident.Entity.TradingName", "Resident.Entity.RegistrationNumber", "Resident.Entity.AccountName", "Resident.Entity.AccountIdentifier", "Resident.Entity.AccountNumber", "Resident.Entity.CustomsClientNumber", "Resident.Entity.TaxNumber", "Resident.Entity.VATNumber", "Resident.Entity.TaxClearanceCertificateIndicator", "Resident.Entity.TaxClearanceCertificateReference", "Resident.Entity.StreetAddress.AddressLine1", "Resident.Entity.StreetAddress.AddressLine2", "Resident.Entity.StreetAddress.Suburb", "Resident.Entity.StreetAddress.City", "Resident.Entity.StreetAddress.State", "Resident.Entity.StreetAddress.PostalCode", "Resident.Entity.PostalAddress.AddressLine1", "Resident.Entity.PostalAddress.AddressLine2", "Resident.Entity.PostalAddress.Suburb", "Resident.Entity.PostalAddress.City", "Resident.Entity.PostalAddress.State", "Resident.Entity.PostalAddress.PostalCode", "Resident.Entity.ContactDetails.ContactSurname", "Resident.Entity.ContactDetails.ContactName", "Resident.Entity.ContactDetails.Email", "Resident.Entity.ContactDetails.Fax", "Resident.Entity.ContactDetails.Telephone", "Resident.Entity.CardNumber", "Resident.Entity.SupplementaryCardIndicator", "Resident.Exception.ExceptionName", "Resident.Exception.Country"], 
          rules: [
            failure("sch_str", "SCS", "This value must be a string", notSimpleValue)
          ]
        },
        {
          field: ["ValueDate", "Resident.Individual.DateOfBirth"], 
          rules: [
            failure("sch_date", "SCD", "This value must be a valid date", notEmpty.and(notDatePattern))
          ]
        },
        {
          field: ["TotalForeignValue", "Resident.Entity.InstitutionalSector", "Resident.Entity.IndustrialClassification"], 
          rules: [
            failure("sch_num", "SCN", "This value must be a number", notEmpty.and(notValidNumber))
          ]
        },
        {
          field: ["NonResident", "NonResident.Individual", "NonResident.Individual.Address", "NonResident.Entity", "NonResident.Entity.Address", "NonResident.Exception", "Resident", "Resident.Individual", "Resident.Individual.StreetAddress", "Resident.Individual.PostalAddress", "Resident.Individual.ContactDetails", "Resident.Entity", "Resident.Entity.StreetAddress", "Resident.Entity.PostalAddress", "Resident.Entity.ContactDetails", "Resident.Exception"], 
          rules: [
            failure("sch_cmp", "SCC", "This value must be an object", notComplexValue)
          ]
        },
        {
          field: "MonetaryAmount", 
          rules: [
            failure("sch_arr", "SCA", "This value must be an array", notArrayValue)
          ]
        }
      ]
    };
    v17 = {
      ruleset: "Schema-based Money Rules", 
      scope: "money", 
      validations: [
        {
          field: ["MoneyTransferAgentIndicator", "SWIFTDetails", "StrateRefNumber", "LoanRefNumber", "LoanTenor", "LoanInterestRate", "{{Regulator}}Auth.RulingsSection", "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", "CannotCategorize", "AdHocRequirement.Subject", "AdHocRequirement.Description", "LocationCountry", "ReversalTrnRefNumber", "BOPDIRTrnReference", "BOPDIR{{DealerPrefix}}Code", "ThirdParty.Individual.Surname", "ThirdParty.Individual.Name", "ThirdParty.Individual.Gender", "ThirdParty.Individual.TempResPermitNumber", "ThirdParty.Individual.PassportNumber", "ThirdParty.Individual.PassportCountry", "ThirdParty.Entity.Name", "ThirdParty.Entity.RegistrationNumber", "ThirdParty.CustomsClientNumber", "ThirdParty.TaxNumber", "ThirdParty.VATNumber", "ThirdParty.StreetAddress.AddressLine1", "ThirdParty.StreetAddress.AddressLine2", "ThirdParty.StreetAddress.Suburb", "ThirdParty.StreetAddress.City", "ThirdParty.StreetAddress.State", "ThirdParty.StreetAddress.PostalCode", "ThirdParty.PostalAddress.AddressLine1", "ThirdParty.PostalAddress.AddressLine2", "ThirdParty.PostalAddress.Suburb", "ThirdParty.PostalAddress.City", "ThirdParty.PostalAddress.State", "ThirdParty.PostalAddress.PostalCode", "ThirdParty.ContactDetails.ContactSurname", "ThirdParty.ContactDetails.ContactName", "ThirdParty.ContactDetails.Email", "ThirdParty.ContactDetails.Fax", "ThirdParty.ContactDetails.Telephone", "CardChargeBack", "CardIndicator", "ElectronicCommerceIndicator", "POSEntryMode", "CardFraudulentTransactionIndicator"], 
          rules: [
            failure("sch_mstr", "SCS", "This value must be a string", notSimpleValue)
          ]
        },
        {
          field: ["{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumberDate", "ThirdParty.Individual.DateOfBirth"], 
          rules: [
            failure("sch_mdate", "SCD", "This value must be a valid date", notEmpty.and(notDatePattern))
          ]
        },
        {
          field: ["SequenceNumber", "{{LocalValue}}", "ForeignValue", "ReversalTrnSeqNumber", "ForeignCardHoldersPurchases{{LocalValue}}", "ForeignCardHoldersCashWithdrawals{{LocalValue}}"], 
          rules: [
            failure("sch_mnum", "SCN", "This value must be a number", notEmpty.and(notValidNumber))
          ]
        },
        {
          field: ["{{Regulator}}Auth", "AdHocRequirement", "ThirdParty", "ThirdParty.Individual", "ThirdParty.Entity", "ThirdParty.StreetAddress", "ThirdParty.PostalAddress", "ThirdParty.ContactDetails"], 
          rules: [
            failure("sch_mcmp", "SCC", "This value must be an object", notComplexValue)
          ]
        },
        {
          field: "ImportExport", 
          rules: [
            failure("sch_marr", "SCA", "This value must be an array", notArrayValue)
          ]
        }
      ]
    };
    v18 = {
      ruleset: "Standard Import/Export Rules", 
      scope: "importexport", 
      validations: [
        {
          field: ["ImportControlNumber", "TransportDocumentNumber", "UCR", "PaymentCurrencyCode", "MRNNotOnIVS"], 
          rules: [
            failure("sch_iestr", "SCS", "This value must be a string", notSimpleValue)
          ]
        },
        {
          field: ["PaymentValue"], 
          rules: [
            failure("sch_ienum", "SCN", "This value must be a number", notEmpty.and(notValidNumber))
          ]
        }
      ]
    };
    v19 = {
      ruleset: "Money Rules for 511 Entities", 
      scope: "money", 
      validations: [
        {
          field: "CategoryCode", 
          rules: [
            failure("mcc6", "416", "If CategoryCode 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, the third party individual and address details must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname").or(notMoneyField("ThirdParty.StreetAddress.AddressLine1")).or(notMoneyField("ThirdParty.PostalAddress.AddressLine1"))).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "AdHocRequirement.Subject", 
          rules: [
            failure("madhs20", "292", "If Flow is OUT and category is 511/01 to 511/07 and Resident Entity is used and Third Party Individual details are provided then Subject must be SDA", notValue("SDA").and(hasTransactionField("Resident.Entity")).and(hasMoneyField("ThirdParty.Individual"))).onOutflow().onCategory("511").onSection("A")
          ]
        },
        {
          field: "ThirdParty.Individual.Surname", 
          rules: [
            failure("mtpisn1", "416", "If CategoryCode 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, it must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("AB").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.Individual.IDNumber", 
          rules: [
            failure("mtpiid2", "425", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("AB").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.StreetAddress.AddressLine1", 
          rules: [
            failure("fe_mtpsal11", "456", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        },
        {
          field: "ThirdParty.PostalAddress.AddressLine1", 
          rules: [
            failure("fe_mtppal11", "512", "If category 511/01 to 511/07 or 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onSection("A").onCategory(["511", "512", "513"])
          ]
        }
      ]
    };
    v20 = {
      ruleset: "Branch and Hub Rules", 
      scope: "transaction", 
      validations: [
        {
          field: "BranchCode", 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("bh_brc1", "222", "Must be completed", isEmpty).onSection("ABCDG"),
            failure("bh_brc2", "223", "Must not be completed", notEmpty).onSection("EF"),
            failure("bh_brc3", "224", "Invalid branch code as per the BranchCode table", notEmpty.and(not(isValidBranchCode))).onSection("ABCDG")
          ]
        },
        {
          field: "BranchName", 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("bh_brn1", "225", "Must be completed", isEmpty).onSection("ABCDG"),
            failure("bh_brn2", "226", "Must not be completed", notEmpty).onSection("EF"),
            failure("bh_brn3", "283", "BranchName does not correspond to the provided BranchCode", notEmpty.and(not(isValidBranchName))).onSection("ABCDG")
          ]
        },
        {
          field: "HubCode", 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("bh_hubc1", "223", "Must not be completed", notEmpty).onSection("EF"),
            failure("bh_hubc2", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces)),
            failure("bh_hubc3", "224", "Invalid hub code as per the HubCode table", notEmpty.and(not(isValidHubCode))).onSection("ABCDG")
          ]
        },
        {
          field: "HubName", 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("bh_hubn1", "226", "Must not be completed", notEmpty).onSection("EF"),
            failure("bh_hubn2", "225", "Must be completed if HubCode contains a value", isEmpty.and(hasTransactionField("HubCode"))).onSection("ABCDG"),
            failure("bh_hubn3", "283", "HubName does not correspond to the provided HubCode", notEmpty.and(not(isValidHubName))).onSection("ABCDG")
          ]
        }
      ]
    };
    v21 = {
      ruleset: "External Transaction Rules", 
      scope: "transaction", 
      validations: [
        {
          field: ["Resident.Individual.CustomsClientNumber", "Resident.Entity.CustomsClientNumber"], 
          rules: [
            validate("ext_ccn1", "Validate_ImportUndertakingCCN", notEmpty.and(hasPattern(/^\d{8}$/))),
            validate("ext_ccn2", "Validate_ValidCCN", notEmpty.and(hasPattern(/^\d{8}$/))).onOutflow().onSection("AB").notOnCategory(["102/11", "104/11"]).onCategory(["102", "104", "106"])
          ]
        },
        {
          field: "ReplacementOriginalReference", 
          minLen: "1", 
          maxLen: "30", 
          rules: [
            validate("ext_repot1", "Validate_ReplacementTrnReference", notEmpty.and(hasTransactionFieldValue("ReplacementTransaction", "Y"))).onSection("ABCDEFG")
          ]
        }
      ]
    };
    v22 = {
      ruleset: "External Money Rules", 
      scope: "money", 
      validations: [
        {
          field: "ReversalTrnRefNumber", 
          rules: [
            validate("ext_mrtrn1", "Validate_ReversalTrnRef", notEmpty.and(hasMoneyField("ReversalTrnSeqNumber"))).onCategory(["100", "200", "300", "400", "500", "600", "700", "800"]).onSection("ABG")
          ]
        },
        {
          field: "ReversalTrnSeqNumber", 
          rules: [
            validate("ext_mrtrn1", "Validate_ReversalTrnRef", notEmpty.and(hasMoneyField("ReversalTrnRefNumber"))).onCategory(["100", "200", "300", "400", "500", "600", "700", "800"]).onSection("ABG")
          ]
        },
        {
          field: "LoanRefNumber", 
          rules: [
            validate("ext_mlrn1", "Validate_LoanRef", notEmpty).onSection("ABG")
          ]
        },
        {
          field: "ThirdParty.CustomsClientNumber", 
          rules: [
            validate("ext_tpccn1", "Validate_ValidCCN", notEmpty.and(hasPattern(/^\d{8}$/))).onSection("AB")
          ]
        }
      ]
    };
    v23 = {
      ruleset: "External ImportExport Rules", 
      scope: "importexport", 
      validations: [
        {
          field: "UCR", 
          rules: [
            validate("ext_ieucr1", "Validate_ValidCCNinUCR", notEmpty.and(hasPattern(/^[0-9]ZA[0-9]{8}.+$/))).onInflow().onSection("ABG")
          ]
        }
      ]
    };
    v24 = {
      ruleset: "Specific SARB Transaction rules", 
      scope: "transaction", 
      validations: [
        {
          field: ["Resident.Individual.StreetAddress.Province", "Resident.Entity.StreetAddress.Province", "Resident.Individual.PostalAddress.Province", "Resident.Entity.PostalAddress.Province"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("p1", "336", "Must be valid South African province", notValidProvince).onSection("BG"),
            failure("p2", "527", "May not be completed", notEmpty).onSection("DF"),
            ignore("p3"),
            failure("p4", "336", "Must be valid province (including NAMIBIA, LESOTHO or SWAZILAND)", notValidProvince.and(notValueIn(["NAMIBIA", "LESOTHO", "SWAZILAND"]))).onSection("E"),
            failure("p5", "287", "Must be set to NAMIBIA because ForeignIDCountry is NA", hasTransactionFieldValue("Resident.Individual.ForeignIDCountry", "NA").and(notValueIn("NAMIBIA"))).onSection("E"),
            failure("p6", "287", "Must be set to LESOTHO because ForeignIDCountry is LS", hasTransactionFieldValue("Resident.Individual.ForeignIDCountry", "LS").and(notValueIn("LESOTHO"))).onSection("E"),
            failure("p7", "287", "Must be set to SWAZILAND because ForeignIDCountry is SZ", hasTransactionFieldValue("Resident.Individual.ForeignIDCountry", "SZ").and(notValueIn("SWAZILAND"))).onSection("E"),
            failure("p8", "336", "Must be valid South African province", notValidProvince).onInflow().onSection("A"),
            failure("p9", "336", "Must be valid South African province", notEmpty.and(notValidProvince)).onOutflow().onSection("A"),
            failure("p10", "336", "If the flow is OUT and the Subject is REMITTANCE DISPENSATION, this must be valid South African province", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("p11", "336", "If the Flow is IN and the category is 400 and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION, this must be valid South African province", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onCategory("400").onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.ContactDetails.Email", "Resident.Entity.ContactDetails.Email"], 
          rules: [
            warning("cnte3", "E01", "This is not a valid email address", notEmpty.and(notValidEmail))
          ]
        }
      ]
    };
    v25 = {
      ruleset: "Standard SARB Money Rules", 
      scope: "money", 
      validations: [
        {
          field: ["Resident.Individual.ContactDetails.Email", "Resident.Entity.ContactDetails.Email"], 
          rules: [
            warning("cnte4", "E01", "This is not a valid email address", notEmpty.and(notValidEmail))
          ]
        }
      ]
    };
    v26 = {
      ruleset: "Standard Transaction Rules", 
      scope: "transaction", 
      validations: [
        {
          field: "ReplacementTransaction", 
          rules: [
            failure("repyn1", "543", "ReplacementTransaction must be completed", isEmpty.and(hasTransactionField("ReplacementOriginalReference"))),
            failure("repyn2", "209", "ReplacementTransaction must contain a value Y or N", notEmpty.and(notValueIn(["Y", "N"])))
          ]
        },
        {
          field: "ReplacementOriginalReference", 
          rules: [
            failure("repor1", "210", "If ReplacementTransaction is Y, must be completed", isEmpty.and(hasTransactionFieldValue("ReplacementTransaction", "Y"))),
            failure("repor2", "211", "Must not be completed if ReplacementTransactionIndicator is N", notEmpty.and(hasTransactionFieldValue("ReplacementTransaction", "N"))),
            failure("repor3", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces))
          ]
        },
        {
          field: "ReportingQualifier", 
          minLen: "6", 
          maxLen: "25", 
          rules: [
            failure("repq1", "541", "Must be completed", isEmpty),
            failure("repq2", "207", "Must contain a valid value", notEmpty.and(notReportingQualifier))
          ]
        },
        {
          field: "Flow", 
          rules: [
            failure("flow1", "542", "Must be completed", isEmpty),
            failure("flow2", "208", "Must contain the value IN or OUT", notEmpty.and(notValueIn(["IN", "OUT"]))).onSection("ABCDEG"),
            failure("flow3", "208", "Must only contain the value IN", notEmpty.and(notValue("IN"))).onSection("F"),
            failure("flow4", "F01", "The flow direction of the report data must match the flow on the account entry. Please ask IT support to investigate", notEmpty.and(notAccountFLow)).onSection("ABDEFG"),
            failure("flow5", "F01", "The flow direction of the report data must match the flow on the account entry unless this is a VOSTRO related transaction. Please ask IT support to investigate", notEmpty.and(notAccountFLow.and(notResException("VOSTRO NON REPORTABLE").and(notNonResException("VOSTRO NON REPORTABLE"))))).onSection("C")
          ]
        },
        {
          field: "ValueDate", 
          rules: [
            failure("vd1", "544", "Must be completed", isEmpty),
            failure("vd2", "216", "May not exceed today's date plus 10 days", notEmpty.and(isDaysInFuture("10"))),
            warning("vd3", "217", "Old transaction if the ValueDate is today's date less 3 days", notEmpty.and(isDaysInPast("4"))),
            failure("vd4", "218", "ValueDate must be equal to or after the 2013-08-19", notEmpty.and(isBeforeGoLive)),
            failure("vd5", "214", "Date format incorrect (Date format is CCYY-MM-DD)", notEmpty.and(notDatePattern))
          ]
        },
        {
          field: "FlowCurrency", 
          rules: [
            failure("fcurr1", "S01", "Must be completed", isEmpty),
            failure("fcurr2", "360", "Invalid SWIFT currency code", notEmpty.and(hasInvalidSWIFTCurrency))
          ]
        },
        {
          field: "TotalForeignValue", 
          rules: [
            failure("tfv1", "244", "If the FlowCurrency is {{LocalCurrency}} then the sum of the {{LocalCurrencyName}} Monetary Amounts must add up to the TotalForeignValue", notEmpty.and(isCurrencyIn(map("LocalCurrency")).and(notSumLocalValue).and(notSumForeignValue))).onSection("ABCDEG"),
            failure("tfv2", "244", "If the FlowCurrency is not {{LocalCurrency}} then the sum of the Foreign Monetary Amounts must add up to the TotalForeignValue", notEmpty.and(notCurrencyIn(map("LocalCurrency")).and(notSumForeignValue))).onSection("ABCDEG"),
            failure("tfv3", "551", "ForeignCardHoldersPurchases{{LocalValue}} + ForeignCardHoldersCashWithdrawals{{LocalValue}} must equal TotalForeignValue", notEmpty.and(notSumCardValue)).onSection("F"),
            failure("tfv4", "245", "The TotalForeignValue must be completed and must be greater than 0.00", isEmpty.or(not(isGreaterThan("0.00")))).onSection("ABCDEG")
          ]
        },
        {
          field: "TrnReference", 
          minLen: "1", 
          maxLen: "30", 
          rules: [
            failure("tref1", "545", "Must be completed", isEmpty),
            failure("tref2", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces))
          ]
        },
        {
          field: "OriginatingBank", 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("obank1", "228", "If Flow is OUT, must be completed", isEmpty).onOutflow().onSection("ABCDG"),
            failure("obank2", "229", "May not be completed", notEmpty).onSection("EF"),
            failure("obank3", "230", "If Flow is IN, must be completed except if the Non-Resident AccountIdentifier is CASH", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", "CASH"))).onInflow().onSection("CDG"),
            failure("obank4", "229", "If category 250 or 251, is used and the Flow is IN and the Resident ExceptionName is MUTUAL PARTY, the OriginatingBank may not be completed", notEmpty.and(hasResException("MUTUAL PARTY"))).onInflow().onSection("AB").onCategory(["250", "251"]),
            failure("obank5", "229", "If category 252, 255 or 256 is used and the Flow is IN and the NonResident ExceptionName is MUTUAL PARTY, the OriginatingBank may not be completed", notEmpty.and(hasNonResException("MUTUAL PARTY"))).onInflow().onSection("AB").onCategory(["252", "255", "256"]),
            failure("obank6", "230", "If OriginatingCountry is completed, must be completed", isEmpty.and(hasTransactionField("OriginatingCountry"))).onSection("ABCDG"),
            failure("obank7", "230", "If Flow is IN, must be completed except if the Non-Resident AccountIdentifier is CASH", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", "CASH"))).onInflow().onSection("AB").notOnCategory(["250", "251", "252", "255", "256"]),
            failure("obank8", "230", "If Flow is IN, must be completed except if the Non-Resident AccountIdentifier is CASH or Resident ExceptionName is MUTUAL PARTY", isEmpty.and(notResException("MUTUAL PARTY").and(notNonResidentFieldValue("AccountIdentifier", "CASH")))).onInflow().onSection("AB").onCategory(["250", "251"]),
            failure("obank9", "230", "If Flow is IN, must be completed except if the Non-Resident AccountIdentifier is CASH or NonResident ExceptionName is MUTUAL PARTY", isEmpty.and(notNonResException("MUTUAL PARTY").and(notNonResidentFieldValue("AccountIdentifier", "CASH")))).onInflow().onSection("AB").onCategory(["252", "255", "256"])
          ]
        },
        {
          field: "OriginatingCountry", 
          rules: [
            failure("ocntry1", "231", "If OriginatingBank is completed, must be completed", isEmpty.and(hasTransactionField("OriginatingBank"))).onSection("ABCDG"),
            failure("ocntry2", "229", "May not be completed", notEmpty).onSection("EF"),
            failure("ocntry3", "233", "If Flow is OUT, SWIFT country code must be {{Locale}}", notEmpty.and(notValue(map("Locale")))).onOutflow().onSection("ABG"),
            failure("ocntry4", "234", "If Flow is IN, SWIFT country code may not be {{Locale}}", notEmpty.and(hasValue(map("Locale")))).onInflow().onSection("ABG"),
            failure("ocntry5", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABCDG"),
            failure("ocntry6", "229", "If category 250 or 251, is used and the Flow is IN and the Resident ExceptionName is MUTUAL PARTY, the OriginatingCountry may not be completed", notEmpty.and(hasResException("MUTUAL PARTY"))).onInflow().onSection("AB").onCategory(["250", "251"]),
            failure("ocntry7", "229", "If category 252, 255 or 256 is used and the Flow is IN and the NonResident ExceptionName is MUTUAL PARTY, the OriginatingCountry may not be completed", notEmpty.and(hasNonResException("MUTUAL PARTY"))).onInflow().onSection("AB").onCategory(["252", "255", "256"])
          ]
        },
        {
          field: "CorrespondentBank", 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("cbank1", "235", "If CorrespondentCountry is completed, must be completed", isEmpty.and(hasTransactionField("CorrespondentCountry"))).onSection("ABCDG"),
            failure("cbank2", "236", "May not be completed", notEmpty).onSection("EF")
          ]
        },
        {
          field: "CorrespondentCountry", 
          rules: [
            failure("ccntry1", "237", "If CorrespondentBank is completed, must be completed", isEmpty.and(hasTransactionField("CorrespondentBank"))).onSection("ABCDG"),
            failure("ccntry2", "236", "May not be completed", notEmpty).onSection("EF"),
            failure("ccntry3", "233", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABCDG")
          ]
        },
        {
          field: "ReceivingBank", 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("rbank1", "239", "If Flow is IN, must be completed", isEmpty).onInflow().onSection("ABCDG"),
            failure("rbank2", "240", "Must not be completed", notEmpty).onSection("EF"),
            failure("rbank3", "241", "If ReceivingCountry is completed, must be completed", isEmpty.and(hasTransactionField("ReceivingCountry"))).onSection("ABCDG"),
            failure("rbank4", "241", "If Flow is OUT, must be completed except if the Non-Resident AccountIdentifier is CASH, VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["CASH", "VISA NET", "MASTER SEND"]))).onOutflow().onSection("CDG"),
            failure("rbank5", "240", "If category 200, 250 or 251 is used and the Flow is OUT and the Resident ExceptionName is MUTUAL PARTY and the Non Resident AccountIdentifier is CASH, the ReceivingBank may not be completed", notEmpty.and(hasResException("MUTUAL PARTY")).and(hasNonResidentFieldValue("AccountIdentifier", "CASH"))).onOutflow().onSection("AB").onCategory(["200", "250", "251"]),
            failure("rbank6", "240", "If category 255, 256 or 530/05 is used and the Flow is OUT and the NonResident ExceptionName is MUTUAL PARTY, the ReceivingBank may not be completed", notEmpty.and(hasNonResException("MUTUAL PARTY"))).onOutflow().onSection("AB").onCategory(["200", "255", "256", "530/05"]),
            failure("rbank7", "241", "If Flow is OUT, must be completed except if the Non-Resident AccountIdentifier is CASH, VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["CASH", "VISA NET", "MASTER SEND"]))).onOutflow().onSection("AB").notOnCategory(["200", "250", "251", "255", "256", "530/05"]),
            failure("rbank8", "241", "If Flow is OUT, must be completed except if the Non-Resident AccountIdentifier is CASH, VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["CASH", "VISA NET", "MASTER SEND"])).and(notResException("MUTUAL PARTY"))).onOutflow().onSection("AB").onCategory(["200", "250", "251"]),
            failure("rbank9", "241", "If Flow is OUT, must be completed except if the Non-Resident AccountIdentifier is CASH, VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["CASH", "VISA NET", "MASTER SEND"])).and(notNonResException("MUTUAL PARTY"))).onOutflow().onSection("AB").onCategory(["255", "256", "530/05"])
          ]
        },
        {
          field: "ReceivingCountry", 
          rules: [
            failure("rcntry1", "242", "If ReceivingBank is completed must be completed", isEmpty.and(hasTransactionField("ReceivingBank"))).onSection("ABCDG"),
            failure("rcntry2", "240", "Must not be completed", notEmpty).onSection("EF"),
            failure("rcntry3", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABCDG"),
            failure("rcntry4", "243", "If Flow is IN, SWIFT country code must be {{Locale}}", notValue(map("Locale"))).onInflow().onSection("ABG"),
            failure("rcntry5", "331", "If Flow is OUT, SWIFT country code may not be {{Locale}}", notEmpty.and(hasValue(map("Locale")))).onOutflow().onSection("ABG"),
            failure("rcntry6", "240", "If category 200, 250 or 251 is used and the Flow is OUT and the Resident ExceptionName is MUTUAL PARTY, the ReceivingCountry may not be completed", notEmpty.and(hasResException("MUTUAL PARTY"))).onOutflow().onSection("AB").onCategory(["200", "250", "251"]),
            failure("rcntry7", "240", "If category 255, 256 or 530/05 is used and the Flow is OUT and the NonResident ExceptionName is MUTUAL PARTY, the ReceivingCountry may not be completed", notEmpty.and(hasNonResException("MUTUAL PARTY"))).onOutflow().onSection("AB").onCategory(["255", "256", "530/05"])
          ]
        },
        {
          field: "NonResident", 
          rules: [
            failure("nr1", "246", "Must contain one of Individual, Entity, or Exception elements", isMissingField(["Individual", "Entity", "Exception"])).onSection("ACDG"),
            failure("nr2", "247", "Must contain only one of Individual or Entity elements", isMissingField(["Individual", "Entity"])).onSection("BE"),
            failure("nr3", "248", "If the Reporting Entity Code is 304 or 305 only Non Resident Entity element may be completed", isMissingField("Entity")).onSection("G").onCategory(["304", "305"]),
            failure("nr4", "248", "Non Resident Entity element must be completed unless the Non Resident Account Identifier is VISA NET or MASTER SEND", isMissingField("Entity").and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E"),
            failure("nr5", "250", "NonResident and AdditionalNonResidentData elements must not be completed", notEmpty).onSection("F"),
            failure("nr6", "253", "If category 250 or 251 is used, Non Resident Entity, or NonResident Exception element may not be completed", isMissingField("Individual")).onSection("A").onCategory(["250", "251"])
          ]
        },
        {
          field: "NonResident.Individual.Surname", 
          minLen: "1", 
          maxLen: "35", 
          rules: [
            failure("nrsn1", "254", "If NonResident Individual is completed, must be completed", isEmpty).onSection("ABCDEG"),
            failure("nrsn2", "255", "The words specified under Non-resident or Resident ExceptionName, excluding STRATE, must not be used.", notEmpty.and(notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName))).onSection("ABCDG"),
            failure("nrsn3", "254", "If the Flow is OUT and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then the Surname must have a value", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("nrsn4", "254", "If the Flow is IN and the category is 400 and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then the Surname must have a value", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onCategory("400").onSection("A")
          ]
        },
        {
          field: "NonResident.Individual.Name", 
          minLen: "1", 
          maxLen: "50", 
          rules: [
            failure("nrnm1", "256", "If Flow is OUT, and NonResident Individual is completed, must be completed", isEmpty).onOutflow().onSection("ABCDG"),
            failure("nrnm2", "256", "If the Flow is OUT and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then the Name must have a value", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("nrnm3", "254", "If the Flow is IN and the category is 400 and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then the Name must have a value", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onCategory("400").onSection("A"),
            failure("nrnm4", "255", "The words specified under Non-resident or Resident ExceptionName, excluding STRATE, must not be used.", notEmpty.and(notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName))).onSection("ABCDG")
          ]
        },
        {
          field: "NonResident.Individual.Gender", 
          rules: [
            failure("nrgn1", "295", "Invalid gender value", notEmpty.and(notValueIn(["M", "F"]))).onOutflow().onSection("ABCDG")
          ]
        },
        {
          field: "NonResident.Individual.PassportNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("nrpn1", "258", "If category 250 or 251 is completed, must be completed", isEmpty).onSection("AB").onCategory(["250", "251"]),
            failure("nrpn2", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces)).onSection("AB")
          ]
        },
        {
          field: "NonResident.Individual.PassportCountry", 
          rules: [
            failure("nrpc1", "259", "If category 250 or 251 is completed, must be completed", isEmpty).onSection("AB").onCategory(["250", "251"]),
            failure("nrpc2", "238", "Invalid CountryCode", notEmpty.and(hasInvalidSWIFTCountry)).onSection("AB")
          ]
        },
        {
          field: "NonResident.Entity.EntityName", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("nrlen1", "260", "If NonResident Entity is completed, must be completed", isEmpty).onSection("ABCDG"),
            failure("nrlen2", "559", "Must not be completed (other than for VISA NET or MASTER SEND)", notEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E"),
            failure("nrlen3", "255", "The words specified under Non-resident or Resident ExceptionName, excluding STRATE, must not be used.", notEmpty.and(notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName))).onSection("ABCDG"),
            failure("nrlen4", "261", "If the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then an Entity must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("nrlen5", "559", "This field or CardMerchantName must be completed (Only for VISA NET or MASTER SEND)", isEmpty.and(notTransactionField("NonResident.Entity.CardMerchantName")).and(hasNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E")
          ]
        },
        {
          field: "NonResident.Entity.CardMerchantName", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("nrcmn1", "559", "Must be completed (other than for VISA NET or MASTER SEND)", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E"),
            failure("nrcmn2", "263", "May not be completed", notEmpty).onSection("ABCDG"),
            failure("nrcmn3", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces)).onSection("E"),
            failure("nrcmn4", "559", "This field or EntityName must be completed", isEmpty.and(notTransactionField("NonResident.Entity.EntityName")).and(hasNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E")
          ]
        },
        {
          field: "NonResident.Entity.CardMerchantCode", 
          minLen: "4", 
          maxLen: "6", 
          rules: [
            failure("nrcmc1", "264", "Must be completed", isEmpty).onSection("E"),
            failure("nrcmc2", "265", "Must not be completed", notEmpty).onSection("ABCDG"),
            failure("nrcmc3", "266", "Invalid CardMerchantCode", notEmpty.and(not(hasPattern(/^[0-9]*$/)))).onSection("E")
          ]
        },
        {
          field: "NonResident.Exception", 
          rules: [
            failure("nrex1", "268", "If it has a value, ResidentCustomerAccountHolder Exception must not be completed", notEmpty.and(hasTransactionField("Resident.Exception"))).onSection("A"),
            failure("nrex2", "269", "Must not be completed", notEmpty).onSection("BEFG")
          ]
        },
        {
          field: "NonResident.Exception.ExceptionName", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("nrexn1", "267", "May only contain one of the specified values", notNonResExceptionName).onSection("ACD"),
            failure("nrexn2", "270", "If the ExceptionName is MUTUAL PARTY, the BoPCategory must only be 200, 252, 255, 256 or 530/05", hasValue("MUTUAL PARTY")).onSection("A").notOnCategory(["200", "252", "255", "256", "530/05"]),
            failure("nrexn3", "270", "If BoPCategory is 252 and the Flow is IN, must only contain the value MUTUAL PARTY under NonResident ExceptionName", notEmpty.and(notValue("MUTUAL PARTY"))).onInflow().onSection("A").onCategory("252"),
            failure("nrexn4", "270", "Must only be completed if BoPCategory is 300 and the original transaction was reported with BoPCategory and SubBoPCategory 309/08 with a Non Resident ExceptionName BULK INTEREST", notEmpty.and(hasValue("BULK INTEREST"))).onSection("A").notOnCategory(["300", "309/08"]),
            failure("nrexn5", "270", "ExceptionName of BULK VAT REFUNDS may only be used for category 400 or 411/02", notEmpty.and(hasValue("BULK VAT REFUNDS"))).onSection("A").notOnCategory(["400", "411/02"]),
            failure("nrexn6", "270", "ExceptionName of BULK BANK CHARGES may only be used for category 200 or 275", notEmpty.and(hasValue("BULK BANK CHARGES"))).onSection("A").notOnCategory(["200", "275"]),
            failure("nrexn7", "270", "ExceptionName of BULK PENSIONS may only be used for category 400 or 407", notEmpty.and(hasValue("BULK PENSIONS"))).onSection("A").notOnCategory(["400", "407"]),
            failure("nrexn9", "270", "ExceptionName of STRATE may only be used for category 601/01 or 603/01", notEmpty.and(hasValue("STRATE"))).onSection("A").notOnCategory(["601/01", "603/01"]),
            failure("nrexn10", "269", "May not be used. MUTUAL PARTY is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("MUTUAL PARTY"))).onSection("BCDEFG"),
            failure("nrexn11", "269", "May not be used. BULK INTEREST is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK INTEREST"))).onSection("BCDEFG"),
            failure("nrexn12", "269", "May not be used. BULK VAT REFUNDS is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK VAT REFUNDS"))).onSection("BCDEFG"),
            failure("nrexn13", "269", "May not be used. BULK BANK CHARGES is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK BANK CHARGES"))).onSection("BCDEFG"),
            failure("nrexn14", "269", "May not be used. BULK PENSIONS is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK PENSIONS"))).onSection("BCDEFG"),
            failure("nrexn15", "269", "May not be used. STRATE is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("STRATE"))).onSection("BCDEFG"),
            failure("nrexn16", "269", "May not be used. FCA RESIDENT NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("FCA RESIDENT NON REPORTABLE"))).onSection("ABDEFG"),
            failure("nrexn17", "269", "May not be used. CFC RESIDENT NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("CFC RESIDENT NON REPORTABLE"))).onSection("ABDEFG"),
            failure("nrexn18", "269", "May not be used. VOSTRO NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("VOSTRO NON REPORTABLE"))).onSection("ABDEFG"),
            failure("nrexn19", "269", "May not be used. VOSTRO INTERBANK is only applicable for INTERBANK and NON REPORTABLE transactions", notEmpty.and(hasValue("VOSTRO INTERBANK"))).onSection("ABEFG"),
            failure("nrexn20", "269", "May not be used. NOSTRO INTERBANK is only applicable for INTERBANK and NON REPORTABLE transactions", notEmpty.and(hasValue("NOSTRO INTERBANK"))).onSection("ABEFG"),
            failure("nrexn21", "269", "May not be used. NOSTRO NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("NOSTRO NON REPORTABLE"))).onSection("ABDEFG"),
            failure("nrexn22", "269", "May not be used. RTGS NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("RTGS NON REPORTABLE"))).onSection("ABDEFG"),
            failure("nrexn23", "269", "If the Subject under the MonetaryDetails is REMITTANCE DISPENSATION and category 256 then MUTUAL PARTY may not be used", hasValue("MUTUAL PARTY").and(hasAnyMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A").onCategory("256"),
            failure("nrexn24", "437", "If value is IHQ, the Subject under MonetaryDetails must be IHQnnn", notEmpty.and(hasValue("IHQ").and(evalMoneyField("AdHocRequirement.Subject", notPattern(/^IHQ\d{3}$/))))).onSection("A"),
            failure("nrexn25", "269", "May not be used. 'IHQ' is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("IHQ"))).onSection("BCDEFG"),
            failure("nrexn26", "270", "If the MoneyTransferAgentIndicator is TRAVEL CARD or TRAVELLERS CHEQUE, the category can only be 252, 255, 256 or 530/05", hasValue("MUTUAL PARTY").and(hasAnyMoneyFieldValue("MoneyTransferAgentIndicator", ["TRAVEL CARD", "TRAVELLERS CHEQUE"]))).onSection("A").notOnCategory(["252", "255", "256", "530/05"]),
            failure("nrexn27", "446", "If value is IHQ, the RegistrationNumber under EntityCustomer of the particular IHQnnn must not be equal to the registered IHQ registration number", notEmpty.and(hasValue("IHQ")).and(evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber")))).onSection("A"),
            failure("nrexn28", "447", "If value is IHQ, the Resident LegalEntityName of the particular IHQnnn must not be equal to the registered IHQ name", notEmpty.and(hasValue("IHQ")).and(evalTransactionField("Resident.Entity.EntityName", isInLookup("ihqCompanies", "companyName")))).onSection("A")
          ]
        },
        {
          field: "NonResident.Exception.AccountIdentifier", 
          rules: [
            deprecated("nrexai", "S03", "This fields is not used for finsurv submissions", notEmpty)
          ]
        },
        {
          field: "NonResident.Exception.AccountNumber", 
          rules: [
            deprecated("nrexan", "S04", "This fields is not used for finsurv submissions", notEmpty)
          ]
        },
        {
          field: ["NonResident.Individual.AccountIdentifier", "NonResident.Entity.AccountIdentifier"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("nriaid1", "272", "If the Flow is OUT must contain a value of NON RESIDENT OTHER, NON RESIDENT RAND, NON RESIDENT FCA, CASH, FCA RESIDENT, RES FOREIGN BANK ACCOUNT, VOSTRO, VISA NET or MASTER SEND", notValueIn(["NON RESIDENT OTHER", "NON RESIDENT RAND", "NON RESIDENT FCA", "CASH", "FCA RESIDENT", "RES FOREIGN BANK ACCOUNT", "VOSTRO", "VISA NET", "MASTER SEND"])).onOutflow().onSection("ACDG"),
            failure("nriaid2", "273", "Must contain a value of NON RESIDENT RAND, VISA NET or MASTER SEND", notValueIn(["NON RESIDENT RAND", "VISA NET", "MASTER SEND"])).onSection("B"),
            failure("nriaid3", "272", "Must contain a value VISA NET or MASTER SEND", notEmpty.and(notValueIn(["VISA NET", "MASTER SEND", "CARD DIRECT"]))).onSection("E"),
            failure("nriaid4", "252", "If the AccountIdentifier is NON RESIDENT OTHER or NON RESIDENT RAND or NON RESIDENT FCA or FCA RESIDENT or RES FOREIGN BANK ACCOUNT, NonResident Exception may not be completed", hasValueIn(["NON RESIDENT OTHER", "NON RESIDENT RAND", "NON RESIDENT FCA", "FCA RESIDENT", "RES FOREIGN BANK ACCOUNT"]).and(hasTransactionField("NonResident.Exception"))).onSection("ABEG"),
            failure("nriaid5", "274", "If the AccountIdentifier is RES FOREIGN BANK ACCOUNT and the Flow is IN and the category is 255 or 256 or 810 or 416, the Non Resident Individual element must be completed.", hasValue("RES FOREIGN BANK ACCOUNT").and(notTransactionField("NonResident.Individual"))).onInflow().onCategory(["255", "256", "810", "416"]).onSection("A"),
            failure("nriaid6", "274", "If the AccountIdentifier is RES FOREIGN BANK ACCOUNT and the Flow is OUT and the category is 255 or 256 or 810, the non resident Individual element must be completed.", hasValue("RES FOREIGN BANK ACCOUNT").and(notTransactionField("NonResident.Individual"))).onOutflow().onCategory(["255", "256", "810"]).onSection("A"),
            ignore("nriaid7", "272", "If the Flow is OUT must contain a value of NON RESIDENT OTHER, NON RESIDENT RAND, NON RESIDENT FCA, CASH, FCA RESIDENT, RES FOREIGN BANK ACCOUNT, VOSTRO, VISA NET or MASTER SEND", notValueIn(["NON RESIDENT OTHER", "NON RESIDENT RAND", "NON RESIDENT FCA", "CASH", "FCA RESIDENT", "RES FOREIGN BANK ACCOUNT", "VOSTRO", "VISA NET", "MASTER SEND"])).onOutflow().onSection("ACDG"),
            failure("nriaid9", "276", "If AccountIdentifier is FCA RESIDENT and the Flow is OUT the category 513 must be completed", hasValue("FCA RESIDENT")).onOutflow().onSection("A").notOnCategory("513"),
            failure("nriaid10", "276", "If AccountIdentifier is FCA RESIDENT and the Flow is IN the category 517 must be completed", hasValue("FCA RESIDENT")).onInflow().onSection("A").notOnCategory("517"),
            ignore("nriaid11"),
            warning("nriaid12", "272", "The value CARD DIRECT is being deprecated (VISA NET and MASTER SEND to be implemented by 2016-05-15)", hasValue("CARD DIRECT")).onSection("E"),
            failure("nriaid13", "252", "May not be completed", notEmpty).onSection("F"),
            failure("nriaid14", "274", "If the AccountIdentifier is FCA RESIDENT and the Flow is OUT and the category is 513, Non Resident Individual must be completed", hasValue("FCA RESIDENT").and(hasTransactionField("NonResident.Entity"))).onOutflow().onSection("A").onCategory("513")
          ]
        },
        {
          field: ["NonResident.Individual.AccountNumber", "NonResident.Entity.AccountNumber"], 
          minLen: "2", 
          maxLen: "40", 
          rules: [
            failure("nrian1", "279", "If the Flow is OUT must be completed if the AccountIdentifier is not CASH or NON RESIDENT RAND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["CASH", "NON RESIDENT RAND"]))).onOutflow().onSection("ABCDG"),
            failure("nrian2", "280", "Must not be equal to AccountNumber under Resident element", notEmpty.and(matchesResidentField("AccountNumber"))).onSection("ABCDG"),
            failure("nrian3", "533", "May not contain invalid characters like ' or &", notEmpty.and(hasPattern(/['&]/))).onSection("ABCDG"),
            failure("nrian4", "252", "Must not be completed", notEmpty).onSection("F")
          ]
        },
        {
          field: ["NonResident.Individual.Address.AddressLine1", "NonResident.Entity.Address.AddressLine1"], 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("nrial11", "281", "Must not be completed", notEmpty).onSection("E")
          ]
        },
        {
          field: ["NonResident.Individual.Address.AddressLine2", "NonResident.Entity.Address.AddressLine2"], 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("nrial21", "281", "Must not be completed", notEmpty).onSection("E")
          ]
        },
        {
          field: ["NonResident.Individual.Address.Suburb", "NonResident.Entity.Address.Suburb"], 
          minLen: "2", 
          maxLen: "50", 
          rules: [
            failure("nrial31", "281", "Must not be completed", notEmpty).onSection("E")
          ]
        },
        {
          field: ["NonResident.Individual.Address.City", "NonResident.Entity.Address.City"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            ignore("nric1").onSection("ABCDG"),
            failure("nric2", "284", "Must not be completed", notEmpty).onSection("E")
          ]
        },
        {
          field: ["NonResident.Individual.Address.State", "NonResident.Entity.Address.State"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            ignore("nris1").onSection("ABCDG"),
            failure("nris2", "286", "Must not be completed", notEmpty).onSection("E")
          ]
        },
        {
          field: ["NonResident.Individual.Address.PostalCode", "NonResident.Entity.Address.PostalCode"], 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            ignore("nriz1").onSection("ABCDG"),
            failure("nriz2", "288", "Must not be completed", notEmpty).onSection("E"),
            failure("nriz3", "L04", "Length is too long", notEmpty.and(isTooLong("10"))).onSection("ABCDG")
          ]
        },
        {
          field: ["NonResident.Individual.Address.Country", "NonResident.Entity.Address.Country"], 
          len: "2", 
          rules: [
            failure("nrictry1", "289", "Must be completed", isEmpty).onSection("ABCDEG"),
            failure("nrictry2", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABCDEG"),
            failure("nrictry3", "290", "SWIFT country code may not be {{Locale}}", notEmpty.and(hasValue(map("Locale")))).onSection("ABCDG"),
            failure("nrictry4", "238", "SWIFT country code must not be {{Locale}} except if the ForeignIDCountry under IndividualCustomer is NA, LS or SZ", notEmpty.and(hasValue(map("Locale")).and(notTransactionFieldValue("Resident.Individual.ForeignIDCountry", ["NA", "LS", "SZ"])))).onSection("E"),
            failure("nrictry5", "238", "EU is not a valid country and may not be used", notEmpty.and(hasValue("EU"))).onOutflow().onSection("A").notOnCategory("513"),
            failure("nrictry6", "238", "EU is not a valid country and may not be used", notEmpty.and(hasValue("EU"))).onInflow().onSection("A").notOnCategory("517"),
            failure("nrictry7", "238", "For Outflow category 513 the country must be linked to the currency (EU must be used for EUR payments)", notEmpty.and(notMatchToCurrency)).onOutflow().onSection("A").onCategory("513"),
            failure("nrictry8", "238", "For Inflow category 517 the country must be linked to the currency (EU must be used for EUR payments)", notEmpty.and(notMatchToCurrency)).onInflow().onSection("A").onCategory("517"),
            failure("nrictry9", "252", "Must not be completed", notEmpty).onSection("F")
          ]
        },
        {
          field: "Resident", 
          rules: [
            failure("rg1", "277", "Must contain one of IndividualCustomer or EntityCustomer or Exception elements", notTransactionField("Resident.Individual").and(notTransactionField("Resident.Entity").and(notTransactionField("Resident.Exception")))).onSection("ABCEG"),
            failure("rg2", "291", "If the Flow is OUT and category 255 is used, Resident EntityCustomer element must be completed", notTransactionField("Resident.Entity")).onOutflow().onCategory("255").onSection("AB"),
            failure("rg5", "548", "Resident CustomerAccountHolder and AdditionalCustomer Data elements must not be completed", notEmpty).onSection("F"),
            ignore("rg6")
          ]
        },
        {
          field: "Resident.Individual.Surname", 
          minLen: "1", 
          maxLen: "35", 
          rules: [
            failure("risn1", "254", "If Resident Individual is completed, Surname must be completed", isEmpty).onSection("ABCEG"),
            failure("risn2", "255", "The words specified under NonResident or Resident ExceptionName, excluding STRATE, must not be used.", notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName)).onSection("ABCE"),
            failure("risn3", "254", "If the Flow is OUT and the Subject under the MonetaryDetails element is REMITTANCE DISPENSATION, surname must have a value.", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("risn4", "254", "If the Flow is IN and the category is 400 and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION, surname must be completed.", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onCategory("400").onSection("A")
          ]
        },
        {
          field: "Resident.Individual.Name", 
          minLen: "1", 
          maxLen: "50", 
          rules: [
            failure("rin1", "256", "If Resident Individual is completed, must be completed", isEmpty.and(hasTransactionField("Resident.Individual"))).onSection("ABCEG"),
            failure("rin2", "256", "If the Flow is OUT and the Subject under the MonetaryDetails element is REMITTANCE DISPENSATION, name must have a value.", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("rin3", "256", "If the Flow is IN and the category is 400 and the Subject under the MonetaryDetails is REMITTANCE DISPENSATION, name must be completed.", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onCategory("400").onSection("A"),
            failure("rin4", "255", "The words specified under NonResident or Resident ExceptionName, excluding STRATE, must not be used.", notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName)).onSection("ABCE")
          ]
        },
        {
          field: "Resident.Individual.Gender", 
          len: "1", 
          rules: [
            failure("rig1", "257", "Must be completed", isEmpty).onSection("ABEG"),
            failure("rig2", "295", "Invalid Gender value", notEmpty.and(notValueIn(["M", "F"]))).onSection("ABEG"),
            warning("rig3", "S11", "The gender should match the ID Number", notEmpty.and(evalTransactionField("Resident.Individual.IDNumber", notEmpty.and(isValidRSAID)).and(notMatchesGenderToIDNumber("Resident.Individual.IDNumber")))).onSection("ABEG")
          ]
        },
        {
          field: "Resident.Individual.DateOfBirth", 
          rules: [
            failure("ridob1", "296", "Must be completed", isEmpty).onSection("ABEG"),
            failure("ridob2", "215", "Date format incorrect (Date format is CCYY-MM-DD)", notEmpty.and(notPattern(/^(19|20)\d{2}-(0\d|10|11|12)-(0[1-9]|1\d|2\d|3[01])$/).or(isDaysInFuture("0")))).onSection("ABEG"),
            warning("ridob3", "S11", "The date of birth should match the ID Number", notEmpty.and(hasTransactionField("Resident.Individual.IDNumber").and(notMatchResidentDateToIDNumber))).onSection("ABEG")
          ]
        },
        {
          field: "Resident.Individual.IDNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("riidn1", "298", "If BoPCategory and SubBoPCategory is 511/01 to 511/08 or 512/01 to 512/07 or 513 is used, must be completed", isEmpty).onSection("AB").onCategory(["511", "512", "513"]),
            failure("riidn2", "298", "If the Flow is OUT and category 401 is used, must be completed", isEmpty).onOutflow().onSection("AB").onCategory("401"),
            failure("riidn3", "297", "Invalid ID number if completed. (Note, if the ID number does not comply to the algorithm, the Subject must be INVALIDIDNUMBER to accept an invalid ID number)", notEmpty.and(notValidRSAID).and(notMoneyFieldValue("AdHocRequirement.Subject", "INVALIDIDNUMBER"))).onSection("ABEG"),
            failure("riidn4", "294", "If IndividualCustomer is selected, at least one of IDNumber or TempResPermitNumber or ForeignIDNumber must be completed", isEmpty.and(notTransactionField("Resident.Individual.TempResPermitNumber").and(notTransactionField("Resident.Individual.ForeignIDNumber")))).onSection("ABE"),
            failure("riidn5", "563", "If the Subject is SDA, IDNumber must be completed", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onSection("AB")
          ]
        },
        {
          field: "Resident.Individual.TempResPermitNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("ritrpn1", "299", "If CategoryCode 511, 512 or 513 is used, TempResPermitNumber may not be completed", notEmpty).onSection("AB").onCategory(["511", "512", "513"]),
            failure("ritrpn2", "299", "If the Flow is OUT and category 401 is used, may not be completed", notEmpty).onOutflow().onSection("AB").onCategory("401"),
            failure("ritrpn3", "294", "If IndividualCustomer is selected, at least one of IDNumber or TempResPermitNumber or ForeignIDNumber must be completed", isEmpty.and(notTransactionField("Resident.Individual.IDNumber").and(notTransactionField("Resident.Individual.ForeignIDNumber")))).onSection("ABE"),
            failure("ritrpn4", "562", "If the Subject is SDA, TempRespermitNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onSection("AB")
          ]
        },
        {
          field: "Resident.Individual.ForeignIDNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("rifidn1", "452", "If category is 511/01 to 511/08 or 512/01 to 512/07 or 513 is used, ForeignIDNumber may not be completed", notEmpty).onSection("AB").onCategory(["511", "512", "513"]),
            failure("rifidn2", "452", "If the Flow is OUT and category 401 is used, may not be completed", notEmpty).onOutflow().onSection("AB").onCategory("401"),
            failure("rifidn3", "294", "If IndividualCustomer is selected, at least one of IDNumber or TempResPermitNumber or ForeignIDNumber must be completed", isEmpty.and(notTransactionField("Resident.Individual.IDNumber").and(notTransactionField("Resident.Individual.TempResPermitNumber")))).onSection("ABE"),
            failure("rifidn4", "204", "If the Non Resident AccountIdentifier is NON RESIDENT OTHER or CASH, the Resident IndividualCustomer is completed and the category is 250 or 251, the ForeignIDNumber must be completed", isEmpty.and(hasNonResidentFieldValue("AccountIdentifier", ["NON RESIDENT OTHER", "CASH"]))).onSection("A").onCategory(["250", "251"]),
            failure("rifidn5", "562", "If the Subject is SDA, ForeignIDNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onSection("AB")
          ]
        },
        {
          field: "Resident.Individual.ForeignIDCountry", 
          len: "2", 
          rules: [
            failure("rifidc1", "300", "If ForeignIDNumber is completed, must be completed", isEmpty.and(hasTransactionField("Resident.Individual.ForeignIDNumber"))).onSection("AB"),
            failure("rifidc2", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABE"),
            failure("rifidc3", "391", "The ForeignID Country must not be {{Locale}}", notEmpty.and(hasValue(map("Locale")))).onSection("ABE")
          ]
        },
        {
          field: "Resident.Individual.PassportNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("ripn1", "301", "If BoPCategory 255 is used and the Flow is OUT, must not be completed (IndividualThirdPartyPassportNumber must be completed)", notEmpty).onOutflow().onSection("AB").onCategory("255"),
            failure("ripn2", "301", "Must not be completed", notEmpty).onSection("G"),
            failure("ripn3", "302", "If BoPCategory 256 is used and the PassportNumber is not completed, (account holder is not traveling) the IndividualThirdPartyPassportNumber must be completed.", isEmpty.and(not(hasAllMoneyField("ThirdParty.Individual.PassportNumber")))).onSection("AB").onCategory("256")
          ]
        },
        {
          field: "Resident.Individual.PassportCountry", 
          len: "2", 
          rules: [
            failure("ripc1", "259", "If PassportNumber is completed, must be completed", isEmpty.and(hasTransactionField("Resident.Individual.PassportNumber"))).onSection("AB"),
            failure("ripc2", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("AB"),
            failure("ripc3", "302", "If BoPCategory 256 is used and the PassportCountry is not completed, (account holder is not traveling) the IndividualThirdPartyPassportCountry must be completed.", isEmpty.and(not(hasAllMoneyField("ThirdParty.Individual.PassportCountry")))).onSection("AB").onCategory("256")
          ]
        },
        {
          field: ["Resident.Individual.BeneficiaryID1", "Resident.Individual.BeneficiaryID2", "Resident.Individual.BeneficiaryID3", "Resident.Individual.BeneficiaryID4"], 
          rules: [
            deprecated("ribenid", "S05", "This fields is not used for finsurv submissions", notEmpty)
          ]
        },
        {
          field: "Resident.Entity.EntityName", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("relen1", "304", "If Resident EntityCustomer is used, must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("ABC"),
            failure("relen2", "303", "Must not be completed", notEmpty).onSection("F"),
            failure("relen3", "261", "If the Subject under the MonetaryDetails is REMITTANCE DISPENSATION then an Entity must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("relen4", "571", "Entity Name must be equal to the registered IHQ name as per the IHQ table if the RegistrationNumber is equal to the IHQ registration number as per the IHQ table", notEmpty.and(evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber"))).and(not(isInLookup("ihqCompanies", "companyName")))).onSection("A"),
            failure("relen5", "447", "If NonResident Exception is IHQ, value must not be same as {{Regulator}}-registered IHQ name", notEmpty.and(evalTransactionField("NonResident.Exception.ExceptionName", hasValue("IHQ"))).and(isInLookup("ihqCompanies", "companyName"))).onSection("A"),
            failure("relen6", "255", "The words specified under Non-resident or Resident ExceptionName, excluding STRATE, must not be used.", notEmpty.and(notPattern(/^[Ss][Tt][Rr][Aa][Tt][Ee]$/).and(isExceptionName))).onSection("ABCDEG"),
            failure("relen7", "303", "If the Reporting Entity Code is 304 or 305, it must not be completed", notEmpty).onSection("G").onCategory(["304", "305"])
          ]
        },
        {
          field: "Resident.Entity.TradingName", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("retn1", "304", "If Resident EntityCustomer is used, must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("ABC")
          ]
        },
        {
          field: "Resident.Entity.RegistrationNumber", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("rern1", "306", "If Resident EntityCustomer is used, must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("ABEG"),
            failure("rern2", "403", "If Subject is AIRPORT the RegistrationNumber must be GOVERNMENT", notValue("GOVERNMENT").and(hasMoneyFieldValue("AdHocRequirement.Subject", "AIRPORT"))).onSection("AB").onCategory("830"),
            failure("rern3", "446", "If NonResident Exception is IHQ, RegistrationNumber must not be same as SARB-registered IHQ registration number", notEmpty.and(evalTransactionField("NonResident.Exception.ExceptionName", hasValue("IHQ"))).and(isInLookup("ihqCompanies", "registrationNumber"))).onSection("A"),
            failure("rern4", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces)).onSection("ABEG")
          ]
        },
        {
          field: "Resident.Entity.InstitutionalSector", 
          len: "2", 
          rules: [
            failure("reis1", "308", "Must be completed", isEmpty).onSection("ABE"),
            failure("reis2", "518", "Must not be completed", notEmpty).onSection("F"),
            failure("reis3", "310", "If InstitutionalSectorCode is completed, must be valid", notEmpty.and(notValidInstitutionalSector)).onSection("ABCDEG")
          ]
        },
        {
          field: "Resident.Entity.IndustrialClassification", 
          len: "2", 
          rules: [
            failure("reic1", "311", "Must be completed", isEmpty).onSection("ABE"),
            failure("reic2", "519", "Must not be completed", notEmpty).onSection("F"),
            failure("reic3", "313", "If IndustrialClassification is completed, must be valid", notEmpty.and(notValidIndustrialClassification)).onSection("ABCDEG")
          ]
        },
        {
          field: "Resident.Exception", 
          rules: [
            failure("re1", "315", "Must not contain a value", notEmpty).onSection("EF")
          ]
        },
        {
          field: "Resident.Exception.ExceptionName", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("ren1", "267", "May only contain one of the specified values", notResExceptionName).onSection("ACD"),
            failure("ren2", "316", "If category 250 or 251 is used, may only contain the value MUTUAL PARTY", notEmpty.and(notValue("MUTUAL PARTY"))).onSection("A").onCategory(["250", "251"]),
            failure("ren3", "316", "For any BoPCategory other than 200, 250, 251 the value MUTUAL PARTY must not be completed.", notEmpty.and(hasValue("MUTUAL PARTY"))).onSection("A").notOnCategory(["200", "250", "251"]),
            failure("ren4", "315", "May not be used. MUTUAL PARTY is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("MUTUAL PARTY"))).onSection("BCDEFG"),
            failure("ren5", "316", "May not be used. BULK PENSIONS is only valid for category 407 or 400", notEmpty.and(hasValue("BULK PENSIONS"))).onSection("A").notOnCategory(["400", "407"]),
            failure("ren6", "316", "May only be completed if the category is 100 or 200 or 300 or 400 or 500 or 600 or 700 or 800", notEmpty.and(hasValue("UNCLAIMED DRAFTS"))).onSection("A").notOnCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("ren7", "315", "May not be completed. FCA NON RESIDENT NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("FCA NON RESIDENT NON REPORTABLE"))).onSection("ABEFG"),
            failure("ren8", "315", "May not be completed. VOSTRO NON REPORTABLE is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("VOSTRO NON REPORTABLE"))).onSection("ABEFG"),
            failure("ren9", "315", "May not be completed. VOSTRO INTERBANK is only applicable for NON REPORTABLE transactions.", notEmpty.and(hasValue("VOSTRO INTERBANK"))).onSection("ABEFG"),
            failure("ren10", "316", "May not be used. BULK INTEREST is only valid for category 309/08 or 300", notEmpty.and(hasValue("BULK INTEREST"))).onSection("A").notOnCategory(["309/08", "300"]),
            failure("ren11", "316", "Category may only be 301 or 300", notEmpty.and(hasValue("BULK DIVIDENDS"))).onSection("A").notOnCategory(["301", "300"]),
            failure("ren12", "316", "Category may only be 275 or 200", notEmpty.and(hasValue("BULK BANK CHARGES"))).onSection("A").notOnCategory(["275", "200"]),
            failure("ren13", "316", "May only be completed if the category is 601/01 or 603/01 or 600", notEmpty.and(hasValue("STRATE"))).onSection("A").notOnCategory(["601/01", "603/01", "600"]),
            failure("ren14", "315", "May not be used. RAND CHEQUE is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("RAND CHEQUE"))).onSection("BCDEFG"),
            failure("ren15", "315", "May not be used. BULK PENSIONS is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK PENSIONS"))).onSection("BCDEFG"),
            failure("ren16", "315", "May not be completed except if the NonResident AccountIdentifier is NON RESIDENT RAND, VISA NET or MASTER SEND", notEmpty.and(hasValue("NON RESIDENT RAND").and(notNonResidentFieldValue("AccountIdentifier", ["NON RESIDENT RAND", "VISA NET", "MASTER SEND"])))).onSection("ABC"),
            failure("ren17", "315", "May not be used. NON RESIDENT RAND is only applicable for BOPCUS transactions", notEmpty.and(hasValue("NON RESIDENT RAND"))).onSection("CDEFG"),
            failure("ren18", "315", "May not be used. UNCLAIMED DRAFTS is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("UNCLAIMED DRAFTS"))).onSection("BCDEFG"),
            failure("ren19", "315", "May not be used. BULK INTEREST is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK INTEREST"))).onSection("BCDEFG"),
            failure("ren20", "315", "May not be used. BULK DIVIDENDS is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK DIVIDENDS"))).onSection("BCDEFG"),
            failure("ren21", "315", "May not be used. BULK BANK CHARGES is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("BULK BANK CHARGES"))).onSection("BCDEFG"),
            failure("ren22", "315", "May not be used. NOSTRO INTERBANK is only applicable for INTERBANK and NON REPORTABLE transactions", notEmpty.and(hasValue("NOSTRO INTERBANK"))).onSection("ABEFG"),
            failure("ren23", "315", "May not be used. NOSTRO NON REPORTABLE is only applicable for INTERBANK and NON REPORTABLE transactions", notEmpty.and(hasValue("NOSTRO NON REPORTABLE"))).onSection("ABEFG"),
            failure("ren24", "315", "May not be used. RTGS NON REPORTABLE is only applicable for INTERBANK and NON REPORTABLE transactions", notEmpty.and(hasValue("RTGS NON REPORTABLE"))).onSection("ABEFG"),
            failure("ren25", "315", "May not be used. STRATE is only applicable for BOPCUS transactions.", notEmpty.and(hasValue("STRATE"))).onSection("BCDEFG"),
            failure("ren26", "315", "For categories 511/01 to 511/08 or 512/01 to 512/07 or 513 the Exception must not be completed", notEmpty).onSection("AB").onCategory(["511", "512", "513"]),
            failure("ren27", "315", "If the Subject under the MonetaryDetails is REMITTANCE DISPENSATION and category 251 then MUTUAL PARTY may not be used", hasValue("MUTUAL PARTY").and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A").onCategory("251"),
            failure("ren28", "267", "Exception name is case-sensitive and must contain one of the specified values", notEmpty.and(notPattern(/^[A-Z][A-Z\s]+[A-Z]$/)))
          ]
        },
        {
          field: "Resident.Exception.Country", 
          len: "2", 
          rules: [
            failure("rec1", "318", "Must be completed", isEmpty).onSection("CD"),
            failure("rec2", "290", "If Resident ExceptionName is VOSTRO NON REPORTABLE or VOSTRO INTERBANK, the Country must not be {{Locale}}", notEmpty.and(hasValue(map("Locale"))).and(hasTransactionFieldValue("Resident.Exception.ExceptionName", ["VOSTRO NON REPORTABLE", "VOSTRO INTERBANK"]))).onSection("CD"),
            failure("rec3", "238", "Invalid country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("CD"),
            failure("rec4", "314", "May not be completed", notEmpty).onSection("ABEFG")
          ]
        },
        {
          field: "Resident.Exception.AccountIdentifier", 
          rules: [
            deprecated("rexai", "S06", "This field is not used for finsurv submissions", notEmpty)
          ]
        },
        {
          field: "Resident.Exception.AccountNumber", 
          rules: [
            deprecated("rexan", "S07", "This field is not used for finsurv submissions", notEmpty)
          ]
        },
        {
          field: ["Resident.Individual", "Resident.Entity"], 
          rules: [
            failure("g1", "340", "Must contain at least one of Email, Fax or Telephone", notEmpty.and(notResidentField("ContactDetails.Email").and(notResidentField("ContactDetails.Fax").and(notResidentField("ContactDetails.Telephone"))))).onSection("ABEG")
          ]
        },
        {
          field: ["Resident.Individual.AccountName", "Resident.Entity.AccountName"], 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("an1", "319", "Must be completed except if the AccountIdentifier is CASH or EFT or CARD PAYMENT", isEmpty.and(notResidentFieldValue("AccountIdentifier", ["CASH", "EFT", "CARD PAYMENT"]))).onSection("ABEG")
          ]
        },
        {
          field: ["Resident.Individual.AccountIdentifier", "Resident.Entity.AccountIdentifier"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("accid1", "272", "Must contain a value of RESIDENT OTHER or CFC RESIDENT or FCA RESIDENT or CASH or EFT or CARD PAYMENT", notValueIn(["RESIDENT OTHER", "CFC RESIDENT", "FCA RESIDENT", "CASH", "EFT", "CARD PAYMENT"])).onSection("ABG"),
            failure("accid2", "272", "Must contain a value of RESIDENT OTHER or CFC RESIDENT or FCA RESIDENT, CASH or VOSTRO or EFT or CARD PAYMENT", notValueIn(["RESIDENT OTHER", "CFC RESIDENT", "FCA RESIDENT", "CASH", "VOSTRO", "EFT", "CARD PAYMENT"])).onSection("CD"),
            failure("accid3", "520", "May not be completed", notEmpty).onSection("F"),
            failure("accid4", "272", "Must contain a value DEBIT CARD or CREDIT CARD", notValueIn(["DEBIT CARD", "CREDIT CARD"])).onSection("E"),
            failure("accid5", "272", "Invalid AccountIdentifier", notValueIn(["RESIDENT OTHER", "CFC RESIDENT", "FCA RESIDENT", "CASH", "VOSTRO", "DEBIT CARD", "CREDIT CARD", "EFT", "CARD PAYMENT"])).onSection("ABCDEG"),
            failure("accid6", "355", "If CFC RESIDENT, ForeignValue must be completed", hasValue("CFC RESIDENT").and(isCurrencyIn(map("LocalCurrency")))).onSection("ABCD"),
            failure("accid7", "356", "If VOSTRO, {{LocalValue}} must be completed", hasValue("VOSTRO").and(notCurrencyIn(map("LocalCurrency")))).onSection("ABCD")
          ]
        },
        {
          field: "Resident.Entity.AccountIdentifier", 
          rules: [
            failure("eaccid1", "521", "If the RegistrationNumber under Resident EntityCustomer is equal to the IHQ registration number as per the IHQ table, the Resident AccountIdentifier must be FCA RESIDENT", evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber")).and(notNonResException("IHQ")).and(not(hasValue("FCA RESIDENT")))).onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.AccountNumber", "Resident.Entity.AccountNumber"], 
          minLen: "2", 
          maxLen: "40", 
          rules: [
            failure("accno1", "279", "Must be completed", isEmpty).onSection("E"),
            failure("accno2", "522", "Must not be completed", notEmpty).onSection("F"),
            failure("accno3", "280", "Must not be equal to AccountNumber under the NonResidentData element", notEmpty.and(matchesNonResidentField("AccountNumber"))).onSection("ABCDG"),
            failure("accno4", "279", "Must be completed if the Flow is OUT and the AccountIdentifier under AdditionalCustomerData is not CASH or EFT or CARD PAYMENT", isEmpty.and(notResidentFieldValue("AccountIdentifier", ["CASH", "EFT", "CARD PAYMENT"]))).onOutflow().onSection("ABCDG")
          ]
        },
        {
          field: ["Resident.Individual.CustomsClientNumber", "Resident.Entity.CustomsClientNumber"], 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("ccn1", "320", "Must be completed if Flow is IN and category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106", isEmpty).onInflow().onSection("AB").notOnCategory(["101/11", "103/11"]).onCategory(["101", "103", "105", "106"]),
            failure("ccn2", "320", "Must be completed if Flow is OUT and category is 101/01 to 101/10 or 102/01 to 102/10 or 103/01 to 103/10 or 104/01 to 104/10 or 105 or 106", isEmpty).onOutflow().onSection("AB").notOnCategory(["101/11", "102/11", "103/11", "104/11"]).onCategory(["101", "102", "103", "104", "105", "106"]),
            failure("ccn3", "322", "CustomsClientNumber must be numeric and contain between 8 and 13 digits", notEmpty.and(notPattern(/^\d{8,13}$/))).onInflow().onSection("AB").notOnCategory(["101/11", "103/11"]).onCategory(["101", "103", "105", "106"]),
            failure("ccn4", "322", "CustomsClientNumber must be numeric and contain between 8 and 13 digits", notEmpty.and(notPattern(/^\d{8,13}$/))).onOutflow().onSection("AB").notOnCategory(["101/11", "102/11", "103/11", "104/11"]).onCategory(["101", "102", "103", "104", "105", "106"]),
            warning("ccn5", "322", "CustomsClientNumber 70707070 should not be regularly used for transactions more than R50,000.00", notEmpty.and(hasPattern(/^70707070$/)).and(hasSumLocalValue(">", "50000"))).onSection("AB"),
            failure("ccn6", "321", "May not be completed", notEmpty).onSection("DEF"),
            warning("ccn7", "S09", "Unless the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, this need not be provided and if invalid will cause the SARB to reject transaction", notEmpty.and(not(hasAnyMoneyFieldValue("CategoryCode", ["101", "103", "105", "106"])))).onInflow().onSection("AB"),
            ignore("ccn8", "S15", "Check that CustomsClientNumber 70707070 is only used if this transaction is smaller than R50,000.00", notEmpty.and(hasPattern(/^70707070$/)).and(hasSumLocalValue("<=", "50000"))).onSection("AB"),
            warning("ccn9", "S09", "Unless the category is 101/01 to 101/10 or 102/01 to 102/10 or 103/01 to 103/10 or 104/01 to 104/10 or 105 or 106, this need not be provided and if invalid will cause the SARB to reject transaction", notEmpty.and(not(hasAnyMoneyFieldValue("CategoryCode", ["101", "102", "103", "104", "105", "106"])))).onOutflow().onSection("AB"),
            warning("ccn10", "322", "CustomsClientNumber should pass one of the validations for either CCN, ID Number or Tax Number", notEmpty.and(hasPattern(/^\d{8,13}$/)).and(not(isValidCCN.or(isValidRSAID.or(isValidZATaxNumber))))).onInflow().onSection("AB").notOnCategory(["101/11", "103/11"]).onCategory(["101", "103", "105", "106"]),
            warning("ccn11", "322", "CustomsClientNumber should pass one of the validations for either CCN, ID Number or Tax Number", notEmpty.and(hasPattern(/^\d{8,13}$/)).and(not(isValidCCN.or(isValidRSAID.or(isValidZATaxNumber))))).onOutflow().onSection("AB").notOnCategory(["101/11", "102/11", "103/11", "104/11"]).onCategory(["101", "102", "103", "104", "105", "106"]),
            failure("ccn12", "322", "CustomsClientNumber is 70707070, which implies CCN is not known", notEmpty.and(hasPattern(/^70707070$/)))
          ]
        },
        {
          field: "Resident.Individual.TaxNumber", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("tni1", "324", "Must be completed if Flow is OUT and category is 512/01 to 512/07 or 513", isEmpty).onSection("AB").onCategory(["512", "513"]),
            failure("tni2", "523", "Must not be completed.", notEmpty).onSection("F")
          ]
        },
        {
          field: "Resident.Entity.TaxNumber", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("tne1", "324", "Must be completed if category is 101/01 to 101/10 or 102/01 to 102/10 or 103/01 to 103/10 or 104/01 to 104/10 or 105 or 106", isEmpty).onSection("AB").notOnCategory(["101/11", "102/11", "103/11", "104/11"]).onCategory(["101", "102", "103", "104", "105", "106"]),
            failure("tne2", "523", "Must not be completed.", notEmpty).onSection("F")
          ]
        },
        {
          field: ["Resident.Individual.TaxNumber", "Resident.Entity.TaxNumber"], 
          rules: [
            warning("tn1", "325", "Incorrect format: Invalid checksum", notEmpty.and(not(isValidZATaxNumber))).onSection("AB")
          ]
        },
        {
          field: "Resident.Individual.VATNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            ignore("vni1"),
            failure("vni2", "524", "Must not be completed.", notEmpty).onSection("F")
          ]
        },
        {
          field: "Resident.Entity.VATNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("vne1", "326", "Must be completed or have \"NO VAT NUMBER\" if category is 101/01 to 101/10 or 102/01 to 102/10 or 103/01 to 103/10 or 104/01 to 104/10 or 105 or 106", isEmpty).onSection("AB").notOnCategory(["101/11", "102/11", "103/11", "104/11"]).onCategory(["101", "102", "103", "104", "105", "106"]),
            failure("vne2", "524", "Must not be completed.", notEmpty).onSection("F")
          ]
        },
        {
          field: ["Resident.Individual.VATNumber", "Resident.Entity.VATNumber"], 
          rules: [
            warning("vn1", "326", "Incorrect format: Should be \"NO VAT NUMBER\" or has invalid checksum", notEmpty.and(not(hasValue("NO VAT NUMBER").or(isValidZAVATNumber)))).onSection("AB")
          ]
        },
        {
          field: ["Resident.Individual.TaxClearanceCertificateIndicator", "Resident.Entity.TaxClearanceCertificateIndicator"], 
          len: "1", 
          rules: [
            failure("tcci1", "327", "Must be \"Y\" or \"N\" if Flow is OUT and category is 512/01 to 512/07 or 513", notValueIn(["Y", "N"])).onOutflow().onSection("AB").onCategory(["512", "513"]),
            failure("tcci2", "525", "Must not be completed", notEmpty).onSection("CDEF")
          ]
        },
        {
          field: ["Resident.Individual.TaxClearanceCertificateReference", "Resident.Entity.TaxClearanceCertificateReference"], 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("tccr1", "249", "TaxClearanceCertificateIndicator is \"Y\", so needs to be completed", isEmpty.and(hasResidentFieldValue("TaxClearanceCertificateIndicator", "Y"))).onSection("AB"),
            failure("tccr2", "526", "Must not be completed", notEmpty).onSection("CDEF")
          ]
        },
        {
          field: ["Resident.Individual.StreetAddress.AddressLine1", "Resident.Entity.StreetAddress.AddressLine1", "Resident.Individual.PostalAddress.AddressLine1", "Resident.Entity.PostalAddress.AddressLine1"], 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("a1_1", "332", "Must be completed", isEmpty).onSection("BEG"),
            failure("a1_2", "527", "Must not be completed", notEmpty).onSection("DF"),
            failure("a1_3", "332", "Must be completed (unless REMITTANCE DISPENSATION on a reversal)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onSection("A"),
            failure("a1_4", "332", "Must be completed (unless REMITTANCE DISPENSATION)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.StreetAddress.AddressLine2", "Resident.Entity.StreetAddress.AddressLine2", "Resident.Individual.PostalAddress.AddressLine2", "Resident.Entity.PostalAddress.AddressLine2"], 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("a2_1", "527", "Must not be completed", notEmpty).onSection("DF")
          ]
        },
        {
          field: ["Resident.Individual.StreetAddress.Suburb", "Resident.Entity.StreetAddress.Suburb", "Resident.Individual.PostalAddress.Suburb", "Resident.Entity.PostalAddress.Suburb"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("s1", "333", "Must be completed", isEmpty).onSection("BEG"),
            failure("s2", "527", "May not be completed", notEmpty).onSection("DF"),
            failure("s3", "333", "Must be completed (unless REMITTANCE DISPENSATION on a reversal)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onSection("A"),
            failure("s4", "333", "Must be completed (unless REMITTANCE DISPENSATION)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.StreetAddress.City", "Resident.Entity.StreetAddress.City", "Resident.Individual.PostalAddress.City", "Resident.Entity.PostalAddress.City"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("c1", "334", "Must be completed", isEmpty).onSection("BEG"),
            failure("c2", "527", "Must not be completed", notEmpty).onSection("DF"),
            failure("c3", "334", "Must be completed (unless REMITTANCE DISPENSATION on a reversal)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onSection("A"),
            failure("c4", "334", "Must be completed (unless REMITTANCE DISPENSATION)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.StreetAddress.PostalCode", "Resident.Entity.StreetAddress.PostalCode"], 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("spc1", "338", "Invalid postal code", notEmpty.and(notValidPostalCode)).onSection("ABEG"),
            failure("spc2", "527", "May not be completed", notEmpty).onSection("DF"),
            failure("spc3", "338", "If the Street Province is NAMIBIA or LESOTHO or SWAZILAND then the PostalCode must be 9999", hasResidentFieldValue("StreetAddress.Province", ["NAMIBIA", "LESOTHO", "SWAZILAND"]).and(notValue("9999"))).onSection("E"),
            failure("spc4", "338", "A Postal code of 9999 may not be used for South African province", notEmpty.and(hasValue("9999").and(notResidentFieldValue("StreetAddress.Province", ["NAMIBIA", "LESOTHO", "SWAZILAND"])))).onSection("ABEG")
          ]
        },
        {
          field: ["Resident.Individual.PostalAddress.PostalCode", "Resident.Entity.PostalAddress.PostalCode"], 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("pc1", "338", "Invalid postal code", notEmpty.and(notValidPostalCode)).onSection("ABEG"),
            failure("pc2", "337", "Must be completed", isEmpty).onSection("BEG"),
            failure("pc3", "527", "Must not be completed", notEmpty).onSection("DF"),
            failure("pc4", "338", "If the Street Province is NAMIBIA or LESOTHO or SWAZILAND then the PostalCode must be 9999", hasResidentFieldValue("PostalAddress.Province", ["NAMIBIA", "LESOTHO", "SWAZILAND"]).and(notValue("9999"))).onSection("E"),
            failure("pc5", "338", "A Postal code of 9999 may not be used for South African province", notEmpty.and(hasValue("9999").and(notResidentFieldValue("PostalAddress.Province", ["NAMIBIA", "LESOTHO", "SWAZILAND"])))).onSection("ABEG"),
            failure("pc6", "337", "Must be completed (unless REMITTANCE DISPENSATION on a reversal)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onInflow().onSection("A"),
            failure("pc7", "337", "Must be completed (unless REMITTANCE DISPENSATION)", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A")
          ]
        },
        {
          field: ["Resident.Individual.ContactDetails.ContactSurname", "Resident.Entity.ContactDetails.ContactSurname", "Resident.Individual.ContactDetails.ContactName", "Resident.Entity.ContactDetails.ContactName"], 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("cn1", "339", "Must be completed", isEmpty).onSection("ABE"),
            failure("cn2", "528", "Must not be completed", notEmpty).onSection("DF")
          ]
        },
        {
          field: ["Resident.Individual.ContactDetails.Email", "Resident.Entity.ContactDetails.Email"], 
          minLen: "2", 
          maxLen: "120", 
          rules: [
            failure("cnte1", "340", "Must contain at least one of Email, Fax or Telephone", notResidentField("ContactDetails.Email").and(notResidentField("ContactDetails.Fax")).and(notResidentField("ContactDetails.Telephone"))).onSection("ABEG"),
            failure("cnte2", "528", "Must not be completed", notEmpty).onSection("DF"),
            failure("cnte3", "E01", "This is not a valid email address", notEmpty.and(notValidEmail))
          ]
        },
        {
          field: ["Resident.Individual.ContactDetails.Fax", "Resident.Entity.ContactDetails.Fax", "Resident.Individual.ContactDetails.Telephone", "Resident.Entity.ContactDetails.Telephone"], 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("cntft1", "340", "Must contain at least one of Email, Fax or Telephone", notResidentField("ContactDetails.Email").and(notResidentField("ContactDetails.Fax")).and(notResidentField("ContactDetails.Telephone"))).onSection("ABEG"),
            failure("cntft2", "528", "Must not be completed", notEmpty).onSection("DF"),
            failure("cntft3", "341", "Must be in a 10 to 15 digit format", notEmpty.and(notPattern(/^\d{10,15}$/))).onSection("ABEG")
          ]
        },
        {
          field: ["Resident.Individual.CardNumber", "Resident.Entity.CardNumber"], 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("crd1", "342", "Must be completed", isEmpty).onSection("E"),
            failure("crd2", "344", "Must not be completed", notEmpty).onSection("ABCDFG")
          ]
        },
        {
          field: ["Resident.Individual.SupplementaryCardIndicator", "Resident.Entity.SupplementaryCardIndicator"], 
          len: "1", 
          rules: [
            failure("crdi1", "345", "Must be set to be either Y or N (blank assumed as N)", notEmpty.and(notValueIn(["Y", "N"]))).onSection("E"),
            failure("crdi2", "347", "Must not be completed", notEmpty).onSection("ABCDFG")
          ]
        },
        {
          field: "MonetaryAmount", 
          rules: [
            failure("tma1", "S08", "At least one MonetaryAmount entry must be provided", emptyMoneyField),
            failure("tma2", "349", "Must contain a sequential number that must start with the value 1 except if the ReplacementTransaction indicator is Y", notValidSequenceNumbers)
          ]
        },
        {
          field: "Total{{LocalValue}}", 
          rules: [
            warning("tlv1", "DVT", "If the FlowCurrency is not {{LocalCurrency}} then the sum of the Local Monetary Amounts must add up to the Total{{LocalValue}}", notEmpty.and(notCurrencyIn(map("LocalCurrency")).and(notSumLocalValue))).onSection("ABCDEG")
          ]
        }
      ]
    };
    v27 = {
      ruleset: "Standard Money Rules", 
      scope: "money", 
      validations: [
        {
          field: "SequenceNumber", 
          rules: [
            failure("mseq1", "547", "Must be completed", isEmpty)
          ]
        },
        {
          field: "MoneyTransferAgentIndicator", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mta1", "351", "Must contain one of the following values: AD, or ADLA, CARD or MONEYGRAM, or WESTERNUNION, or PAYPAL, or EXCHANGE4FREE, or MUKURU, or MONEYTRANS or XPRESSMONEY or ZMT or ESKOM or SANLAM or MOMENTUM or TOURVEST or TOWER or IMALI or TRAVELEX or INTERAFRICA or GLOBAL or SIKHONA or FOREXWORLD or ACE or AYOBA or MASTERCURRENCY or INTERCHANGE or HELLO PAISA or TRAVEL CARD or TRAVELLERS CHEQUE or SOUTH EAST or MAMA MONEY or SHOPRITE or DAYTONA or FLASH or PEP or AFROCOIN or ECONET or PAYMENT PARTNER.", notEmpty.and(notMoneyTransferAgent)).onSection("ABCD"),
            failure("mta2", "351", "Must contain the value BOPDIR", notEmpty.and(notValueIn("BOPDIR"))).onSection("G"),
            failure("mta3", "213", "If the CategoryCode is 833, the MoneyTransferAgentIndicator may not be AD or ADLA", notEmpty.and(hasValueIn(["AD", "ADLA"]))).onSection("AB").onCategory("833"),
            failure("mta4", "351", "Must contain the value CARD", notEmpty.and(notValueIn("CARD"))).onSection("EF"),
            failure("mta5", "350", "Must be completed", isEmpty),
            failure("mta6", "S13", "This dealer is an AD and therefore may not specify ADLA", notEmpty.and(dealerTypeAD).and(hasValue("ADLA"))).onSection("ABCD"),
            failure("mta7", "S833", "For BOPCUS transactions, CARD may be used as MTA Indicator provided that the Bop Category is 833.", notEmpty.and(hasValueIn(["CARD"]))).onSection("A").notOnCategory(["833", "800"]),
            failure("mta8", "S833", "Should not contain the value BOPDIR for AD on sections other than G", notEmpty.and(hasValueIn(["BOPDIR"])).and(dealerTypeAD)).onSection("ABCDEF")
          ]
        },
        {
          field: "{{LocalValue}}", 
          rules: [
            failure("mrv1", "348", "At least one of {{LocalValue}} or ForeignValue must be present", isEmpty.or(not(isGreaterThan("0.00"))).and(notMoneyField("ForeignValue").or(hasMoneyFieldValue("ForeignValue", "0")))).onSection("ABCDEG"),
            failure("mrv2", "352", "Must not contain a negative value", notEmpty.and(isNegative)).onSection("ABCDEG"),
            failure("mrv3", "353", "May not equal ForeignValue except if ForeignCurrencyCode is LSL, NAD, SZL, DKK, NOK, SEK, CNY, BWP, UAH, ZAR, HKD, RTG or ZMW", equalsMoneyField("ForeignValue").and(notCurrencyIn(["LSL", "NAD", "SZL", "DKK", "NOK", "SEK", "CNY", "BWP", "UAH", "ZAR", "HKD", "RTG", "ZMW"]))).onSection("ABCDEG"),
            failure("mrv4", "354", "If a ForeignValue is completed and a {{LocalValue}} is reported, the reported {{LocalValue}} must be within a 15% variance with the applicable mid-rate completed by the {{Regulator}}. MWK and ZWD are excluded from this validation", notEmpty.and(hasMoneyField("ForeignValue").and(notCurrencyIn(["MWK", "ZWD", map("LocalCurrency")]).and(outOfMidRateVariance)))).onSection("ABCDEG"),
            failure("mrv5", "361", "Must not be completed", notEmpty).onSection("F"),
            warning("mrv6", "515", "Category 101/11 or 102/11 or 103/11 or 104/11 is used and the value exceeds {{LocalValue}} of {{LocalCurrencySymbol}}100,000.00", notEmpty.and(isGreaterThan("100000"))).onSection("AB").onCategory(["101/11", "102/11", "103/11", "104/11"]),
            failure("mrv7", "516", "If category 107 is used, the value may not exceed {{LocalValue}} of {{LocalCurrencySymbol}}1,000.00", notEmpty.and(isGreaterThan("1000"))).onSection("AB").onCategory("107"),
            failure("mrv8", "343", "Must not exceed 20 digits", isTooLong("20")).onSection("ABCDEG"),
            failure("mrv9", "356", "If the flow is OUT and the Subject is REMITTANCE DISPENSATION, the {{LocalValueName}} value must be completed", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onOutflow().onSection("A"),
            failure("mrv11", "353", "Both the {{LocalValue}} and ForeignValue for RTG transactions must be supplied, since the RTG rate is not available.", isEmpty.or(notMoneyField("ForeignValue").or(hasMoneyFieldValue("ForeignValue", "0"))).and(isCurrencyIn("RTG"))).onSection("ABCDEG"),
            failure("mrv12", "353", "Must equal ForeignValue if ForeignCurrencyCode is same as DomesticCurrencyCode, and both Domestic and Foreign values are supplied.", notEmpty.and(hasMoneyField("ForeignValue")).and(hasTransactionFieldValue("FlowCurrency", map("LocalCurrency"))).and(not(equalsMoneyField("ForeignValue"))))
          ]
        },
        {
          field: "ForeignValue", 
          rules: [
            failure("mfv1", "348", "At least one of {{LocalValue}} or ForeignValue must be present", isEmpty.and(notMoneyField(map("LocalValue")))).onSection("ABCDEG"),
            failure("mfv2", "357", "Must not contain a negative value", isNegative).onSection("ABCDEG"),
            failure("mfv3", "355", "If FlowCurrency is not {{LocalCurrency}}, ForeignValue must be completed", isEmpty.or(not(isGreaterThan("0.00"))).and(notCurrencyIn(map("LocalCurrency")))).onSection("ABCDEG"),
            failure("mfv4", "362", "Must not be completed", notEmpty).onSection("F"),
            failure("mfv5", "343", "Must not exceed 20 digits", isTooLong("20")).onSection("ABCDEG"),
            failure("mfv6", "355", "If CFC RESIDENT, ForeignValue must be completed", isEmpty.or(not(isGreaterThan("0.00"))).and(hasResidentFieldValue("AccountIdentifier", "CFC RESIDENT"))).onSection("ABC")
          ]
        },
        {
          field: "CategoryCode", 
          len: "3", 
          rules: [
            failure("mcc1", "366", "Must be completed", isEmpty).onSection("ABG"),
            failure("mcc2", "367", "May not be completed", notEmpty).onSection("DEF"),
            failure("mcc3", "368", "Invalid BoPcategory", notEmpty.and(hasInvalidCategory)).onSection("ABG"),
            failure("mcc4", "S10", "For NON REPORTABLE transactions this must be set to ZZ1", notEmpty.and(notValue("ZZ1"))).onSection("C"),
            failure("mcc5", "292", "If the Flow is IN and category 303, 304, 305, 306, 416 or 417 is used, the Entity element under Resident is completed, the ThirdParty Individual attributes must be completed.", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname"))).onInflow().onCategory(["303", "304", "305", "306", "416", "417"]).onSection("AB"),
            failure("mcc6", "416", "If CategoryCode 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, the third party individual and address details must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname").or(notMoneyField("ThirdParty.StreetAddress.AddressLine1")).or(notMoneyField("ThirdParty.PostalAddress.AddressLine1")))).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("mcc7", "416", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, the third party individual and address details must be completed", hasTransactionField("Resident.Entity").and(notMoneyField("ThirdParty.Individual.Surname").or(notMoneyField("ThirdParty.StreetAddress.AddressLine1")).or(notMoneyField("ThirdParty.PostalAddress.AddressLine1")))).onInflow().onSection("A").onCategory(["511", "516"]),
            failure("mcc8", "323", "If the Flow is OUT then the categories 102/01 to 102/10 or 104/01 to 104/10 can only be used for import undertaking customers", notEmpty.and(not(hasMoneyField("ThirdParty.CustomsClientNumber").and(evalMoneyField("ThirdParty.CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))).and(not(evalResidentField("CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))))).onOutflow().onSection("AB").onCategory(["102", "104"]).notOnCategory(["102/11", "104/11"]),
            failure("mcc9", "323", "If the Flow is OUT then the categories 101/01 to 101/10 or 103/01 to 103/10 may not be used for import undertaking customers", notEmpty.and(hasMoneyField("ThirdParty.CustomsClientNumber").and(evalMoneyField("ThirdParty.CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))).or(not(hasMoneyField("ThirdParty.CustomsClientNumber")).and(evalResidentField("CustomsClientNumber", isInLookup("luClientCCNs", "ccn"))))).onOutflow().onSection("AB").onCategory(["101", "103"]).notOnCategory(["101/11", "103/11"]),
            failure("mcc11", "S11", "For BOPCUS transactions this can not be set to ZZ1", notEmpty.and(hasValue("ZZ1"))).onSection("A")
          ]
        },
        {
          field: "CategorySubCode", 
          rules: [
            failure("msc1", "369", "Invalid SubBoPCategory", notEmpty.and(hasInvalidSubCategory.and(notValue("*")))).onSection("ABG"),
            failure("msc2", "370", "Must not be completed", notEmpty).onSection("CDE"),
            failure("msc3", "368", "This field contains the old category code. Please select a new Finsurv category", notEmpty.and(hasValue("*"))).onSection("ABG")
          ]
        },
        {
          field: "SWIFTDetails", 
          minLen: "2", 
          maxLen: "100", 
          rules: [
            failure("mswd1", "293", "Must not be completed", notEmpty).onSection("EF")
          ]
        },
        {
          field: "StrateRefNumber", 
          minLen: "1", 
          maxLen: "30", 
          rules: [
            failure("msrn1", "371", "Should be completed with the wording ON MARKET or OFF MARKET if the category is 601/01 or 603/01", isEmpty).onSection("AB").onCategory(["601/01", "603/01"]),
            failure("msrn2", "371", "Should be completed with the wording ON MARKET or OFF MARKET if the Resident ExceptionName is STRATE", isEmpty.and(hasResException("STRATE"))).onSection("AB"),
            failure("msrn3", "372", "Must not be completed", notEmpty).onSection("CDEF"),
            failure("msrn4", "565", "Should only contain ON MARKET or OFF MARKET", notEmpty.and(notValueIn(["ON MARKET", "OFF MARKET"]))).onSection("AB")
          ]
        },
        {
          field: ["Travel", "Travel.Surname", "Travel.Name", "Travel.IDNumber", "Travel.DateOfBirth", "Travel.TempResPermitNumber"], 
          rules: [
            deprecated("mtvl1", "S11", "The Travel field can no longer be used for new Finsurv messages. Use ThirdParty element instead", notEmpty)
          ]
        },
        {
          field: "LoanRefNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mlrn1", "373", "If category 801, or 802, or 803 or 804 is used, must be completed", isEmpty).onSection("AB").onCategory(["801", "802", "803", "804"]),
            failure("mlrn2", "374", "If CategoryCode is 801, or 802, or 803 or 804 and LocationCountry is LS and the LoanRefNumber must be 99012301230123", notValue("99012301230123").and(hasMoneyFieldValue("LocationCountry", "LS"))).onSection("ABG").onCategory(["801", "802", "803", "804"]),
            failure("mlrn3", "374", "If CategoryCode is 801, or 802, or 803 or 804 and LocationCountry is SZ and the LoanRefNumber must be 99456745674567", notValue("99456745674567").and(hasMoneyFieldValue("LocationCountry", "SZ"))).onSection("ABG").onCategory(["801", "802", "803", "804"]),
            failure("mlrn4", "374", "If CategoryCode is 801, or 802, or 803 or 804 and LocationCountry is NA and the LoanRefNumber must be 99789078907890", notValue("99789078907890").and(hasMoneyFieldValue("LocationCountry", "NA"))).onSection("ABG").onCategory(["801", "802", "803", "804"]),
            failure("mlrn5", "374", "If CategoryCode and CategorySubCode is 106 or 309/04 or 309/05 or 309/06 or 309/07 is used and the Flow is OUT, and the LocationCountry is LS and the LoanRefNumber must be 99012301230123", notValue("99012301230123").and(hasMoneyFieldValue("LocationCountry", "LS"))).onOutflow().onSection("AB").onCategory(["106", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlrn6", "374", "If CategoryCode and CategorySubCode is 106 or 309/04 or 309/05 or 309/06 or 309/07 is used and the Flow is OUT, and the LocationCountry is SZ and the LoanRefNumber must be 99456745674567", notValue("99456745674567").and(hasMoneyFieldValue("LocationCountry", "SZ"))).onOutflow().onSection("AB").onCategory(["106", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlrn7", "374", "If CategoryCode and CategorySubCode is 106 or 309/04 or 309/05 or 309/06 or 309/07 is used and the Flow is OUT, and the LocationCountry is NA and the LoanRefNumber must be 99789078907890", notValue("99789078907890").and(hasMoneyFieldValue("LocationCountry", "NA"))).onOutflow().onSection("AB").onCategory(["106", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlrn8", "373", "If the Flow is OUT, and category is 106 or 309/04 or 309/05 or 309/06 or 309/07, it must be completed", isEmpty).onOutflow().onSection("AB").onCategory(["106", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlrn9", "375", "If the Flow is OUT and CategoryCode is 810 or 815 or 816 or 817 or 818 or 819 is used, it may not be completed", notEmpty).onOutflow().onSection("AB").onCategory(["810", "815", "816", "817", "818", "819"]),
            failure("mlrn10", "375", "May not be completed", notEmpty).onSection("CDE"),
            failure("mlrn11", "375", "For any other category other than 801, 802, 803, 804, 106, 309/04, 309/05, 309/06 or 309/07 this must not be completed", notEmpty).onSection("AB").notOnCategory(["801", "802", "803", "804", "106", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlrn12", "374", "The LoanRefNumber must contain only characters 0-9", notEmpty.and(notPattern(/^\d+$/))).onSection("ABG")
          ]
        },
        {
          field: "LoanTenor", 
          minLen: "9", 
          maxLen: "10", 
          rules: [
            failure("mlt1", "376", "If the Flow is Out and CategoryCode 810 or 815 or 816 or 817 or 818 or 819 is used, must reflect the date of maturity in the format CCYY-MM-DD or ON DEMAND if no date is applicable", isEmpty.or(notValue("ON DEMAND").and(notDatePattern))).onOutflow().onSection("AB").onCategory(["810", "815", "816", "817", "818", "819"]),
            failure("mlt2", "346", "If the LoanTenor in the format CCYY-MM-DD it must be a future date", notEmpty.and(hasDatePattern).and(isDaysInPast("-1"))).onSection("ABG"),
            failure("mlt3", "377", "Must not be completed", notEmpty).onSection("CD")
          ]
        },
        {
          field: "LoanInterestRate", 
          minLen: "1", 
          maxLen: "25", 
          rules: [
            failure("mlir1", "378", "If the Flow is Out and CategoryCode 810 or 815 or 816 or 817 or 818 or 819 is used, must contain a value of reflecting interest rate percentage of the loan in the proper format. E.g. 0.00, 5.12, BASE PLUS 1.25, BASE MINUS 1.00, 12 LIBOR, 12 LIBOR PLUS 1.25, 6 LIBOR MINUS 0.5, 12 JIBAR, 9 JIBAR PLUS 1.25, 6 JIBAR MINUS 0.5", notPattern("^(\\d+(\\.\\d{1,2})?|FIXED \\d+(\\.\\d{1,2})?|BASE PLUS \\d+(\\.\\d{1,2})?|BASE MINUS \\d+(\\.\\d{1,2})?|\\d{1,2} [A-Z]{1,10}|\\d{1,2} [A-Z]{1,10} PLUS \\d+(\\.\\d{1,2})|\\d{1,2} [A-Z]{1,10} MINUS \\d+(\\.\\d{1,2})|[A-Z]{1,10} PLUS \\d+(\\.\\d{1,2})|[A-Z]{1,10} MINUS \\d+(\\.\\d{1,2})|[A-Z]{1,10})$")).onOutflow().onSection("AB").onCategory(["810", "815", "816", "817", "818", "819"]),
            failure("mlir2", "379", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mlir3", "380", "If the Flow is Out and CategoryCode 309/04 to 309/07 is used, must be completed reflecting the percentage interest paid in the format 0.00", notPattern("^\\d{1,3}\\.\\d{2}?$")).onOutflow().onSection("ABG").onCategory(["309/04", "309/05", "309/06", "309/07"]),
            failure("mlir4", "380", "If the Flow is In and CategoryCode 309/01 to 309/07 is used, must be completed reflecting the percentage interest paid in the format 0.00", notPattern("^\\d{1,3}\\.\\d{2}?$")).onInflow().onSection("ABG").onCategory(["309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"]),
            warning("mlir5", "S12", "It is unlikely that an interest rate greater than 100% is being charged", hasPattern("^\\d{3}\\.\\d{2}?$")).onOutflow().onSection("ABG").onCategory(["309/04", "309/05", "309/06", "309/07"]),
            warning("mlir6", "S12", "It is unlikely that an interest rate greater than 100% is being charged", hasPattern("^\\d{3}\\.\\d{2}?$")).onInflow().onSection("ABG").onCategory(["309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlir7", "379", "May not be completed unless a loan related transaction is being captured", notEmpty).onOutflow().onSection("ABG").notOnCategory(["810", "815", "816", "817", "818", "819", "309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"]),
            failure("mlir8", "379", "May not be completed unless a loan related transaction is being captured", notEmpty).onInflow().onSection("ABG").notOnCategory(["810", "815", "816", "817", "818", "819", "309/01", "309/02", "309/03", "309/04", "309/05", "309/06", "309/07"])
          ]
        },
        {
          field: "{{Regulator}}Auth.RulingsSection", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("mars1", "381", "Must be completed if the Flow is OUT and no data is supplied under either {{DealerPrefix}}InternalAuthNumber or {{RegulatorPrefix}}AuthAppNumber", isEmpty.and(notMoneyField(map("{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber")).and(notMoneyField(map("{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber"))))).onOutflow().onSection("ABG").notOnCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("mars2", "382", "Must not be completed", notEmpty).onSection("CDEF"),
            failure("mars3", "566", "If the flow is OUT and the Subject is REMITTANCE DISPENSATION then the RulingsSection must be 'Circular 06/2019'", hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION").and(notValueIn(["Circular 06/2019", "CIRCULAR 06/2019"]))).onOutflow().onSection("A"),
            failure("mars4", "566", "If the Flow is OUT and the RulingsSection is Circular 06/2019 the RandValue must be equal to or less than 5000.00 and the Subject must be REMITTANCE DISPENSATION.", hasValueIn(["Circular 06/2019", "CIRCULAR 06/2019"]).and(hasSumLocalValue(">", "5000").or(not(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))))).onOutflow().onSection("A"),
            failure("mars5", "566", "If the Flow is IN, the BoPCategory is 400 and the RulingsSection is Circular 06/2019 the RandValue must be equal to or less than 5000.00 and the Subject must be REMITTANCE DISPENSATION.", hasValueIn(["Circular 06/2019", "CIRCULAR 06/2019"]).and(hasSumLocalValue(">", "5000").or(not(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))))).onInflow().onCategory("400").onSection("A"),
            failure("mars6", "570", "If the Flow is OUT and the RandValue is equal or less than 5000.00 and the resident street and postal address attributes are not completed, the RulingsSection must be Circular 06/2019 and the Subject must be REMITTANCE DISPENSATION, except if the Resident ExceptionName is completed", notValueIn(["Circular 06/2019", "CIRCULAR 06/2019"]).and(hasSumLocalValue("<=", "5000")).and(not(hasTransactionField("Resident.Exception.ExceptionName"))).and(not(hasResidentField("StreetAddress.AddressLine1"))).and(not(hasResidentField("PostalAddress.AddressLine1")))).onOutflow().onSection("A"),
            failure("mars7", "570", "If the Flow is IN, the RandValue is equal or less than 5000.00, the BoPCategory is 400 and the resident street and postal address attributes are not completed, the RulingSection must be 22/2015 and the Subject must be REMITTANCE DISPENSATION, except if the Resident ExceptionName is completed.", notValue("22/2015").and(hasSumLocalValue("<=", "5000")).and(not(hasTransactionField("Resident.Exception.ExceptionName"))).and(not(hasResidentField("StreetAddress.AddressLine1"))).and(not(hasResidentField("PostalAddress.AddressLine1")))).onInflow().onCategory("400").onSection("A")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("maian1", "381", "Must be completed if the Flow is OUT and no data is supplied under either RulingsSection or {{RegulatorPrefix}}AuthAppNumber", isEmpty.and(notMoneyField(map("{{Regulator}}Auth.RulingsSection")).and(notMoneyField(map("{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber"))))).onOutflow().onSection("ABG").notOnCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("maian2", "382", "May not be completed", notEmpty).onSection("CDEF")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumberDate", 
          rules: [
            failure("maiad1", "385", "If {{DealerPrefix}}InternalAuthNumber has a value, it must be completed", isEmpty.and(hasMoneyField(map("{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber")))).onSection("ABG"),
            failure("maiad2", "382", "May not be completed", notEmpty).onSection("CDEF"),
            failure("maiad3", "215", "Is not in the required date format is CCYY-MM-DD", notEmpty.and(notPattern(/^(19|20)\d{2}-(0\d|10|11|12)-(0[1-9]|1\d|2\d|3[01])$/))).onSection("ABG")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("masan1", "381", "Must be completed if the Flow is OUT and no data is supplied under either RulingsSection or {{DealerPrefix}}InternalAuthNumber", isEmpty.and(notMoneyField(map("{{Regulator}}Auth.RulingsSection")).and(notMoneyField(map("{{Regulator}}Auth.{{DealerPrefix}}InternalAuthNumber"))))).onOutflow().onSection("ABG").notOnCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("masan2", "386", "If the Flow is IN and the Subject is SETOFF, it must be completed", isEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "SETOFF"))).onInflow().onSection("AB"),
            failure("masan3", "382", "May not be completed", notEmpty).onSection("CDEF"),
            failure("masan4", "387", "Must be completed if the RegistrationNumber is registered as an IHQ entity or the Subject is IHQnnn", isEmpty.and(evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber")).or(evalMoneyField("AdHocRequirement.Subject", hasPattern("^IHQ\\d{3}$"))))).onSection("A")
          ]
        },
        {
          field: "{{Regulator}}Auth.{{RegulatorPrefix}}AuthRefNumber", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("masar1", "388", "If {{RegulatorPrefix}}AuthAppNumber has a value, it must be completed", isEmpty.and(hasMoneyField(map("{{Regulator}}Auth.{{RegulatorPrefix}}AuthAppNumber")))).onSection("ABG"),
            failure("masar3", "382", "May not be completed", notEmpty).onSection("CDEF")
          ]
        },
        {
          field: "CannotCategorize", 
          minLen: "2", 
          maxLen: "100", 
          rules: [
            failure("mexc1", "390", "If CategoryCode 830 is used, must be completed", isEmpty).onSection("ABG").onCategory("830"),
            failure("mexc2", "392", "Must not be completed", notEmpty).onSection("CDEF")
          ]
        },
        {
          field: "AdHocRequirement.Subject", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("madhs1", "393", "If the Subject contains a value, it must be INVALIDIDNUMBER, AIRPORT, IHQnnn (where nnn is numeric) or SETOFF or ZAMBIAN GRAIN or YES or NO or HOLDCO or SDA", notEmpty.and(notValueIn(["INVALIDIDNUMBER", "AIRPORT", "SETOFF", "ZAMBIAN GRAIN", "YES", "NO", "HOLDCO", "SDA", "REMITTANCE DISPENSATION"]).and(notPattern("^IHQ\\d{3}$")))).onSection("AB"),
            failure("madhs2", "394", "Must not be completed", notEmpty).onSection("CDE"),
            failure("madhs3", "395", "If the Subject contains the value AIRPORT the Flow must be IN", notEmpty.and(hasValue("AIRPORT")).and(notInflow)).onSection("AB"),
            failure("madhs5", "400", "If the value is SETOFF, the CategoryCode and SubCategory must be 100 or 101/01 to 101/11 or 102/01 to 102/11 103/01 to 103/11 or 104/01 to 104/11 or 105 or 106 or 107 or 108", notEmpty.and(hasValue("SETOFF"))).onSection("AB").notOnCategory(["100", "101", "102", "102", "103", "104", "105", "106", "107", "108"]),
            failure("madhs6", "401", "If the value is SETOFF, the Flow must be IN", notEmpty.and(hasValue("SETOFF")).and(notInflow)).onSection("AB"),
            failure("madhs7", "448", "If the Value is ZAMBIAN GRAIN, the CategoryCode must be 101/01 or 109/01 or 110", notEmpty.and(hasValue("ZAMBIAN GRAIN"))).onSection("AB").notOnCategory(["101/01", "109/01", "110"]),
            failure("madhs8", "393", "If the Flow is OUT and the CategoryCode is 512/01 to 512/07 or 513 and the Resident Entity element is used, the Subject must be YES or NO", hasTransactionField("Resident.Entity").and(notValueIn(["YES", "NO"]))).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("madhs9", "403", "If Subject is AIRPORT, the CategoryCode must be 830", notEmpty.and(hasValue("AIRPORT"))).onSection("AB").notOnCategory("830"),
            failure("madhs10", "403", "If Subject is AIRPORT, the Resident Entity RegistrationNumber must be GOVERNMENT", notEmpty.and(hasValue("AIRPORT")).and(notTransactionFieldValue("Resident.Entity.RegistrationNumber", "GOVERNMENT"))).onSection("AB").onCategory("830"),
            failure("madhs11", "403", "If Subject is AIRPORT, the Resident EntityName must be CORPORATION FOR PUBLIC DEPOSITS", notEmpty.and(hasValue("AIRPORT")).and(notTransactionFieldValue("Resident.Entity.EntityName", "CORPORATION FOR PUBLIC DEPOSITS"))).onSection("AB").onCategory("830"),
            failure("madhs12", "567", "If the Flow is OUT and the Subject is REMITTANCE DISPENSATION, the {{LocalCurrencyName}} value may not exceed 5,000", notEmpty.and(hasValue("REMITTANCE DISPENSATION")).and(hasSumLocalValue(">", "5000"))).onOutflow().onSection("A"),
            failure("madhs13", "393", "Transactions with ReportQualifier 'NON REPORTABLE' may not use REMITTANCE DISPENSATION", notEmpty.and(hasValue("REMITTANCE DISPENSATION"))).onSection("C"),
            failure("madhs14", "437", "If NonResident ExceptionName is IHQ, the value must be IHQnnn (where nnn is numeric)", hasTransactionFieldValue("NonResident.Exception.ExceptionName", "IHQ").and(isEmpty.or(not(hasPattern(/^IHQ\d{3}$/))))).onSection("A"),
            failure("madhs16", "397", "If the RegistrationNumber is equal to a registration number of an IHQ entity as per the IHQ table, the Subject must be IHQnnn related to the IHQ entity. Must be completed if the RegistrationNumber is registered as an IHQ entity or the Subject is IHQnnn", evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber")).and(not(hasLookupTransactionFieldValue("ihqCompanies", "ihqCode", "registrationNumber", "Resident.Entity.RegistrationNumber")))).onSection("A"),
            failure("madhs17", "399", "Invalid IHQ number", notEmpty.and(hasPattern("^IHQ\\d{3}$")).and(not(isInLookup("ihqCompanies", "ihqCode")))).onSection("A"),
            failure("madhs19", "437", "If Resident Registration Number is not an IHQ, the value can only be be IHQnnn if Non Resident Exception is IHQ", not(evalTransactionField("Resident.Entity.RegistrationNumber", isInLookup("ihqCompanies", "registrationNumber"))).and(notNonResException("IHQ")).and(hasPattern("^IHQ\\d{3}$"))).onSection("A"),
            failure("madhs20", "567", "If the Flow is IN and the Subject is REMITTANCE DISPENSATION on category 400, the {{LocalCurrencyName}} value may not exceed 5,000", notEmpty.and(hasValue("REMITTANCE DISPENSATION")).and(hasSumLocalValue(">", "5000"))).onInflow().onCategory("400").onSection("A"),
            failure("madhs21", "567", "If the Flow is IN and the Subject is REMITTANCE DISPENSATION, then category 400 must be used", notEmpty.and(hasValue("REMITTANCE DISPENSATION"))).onInflow().notOnCategory("400").onSection("A"),
            failure("madhs22", "570", "If the Flow is OUT and the RulingsSection is 'Circular 06/2019' then the Subject must be 'REMITTANCE DISPENSATION'", notValue("REMITTANCE DISPENSATION").and(hasMoneyFieldValue(map("{{Regulator}}Auth.RulingsSection"), ["Circular 06/2019", "CIRCULAR 06/2019"]))).onOutflow().onSection("A")
          ]
        },
        {
          field: "AdHocRequirement.Description", 
          minLen: "2", 
          maxLen: "100", 
          rules: [
            failure("madhd1", "402", "If Subject is used, must be completed", isEmpty.and(hasMoneyField("AdHocRequirement.Subject"))).onSection("ABG"),
            failure("madhd2", "454", "If Subject is NO, the Description must be NONE", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "NO").and(notValue("NONE")))).onSection("A"),
            failure("madhd3", "404", "Must not be completed", notEmpty).onSection("CDE")
          ]
        },
        {
          field: "LocationCountry", 
          len: "2", 
          rules: [
            failure("mlc1", "405", "Must be completed", isEmpty).onSection("ABG"),
            failure("mlc2", "238", "Invalid SWIFT country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("ABFG"),
            failure("mlc3", "290", "SWIFT country code may not be {{Locale}} (except if the Non Resident Exception is IHQ)", notEmpty.and(hasValue(map("Locale")).and(not(hasNonResException("IHQ"))))).onSection("ABFG"),
            failure("mlc4", "238", "The LocationCountry EU may only be used if the CategoryCode is 513", notEmpty.and(hasValue("EU"))).onSection("AB").notOnCategory("513"),
            failure("mlc5", "407", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mlc6", "405", "Must be completed if ForeignCardHoldersPurchases{{LocalCurrencyName}}Value and/or ForeignCardHoldersCashWithdrawals{{LocalCurrencyName}}Value is equal or greater than 0.01", isEmpty.and(hasMoneyCardValue)).onSection("F"),
            failure("mlc7", "407", "Must not be completed if ForeignCardHoldersPurchases{{LocalCurrencyName}}Value and ForeignCardHoldersCashWithdrawals{{LocalCurrencyName}}Value is equal to 0.00", notEmpty.and(not(hasMoneyCardValue))).onSection("F"),
            failure("mlc8", "398", "Value must be {{Locale}} if the Non Resident Exception is IHQ", notEmpty.and(not(hasValue(map("Locale"))).and(hasNonResException("IHQ")))).onSection("A")
          ]
        },
        {
          field: "ReversalTrnRefNumber", 
          minLen: "1", 
          maxLen: "30", 
          rules: [
            failure("mrtrn1", "408", "If CategoryCode 100 or 200 or 300 or 400 or 500 or 600 or 700 or 800 is used, it must be completed", isEmpty).onSection("AB").onCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("mrtrn2", "406", "If ReversalTrnRefNumber has a value, the CardChargeBack must be Y", notEmpty.and(notMoneyFieldValue("CardChargeBack", "Y"))).onSection("E"),
            failure("mrtrn3", "285", "May not be used", notEmpty).onSection("F"),
            failure("mrtrn4", "219", "Additional spaces identified in data content", notEmpty.and(hasAdditionalSpaces)).onSection("ABG"),
            failure("mrtrn5", "408", "Unless CategoryCode 100 or 200 or 300 or 400 or 500 or 600 or 700 or 800 is used, this must not be completed", notEmpty).onSection("AB").notOnCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("mrtrn6", "S03", "ReversalTrnRefNumber must not have a value if the Category Code is not a Reversal Code", notEmpty.and(not(hasMoneyFieldValue("CategoryCode", ["100", "200", "300", "400", "500", "600", "700", "800"]))))
          ]
        },
        {
          field: "ReversalTrnSeqNumber", 
          minLen: "1", 
          maxLen: "3", 
          rules: [
            failure("mrtrs1", "408", "If CategoryCode 100 or 200 or 300 or 400 or 500 or 600 or 700 or 800 is used, it must be completed", isEmpty).onSection("AB").onCategory(["100", "200", "300", "400", "500", "600", "700", "800"]),
            failure("mrtrs2", "409", "If the ReversalTrnRefNumber has a value, it must be completed", isEmpty.and(hasMoneyField("ReversalTrnRefNumber")).or(notEmpty.and(notMoneyField("ReversalTrnRefNumber")))).onSection("E"),
            failure("mrtrs3", "285", "May not be used", notEmpty).onSection("F"),
            failure("mrtrs4", "S02", "ReversalTrnSeqNumber must not have a value if the Category Code is not a Reversal Code", notEmpty.and(not(hasMoneyFieldValue("CategoryCode", ["100", "200", "300", "400", "500", "600", "700", "800"]))))
          ]
        },
        {
          field: "BOPDIRTrnReference", 
          minLen: "1", 
          maxLen: "30", 
          rules: [
            failure("mdirtr1", "413", "Must not be completed", notEmpty).onSection("BCD")
          ]
        },
        {
          field: "BOPDIR{{DealerPrefix}}Code", 
          len: "3", 
          rules: [
            failure("mdircd1", "415", "If the Reporting Entity Code is not 304 or 305 it may not be completed", notEmpty).onSection("ABCDEF"),
            failure("mdircd2", "415", "Must not be completed", notEmpty).onSection("BCDE")
          ]
        },
        {
          field: "ThirdParty.Individual", 
          rules: [
            failure("mtpi1", "292", "If Flow is OUT and category is 511/01 to 511/07 and Subject is SDA and Resident Entity is used, Third Party Individual details must be provided", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onOutflow().onCategory("511").onSection("A")
          ]
        },
        {
          field: "ThirdParty.Individual.Surname", 
          minLen: "1", 
          maxLen: "35", 
          rules: [
            failure("mtpisn1", "416", "If CategoryCode 512/01 to 512/07 or 513 is used and Flow is OUT in cases where the Resident Entity element is completed, it must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onOutflow().onSection("AB").onCategory(["512", "513"]),
            failure("mtpisn2", "416", "If IndividualThirdPartyName contains a value, the IndividualThirdPartySurname must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Name"))).onSection("AB"),
            failure("mtpisn3", "416", "If the category is 256 and the PassportNumber under Individual ResidentCustomerAccountHolder contains no value, IndividualThirdPartySurname must be completed.", isEmpty.and(notTransactionField("Resident.Individual.PassportNumber"))).onSection("AB").onCategory("256"),
            failure("mtpisn4", "416", "If the category is 255 or 256 and the EntityCustomer element is completed, IndividualThirdPartySurname must be completed.", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory(["255", "256"]),
            failure("mtpisn5", "417", "Must not be equal to EntityCustomer LegalEntityName", notEmpty.and(matchesTransactionField("Resident.Entity.EntityName"))).onSection("ABE"),
            failure("mtpisn6", "416", "If the SupplementaryCardIndicator is Y, it must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y"))).onSection("E"),
            failure("mtpisn7", "418", "Must not be completed", notEmpty).onSection("CD"),
            failure("mtpisn8", "416", "If the Flow is IN and category 303, 304, 305, 306, 416 or 417 is used and Resident Entity is completed, then must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("AB").onCategory(["303", "304", "305", "306", "416", "417"]),
            failure("mtpisn9", "416", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("A").onCategory(["511", "516"]),
            failure("mtpisn10", "418", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty Surname must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("mtpisn11", "564", "If the Subject is SDA and the EntityCustomer element is completed in respect of any category the ThirdParty Individual Surname must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onSection("AB")
          ]
        },
        {
          field: "ThirdParty.Individual.Name", 
          minLen: "1", 
          maxLen: "35", 
          rules: [
            failure("mtpinm1", "419", "If IndividualThirdPartySurname contains a value, the IndividualThirdPartyName must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname"))).onSection("AB"),
            failure("mtpinm2", "419", "If the category is 256 and the PassportNumber under IndividualCustomer contains no value, IndividualThirdPartyName must be completed.", isEmpty.and(notTransactionField("Resident.Individual.PassportNumber"))).onSection("AB").onCategory("256"),
            failure("mtpinm3", "419", "If the category is 255 or 256 and the EntityCustomer element is completed, IndividualThirdPartyName must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory(["255", "256"]),
            failure("mtpinm4", "417", "Must not be equal to LegalEntityName", notEmpty.and(matchesTransactionField("Resident.Entity.EntityName"))).onSection("ABE"),
            failure("mtpinm5", "420", "Must not be completed", notEmpty).onSection("CDF"),
            failure("mtpinm6", "419", "If the SupplementaryCardIndicator is Y, it must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y"))).onSection("E"),
            failure("mtpinm7", "420", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty Name must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.Individual.Gender", 
          len: "1", 
          rules: [
            failure("mtpig1", "421", "If IndividualThirdPartySurname contains a value, the IndividualThirdPartyGender must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname"))).onSection("AB"),
            failure("mtpig2", "422", "Invalid gender", notEmpty.and(notValueIn(["M", "F"]))).onSection("ABEG"),
            failure("mtpig3", "421", "If the category is 256 and the PassportNumber under IndividualCustomer contains no value, IndividualThirdPartyGender must be completed.", isEmpty.and(notTransactionField("Resident.Individual.PassportNumber"))).onSection("AB").onCategory("256"),
            failure("mtpig4", "421", "If the category is 255 and the EntityCustomer element is completed, IndividualThirdPartyGender must be completed.", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory("255"),
            failure("mtpig5", "423", "Must not be completed", notEmpty).onSection("CDF"),
            failure("mtpig6", "421", "If the SupplementaryCardIndicator is Y, IndividualThirdPartyGender must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y"))).onSection("E"),
            failure("mtpig7", "423", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty Gender must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            warning("mtpig8", "S11", "The gender should match the ID Number", notEmpty.and(hasMoneyField("ThirdParty.Individual.IDNumber").and(notMatchesGenderToIDNumber("ThirdParty.Individual.IDNumber")))).onSection("ABEG")
          ]
        },
        {
          field: "ThirdParty.Individual.IDNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mtpiid1", "424", "If IndividualThirdPartySurname contains a value, either IndividualThirdPartyIDNumber or IndividualThirdPartyTempResPermitNumber must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname")).and(notMoneyField("ThirdParty.Individual.TempResPermitNumber"))).onSection("AB"),
            failure("mtpiid2", "425", "If category 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onOutflow().onSection("AB").onCategory(["512", "513"]),
            failure("mtpiid3", "426", "This is an Invalid ID number (Note, if the ID number does not comply to the algorithm, the Subject must be INVALIDIDNUMBER to accept an invalid ID number)", notEmpty.and(notValidRSAID).and(notMoneyFieldValue("AdHocRequirement.Subject", "INVALIDIDNUMBER"))).onSection("ABEG"),
            failure("mtpiid4", "427", "Must not be completed", notEmpty).onSection("CDF"),
            failure("mtpiid5", "424", "If the SupplementaryCardIndicator is Y, either IndividualThirdPartyIDNumber or IndividualThirdPartyTempResPermit Number must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y")).and(notMoneyField("ThirdParty.Individual.TempResPermitNumber"))).onSection("E"),
            failure("mtpiid6", "553", "Must not be equal to IDNumber under Resident IndividualCustomer", notEmpty.and(matchesTransactionField("Resident.Individual.IDNumber"))).onSection("ABEG"),
            failure("mtpiid7", "427", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty IDNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("mtpiid8", "564", "If the Subject is SDA and the EntityCustomer element is completed in respect of any category the ThirdParty IDNumber must be completed", isEmpty.and(hasTransactionField("Resident.Entity")).and(hasMoneyFieldValue("AdHocRequirement.Subject", "SDA"))).onSection("AB"),
            failure("mtpiid9", "425", "If category or 511/01 to 511/07 or 516 is used and flow is IN in cases where the Entity Customer element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("AB").onCategory(["511", "516"])
          ]
        },
        {
          field: "ThirdParty.Individual.DateOfBirth", 
          rules: [
            failure("mtpibd1", "428", "If IndividualThirdPartySurname contains a value, the IndividualThirdPartyDateOfBirth must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname"))).onSection("AB"),
            failure("mtpibd2", "428", "If the category is 256 and the PassportNumber under Resident Individual contains no value, this must be completed", isEmpty.and(notTransactionField("Resident.Individual.PassportNumber"))).onSection("AB").onCategory("256"),
            failure("mtpibd3", "428", "If the category is 255 and the Resident Entity is provided, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory("255"),
            failure("mtpibd4", "429", "Must not be completed", notEmpty).onSection("CDF"),
            failure("mtpibd5", "428", "If the SupplementaryCardIndicator is Y, IndividualThirdPartyDateOfBirth must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y"))).onSection("E"),
            failure("mtpibd6", "428", "Must be in a valid date format: YYYY-MM-DD", notEmpty.and(notPattern(/^(19|20)\d{2}-(0\d|10|11|12)-(0[1-9]|1\d|2\d|3[01])$/).or(isDaysInFuture("0")))).onSection("ABEG"),
            failure("mtpibd7", "429", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty DateOfBirth must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            warning("mtpibd8", "S11", "The date of birth should match the ID Number", notEmpty.and(hasMoneyField("ThirdParty.Individual.IDNumber").and(notMatchThirdPartyDateToIDNumber))).onSection("ABEG")
          ]
        },
        {
          field: "ThirdParty.Individual.TempResPermitNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mtpitp1", "424", "If IndividualThirdPartySurname contains a value, either IndividualThirdPartyIDNumber or IndividualThirdPartyTempResPermit Number must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname")).and(notMoneyField("ThirdParty.Individual.IDNumber"))).onSection("AB"),
            failure("mtpitp2", "299", "If category is 511/01 to 511/08 or 512/01 to 512/07 or 513 or 516 is used, this may not be completed", notEmpty).onSection("AB").onCategory(["511", "512", "513", "516"]),
            failure("mtpitp3", "424", "If the SupplementaryCardIndicator is Y, either IndividualThirdPartyIDNumber or IndividualThirdPartyTempResPermit Number must be completed", isEmpty.and(hasResidentFieldValue("SupplementaryCardIndicator", "Y")).and(notMoneyField("ThirdParty.Individual.IDNumber"))).onSection("E"),
            failure("mtpitp4", "430", "May not be completed", notEmpty).onSection("CDF"),
            failure("mtpitp5", "554", "Must not be equal to TempResPermitNumber under Resident IndividualCustomer", notEmpty.and(matchesTransactionField("Resident.Individual.TempResPermitNumber"))).onSection("ABEG"),
            failure("mtpitp6", "430", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty TempResPermitNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("mtpitp7", "282", "If IndividualThirdPartySurname contains a value and the BoPCategory is 250 and the Flow is OUT, either IndividualThirdPartyTempResPermit Number or IndividualThirdPartyPassportNumber must be completed. (This rule caters for non-residents, but excluding contract workers, traveling o.b.o. of a SA entity)", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname")).and(notMoneyField("ThirdParty.Individual.PassportNumber"))).onOutflow().onCategory("250").onSection("AB")
          ]
        },
        {
          field: "ThirdParty.Individual.PassportNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mtpipn1", "431", "If the BoPCategory is 256 and the PassportNumber under IndividualCustomer contains no value, IndividualThirdPartyPassportNumber must be completed.", isEmpty.and(notTransactionField("Resident.Individual.PassportNumber"))).onSection("AB").onCategory("256"),
            failure("mtpipn2", "431", "If the category is 255 and the EntityCustomer element is completed, IndividualThirdPartyPassportNumber must be completed.", isEmpty.and(hasTransactionField("Resident.Entity"))).onSection("AB").onCategory("255"),
            failure("mtpipn3", "432", "Must not be completed", notEmpty).onSection("CDEFG"),
            failure("mtpipn4", "555", "Must not be equal to PassportNumber under Resident IndividualCustomer", notEmpty.and(matchesTransactionField("Resident.Individual.PassportNumber"))).onSection("AB"),
            failure("mtpipn5", "432", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty PassportNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("mtpipn6", "282", "If Individual ThirdParty Surname contains a value and the category is 250 and the Flow is OUT, either ThirdParty TempResPermit Number or ThirdParty PassportNumber must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname")).and(notMoneyField("ThirdParty.Individual.TempResPermitNumber"))).onOutflow().onCategory("250").onSection("AB")
          ]
        },
        {
          field: "ThirdParty.Individual.PassportCountry", 
          len: "2", 
          rules: [
            failure("mtpipc1", "433", "If the IndividualThirdPartyPassportNumber contains a value, IndividualThirdPartyPassportCountry must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.PassportNumber"))).onSection("AB"),
            failure("mtpipc2", "434", "Must not be completed", notEmpty).onSection("CDEFG"),
            failure("mtpipc3", "238", "Invalid country code", notEmpty.and(hasInvalidSWIFTCountry)).onSection("AB"),
            failure("mtpipc4", "434", "If the Subject is REMITTANCE DISPENSATION the Individual ThirdParty PassportCountry must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.Entity.Name", 
          minLen: "1", 
          maxLen: "50", 
          rules: [
            failure("mtpenm2", "435", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpenm3", "435", "If the Subject is REMITTANCE DISPENSATION the Entity ThirdParty Name must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.Entity.RegistrationNumber", 
          minLen: "2", 
          maxLen: "30", 
          rules: [
            failure("mtpern1", "436", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpern2", "556", "Must not be equal to RegistrationNumber under Resident EntityCustomer", notEmpty.and(matchesTransactionField("Resident.Entity.RegistrationNumber"))).onSection("ABG"),
            failure("mtpern3", "436", "If the Subject is REMITTANCE DISPENSATION the Entity ThirdParty RegistrationNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.CustomsClientNumber", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpccn1", "322", "CustomsClientNumber must be numeric and contain between 8 and 13 digits", notEmpty.and(notPattern(/^\d{8,13}$/))).onSection("ABG"),
            failure("mtpccn2", "322", "The value 70707070 implies an unknown customs client number", notEmpty.and(hasValue("70707070"))).onSection("ABG"),
            failure("mtpccn3", "321", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpccn4", "321", "If the Subject is REMITTANCE DISPENSATION the Entity ThirdParty CustomsClientNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            warning("mtpccn5", "322", "CustomsClientNumber should pass one of the validations for either CCN, ID Number or Tax Number", notEmpty.and(hasPattern(/^\d{8,13}$/)).and(not(isValidCCN.or(isValidRSAID.or(isValidZATaxNumber)))))
          ]
        },
        {
          field: "ThirdParty.TaxNumber", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("mtptx1", "439", "If category 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onOutflow().onSection("AB").onCategory(["512", "513"]),
            failure("mtptx2", "438", "Must not be completed", notEmpty).onSection("CD"),
            failure("mtptx3", "557", "Must not be equal to TaxNumber under AdditionalCustomerData", notEmpty.and(matchesTransactionField("Resident.Individual.TaxNumber").or(matchesTransactionField("Resident.Entity.TaxNumber")))).onSection("ABG"),
            failure("mtptx4", "439", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("AB").onCategory(["511", "516"]),
            failure("mtptx5", "438", "If the Subject is REMITTANCE DISPENSATION the Entity ThirdParty TaxNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.VATNumber", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mtpvn1", "569", "Must not be completed", notEmpty).onSection("CD"),
            failure("mtpvn2", "558", "Must not be equal to VATNumber under Resident", notEmpty.and(matchesTransactionField("Resident.Individual.VATNumber").or(matchesTransactionField("Resident.Entity.VATNumber")))).onSection("ABEG"),
            failure("mtpvn3", "569", "If the Subject is REMITTANCE DISPENSATION the Entity ThirdParty VATNumber must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.AddressLine1", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("mtpsal11", "456", "If category 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("mtpsal12", "441", "Must not be completed", notEmpty).onSection("CD"),
            failure("mtpsal13", "456", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("A").onCategory(["511", "516"]),
            failure("mtpsal14", "441", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street AddressLine1 must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.AddressLine2", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("mtpsal21", "442", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpsal22", "442", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street AddressLine2 must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.Suburb", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpsas1", "458", "If ThirdParty StreetAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.StreetAddress.AddressLine1"))).onSection("AB"),
            failure("mtpsas2", "443", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpsas3", "443", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street Suburb must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.City", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpsac1", "459", "If ThirdParty StreetAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.StreetAddress.AddressLine1"))).onSection("AB"),
            failure("mtpsac2", "444", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpsac3", "444", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street City must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.Province", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpsap1", "485", "If ThirdParty StreetAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.StreetAddress.AddressLine1"))).onSection("AB"),
            failure("mtpsap2", "445", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpsap3", "336", "Must be a valid province", notEmpty.and(notValidProvince)).onSection("ABG"),
            failure("mtpsap4", "445", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street Province must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.StreetAddress.PostalCode", 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("mtpsaz1", "501", "If ThirdParty StreetAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.StreetAddress.AddressLine1"))).onSection("AB"),
            failure("mtpsaz2", "449", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpsaz3", "338", "Invalid Postal Code", notEmpty.and(notValidPostalCode)).onSection("AB"),
            failure("mtpsaz4", "449", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Street PostalCode must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.AddressLine1", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("mtppal11", "512", "If category 512/01 to 512/07 or 513 is used and flow is OUT in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onOutflow().onSection("A").onCategory(["512", "513"]),
            failure("mtppal12", "450", "Must not be completed", notEmpty).onSection("CD"),
            failure("mtppal13", "512", "If CategoryCode 511/01 to 511/07 or 516 is used and Flow is IN in cases where the Resident Entity element is completed, this must be completed", isEmpty.and(hasTransactionField("Resident.Entity"))).onInflow().onSection("A").onCategory(["511", "516"]),
            failure("mtppal14", "450", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal AddressLine1 must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.AddressLine2", 
          minLen: "2", 
          maxLen: "70", 
          rules: [
            failure("mtppal21", "451", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtppal22", "451", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal AddressLine2 must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.Suburb", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtppas1", "534", "If ThirdParty PostalAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.PostalAddress.AddressLine1"))).onSection("AB"),
            failure("mtppas2", "453", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtppas3", "453", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal Suburb must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.City", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtppac1", "535", "If ThirdParty PostalAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.PostalAddress.AddressLine1"))).onSection("AB"),
            failure("mtppac2", "455", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtppac3", "455", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal City must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.Province", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtppap1", "536", "If ThirdParty PostalAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.PostalAddress.AddressLine1"))).onSection("AB"),
            failure("mtppap2", "457", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtppap3", "336", "Must be a valid province", notEmpty.and(notValidProvince)).onSection("ABG"),
            failure("mtppap4", "457", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal Province must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.PostalAddress.PostalCode", 
          minLen: "2", 
          maxLen: "10", 
          rules: [
            failure("mtppaz1", "537", "If ThirdParty PostalAddress Line1 has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.PostalAddress.AddressLine1"))).onSection("AB"),
            failure("mtppaz2", "262", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtppaz3", "338", "Invalid Postal Code", notEmpty.and(notValidPostalCode)).onSection("ABG"),
            failure("mtppaz4", "262", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Postal Code must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.ContactDetails.ContactSurname", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpcds1", "460", "If Individual ThirdParty Surname or Entity ThirdParty Name has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname").or(hasMoneyField("ThirdParty.Entity.Name")))).onSection("ABG"),
            failure("mtpcds2", "461", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpcds3", "461", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Contact Surname must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.ContactDetails.ContactName", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("mtpcdn1", "462", "If Individual ThirdParty Surname or Entity ThirdParty Name has a value, this must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname").or(hasMoneyField("ThirdParty.Entity.Name")))).onSection("ABG"),
            failure("mtpcdn2", "463", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpcdn3", "463", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Contact Name must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.ContactDetails.Email", 
          minLen: "2", 
          maxLen: "120", 
          rules: [
            failure("mtpcde1", "464", "If Individual ThirdParty Surname or Entity ThirdParty Name has a value, either this or ThirdParty Fax or ThirdParty Telephone be must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname").or(hasMoneyField("ThirdParty.Entity.Name"))).and(notMoneyField("ThirdParty.ContactDetails.Fax")).and(notMoneyField("ThirdParty.ContactDetails.Telephone"))).onSection("ABG"),
            failure("mtpcde2", "465", "May not be completed", notEmpty).onSection("CD"),
            failure("mtpcde3", "465", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Contact Email must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A"),
            failure("cnte4", "E01", "This is not a valid email address", notEmpty.and(notValidEmail))
          ]
        },
        {
          field: "ThirdParty.ContactDetails.Fax", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("mtpcdf1", "464", "If Individual ThirdParty Surname or Entity ThirdParty Name has a value, either this or ThirdParty Email or ThirdParty Telephone be must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname").or(hasMoneyField("ThirdParty.Entity.Name"))).and(notMoneyField("ThirdParty.ContactDetails.Email")).and(notMoneyField("ThirdParty.ContactDetails.Telephone"))).onSection("ABG"),
            failure("mtpcdf2", "465", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpcdf3", "465", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Contact Fax must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "ThirdParty.ContactDetails.Telephone", 
          minLen: "2", 
          maxLen: "15", 
          rules: [
            failure("mtpcdt1", "464", "If Individual ThirdParty Surname or Entity ThirdParty Name has a value, either this or ThirdParty Fax or ThirdParty Email be must be completed", isEmpty.and(hasMoneyField("ThirdParty.Individual.Surname").or(hasMoneyField("ThirdParty.Entity.Name"))).and(notMoneyField("ThirdParty.ContactDetails.Fax")).and(notMoneyField("ThirdParty.ContactDetails.Email"))).onSection("ABG"),
            failure("mtpcdt2", "465", "Must not be completed", notEmpty).onSection("CDE"),
            failure("mtpcdt3", "465", "If the Subject is REMITTANCE DISPENSATION the ThirdParty Contact Telephone must not be completed", notEmpty.and(hasMoneyFieldValue("AdHocRequirement.Subject", "REMITTANCE DISPENSATION"))).onSection("A")
          ]
        },
        {
          field: "CardChargeBack", 
          rules: [
            failure("mcrdcb1", "466", "Must not be completed", notEmpty).onSection("ABCDFG"),
            failure("mcrdcb2", "477", "Must be set to either Y or N (blank assumed as N)", notEmpty.and(notValueIn(["Y", "N"]))).onSection("E"),
            failure("mcrdcb3", "383", "If CardChargeBack is Y, the Flow must be IN or the Non Resident Account Identifier must be VISA NET or MASTER SEND", notEmpty.and(hasValue("Y")).and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onOutflow().onSection("E")
          ]
        },
        {
          field: "CardIndicator", 
          minLen: "2", 
          maxLen: "20", 
          rules: [
            failure("mcrdci1", "478", "Must not be completed", notEmpty).onSection("ABCDG"),
            failure("mcrdci2", "479", "Must contain AMEX or DINERS or ELECTRON or MAESTRO or MASTER or VISA or BOCEXPRESS", notValidCardType).onSection("EF")
          ]
        },
        {
          field: "ElectronicCommerceIndicator", 
          minLen: "1", 
          maxLen: "2", 
          rules: [
            failure("mcrdec1", "538", "Must be completed except if the Non Resident AccountIdentifier is VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E"),
            failure("mcrdec2", "480", "Must not be completed", notEmpty).onSection("ABCDFG"),
            failure("mcrdec3", "539", "The {{Regulator}} mandates that only certain codes are applicable", notEmpty.and(notValidECI)).onSection("E")
          ]
        },
        {
          field: "POSEntryMode", 
          len: "2", 
          rules: [
            failure("mcrdem1", "480", "Must not be completed", notEmpty).onSection("ABCDFG"),
            failure("mcrdem2", "481", "Must be completed except if the Non Resident AccountIdentifier is VISA NET or MASTER SEND", isEmpty.and(notNonResidentFieldValue("AccountIdentifier", ["VISA NET", "MASTER SEND"]))).onSection("E"),
            failure("mcrdem3", "482", "The {{Regulator}} mandates that only certain codes are applicable", notEmpty.and(notValidPOSEntryMode)).onSection("E"),
            warning("mcrdem4", "364", "This transaction is regarded as online foreign lottery and gambling", notEmpty.and(isForeignGambling)).onSection("E")
          ]
        },
        {
          field: "CardFraudulentTransactionIndicator", 
          rules: [
            failure("mcrdft1", "483", "Must not be completed", notEmpty).onSection("ABCDFG"),
            failure("mcrdft2", "484", "Must contain a value Y or N", isEmpty.or(notValueIn(["Y", "N"]))).onSection("E")
          ]
        },
        {
          field: "ForeignCardHoldersPurchases{{LocalValue}}", 
          rules: [
            failure("mcrdfp1", "486", "Must not be completed", notEmpty).onSection("ABCDEG"),
            failure("mcrdfp2", "487", "Must be completed", isEmpty).onSection("F"),
            failure("mcrdfp3", "352", "Must not contain a negative value", notEmpty.and(isNegative)).onSection("F")
          ]
        },
        {
          field: "ForeignCardHoldersCashWithdrawals{{LocalValue}}", 
          rules: [
            failure("mcrdfw1", "488", "Must not be completed", notEmpty).onSection("ABCDEG"),
            failure("mcrdfw2", "489", "Must be completed", isEmpty).onSection("F"),
            failure("mcrdfw3", "352", "Must not contain a negative value", notEmpty.and(isNegative)).onSection("F")
          ]
        },
        {
          field: "ImportExport", 
          rules: [
            failure("mtie1", "490", "If Inflow and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, the ImportExport element must be completed", emptyImportExport.and(not(hasMoneyFieldValue("CategoryCode", "106").and(hasMoneyField("ThirdParty.CustomsClientNumber").and(evalMoneyField("ThirdParty.CustomsClientNumber", isInLookup("luClientCCNs", "ccn"))))).and(not(hasMoneyFieldValue("CategoryCode", "106").and(not(hasMoneyField("ThirdParty.CustomsClientNumber")).and(evalResidentField("CustomsClientNumber", isInLookup("luClientCCNs", "ccn")))))))).onInflow().onSection("AB").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"]),
            failure("mtie2", "491", "ImportExportData Element must not be completed", notEmptyImportExport).onSection("CD"),
            failure("mtie3", "529", "Total PaymentValue of all ImportExport entries may not exceed a 1% variance with the {{LocalValue}} or ForeignValue", notChecksumImportExport).onSection("ABG"),
            failure("mtie4", "490", "If outflow and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, the ImportExport element must be completed unless a) the Subject is SDA or REMITTANCE DISPENSATION or b) category 106 and import undertaking client", emptyImportExport.and(notMoneyFieldValue("AdHocRequirement.Subject", ["SDA", "REMITTANCE DISPENSATION"]).and(not(hasMoneyFieldValue("CategoryCode", "106").and(hasMoneyField("ThirdParty.CustomsClientNumber").and(evalMoneyField("ThirdParty.CustomsClientNumber", isInLookup("luClientCCNs", "ccn"))))).and(not(hasMoneyFieldValue("CategoryCode", "106").and(not(hasMoneyField("ThirdParty.CustomsClientNumber")).and(evalResidentField("CustomsClientNumber", isInLookup("luClientCCNs", "ccn"))))))))).onOutflow().onSection("A").onCategory(["101", "103", "105", "106"]).notOnCategory(["101/11", "103/11"]),
            failure("mtie5", "491", "For any category other than 101/01 to 101/11 or 103/01 to 103/11 or 105 or 106, the ImportExport element must not be completed", notEmptyImportExport).onSection("ABG").notOnCategory(["101", "103", "105", "106"]),
            failure("mtie6", "494", "Must contain a sequential SubSequence entries that must start with the value 1", notValidSubSequenceNumbers)
          ]
        }
      ]
    };
    v28 = {
      ruleset: "Standard Import/Export Rules", 
      scope: "importexport", 
      validations: [
        {
          field: "ImportControlNumber", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("ieicn1", "495", "Must be completed if the Flow is OUT and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, unless the Subject is SDA or REMITTANCE DISPENSATION", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", ["SDA", "REMITTANCE DISPENSATION"]))).onOutflow().onSection("A").notOnCategory("103/11").onCategory(["101", "103", "105", "106"]),
            failure("ieicn2", "496", "For any category other than 101/01 to 101/11 or 103/01 to 103/11 or 105 or 106, the ImportControlNumber must not be completed.", notEmpty).onSection("ABG").notOnCategory(["101", "103", "105", "106"]),
            failure("ieicn3", "499", "If the Flow is OUT and the category is 101/01 to 101/10, the first 3 characters must be INV followed by the invoice number. The minimum total number of characters must be 4", notPattern(/^INV.+$/)).onOutflow().onSection("ABG").onCategory("101").notOnCategory("101/11"),
            failure("ieicn4", "499", "If the Flow is OUT and the category is 103/01 to 103/10 or 105 or 106, the format is as follows: AAACCYYMMDD0000000 where AAA is a valid customs office code in alpha format; CC is the century of import, YY is the year of import, MM is the month of import, DD is the day of import, and 0000000 is the 7 digit unique bill of entry number allocated by SARS as part of the MRN", notValidICN).onOutflow().onSection("ABG").notOnCategory("103/11").onCategory(["103", "105", "106"]),
            failure("ieicn5", "219", "Additional spaces identified in data content. May also not contain a comma (,)", notEmpty.and(hasAdditionalSpaces.or(hasPattern(/,/)))).onSection("ABG"),
            failure("ieicn6", "495", "Must be completed if the Flow is OUT and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106", isEmpty).onOutflow().onSection("BG").notOnCategory("103/11").onCategory(["101", "103", "105", "106"])
          ]
        },
        {
          field: "TransportDocumentNumber", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("ietdn1", "502", "Must be completed if the Flow is OUT and the category is 103/01 to 103/10, unless the Subject is SDA or REMITTANCE DISPENSATION", isEmpty.and(notMoneyFieldValue("AdHocRequirement.Subject", ["SDA", "REMITTANCE DISPENSATION"]))).onOutflow().notOnCategory("103/11").onCategory("103").onSection("ABG"),
            failure("ietdn2", "503", "For any category other than 103/01 to 103/11 or 105 or 106, the TransportDocumentNumber must not be completed.", notEmpty).onSection("ABG").notOnCategory(["103", "105", "106"]),
            failure("ietdn3", "502", "Must be completed if the Flow is OUT and the category is 103/01 to 103/10", isEmpty).onOutflow().notOnCategory("103/11").onCategory("103").onSection("BG"),
            failure("ietdn4", "503", "May NOT be completed if the CustomsClientNumber is registered as an Imports Undertaking entity and the category is 105 or 106", notEmpty.and(importUndertakingClient)).onCategory(["105", "106"]).onSection("ABG")
          ]
        },
        {
          field: "UCR", 
          minLen: "2", 
          maxLen: "35", 
          rules: [
            failure("ieucr1", "504", "If UCR contains a value and the Flow is IN, the minimum characters is 12 but not exceeding 35 characters", notEmpty.and(isTooShort("12").or(isTooLong("35")))).onInflow().onSection("ABG"),
            failure("ieucr2", "505", "Must be completed if the Flow is IN and the category is 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106", isEmpty).onSection("ABG").onInflow().notOnCategory(["101/11", "103/11"]).onCategory(["101", "103", "105", "106"]),
            failure("ieucr3", "506", "For any BoPCategory and SubBopCategory other than 101/01 to 101/10 or 103/01 to 103/10 or 105 or 106, the UCR must not be completed.", notEmpty).onSection("ABG").onInflow().notOnCategory(["101/01", "101/02", "101/03", "101/04", "101/05", "101/06", "101/07", "101/08", "101/09", "101/10", "103/01", "103/02", "103/03", "103/04", "103/05", "103/06", "103/07", "103/08", "103/09", "103/10", "105", "106"]),
            failure("ieucr4", "506", "UCR must not be completed", notEmpty).onSection("ABG").onOutflow()
          ]
        },
        {
          field: "PaymentCurrencyCode", 
          len: "3", 
          rules: [
            failure("iepcc1", "530", "Must be completed", isEmpty).onSection("ABG"),
            failure("iepcc2", "531", "PaymentCurrencyCode of all ImportExport entries is not {{LocalCurrency}} or does not match FlowCurrency", notEmpty.and(notValidImportExportCurrency)).onSection("ABG")
          ]
        },
        {
          field: "PaymentValue", 
          rules: [
            failure("iepv1", "507", "Must be completed and must not be 0.00 or 0", isEmpty).onSection("ABG")
          ]
        },
        {
          field: "MRNNotOnIVS", 
          rules: [
            failure("iemrn1", "203", "The value must only be Y or N", notEmpty.and(notValueIn(["Y", "N"]))).onSection("ABG")
          ]
        }
      ]
    };
  }
  return {
    comment: 'sbZABOLP (1,0) [featureMTAAccounts, featureLimit, featureEntity511] -> sbZAFlow (1,0) -> stdSARB (1,0) [featureHOLDCO, featureMTAAccounts, featureSchema, featureEntity511, featureBranchHub] -> coreSARBExternal (1,0) -> coreSARB (1,0) -> coreSADC (1,0)',
    engine: {major: '1', minor: '0'},
    rules: [v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28],
    mappings: {Locale: "ZA", LocalCurrencySymbol: "R", RegulatorPrefix: "CB", StateName: "Province", DealerPrefix: "RE", LocalValue: "DomesticValue", LocalCurrency: "ZAR", Regulator: "Regulator", LocalCurrencyName: "Local Currency", _maxLenErrorType: "ERROR", _lenErrorType: "ERROR", _minLenErrorType: "ERROR"}
  };
 };
})
