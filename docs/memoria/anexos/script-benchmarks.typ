= Pruebas de rendimiento

== Archivo de control de acceso <benchmark-ac>
```yaml
permissions:
  - topic: control/view/+
    rules:
      - action: subscribe
  - topic: control/mod/#
    rules:
      - action: publish
        username: admin
  - topic: tmgr/ping
    rules:
      - action: subscribe
        qos: one_two
  - topic: "tmgr/rep/${{clientid}}"
    rules:
      - allow: false
        action: publish
  - topic: "tmgr/rep/+"
    rules:
      - allow: true
  - topic: test/#
    rules:
      - clientId: cli
        username: test
        trust: 0.5
      - username: paco
        allow: false
      - username: admin
  - topic: my/#
    rules:
      - username: test

```

== Script <benchmark-script>
Script proveniente del repositorio del proyecto: https://github.com/diegojrl/TFG/blob/main/benchmarks/launch.sh

```bash
#!/bin/env bash

host=hivemq.lan
user=
password=
qos=1



cd "$(dirname "$0")" || exit 1

read -rp "Introduce el número de clientes: " n

files=()
# Generar archivo de configuración
for i in $(seq 1 $n); do
  file="/tmp/tc_$i.yaml"
  files[$i]=$file
  cp bench.yaml $file

  # Mensaje autogenerado
  msg=$(tr -dc '[:lower:]' </dev/urandom | head -c512) # 512 Bytes

  sed -i "s/\${host}/$host/g" $file
  sed -i "s/\${user}/$user/g" $file
  sed -i "s/\${password}/$password/g" $file
  sed -i "s/\${clientId}/c$i/g" $file
  sed -i "s/\${qos}/$qos/g" $file
  sed -i "s/\${message}/$msg/g" $file
done


# Lanzar todos los clientes
for i in "${files[@]}"; do
  env CONFIG_FILE="$i" ../client/target/client &
done
echo "All clients started"
echo "wait 30s"
sleep 30
echo "Stoping"
kill $(jobs -pr)
wait $(jobs -pr)

# Eliminar archivos tmp
for i in "${files[@]}"; do rm "$i"; done

```
