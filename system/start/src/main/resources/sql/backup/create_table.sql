DROP TABLE IF EXISTS backup_source;
CREATE TABLE backup_source
(
    id            INT PRIMARY KEY auto_increment,
    type          VARCHAR(20) COMMENT '数据源类型',
    ip            VARCHAR(20) COMMENT '数据库ip',
    username      VARCHAR(100) COMMENT '数据库用户名',
    database_name VARCHAR(100) COMMENT '指定数据库',
    password      VARCHAR(100) COMMENT '数据库密码',
    endpoint      VARCHAR(255) COMMENT 'S3-endpoint',
    access_key    VARCHAR(100) COMMENT 'S3-ak',
    secret_key    VARCHAR(100) COMMENT 'S3-sk',
    bucket_name   VARCHAR(100) COMMENT 'S3桶名',

    -- 系统字段
    tenant_id     INT(10) DEFAULT '0' comment '租户id',
    del_flag      TINYINT COMMENT '删除标志位: 0未删除; 1已删除',
    create_by     VARCHAR(100) COMMENT '创建人',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(100) COMMENT '更新人',
    update_time   DATETIME COMMENT '更新时间'
)COMMENT='备份数据源表';

DROP TABLE IF EXISTS backup_task;
CREATE TABLE backup_task
(
    id          INT PRIMARY KEY auto_increment,
    source_id   INT COMMENT '源数据源id',
    source_type VARCHAR(20) COMMENT '源数据源类型',
    dest_id     INT COMMENT '目的数据源id',
    dest_type   VARCHAR(20) COMMENT '目的数据源类型',
    cron        VARCHAR(50) COMMENT '定时任务时间信息',
    status      VARCHAR(10) COMMENT '执行状态：待执行，执行中，停止中，已结束',
    sub_status  VARCHAR(10) COMMENT '子状态：成功，失败，人工终止',

    -- 系统字段
    tenant_id   INT(10) DEFAULT '0' comment '租户id',
    del_flag    TINYINT COMMENT '删除标志位: 0未删除; 1已删除',
    create_by   VARCHAR(100) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by   VARCHAR(100) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间'
)COMMENT='备份任务表';

DROP TABLE IF EXISTS backup_task_record;
CREATE TABLE backup_task_record
(
    id          INT PRIMARY KEY auto_increment,
    task_id     INT COMMENT '任务id',
    source_id   INT COMMENT '源数据源id',
    source_type VARCHAR(20) COMMENT '源数据源类型',
    dest_id     INT COMMENT '目的数据源id',
    dest_type   VARCHAR(20) COMMENT '目的数据源类型',
    cron        VARCHAR(50) COMMENT '定时任务时间信息',
    status      VARCHAR(10) COMMENT '执行状态：待执行，执行中，停止中，已结束',
    sub_status  VARCHAR(10) COMMENT '子状态：成功，失败，人工终止',
    log_info    VARCHAR(1000) COMMENT '运行结果日志',

    -- 系统字段
    tenant_id   INT(10) DEFAULT '0' comment '租户id',
    del_flag    TINYINT COMMENT '删除标志位: 0未删除; 1已删除',
    create_by   VARCHAR(100) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by   VARCHAR(100) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间'
)COMMENT='备份任务记录表';