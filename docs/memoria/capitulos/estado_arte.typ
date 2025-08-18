Con el fin de establecer el contexto de este trabajo se explorarán los mecanismos y buenas prácticas relativas a la autorización y autenticación en el protocolo MQTT. Luego se revisarán los estudios relacionados con la confianza y el control de acceso en IoT.

- Mosquitto @mosquitto uno de los brokers más conocidos, reconocido por su buen rendimiento y simplicidad. Este broker permite la configuración de usuarios y contraseñas a través de un archivo específico, y emplea un enfoque similar para la autorización, mediante el uso de listas de control de acceso definidas en archivos. En el caso que se requiera un sistema de mayor complejidad, Mosquitto ofrece la posibilidad de desarrollar  plugins personalizados y aumentar las capacidades del broker. 

- El broker MQTT HiveMQ @hiveMq no ofrece ningún mecanismo para la autenticación o autorización por defecto. En su lugar, recomienda la implementación de soluciones adaptadas a cada caso, ofreciendo para ello un extenso sistema de extensiones con el cual se puede ampliar la funcionalidad del sistema. Los creadores ofrecen una extensión que soporta RBAC(Role Based Authorization Control), donde los usuarios se identifican con una contraseña.

- EMQX @emqx es un broker diseñado para ofrecer una alta escalabilidad. En lo que respecta a la autenticación de usuarios, EMQX admite múltiples mecanismos, incluyendo el uso de contraseñas gestionadas mediante servicios externos como LDAP, HTTP o bases de datos, así como mediante Json Web Tokens (JWT) o Kerberos, entre otros. Respecto a la autorización, emplea listas de control de acceso (ACLs), con múltiples opciones para su almacenamiento. Además, EMQX dispone de un sistema de plugins que permite ampliar su funcionalidad, facilitando la integración con sistemas externos o la implementación de lógica personalizada dentro del broker.

Tras revisar las distintas funcionalidades que ofrecen los principales brokers MQTT analizados, se puede observar que, en términos generales, presentan características similares. En la mayoría de los casos, se proporciona un sistema básico de gestión de usuarios y mecanismos de control de acceso. Adicionalmente, todos ellos ofrecen la posibilidad de desarrollar e integrar extensiones o plugins, lo que permite implementar sistemas personalizados que se adapten a las necesidades de cada entorno.

- En el trabajo @siot, se presenta una arquitectura orientada a la integración de dispositivos inteligentes. El componente de gestión de confianza se basa
  en relaciones previas con otros dispositivos para seleccionar el dispositivo que mejor cumplirá con cierto servicio.

- Gestión de la confianza para la arquitectura basada en servicio @soa-iot, introduce un sistema de confianza distribuido diseñado para interconectar
  dispositivos IoT que proporcionan diferentes servicios, tomano como base las relaciones sociales entre los propios dispositivos.

-  En Trust-aware access control system for IoT (TACIoT) @taciot, se propone un mecanismo de seguridad ligero para dispositivos IoT, tomando la confianza
  como un factor principal y usando la lógica difusa para su cálculo. Este sistema ha sido probado bajo condiciones reales y se ha demostrado que ofrece un buen rendimiento.

- Trustee @trustee, plantea un sistema de gestión de la confianza que combina múltiples factores. Gracias a la incorporación de técnicas de aprendizaje
  computacional, el sistema tiene la capacidad de detectar anomalías e integrar esta información en la evaluación de la confianza.

- Finalmente, el estudio @IotAC presenta una revisión sobre los mecanismos para el control de acceso en IoT. Comparando las limitaciones y fortalezas de distintos modelos, entre ellos, Role Base Access Control(RBAC), Attribute Based Access Control(ABAC) o Capability Base Access Control(CapBAC). Destacando la ausencia de una solución universal para el control de acceso enfocado a IoT.

Tras realizar una revisión exhaustiva de las investigaciones relevantes, se puede concluir
que, aunque existe una gran cantidad de información respecto a la confianza en IoT, actualmente
no existe un sistema que ofrezca la capacidad de integrarse fácilmente en un entorno existente.