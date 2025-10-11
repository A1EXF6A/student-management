# Conexion UTA

Aplicación de escritorio Java Swing para gestionar estudiantes, cursos e inscripciones.

Este proyecto fue creado con NetBeans y utiliza formularios generados (JInternalFrame). Está pensado como una app de ejemplo para gestionar datos en MySQL.

## Estructura del repositorio

- `src/` - Código fuente Java.
  - `src/cuartouta/` - Clases principales de la aplicación: `Estudiantes.java`, `Cursos.java`, `Inscripcion.java`, `Conexion.java`, `Principal.java`, `Login.java`, `Main.java`.
  - `src/reportes/` - Clases para generación y visualización de reportes en PDF (usa Apache PDFBox si está disponible).
- `build/` - Artefactos de compilación (archivos `.class`) generados por NetBeans. No es necesario en el control de versiones.
- `dist/` - Distribuciones/jar empaquetados (generados). No es necesario en el control de versiones.
- `nbproject/` - Metadatos del proyecto NetBeans.
- `reportes/` - Carpeta de salida de reportes PDF generados (puede contener PDFs generados anteriormente).
- `run.ps1` - Script PowerShell para compilar y ejecutar desde línea de comandos usando las JAR en `lib/`.
- `README_PDFBOX.md` - Notas adicionales sobre cómo obtener PDFBox y ejecutar los reportes.

## Funcionalidad principal

- CRUD para Estudiantes, Cursos e Inscripciones.
- Búsqueda dinámica en tablas (filtros en tiempo real mientras se escribe).
- Reportes en PDF (Estudiantes, Cursos, Inscripciones) generados programáticamente.
- Visualizador de PDF embebido en la aplicación (usa PDFBox para renderizar páginas como imágenes dentro de un `JInternalFrame`).
- Las ventanas internas (`JInternalFrame`) son cerrables y el sistema evita que se solapen al abrir nuevas ventanas.

## Dependencias externas

Para compilar y ejecutar completamente desde línea de comandos (fuera de NetBeans) necesitas añadir las librerías externas en `lib/` y usar el classpath apropiado.

Recomendadas:
- Apache PDFBox (por ejemplo `pdfbox-app-2.0.27.jar` o `pdfbox-2.0.27.jar` + `fontbox-2.0.27.jar`).
- (Opcional) JasperReports / DynamicReports si deseas generar `.jasper`/`.jrxml` o usar esas herramientas.

Si ejecutas dentro de NetBeans y has añadido esas librerías al proyecto por el IDE, NetBeans ya las manejará.

## Cómo compilar y ejecutar (PowerShell)

1. Coloca las JARs en `lib/` (por ejemplo `lib/pdfbox-app-2.0.27.jar`).
2. Compilar todo:

```powershell
# desde la raíz del repo (PowerShell)
javac -d build/classes -cp "lib/*" src/cuartouta/*.java src/reportes/*.java
```

3. Ejecutar la aplicación:

```powershell
java -cp "build/classes;lib/*" cuartouta.Main
```

Alternativamente usa `.
un.ps1` si tienes las librerías puestas y el script configurado.

## Notas importantes

- Los archivos en `build/`, `dist/` y los PDFs generados en `reportes/` son outputs generados — pueden eliminarse sin afectar el código fuente. Si quieres que los elimine o archive automáticamente, puedo hacerlo.
- Si trabajas en macOS/linuxto y usas atajos de teclado, NetBeans maneja los accelerators; en el código hay atajos configurados con `KeyStroke`.
- Para evitar problemas de reflexión con JasperReports en Java 9+ podrías necesitar flags `--add-opens` en la JVM o ejecutar con Java 8.

## Próximos pasos sugeridos

- Añadir `lib/` al proyecto con las JARs necesarias (`pdfbox`, `fontbox`, etc.) y ejecutar la compilación.
- (Mejora) Reemplazar los `String` en los `JComboBox` por objetos `ComboItem(id,name)` para evitar parsing frágil.
- (Mejora) Convertir reportes a DynamicReports o Jasper para aprovechar plantillas y facilidad de diseño.

---

## Esquema de base de datos (SQL)

A continuación se incluyen los scripts SQL para crear las tablas usadas por la aplicación: `estudiante`, `cursos` y `estudiante_curso`.
Estos scripts están pensados para MySQL/MariaDB. Ajusta tipos/longitudes según tu servidor y necesidades.

```sql
-- Tabla de estudiantes
CCREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(30) NOT NULL,
    password VARCHAR(10) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

CREATE TABLE estudiante (
    est_cedula CHAR(10) NOT NULL,
    est_nombre VARCHAR(50) NOT NULL,
    est_apellido VARCHAR(50) NOT NULL,
    est_direccion VARCHAR(50) NOT NULL,
    est_telefono CHAR(10) NOT NULL,
    PRIMARY KEY (est_cedula)
);

CREATE TABLE cursos (
    cursoid INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL
);

-- 🔗 Tabla intermedia para la relación muchos a muchos
CREATE TABLE estudiante_curso (
    est_cedula CHAR(10) NOT NULL,
    cursoid INT NOT NULL,
    PRIMARY KEY (est_cedula, cursoid),
    FOREIGN KEY (est_cedula) REFERENCES estudiante(est_cedula)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (cursoid) REFERENCES cursos(cursoid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

```

Notas:
- `est_cedula` se usa como PK en `estudiante` y como FK en `estudiante_curso`.
- La aplicación actualmente busca `cursoid` por `nombre` y usa `est_cedula` para las inscripciones.
- Si tu columna `est_cedula` en la base de datos tiene otra longitud, ajusta `VARCHAR(50)` al valor real.
