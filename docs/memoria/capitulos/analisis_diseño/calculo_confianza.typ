La confianza es un concepto complejo y multidisciplinar que se aplica en en muchos campos, como la filosofía o la informática. Generalmente, se entiende como la expectativa de que una persona, entidad o sistema actuará en el futuro de manera honesta, predecible y fiable @iot-trust-survey.

En esta relación intervienen al menos dos actores, el que confia y el que recibe la confianza. La confianza es dinámica, puede variar según el contexto y las acciones realizadas por cada actor @iot-trust-survey. En este sistema se introduce un tercer actor, el broker MQTT, que actua como *intermediario* legítimo en la relación de confianza.

=== Atributos
Este modelo intenta ser lo más genérico posible, sin forzar una arquitectura específica, pero aprovechando las características y mensajes ya existentes en el protocolo MQTT. Es por ello que se han seleccionado los siguientes atributos para el cálculo de confianza.

==== Latencia <att-ping>
La *latencia* se refiere a el tiempo que tarda un dispositivo en recibir un mensaje y confirmar la recepción de este. Este atributo indica la media de la latencia en cada mensaje.

Para obtener la latencia media de un dispositivo, en cada mensaje que se envía hacia este cliente con $"QoS" > 0$, el broker guarda el instante de tiempo en el que se envía el mensaje y espera a su confirmación, _puback_ o _pubrec_ para $"QoS" = 1$ o $"QoS" = 2$ respectivamente. Además, cuando el broker recibe un mensaje _MQTT ReqPing_ publica un mensaje en el tópico _“tmgr/ping”_ para ese dispositivo, de esta forma, el dispositivo únicamente debe subscribirse al tópico con _QoS 1 o 2_ y el protocolo MQTT se encarga de el envío y recepción de todos estos mensajes. En el siguiente diagrama se puede visualizar el intercambio de mensajes durante el cálculo de la latencia.

#align(center)[
  #figure(
    image("../../imagenes/diagramas/secuencia/secuencia-ping.drawio.svg", width: 80%),
    caption: "Proceso de cálculo de latencia",
  )

]

El valor final de la latencia se obtiene de la siguiente forma:

- Si $"Latencia" ≤ "LATENCIA_MIN" "entonces" "Latencia" = "LATENCIA_MIN"$.

- Si $"Latencia" ≥ "LATENCIA_MAX" "entonces" "Latencia" = "LATENCIA_MAX"$.

- En otros casos $"Latencia"$.

Los valores de $"LATENCIA_MIN"$ y $"LATENCIA_MAX"$ son configurables, ver más en @configuración-general.

==== Seguridad <att-seguridad>
Esta característica tiene en cuenta la *seguridad de la conexión* entre el cliente y el servidor, la seguridad se clasifica como buena o mala. Será buena si la conexión es cifrada (TLS) o si la dirección IP se encuentra en un rango configurado como seguro, ver más en @configuración-general.

==== Tasa de errores <att-errors>
La tasa errores indica con un porcentage el número de mensajes que han sido reenviados frente a el total de los mensajes publicados. Tiene un valor por defecto de 50%.

Usando la misma información que en la detección de Latencia, comprueba las veces que se envía cada mensaje. Si un mensaje se envía varias veces, aumenta la tasa de fallos.

$ "tasa de errores" = "paquetes_reenviados" / "mensajes_publicados" $

==== Reputación <att-rep>
La reputación se refiere a la relación entre dispositivos, donde cada dispositivo puede aportar su *opinión* sobre otro.

Para calcular el valor de la reputación de cada dispositivo es necesario disponer con cierta información de antemano. Principalmente las opiniones de los dispositivos entre ellos, debe ser un valor entre 0 y 1. Para obtener estos datos cada dispositivo debe proporcionarlos individualmente usando el siguiente procedimiento:

#figure(
  image("../../imagenes/diagramas/secuencia/secuencia-conexion.drawio.svg"),
  caption: "Procedimiento de obtención de opiniones",
)

Primero un nuevo dispositivo se conecta al broker. Tras esto el broker automáticamente envía un aviso de este hecho al resto de clientes y cada cliente, opcionalmente, aporta un valor de opinión sobre este nuevo dispositivo. En ningún caso un cliente podrá opinar sobre él mismo.

Al mismo tiempo, el nuevo cliente puede subscribirse a los avisos de dispositivos conectados. Al realizar esta acción, recibirá un aviso de todos los dispositivos conectados al broker actualmente y, al igual que el resto de clientes, puede aportar su propia opinión sobre el resto.

Una vez que se obtienen todos los datos necesarios, la reputación de cada dispositivo se calcula con la siguiente fórmula:

$ R_N = sum_(i=1)^N O_i*T_i $

Donde $R_j$ es la reputación del cliente $j$, $N$ es el número de opiniones que afectan a el dispositivo actualmente, $O_i$ es la opinión que ha aportado el dispositivo $i$ sobre el cliente $j$ y $T_i$ es el valor de la confianza del dispositivo $i$ en el momento del cálculo.

=== Lógica difusa <att-trust>
Tras adquirir la información de los atributos de cada cliente se usará la lógica difusa para obtener un *valor final de confianza* para el dispositivo. Primero, para cada atributo, se establecen unos términos asociados a un mnemónico y una función de pertenencia, ver más en @configuración-de-reglas-difusas. Por ejemplo, para la reputación, se obtiene la siguiente función de pertenencia:
#figure(
  image("../../imagenes/funcionesActivacion/reputacion.svg"),
  caption: "Función de activación de reputación",
)

En @funciones-pertenencia se pueden observar las funciones de pertenencia de cada atributo.

Una vez definidas las funciones de pertenencia, el conjunto difuso se calcula con las reglas definidas en el archivo de configuración, ver más en @configuración-de-reglas-difusas. Luego estas reglas se agregan usando una media ponderada y el mínimo como función de activación. Los pesos de la media ponderada indican el grado de importancia de cada atributo en el valor final de la confianza. La importancia de cada atributo se ha establecido como se muestra en la siguiente tabla:
#figure(
  align(center)[#table(
    columns: 2,
    align: (col, row) => (auto, auto).at(col),
    inset: 6pt,
    [Atributo], [Peso],
    [Seguridad], [0.3],
    [Latencia], [0.15],
    [Tasa de errores], [0.3],
    [Reputación], [0.25],
  )],
)