CREATE TABLE "User"
(
  "userId" serial NOT NULL,
  "firstName" text,
  "lastName" text,
  "companyName" text,
  email text,
  type text,
  "createdOn" timestamp with time zone,
  "isTrial" boolean,
  "trialDuration" integer,
  password text,
  "isLocked" boolean,
  "modifiedOn" timestamp with time zone,  
  "isActive" boolean,
  CONSTRAINT "userId" PRIMARY KEY ("userId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "User"
  OWNER TO cqamtiza;
  
CREATE TABLE "Settings"
(
	name text NOT NULL,
	type text,
	value text,
	CONSTRAINT "settingName" PRIMARY KEY (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "Settings"
  OWNER TO cqamtiza;
  
CREATE TABLE "File"
(
  "fileId" serial NOT NULL,
  name text,
  size bigint,
  "mimeType" text,
  body bytea,
  CONSTRAINT "fileId" PRIMARY KEY ("fileId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "File"
  OWNER TO cqamtiza; 
  
CREATE TABLE "Document"
(
  "documentId" serial NOT NULL,
  "uploadedOn" timestamp with time zone,
  "uploadedBy" integer,
  "fileId" integer NOT NULL,
  "plainText" text,
  status text,
  CONSTRAINT "documentId" PRIMARY KEY ("documentId"),
  CONSTRAINT "fileId" FOREIGN KEY ("fileId")
      REFERENCES "File" ("fileId") MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "uploadedBy" FOREIGN KEY ("uploadedBy")
      REFERENCES "User" ("userId") MATCH SIMPLE
      ON UPDATE SET NULL ON DELETE SET NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "Document"
  OWNER TO cqamtiza;
  
CREATE TABLE "History"
(
  "historyId" serial NOT NULL,
  "searchText" text,
  "performedOn" timestamp with time zone,
  "userId" integer,
  CONSTRAINT "historyId" PRIMARY KEY ("historyId"),
  CONSTRAINT "userId" FOREIGN KEY ("userId")
      REFERENCES "User" ("userId") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "History"
  OWNER TO cqamtiza;  
  
INSERT INTO "Settings" (
	name,
	type,
	value)
VALUES 
	('solrClusterId', 'text', NULL) 
	('solrClusterName', 'text', NULL)