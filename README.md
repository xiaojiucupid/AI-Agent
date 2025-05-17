# Ai Agent 智能体，部署说明 v1.0.0



- docker 使用文档：[https://bugstack.cn/md/road-map/docker.html](https://bugstack.cn/md/road-map/docker.html)

- DDD 教程；
  - [DDD 概念理论](https://bugstack.cn/md/road-map/ddd-guide-01.html)
  - [DDD 建模方法](https://bugstack.cn/md/road-map/ddd-guide-02.html)
  - [DDD 工程模型](https://bugstack.cn/md/road-map/ddd-guide-03.html)
  - [DDD 架构设计](https://bugstack.cn/md/road-map/ddd.html)
  - [DDD 建模案例](https://bugstack.cn/md/road-map/ddd-model.html)
  
>本项目采用了 DDD 架构进行设计开发，可以阅读以上资料了解架构。

## 1. 前置说明

- 云服务器 [https://618.gaga.plus](https://618.gaga.plus) 2c4g 系统镜像 centos 7.9 / 应用镜像 docker - 防火墙开放端口；9000、8091、8899。用云服务器公网IP，替换 dev-ops/nginx/html 下，admin，js，里面的接口IP地址。搜索，192.168.1.109 替换你的 IP
- SSH&SFT 工具，链接云服务器 [https://termius.com/](https://termius.com/) - `免费的就可以使用`
- Docker 安装（含 docker-compose） [https://bugstack.cn/md/road-map/docker.html](https://bugstack.cn/md/road-map/docker.html)
- 注册，微信公众号测试平台，用于配置接收 MCP 消息通知 [https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index](https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index) 
- 注册，CSDN 平台，用于自动推送 MCP 发帖服务 [https://www.csdn.net/](https://www.csdn.net/) 
- 需要使用 OpenAi 模型，推荐 gpt-4.1、gpt-4.1-mini

---

OpenAi 渠道对接说明：

1. 进入 [https://openai.itedus.cn/#/mall](https://openai.itedus.cn/#/mall) 购买最低的这个，就够测试用。（如果大量使用，可以买token更多的，实惠）

2. 购买后在左下角设置里，点击【`查看接口`】base-url: https://apis.itedus.cn api-key，从`查看接口`获取，sk 开头的。

3. 你可以把点击查看接口的内容，导入到 ApiPost、ApiFox 等工具进行调用验证。

支持；gpt-4o、gpt-4.1、gpt-4.1-mini、claude-3-7-sonnet-20250219
建议；gpt-4.1、gpt-4.1-mini，效果更好，速度更快，消耗更低。

## 2. 部署脚本

在 ai-agent-station 工程下提供了部署脚本 docs/dev-ops；

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-01.png" width="650px">
</div>

- 如图，为 ai-agent-station 脚本的配置说明。
- 如果，你只是想部署云服务器进行验证，那么先不需要构建，可以直接使用我已经做好的镜像。
- 注意，各个服务，mysql、redis、pg等，配置的账号密码，都在 docker-compose-environment-aliyun.yml 中。

### 2.1 修改项，微信配置

**docker-compose-environment-aliyun.yml 部分配置**

```java
mcp-server-weixin-app:
  #    image: fuzhengwei/mcp-server-weixin-app:1.1
  image: registry.cn-hangzhou.aliyuncs.com/fuzhengwei/mcp-server-weixin-app:1.1
  container_name: mcp-server-weixin-app
  restart: always
  ports:
    - "8102:8102"
  volumes:
    - ./log:/data/log
  environment:
    - TZ=PRC
    - SERVER_PORT=8102
    - WEIXIN_API_ORIGINAL_ID=gh_e067c267e056
    - WEIXIN_API_APP_ID=wx5a228ff69e28a91f
    - WEIXIN_API_APP_SECRET=0bea03aa1310bac050aae79dd8703928
    - WEIXIN_API_TEMPLATE_ID=O8qI6gy75F-bXfPiQugInTMLA0MRzaMff9WSBb16cFk
    - WEIXIN_API_TOUSER=or0Ab6ivwmypESVp_bYuk92T6SvU
  logging:
    driver: "json-file"
    options:
      max-size: "10m"
      max-file: "3"
  networks:
    - my-network
```

首先，你需要打开微信公众号测试平台，之后分别获取以下配置，修改你的配置文件。

- WEIXIN_API_ORIGINAL_ID 获取来源；【微信号】
- WEIXIN_API_APP_ID 获取来源；【appID】
- WEIXIN_API_APP_SECRET 获取来源；【appsecret】
- WEIXIN_API_TEMPLATE_ID 获取来源；在模板消息接口下，点击新增测试模板。新建后会拿到一个模板ID
	- 名称：AI-Agent
	- 模板：`平台：{{platform_name.DATA}} 主题：{{subject_name.DATA}} 说明：{{description_name.DATA}}`
- WEIXIN_API_TOUSER 获取来源；使用手机扫描【测试号二维码】，之后用户列表就可以拿到你的微信号，填写到这里。这个ID就表示发送给谁。

### 2.2 修改项，CSDN配置

```java
mcp-server-csdn-app:
  #    image: fuzhengwei/mcp-server-csdn-app:1.1
  image: registry.cn-hangzhou.aliyuncs.com/fuzhengwei/mcp-server-csdn-app:1.1
  container_name: mcp-server-csdn-app
  restart: always
  ports:
    - "8101:8101"
  volumes:
    - ./log:/data/log
  environment:
    - TZ=PRC
    - SERVER_PORT=8101
    - CSDN_API_CATEGORIES=Java场景面试宝典
    - CSDN_API_COOKIE=uuid_tt_dd=10_37460597350-17448448791你的cookie
  logging:
    driver: "json-file"
    options:
      max-size: "10m"
      max-file: "3"
  networks:
    - my-network
```

- CSDN_API_CATEGORIES，你可以任意填写一个名称，代表文章分类名称。
- CSDN_API_COOKIE，进入到个人中心 [https://i.csdn.net/#/user-center/profile](https://i.csdn.net/#/user-center/profile) 获取 Cookie

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-02.png" width="950px">
</div>

- 把获取的 cookie 复制出来，放到 CSDN_API_COOKIE 后面。

## 3. 执行部署

### 3.1 上传脚本

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-03.png" width="950px">
</div>

- 如图，通过 sft 工具，把脚本上传到云服务器。

### 3.2 安装环境

```java
cd /dev-ops/ai-agent-station
docker-compose -f docker-compose-environment-aliyun.yml up -d
```

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-04.png" width="950px">
</div>

- 执行脚本后，等待环境全部安装完成。
- 安装环境，并确定 8899 端口开放，访问你的地址：[IP:8899](http://IP:8899) 进入 mysql 管理平台，修改 ai_client_model 里的 base_url，api_key

### 3.3 安装应用

```java
cd /dev-ops/ai-agent-station
docker-compose -f docker-compose-app.yml up -d
```

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-05.png" width="950px">
</div>

- 安装完成后，所有内容都会运行起来。
- 你可以进入到 ai-agent-station-app 点 📃 文件，可以查看运行日志，它的加载情况。

### 3.4 安装联网

```java
# 注意；因为 ai agent 配置了联网能力，需要部署应用后，手动安装 playwright
# 进入后端 docker exec -it ai-agent-station-app /bin/bash
# 手动安装（1）；npx playwright@1.52.0 install-deps
# 手动安装（2）；npx playwright@1.52.0 install
```

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-06.png" width="950px">
</div>

- 你可以进入项目后台，点击`>_` 即可进入，也可以通过命令 `docker exec -it ai-agent-station-app /bin/bash`
- 之后分别执行 `npx playwright@1.52.0 install-deps`、`npx playwright@1.52.0 install` 来安装联网。

## 4. 访问页面

### 4.1 联网检索

<div align="center">
    <img src="https://bugstack.cn/images/article/project/ai-rag-knowledge/ai-agent-station-1-07.png" width="950px">
</div>

- 部署后，可以尝试使用各项功能模块。更多使用案例视频；[https://www.bilibili.com/video/BV1NoENzsELn](https://www.bilibili.com/video/BV1NoENzsELn)
