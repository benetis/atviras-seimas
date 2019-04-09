#!/usr/bin/env bash
java -Dconfig.file=application.properties -jar coordinator-assembly-0.0.1-SNAPSHOT.jar

docker run --net=host