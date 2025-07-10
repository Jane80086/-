-- "public"."enterprise" definition

-- Drop table

-- DROP TABLE "public"."enterprise";

CREATE TABLE "public"."enterprise" (
	"enterprise_id" character varying(30 char) NOT NULL,
	"enterprise_name" character varying(30 char) NOT NULL,
	"credit_code" character varying(30 char) NULL,
	"register_address" character varying(30 char) NULL,
	"legal_representative" character varying(30 char) NULL,
	"registration_date" date NULL,
	"enterprise_type" character varying(30 char) NULL,
	"registered_capital" character varying(30 char) NULL,
	"business_scope" text NULL,
	"establishment_date" date NULL,
	"business_term" character varying(30 char) NULL,
	"registration_authority" character varying(30 char) NULL,
	"approval_date" date NULL,
	"enterprise_status" character varying(30 char) NULL,
	"create_time" timestamp without time zone NULL DEFAULT CURRENT_TIMESTAMP,
	"update_time" timestamp without time zone NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "enterprise_pkey" PRIMARY KEY (enterprise_id),
	CONSTRAINT "enterprise_social_credit_code_key" UNIQUE (credit_code)
)TABLESPACE sys_default;
CREATE INDEX idx_social_credit_code ON public.enterprise USING btree (credit_code) TABLESPACE sys_default;

-- Table Triggers

CREATE OR REPLACE TRIGGER update_enterprise_trigger BEFORE UPDATE ON public.enterprise FOR EACH ROW EXECUTE FUNCTION update_enterprise_time();