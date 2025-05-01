-- AI Agent 数据库表设计

-- 通信模型配置表
CREATE TABLE `ai_api_model_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(50) NOT NULL COMMENT '模型名称',
  `base_url` varchar(255) NOT NULL COMMENT '基础URL',
  `api_key` varchar(255) NOT NULL COMMENT 'API密钥',
  `completions_path` varchar(100) DEFAULT 'v1/chat/completions' COMMENT '完成路径',
  `embeddings_path` varchar(100) DEFAULT 'v1/embeddings' COMMENT '嵌入路径',
  `model_type` varchar(50) NOT NULL COMMENT '模型类型(openai/azure等)',
  `model_version` varchar(50) DEFAULT 'gpt-4.1' COMMENT '模型版本',
  `timeout` int DEFAULT 180 COMMENT '超时时间(秒)',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_name` (`model_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通信模型配置表';

-- 系统提示词配置表
CREATE TABLE `ai_pattern_system_prompt` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prompt_name` varchar(50) NOT NULL COMMENT '提示词名称',
  `prompt_content` text NOT NULL COMMENT '提示词内容',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prompt_name` (`prompt_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统提示词配置表';

-- MCP客户端配置表
CREATE TABLE `ai_pattern_config_mcp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mcp_name` varchar(50) NOT NULL COMMENT 'MCP名称',
  `transport_type` varchar(20) NOT NULL COMMENT '传输类型(sse/stdio)',
  `server_url` varchar(255) DEFAULT NULL COMMENT '服务器URL(sse类型必填)',
  `command` varchar(50) DEFAULT NULL COMMENT '命令(stdio类型必填)',
  `args` varchar(255) DEFAULT NULL COMMENT '参数(stdio类型选填)',
  `request_timeout` int DEFAULT 180 COMMENT '请求超时时间(分钟)',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mcp_name` (`mcp_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP客户端配置表';

-- 知识库配置表
CREATE TABLE `ai_pattern_config_rag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rag_name` varchar(50) NOT NULL COMMENT '知识库名称',
  `resource_path` varchar(255) NOT NULL COMMENT '资源路径',
  `knowledge_tag` varchar(50) NOT NULL COMMENT '知识标签',
  `top_k` int DEFAULT 5 COMMENT '返回结果数量',
  `filter_expression` varchar(255) DEFAULT NULL COMMENT '过滤表达式',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rag_name` (`rag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库配置表';

-- 内存管理配置表
CREATE TABLE `ai_pattern_config_memory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `memory_name` varchar(50) NOT NULL COMMENT '内存名称',
  `memory_type` varchar(20) NOT NULL COMMENT '内存类型(InMemory/Redis等)',
  `retrieve_size` int DEFAULT 100 COMMENT '检索大小',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_memory_name` (`memory_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内存管理配置表';

-- 顾问配置表
CREATE TABLE `ai_advisor_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `advisor_name` varchar(50) NOT NULL COMMENT '顾问名称',
  `advisor_type` varchar(50) NOT NULL COMMENT '顾问类型(PromptChatMemory/QuestionAnswer等)',
  `order_num` int DEFAULT 0 COMMENT '顺序号',
  `param_ext` varchar(2048) NOT NULL COMMENT '扩展参数配置',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_advisor_name` (`advisor_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾问配置表';

-- AI客户端配置表
CREATE TABLE `ai_client_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_name` varchar(50) NOT NULL COMMENT '客户端名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_name` (`client_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客户端配置表';

CREATE TABLE `ai_client_part_model`(
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `client_id` bigint NOT NULL COMMENT '客户端ID',
   `model_id` bigint NOT NULL COMMENT '模型ID',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客户端，零部件；模型配置';

CREATE TABLE `ai_client_part_prompt`(
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `client_id` bigint NOT NULL COMMENT '客户端ID',
   `system_prompt_id` bigint NOT NULL COMMENT '系统提示词ID',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客户端，零部件；模型配置';

-- 客户端-MCP关联表
CREATE TABLE `ai_client_part_mcp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` bigint NOT NULL COMMENT '客户端ID',
  `mcp_id` bigint NOT NULL COMMENT 'MCP ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_mcp` (`client_id`,`mcp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端-MCP关联表';

-- 客户端-顾问关联表
CREATE TABLE `ai_client_part_advisor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` bigint NOT NULL COMMENT '客户端ID',
  `advisor_id` bigint NOT NULL COMMENT '顾问ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_advisor` (`client_id`,`advisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端-顾问关联表';

-- AI智能体配置表
CREATE TABLE `ai_agent_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_name` varchar(50) NOT NULL COMMENT '智能体名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_name` (`agent_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能体配置表';

-- 智能体-客户端关联表
CREATE TABLE `ai_agent_part_client` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` bigint NOT NULL COMMENT '智能体ID',
  `client_id` bigint NOT NULL COMMENT '客户端ID',
  `sequence` int NOT NULL COMMENT '序列号(执行顺序)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_client_seq` (`agent_id`,`client_id`,`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能体-客户端关联表';