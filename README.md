# Tiendaapp

Aplicación Android de una tienda de videojuegos desarrollada con Kotlin + Jetpack Compose para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**. El proyecto cubre registro/login con validaciones, catálogo de juegos, carrito de compras y un Back Office conectado a un microservicio propio, además de que el catalogo consume la API externa RAWG.

## Integrantes
- Cristopher Ricchasse
- Joaquín Fernández

## Arquitectura y funcionalidades principales
- **Compose + MVVM + Navigation** para todas las pantallas (registro, login, catálogo, carrito, back office, detalle, resultados de compra).
- **Validaciones**: correos, contraseñas y RUT en registro/login con mensajes al usuario.
- **Persistencia local**: Room (`JuegoEntity`, `JuegoDao`, `AppDatabase`) cachea el catálogo descargado.
- **Microservicio propio** (`backend/`): CRUD de productos con Spring Boot + H2. Back Office crea y elimina productos reales.
- **API externa RAWG**: En `CatalogoScreen` muestra juegos obtenidos en vivo y diferencia visualmente su origen.
- **Pruebas**: unitarias para ViewModels/lógica y una prueba Compose UI básica.

## Microservicio Spring Boot
1. Posicionese en la raíz del repositorio.
2. Ejecute `./gradlew -p backend bootRun`.
3. Endpoints expuestos (base `http://localhost:8081/api/productos`):
	- `GET /` listar productos.
	- `GET /{id}` obtener detalle.
	- `POST /` crear producto.
	- `PUT /{id}` actualizar producto.
	- `DELETE /{id}` eliminar producto.
4. La BD H2 se inicializa con datos (`data.sql`). Puede ver la consola en `http://localhost:8081/h2-console`.

## API externa RAWG
- Cree una cuenta en [https://rawg.io/apidocs](https://rawg.io/apidocs) y copie su API key.
- En `local.properties` agrega `RAWG_API_KEY=Llave` (no se versiona).
- El `JuegoRepository` usa esa key para descargar descripciones. Si falta, la app mostrará un mensaje.

## Ejecución de la app
1. **Variables locales**:
	- `local.properties`: debe contener `sdk.dir=...` y `RAWG_API_KEY=...`.
	- (Opcional) `gradle.properties` con credenciales de firma (ver más abajo).
2. **Backend**: inicia el microservicio con `./gradlew -p backend bootRun`.
3. **Aplicación Android**: ejecuta `./gradlew installDebug` o usa Android Studio/`adb` para lanzar en emulador/dispositivo.

## Pruebas
- **Unit tests** (`./gradlew test`):
  - `CartViewModelTest` (cálculo del carrito).
  - `LoginViewModelTest` (validación de RUT).
  - `JuegoViewModelTest` (manejo de API externa con MockK).
- **UI test** (`./gradlew connectedAndroidTest` con dispositivo/emulador):
  - `RegisterScreenTest` asegura que el botón “Registrar” parte deshabilitado cuando los campos están vacíos.

## Endpoints consumidos
- **Microservicio propio**: `http://10.0.2.2:8081/api/productos` (CRUD completo desde Back Office y catálogo principal).
- **RAWG API externa**: `https://api.rawg.io/api/games` y `https://api.rawg.io/api/games/{id}` (descripciones extendidas).
