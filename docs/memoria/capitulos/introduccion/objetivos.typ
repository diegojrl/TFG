El objetivo de este proyecto es diseñar e implementar un sistema de control de acceso basado en la confianza para IoT @taciot usando el protocolo MQTT como base. La solución incluirá mecanismos de autenticación y autorización de dispositivos. Adicionalmente, se implementará una interfaz web que permita a los usuarios visualizar y gestionar los parámetros relevantes del sistema.

El proyecto tiene varios objetivos, todos relacionados con un sistema de control de acceso para IoT basado en la confianza y el protocolo MQTT. Se pueden desglosar como:
- Creación de un modelo de confianza apropiado para MQTT.
- Implementar un sistema de control de acceso y un protocolo de control como una extensión del broker HiveMQ, basado en el modelo de confianza descrito anteriormente.
- Desarrollo de una interfaz web donde se podrán visualizar y editar ciertos atributos del modelo.
- Desarrollo de un cliente MQTT que sea capaz de aprovechar las ventajas del sistema de control de acceso.
- Realizar un pequeño estudio respecto al rendimiento del sistema.

Con estos objetivos en mente, el proyecto se puede dividir en estos tres componentes:
- Extensión HiveMQ, donde se encontrará toda la lógica del sistema.
- Aplicación web, será la interfaz para observar y alterar el sistema.
- Cliente MQTT, encargado de demostrar la funcionalidad.