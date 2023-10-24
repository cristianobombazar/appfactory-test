-- 1. Create new table for domestic branch data sbg_bankBranch
CREATE TABLE `sbg_bankBranch` (
  `SWIFTCODE` text COLLATE utf8_unicode_ci,
  `BANKCODE` text COLLATE utf8_unicode_ci,
  `COUNTRYCODE` text COLLATE utf8_unicode_ci,
  `LOCATIONCODE` text COLLATE utf8_unicode_ci,
  `BRANCHCODE` text COLLATE utf8_unicode_ci,
  `INSTITUTIONNAME` text COLLATE utf8_unicode_ci,
  `BRANCHINFO` text COLLATE utf8_unicode_ci,
  `CITYNAME` text COLLATE utf8_unicode_ci,
  `SUBTYPE` text COLLATE utf8_unicode_ci,
  `VALUEADDEDSERVICES` text COLLATE utf8_unicode_ci,
  `EXTRAINFO` text COLLATE utf8_unicode_ci,
  `PHYSICALADDRESS` text COLLATE utf8_unicode_ci,
  `LOCATION` text COLLATE utf8_unicode_ci,
  `COUNTRYNAME` text COLLATE utf8_unicode_ci,
  `POBDETAILS` text COLLATE utf8_unicode_ci,
  `POBCOUNTRYNAME` text COLLATE utf8_unicode_ci,
  `VERSION` int(11) DEFAULT NULL,
  `WHENMODIFIED` text COLLATE utf8_unicode_ci,
  `STATUS` text COLLATE utf8_unicode_ci,
  `PROCESSING` text COLLATE utf8_unicode_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 2. ingest bank branch data from CSV file
LOAD DATA LOCAL INFILE "<path\\to\\file>\\CIM900FileFinal-17032023.csv" INTO TABLE dbxdb.sbg_bankBranch
FIELDS TERMINATED BY ';' 
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(BRANCHCODE, BRANCHINFO, INSTITUTIONNAME, PHYSICALADDRESS, COUNTRYNAME, SWIFTCODE, CITYNAME);

-- 3. update bank branch data country code to ZA
UPDATE dbxdb.sbg_bankBranch SET COUNTRYCODE = 'ZA';

-- 4. empty out sbg_swiftcode to remove domestic bank branch
TRUNCATE table dbxdb.sbg_swiftcode;

--5. ingest swiftcode data from CSV file
LOAD DATA LOCAL INFILE "<path\\to\\file>\\bic_bank_directories.csv" INTO TABLE dbxdb.sbg_swiftcode
FIELDS TERMINATED BY ';' 
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(SWIFTCODE, INSTITUTIONNAME, CITYNAME, BRANCHINFO, STATUS, COUNTRYNAME, PHYSICALADDRESS);

