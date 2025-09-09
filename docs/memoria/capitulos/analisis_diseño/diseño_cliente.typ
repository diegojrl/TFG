El cliente MQTT es una adaptación de un cliente ya existente @hiveMqClient, pero agregando la nueva funcionalidad que ofrece el sistema. El objetivo principal de este cliente es realizar las pruebas de lafuncionalidad añadida al sistema y adicionalmente evaluar el rendimiento del sistema.

Con este objetivo, el cliente integrará las siguientes características:

- Para que el cliente sea lo más ligero posible, en vez de ser empaquetado en un archivo jar para JVM, se compila a código nativo usando GraalVM. Esto aporta ventajas como un inicio casi instantáneo, menor uso de recursos y un rendimiento más consistente desde el inicio @graalvm.

- El cliente incluirá la capacidad de publicar mensajes automáticamente y estos mensajes serán personalizables. Para configurar estos mensajes ver @configuración-de-mensajes.

- Se incorporará la funcionalidad para opinar sobre otros dispositivos desde este cliente. Ver @configuración-de-opiniones.