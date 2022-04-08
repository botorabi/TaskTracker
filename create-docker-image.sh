#!/bin/bash

VERSION=""

if [ "$#" -eq 1 ]; then
  VERSION=$1
  echo "Using command line argument for version information: ${VERSION}"
else
  echo "Retrieving version information from pom.xml file..."
  VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[')
  echo "Using version information: ${VERSION}"
fi

if [ "${VERSION}" = "@" ] || [ -z "${VERSION}" ]; then
  echo "Could not determine the version information, define it by command line!"
  echo " Use: distribute-package.sh <version string>"
  exit -1
fi


#export TASK_TRACKER_BASE_URL=/apps/tasktracker
export TASK_TRACKER_BASE_URL=
mvn clean package

PACKAGE_DIR=tasktracker

rm -rf tmp
mkdir -p tmp/${PACKAGE_DIR}/bin
mkdir -p tmp/${PACKAGE_DIR}/logs

# NOTE: Adapt the following settings for your application
echo "# HTTPS server
server.ssl.enabled = false
server.port = 8080

# LDAP Settings
tasktracker.ldap.urls =
tasktracker.ldap.user.dn.pattern =
tasktracker.ldap.mgm.dn =
tasktracker.ldap.mgm.pw =
tasktracker.ldap.grp.filter =

spring.mail.host =
spring.mail.port =
spring.mail.username =
spring.mail.password =
spring.mail.properties.mail.smtp.auth = true
spring.mail.properties.mail.smtp.starttls.enable = true

company.name =
" > tmp/${PACKAGE_DIR}/application.properties

cp target/tasktracker*.jar tmp/${PACKAGE_DIR}/bin
cd tmp/${PACKAGE_DIR}/bin
ln -s $(ls tasktracker*.jar)  tasktracker.jar
cd -
cp LICENSE tmp/${PACKAGE_DIR}/
cp README.md tmp/${PACKAGE_DIR}/

echo "Creating docker image..."
docker build . -t tasktracker:v${VERSION}

