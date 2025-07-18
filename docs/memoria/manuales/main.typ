= Autorización basada en la confianza para el protocolo MQTT
<autorización-basada-en-la-confianza-para-el-protocolo-mqtt>
Este repositorio contiene el código y la documentación para el proyecto.

El proyecto se divide en tres partes: - Implementación en el servidor,
como una #link("trust-extension")[extensión] para el broker MQTT
#link("https://www.hivemq.com/products/mqtt-broker/")[HiveMQ];. -
#link("client")[Cliente MQTT] implementando el protocolo. -
#link("web")[Cliente web] para la visualización de datos relativos al
protocolo.

== Instalación
<instalación>
El sistema está preparado para ser usado con un sistema operativo linux
y ha sido probado en
#link("https://www.debian.org/index.es.html")[Debian 13];. Además
necesita las siguientes dependencias: -
#link("https://docs.docker.com/engine/install/")[Docker] - unzip - git

=== Instalación automática
<instalación-automática>
La instalación automática se trata de un script bash que compila y
configura el sistema completamente. Esto incluye el servidor MQTT con la
extensión, el cliente web y opcionalmente el cliente MQTT.

Para la instalación automática es necesario tener el comando `hostname`
disponible.

Ejecutar los siguientes comandos en el terminal y seguir los pasos
indicados por pantalla.

```shell
git pull https://github.com/diegojrl/TFG.git
cd TFG/docker
./install.sh
```

Si el proceso finaliza correctamente y se ha seleccionado la opción de
ejecutar los servidores, el entorno estará funcionando y se podrá
conectar usando los datos mostrados por pantalla al final de dicho
procso.

=== Instalación Manual
<instalación-manual>
La instalación de forma manual requiere la ejecución de una serie de
comandos, cada parte del proyecto se compila y configura por separado.
Ver el documento #strong[#emph[README.md];] en cada uno de estos
apartados para compilar y configuar el proyecto de forma manual.

- Instalación del #link("trust-extension#compilación")[servidor MQTT]
- Instalación del #link("client#compilación")[cliente]
- Instalación del #link("web#compilación")[servidor web]
