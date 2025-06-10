#set heading(numbering: "1.")
#set text(font: "Arial", size: 12pt, lang: "es")
#set page(numbering: "1", paper: "a4", margin: 2.5cm)
#set par(leading: 0.75em, justify: true, first-line-indent: (amount: 2em, all: true))
#let anexo(text) = {
  set heading(numbering: "A.", supplement: [Anexo])
  counter(heading).update(0)
  text
}
#{
  set align(center)
  text(size: 17pt, weight: "bold", "Autorización basada en la confianza para el protocolo MQTT")
  grid(
    columns: 1,
    row-gutter: 24pt,
    "Diego Jesús Romero Luque",
  )
  par(justify: false)[
    *Resumen* \
    Test
  ]
}
//#pagebreak()
//Indice
#outline(target: heading.where(numbering: "1."))
//Apendice
#outline(target: heading.where(numbering: "A."), title: [Anexo])

// Cada apartado aparece en una página nueva e impar
#show heading: it => if it.level == 1 { pagebreak(to: "odd", weak: true) + it } else { it }


= Introducción

Hola a tofdaosifjadsklfjdalksfj adsfadsf
fadskfjdakslfjlkadñsjfa
fadsjfhadskfhkjfasd
adsfkjasdklf \
fads
== Motivación
== Objetivos
== Tecnologías utilizadas
=== Extensión HiveMq
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
Estos son los requisitos definidos para la página web.
- RF1: Un usuario y contraseña válidos serán necesarios para acceder a la web.
- RF2: Se podrá acceder a la web desde cualquier dispositivo.
- RF3: Desde la vista principal se podrán observar todos los dispositivos conectados al servidor desde que inicia la conexión.
- RF4: Cada dispositivo tiene asociados datos sobre la confianza.
- RF5: Desde cada dispositivo se puede entrar en su menú de configuración.
- RF6:   


=== No funcionales
- RNF1: Tiempos de carga de la web bajos
- RNF2: Seguridad en el acceso, la conexión será cifrada.
- RNF3: Seguridad de la contraseña de los usuarios.
- RNF4: Facilidad de uso.


== Casos de uso
#bibliography("bibliografia.bib", style: "ieee")

#show: anexo

= Anexo1 <a1>
