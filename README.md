# Readao — учёт прочитанных книг

Мобильное приложение для Android на Kotlin и Jetpack Compose. Курсовой проект: «Разработка мобильного приложения для учёта прочитанной литературы на платформе Android (Kotlin)».

## Возможности

- личная библиотека: добавление, редактирование и удаление книг, выбор обложки из галереи;
- статусы «В процессе», «Прочитано», «Отложено»; автоматическая смена статуса при достижении последней страницы, даты начала и окончания чтения;
- статистика: количество прочитанных книг и страниц;
- отзывы и оценки (1–5 звёзд) со средней оценкой;
- общий каталог книг в Cloud Firestore: публикация, добавление книги из каталога, отзывы других пользователей;
- таймер чтения с накоплением времени по каждой книге.

## Технологии

Kotlin 2.0, Jetpack Compose (Material 3), Navigation Compose, ViewModel + StateFlow (MVVM), Room 2.6, Kotlin Coroutines/Flow, Firebase Cloud Firestore, Coil.

minSdk 24 (Android 7.0), targetSdk 35.

## Структура

```
app/src/main/java/com/example/readao/
├── BookTrackerApp.kt      — класс приложения (Firebase, база данных, репозиторий)
├── data/                  — сущности Room, DAO, база данных, репозитории, настройки пользователя
├── viewmodel/             — MainViewModel, CatalogViewModel, TimerViewModel, BookEvent
└── ui/                    — экраны Compose, нижняя навигация, тема
```

## Сборка

1. Открыть проект в Android Studio.
2. Создать проект в [Firebase Console](https://console.firebase.google.com/), добавить Android-приложение с идентификатором `com.example.readao`, скачать `google-services.json` и положить его в папку `app/`. Файл не хранится в репозитории.
3. Включить Cloud Firestore и настроить правила безопасности для коллекций `catalog` (с подколлекцией `ratings`) и `admins`.
4. Запустить конфигурацию `app` на эмуляторе или устройстве, либо собрать APK: `./gradlew assembleDebug`.

Без `google-services.json` проект не соберётся (используется плагин `com.google.gms.google-services`).

## Известные ограничения

Если Firestore отклоняет запрос (например, истёк срок тестовых правил безопасности), вкладка «Каталог» завершает приложение с ошибкой `PERMISSION_DENIED`: ошибка потока каталога пока не обрабатывается.
