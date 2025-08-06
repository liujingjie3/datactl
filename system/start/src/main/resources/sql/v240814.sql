ALTER TABLE metadata_gf
    ALTER COLUMN review DROP DEFAULT,
    ALTER COLUMN review TYPE integer USING review::integer,
    ALTER COLUMN review SET DEFAULT 0;
