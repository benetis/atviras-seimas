#!/usr/bin/env bash

mkdir -p ./docker/client/build/

rm ./docker/client/build/*

sbt frontend/fullOptJS::webpack

cp ./frontend/target/scala-2.12/scalajs-bundler/main/*-bundle.js ./frontend/target/scala-2.12/scalajs-bundler/main/*.css ./frontend/target/scala-2.12/scalajs-bundler/main/*.html  ./docker/client/build

cd docker/client
docker build .