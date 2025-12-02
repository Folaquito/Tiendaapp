# TiendaApp - Catálogo de Juegos

## 2. Integrantes

- **Joaquin Fernandez**
- **Cristopher Richasse**

## 3. Funcionalidades

La aplicación permite explorar un catálogo de videojuegos y gestionar una lista de favoritos personalizada.

- **Catálogo de Juegos**: Visualización de una lista de juegos populares obtenidos desde la API de RAWG.
- **Detalle de Juego**: Vista detallada con descripción, imagen, valoración y precio simulado.
- **Gestión de Favoritos (CRUD Completo)**:
  - **Create**: Agregar juegos a la lista de favoritos.
  - **Read**: Visualizar la lista de juegos favoritos guardados en el backend.
  - **Update**: Agregar y editar **Notas Personales** en los juegos favoritos.
  - **Delete**: Eliminar juegos de la lista de favoritos.
- **Persistencia**: Los favoritos y sus notas se guardan en una base de datos H2 (archivo local) a través del microservicio Spring Boot.

## 4. Endpoints Utilizados

### API Externa (RAWG)

- `GET https://api.rawg.io/api/games`: Obtiene el listado de juegos.
- `GET https://api.rawg.io/api/games/{id}`: Obtiene el detalle y descripción de un juego.

### Microservicio Propio (Spring Boot)

- `GET http://10.0.2.2:8080/favorites`: Obtiene todos los favoritos.
- `POST http://10.0.2.2:8080/favorites`: Agrega un nuevo favorito.
- `PUT http://10.0.2.2:8080/favorites/{gameId}`: Actualiza la nota personal de un favorito.
- `DELETE http://10.0.2.2:8080/favorites/{gameId}`: Elimina un favorito.

## 5. Pasos para Ejecutar

### Requisitos Previos

- Android Studio Ladybug o superior.
- JDK 17.
- Emulador Android configurado.
- Crear el archivo "local.properties" y dejar RAWG_API_KEY=<llave>

### Paso 1: Iniciar el Backend

El backend es necesario para la funcionalidad de favoritos.

1.  Abrir una terminal en la carpeta raíz del proyecto.
2.  Navegar a la carpeta `backend`:

    cd backend

3.  Ejecutar el servidor Spring Boot:

    ..\gradlew.bat bootRun

4.  Esperar a que aparezca el mensaje `Tomcat started on port 8081`.

### Paso 2: Ejecutar la App Android

1.  Abrir el proyecto en Android Studio (carpeta `app` o raíz).
2.  Sincronizar Gradle.
3.  Seleccionar el dispositivo emulador.
4.  Presionar **Run**

## 6. APK Firmado y Llave

Los archivos generados para la entrega se encuentran en las siguientes rutas:

- **APK Firmado**: `app/build/outputs/apk/release/app-release.apk`
- **Keystore (.jks)**: `app/release-key.jks`
