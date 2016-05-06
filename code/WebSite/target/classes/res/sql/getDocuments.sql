SELECT
	d."documentId",
	d."uploadedOn",
	d."uploadedBy",
	d.status,
	f."fileId",
	f.name,
	f.size,
	f."mimeType"
FROM "Document" d
INNER JOIN "File" f
ON d."fileId" = f."fileId"
ORDER BY "uploadedOn" desc
LIMIT 50
OFFSET ?