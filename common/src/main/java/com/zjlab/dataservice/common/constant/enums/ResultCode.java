package com.zjlab.dataservice.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {
    SUCCESS(200, "SUCCESS"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SC_JEECG_NO_AUTHZ(510,"访问权限认证未通过"),
    PARA_ERROR(501, "参数错误"),
    REQUEST_BODY_MISSING_ERROR(502, "request body不可为空"),
    SQL_UPDATE_ERROR(1000, "数据库更新失败"),
    CMD_EXECUTE_FAILED(1001, "cmd命令执行失败"),


    //user 110000-119999
    USER_NOT_EXIST(110000, "用户不存在"),
    TOKEN_NULL(110001, "token不能为空"),
    TOKEN_INVALID(110002, "token非法无效"),
    ACCOUNT_LOCKED(110003, "账号已被锁定,请联系管理员"),
    TOKEN_EXPIRED(110004,  "token失效,请重新登陆"),


    //数据集 120000-129999
    //开源数据集 121000-121999

    //标注 122000-122999
    STATUS_INVALID(122000, "传入状态不合法"),
    CONTAIN_ID_NOT_EXIST(122001, "id列表中包含不存在的id"),
    CONTAIN_ID_NOT_EXIST_OR_INVALID(122002, "id列表中包含不存在的id或不合法状态"),
    FILE_NOT_EXIST(122003, "文件不存在"),
    MARK_FILE_STATUS_NOT_MATCH(122004, "标注文件状态不匹配"),
    CONTAIN_FILE_NOT_EXIST(122005, "包含不存在的文件"),
    CONTAIN_FILE_STATUS_NOT_MATCH(122006, "包含文件状态不匹配的文件"),
    REVIEW_STATUS_INVALID(122007, "审查状态不合法"),

    //专题 123000-123999

    //看板 124000-124999
    MARK_STATISTICS_ERROR(124000, "统计标注数据失败"),
    DATASET_STATISTICS_ERROR(124001, "统计影像数据集数据失败"),
    IMAGE_STATISTICS_ERROR(124002, "统计标准影像数据失败"),
    DATEFORMAT_ERROR(124003, "时间格式错误"),


    //个人空间 130000-139999
    //收藏 131000-131999
    USERID_IS_NULL(131000, "用户id为空"),
    IMAGE_ALREADY_COLLECTED(131001, "影像已收藏"),
    CONTAIN_COLLECTED_IMAGE(131002, "影像列表中存在已收藏的影像"),
    COLLECTED_IMAGE_NOT_EXIST(131003, "影像未收藏，不可取消收藏"),
    CONTAIN_NOT_EXIST_COLLECTED_IMAGE(131004, "影像列表中存在未收藏的影像"),
    COLLECT_FOLDER_EXISTED(131005, "收藏列表中已存在文件夹"),
    COLLECT_MOVEPATH_ORIGINPATH_NULL(131006, "收藏列表移动文件夹原始路径为null"),
    COLLECT_MOVEPATH_IDS_CONTAIN_NULL(131007, "收藏列表移动文件列表为空，或列表中存在null的id"),
    COLLECT_REMOVE_PARAM_NULL(131008, "取消收藏/删除收藏文件夹入参不可同时为空"),
    COLLECT_RENAME_PATH_EXIST(131009, "已存在重名文件夹"),

    //申请列表和订单
    IMAGE_ALREADY_ADD(141001, "影像中存在已加入申请订单的影像"),
    CONTAIN_NOT_EXIST_SHOPCART_IMAGE(141004, "影像列表中存在未在申请列表的影像"),
    SHOPCART_IMAGE_NOT_EXIST(141003, "影像未在申请列表，不可删除"),

    //数据处理 150000-159999
    DUPLICATE_TASK_NAME(150001, "任务名重复"),
    UPDATE_TASK_FAIL(150002, "保存任务失败"),
    START_TASK_FAIL(150003, "执行任务失败"),
    DUPLICATE_TEMPLATE_NAME(150004, "模板名重复"),
    QUERY_TEMPLATE_FAIL(150005, "模板未找到"),

    // 任务中心
    TASK_IS_EXISTS(200001, "待办任务已存在"),
    TASK_IS_NOT_EXISTS(200002, "待办任务不存在"),
    INSTANCE_IS_EXISTS(200003, "实例已经存在"),
    INSTANCE_IS_NOT_EXISTS(200004, "实例对象不存在"),


    ;
    //返回码
    private final Integer code;
    //返回信息
    private final String message;
}
