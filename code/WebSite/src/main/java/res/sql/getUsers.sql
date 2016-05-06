
SELECT 
	"userId",
	"firstName",
	"lastName",
	"companyName",
	email,
	type,
	"createdOn",
	"isTrial",
	"trialDuration",
	"isLocked",
	"modifiedOn",
	"isActive"
FROM "User"
WHERE "firstName" ILIKE ? 
OR "lastName" ILIKE ?
OR "companyName" ILIKE ?
OR "email" ILIKE ?
ORDER BY ?
LIMIT 50
OFFSET ?