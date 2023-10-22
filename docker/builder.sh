#准备工作
#mkdir -p /opt/fox-docker/opt
#cp -r /opt/fox-edge /opt/fox-docker/opt
#cd /opt/fox-docker


#生成后端fox-edge-server的docker镜像，并启动该容器
#docker build -f Dockerfile.fox-edge-server -t fox-edge-server .
#docker run --privileged -p 9000:9000 -p 9101:9101  -p 9301:9301 -p 9302:9302 -v /dev:/dev  -v /tmp:/opt/fox-edge/logs -d fox-edge-server

#生成前端fox-edge-ui的docker镜像，并启动该容器
#docker build -f Dockerfile.fox-edge-ui -t fox-edge-ui .
#docker run -p 80:80 -d fox-edge-ui

#docker exec -it 容器ID /bin/bash
#docker exec -it 容器ID /bin/sh




