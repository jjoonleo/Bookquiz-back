-- Add string columns for authors, translators, illustrators
ALTER TABLE books
ADD COLUMN IF NOT EXISTS authors VARCHAR(2000) NOT NULL DEFAULT '';
ALTER TABLE books
ADD COLUMN IF NOT EXISTS translators VARCHAR(2000) NOT NULL DEFAULT '';
ALTER TABLE books
ADD COLUMN IF NOT EXISTS illustrators VARCHAR(2000) NOT NULL DEFAULT '';
-- Backfill data by concatenating names with comma + space
-- Authors
UPDATE books b
SET authors = COALESCE(string_agg(p.name, ', '), '')
FROM (
    SELECT ba.book_id,
      p.name
    FROM book_author ba
      JOIN persons p ON p.id = ba.person_id
    ORDER BY ba.book_id,
      p.name
  ) s
  JOIN persons p ON p.name = s.name
WHERE b.id = s.book_id;
-- Translators
UPDATE books b
SET translators = COALESCE(string_agg(p.name, ', '), '')
FROM (
    SELECT bt.book_id,
      p.name
    FROM book_translator bt
      JOIN persons p ON p.id = bt.person_id
    ORDER BY bt.book_id,
      p.name
  ) s
  JOIN persons p ON p.name = s.name
WHERE b.id = s.book_id;
-- Illustrators
UPDATE books b
SET illustrators = COALESCE(string_agg(p.name, ', '), '')
FROM (
    SELECT bi.book_id,
      p.name
    FROM book_illustrator bi
      JOIN persons p ON p.id = bi.person_id
    ORDER BY bi.book_id,
      p.name
  ) s
  JOIN persons p ON p.name = s.name
WHERE b.id = s.book_id;
-- Drop FKs and join tables, then persons table
DO $$ BEGIN IF EXISTS (
  SELECT 1
  FROM information_schema.tables
  WHERE table_name = 'book_author'
) THEN DROP TABLE book_author;
END IF;
IF EXISTS (
  SELECT 1
  FROM information_schema.tables
  WHERE table_name = 'book_translator'
) THEN DROP TABLE book_translator;
END IF;
IF EXISTS (
  SELECT 1
  FROM information_schema.tables
  WHERE table_name = 'book_illustrator'
) THEN DROP TABLE book_illustrator;
END IF;
IF EXISTS (
  SELECT 1
  FROM information_schema.tables
  WHERE table_name = 'persons'
) THEN DROP TABLE persons;
END IF;
END $$;