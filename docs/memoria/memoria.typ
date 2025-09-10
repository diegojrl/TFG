#import "uma_esti_inf.typ": memoria
#import "resumen.typ": resumen, abstract


#show: memoria.with(
  degree: "Grado en Ingeniería Informática",
  title: "Autorización basada en la confianza para el protocolo MQTT",
  title_en: "Trust based authorization for the MQTT protocol",
  author: "Diego Jesús Romero Luque",
  tutors: "Davide Ferraris",
  department: " Lenguajes y Ciencias de la Computación",
  date: "10-09-2025",
  abstract: resumen,
  keywords: ("MQTT", "HiveMQ", "Autorización", "Confianza", "LDAP", "Aplicación Web", "Java", "Svelte", "TypeScript"),
  abstract_en: abstract,
  keywords_en: ("MQTT", "HiveMQ", "Authorization", "Trust", "LDAP", "Web Application", "Java", "Svelte", "TypeScript"),
)


#let anexo(text) = {
  set heading(numbering: "A.1.", supplement: [Anexo])
  counter(heading).update(0)
  set page(numbering: "I")
  text
}



= Introducción
#include "capitulos/introduccion/introduccion.typ"

== Objetivos
#include "capitulos/introduccion/objetivos.typ"

== Tecnologías utilizadas
#include "capitulos/introduccion/tecnologias.typ"

== Metodología
#include "capitulos/introduccion/metodologia.typ"

= Estado del arte
#include "capitulos/estado_arte.typ"


= Análisis y diseño
== Requisitos
#include "capitulos/analisis_diseño/requisitos.typ"

== Casos de uso
#include "capitulos/analisis_diseño/casos_de_uso.typ"

== Cálculo de confianza
#include "capitulos/analisis_diseño/calculo_confianza.typ"

== Base de datos
#include "capitulos/analisis_diseño/base_de_datos.typ"

== Protocolo de gestión de dispositivos
#include "capitulos/analisis_diseño/gestion_dispositivos.typ"

== Autenticación
#include "capitulos/analisis_diseño/autenticacion.typ"

== Autorización <autorizacion>
#include "capitulos/analisis_diseño/autorizacion.typ"

== Diseño web
#include "capitulos/analisis_diseño/diseño_web.typ"

== Diseño cliente
#include "capitulos/analisis_diseño/diseño_cliente.typ"

== Docker
#include "capitulos/analisis_diseño/docker.typ"


= Implementación
== Extensión HiveMQ
#include "capitulos/implementacion/extension.typ"

== Aplicación web
#include "capitulos/implementacion/web.typ"

== Cliente MQTT
#include "capitulos/implementacion/cliente.typ"


= Pruebas de rendimiento
#include "capitulos/pruebas-rendimiento.typ"


= Conclusiones
#include "capitulos/conclusion.typ"


#bibliography(
  "bibliografia.bib",
  full: false,
  style: "ieee",
)
#pagebreak(weak: true, to: "odd")


#show: anexo

#counter(page).update(1)
#include "anexos/manuales/main.typ"
#include "anexos/manuales/trust-extension.typ"
#include "anexos/manuales/web.typ"
#include "anexos/manuales/client.typ"
#include "anexos/funcionesActivacion.typ"
#include "anexos/script-benchmarks.typ"
