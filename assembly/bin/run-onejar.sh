#!/usr/bin/env bash

VERSION=1.0.0
SERVICE_NAME=my-tools
JAR=${SERVICE_NAME}-${VERSION}.one-jar.jar

# colors
red='\e[0;31m'
green='\e[0;32m'
yellow='\e[0;33m'
reset='\e[0m'

echoRed() { echo -e "${red}$1${reset}"; }
echoGreen() { echo -e "${green}$1${reset}"; }
echoYellow() { echo -e "${yellow}$1${reset}"; }

# 获取APP所在的目录的绝对路径
function get_abs_dir() {
    SOURCE="${BASH_SOURCE[0]}"
    # resolve $SOURCE until the file is no longer a symlink
    while [ -h "$SOURCE" ]; do
        TARGET="$(readlink "$SOURCE")"
        if [[ ${SOURCE} == /* ]]; then
            # echo "SOURCE '$SOURCE' is an absolute symlink to '$TARGET'"
            SOURCE="$TARGET"
        else
            DIR="$(dirname "$SOURCE")"
            # echo "SOURCE '$SOURCE' is a relative symlink to '$TARGET' (relative to '$DIR')"
            # if $SOURCE was a relative symlink, we need to resolve it
            # relative to the path where the symlink file was located
            SOURCE="$DIR/$TARGET"
        fi
    done
    # echo "SOURCE is '$SOURCE'"

    # RDIR="$( dirname "$SOURCE" )"
    DIR="$( cd -P "$( dirname "$SOURCE" )" && cd .. && pwd )"
    # if [ "$DIR" != "$RDIR" ]; then
    #     echo "DIR '$RDIR' resolves to '$DIR'"
    # fi
    # echo "DIR is '$DIR'"
    echo $DIR
}

APP_HOME=`get_abs_dir`

java -jar ${APP_HOME}/${JAR} &
