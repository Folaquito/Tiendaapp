# Tiendaapp

Aplicación Android desarrollada con Kotlin + Jetpack Compose para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**. El proyecto cubre registro/login con validación de RUT, catálogo de juegos, carrito de compras y un Back Office conectado a un microservicio propio, además de una sección de recomendaciones que consume la API externa RAWG.

## Integrantes
- Cristopher Ricchasse
- Joaquín Fernández

## Arquitectura y funcionalidades principales
- **Compose + MVVM + Navigation** para todas las pantallas (registro, login, catálogo, carrito, back office, detalle, resultados de compra).
- **Validaciones**: correos, contraseñas y RUT en registro/login con mensajes al usuario.
- **Persistencia local**: Room (`JuegoEntity`, `JuegoDao`, `AppDatabase`) cachea el catálogo descargado.
- **Microservicio propio** (`backend/`): CRUD de productos con Spring Boot + H2. Back Office crea y elimina productos reales.
- **API externa RAWG**: sección “Recomendados” en `CatalogoScreen` muestra juegos obtenidos en vivo y diferencia visualmente su origen.
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

## API externa RAWG (IE 3.1.4)
- Crea una cuenta en [https://rawg.io/apidocs](https://rawg.io/apidocs) y copia tu API key.
- En `local.properties` agrega `RAWG_API_KEY=tu_llave_real` (no se versiona).
- El `JuegoRepository` usa esa key para descargar descripciones y recomendaciones. Si falta, la app mostrará un mensaje en la sección externa.

## Ejecución de la app
1. **Variables locales**:
	- `local.properties`: debe contener `sdk.dir=...` y `RAWG_API_KEY=...`.
	- (Opcional) `gradle.properties` con credenciales de firma (ver más abajo).
2. **Backend**: inicia el microservicio con `./gradlew -p backend bootRun`.
3. **Aplicación Android**: ejecuta `./gradlew installDebug` o usa Android Studio/`adb` para lanzar en emulador/dispositivo.

## Pruebas (IE 3.2.1)
- **Unit tests** (`./gradlew test`):
  - `CartViewModelTest` (cálculo del carrito).
  - `LoginViewModelTest` (validación de RUT).
  - `JuegoViewModelTest` (manejo de API externa con MockK).
- **UI test** (`./gradlew connectedAndroidTest` con dispositivo/emulador):
  - `RegisterScreenTest` asegura que el botón “Registrar” parte deshabilitado cuando los campos están vacíos.

## Firma release (IE 3.3.1)
1. Genera un keystore en Android Studio (Build > Generate Signed Bundle / APK) y ubícalo, por ejemplo, en `keystore/tiendaapp-release.jks`.
2. En `~/.gradle/gradle.properties` (o `local.properties`, sin versionar) define:
	```
	RELEASE_STORE_FILE=/ruta/absoluta/tiendaapp-release.jks
	RELEASE_STORE_PASSWORD=********
	RELEASE_KEY_ALIAS=tiendaapp
	RELEASE_KEY_PASSWORD=********
	```
3. El bloque `signingConfigs` de `app/build.gradle.kts` usa estos valores. Para generar el APK ejecuta `./gradlew assembleRelease`.
4. Documenta en Trello/Git evidencias del keystore (sin exponer contraseñas reales).

## Endpoints consumidos
- **Microservicio propio**: `http://10.0.2.2:8081/api/productos` (CRUD completo desde Back Office y catálogo principal).
- **RAWG API externa**: `https://api.rawg.io/api/games` y `https://api.rawg.io/api/games/{id}` (sección de recomendaciones y descripciones extendidas).
