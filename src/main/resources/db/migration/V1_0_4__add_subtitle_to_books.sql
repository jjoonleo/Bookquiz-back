-- Add subtitle column to books table
ALTER TABLE books
ADD COLUMN IF NOT EXISTS subtitle VARCHAR(500);