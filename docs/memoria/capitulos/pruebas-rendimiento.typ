== Metodología
Las pruebas de rendimiento se han realizado usando dos ordenadores, interconectados por una red LAN a 1gbps. El cliente y el servidor se han instalado usando el procedimiento especificado en @cliente-mqtt-adaptado-para-usar-el-protocolo-de-confianza y @extensión-hivemq respectivamente.

Los equipos utilizados tienen las siguientes características. Para el servidor, el sistema es una máquina Virtual con 4GB de ram y 2 núcleos del procesador. Para el sistema donde se ejecutan los clientes, tiene 32GB de memoria ram y un procesador Ryzen 7 5700.

La prueba consiste en conectar múltiples clientes al borker y observar la cantidad de mensajes que pueden publicar. Cada cliente se conecta usando cifrado TLS y con el mismo usuario. Cada cliente publica un mensaje de 512 bytes cada 5 milisegundos.

Se han realizado pruebas conectando 20, 100 y 500 clientes durante un periodo de 30 segundos. Una usando la extension desarrollada en este proyecto y otra usando la extension _Allow All Extension_ @allow-all-extension. Cada prueba se ha repetido 4 veces, descartando el primer resultado y tomando la media de los resultados restantes.
== Resultados
#figure(
  image("../imagenes/rendimiento/bench-clientes-mensajes.svg"),
  caption: "Mensajes publicados respecto a la cantidad de clientes."
)

#figure(
  image("../imagenes/rendimiento/bench-clientes-recursos.svg"),
  caption: "Uso de recursos frente a cantidad de clientes."
)

#figure(
  image("../imagenes/rendimiento/bench-mensajes-recursos.svg"),
  caption: "Uso de recursos frente a número de mensajes"
)