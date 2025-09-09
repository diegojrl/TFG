En esta sección se listan los requisitos funcionales y no funcionales del proyecto.
=== Funcionales
==== Web
Estos son los requisitos definidos para la página web.

- RF1: Un usuario y contraseña válidos serán necesarios para acceder a la web.

- RF2: Se podrá acceder a la web desde cualquier dispositivo.

- RF3: Desde la vista principal se podrán observar todos los dispositivos conectados al servidor desde que inicia la conexión.

- RF4: Cada dispositivo tiene asociados datos sobre la confianza.

- RF5: Desde cada dispositivo se puede entrar en su menú de configuración.

==== Extensión

- RF6: El cliente debe aportar un usuario y contraseña válidos.

- RF7: El servidor comprueba la validez del usuario y contraseña mediante el protocolo LDAP.

- RF8: El servidor calculará la confianza a partir de los datos recabados de cada dispositivo.

- RF9: El servidor almacenará la información histórica sobre los clientes.

- RF10: El servidor enviará información sobre la confianza de cada cliente.

- RF11: El cliente tendrá la posibilidad de aportar su opinión sobre otros clientes conectados.

- RF12: Se podrán modificar algunas de las características usadas para el cálculo de la confianza.

- RF13: Se podrá configurar el cálculo de la confianza.

- RF14: El servidor podrá denegar o conceder acceso a los recursos según la configuración proporcionada por el usuario.
==== Cliente

- RF15: Un usuario y contraseña válidos serán necesarios para iniciar la conexión con el servidor.

- RF16: El cliente dará su opinión de otros clientes según la configuración del usuario.

- RF17: El cliente publicará mensajes cada cierto tiempo según la configuración del usuario.

=== No funcionales
==== Web

- RNF1: Tiempos de carga de la web bajos

- RNF2: Seguridad en el acceso, la conexión será cifrada.

- RNF3: Seguridad de la contraseña de los usuarios.

- RNF4: Facilidad de uso.

==== Extensión

- RNF5: La configuración del servidor será fácil e intuitiva.

- RNF6: La inclusión de las características en el sistema no tendrá un impacto excesivo en el rendimiento.

- RNF7: Los clientes podrán usar el servidor aun sin conocer las nuevas características
==== Cliente

- RNF8: El cliente será fácil de usar.

- RNF9: La configuración del cliente será sencilla.

- RNF10: El cliente  se conectará de forma segura al servidor, verificando los certificados.