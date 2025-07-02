#!/usr/bin/env bash

function check_dependencies() {
  if ! which unzip > /dev/null; then echo "El comando unzip es necesario para la instalación"; exit 20; fi;
  if ! which docker > /dev/null; then echo "El comando docker es necesario para la instalación"; exit 21; fi;
}

function setup_extension() {
  docker run --rm -v ../trust-extension:/build -w /build maven:3-eclipse-temurin-21-alpine mvn package || exit 90

  if [ ! -d 'extensions' ]
  then
    mkdir extensions || exit 10
  elif [ -d 'extensions/trust-extension' ]; then
    rm -rf extensions/trust-extension || exit 10
  fi

  unzip ../trust-extension/target/trust-extension-1.0-distribution.zip -d extensions || exit 11
}

function setup_certificates() {
  cd conf || exit 1
  #Server cert
  docker run --rm -v ./:/certs-aux docker.io/hivemq/hivemq-ce keytool -genkey -keyalg RSA -alias "$hostname" \
    -keystore /certs-aux/hivemq.jks -storepass "$password" -validity 360 -keysize 4096 -dname "CN=$hostname" || exit 3
  sed -i "s/\${pass}/$password/g" config.xml || exit 12

  #Client cert
  docker run --rm -v ./:/certs-aux docker.io/hivemq/hivemq-ce keytool -export -keystore /certs-aux/hivemq.jks -alias "$hostname" \
    -storepass "$password" -file /certs-aux/hivemq-server.crt || exit 5
  docker run --rm -v ./:/certs-aux docker.io/hivemq/hivemq-ce keytool -import -file /certs-aux/hivemq-server.crt -alias HiveMQ \
    -keystore /certs-aux/mqtt-client-trust-store.jks -storepass "$password" -trustcacerts -noprompt || exit 6
  rm -f hivemq-server.crt || exit 13
  cd ..
}

function build_web() {
  echo PUBLIC_MQTT_HOST="ws://${hostname}/mqtt" > ../web/.env
  docker compose build || exit 91
}

function build_client() {
  #docker run --rm -v ../client:/build -w /build --entrypoint /bin/bash ghcr.io/graalvm/native-image-community:21 \
  #  -c "microdnf --refresh install maven -y && JAVA_HOME=/usr/lib64/graalvm/graalvm-community-java21 mvn package" || exit 92
  config_file=../client/src/config.yaml
  sed -i "s/\${host}/$hostname/g" $config_file
  cert_path="$(pwd)/conf/mqtt-client-trust-store.jks"
  sed -i "s|\${cert}|$cert_path|g" $config_file
  sed -i "s/\${cert-pwd}/$password/g" $config_file
}

function run_servers() {
  read -pr "Ejecutar servidores(S/N): " start
  case "$start" in
      [Ss]* )
        docker compose up -d || exit 50
        return 0
        ;;
      * ) ;;
  esac
  return 1
}

function run_client() {
  read -pr "Ejecutar cliente(S/N): " start
  case "$start" in
      [Ss]* )
        return 0
        ;;
      * ) ;;
  esac
}

check_dependencies

# Move to this script dir
cd "$(dirname "$0")" || exit 1
chmod 777 .
hostname=$(hostname --fqdn)

read -rsp "Introduce la contraseña para el certificado: " password
if [ "${#password}" -lt 6 ]
then
  echo La contraseña debe tener al menos 6 caracteres
  exit 2
fi
echo

setup_extension
setup_certificates
build_web
build_client

if run_servers
then
  run_client
fi
