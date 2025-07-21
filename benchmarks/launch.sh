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
    files[$i]="/tmp/tc_$i.yaml"
    cp ../client/src/config.yaml files[$i]

    # Mensaje autogenerado
    msg=$(tr -dc 'a-z' </dev/urandom | head -c99)

    sed -i "s/\${host}/$host/g" files[$i]
    sed -i "s/\${user}/$user/g" files[$i]
    sed -i "s/\${password}/$password/g" files[$i]
    sed -i "s/\${clientId}/c$i/g" files[$i]
    sed -i "s/\${qos}/$qos/g" files[$i]
    sed -i "s/\${message}/$msg/g" files[$i]

done

    
# Lanzar todos los clientes
for i in ${files[@]}; do
    #env CONFIG_FILE=$i ../client/target/client
done
wait $(jobs -pr)

# Eliminar archivos tmp
for i in ${files[@]}; do rm  $i; done
