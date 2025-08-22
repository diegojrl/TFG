=== Inicio de sesión y desconexión
Primero el servidor MQTT comprueba el nombre de usuario y la contraseña usando la clase _LdapAuth_ que implementa la interfaz _SimpleAuthenticator_.

Una vez se comprueba que los datos del usuario son correctos, se actualiza la base de datos para incorporar al usuario y se buscan los datos previos del dispositivo, actualizando el usuario dueño si fuera necesario. Por último se retransmite un mensaje a todos los clientes, avisando del nuevo dispositivo conectado y se publican, como mensaje retenido, sus atributos en el tópico _control/view/{clientId}_.

#figure(
  image("../../../diagramas/LifeCycleListener.png"),
  caption: "",
)

Más adelante, cuando el cliente se desconecta del broker, primero se actualiza la información del dispositivo en la base de datos y luego se eliminan los mensajes residuales del tópico _control/view/{clientId}_.

=== Obtención de atributos <impl-att>
Los atributos de cada dispositivo se almacenan en una instancia de la clase _DeviceTrustAttributes_, que a su vez se guardan en un _ConcurrentHashMap_ para obtener un acceso muy veloz a esta información, esto se hace con la clase _TrustStore_ siguiendo un patrón de singleton.
#figure(
  image("../../../diagramas/DeviceTrustAttributes.png"),
  caption: "Clase DeviceTrustAttributes y TrustStore.",
)
- Latencia y tasa de errores: Para detectar la latencia y los errores de los dispositivos hay que aprovechar una de las caracterísiticas del protocolo MQTT, cada mensaje publicado está identificado mediante un PacketId, que es único por cada conexión. El identificador se envía al publicar y confirmar la recepción de los mensajes. Usando la interfaz _PublishOutboundInterceptor_ y _PubrecOutboundInterceptor_ se almacenan los identifadores y el instante de tiempo en el que se recibe cada paquete. En este momento también se comprueba que el identificador del paquete no es repetido, en ese caso se considera que la entrega anterior ha fallado y aumenta el contador de mensajes fallidos del cliente. Luego usando _PubackInboundInterceptor_, _PubrecInboundInterceptor_ y _PubrelInboundInterceptor_, se interceptan las confirmaciones de cada mensaje y actualizan los valores de latencia.

#figure(
  image("../../../diagramas/trustManagement.png"),
  caption: "Clases cálculo de atributos.",
)

- Seguridad: Cuando se establece la conexión inicialmente se comprueba el uso de cifrado y la red desde donde se realiza la conexión. Con estos datos se puede calcular el valor de la seguridad según lo redactado en la  @att-seguridad.

- Reputación: Usando la interfaz _PublishInboundInterceptor_, la clase _ReputationListener_ intercepta todos los mensajes que se publican en busca del tópico _tmgr/rep/_. Cada vez que se recibe una nueva opinión para cierto cliente se recalcula la reputación. Para ello se accede a la base de datos y se recuperan todas las opiniones y valores de confianza que afectan al cliente. Luego se calcula como se indica en la @att-rep.

=== Control de atributos
El control de los atributos de los dispositivos cuenta de dos acciones separadas. La primera se encarga de informar a los clientes de los valores de los atributos. Para ello se inicia un nuevo hilo en el servidor que se activa cada 15 segundos y publica toda la información de todos los dispositivos. La información de interés de cada dispositivo se convierte en formato binario de MessagePack @messagePack con la biblioteca Jackson @jackson y se publica el mensaje que llegará a todos los clientes que se hayan subscrito.

#figure(
  image("../../../diagramas/trustControl.png", width: 90%),
  caption: "Clases control de atributos."
)

La otra accion que se puede realizar es la modificación de atributos. En este caso se realiza en la clase _ModificationListener_ siguiendo el mismo procedimiento que en el cambio de opinión, explicado en la @impl-att.

=== Base de datos
La base de datos se controla desde el paquete _db_. En este paquete se encuentran dos clases auxiliares que reflejan el esquema de base de datos, _Device_ y _Opinion_, estos objetos hacen de intermediarios entre la base de datos y los datos usados para el cálculo de confianza.

#figure(
  image("../../../diagramas/db-package.png"),
  caption: "Paquete base de datos",
)


Desde la clase _Database_ se crea el esquema de la base de datos y contiene todos los métodos necesarios para lanzar las consultas SQL. También se añade un trigger en la base de datos _AuditTrigger_ que se encarga de almacenar los datos históricos de los dispositivos, para un posible estudio de esta información en el futuro.

=== Controlador lógica difusa
//rules
//datos dispositivos
// @fuzzy4j

=== Autorización
La autorización se ha implementado siguiendo lo descrito en la @autorizacion.

#figure(
  image("../../../diagramas/authz.png"),
  caption: "Paquete autorización",
)
La clase _PolicyEnforcementPoint_, encargada de aplicar las decisiones implementa las interfaces _SubscriptionAuthorizer_ y _PublishAuthorizer_. Con esto, el broker es capaz de cerrar una conexión cuando el cliente no está autorizado.

Luego, la clase _PolicyAdministrationPoint_ se ocupa de cargar la configuración de la autorización. Para más información de como configurar la autorización ver @configuración-de-autorización. El archivo está en formato YAML y se lee usando la biblioteca Jackson@jackson, siguiendo el esquema definido en la clasee _File_:
#figure(
  image("../../../diagramas/authz-conf.png"),
  caption: "Paquete configuración de autorización",
)
