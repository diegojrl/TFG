# Cliente MQTT adaptado para usar el protocolo de confianza
Este es un cliente MQTT adaptado para aprovechar las características del protocolo de confianza. Además permite el envío de mensajes de forma periódica.

## Compilación

### Dependencias
Para compilar el cliente son necesarias las siguientes dependencias:
- [GraalVM jdk](https://www.graalvm.org/jdk21/getting-started/#installing), con soporte para imágenes nativas.
- [Maven](https://maven.apache.org/install.html).

### Creación del ejecutable
Usando la línea de comandos:
- Entrar en la carpeta ***client***.
- Ejecutar el comando: `mvn package`.
- Si el proceso termina correctamente, el ejecutable se encotrará en el directorio ***./target/client***.
## Uso

Para poder usar el cliente se necesita el ejecutable creado en el apartado anterior y un archivo de configuración.

El cliente localiza el archivo de configuración con la variable de entorno `CONFIG_FILE`, que debe apuntar al archivo de configuración.

Archivo de ejemplo:

```yaml
host: "mqtt.example.com"
username: "user"
password: "password"
tls:
  useTls: true
  certificate: "/home/user/certs/store.jks"
  certificatePassword: "store-password"
opinions:
  web control admin: 1
  web control paco: 0.4
  cli: 0.9
messages:
  - topic: "my/test/topic/1" # Empty content
    interval: 12
    qos: 2
  - topic: "my/test/topic/2"  # With content
    content: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin a
     enim convallis enim ultricies ultrices."
    interval: 5
  - topic: "my/topic" # Only once
    content: ""

```
### Configuración básica
Esta configuración es **obligatoria** para que el cliente pueda conectarse al broker MQTT. Ejemplo:
```yaml
host: "mqtt.example.com"
username: "user"
password: "password"
```
| Configuración | Por defecto | Descripción                                                     |
| ------------- | ----------- | --------------------------------------------------------------- |
| host          | -           | Dirección del servidor, puede ser un nombre de dominio o una IP |
| username      | -           | Nombre de usuario enviado en MQTT                               |
| password      | -           | Contraseña del usuario MQTT                                     |

### Configuración de certificados

Si el certificado TLS del servidor no es de confianza, por ejemplo, si es autofirmado, es necesario configurar un *certificate store* en java. Para ello se usa el apartado *tls* en la configuración.

Ejemplo:
```yaml
tls:
  useTls: true
  certificate: "/home/user/certs/store.jks"
  certificatePassword: "store-password"
```
| Configuración       | Por defecto | Descripción                                      |
| ------------------- | ----------- | ------------------------------------------------ |
| useTls              | false       | Indica si se usa el cifrado TLS                  |
| certificate         | -           | Ruta del almacén de certificados, en formato jks |
| certificatePassword | -           | Contraseña del almacén de certificados           |

### Configuración de opiniones
Configura la opinión que envía este cliente sobre el resto de clientes. Este apartado es una lista de los identificadores de cada cliente con la opinión sobre este. Ejemplo:
```yaml
opinions:
  web control admin: 1
  cli: 0.01
```
| Configuración | Por defecto | Descripción                            |
| ------------- | ----------- | -------------------------------------- |
| *clientId*    | -           | Valor de la opinión, en el rango [0-1] |


### Configuración de mensajes
El cliente tiene la capacidad de publicar mensajes de forma automática, para ello es necesario configurarlo correctamente. Ejemplo:
```yaml
messages:
  - topic: "my/test/topic/1" # Empty content
    interval: 12
    qos: 2
  - topic: "my/test/topic/2"  # With content
    content: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin a
     enim convallis enim ultricies ultrices."
    interval: 5
```
| Configuración | Por defecto | Descripción                                                                                                                                      |
| ------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| topic         | -           | Topic de MQTT para publicar el mensaje (obligatorio)                                                                                             |
| interval      | null        | Intervalo en segundos para publicar el mensaje, Si es *null* indica que solo se enviará una vez. Si el valor es 0, el intervalo real será de 5ms |
| content       | ""          | Contenido del mensaje, en formato UTF-8                                                                                                          |
| qos           | null        | Valor de QoS para publicar el mensaje, puede ser 0, 1 o 2. El valor será 0 si qos es null                                                                            |
