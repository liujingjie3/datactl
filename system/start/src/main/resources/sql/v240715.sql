ALTER TABLE `dataset_mark_file` MODIFY COLUMN `mark_type` VARCHAR(100);

ALTER TABLE dataset_mark_file ADD COLUMN segment_url VARCHAR(255);