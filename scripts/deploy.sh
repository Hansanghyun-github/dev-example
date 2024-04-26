#!/usr/bin/bash

TARGET_PORT=8080
TARGET_PID=$(sudo lsof -ti :${TARGET_PORT})

echo "current running WAS's pid is ${TARGET_PID}"
echo "start updating WAS version"

sudo kill ${TARGET_PID}

nohup java -jar -Dserver.port=${TARGET_PORT} -Dspring.profiles.active=dev build/libs/dev-example-0.0.1-SNAPSHOT.jar > ../logs/was_out.txt 2> ../logs/was_err.txt < /dev/null &

if [ "$?" -ne 0 ]; then
    echo "fail to execute WAS"
    return 1
fi

echo "success to execute new version WAS"
echo "finish updating WAS version"