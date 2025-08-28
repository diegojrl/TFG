El subsistema de autorización analiza cada mensaje de publicación que recibe el broker y decide si permite o rechaza el mensaje en cuestión. Para ello
se ha diseñado un sistema de control de acceso basado en atributos (ABAC).

El sistema ABAC se considera uno de los modelos más apropiado para IoT. Esto se debe a su capacidad de incluir atributos adicionales de forma flexible  @IotAC. El sistema sigue la siguiente estructura:

#figure(
  image("../../imagenes/diagramas/ac-arch.png", width: 70%),
  caption: [Arquitectura del sistema de control de acceso @IotAC],
)

- *PIP* o punto de información de políticas se encarga de obtener la información del dispositivo al cual le afecta la política.
- *PAP* o punto de administración de políticas se ocupa de almacenar y proporcionar las reglas que tienen efecto en cada caso.
- *PDP* o punto de decisión de políticas se encarga de tomar la decisión de acceso, aceptando o rechazando la publicación de mensajes.
- *PEP* o punto de cumplimiento de políticas se ocupa de aplicar las decisiones tomadas. Cuando un mensaje es rechazado el cliente que lo envía es forzosamente desconectado del broker.

Los atributos disponibles para el uso en las reglas de autorización son los siguientes:
- clientId: Identificador del dispositivo.
- trust: Valor de la confianza del dispositivo.
- username: Nombre de usuario utilizado en el último inicio de sesión del cliente.
- qos: Valor de la calidad de servicio usada en la publicación del mensaje.
- retain: Valor que indica si el mensaje a publicar debe ser retenido.

Para configurar las reglas se usa un archivo en formato YAML. En este archivo se introducen las reglas de control de acceso asociadas a cada _topic_. El diseño se inspira en el funcionamiento de algunos firewalls de red como iptables o nftables, donde la regla que se aplica es la primera que coincide. Para más información sobre la configuración de las reglas ver @configuración-de-autorización