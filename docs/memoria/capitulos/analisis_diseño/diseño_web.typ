La idea para la aplicación web es que proporcione la funcionalidad de la forma más simple posible, pero con un diseño moderno. Por tanto, la página web dispondrá de una página principal, donde se podrá visualizar los clientes conectados al broker y acceder a la configuración de cada dispositivo.

Además la pantalla principal deberá ser visualizada correctamente independientemente del dispositivo desde el que se accede.

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("../../imagenes/web/principal-pequeño.png", height: 55%),
    ),
    grid.cell(
      colspan: 3,
      image("../../imagenes/web/principal-mediano.png", height: auto),
    ),
    grid.cell(
      colspan: 3,
      image("../../imagenes/web/principal-grande.png", height: auto),
    ),
  ),
  caption: "Pantalla principal, en diferentes tamaños.",
)

Para recibir la información que se muestra en la pantalla principal, la aplicación web se conecta a el servidor MQTT usando WebSocket y se subscribe a los tópicos correspondientes para recibir los datos necesarios (@vista-control). La dirección del broker debe ser especificada en la configuración de la aplicación web, ver más en @cliente-web. Para iniciar la conexión es necesario que el usuario proporcione los datos para iniciar sesión, con este proposito se crea una ruta en la página web que se visualizará de la siguiente manera:

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("../../imagenes/web/inicio-sesion-pequeño.png", height: 30%),
    ),
    grid.cell(
      colspan: 3,
      rowspan: 2,
      image("../../imagenes/web/inicio-sesion-grande.png", width: auto),
    ),
  ),
  caption: "Inicio de sesión.",
)

Por último, la aplicación web tiene la capacidad de modificar algunos de los atributos de los dispositivos. Para ello el usuario deberá pulsar el botón que se encuentra en la esquina superior derecha de cada dispositivo, apareciendo la siguiente ventana:

#figure(
  grid(
    columns: 4,
    gutter: 3pt,
    align: horizon,
    grid.cell(
      rowspan: 2,
      image("../../imagenes/web/editar-pequeño.png", height: 30%),
    ),
    grid.cell(
      colspan: 3,
      rowspan: 2,
      image("../../imagenes/web/editar-grande.png", width: auto),
    ),
  ),
  caption: "Edición de atributos.",
)

Este menú publicará los mensajes apropidaos según el atributo que se intente modificar (@modificacion-atributos). Para que el procedimiento funcione correctamente el cliente debe tener otorgados los permisos suficientes para cada tópico, ver @autorizacion.