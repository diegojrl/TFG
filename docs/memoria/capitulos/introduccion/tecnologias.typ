=== MQTT
MQTT es un protocolo de mensajería ligero y eficiente, diseñado para la comunicación entre dispositivos en redes con bajo ancho de banda, alta latencia o conexiones inestables.

Se basa en un modelo de publicación/suscripción con un broker o servidor central que distribuye los mensajes entre clientes. Es ampliamente usado en IoT y sistemas de telemetría.

Algunas de las ventajas que aporta son:
- Ligereza y eficiencia. Está diseñado para dispositivos con recursos limitados y tener un uso de ancho de banda bajo.
- Independencia sobre la red. El protocolo puede funcionar con TCP, Websockets y otros tipos de redes.
- Conexiones no continuas. Si un cliente pierde la conexión, al reconectarse recibirá los mensajes pendientes.
- Fiabilidad configurable. La confirmación de mensajes se puede establecer en tres niveles, $0$ para mensajes no confirmados, $1$ para garantizar que el mensaje se recibe al menos una vez y $2$ para entregar el mensaje exactamente una vez.

=== HiveMQ
HiveMQ @hiveMq es un servidor creado especialmente para gestionar los mensajes del protocolo MQTT. Este tipo de servidor se conoce comúnmente como _broker_,
ya que hace de intermediario para todos los mensajes que se envían.

Este servicio es usado por las marcas principales en los sectores de producción, energía, automoción y logística. Usando el servicio para construir sistemas IoT y transmisión de datos en tiempo real @hiveMq.

Se ha seleccionado este broker debido a su sistema de extensiones, el cual provee una buena documentación @hiveMqDocs y permite adaptar el servidor a una gran cantidad de situaciones.

=== Web
==== Svelte
Svelte es un framework para el desarrollo de interfaces de usuario que usa un compilador extremadamente conciso, usando lenguajes muy conocidos, como HTML, CSS y JavaScript @svelte. Algunas de sus ventajas son:

- Rendimiento. Genera código optimizado para su uso directo.
- Código más simple. Vincula el código HTML, CSS y JavaScript en un mismo fichero.
- Reactividad. Los valores se actualizan automaticamente cuando cambian.
==== Tailwind CSS
Tailwind CSS es un framework de CSS que permite diseñar interfaces directamente en el HTML usando clases predefinidas @tailwind. No proporciona componentes ya diseñados, sino que aporta las herramientas para crearlos fácilmente. Sus ventajas son las siguientes:

- Personalización. No obliga a seguir un estilo predeterminado.
- Diseño responsive. Ofrece las utilidades necesarias para crear componentes que reaccionen a los cambios de tamaño de la pantalla.
- Muy bien integrado en frameworks, entre ellos, Svelte.
==== TypeScript
TypeScript es un lenguaje que complementa al lenguaje JavaScript, añadiendo características extras @typescript. Fue desarrollados por Microsoft y es necesario convertir el código a el lenguaje JavaScript antes de ejecutarlo. Sus ventajas son:

- Sistema de típos estático. Permite la declaración de tipos para las funciones y variables.
- El autocompletado es más completo que en JavaScript.
- Integrado con la mayoría de frameworks modernos.
- Compatibilidad con JavaScript. Es posible usar dependencias creadas para JavaScript.

=== Herramientas comunes
==== Java
Java es un lenguaje de programación orientado a objetos,  propiedad de Oracle @java. Es uno de los lenguajes más usados en el mundo gracias a su portabilidad, robustez y versatilidad. Sus principales ventajas son:

- Multiplataforma, culquier sistema que tenga disponible una Máquina Virtual de Java (JVM), puede ejecutar programas java.
- Al ser uno de los lenguajes más usados, tiene una gran cantidad de recursos disponibles, como documentación o frameworks.
- Muy versátil, es usado en aplicaciones de escritorio, servidores, teléfonos móviles e incluso IoT.

==== Maven
Maven es una herramienta de gestión y automatización de proyectos de software, principalmente para  Java, creado por la fundación apache @maven. En maven se crea un modelo del proyecto donde se definen dependencias, configuración de compilación, empaquetado y despliegue.

- Gestión automática de dependencias.
- Integración con Entornos de Desarrollo Integrados (IDEs) y otras herramientas.
- Automatización del proceso de compilación.

==== Docker y Linux
Docker es una plataforma que permite empaquetar aplicaciones junto a todas sus dependencias, en un entorno aislado y portable @docker. Para poder usar Docker es necesario disponer de el sistema operativo Linux, un sistema gratuito y de código libre, ampliamente usado en servidores y sistemas empotrados. Docker aporta las siguientes ventajas:

- Portabilidad y reproducibilidad, los contenedores aseguran que las aplicaciones funcionan exactamente igual sin importar la plataforma o el sistema usado.
- Aislamiento y seguridad. Cada contenedor se ejecuta en un entorno aislado, sin acceso directo a los recursos del sistema.
- Escalabilidad. Los contenedores pueden ser usados en orquestadores como Kubernetes @k8s, usado para desplegar aplicaciones en escala.

==== Intellij IDEA
IntelliJ IDEA es un IDE creado por JetBrains @Intellij, diseñado principalmente para el desarrollo en Java y otros lenguajes de la JVM. Algunas de sus ventajas más notables:

- Soporte de primera calidad para java.
- Análisis inteligente del código y autocompletado avanzado.
- Herramientas de depuración integradas.
- Integración con bases de datos y control de versiones.
- Otras integraciones como docker.

==== Visual Studio Code
Visual Studio Code es un editor de código fuente gratuito y multiplataforma desarrollado por Microsoft @VsCode. Está diseñado para ser ligero y extensible. Algunas de sus ventajas son:

- Más ligero que un entorno de desarrollo tradicional.
- Amplio sistema de extensiones, soporta una infinidad de lenguajes y frameworks.
- Integraciones con sistemas de control de versiones como git.
=== Misceláneo
==== Git
Git es un sistema de control de versiones distribuido. Permite registrar los cambios en archivos y proyectos a lo largo del tiempo, facilitando la gestión de versiones de software. Además, permite trabajar sin conexión. El servidor usado para el desarrollo del proyecto es GitHub @github.

==== Draw.io
Draw.io es una herramienta gratuita y online para crear diagramas de todo tipo @drawio. Funciona desde el navegador y además se integra con plataformas de almacenamiento en la nube como Google Drive y GitHub.

==== Typst
Typst es un sistema de composición tipográfica basado en marcado. Está diseñado para ser tan potente como LaTeX, pero con mayor facilidad de uso @typst. Las principales ventajas respescto a LaTeX son:

- Sintaxis simplificada y más fácil de usar.
- Uso flexible de funciones, scripts y plantillas.
- Tiempos de compilación muy reducidos.

==== Pandoc
Pandoc es una herramienta de conversión de documentos que funciona como un conversor universal de formatos @pandoc. Por ejemplo, se puede usar para convertir un archivo Markdown a LaTeX o Typst, o un archivo pdf a otro archivo en formato Microsoft Word.

==== Python
Python es un lenguaje de programación interpretado y de alto nivel @python. Algunas de sus ventajas incluyen: amplia librería estándar, gran ecosistema de paquetes y la velocidad de desarrollo. Todo esto convierte a Python en un lenguaje de programación muy versátil y popular.
