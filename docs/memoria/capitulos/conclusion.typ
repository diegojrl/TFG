== Resultados y conocimientos obtenidos
Tras todo el desarrollo de este proyecto, se puede concluir que los objetivos establecidos se han cumplido. Se ha diseñado e implementado un sistema de autorización basado en la confianza para el protocolo MQTT. Junto a este sistema se ha desarrollado una aplicación web y un cliente MQTT, con la finalidad de demostrar la funcionalidad del proyecto.

Durante el transcurso del proyecto he tratado con tecnologías que no conocía hasta el momento, como VisualVM, para tomar datos de rendimiento. Y otras tecnologías que conocía, pero nunca había utilizado, como docker y typst. Queda claro que mis conocimientos al respecto de estas tecnologías han mejorado considerablemente, pero aún queda mucho más que aprender sobre ellas.

Por último, quiero destacar que este es el primer proyecto personal, de este tamaño, que realizo. Aunque es cierto que ya había desarrolaldo algunos proyectos, ninguno de ellos abarcaba tantas tecnologías y plataformas diferentes, siendo todo un reto conseguir un buen funcionamiento entre la aplicación web, el servidor y la aplicación de consola.

== Dificultades
Nada más comenzar el proyecto, fue complicado seleccionar el servidor MQTT donde se implementaría el sistema, hay muchas opciones y todas similares entre ellas. Finalmente, tras estudiar la documentación de cada opción se acabó seleccionando HiveMQ, por su buena calidad en los documentos y familiaridad con el lenguaje de programación.

Más adelante, durante las pruebas de rendimiento, se detectó un problema con el inicio de sesión, al conectar muchos clientes a la vez, el sistema no podía gestionar la gran cantidad de peticiones, resultando en muhcos clientes desconectados. Tras investigar el problema se ajustó la configuración de la conexión con el servidor LDAP y el problema quedó solucionado.

== Lineas futuras
Por el momento, el sistema funciona correctamente y con un buen rendimiento, pero esto no significa que no pueda mejorarse. A continuación se exploran algunas posibles mejoras.

Para comenzar, incorporar un modelo de inteligencia artificial puede ser beneficioso. Esto permitiría realizar un análisis de comportamiento de cada dispositivo y usuario, esta nueva información se puede añadir a el cálculo de confianza, resultando en un valor más preciso y fiable de confianza.

Añadir atributos extras en el control de acceso. En este momento el sistema proporciona varias características para configurar la autorización, aun así, sería beneficioso agregar más. Entre todas las posibilidades, una muy interesante sería añadir roles, donde se pueden agrupar usuarios o dispositivos.

El cálculo de confianza también podría ser mejorado. Actualmente se utilizan unos valores fijos para los pesos de cada atributo, permitir al usuario modificar estos pesos es una buena opción para mejorar el sistema.

Por último, sería interesante tener acceso a los datos históricos de una forma más cómoda. Por el momento, solo se puede acceder a el registro histórico a través de la base de datos. Una opción sería crear un nuevo apartado en la aplicación web para el visualizado y filtrado de esta información.