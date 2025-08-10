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
  date: "20-07-2025",
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
Que es la confianza?? ....

=== Atributos
Este modelo intenta ser lo mas generico posible, sin forzar una arquitectura específica, pero aprovechando las características y mensajes ya existentes en el protocolo MQTT.

==== Latencia
La latencia se refiere a el tiempo que tarda un dispositivo en recibir un mensaje y confirmar la recepción de este. Este atributo indica la media de la latencia en cada mensaje.

==== Seguridad
Esta característica tiene en cuenta la seguridad de la conexión entre el cliente y el servidor, la seguridad se clasifica como buena o mala. Será buena si la conexión es cifrada (TLS) o si la ip se encuentra en un rango configurado como de confianza, ver más en @configuración-general.

==== Tasa de errores
La tasa errores indica con un porcentage el númerp de mensajes que han sido reenviados frente a el total de los mensajes publicados. 

==== Reputación

=== Cálculo y modificación de atributos
==== Tasa de errores
Tiene un valor por defecto de 50%.

Usando la misma información que en la detección de Latencia (packetId), comprueba las veces que se envía cada mensaje. Si un mensaje se envía varias veces, aumenta la tasa de fallos.
$ text("tasa de errores") = frac(text("paquetes_reenviados"), text("mensajes_publicados")) $
=== Lógica difusa
== Intercambio de información
Para obtener la latencia media de un dispositivo, en cada mensaje que se envía hacia este cliente con $Q o S > 0$, el broker guarda el instante de tiempo en el que se envía el mensaje y espera a su confirmación. Además, cuando el broker recibe un mensaje _MQTT ReqPing_ publica un mensaje en el topic _“tmgr/ping”_ para ese dispositivo, de esta forma, el dispositivo únicamente debe subscribirse al tópico con _QoS 1 o 2_ y el protocolo MQTT se encarga de el envío y recepción de todos estos mensajes. En el siguiente diagrama se puede visualizar el intercambio de mensajes durante el cálculo de la latencia.

#align(center)[
  #image("imagenes/diagramas/secuencia/secuencia-ping.drawio.svg", width: 80%)
]

El valor final de la latencia se obtiene de la siguiente forma:

- Si Latencia ≤ LATENCIA_MIN entonces Latencia = LATENCIA_MIN
- Si Latencia ≥ LATENCIA_MAX entonces Latencia = LATENCIA_MAX
- En otros casos Latencia

Los valores de LATENCIA_MIN y LATENCIA_MAX son configurables, ver más en @configuración-general
#image("imagenes/diagramas/secuencia/secuencia-conexion.drawio.svg")
#image("imagenes/diagramas/secuencia/secuencia-control-view.drawio.svg")

== Arquitectura
= Implementación
= Conclusiones
#show: anexo
#include "manuales/main.typ"
#include "manuales/trust-extension.typ"
#include "manuales/web.typ"
#include "manuales/client.typ"
