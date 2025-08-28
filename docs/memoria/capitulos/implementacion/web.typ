La aplicación web se ha desarrollado principalmente usando TypeScript @typescript, Svelte @svelte y MQTT.js @mqttjs, un cliente MQTT creado en TypeScript, perfecto para el uso en una página web.

La ruta inicial de la aplicación web se trata del inicio de sesión, cuando el usuario introduce un usuario y contraseña válidos se redirige a la pantalla principal y se inicia la conexión con el broker MQTT. Tras establecer la conexión se crea la subscripción al tópico _control/view/+_, para recibir datos de todos los clientes conectados.

Al recibir datos, primero se decodifican usando la biblioteca msgpack-javascript @msgpack, para luego actualizar los datos de este dispositivo en la vista del usuario.

Para la modificación de atributos, se abre una nueva vista al pulsar el icono de editar. Cuando se alteran los valores un mensaje es publicado automáticamente al broker, sin necesidad de confirmar el cambio.