# Codelab: Hilt en Android

Proyecto basado en el codelab oficial [Cómo usar Hilt en tu app para Android](https://developer.android.com/codelabs/android-hilt?hl=es-419).

## ¿Qué se construyó?

Una aplicación en Kotlin que registra las interacciones con tres botones y permite consultar y borrar los registros.

Se reemplazó la creación manual de dependencias con Hilt. También se implementaron una fuente de datos en memoria, una base de datos Room y un ContentProvider para consultar los datos persistidos.

## ¿Qué se aprendió?

- Inyectar dependencias con `@Inject`.
- Configurar Hilt en la aplicación, actividades y fragmentos.
- Proporcionar dependencias con `@Provides` y `@Binds`.
- Usar alcances y calificadores para controlar las instancias.
- Acceder a dependencias mediante `@EntryPoint`.
- Probar una aplicación que utiliza Hilt.

## Ejecución

Abrir el proyecto en Android Studio, sincronizar Gradle y ejecutar el módulo `app`. Se recomienda JDK 17.

## Verificación

El proyecto compila correctamente y se aprobaron 8 pruebas automatizadas.

## Créditos

Código base de The Android Open Source Project, bajo licencia Apache 2.0.
