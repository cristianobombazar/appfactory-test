
DROP PROCEDURE IF EXISTS `dbxdb`.`fetch_signatory_eligible_users_proc`;
DELIMITER $$
CREATE PROCEDURE `dbxdb`.`fetch_signatory_eligible_users_proc`(
    IN _coreCustomerId VARCHAR(64) CHARACTER SET UTF8 COLLATE utf8_general_ci,
    IN _contractId VARCHAR(128) CHARACTER SET UTF8 COLLATE utf8_general_ci
)
BEGIN
    SELECT DISTINCT
        `customer`.`id` AS `userId`,
        `customer`.`isCombinedUser` AS `isCombinedUser`,
        `customer`.`UserName` AS `userName`,
        `customer`.`FirstName` AS `firstName`,
        `customer`.`LastName` AS `lastName`,
        `membergroup`.`Name` AS `role`
    FROM `dbxdb`.`customer`
    LEFT JOIN `dbxdb`.`customergroup` ON (`customergroup`.`Customer_id` = `customer`.`id`)
    LEFT JOIN `dbxdb`.`membergroup` ON (`membergroup`.`id` = `customergroup`.`Group_id` )
        WHERE `customergroup`.`coreCustomerId`=_coreCustomerId  
        AND `customer`.`id` in (SELECT DISTINCT `customeraction`.`Customer_id` FROM `dbxdb`.`customeraction`
            WHERE `customeraction`.`contractId`=_contractId 
            AND `customeraction`.`coreCustomerId`=_coreCustomerId AND `customeraction`.`softdeleteflag` = '0'
            AND `customeraction`.`Action_id` IN ('BILL_PAY_APPROVE','DOMESTIC_WIRE_TRANSFER_APPROVE','BILL_PAY_SELF_APPROVAL','ACH_COLLECTION_APPROVE','ACH_FILE_APPROVE','ACH_FILE_SELF_APPROVAL','ACH_PAYMENT_APPROVE','ACH_PAYMENT_SELF_APPROVAL','BULK_PAYMENT_REQUEST_APPROVE',
                        'DOMESTIC_WIRE_TRANSFER_SELF_APPROVAL','INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE','INTERNATIONAL_WIRE_TRANSFER_APPROVE','INTERNATIONAL_WIRE_TRANSFER_SELF_APPROVAL','INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE','INTRA_BANK_FUND_TRANSFER_APPROVE',
                        'INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE','INTRA_BANK_FUND_TRANSFER_SELF_APPROVAL','P2P_SELF_APPROVAL','TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE','TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE','TRANSFER_BETWEEN_OWN_ACCOUNT_SELF_APPROVAL','ACH_COLLECTION_SELF_APPROVAL',
                        'CHEQUE_BOOK_REQUEST_APPROVE','INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE','INTERNATIONAL_ACCOUNT_FUND_TRANSFER_SELF_APPROVAL','INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE','INTER_BANK_ACCOUNT_FUND_TRANSFER_SELF_APPROVAL','P2P_APPROVE')
            AND `customeraction`.`Customer_id` NOT IN 
                (SELECT `customersignatorygroup`.`customerId` FROM `dbxdb`.`customersignatorygroup` WHERE `customersignatorygroup`.`softdeleteflag` = '0' AND `customersignatorygroup`.`signatoryGroupId` IN 
                    (SELECT `signatorygroup`.`signatoryGroupId` FROM `dbxdb`.`signatorygroup` WHERE `signatorygroup`.`softdeleteflag` = '0' AND `signatorygroup`.`contractId`=_contractId AND `signatorygroup`.`coreCustomerId`=_coreCustomerId)))
            ORDER BY `customer`.`id`;
END$$
DELIMITER ;
