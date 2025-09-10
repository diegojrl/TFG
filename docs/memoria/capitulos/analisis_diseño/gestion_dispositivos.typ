Se ha desarrollado un protocolo que permita monitorizar y alterar el estado de los atributos asociados a cada cliente. El protoclo consta de dos secciones, la primera para recibir información y la segunda para modificar los atributos.
=== Visualización <vista-control>
Para recibir información de el dispositivo con id de cliente _client_id_, el interesado debe subscribirse al tópico _control/view/{client_id}_. Una vez subscrito recibirá la información de este dispositivo repetidamente en un periodo de 15 segundos. En el siguiente diagrama se puede observar el intercambio de mensajes:
#figure(
  image("../../imagenes/diagramas/secuencia/secuencia-control-view.drawio.svg"),
  caption: "Protocolo de obtención de información",
)
Se enviará la siguiente información de cada dispositivo:

- *Latencia* (@att-ping).

- *Tasa de errores* (@att-errors).

- *Tipo de red*. Indica si la conexión se incluye en una red de confianza (@att-seguridad).

- *Seguridad de red*. Indica si la conexión es cifrada (@att-seguridad).

- *Reputación* (@att-rep).

- *Confianza* (@att-trust).

=== Modificación <modificacion-atributos>
Algunos de los atributos descritos anteriormente permiten ser modificados usando mensajes de MQTT.

- *Latencia*: se reserva el tópico _control/mod/{client_id}/ping_. El contenido del mensaje serán 4 bytes representando un número entero. Si el número está fuera del rango configurado se usará el valor máximo o mínimo según corresponda.

- *Tasa de errores*: se usa el tópico _control/mod/{client_id}/failPctr_. El contenido del mensaje serán 4 bytes representando un número entero $n$ donde  $n in [0,100]$. Si $n in.not [0,100]$, se usará el valor $0$ o $100$ según corresponda.

- *Reputación*: se emplea el tópico _control/mod/{client_id}/rep_. Este mensaje no tiene contenido, indica que se deben eliminar todas las opiniones que afectan al dispositivo _client_id_, resultando en una reputación con valor $0$.