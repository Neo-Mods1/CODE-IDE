# CODE-IDE

A professional Android IDE shell application built with native Android XML layouts, Kotlin, and Material 3.

## Features

- Material 3 Dark/Light Theme
- Drawer Navigation with File Tree
- Editor Tab System
- Bottom Panel (Build, Problems, Logs, Terminal, Search)
- Demo Project Auto-Generation
- Smooth Animations
- Performance Optimized

## Tech Stack

- Native Android XML Layouts
- Kotlin
- Material 3
- Android Views (No Jetpack Compose)
- RecyclerViews
- ViewBinding

## Build

```bash
./gradlew assembleDebug
```

## GitHub Actions

This project uses GitHub Actions for CI/CD. The workflow:

1. Builds the debug APK on every push/PR
2. Sends Telegram notifications on success/failure
3. Uploads APK as artifact on successful builds

### Setup

1. Create a new GitHub repository
2. Push this code to the repository
3. Go to Settings > Secrets and variables > Actions
4. Add these secrets:
   - `TELEGRAM_BOT_TOKEN` - Your Telegram bot token
   - `TELEGRAM_CHAT_ID` - Your Telegram chat ID
5. Push code or create a PR to trigger the build

## Project Structure

```
CODE-IDE/
├── app/
│   ├── src/main/
│   │   ├── java/com/neo/ide/
│   │   │   ├── activities/      # Activity classes
│   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   ├── fragments/       # Fragment classes
│   │   │   ├── models/          # Data models
│   │   │   ├── ui/              # Custom UI components
│   │   │   └── utils/           # Utility classes
│   │   └── res/                 # Resources
├── .github/workflows/           # GitHub Actions
└── gradle/                      # Gradle wrapper
```

## License

MIT
