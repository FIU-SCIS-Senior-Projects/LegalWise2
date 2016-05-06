SELECT
	d."documentId",
	d."uploadedOn",
	d."uploadedBy",
	d."fileId",
	d."plainText",
	d.status,
	f."fileId",
	f.name,
	f.size,
	f."mimeType"
FROM "Document" d
INNER JOIN "File" f
ON d."fileId" = f."fileId"
AND d."documentId" IN ?