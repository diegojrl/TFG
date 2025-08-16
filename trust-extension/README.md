# Extensión HiveMQ
Esta extensión proporciona nuevas características al [broker MQTT HiveMQ](https://github.com/hivemq/hivemq-community-edition), escrito en [java](https://es.wikipedia.org/wiki/Java_(lenguaje_de_programaci%C3%B3n)). Implementa un sistema de autenticación apoyandose en el protocolo [LDAP](https://es.wikipedia.org/wiki/Protocolo_ligero_de_acceso_a_directorios). Añade las operaciones necesarias para calcular la confianza por cada dispositivo conectado. Además permite tomar decisiones de autorización basandose en esta información.

## Compilación

### Dependencias
Para compilar la extensión de HiveMQ son necesarias las siguientes dependencias:
- Java jdk 21 o superior.
- [Maven](https://maven.apache.org/install.html).

### Generar extensión
Para generar los archivos necesarios en la instalación de la extensión se ejecuta el siguente comando:

```bash
mvn package
```
Esto crea un archivo comprimido en la ruta ***./target/trust-extension-1.0-distribution.zip***. Este es el único fichero necesario para la instalación


## Uso

### Instalación
Antes de instalar la extensión es necesario tener un servidor MQTT de HiveMQ instalado y configurado correctamente, para más información, ver la [wiki de HiveMQ](https://github.com/hivemq/hivemq-community-edition/wiki). Una de las opciones de configuración del servidor especifica el directorio para las extensiones, este directorio se referenciará como ***./extensions***.

Una vez el servidor está configurado, se puede proceder a instalar la extensión. Para ello, simplemente se descomprime el archivo generado anteriormente, en la ruta *./extensions*. Esto debe crear una nueva carpeta dentro de *./extensions* llamada *trust-extension*.

Con esto la extensión está instalada e iniciará la próxima vez que arranque el servidor. Pero para que funcione correctamente es necesario configurar varios parmámetros. Todos los archivos de configuración se encuentran dentro de la carpeta de la extensión, en la ruta ***./extensions/trust-extension/conf/***.

### Configuración general
En este archivo de configuración se establecen varios ajustes que afectan al cálculo de confianza y la autenticación de usuarios. El fichero se localiza dentro de la carpeta de configuración mencionada anteriormente, con nombre *config.yaml* 

Configuración por defecto:

```yaml
delay_max: 500
delay_min: 20
ldap:
  url: "ldap://ldap:3890"
  baseDn: "ou=people,dc=example,dc=com"
  auth: "simple"
trusted_networks:
  - 192.168.55.0/24
  - 172.16.0.0/12
  - ::1/128
  - localhost/8
```

| Configuración    | Por defecto | Descripción                                                                                                                                                                                                                                      |
| ---------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| delay_min        | 20          | Establece el valor mínimo para la latencia de un dispositivo. Medido en milisegundos.                                                                                                                                                            |
| delay_max        | 500         | Establece el valor máximo para la latencia de un dispositivo. Medido en milisegundos.                                                                                                                                                            |
| ldap.url         | -           | URL del servidor LDAP, en formato (ldap/ldaps)://direccion:puerto                                                                                                                                                                                |
| ldap.baseDn      | -           | Dominio usado para realizar las búsquedas de usuarios                                                                                                                                                                                            |
| ldap.auth        | -           | Valores posibles: "none", "simple", "strong". Este valor depende de la configuración del servidor LDAP                                                                                                                                           |
| trusted_networks | -           | Lista de direcciones IPv4, IPv6 o nombres de domino, junto a una máscara de red. Estás redes se considerarán seguras y, por tanto, las conexiones desde ellas se consideran igual de seguras que una conexión cifrada. Puede ser una lista vacía |


### Configuración de reglas difusas
La extensión utiliza la lógica difusa para integrar la información recabada de cada dispositivo y calcular un valor final de confianza. Las reglas que se aplican en este cálculo son modificables desde el fichero *trustRules.flc*.

Configuración por defecto:

```
IF security low THEN trust low
IF security high THEN trust high

IF delay low THEN trust high
IF delay medium THEN trust medium
IF delay high THEN trust low

IF failed medium THEn trust medium
If failed low THEN trust high
IF failed high THEN trust low

IF reputation high THEN trust high
IF reputation medium THEN trust medium
IF reputation low THEN trust low
```

El archivo sigue el siguente formato:

```
IF (variable) (valor) THEN (variable) (valor)
```

Existen las siguientes variables y sus valores asociados:

| Variable   | Valores           | Descripción                                                                                                                                                                         |
| ---------- | ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| security   | low, high         | Indica el nivel de seguridad de la conexión. El valor será *low* si la conexión no está cifrada y no proviene de una red de confianza, en cualquier otro caso, el valor será *high* |
| delay      | low, medium, high | Indica la latencia de envio de mensaje hasta la recepción de respuesta. Los valores dependen de la [configuración general](#configuración-general)                                  |
| failed     | low, medium, high | Indica la cantidad de mensajes que han debido ser reenviados                                                                                                                        |
| reputation | low, medium, high | Indica la media de las opiniones ponderadas, aportadas por el resto de dispositivos.                                                                                                |
| trust      | low, medium, high | Indica el valor final de confianza                                                                                                                                                  |

El cálculo de confianza se realiza usando la función mínimo como función de activación y una media ponderada como función de agregación. La ponderación aplicada es la siguiente:

| Variable   | Peso |
| ---------- | ---- |
| security   | 0.3  |
| delay      | 0.15 |
| failed     | 0.3  |
| reputation | 0.25 |

### Configuración de autorización 
Por último, es necesario configurar las reglas de autorización y adaptarlas para el correcto funcionamiento del broker MQTT. Esta configuración se situa en el archivo *accessControl.yaml*.

Las reglas en este fichero se aplican en el orden que aparecen y solo se tiene en cuenta la primera regla que se pueda aplicar. Si no se declara ninguna regla o ninguna es aplicable, la acción por defecto es bloquear la conexión.

El formato para los tópicos MQTT sigue las mismas reglas que el broker HiveMQ, para más información ver [HiveMQ mqtt topics](https://www.hivemq.com/blog/mqtt-essentials-part-5-mqtt-topics-best-practices/). Además se aceptan los siguentes sustituidores: _{{clientid}}_ y _{{username}}_ que se sustituyen por el id del cliente y el nombre de usuario respectivamente.

En la configuración por defecto, las cinco primeras reglas son necesarias para el correcto funcionamiento de la extensión, aunque se pueden adaptar para mejorar la seguridad. Se recomienda modificar o eliminar la segunda regla para no permitir modificar valores de la confianza a dispositivos o usuarios no autorizados. La configuración por defecto es la siguiente:

```yaml
permissions:
  - topic: control/view/+
    rules:
      - action: subscribe
  - topic: control/mod/#
    rules:
      - action: publish
        username: admin
  - topic: tmgr/ping
    rules:
      - action: subscribe
        qos: one_two
  - topic: "tmgr/rep/${{clientid}}"
    rules:
      - allow: false
        action: publish
  - topic: "tmgr/rep/+"
    rules:
      - allow: true
  - topic: test/#
    rules:
      - clientId: cli
        username: test
        trust: 0.5
      - username: paco
        allow: false
      - username: admin
  - topic: my/#
    rules:
      - username: test
```

Cada tópico lleva asociada una lista de reglas, cada regla se separa por el caracter `-` y tienen las siguientes opciones:

| Opción    | Por defecto | Descripción                                                                                                                                                                                                                                                                        |
| --------- | ----------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| allow     | true        | Indica si esta regla permite o rechaza la conexión                                                                                                                                                                                                                                 |
| clientId  | -           | Indica el id del cliente                                                                                                                                                                                                                                                           |
| trust     | -           | Indica el umbral de confianza a cumplir para aplicar la regla. Si la regla tiene *allow true* el nivel de confianza tendrá que ser mayor que el umbral. Si la regla especifica *allow false* entonces el nivel de confianza deberá ser menor al umbral para poder aplicar la regla |
| username  | -           | Indica el nombre de usuario                                                                                                                                                                                                                                                        |
| action    | all         | Indica la acción a realizar, puede ser: *publish*, *subscribe*, *all*                                                                                                                                                                                                              |
| qos       | any         | Indica los niveles de QoS permitidos, los valores posibles son: *zero*, *one*, *two*, *zero_one*, *one_two*, *zero_two*, *any*                                                                                                                                                     |
| retention | any         | Indica si el mensaje puede ser retenido. Posibles valores: *yes*, *no*, *any*                                                                                                                                                                                                      |

Una regla solo es aplicable cuando se cumplen todas las condiciones especificadas en dicha regla. 