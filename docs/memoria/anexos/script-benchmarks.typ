= Script pruebas de rendimiento <script-benchmark>
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
