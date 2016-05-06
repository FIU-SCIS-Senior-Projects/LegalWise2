SELECT 
	"historyId",
  	"searchText",
  	"performedOn"
FROM "History"
WHERE "userId" = ?
ORDER BY "performedOn" DESC
LIMIT 20