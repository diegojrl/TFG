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
  counter(page).update(1)
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

== Cáclulo de confianza
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
== Resultados y conocimientos obtenidos
Tras todo el desarrollo de este proyecto, se puede concluir que los objetivos establecidos se han cumplido. Se ha diseñado e implementado un sistema de autorización basado en la confianza para el protocolo MQTT. Junto a este sistema se ha desarrollado una aplicación web y un cliente MQTT, con la finalidad de demostrar la funcionalidad del proyecto.

Durante el transcurso del proyecto he tratado con tecnologías que no conocía hasta el momento, como VisualVM, para tomar datos de rendimiento. Y otras tecnologías que conocía, pero nunca había utilizado, como docker y typst. Queda claro que mis conocimientos al respecto de estas tecnologías han mejorado considerablemente, pero aún queda mucho más que aprender sobre ellas.

Por último, quiero destacar que este es el primer proyecto personal, de este tamaño, que realizo. Aunque es cierto que ya había desarrolaldo algunos proyectos, ninguno de ellos abarcaba tantas tecnologías y plataformas diferentes, siendo todo un reto conseguir un buen funcionamiento entre la aplicación web, el servidor y la aplicación de consola.

== Dificultades
Nada más comenzar el proyecto, fue complicado seleccionar el servidor MQTT donde se implementaría el sistema, hay muchas opciones y todas similares entre ellas. Finalmente, tras repasar la documentación de cada opción se acabó seleccionando HiveMQ, por su buena calidad en los documentos y familiaridad con el lenguaje de programación.

Más adelante, durante las pruebas de rendimiento, se detectó un problema con el inicio de sesión, al conectar muchos clientes a la vez, el sistema no podía gestionar la gran cantidad de peticiones, resultando en muhcos clientes desconectados. Tras investigar el problema se ajustó la configuración de la conexión con el servidor LDAP y el problema quedó solucionado.

== Lineas futuras
Por el momento, el sistema funciona correctamente y con un buen rendimiento, pero esto no significa que no pueda mejorarse. A continuación se exploran algunas posibles mejoras.

Para comenzar, incorporar un modelo de inteligencia artificial puede ser beneficioso. Esto permitiría realizar un análisis de comportamiento de cada dispositivo y usuario, esta nueva información se puede añadir a el cálculo de confianza, resultando en un valor más preciso y fiable de confianza.

Añadir atributos extras en el control de acceso. En este momento el sistema proporciona varias características para configurar la autorización, aun así, sería beneficioso agregar más. Entre todas las posibilidades, una muy interesante sería añadir roles, donde se pueden agrupar usuarios o dispositivos.

El cálculo de confianza también podría ser mejorado. Actualmente se utilizan unos valores fijos para los pesos de cada atributo, permitir al usuario modificar estos pesos es una buena opción para mejorar el sistema.

Por último, sería interesante tener acceso a los datos históricos de una forma más cómoda. Por el momento, solo se puede acceder a el registro histórico a través de la base de datos. Una opción sería crear un nuevo apartado en la aplicación web para el visualizado y filtrado de esta información.

#page(
  header: none,
  bibliography(
    "bibliografia.bib",
    full: false,
    style: "ieee",
  ),
)


#show: anexo

#include "anexos/manuales/main.typ"
#include "anexos/manuales/trust-extension.typ"
#include "anexos/manuales/web.typ"
#include "anexos/manuales/client.typ"
#include "anexos/funcionesActivacion.typ"
#include "anexos/script-benchmarks.typ"