#set heading(numbering: "1.", )
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
    grid(columns: 1, row-gutter: 24pt,"Diego Jesús Romero Luque")
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

#show heading: it => pagebreak(to: "odd", weak: true) + it


= Introducción

Hola a tofdaosifjadsklfjdalksfj adsfadsf
fadskfjdakslfjlkadñsjfa
fadsjfhadskfhkjfasd
adsfkjasdklf \
fads

= Estado del arte

Con el fin de establecer el contexto de este trabajo se revisarán los estudios relacionados con la confianza y el control de acceso en IoT.

Tras realizar una revisión exhaustiva de las investigaciones relevantes, se puede concluir que, aunque existe una gran cantidad de información respecto a la confianza en IoT, actualmente no existe un sistema que ofrezca la capacidad de integrarse fácilmente en un entorno existente.



#show: anexo

= Anexo1 <a1>
