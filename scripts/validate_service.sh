#!/bin/bash

# 최대 대기 시간 (초)
MAX_WAIT_TIME=60
# 대기 간격 (초)
SLEEP_INTERVAL=5

# 서버가 응답할 포트 설정
PORT=80

# 경과 시간 초기화
elapsed_time=0

# 서비스가 정상적으로 실행될 때까지 반복 확인
while [ "$elapsed_time" -lt "$MAX_WAIT_TIME" ]; do
    # 해당 포트가 열려있는지 확인 (nc 명령어 사용)
    nc -zv localhost $PORT
    if [ $? -eq 0 ]; then
        echo "Service is running successfully on port $PORT."
        exit 0
    fi

    echo "Waiting for service to start... ($elapsed_time/$MAX_WAIT_TIME seconds)"
    sleep $SLEEP_INTERVAL
    elapsed_time=$((elapsed_time + SLEEP_INTERVAL))
done

# 최대 대기 시간 내에 서비스가 시작되지 않으면 실패 처리
echo "Service failed to start on port $PORT within $MAX_WAIT_TIME seconds."
exit 1