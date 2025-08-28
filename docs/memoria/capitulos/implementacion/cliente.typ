Para el desarrollo del cliente MQTT se ha partido desde la base de un cliente ya existente, creado por HiveMQ @hiveMqClient.

Para cumplir con los objetivos del cliente se ha creado una Implementación de la interfaz _Mqtt5Client_ en la clase _Mqtt5TrustClient_, que se comportará igual que el cliente estándar. Esta nueva clase usa _MqttClientConnectedListener_ para iniciar la subscripción a el tópico _tmgr/ping_ para el cálculo de latencia y el tópico _tmgr/rep/_ para recibir una notificación de conexión de dispositivos. Cuando recibe una notificación de un nuevo dispositivo conectado, comprueba si este cliente tiene configurada una opinión, si es el caso, publica dicha opinión automáticamente.

#align(center)[
  #figure(
    image("../../imagenes/diagramas/client-package.png"),
    
    caption: "Paquete cliente MQTT",
  )
]

Además, tras iniciar las subscripciones, se inicia un bucle para publicar mensajes periodicamente. Esto se configura dentro de un archivo de configuración.

El archivo de configuración es un fichero en formato YAML cuya estructura se establece en la clase _File_, luego el archivo se carga usando la biblioteca _Jackson_ @jackson. Ver más sobre la configuración del cliente en @cm-uso.

#align(center)[
  #figure(
    image("../../imagenes/diagramas/client-config.png"),
    
    caption: "Paquete configuración cliente MQTT",
  )
]