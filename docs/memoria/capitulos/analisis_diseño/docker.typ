Todo el sistema debe ser desplegable usando contenedores docker. Para ello se utilizarán varios contenedores. Primeramente, el contenedor principal contendrá el broker MQTT junto a la extensión y la base de datos. Luego, en otro contenedor se incluirá un servidor http/https para la aplicación web.

Considerando el uso de un servidor LDAP y las interconexiones entre servicios. Se obtiene un diagrama general de la arquitectura del sistema:
#figure(
  image("../../imagenes/diagramas/docker.svg"),
  caption: "Arquitectura del sistema",
)