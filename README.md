# Autorización basada en la confianza para el protocolo MQTT
Este repositorio contiene el código y la documentación para el proyecto.

El proyecto se divide en tres partes:
- Implementación en el servidor, como una [extensión](trust-extension) para el broker MQTT [HiveMQ](https://www.hivemq.com/products/mqtt-broker/).
- [Cliente MQTT](client) implementando el protocolo.
- [Cliente web](web) para la visualización de datos relativos al protocolo.

## Instalación
El sistema está preparado para ser usado bajo una máquina linux y ha sido probado en [Debian 13](https://www.debian.org/index.es.html). Además necesita las siguientes dependencias:
- [Docker](https://docs.docker.com/engine/install/)
- unzip
- git

### Instalación automática
La instalación automática se trata de un script bash que compila y configura el sistema completo. Esto incluye el servidor MQTT con la extensión, el cliente web y opcionalmente el cliente MQTT.

Para la instalación automática es necesario tener el comando `hostname` instalado.

Ejecutar los siguientes comandos en el terminal y seguir los pasos.
```shell
git pull https://github.com/diegojrl/TFG.git
cd TFG/docker
./install.sh
```
Si el proceso finaliza correctamente y se ha seleccionado la opción de ejecutar los servidores, el entorno estará funcionando y se podrá conectar usando los datos mostrados por pantalla al final de dicho procso.

### Instalación Manual
La instalación de forma manual requiere la ejecución de una serie de comandos, cada parte del proyecto se compila y configura por separado. Ver el documento ***README.md*** en cada uno de estos apartados para compilar y configuar el proyecto de forma manual.

- Instalación del [cliente](client#compilación)
- Instalación del [servidor web](web#compilación)
- Instalación del [servidor MQTT](trust-extension#compilación)