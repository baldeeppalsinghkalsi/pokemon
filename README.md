# Pokémon Search Engine

A local Pokédex-style search app: a Spring Boot API proxies [PokeAPI](https://pokeapi.co/docs/v2) with caching, and a React frontend displays the results.

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Node.js 18+** and npm

## Project structure

```
pokemon/
├── backend/    Spring Boot REST API (port 8080)
└── frontend/   React + TypeScript + Vite (port 5173)
```

## Run locally

### 1. Start the backend

```bash
cd backend
mvn spring-boot:run
```

The API is available at `http://localhost:8080`.

**Example request:**

```bash
curl http://localhost:8080/api/pokemon/pikachu
```

**Build a runnable JAR:**

```bash
cd backend
mvn package -DskipTests
java -jar target/pokemon-search-engine-0.0.1-SNAPSHOT.jar
```

### 2. Start the frontend

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173` in your browser.

In development, the frontend calls the backend at `http://localhost:8080` (CORS is enabled). Alternatively, Vite proxies `/api` to the backend if you point fetches at the dev server origin.

### Production build

```bash
cd frontend
npm run build
```

Serve the `frontend/dist` output behind the same origin as the API, or set `VITE_API_URL` to your backend URL when building:

```bash
VITE_API_URL=https://your-api-host npm run build
```

## API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/pokemon/{name}` | Look up a Pokémon by name (case-insensitive) |

**Success:** `200` with JSON body (`id`, `name`, `types`, `abilities`, `baseStats`, `spriteUrl`, etc.)

**Errors:** JSON body `{ "error": "message" }` with `400`, `404`, `503`, or `500` as appropriate.

## Caching

Upstream PokeAPI responses are cached in-memory (Caffeine): **100 entries max**, **10-minute** expiry after write.
