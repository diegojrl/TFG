== Metodología
Las pruebas de rendimiento se han realizado usando dos ordenadores, interconectados por una red LAN a 1gbps. El cliente y el servidor se han instalado usando el procedimiento especificado en @cliente-mqtt-adaptado-para-usar-el-protocolo-de-confianza y @extensión-hivemq respectivamente, las reglas de control de acceso se pueden encontrar en @benchmark-ac.

Los equipos utilizados tienen las siguientes características. Para el servidor, el sistema es una máquina Virtual con 4GB de ram y 2 núcleos del procesador. Para el sistema donde se ejecutan los clientes, tiene 32GB de memoria ram y un procesador Ryzen 7 5700.

La prueba consiste en conectar múltiples clientes al broker y observar la cantidad de mensajes que pueden publicar. Cada cliente se conecta usando cifrado TLS y con el mismo usuario. Cada cliente publica un mensaje de 512 bytes cada 5 milisegundos.

Se han realizado pruebas conectando 20, 100 y 500 clientes durante un periodo de 30 segundos, este proceso se realiza de automáticamente usando el script descrito en @benchmark-script. En una de las pruebas se usa la extensión desarrollada en este proyecto y en la otra se usa la extensión _Allow All Extension_ @allow-all-extension, que no comprueba ni los mensajes ni los usuarios. Cada prueba se ha repetido 4 veces y el usuario usado es _admin_.

Una vez tomados todos los datos, se ha creado un script en Python que descarta el primer resultado y toma la media de los resultados restantes como valor final.

== Resultados
#figure(
  image("../imagenes/rendimiento/bench-clientes-mensajes.svg", width: 90%),
  caption: "Mensajes publicados respecto a la cantidad de clientes."
) <f-pub-cl>
En esta primera gráfica se puede observar el número de mensjaes totales que se han publicado correctamente en cada caso. Cuando la cantidad de clientes es baja apenas se observa diferencia en los mensajes publicados, pero según va aumentando la cantidad de clientes se empieza a observar una degradación del rendimiento en el sistema. Para encontrar el motivo hay que mirar las siguientes gráficas. 

#figure(
  image("../imagenes/rendimiento/bench-clientes-recursos.svg", width: 90%),
  caption: "Uso de recursos frente a cantidad de clientes."
)
En este caso se observa el uso total del procesador y memoria ram respecto al número de clientes. En la @f-pub-cl se empezaba a ver un deterioro del rendimiento a partir de los 100 clientes y ahora se puede observar que a partir de esta cifra, el uso del procesador está alcanzando el límite del equipo.

#figure(
  image("../imagenes/rendimiento/bench-mensajes-recursos.svg", width: 90%),
  caption: "Uso de recursos frente a número de mensajes"
) <f-rec-pub>
Quizás más interesante es el consumo de recursos según la cantidad de mensajes publicados. En la @f-rec-pub se puede ver el uso de la memoria ram es superior cuando se usa el sistema de autorización basado en la confianza, pero  aumenta linealmente, igual que _Allow All Extension_. El uso del procesador parece que también aumenta linealmente hasta que alcanza valores cercanos al límite.

En resumen, la extensión desarrollada presenta el rendimiento esperado, peor que un sistema completamente inseguro, pero manteniendo la escalabilidad. Para obtener un desempeño mejor solo habría que aumentar los recursos del ordenador donde se ejecuta HiveMQ.