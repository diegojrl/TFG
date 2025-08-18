= Cliente web
<cliente-web>
Servidor web creado con el framework
#link("https://svelte.dev/")[Svelte];. Este servicio permite visualizar
los datos relativos al cálculo de la confianza en tiempo real. Además
tiene la funcionalidad de modificar algunos de estos datos.

== Compilación
<compilación>
=== Dependencias
<dependencias>
Para compilar el cliente web es necesario instalar
#link("https://nodejs.org/en/download")[node];.

Una vez node está instalado, se instalan todas las dependencias del
proyecto. Ejecutar desde la carpeta #emph[web];.

```bash
npm install
```

=== Compilar proyecto Svelte
<compilar-proyecto-svelte>
Antes de compilar la web es necesario configurar la dirección del broker
MQTT al que se conectará.

Para ello se crea el archivo `.env` en la carpeta #emph[cliente];.
Dentro de este debe especificarse en la variable de entorno
`PUBLIC_MQTT_HOST` la URL en formato estándar del servidor MQTT.

```
PUBLIC_MQTT_HOST="ws://example.com:8080/mqtt"
```

Una vez configurada la dirección del servidor se puede compilar la web.
Para ello se ejecuta el siguiente comando:

```bash
npm run build
```

Cuando el proceso finalize, dentro de la carpeta build, se encuentran
los archivos necesarios para el servidor web.

== Instalación y configuración del servidor web
<instalación-y-configuración-del-servidor-web>
En este caso se va a usar
#link("https://httpd.apache.org/")[httpd/apache2] como servidor, pero
podría usarse cualquier otro.

Para configurar la web en el servidor es necesario tener apache2
instalado, por ejemplo, usando
#link("https://www.debian.org/index.es.html")[Debian];:

```bash
sudo apt update && sudo apt install apache2
```

Activar los componentes necesarios en el servidor

```bash
a2enmod proxy proxy_http proxy_wstunnel rewrite
```

Añadir los archivos de la compilación al servidor.

```bash
sudo cp ./build/* /var/www/html

sudo chown www-data:www-data -R /var/www/html
```

=== Certificados TLS
<certificados-tls>
Para activar el cifrado TLS para HTTP es necesario proporcionar un
certificado al servidor web. Hay dos opciones, usar uno existente, o
crear un certificado autofirmado.

Para crear un certificado autofirmado, se puede ejecutar el siguiente
comando, remplazando `{hostname}` por el nombre de dominio del servidor.

```bash
openssl req -x509 -nodes -days 365 -newkey rsa:4096 -keyout certificate.key -out certificate.crt -subj "/CN={hostname}"
```

La ruta de este fichero será necesaria posteriormente, durante la
configuración del servidor web.

=== Configurar sito
<configurar-sito>
Una vez realizados todos los pasos anteriores se puede continuar con la
configuración. Primero, se necesita crear un nuevo archivo de
configuración de apache2.

```bash
cd /etc/apache2/sites-available

sudo touch trust-site.conf
```

Dentro de este documento hay que añadir dos #emph[VirtualHost];. El
primero se encarga de redirigir el tráfico HTTP a HTTPS. Para ello, se
añade las siguientes líneas en el documento.

Al igual que con la creación del certificado, será necesario reemplazar
`{hostname}` por el dominio del servidor.

```xml
<VirtualHost *:80>
   ServerName {hostname}
   Redirect permanent / https://{hostname}
</VirtualHost>
```

Después de configurar la redirección, hay que configurar la ruta de los
documentos y los certificados, además del certificado TLS.

En este ejemplo se usan `/etc/ssl/certs/certificate.crt` y
`/etc/ssl/certs/certificate.key` como las rutas para la clave pública y
clave privada respectivamente. Es necesario asegurar que las rutas
apuntan al certificado creado anteriormente, o a uno ya existente.

Además, se debe establecer la dirección del servidor MQTT, remplazando
`{MQTT_SERVER}` por dicha dirección. Si el servidor MQTT se encuentra en
el mismo ordenador que el servidor web, este valor debe ser `localhost`.

```xml
<VirtualHost *:443>
    SSLEngine on
    SSLCertificateFile /etc/ssl/certs/certificate.crt
    SSLCertificateKeyFile /etc/ssl/private/certificate.key

    DocumentRoot /var/www/html

    DirectoryIndex index.html

    RewriteEngine On
    RewriteCond %{HTTP:Upgrade} =websocket [NC]
    RewriteRule /ws ws://{MQTT_SERVER}:8080/ [P,L]
</VirtualHost>
```

Con esto, el servidor está casi configurado, solo queda activar esta
configuración y reiniciar el servicio de apache2.

```bash
sudo ln -s /etc/apache2/sites-available/trust-site.conf /etc/apache2/sites-enabled/trust-site.conf

sudo systemctl restart apache2.service
```
