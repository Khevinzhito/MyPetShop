# MyPetShop

Aplicación Android nativa para tienda de alimentos premium para mascotas (marca Advance / Logus).

## Importante sobre el tamaño del repositorio

El proyecto descargado desde GitHub pesa **aprox. 300 KB** en ZIP. **Eso es normal y el proyecto está completo.**

En GitHub solo se sube el **código fuente** (Kotlin, XML, Gradle, recursos). **No** se incluyen:

- Carpeta `build/` (se genera al compilar)
- Carpeta `.gradle/` (caché local)
- Dependencias de Maven/Gradle (se descargan solas al abrir el proyecto)
- APK instalable (se genera con *Build > Build APK*)

El repositorio contiene **131 archivos** y **79 archivos Kotlin** con toda la app funcional.

## Cómo abrir el proyecto

1. Clona o descarga el repositorio:
   ```bash
   git clone https://github.com/Khevinzhito/MyPetShop.git
   ```
2. Abre la carpeta en **Android Studio** (Ladybug o superior).
3. Espera a que Gradle sincronice y descargue dependencias (primera vez puede tardar varios minutos).
4. Ejecuta en emulador o dispositivo físico.

## Tecnologías

- Kotlin · Jetpack Compose · Material 3
- MVVM · Hilt · Room (KSP) · Navigation Compose

## Roles de la aplicación

| Rol | Acceso |
|-----|--------|
| **Comprador** | Tienda, carrito, pedidos, tips de mascota, Advance |
| **Administrador** | CRUD productos, mensajes, reporte de ventas |
| **Repartidor** | Registro y panel de entregas |

## Cuentas de demostración

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Comprador | kbrgvinzhito@gmail.com | 3027899171Aa |
| Admin | admin@mypetshop.com | Admin1234 |

## Estructura del código

Ver [ARQUITECTURA.md](ARQUITECTURA.md) para la explicación detallada de carpetas y capas (UI, Domain, Data).

## Compilar

```bash
./gradlew assembleDebug
```

El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

## Autor

Kevin Zhito — Proyecto académico MyPetShop
