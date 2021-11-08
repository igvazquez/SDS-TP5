# Simulacion de Sistemas - TP 5
Dinámica Peatonal - Egreso a través de puerta angosta

Para correr la simulacion, el comando que se debe ejecutar es
```
mvn compile exec:java -Dexec.mainClass=Main
```
Existen 3 clases posibles para ejecutar:
- Main: corre la simulacion con los parametros pasados a config.yml
- Ejb: genera los datos necesarios para correr el script de python con el mismo nombre
- Ejc: genera los datos necesarios para correr el script de python con el mismo nombre

```yaml
# Params
minR: 0.15
maxR: 0.32
maxV: 2.0
maxMass: 1.0
beta: 0.9
tau: 0.5
l: 20.0
d: 1.2
n: 200
```

Para graficar, es necesario tener instalado matplotlib, numpy, pandas

podemos correr los scipts solo con
```
python3 <script>
```
