El objetivo de la base de datos es la persistencia de la información sobre los clientes. Es decir, el sistema recuerda el comportamiento de cada dispositivo a lo largo del tiempo, aunque se desconecte del servidor. Se ha definido el siguiente esquema.
#align(center)[
  #figure(
    image("../../imagenes/diagramas/sql/db.png", width: 70%),
    caption: "Diagrama de base de datos",
  )
]
- *DEVICES*: Es la tabla principal de la base de datos. Guarda la información necesaria para el cálculo de confianza. Se identifica por el _ClientId_ y el usuario al que pertenece el dispositivo. Como el mismo _ClientId_ no puede ser usado por dos dispositivos al mismo tiempo, esta columna se define como única.
- *USERS*: Indica el nombre de usuario usado en la conexión de los clientes. Este nombre debe coincidir con el nombre de usuario en el servidor LDAP y, por tanto, debe ser único.
- *OPINIONS*: Almacena las relaciones de opinión entre los dispositivos.
- *DEVICES_AUDIT*: Guarda información histórica sobre el estado de los atributos de cada dispositivo. Se actualizará automáticamente cuando esta información cambie.
