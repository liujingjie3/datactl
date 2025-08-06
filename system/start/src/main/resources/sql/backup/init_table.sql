INSERT INTO backup_source (type, ip, username, password, database_name, del_flag, create_by, create_time, update_by, update_time)
VALUES ('mysql', '10.101.32.14','dataservice','6gzS081ptn0H50iX7BW7xPxq1BZDPkhz8IDb4ZHBt24=', 'dataservice_ctl', 0, 'admin', CURRENT_TIME,'admin', CURRENT_TIME);
INSERT INTO backup_source (type, endpoint, access_key, secret_key, bucket_name, del_flag, create_by, create_time, update_by, update_time)
VALUES ('minio', 'http://10.101.4.101:9000','root','wpqZmeYL7VvgSftP8n1YVA==', 'dspp', 0, 'admin', CURRENT_TIME,'admin', CURRENT_TIME);

INSERT INTO backup_task(source_id, source_type, dest_id, dest_type, cron, del_flag, create_by, create_time, update_by, update_time)
VALUES (1, 'mysql', 2, 'minio', '0 0 0 * * ?', 0, 'admin', CURRENT_TIME,'admin', CURRENT_TIME)