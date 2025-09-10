= Funciones de pertenencia <funciones-pertenencia>

- Latencia\
  Para la latencia existen tres valores posibles:

  - _low_, es una función trapezoidal con valor máximo en $x=0$ y mínimo en $x=0.5$.

  - _medium_, es una función triangular con mínimos en $x=0, x=1$ y máximo en $x=0.5$.

  - _high_, es una función trapezoidal con valor mínimo en $x=0.5$ y máximo en $x=1$.
#figure(
  image("../imagenes/funcionesActivacion/latency.svg"),
  caption: "Función de pertenencia de latencia"
)

#pagebreak()

- Seguridad\
  La seguridad tiene dos valores posibles:

  - _low_, es una función trapezoidal con valor máximo hasta $x=0.3$ y mínimo desde $x=0.6$.

  - _high_, es una función trapezoidal con valor mínimo hasta $x=0.5$ y máximo desde $x=0.9$.
 
#figure(
  image("../imagenes/funcionesActivacion/security.svg"),
  caption: "Función de pertenencia de seguridad"
)

#pagebreak()

- Tasa de errores\
  Para la tasa de errores hay tres valores posibles:
  
  - _low_, es una función trapezoidal con valor máximo hasta $x=0.05$ y mínimo desde $x=0.15$.
  
  - _medium_, es una función triangular con mínimos en $x=0.1, x=0.25$ y máximo en $x=0.15$.
 
  - _high_, es una función trapezoidal con valor mínimo hasta $x=0.2$ y máximo desde $x=0.3$.
#figure(
  image("../imagenes/funcionesActivacion/error_rate.svg"),
  caption: "Función de pertenencia de tasa de errores"
)

#pagebreak()

- Reputación\
  La reputación tiene tres valores posibles:

  - _low_, es una función trapezoidal con valor máximo hasta $x=0.3$ y mínimo desde $x=0.45$.

  - _medium_, es una función triangular con mínimos en $x=0.3, x=0.7$ y máximo en $x=0.5$.

  - _high_, es una función trapezoidal con valor mínimo hasta $x=0.6$ y máximo desde $x=0.9$.
#figure(
  image("../imagenes/funcionesActivacion/reputacion.svg"),
  caption: "Función de pertenencia de reputación"
)