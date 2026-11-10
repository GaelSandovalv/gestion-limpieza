# Gestión de Actividades de Limpieza

Aplicación de escritorio para que una empresa privada que da servicios de limpieza al municipio de Saltillo registre sus actividades: qué se hizo, qué cuadrilla lo hizo, en qué lugar, qué colonias se atendieron y la imagen de evidencia. Incluye un dashboard con el resumen de actividades por cuadrilla.

Proyecto del Parcial 2 de Diseño y Arquitectura (MVC).

## Integrantes

- Gael Sandoval Villanueva
- Diego Giovanny Rivera Guerrero

## Tecnologías

- Java 21 con Swing (interfaz gráfica)
- Maven (se incluye el wrapper `mvnw`, no hace falta instalarlo)
- MySQL 8.0 (driver JDBC `mysql-connector-j`)

## Arquitectura

El proyecto sigue el patrón **MVC**:

- `mx.limpieza.modelo`: entidades, acceso a datos (DAO), conexión y sesión.
- `mx.limpieza.vista`: ventanas y paneles de Swing.
- `mx.limpieza.controlador`: reciben los eventos de las vistas y usan el modelo.

Patrones de diseño utilizados:

- **Singleton**: `ConexionBD` (una sola conexión a la base de datos) y `Sesion` (administrador que inició sesión).
- **DAO**: la interfaz genérica `DAO<T>` y una clase DAO por cada entidad.

## Cómo ejecutar

1. **Crear la base de datos:** abrir MySQL Workbench, conectarse como `root` y ejecutar el script `bd/01_esquema.sql`. Crea la base `gestion_limpieza`, sus tablas, el administrador inicial y el usuario `limpieza_app` que usa la aplicación.
2. **Ejecutar la aplicación:**
   - Desde VS Code (con el *Extension Pack for Java*): abrir la carpeta del proyecto y ejecutar la clase `mx.limpieza.Main`.
   - Desde la terminal, en la carpeta del proyecto: `mvnw.cmd compile exec:java`. La variable `JAVA_HOME` debe apuntar al JDK 21.
3. **Entrar** con el usuario `admin` y la contraseña `admin123`.

Los datos de conexión están en `src/main/resources/db.properties`.
