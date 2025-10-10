Instrucciones para integrar Apache PDFBox y ejecutar el proyecto desde PowerShell

Resumen
-------
Este proyecto ahora utiliza Apache PDFBox para generar y visualizar reportes PDF. Para compilar y ejecutar desde PowerShell necesitas descargar los jars de PDFBox y colocarlos en la carpeta `lib/` en la raíz del proyecto.

Pasos rápidos
------------
1) Crear carpeta lib (si no existe):

   New-Item -ItemType Directory -Path .\lib

2) Descargar un JAR (opción simple):
   - pdfbox-app-2.0.27.jar (contiene PDFBox + FontBox + utilidades). Puedes descargarlo desde:
     https://downloads.apache.org/pdfbox/2.0.27/pdfbox-app-2.0.27.jar

   Copia `pdfbox-app-2.0.27.jar` dentro de `lib/`.

3) Compilar y ejecutar con el script incluido:
   - Abrir PowerShell en la raíz del proyecto y ejecutar:
     .\run.ps1

   - Si alguna librería necesita apertura reflexiva con módulos, agrega el parámetro -AddOpens:
     .\run.ps1 -AddOpens

Notas
-----
- Si prefieres usar jars separados (pdfbox + fontbox), copia ambos en lib/.
- La primera ejecución de PDFBox puede tardar un poco ya que reconstruye el font cache.
- El script `run.ps1` compila todos los archivos en `src/cuartouta` y ejecuta `cuartouta.Main`.

Problemas comunes
-----------------
- "package org.apache.pdfbox... does not exist": indica que no colocaste los jars en `lib/` o la ruta del classpath es incorrecta.
- Errores de apertura reflexiva (Java 9+): usa el parámetro -AddOpens del script para añadir `--add-opens java.base/java.util=ALL-UNNAMED`.

Si quieres, puedo añadir pdfbox-app.jar por ti al repo si me confirmas la versión que tienes descargada y quieres que cree una carpeta lib/ con el jar (no descargaré archivos desde internet automáticamente).