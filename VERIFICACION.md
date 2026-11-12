# Verificación de la entrega

Fecha: 11 de noviembre de 2026 (America/Guatemala).

## Resultado

La aplicación debug compiló y las pruebas unitarias e instrumentadas finalizaron correctamente.

| Pruebas | Ejecutadas | Fallidas | Omitidas |
| --- | --- | --- | --- |
| Unitarias | 2 | 0 | 0 |
| Instrumentadas | 6 | 0 | 0 |

## Entorno y comando

- Windows, JDK Eclipse Adoptium 17.
- Android Gradle Plugin 8.6.1, Gradle 8.7 y Kotlin 1.9.25.
- Emulador independiente CodelabHiltTest, imagen Android API 37.1 x86_64 con páginas de 16 KB.
- `compileSdkVersion 35`, `targetSdkVersion 33`, `minSdkVersion 21`.

```powershell
.\gradlew.bat --no-daemon assembleDebug testDebugUnitTest connectedDebugAndroidTest
```

La variable `ANDROID_SERIAL` se utilizó para seleccionar exclusivamente el emulador de prueba.
Los resultados JUnit se incluyen en `evidencias/`; se retiraron las propiedades del entorno y la salida de consola para evitar publicar rutas personales.

## Cobertura comprobada

- Los registros en memoria aparecen primero los más recientes y se pueden borrar.
- Las instancias independientes no comparten registros y una consulta devuelve una lista estable.
- Un clic en Button 1 aparece en la pantalla de registros.
- El borrado desde la pantalla de botones se refleja en la pantalla de registros.
- La recreación de la Activity produce un logger en memoria nuevo, acorde a `@ActivityScoped`.
- El proveedor recupera de Room todos los registros y un registro por ID mediante el EntryPoint de Hilt.
- El proveedor rechaza escrituras y rutas desconocidas.

## Alcance

La verificación corresponde a debug y al emulador indicado; no se ejecutó una matriz de dispositivos físicos ni una compilación release firmada. Se mantienen avisos del código base sobre APIs obsoletas, sin impedir estas comprobaciones.
La publicación del repositorio público y del enlace en Canvas está pendiente de quien entrega el trabajo.
