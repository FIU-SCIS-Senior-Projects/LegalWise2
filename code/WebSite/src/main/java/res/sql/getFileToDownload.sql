SELECT
	"fileId",
	name,
	size,
	"mimeType",
	body
FROM "File"
WHERE "fileId" = ?
LIMIT 1