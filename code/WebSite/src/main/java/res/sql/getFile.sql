SELECT
	"fileId",
	name,
	size,
	"mimeType"
FROM "File"
WHERE "fileId" = ?
LIMIT 1