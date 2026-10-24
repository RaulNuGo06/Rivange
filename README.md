# Rivange

App Android nativa para gestionar un complejo deportivo: reserva de pistas, partidos abiertos, ranking de jugadores y chat, con datos meteorológicos en tiempo real de una estación real en Zafra (Badajoz).

Proyecto de fin de ciclo de **Desarrollo de Aplicaciones Multiplataforma** (IES Arroyo Harnina, 2026).

<!-- Sustituye por capturas reales: crea la carpeta docs/capturas y deja ahí las imágenes -->
<p align="center">
  <img src="docs/capturas/inicio.png" width="220" alt="Inicio con el tiempo en pista">
  <img src="docs/capturas/reserva.png" width="220" alt="Nueva reserva">
  <img src="docs/capturas/ranking.png" width="220" alt="Ranking">
</p>

## Qué hace

- **Reservas de pistas** de pádel, tenis, fútbol y baloncesto, por fecha y hora, comprobando que la pista no esté ya ocupada.
- **Partidos abiertos**: un jugador crea un partido con nivel y plazas, y otros se apuntan si tienen una reserva activa en esa pista y hora. La plaza se ocupa dentro de una transacción de Firestore para que dos personas no se queden con la última a la vez.
- **Ranking por puntos**: +25 por victoria y −10 por derrota, con categorías de *Novato* a *Olímpico*.
- **Chat** en tiempo real entre jugadores.
- **Tiempo en pista**: temperatura, humedad e índice de calor leídos de la API REST de Home Assistant, conectada a una estación meteorológica real.
- **Tres roles**:
  - **Usuario**: reserva, juega y chatea.
  - **Árbitro**: pone el marcador de los partidos que tiene asignados, y con él se actualiza el ranking.
  - **Administrador**: tiene un panel con las cifras del complejo (partidos, partidos abiertos, chats, árbitros) y puede dar el rol de árbitro a cualquier usuario.

## Tecnologías

| Parte | Tecnología |
|---|---|
| App | Java, Android SDK (minSdk 26, targetSdk 35), Material Components, RecyclerView |
| Autenticación | Firebase Authentication |
| Datos en tiempo real | Cloud Firestore |
| Peticiones HTTP | OkHttp |
| Sensores | API REST de Home Assistant |
| Build | Gradle (Kotlin DSL) |

## Estructura

```
app/src/main/java/com/rivange/app/
├── activities/   11 pantallas: login, registro, inicio, reservas, partidos, ranking, chats, perfil, panel de admin…
├── adapters/     Adaptadores de RecyclerView (reservas, partidos, ranking, chats, mensajes)
├── models/       Usuario, Pista, Reserva, Partido, Mensaje, Sala de chat, Entrada de ranking
├── sensor/       Cliente de Home Assistant, repositorio, cálculo del índice de calor y estado para la UI
└── utils/        Constantes, reglas del ranking, fechas, navegación inferior y datos de prueba para Firestore
```

La parte de sensores se separa en capas pequeñas:

```
SensorApiClient (HTTP) → SensorRepository (parseo y errores) → HeatIndexCalculator → SensorUiMapper → pantalla
```

Así la lógica del índice de calor no depende de Android y se puede probar por separado.

## Decisiones técnicas

- **Firestore en lugar de un backend propio**: los listeners en tiempo real dan el chat y las plazas de los partidos sin montar un servidor, que es lo que pedía el alcance de un proyecto de fin de ciclo.
- **Transacciones para apuntarse a un partido**: evitan que se supere el máximo de jugadores cuando dos personas se apuntan a la vez.
- **Índice de calor con la fórmula de Rothfusz** (la de la NOAA): por debajo de 27 °C se muestra la temperatura sin más, porque la fórmula solo vale a partir de ahí.
- **Desarrollo asistido por IA con reglas explícitas**: el archivo [`AGENTS.md`](AGENTS.md) fija cómo debía trabajar el asistente (cambios mínimos, sin abstracciones innecesarias, código legible).

## Cómo ejecutarlo

1. Clona el repositorio y ábrelo en Android Studio.
2. Crea un proyecto en Firebase con Authentication (email y contraseña) y Firestore, y deja su `google-services.json` en `app/`.
3. Añade a `local.properties` los datos de tu Home Assistant:
   ```properties
   HOME_ASSISTANT_URL=http://tu-servidor:8123
   HOME_ASSISTANT_TOKEN=tu_token_de_larga_duracion
   ```
4. Ejecuta la app en un emulador o en un móvil con Android 8.0 o superior.

## Qué mejoraría

- Pasar de Activities sueltas a **MVVM con ViewModel y LiveData**, para separar la lógica de las pantallas.
- **Tests unitarios** de `RankingRules`, `HeatIndexCalculator` y `SensorRepository`, y tests de UI con Espresso.
- **Reglas de seguridad de Firestore** por rol, para que los permisos no dependan solo de la app.
- Notificaciones push (Firebase Cloud Messaging) cuando alguien se apunta a tu partido o te escribe.

## Autor

**Raúl Núñez González**: [LinkedIn](https://www.linkedin.com/in/raúl-núñez-gonzález-17032a389) · [GitHub](https://github.com/RaulNuGo06)
