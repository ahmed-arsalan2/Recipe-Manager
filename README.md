# RecipeManager

RecipeManager is an Android recipe management application built with Java and XML. It provides authenticated, user-specific recipe storage, complete recipe CRUD operations, search and category filtering, favorites, and AI-generated recipe descriptions.

## Features

- User registration and login with Firebase Authentication
- User-specific recipe storage with Firebase Realtime Database
- Create, read, update, and delete recipes
- Search recipes by title or category
- Filter recipes by category
- Filter favorite recipes
- Mark and unmark recipes as favorites
- Detailed recipe view
- AI-generated recipe descriptions using Firebase AI Logic and Gemini
- Editorial-style food magazine interface
- Input validation and user-facing error handling
- Empty-state handling
- Logout functionality

## Tech Stack

- **Language:** Java
- **UI:** XML layouts
- **IDE:** Android Studio
- **Authentication:** Firebase Authentication
- **Database:** Firebase Realtime Database
- **Generative AI:** Firebase AI Logic with Gemini
- **Architecture:** Activity-based Android application with repository classes for Firebase and AI operations
- **Build system:** Gradle

## Architecture

The application separates Firebase and AI operations into repository classes while Activities handle the application UI and user interaction.

```text
                         ┌─────────────────────┐
                         │   Firebase Auth     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Authenticated User  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                  ┌────────────────────────────────┐
                  │ RealtimeDatabaseRepository     │
                  └───────────────┬────────────────┘
                                  │
                                  ▼
                       users/{uid}/recipes
                                  │
              ┌───────────────────┼──────────────────┐
              │                   │                  │
              ▼                   ▼                  ▼
    RecipeListActivity    AddRecipeActivity   EditRecipeActivity
              │
              ▼
       RecipeAdapter
              │
              ▼
    RecipeDetailActivity
              │
              ▼
       GeminiRepository
              │
              ▼
        Gemini AI
```

## Data Model

Each recipe contains:

```text
Recipe
├── id
├── title
├── category
├── ingredients
├── instructions
├── favorite
└── createdAt
```

Recipes are stored under the authenticated user's UID:

```text
users/
└── {uid}/
    └── recipes/
        └── {recipeId}/
            ├── id
            ├── title
            ├── category
            ├── ingredients
            ├── instructions
            ├── favorite
            └── createdAt
```

This structure keeps each user's recipes isolated from other users at the database-rule level.

## AI Integration

RecipeManager uses Firebase AI Logic to generate concise recipe descriptions from the recipe's:

- Title
- Category
- Ingredients
- Instructions

The application requests a short, natural description and instructs the model not to invent ingredients.

AI generation is performed asynchronously so that the Android UI remains responsive.

## Security

The project does **not** commit `google-services.json` to Git.

The file is intentionally ignored through `.gitignore`:

```text
app/google-services.json
```

Firebase Authentication and Realtime Database security rules are used to restrict recipe data to authenticated users and their corresponding database paths.

When setting up the project locally, you must provide your own Firebase configuration.

## Getting Started

### Requirements

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- A Firebase project
- An Android application registered in Firebase

### Firebase Setup

1. Create a Firebase project.
2. Enable Firebase Authentication.
3. Enable the Realtime Database.
4. Configure the database security rules.
5. Register the Android application with your Firebase project.
6. Download `google-services.json`.
7. Place it at:

```text
app/google-services.json
```

8. Build and run the application from Android Studio.

### Build from Terminal

Clone the repository:

```bash
git clone https://github.com/ahmed-arsalan2/Recipe-Manager.git
cd Recipe-Manager
```

Place your Firebase configuration file at:

```text
app/google-services.json
```

Then build:

```bash
./gradlew assembleDebug
```

## Project Structure

```text
RecipeManager/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/recipe/
│           │       ├── AddRecipeActivity.java
│           │       ├── EditRecipeActivity.java
│           │       ├── GeminiRepository.java
│           │       ├── LoginActivity.java
│           │       ├── RealtimeDatabaseRepository.java
│           │       ├── Recipe.java
│           │       ├── RecipeAdapter.java
│           │       ├── RecipeApplication.java
│           │       ├── RecipeDetailActivity.java
│           │       ├── RecipeListActivity.java
│           │       └── RegisterActivity.java
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap-*/
│               ├── values/
│               └── xml/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

## Screens

The application includes:

- Login
- Registration
- Recipe list
- Add recipe
- Recipe details
- Edit recipe

The recipe list provides search, category filtering, favorites, and an editorial-style presentation.

## Development Resources

### AI-Assisted Development

AI tools were used during the development of RecipeManager as a development aid.

They were used for:

<<<<<<< HEAD
* Understanding and troubleshooting Android/Java concepts
* Debugging implementation issues
* Reviewing architecture and data flow
* Identifying potential integration and security issues
* Refining UI/UX implementation
* Assisting with Firebase and Gemini integration
* Reviewing and improving project documentation
=======
- Understanding and troubleshooting Android/Java concepts
- Debugging implementation issues
- Reviewing architecture and data flow
- Identifying potential integration and security issues
- Refining UI/UX implementation
- Assisting with Firebase and Gemini integration
- Reviewing and improving project documentation
>>>>>>> e4c035e (Finalised)

The submitted application was implemented, tested, and reviewed by the developer. The developer understands the submitted code and can explain the implementation and architectural decisions.

### AI Used Within the Application

<<<<<<< HEAD
RecipeManager also uses **Firebase AI Logic with Gemini** as an application feature. Gemini is used to generate concise recipe descriptions from recipe information such as the title, category, ingredients, and instructions.

=======
RecipeManager also uses **Firebase AI Logic with Gemini** as an application feature.

Gemini is used to generate concise recipe descriptions from recipe information such as:

- Title
- Category
- Ingredients
- Instructions
>>>>>>> e4c035e (Finalised)

## Current Status

RecipeManager currently includes the core application functionality, Firebase integration, AI integration, and the final editorial UI.

The project is being prepared as a competition-ready Android application, with the remaining work focused on final deployment, documentation, resource declaration, and demonstration.

## License

This project is currently intended as an educational and competition project.
