# MyPetShop — Aplicación Android de Alimentos Premium para Mascotas

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose (Material 3)** para la gestión integral de una tienda de nutrición premium para mascotas (marcas asociadas como *Advance*, *Logus*, *Royal Canin*, *Taste of the Wild* y *Agility Gold*).

---

## 🛠️ Tecnologías y Arquitectura

El proyecto sigue una arquitectura limpia basada en **MVVM (Model-View-ViewModel)** con inyección de dependencias mediante **Hilt**, persistencia local con **Room Database** y diseño declarativo con **Jetpack Compose**.

```
com.example.mypetshop/
├── data/           # Repositorios, fuentes de datos locales (Room), sesiones y preferencias
├── domain/         # Modelos de datos de negocio y contratos de repositorios
├── ui/             # Pantallas (Screens), ViewModels y componentes de interfaz (Jetpack Compose)
└── util/           # Utilidades de seguridad (Hash PBKDF2, Biometría, Validación, etc.)
```

---

## 📖 Documentación de Clases y Métodos Relevantes

### 1. Capa de Datos (`data/`)

#### 🗄️ `MyPetShopDatabase.kt` & DAOs (`UserDao`, `ProductDao`, etc.)
- **Propósito**: Base de datos local SQLite administrada por Room. Define las tablas de usuarios, productos, mensajes de clientes y perfiles de mascotas.
- **Lógica**: Utiliza KSP para la generación de código en tiempo de compilación y estrategias de resolución de conflictos (`OnConflictStrategy.REPLACE`).

#### 🔐 `AuthRepositoryImpl.kt`
- **Propósito**: Implementación del repositorio de autenticación.
- **Métodos Clave**:
  - `login(email, password)`: Valida credenciales utilizando cifrado robusto **PBKDF2 con HMAC-SHA256** mediante `PasswordHasher`.
  - `register(...)`: Registra nuevos usuarios almacenando contraseñas seguras, preguntas y respuestas de privacidad para recuperación.
  - `verifySecurityAnswer(email, answer)`: Valida la respuesta secreta del usuario para permitir el restablecimiento de contraseña sin depender de códigos de 6 dígitos.

#### 🛒 `ShopSession.kt`
- **Propósito**: Gestiona el carrito de compras en tiempo real y el ciclo de vida de los pedidos del usuario.
- **Métodos Clave**:
  - `addToCart(product)` / `updateQuantity`: Controla los artículos seleccionados por el cliente.
  - `checkout(location, paymentMethod, shippingCents)`: Calcula el total de la orden sumando los productos y el costo del domicilio ($2,000 COP por kilómetro), registrando el pedido como pendiente para los repartidores.

---

### 2. Capa de Presentación e Interfaz (`ui/`)

#### 🛍️ `CartScreen.kt` & `CartViewModel.kt`
- **Propósito**: Interfaz de carrito y pasarela de pago (Checkout).
- **Lógica**: Permite capturar la ubicación GPS real del cliente (`FusedLocationProviderClient`), calcular automáticamente la distancia y costo de envío, seleccionar método de pago (**Tarjeta** o **Nequi**) y confirmar la compra.

#### 🛵 `DriverDashboardScreen.kt` & `DriverDashboardViewModel.kt`
- **Propósito**: Panel de control del repartidor.
- **Lógica**: Sincronizado en tiempo real con `ShopSession`, lista las órdenes pendientes de los clientes con su precio total y método de pago, permitiendo seleccionar un pedido, revisar la ruta y confirmar la entrega mediante código o autenticación biométrica.

#### 🔑 `LoginScreen.kt` & `RegisterScreen.kt`
- **Propósito**: Pantallas de autenticación y registro de usuarios.
- **Lógica**: Valida entradas de usuario, implementa un desafío de seguridad CAPTCHA anti-bot y permite configurar preguntas de seguridad en el registro.

---

## 🔒 Seguridad y Ciberseguridad

- **Cifrado de Contraseñas**: Uso de `PasswordHasher` con **PBKDF2-HMAC-SHA256** (120,000 iteraciones y salt aleatorio de 16 bytes).
- **Autenticación Biométrica**: Integración con `BiometricPrompt` (`androidx.biometric`) para validación de identidad.
- **Protección Anti-Bot**: Desafío CAPTCHA matemático en los formularios de inicio de sesión.
- **Recuperación Segura**: Sistema de preguntas de privacidad personal configuradas al registrar la cuenta.

---

## 👥 Roles y Credenciales de Demostración

| Rol | Correo / Usuario | Contraseña |
|-----|------------------|------------|

| **Administrador** | `admin@mypetshop.com` | `Admin1234` |


---

## 🚀 Cómo Compilar y Ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Khevinzhito/MyPetShop.git
   ```
2. Abre la carpeta en **Android Studio** (Ladybug o superior).
3. Sincroniza el proyecto con Gradle.
4. Ejecuta la aplicación en un emulador o dispositivo físico Android.

---

## 📦 Tamaño y Estructura del Repositorio

El repositorio en GitHub contiene todo el código fuente y recursos necesarios (~300 KB en ZIP). Las carpetas de compilación (`build/`, `.gradle/`) se generan automáticamente en tu entorno local al compilar.

---
*Autor: Kevin Zhito — Proyecto Académico MyPetShop*
