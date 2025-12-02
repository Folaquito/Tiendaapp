# Microservicio Tiendaapp Backend

Microservicio en Spring Boot que expone un CRUD básico de productos para ser consumido por la app Android.

## Endpoints

- `GET /api/productos` – listar todos los productos.
- `GET /api/productos/{id}` – obtener detalle por id.
- `POST /api/productos` – crear producto (JSON completo).
- `PUT /api/productos/{id}` – actualizar los campos del producto.
- `DELETE /api/productos/{id}` – eliminar registro.

## Ejecución local

Desde la raíz del repositorio:

```powershell
../gradlew -p backend bootRun
```

Por defecto corre en `http://localhost:8081` y usa H2 en memoria. La consola H2 queda disponible en `http://localhost:8081/h2-console` (JDBC `jdbc:h2:mem:tiendaapp`).
