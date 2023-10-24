CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `readwrite`@`%` 
    SQL SECURITY DEFINER
VIEW `interbanktransfer_view` AS
    SELECT 
        `interbankfundtransfers`.`confirmationNumber` AS `referenceId`,
        `interbankfundtransfers`.`scheduledDate` AS `scheduledDate`,
        `interbankfundtransfers`.`payeeName` AS `beneficiaryName`,
        `interbankfundtransfers`.`bankName` AS `bankName`,
        `interbankfundtransfers`.`bicCode` AS `bicCode`,
        `interbankfundtransfersRefData`.`benerefeno` AS `beneficiaryReference`,
        `interbankfundtransfers`.`transactionAmount` AS `amount`,
        `interbankfundtransfers`.`transactionCurrency` AS `transactionCurrency`,
        `interbankfundtransfers`.`toAccountNumber` AS `toAccountNumber`,
		`interbankfundtransfersRefData`.`fromAccountName` AS `payeeName`

    FROM
        (`interbankfundtransfers`
        JOIN `interbankfundtransfersRefData`)
    WHERE
        (`interbankfundtransfers`.`confirmationNumber` = (`interbankfundtransfersRefData`.`confirmationNumber` COLLATE utf8_general_ci))