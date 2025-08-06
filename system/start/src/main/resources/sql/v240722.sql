ALTER TABLE dataset_mark_file
    ADD COLUMN classify_status tinyint COMMENT '分类任务状态',
    ADD COLUMN classify_checker VARCHAR(100) COMMENT '分类任务小组长id',
    ADD COLUMN classify_marker VARCHAR(100) COMMENT '分类任务组员id',
    ADD COLUMN classify_update_by VARCHAR(100) COMMENT '分类任务更新人',
    ADD COLUMN classify_update_time DATETIME COMMENT '分类任务更新时间',
    ADD COLUMN classify_review tinyint DEFAULT 0 COMMENT '分类任务复审标志位，0未复审，1已复审',
    ADD COLUMN segment_status tinyint COMMENT '分割任务状态',
    ADD COLUMN segment_checker VARCHAR(100) COMMENT '分割任务小组长id',
    ADD COLUMN segment_marker VARCHAR(100) COMMENT '分割任务组员id',
    ADD COLUMN segment_update_by VARCHAR(100) COMMENT '分割任务更新人',
    ADD COLUMN segment_update_time DATETIME COMMENT '分割任务更新时间',
    ADD COLUMN segment_review tinyint DEFAULT 0 COMMENT '分割任务复审标志位，0未复审，1已复审'
;

ALTER TABLE dataset_operation_log
    ADD COLUMN task_type VARCHAR(100) COMMENT '任务类型：classify分类，segment分割，text图文,filter图筛'
;

ALTER TABLE dataset_mark_file
    ALTER COLUMN review SET DEFAULT 0;

UPDATE dataset_mark_file
    SET review = 0 WHERE review IS NULL;

ALTER TABLE dataset_operation_log
    MODIFY COLUMN `mark_type` VARCHAR(100),
    MODIFY COLUMN `last_mark_type` VARCHAR(100);

ALTER TABLE dataset_mark_file
    ADD COLUMN classify_purpose tinyint COMMENT '分类任务用途：1训练，2验证，3测试，4其他',
    ADD COLUMN segment_purpose tinyint COMMENT '分类任务用途：1训练，2验证，3测试，4其他';
