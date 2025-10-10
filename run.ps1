# Run script for conexionUta (PowerShell)
# Compila y ejecuta la aplicación incluyendo jars en lib\*

param(
    [string]$MainClass = "cuartouta.Main",
    [switch]$AddOpens
)

# Ensure lib exists
if (-not (Test-Path .\lib)) {
    Write-Host "Carpeta lib/ no encontrada. Crea la carpeta y coloca pdfbox-app-2.x.jar (o pdfbox+fontbox) y otras dependencias allí." -ForegroundColor Yellow
    exit 1
}

# Compile
Write-Host "Compiling..."
javac -cp "lib/*;src" -d build\classes src\cuartouta\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilación fallida. Asegúrate de que los jars están en lib/ y vuelve a intentar." -ForegroundColor Red
    exit $LASTEXITCODE
}

# Run
$cp = "lib/*;build\classes"
$javaCmd = "java"
$flags = "-cp \"$cp\" $MainClass"
if ($AddOpens) {
    $flags = "--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED -cp \"$cp\" $MainClass"
}

Write-Host "Running: $javaCmd $flags"
& $javaCmd $flags

exit $LASTEXITCODE
