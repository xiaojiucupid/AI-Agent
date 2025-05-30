# 基础镜像 amd openjdk:17-jdk-slim arm openjdk:17-ea-17-jdk
FROM openjdk:17-ea-17-jdk-slim-buster

# 作者
MAINTAINER xiaofuge

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装 Node.js 环境
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g npm@latest && \
    apt-get purge -y --auto-remove curl && \
    rm -rf /var/lib/apt/lists/*

# 验证 Node.js 环境（可选）
RUN node -v && npm -v

# 添加应用
ADD target/ai-agent-station-app.jar /ai-agent-station-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /ai-agent-station-app.jar $PARAMS"]