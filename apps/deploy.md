Requirements:
- java8, scala-2.12, sbt-1.x
- mysql 5.6+ installed
- docker and docker-compose
- working ssh server

Steps for server:
- import data to mysql from the dump file (or data schema)
- adjust application.properties file to match mysql connection parameters
- run assembled jar with command below:
    >  java -Dconfig.file=application.properties -jar coordinator-assembly-0.0.1-SNAPSHOT.jar
- to build and push to server: `sbt, project coord, assembly, scp and run`

Steps for frontend:
- build production assets with ./frontend-deploy.sh
- docker build, tag and push
- On production server run: `docker run --net=host zygimantasdev/atviras-seimas-client:0.2.0`


