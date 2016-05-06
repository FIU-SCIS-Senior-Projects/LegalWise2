
UPDATE "User"
 SET 
	"firstName" = ?,
	"lastName" = ?,
	"companyName" = ?,
	email = ?,
	type = ?,
	"isTrial" = ?,
	"trialDuration" = ?,
	"isLocked" = ?,
	"modifiedOn" = now(),
	"isActive" = ?
WHERE
	"userId" = ?
	