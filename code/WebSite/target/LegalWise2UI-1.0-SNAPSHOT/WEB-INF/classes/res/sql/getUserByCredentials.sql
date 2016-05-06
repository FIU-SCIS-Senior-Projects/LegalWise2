
SELECT 
	"userId",
	"firstName",
	"lastName",
	"companyName",
	"email",
	type,
	"createdOn",
	"isTrial",
	"trialDuration",
	"modifiedOn"
FROM "User" 
WHERE "email" = ?
AND "password" = ? 
AND "isActive" = true
AND "isLocked" = false
LIMIT 1