#import "uma_esti_inf.typ": memoria


#let resumen = lorem(200)

#let abstract = lorem(200)


#show: memoria.with(
  degree: "Grado en Ingeniería Informática",
  title: "Autorización basada en la confianza para el protocolo MQTT",
  title_en: "Trust based authorization for the MQTT protocol",
  author: "Diego Jesús Romero Luque",
  tutors: "Davide Ferraris",
  department: " Lenguajes y Ciencias de la Computación",
  date: "20-08-2025",
  abstract: resumen,
  keywords: ("MQTT", "HiveMq", "Autorización", "Confianza", "LDAP", "Aplicación Web", "Java", "Svelte", "TypeScript"),
  abstract_en: abstract,
  keywords_en: ("MQTT", "HiveMq", "Authorization", "Trust", "LDAP", "Web Application", "Java", "Svelte", "TypeScript"),
)


#let anexo(text) = {
  set heading(numbering: "A.1.", supplement: [Anexo])
  counter(heading).update(0)
  text
}



= Introducción
#lorem(50)

== Motivación
#lorem(50)

== Objetivos
Este trabajo tiene como objetivo implementar un sistema de control de acceso basado en la confianza para IoT [5] usando el protocolo MQTT como base. La solución incluirá mecanismos de autenticación y autorización de dispositivos. Además, se implementará una interfaz web que permita a los usuarios visualizar y gestionar los parámetros relevantes del sistema.

El TFG tiene varios objetivos, todos relacionados con un sistema de control de acceso para IoT basando en la confianza y el protocolo MQTT:
- Creación de un modelo de confianza apropiado para MQTT.
- Implementar un sistema de control de acceso como una extensión del broker HiveMQ, basado en el modelo de confianza descrito anteriormente.
- Desarrollo de una interfaz web donde se podrán visualizar y editar ciertos atributos del modelo.
== Tecnologías utilizadas
#include "tecnologias.typ"

= Estado del arte
#include "estado_arte.typ"


= Análisis y diseño
== Requisitos
#include "requisitos.typ"

== Casos de uso
#include "casos_de_uso.typ"

== Cáclulo de confianza
La confianza es un concepto complejo y multidisciplinar que se aplica en en muchos campos, como la filosofía o la informática. Generalmente, se entiende como la expectativa de que una persona, entidad o sistema actuará en el futuro de manera honesta, predecible y fiable @iot-trust-survey.

En esta relación intervienen al menos dos actores, el que confia y el que recibe la confianza. La confianza es dinámica, puede variar según el contexto y las acciones realizadas por cada actor @iot-trust-survey. En este sistema se introduce un tercer actor, el broker MQTT, que actua como intermediario legítimo en la relación de confianza.

=== Atributos
Este modelo intenta ser lo mas generico posible, sin forzar una arquitectura específica, pero aprovechando las características y mensajes ya existentes en el protocolo MQTT. Es por ello que se han seleccionado los siguientes atributos para el cálculo de confianza.

==== Latencia
La latencia se refiere a el tiempo que tarda un dispositivo en recibir un mensaje y confirmar la recepción de este. Este atributo indica la media de la latencia en cada mensaje.

Para obtener la latencia media de un dispositivo, en cada mensaje que se envía hacia este cliente con $"Qos" > 0$, el broker guarda el instante de tiempo en el que se envía el mensaje y espera a su confirmación, _puback_ o _pubrec_ para $"Qos" = 1$ o $"Qos" = 2$ respectivamente. Además, cuando el broker recibe un mensaje _MQTT ReqPing_ publica un mensaje en el tópico _“tmgr/ping”_ para ese dispositivo, de esta forma, el dispositivo únicamente debe subscribirse al tópico con _QoS 1 o 2_ y el protocolo MQTT se encarga de el envío y recepción de todos estos mensajes. En el siguiente diagrama se puede visualizar el intercambio de mensajes durante el cálculo de la latencia.

#align(center)[
  #figure(
    image("imagenes/diagramas/secuencia/secuencia-ping.drawio.svg", width: 80%),
    caption: "Proceso de cálculo de latencia",
  )

]

El valor final de la latencia se obtiene de la siguiente forma:

- Si $"Latencia" ≤ "LATENCIA_MIN" "entonces" "Latencia" = "LATENCIA_MIN"$
- Si $"Latencia" ≥ "LATENCIA_MAX" "entonces" "Latencia" = "LATENCIA_MAX"$
- En otros casos Latencia

Los valores de $"LATENCIA_MIN"$ y $"LATENCIA_MAX"$ son configurables, ver más en @configuración-general

==== Seguridad
Esta característica tiene en cuenta la seguridad de la conexión entre el cliente y el servidor, la seguridad se clasifica como buena o mala. Será buena si la conexión es cifrada (TLS) o si la dirección IP se encuentra en un rango configurado como seguro, ver más en @configuración-general.

==== Tasa de errores
La tasa errores indica con un porcentage el número de mensajes que han sido reenviados frente a el total de los mensajes publicados. Tiene un valor por defecto de 50%.

Usando la misma información que en la detección de Latencia, comprueba las veces que se envía cada mensaje. Si un mensaje se envía varias veces, aumenta la tasa de fallos.

$ "tasa de errores" = "paquetes_reenviados" / "mensajes_publicados" $

==== Reputación
La reputación se refiere a la relación entre dispositivos, donde cada dispositivo puede aportar su opinión sobre otro.

Para calcular el valor de la reputación de cada dispositivo es necesario disponer con cierta información de antemano. Principalmente las opiniones de los dispositivos entre ellos, debe ser un valor entre 0 y 1. Para obtener estos datos cada dispositivo debe proporcionarlos individualmente usando el siguiente procedimiento:

#figure(
  image("imagenes/diagramas/secuencia/secuencia-conexion.drawio.svg"),
  caption: "Procedimiento de obtención de opiniones",
)

Primero un nuevo dispositivo se conecta al broker. Tras esto el broker automáticamente envía un aviso de este hecho al resto de clientes y cada cliente, opcionalmente, aporta un valor de opinión sobre este nuevo dispositivo. En ningún caso un cliente podrá opinar sobre él mismo.

Al mismo tiempo, el nuevo cliente puede subscribirse a los avisos de dispositivos conectados. Al realizar esta acción, recibirá un aviso de todos los dispositivos conectados al broker actualmente y, al igual que el resto de clientes, puede aportar su propia opinión sobre el resto.

Una vez se obtienen todos los datos necesarios, la reputación de cada dispositivo se calcula con la siguiente fórmula:

$ R_N = sum_(i=1)^N O_i*T_i $

Donde $R_j$ es la reputación del cliente $j$, $N$ es el número de opiniones que afectan a el dispositivo actualmente, $O_i$ es la opinión que ha aportado el dispositivo $i$ sobre el cliente $j$ y $T_i$ es el valor de la confianza del dispositivo $i$ en el momento del cálculo.

=== Lógica difusa
Tras adquirir la información de los atributos de cada cliente...
- pesos...
- valores logica difusa, funciones ...
- reglas ...
- librería f4j
=== Modificación de atributos <modificacion-atributos>
Algunos de los atributos descritos anteriormente permiten ser modificados usando mensajes de MQTT.

- *Latencia*: se reserva el tópico _control/mod/{client_id}/ping_. El contenido del mensaje serán 4 bytes representando un número entero. Si el número está fuera del rango configurado se usará el valor máximo o mínimo según corresponda.

- *Tasa de errores*: se usa el tópico _control/mod/{client_id}/failPctr_. El contenido del mensaje serán 4 bytes representando un número entero $n$ donde  $n in [0,100]$. Si $n in.not [0,100]$, se usará el valor $0$ o $100$ según corresponda.

- *Reputación*: se emplea el tópico _control/mod/{client_id}/rep_. Este mensaje no tiene contenido, indica que se deben eliminar todas las opiniones que afectan al dispositivo _client_id_, resultando en una reputación con valor $0$.


== Gestión de datos
=== Clase DeviceTrustAttributes -- impementacion??
=== Base de datos
#align(center)[
  #figure(
    image("imagenes/diagramas/sql/db.png", width: 80%),
    caption: "Diagrama de entidad",
  )

]
=== Vista de control? <vista-control>
#figure(
  image("imagenes/diagramas/secuencia/secuencia-control-view.drawio.svg"),
  caption: "Protocolo de obtención de información",
)
== Autenticación
El proceso para autenticar un usuario es relativamente sencillo. Se utiliza un nombre de usuario y una contraseña para acceder al broker. Estos datos son enviados, primero mediante el protocolo MQTT hacia el broker, y más adelante, usando el protocolo Lightweight Directory Access Protocol (LDAP), a un servicio externo.

Dicho servicio se encargará de la gestión de los usuarios y toda la información relacionada. Es desde este servicio donde hay que crear nuevos usuarios, actualizar las contraseñas y establecer políticas de seguridad para las contraseñas. Será el proveedor de LDAP el cual se ocupe de almacenar las claves y otros datos de forma segura, siguiendo las buenas prácticas en el ámbito. La conexión con este servicio es configurable, ver @configuración-general.

== Autorización <autorizacion>
El subsistema de autorización analiza cada mensaje de publicación que recibe el broker y decide si permite o rechaza el mensaje en cuestión. Para ello
se ha diseñado un sistema de control de acceso basado en atributos (ABAC).

El sistema ABAC se considera uno de los modelos más apropiado para IoT. Esto se debe a su capacidad de incluir atributos adicionales de forma flexible  @IotAC. El sistema sigue la siguiente estructura:

#figure(
  image("imagenes/diagramas/ac-arch.png", width: 70%),
  caption: [Arquitectura del sistema de control de acceso @IotAC],
)

- *PIP* o punto de información de políticas se encarga de obtener la información del dispositivo al cual le afecta la política.
- *PAP* o punto de administración de políticas se ocupa de alamcenar y proporcionar las reglas que tienen efecto en cada caso.
- *PDP* o punto de decisión de políticas se encarga de tomar la decisión de acceso, aceptando o rechazando la publicación de mensajes.
- *PEP* o punto de cumplimiento de políticas se ocupa de aplicar las decisiones tomadas. Cuando un mensaje es rechazado el cliente que lo envía es forzosamente desconectado del broker.

Los atributos disponibles para el uso en las reglas de autorización son los siguientes:
- clientId: Identificador del dispositivo.
- trust: Valor de la confianza del dispositivo.
- username: Nombre de usuario utilizado en el último inicio de sesión del cliente.
- qos: Valor de la calidad de servicio usada en la publicación del mensaje.
- retain: Valor que indica si el mensaje a publicar debe ser retenido.

Para configurar las reglas se usa un archivo en formato YAML. En este archivo se introducen las reglas de control de acceso asocidadas a cada _topic_. El diseño se inspira en el funcionamiento de algunos firewalls de red como iptables o nftables, donde la regla que se aplica es la primera que coincide. Para más información sobre la configuración de las reglas ver @configuración-de-autorización

== Diseño web
La idea para la aplicación web es que proporcione la funcionalidad de la forma más simple posible, pero con un diseño moderno. Por tanto, la página web dispondrá de una página principal, donde se podrá visualizar los clientes conectados al broker y acceder a la configuración de cada dispositivo.

Además la pantalla principal deberá ser visualizada correctamente independientemente del dispositivo desde el que se accede.

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("imagenes/web/principal-pequeño.png", height: 55%),
    ),
    grid.cell(
      colspan: 3,
      image("imagenes/web/principal-mediano.png", height: auto),
    ),
    grid.cell(
      colspan: 3,
      image("imagenes/web/principal-grande.png", height: auto),
    ),
  ),
  caption: "Pantalla principal, en diferentes tamaños.",
)

Para recibir la información que se muestra en la pantalla principal, la aplicación web se conecta a el servidor MQTT usando WebSocket y se subscribe a los tópicos correspondientes para recibir los datos necesarios (@vista-control). La dirección del broker debe ser especificada en la configuración de la aplicación web, ver más en @cliente-web. Para iniciar la conexión es necesario que el usuario proporcione los datos para iniciar sesión, con este proposito se crea una ruta en la página web que se visualizará de la siguiente manera:

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("imagenes/web/inicio-sesion-pequeño.png", height: 30%),
    ),
    grid.cell(
      colspan: 3,
      rowspan: 2,
      image("imagenes/web/inicio-sesion-grande.png", width: auto),
    ),
  ),
  caption: "Inicio de sesión.",
)

Por último, la aplicación web tiene la capacidad de modificar algunos de los atributos de los dispositivos. Para ello el usuario deberá pulsar el botón que se encuentra en la esquina superior derecha de cada dispositivo, apareciendo la siguiente ventana:

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("imagenes/web/editar-pequeño.png", height: 30%),
    ),
    grid.cell(
      colspan: 3,
      rowspan: 2,
      image("imagenes/web/editar-grande.png", width: auto),
    ),
  ),
  caption: "Edición de atributos.",
)

Este menú publicará los mensajes apropidaos según el atributo que se intente modificar (@modificacion-atributos). Para que el procedimiento funcione correctamente el cliente debe tener otorgados los permisos suficientes para cada tópico, ver @autorizacion.

== Diseño cliente
El cliente MQTT es una adaptación de un cliente ya existente @hiveMqClient, pero agregando la nueva funcionalidad que ofrece el sistema. El objetivo principal de este cliente es realizar las pruebas de las diferentes funciones que se han añadido y adicionalmente evaluar el rendimiento del sistema.

Además, el cliente, para ser usado en pruebas de rendimiento, deberá ser lo más ligero posible. Con este objetivo, en vez de empaquetar el cliente en un archivo jar para JVM, se compila a código nativo usando GraalVM. Esto aporta ventajas como un inicio casi instantáneo, menor uso de recursos y un rendimiento más consistente desde el inicio @graalvm.

El cliente debe ser configura
= Implementación
= Conclusiones
#show: anexo
#include "manuales/main.typ"
#include "manuales/trust-extension.typ"
#include "manuales/web.typ"
#include "manuales/client.typ"
