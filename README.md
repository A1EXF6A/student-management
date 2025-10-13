# Student-management

Aplicación de escritorio Java Swing para gestionar estudiantes, cursos e inscripciones.

Este proyecto fue creado con NetBeans y utiliza formularios generados (JInternalFrame). Está pensado como una app de ejemplo para gestionar datos en MySQL.

# Student-management

Aplicación de escritorio Java (Swing) para gestionar estudiantes, cursos e inscripciones. Fue creada con NetBeans y utiliza ventanas internas (`JInternalFrame`) para las pantallas CRUD.

Este README documenta la estructura del proyecto, las clases principales, cómo compilar/ejecutar, dependencias y el esquema de base de datos.

## Resumen rápido

- Lenguaje: Java (Swing)
- IDE de referencia: NetBeans (project metadata en `nbproject/`)
- Base de datos: MySQL/MariaDB (conector JDBC)
- Reportes: JasperReports (.jrxml/.jasper) incluidos en `src/reportes/` y visores integrados en la app
- Renderizado/visualizador de PDF: Opcional (PDFBox si se añade)

## Estructura principal del repositorio

- `src/` - Código fuente Java
    - `src/cuartouta/` - Clases de la aplicación (ver sección "Clases principales")
    - `src/reportes/` - Plantillas de JasperReports (`.jrxml`) y archivos generados
- `build/` - Clases compiladas generadas por NetBeans (build output)
- `nbproject/` - Metadatos del proyecto NetBeans
- `reportes/` - Salida de reportes (PDFs u otros artefactos)
- `icons/`, `ireport/` - Recursos gráficos y archivos de diseño
- `README.md` - Este documento

Nota: las carpetas `build/` y `reportes/` contienen outputs que pueden regenerarse y no son necesarias en VCS si prefieres limpiarlas.

## Clases principales y responsabilidades

Resumen de las clases principales que encontré en `src/cuartouta/`:

- `Conexion.java` — Clase helper que encapsula la conexión JDBC. Actualmente contiene las constantes:
    - URL: `jdbc:mysql://localhost:3306/cv`
    - USER: `root`
    - PASSWORD: `` (vacío)
    Recomendación: extraer estas credenciales a un archivo de configuración o variables de entorno.

- `Main.java` — Punto de entrada simple que muestra la ventana de login (`Login`).

- `Login.java` — JFrame de autenticación. Lee `users` desde la BD y, si el login es correcto, abre la ventana `Principal` pasando el rol del usuario.
    - `getPassword()` devuelve la contraseña leída del `JPasswordField`.
    - `setIconLabel(...)` es un helper para cargar y ajustar iconos en labels.

- `Principal.java` — JFrame principal que contiene un `JDesktopPane` (`jdskPrincipal`) y el menú de la aplicación.
    - Controla la apertura de `JInternalFrame` sin solapamientos mediante `addInternalFrameNoOverlap(...)`.
    - Contiene atajos de teclado para abrir ventanas y lanzar reportes (usa JasperReports para compilar `.jrxml`).
    - Control de permisos básicos mediante el `rol` del usuario (ej. `isAdmin()`).

- `Estudiantes.java` — `JInternalFrame` con CRUD para la tabla `estudiante`.
    - Validaciones de campos (cedula solo dígitos, teléfono empieza con `09`, límites de longitud).
    - Métodos: `save()`, `getData(filter)`, `updateStudent()`, `deleteStudent()` y utilidades para controlar botones/estado.

- `Cursos.java` — `JInternalFrame` con CRUD para la tabla `cursos`.
    - Métodos: `saveCourse()`, `getData(filter)`, `deleteCourse()`, `updateCourse()`.

- `Inscripcion.java` — `JInternalFrame` para gestionar inscripciones (tabla intermedia `estudiante_curso`).
    - Carga estudiantes y cursos en `JComboBox` y guarda una inscripción buscando `cursoid` por `nombre`.
    - Contiene comprobaciones adicionales (existencia del estudiante, tamaño de columna, manejo de constraints).

- `CuartoUTA.java` — Clase con `main` de prueba que intenta abrir una conexión y la cierra inmediatamente (útil para testear la conexión a BD desde línea de comandos).

## Esquema de base de datos (MySQL)

Las tablas que usa la aplicación (simplificado):

1) Tabla de usuarios para login

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);
```

2) Tabla de estudiantes

```sql
CREATE TABLE estudiante (
    est_cedula VARCHAR(50) NOT NULL,
    est_nombre VARCHAR(100) NOT NULL,
    est_apellido VARCHAR(100) NOT NULL,
    est_direccion VARCHAR(255) DEFAULT 'S/D',
    est_telefono VARCHAR(20) DEFAULT '00000',
    PRIMARY KEY (est_cedula)
);
```

3) Tabla de cursos

```sql
CREATE TABLE cursos (
    cursoid INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);
```

4) Tabla intermedia (inscripciones)

```sql
CREATE TABLE estudiante_curso (
    est_cedula VARCHAR(50) NOT NULL,
    cursoid INT NOT NULL,
    PRIMARY KEY (est_cedula, cursoid),
    FOREIGN KEY (est_cedula) REFERENCES estudiante(est_cedula) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (cursoid) REFERENCES cursos(cursoid) ON DELETE CASCADE ON UPDATE CASCADE
);
```

Notas:
- Ajusta los tamaños (`VARCHAR`) según tus requisitos reales. En el código hay comprobaciones defensivas (por ejemplo la clase `Inscripcion` consulta metadata para el tamaño de la columna `est_cedula`).

## Dependencias necesarias

Para compilar y ejecutar fuera de NetBeans (línea de comandos), coloca las JARs en `lib/` y añade al classpath:

- Conector JDBC MySQL (por ejemplo `mysql-connector-java-8.0.xx.jar`)
- JasperReports (si usas los `.jrxml`/`.jasper` incluidos)
- Apache PDFBox (si vas a usar el visualizador embebido)

Si trabajas en NetBeans, añade esas JARs desde el panel de librerías del proyecto y NetBeans las incluirá en el classpath de ejecución.

## Cómo compilar y ejecutar (PowerShell / Windows)

1) Coloca las JARs necesarias dentro de una carpeta `lib/` en la raíz del proyecto.

2) Compilar los fuentes:

```powershell
# desde la raíz del repositorio (PowerShell)
javac -d build/classes -cp "lib/*" src/cuartouta/*.java src/reportes/*.java
```

3) Ejecutar la aplicación:

```powershell
java -cp "build/classes;lib/*" cuartouta.Main
```

Notas:
- En Windows el separador de classpath es `;`. En Unix (macOS/Linux) es `:`.
- Si prefieres usar Ant (NetBeans genera `build.xml`) o ejecutar directamente desde NetBeans, usa la tarea `clean` / `build` del IDE.

### Ejecutar desde NetBeans

- Abrir el proyecto en NetBeans. Asegúrate de añadir las librerías externas (Jasper, PDFBox, Connector JDBC) desde Propiedades → Librerías.
- Ejecutar con el botón "Run" de NetBeans.

## Reportes (JasperReports)

- Los action listeners en `Principal` llaman a `JasperCompileManager.compileReport("src\\reportes\\<nombre>.jrxml")` y luego `JasperFillManager.fillReport(...)` usando la conexión de `Conexion.java`.
- Para usar los reportes necesitas tener JasperReports en el classpath (librerías Jasper + sus dependencias).
- Alternativa: precompilar los `.jrxml` a `.jasper` y distribuir los `.jasper` para acelerar la carga en producción.

## Buenas prácticas y recomendaciones

- No dejes credenciales en código: mueve la URL/usuario/contraseña de `Conexion.java` a un archivo `config.properties` o variables de entorno.
- Usa password hashing para usuarios (`users.password`) en lugar de almacenar contraseñas en texto plano.
- Evita concatenar SQL con valores del usuario; usa siempre `PreparedStatement` (la mayoría del código ya lo hace, pero hay sitios con concatenación en `Estudiantes.updateStudent()` y `deleteStudent()` que conviene corregir).
- Valida y maneja excepciones de forma consistente; libera recursos JDBC en bloques `finally` o usa try-with-resources.

## Solución de problemas comunes

- Error de conexión JDBC: verifica que MySQL esté corriendo, que existe la base `cv` y que el usuario/contraseña son correctos. Prueba `CuartoUTA.main()` para un test rápido de conexión.
- Error al generar reportes con Jasper en Java 9+: puede requerir flags adicionales `--add-opens` si hay errores de reflexión.
- Problemas con encoding/acentos en reportes: verificar `locale` y configuración de la fuente en Jasper/PDFBox.

## Próximos pasos sugeridos (mejoras)

- Extraer configuración de conexión a archivo `config.properties`.
- Reemplazar las listas de `String` en `JComboBox` por objetos modelo (ej. `ComboItem{id,name}`) para evitar parsing de cadenas.
- Añadir pruebas unitarias simples sobre la lógica no-GUI (DAO/servicios) y un script de CI que compile el proyecto.
- Limpiar SQL que concatena parámetros y usar `PreparedStatement` en todos los lugares.

## Contribuir

Si deseas contribuir, abre un issue describiendo lo que planeas cambiar y crea un PR con cambios pequeños y bien comentados. Si vas a añadir dependencias nuevas, documenta la razón en el PR.

## Licencia

Incluye la licencia que corresponda si deseas compartir públicamente (este repositorio no tiene una licencia explícita en este README).

---
