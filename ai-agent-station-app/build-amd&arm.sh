# 兼容 amd、arm 构建镜像
 docker buildx build --load --platform linux/amd64,linux/arm64 -t fuzhengwei/ai-agent-station-app:1.0.2 -f ./Dockerfile .