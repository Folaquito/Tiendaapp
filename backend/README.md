# Microservicio Tiendaapp Backend

Backend Kotlin/Spring Boot que expone catálogo y favoritos para la app Android.

## Prerrequisitos (equipo virgen)

- JDK 17 instalado y en PATH (`java -version` debe mostrar 17). Si no, instala Temurin 17.
- Git y curl disponibles en consola.
- Variable de entorno `RAWG_API_KEY` con tu clave válida de api.rawg.io (o edita `backend/src/main/resources/application.properties`).
- Opcional: jq para inspeccionar JSON en consola (`winget install jqlang.jq`).

## Arranque rápido

Desde la raíz del repo:

```powershell
# Clonar (si aplica) y entrar
# git clone <repo> && cd <repo>

# Levantar backend con el wrapper de Gradle
./gradlew -p backend bootRun
```

El servicio queda en `http://localhost:8081` con H2 en memoria. Consola H2: `http://localhost:8081/h2-console` (JDBC `jdbc:h2:mem:tiendaapp`, user `sa`, sin contraseña).

## Endpoints básicos

- `GET /api/productos` – listar productos.
- `POST /api/productos/import/rawg` – importa desde RAWG (params `precio`, `stock`, `page`, `pageSize`).
- `GET /favorites` – lista favoritos.
- `POST /favorites` – upsert favorito `{gameId,title,imageUrl,note?}`.
- `DELETE /favorites/{gameId}` – borra por `gameId`.

## Smoke checks sugeridos

```powershell
# Importa 20 juegos de RAWG con precio y stock obligatorios
curl -X POST "http://localhost:8081/api/productos/import/rawg?precio=24990&stock=5&page=1&pageSize=20"

# Verifica que el catálogo tenga al menos 20 productos
curl http://localhost:8081/api/productos | jq 'length'

# Inserta un favorito de prueba
curl -X POST http://localhost:8081/favorites ^
	-H "Content-Type: application/json" ^
	-d '{"gameId":99,"title":"Demo","imageUrl":"https://example.com/img.jpg"}'

# Lista favoritos
curl http://localhost:8081/favorites | jq
```
