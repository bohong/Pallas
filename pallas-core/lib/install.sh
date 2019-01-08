#!/bin/bash

cd ./lib

mvn install:install-file -Dfile=elasticsearch-sql-5.5.2.0.jar -DgroupId=org.nlpcn -DartifactId=elasticsearch-sql -Dversion=5.5.2.0  -Dpackaging=jar