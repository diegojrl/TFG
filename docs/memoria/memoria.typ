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
  set heading(numbering: "A.", supplement: [Anexo])
  counter(heading).update(0)
  text
}



= Introducción

Hola a tofdaos \

#lorem(50)
fads
== Motivación
== Objetivos
== Tecnologías utilizadas
=== HiveMq
HiveMq @hiveMq es un servidor creado especialmente para gestionar los mensajes del protocolo MQTT. Este tipo de servidor se conoce comunmente como _broker_, 
ya que hace de intermediario para todos los mensajes que se envian.
=== Cliente web
=== Herramientas comunes

= Estado del arte

Con el fin de establecer el contexto de este trabajo se revisarán los estudios relacionados con la confianza y el control de
acceso en IoT.

- En @siot se presenta una arquitectura para integrar dispositivos inteligentes. El componente de gestión de confianza se basa
  en las relaciones previas con los dispositivos para seleccionar el dispositivo que mejor cumplirá con el servicio.

- Gestión de la confianza para la arquitectura basada en servicio @soa-iot, presenta un sistema de confianza distribuido para interconectar
  dispositivos IoT que proporcionan diferentes servicios, tomano como base las relaciones sociales entre los dispositivos.

- Trust-aware access control system for IoT (TACIoT) @taciot, propone un mecanismo de seguridad ligero para dispositivos IoT, tomando la confianza
  como un factor principal y usando la lógica difusa para su cálculo. Este sistema ha sido probado bajo condiciones reales y muestra un buen rendimiento.

- Trustee @trustee, presenta un sistema de gestión de la confianza usando múltiples factores. Aprovechando las ventajas que aportan las técnicas de aprendizaje
  computacional, el sistema tiene la capacidad de detectar anomalías e integrar esta información en el cálculo de la confianza.

- El estudio @IotAC presenta una revisión sobre los mecanismos para el control de acceso en IoT. Comparando las limitaciones y fortalezas de distintos modelos, entre ellos, Role Base Access Control(RBAC), Attribute Based Access Control(ABAC) o Capability Base Access Control(CapBAC). Destacando la inexistencia de una solución universal para el control de acceso enfocado a IoT.

Tras realizar una revisión exhaustiva de las investigaciones relevantes, se puede concluir
que, aunque existe una gran cantidad de información respecto a la confianza en IoT, actualmente
no existe un sistema que ofrezca la capacidad de integrarse fácilmente en un entorno existente.


= Desarrollo del proyecto
== Requisitos
=== Funcionales
==== Web
Estos son los requisitos definidos para la página web.
- RF1: Un usuario y contraseña válidos serán necesarios para acceder a la web.
- RF2: Se podrá acceder a la web desde cualquier dispositivo.
- RF3: Desde la vista principal se podrán observar todos los dispositivos conectados al servidor desde que inicia la conexión.
- RF4: Cada dispositivo tiene asociados datos sobre la confianza.
- RF5: Desde cada dispositivo se puede entrar en su menú de configuración.

==== Extensión
- RF6: El cliente debe aportar un usuario y contraseña válidos.
- RF7: El servidor comprueba la validez del usuario y contraseña mediante el protocolo LDAP.
- RF8: El servidor calculará la confianza a partir de los datos recabados de cada dispositivo.
- RF9: El servidor almacenará la información histórica sobre los clientes.
- RF10: El servidor enviará información sobre la confianza de cada cliente.
- RF11: El cliente tendrá la posibilidad de aportar su opinión sobre otros clientes conectados.
- RF12: Se podrán modificar algunas de las características usadas para el cálculo de la confianza.
- RF13: Se podrá configurar el cálculo de la confianza.
- RF14: El servidor podrá denegar o conceder acceso a los recursos según la configuración proporcionada por el usuario.
==== Cliente
- RF15: Un usuario y contraseña válidos serán necesarios para iniciar la conexión con el servidor.
- RF16: El cliente dará su opinión de otros clientes según la configuración del usuario.
- RF17: El cliente publicará mensajes cada cierto tiempo según la configuración del usuario.

=== No funcionales
==== Web
- RNF1: Tiempos de carga de la web bajos
- RNF2: Seguridad en el acceso, la conexión será cifrada.
- RNF3: Seguridad de la contraseña de los usuarios.
- RNF4: Facilidad de uso.

==== Extensión
- RNF5: La configuración del servidor será fácil e intuitiva.
- RNF6: La inclusión de las características en el sistema no tendrá un impacto excesivo en el rendimiento.
- RNF7: Los clientes podrán usar el servidor aun sin conocer las nuevas características
==== Cliente
- RNF8: El cliente será fácil de usar.
- RNF9: La configuración del cliente será sencilla.
- RNF10: El cliente  se conectará de forma segura al servidor, verificando los certificados.

== Casos de uso

#show: anexo
= Anexo1 <a1>
