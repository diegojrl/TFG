#let tabla-casos-uso(id, nombre, descripcion, precondicion, actor, escenario, escenario_alt) = table(
  columns: 2,
  align: (col, row) => (auto, auto, auto).at(col),
  inset: 10pt,
  [*Caso de uso #id*], [ *#nombre* ],
  [*Descripción*], [ #descripcion ],
  [*Precondición*], [ #precondicion ],
  [*Actor*], [ #actor ],
  table.cell(colspan: 2)[ *Escenario principal* ],
  table.cell(colspan: 2)[ #escenario ],
  ..if escenario_alt != [] {
    (
    table.cell(colspan: 2)[ *Escenario alternativo* ], 
    table.cell(colspan: 2)[ #escenario_alt ]
    )
  } else {
    ()
  },
)

#let casos-de-uso-web = [
  #tabla-casos-uso(
    1,
    [Acceso Web],
    [El usuario accede a la página web y en ella observa los dispositivos conectados. ],
    [El usuario debe disponer de un navegador web y el link de la página web. ],
    [Usuario/Administrador.],
    [
      + El usuario accede a la página web a través de un enlace.
      + El usuario introduce un nombre de usuario y una contraseña correcta.
      + El usuario puede observar la página principal.
    ],
    [
      + El usuario accede a la página web a través de un enlace.
      + El usuario introduce un nombre de usuario y una contraseña incorrecta.
      + Vuelta al punto 1.
    ],
  )
  #tabla-casos-uso(
    2,
    [Visualización de características.],
    [El usuario accede a la web y puede visualizar las características de cada dispositivo conectado.],
    [El usuario tiene acceso a la página web.],
    [Usuario/Administrador.],
    [
      + El usuario accede a la página web correctamente.
      + Aparecen, de forma automática, los dispositivos conectados al servidor con sus datos asociados.
      + Los datos se actualizan cada 15 segundos.
    ],
    [],
  )
  #tabla-casos-uso(
    3,
    [Menú de edición de características.],
    [El administrador o el usuario acceden al menú de edición de características de un dispositivo.],
    [El usuario tiene acceso a la página web.],
    [Administrador/Usuario.],
    [
      + El usuario accede a la página web correctamente.
      + El usuario localiza el dispositivo al cual desea modificar sus características.
      + El usuario pulsa el botón de editar del dispositivo seleccionado.
    ],
    [],
  )
  #tabla-casos-uso(
    4,
    [Modificación de latencia.],
    [El administrador o el usuario modifica el valor de la latencia de un dispositivo.],
    [El usuario tiene acceso a la página web.],
    [Administrador/Usuario.],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario introduce el valor de latencia deseado.
      + El servidor ajusta este valor dentro del rango máximo y mínimo.
      + En la próxima actualización de datos aparecerá el nuevo valor.
    ],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario no tiene permisos para modificar la latencia del dispositivo.
      + El cliente web reinicia la conexión MQTT.
      + Vuelta al punto 1.
    ],
  )
  #tabla-casos-uso(
    5,
    [Modificación de tasa de errores.],
    [El administrador o el usuario modifica el valor de la tasa de errores de un dispositivo.],
    [El usuario tiene acceso a la página web.],
    [Administrador/Usuario.],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario desliza el indicador de _Tasa de errores_ hasta seleccionar el valor deseado.
      + En la próxima actualización de datos aparecerá el nuevo valor.
    ],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario no tiene permisos para modificar la tasa de errores del dispositivo.
      + El cliente web reinicia la conexión MQTT.
      + Vuelta al punto 1.
    ],
  )
  #tabla-casos-uso(
    6,
    [Modificación de la reputación.],
    [El administrador o el usuario resetea el valor de la reputación de un dispositivo.],
    [El usuario tiene acceso a la página web.],
    [Administrador/Usuario.],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario pulsa el botón _Resetear_.
      + En la próxima actualización de datos aparecerá el valor de reputación actualiado.
    ],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario no tiene permisos para resetear la reputación del dispositivo.
      + El cliente web reinicia la conexión MQTT.
      + Vuelta al punto 1.
    ],
  )
  #tabla-casos-uso(
    7,
    [Modificación de la opinión.],
    [El cliente web envia su opinión sobre un dispositivo.],
    [El usuario tiene acceso a la página web.],
    [Usuario/Administrador.],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario desliza el indicador de _opinión_ hasta seleccionar el valor deseado.
      + En la próxima actualización de datos aparecerá el valor de reputación actualiado.
    ],
    [
      + El usuario accede al menú de edición del dispositivo.
      + El usuario no tiene permisos para opinar sobre el dispositivo.
      + El cliente web reinicia la conexión MQTT.
      + Vuelta al punto 1.
    ],
  )
]


En esta sección se describen los casos de uso relacionados con los requisitos de la página web detallados previamente.

#casos-de-uso-web
