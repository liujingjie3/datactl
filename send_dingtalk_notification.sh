#!/bin/bash


# 配置钉钉通知的标题和内容
CI_PROJECT_NAME=${CI_PROJECT_NAME}
CI_JOB_ID=${CI_JOB_ID}
CI_JOB_URL=${CI_JOB_URL}
CI_COMMIT_REF_NAME=${CI_COMMIT_REF_NAME}
CI_COMMIT_MESSAGE=${CI_COMMIT_MESSAGE}
CI_JOB_NAME=${CI_JOB_NAME}  # 当前阶段名称

message="GitLab CI/CD Job Failed:
- 项目: ${CI_PROJECT_NAME}
- 分支: ${CI_COMMIT_REF_NAME}
- 阶段: ${CI_JOB_NAME}  # 当前阶段名称
- 任务ID: ${CI_JOB_ID}
- 任务链接: ${CI_JOB_URL}
- 提交信息: ${CI_COMMIT_MESSAGE}"

# 调用钉钉 API 发送通知
curl -X POST "${DINGTALK_WEBHOOK}" \
   -H 'Content-Type: application/json' \
   -d '{
        "msgtype": "text",
        "text": {
            "content": "'"${message}"'"
        }
    }'
