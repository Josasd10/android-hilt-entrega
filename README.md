# Codelab: Hilt en una aplicación Android

Implementación del codelab oficial [Cómo usar Hilt en tu app para Android](https://developer.android.com/codelabs/android-hilt?hl=es-419), a partir de la rama `main` de [android/codelab-android-hilt](https://github.com/android/codelab-android-hilt).
El código base pertenece a The Android Open Source Project y conserva su licencia Apache 2.0.

## Qué se construyó

Una app Kotlin con tres botones que registran interacciones y una pantalla que muestra los registros con fecha y hora. Permite consultar y borrar registros. Se reemplazó la creación manual de dependencias con `ServiceLocator` por Hilt.

La versión final utiliza `@InMemoryLogger` en ambos fragmentos: los registros se comparten dentro de la misma Activity y se pierden al destruirla. La implementación Room sigue disponible como `@DatabaseLogger`, con alcance de aplicación. Un `ContentProvider` consulta los registros persistidos por medio de un `@EntryPoint`.

**Los botones guardan registros en memoria; el proveedor consulta Room.** Por eso el proveedor puede devolver una lista vacía aunque la pantalla muestre registros. Para usar Room desde la pantalla se puede cambiar `@InMemoryLogger` por `@DatabaseLogger` en ambos fragmentos. Las pruebas del proveedor insertan registros directamente mediante el DAO.

## Pasos implementados

| Tema del codelab | Implementación |
| --- | --- |
| Inicialización | `LogApplication` con `@HiltAndroidApp` |
| Inyección en Android | Activity y fragmentos con `@AndroidEntryPoint` y campos `@Inject` |
| Inyección por constructor | `DateFormatter`, fuentes de datos y `AppNavigatorImpl` |
| Proveedores | `DatabaseModule`: Room y `LogDao` mediante `@Provides` |
| Interfaces | `NavigationModule` y módulos de logging con `@Binds` |
| Alcances | Room/logger persistente con `@Singleton`; logger en memoria con `@ActivityScoped` |
| Calificadores | `@DatabaseLogger` y `@InMemoryLogger` distinguen implementaciones |
| Retiro del localizador | Se elimina `ServiceLocator` y sus referencias |
| Pruebas Hilt | `CustomTestRunner`, `HiltTestApplication`, `@HiltAndroidTest`, `HiltAndroidRule` |
| Acceso desde clases no compatibles | `LogsContentProvider` obtiene `LogDao` mediante `@EntryPoint` |

## Aprendizajes que demuestra el proyecto

- Una dependencia es un objeto que otra clase necesita. `@Inject constructor` permite que Hilt construya la clase y resuelva sus dependencias.
- `@Provides` sirve para objetos que requieren una fábrica, como Room. `@Binds` asocia una interfaz con su implementación.
- `@InstallIn` decide qué componente conoce una vinculación. Un componente de Activity puede acceder a las dependencias de aplicación; el componente de aplicación no puede acceder a dependencias exclusivas de Activity.
- El alcance determina cuándo se reutiliza una instancia. `@ActivityScoped` no conserva datos al recrear la Activity; `@Singleton` dura dentro del proceso de la aplicación.
- Los calificadores distinguen varias vinculaciones de la misma interfaz.
- `@EntryPoint` permite obtener dependencias desde una clase que no admite `@AndroidEntryPoint`, como un `ContentProvider`.
- Las pruebas Hilt generan sus propios componentes. La prueba del proveedor usa inyección explícita mediante `hiltRule.inject()`.

## Requisitos y apertura

- JDK **17** recomendado para ejecutar Gradle; es la versión utilizada para verificar la entrega.
- Android SDK Platform **35** y conexión a Internet para descargar dependencias.
- Dispositivo o emulador Android API **21** o superior.

1. Extraer el proyecto fuera de OneDrive, por ejemplo en `C:\ViewModel\android-hilt-codelab`.
2. En Android Studio, elegir **File > Open** y seleccionar esta carpeta, que contiene `settings.gradle`.
3. Abrir **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
4. En **Gradle JDK**, elegir un JDK 17. Si falta, usar **Download JDK** y seleccionar 17.
5. Pulsar **Apply > OK** y **Sync Project with Gradle Files**.
6. Seleccionar `app`, elegir un dispositivo y pulsar Run.

Se actualizan las herramientas del proyecto base para compilar la migración y ejecutar las pruebas en Android reciente:

| Herramienta | Versión |
| --- | --- |
| Gradle | 8.7 |
| Android Gradle Plugin | 8.6.1 |
| Kotlin | 1.9.25 |
| Hilt | 2.52 |
| Room | 2.6.1 |
| AndroidX Test Core / Rules | 1.7.0 |
| AndroidX Test JUnit | 1.3.0 |
| Espresso | 3.7.0 |

El Hilt original producía `Unsupported metadata version` después de la migración. El Espresso original intentaba acceder por reflexión a `InputManager.getInstance`, una API que no existe en el emulador Android usado para verificar esta entrega. Espresso 3.7.0 corrige ese acceso y requiere API 21 como mínimo; las versiones de Kotlin, Hilt y Room se mantienen compatibles entre sí. Se conserva `targetSdkVersion 33` del ejercicio; `compileSdkVersion` es 35.

Se utiliza `SingletonComponent`, equivalente al antiguo `ApplicationComponent` mostrado en la traducción del codelab. El `namespace` se declara en `app/build.gradle`.

Referencias: [compatibilidad AGP 8.6](https://developer.android.com/build/releases/agp-8-6-0-release-notes), [notas AndroidX Test](https://developer.android.com/jetpack/androidx/releases/test).

## Compilar y probar

Con `JAVA_HOME` apuntando al JDK 17 y `ANDROID_HOME` al SDK (o un `local.properties` generado por Android Studio):

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest assembleDebugAndroidTest
```

Para ejecutar las pruebas instrumentadas con un emulador iniciado o teléfono conectado:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

- Pruebas unitarias: orden de registros, borrado, independencia de instancias y estabilidad de una lista consultada.
- Pruebas instrumentadas: pulsar un botón y mostrar su registro; borrar registros compartidos entre pantallas; perder los registros al recrear la Activity; consultas Room a través del proveedor; rechazo de escritura y rutas desconocidas.
- Los resultados reales de verificación se documentan en `VERIFICACION.md`.

## Proveedor de contenido

URI de colección: `content://com.example.android.hilt.provider/logs`.
URI individual: `content://com.example.android.hilt.provider/logs/ID`.

El proveedor se registra en el manifiesto y solo permite lectura. Para consultar desde otra app, esta debe declarar el permiso `com.example.android.hilt.permission.READ_LOGS` y estar firmada con la misma clave, porque el permiso es de nivel `signature`. Implementa las dos consultas del ejercicio; no aplica filtros o proyecciones personalizados.

Respecto al ejemplo, se registra el proveedor, se usa `logs/#` para IDs numéricos y se devuelve una copia de la lista en memoria para mantener estables los resultados de una consulta.

## Comprobación manual

1. Pulsar Button 1, Button 2 y Button 3.
2. Abrir See All Logs: deben aparecer los tres registros con fecha, primero el más reciente.
3. Volver con Atrás, pulsar Delete Logs y abrir See All Logs: la lista debe estar vacía.
4. Agregar otro registro, forzar la detención de la app y abrirla otra vez: la lista debe estar vacía porque usa memoria.

## Entrega en GitHub y Canvas

El ZIP incluye un repositorio Git local con commits descriptivos y sin remoto configurado. Conservar la carpeta oculta `.git` al extraerlo para mantener el historial. Los commits de preparación están atribuidos a Codex; el código original conserva su atribución a The Android Open Source Project.

Crear un repositorio **público y vacío** en GitHub, sin README, licencia ni `.gitignore` iniciales. Desde la carpeta extraída ejecutar, reemplazando `TU_USUARIO`:

```powershell
git remote add origin https://github.com/TU_USUARIO/android-hilt-codelab.git
git push -u origin main
```

No hace falta ejecutar `git init`: el historial ya está preparado. Para comprobarlo: `git log --oneline`.
Si se incorpora el código a otro repositorio local ya existente, conservar su `.git` en vez de sobrescribirlo y revisar `git status` y `git remote -v` antes de añadir cambios.
Conservar `LICENSE` y las atribuciones. `.gitignore` excluye `.gradle`, `.idea`, `local.properties` y carpetas `build`.
Publicar en Canvas el enlace real al repositorio y comprobar que abra sin iniciar sesión.
La publicación en GitHub y la entrega en Canvas quedan a cargo de quien entrega el proyecto.
