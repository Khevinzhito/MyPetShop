# MyPetShop — Arquitectura del proyecto

Aplicación Android nativa para tienda de alimentos premium para mascotas (marca Advance / Logus).

**Tecnologías:** Kotlin · Jetpack Compose · Material 3 · MVVM · Hilt · Room (KSP) · Navigation Compose

---

## Estructura de carpetas

```
app/src/main/java/com/example/mypetshop/
│
├── MainActivity.kt              → Punto de entrada de la app
├── MyPetShopApplication.kt      → Application con Hilt
│
├── ui/                          → CAPA DE INTERFAZ (Compose + ViewModels)
│   ├── auth/                    → Bienvenida, login, registro
│   ├── onboarding/              → Registro de mascota del usuario
│   ├── main/                    → Menú principal del comprador (drawer)
│   ├── shop/                    → Tienda, carrito, pedidos, mensajes
│   ├── advance/                 → Catálogo Advance & Logus
│   ├── about/                   → Sobre nosotros
│   ├── admin/                   → Panel administrador
│   ├── driver/                  → Registro y panel repartidor
│   ├── navigation/              → Rutas y grafo de navegación
│   ├── components/              → Componentes reutilizables (logo, mascota, etc.)
│   └── theme/                   → Colores y tema Material 3
│
├── domain/                      → CAPA DE DOMINIO (reglas de negocio puras)
│   ├── model/                   → Clases de datos: mascota, producto, usuario, pedido…
│   └── repository/              → Interfaces de repositorios (contratos)
│
├── data/                        → CAPA DE DATOS (implementaciones)
│   ├── local/                   → Room: base de datos, entidades, DAOs, mappers
│   ├── repository/              → Implementación de repositorios (Room)
│   ├── session/                 → Sesión de usuario, carrito y pedidos en memoria
│   ├── seed/                    → Catálogos iniciales de productos
│   ├── demo/                    → Cuentas y datos de demostración
│   └── tips/                    → Motor de tips nutricionales para mascotas
│
├── di/                          → INYECCIÓN DE DEPENDENCIAS (Hilt)
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
└── util/                        → Utilidades (validación, biometría, extensiones)
```

---

## Patrón arquitectónico: MVVM + Clean Architecture simplificada

```
┌─────────────────────────────────────────────────────────┐
│  UI (Compose)                                           │
│  Pantallas + ViewModels                                 │
└───────────────────────┬─────────────────────────────────┘
                        │ observa StateFlow / Llama funciones
┌───────────────────────▼─────────────────────────────────┐
│  DOMAIN                                                 │
│  Modelos + Interfaces Repository                        │
└───────────────────────┬─────────────────────────────────┘
                        │ implementa
┌───────────────────────▼─────────────────────────────────┐
│  DATA                                                   │
│  Room · Repositorios · Sesión · Catálogos               │
└─────────────────────────────────────────────────────────┘
```

### Flujo de datos (ejemplo: tienda)

1. `MarketplaceScreen` muestra productos observando `ShopViewModel.products`
2. `ShopViewModel` obtiene datos de `ProductRepository` (interfaz en `domain`)
3. `ProductRepositoryImpl` (en `data/repository`) lee de Room vía `ProductDao`
4. Al añadir al carrito, `ShopViewModel` delega en `ShopSession` (estado en memoria)

---

## Roles de la aplicación

| Rol | Paquete UI | Descripción |
|-----|------------|-------------|
| **Comprador** | `ui/main`, `ui/shop` | Tienda, carrito, pedidos, tips de mascota |
| **Administrador** | `ui/admin` | CRUD productos, mensajes, reporte de ventas |
| **Repartidor** | `ui/driver` | Registro y panel de entregas (desde menú comprador) |

---

## Navegación

| Archivo | Responsabilidad |
|---------|-----------------|
| `ui/navigation/Routes.kt` | Constantes de rutas |
| `ui/navigation/AppNavigation.kt` | NavHost principal (auth → onboarding → main/admin) |
| `data/session/NavigationSession.kt` | Usuario, perfil mascota y repartidor en sesión |

---

## Persistencia (Room)

| Tabla | Entidad | DAO |
|-------|---------|-----|
| Perfil mascota | `PetProfileEntity` | `PetProfileDao` |
| Productos | `ProductEntity` | `ProductDao` |
| Mensajes | `ClientMessageEntity` | `ClientMessageDao` |

Los **mappers** convierten entidades Room ↔ modelos de dominio.

---

## Recursos

```
app/src/main/res/
├── values/strings.xml    → Textos de la app (español)
├── values/colors.xml     → Paleta blanco + naranja
└── drawable/             → Iconos, logo, mascotas (perro/gato)
```

---

## Cómo evaluar el código (guía para revisión)

1. **Empieza por** `MainActivity.kt` → `AppNavigation.kt` → pantalla según rol
2. **Cada pantalla** tiene su ViewModel en el mismo paquete de feature (`ui/shop/`, `ui/auth/`, etc.)
3. **Los modelos** están en `domain/model/` sin dependencias de Android
4. **Room y APIs** están ocultos detrás de repositorios en `data/repository/`
5. **Hilt** conecta todo en `di/`

---

## Cuentas de demostración

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Comprador | kbrgvinzhito@gmail.com | 3027899171Aa |
| Admin | admin@mypetshop.com | Admin1234 |

---

*Proyecto académico — MyPetShop · Logus / Advance*
