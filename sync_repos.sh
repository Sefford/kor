#!/bin/bash

modules=(kor-repositories kor-usecases kor-repositories-extensions kor-repositories-gson-converter kor-repositories-moshi-converter)
bintray_user="$(grep -oP "bintray.user=\K.*" local.properties)"
bintray_key="$(grep -oP "bintray.apikey=\K.*" local.properties)"
maven_user="$(grep -oP "oss.user=\K.*" local.properties)"
maven_pass="$(grep -oP "oss.password=\K.*" local.properties)"
version="$(grep -oP "VERSION_NAME=\K.*" gradle.properties)"

for module in "${modules[@]}"
do
   echo -e "Deploying ${module}...\n"
   curl --trace-ascii dump -H "Content-Type: application/json" -u $bintray_user:$bintray_key -d '{ "username": "'$maven_user'","password": "'$maven_pass'", "close": "1"}' https://api.bintray.com/maven_central_sync/sefford/maven/"${module}"/versions/$version
   echo -e "\n"
done

