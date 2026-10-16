# WebSage AI – Full-Stack Web Page Summarizer

[![Java](https://img.shields.io/badge/Java-21%20%7C%2017-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![Material UI](https://img.shields.io/badge/Material--UI-v6-007FFF?logo=mui&logoColor=white)](https://mui.com/)
[![Google Gemini](https://img.shields.io/badge/Google%20Gemini-1.5%20Flash-4285F4?logo=google&logoColor=white)](https://aistudio.google.com/)

**WebSage AI** is a complete, production-quality full-stack web application that takes any public webpage URL, safely extracts its meaningful text content using **Jsoup**, and generates an objective 100–150 word summary using the **Google Gemini API** (free tier).

---

## 🌐 Live Deployments & Demo

| Service | Platform | Live URL | Status |
| :--- | :--- | :--- | :--- |
| **Frontend Application** | Vercel | [https://websage-ai-delta.vercel.app](https://websage-ai-delta.vercel.app) | ![Vercel](https://img.shields.io/badge/Vercel-Live-brightgreen?logo=vercel) |
| **Backend REST API** | Render (Docker) | [https://websage-backend-e3ak.onrender.com](https://websage-backend-e3ak.onrender.com) | ![Render](https://img.shields.io/badge/Render-Live-brightgreen?logo=render) |
| **API Health Check** | Render | [https://websage-backend-e3ak.onrender.com/api/health](https://websage-backend-e3ak.onrender.com/api/health) | ![Health](https://img.shields.io/badge/Health-UP-blue) |
| **GitHub Repository** | GitHub | [https://github.com/sujalrathore1/websage-ai](https://github.com/sujalrathore1/websage-ai) | ![Repo](https://img.shields.io/badge/GitHub-Public-informational?logo=github) |

---

## Table of Contents
- [Live Deployments & Demo](#-live-deployments--demo)
- [Architecture & Request Flow](#architecture--request-flow)
- [Technologies Used](#technologies-used)
- [Key Features](#key-features)
- [Security & SSRF Protection](#security--ssrf-protection)
- [Prerequisites](#prerequisites)
- [How to Obtain a Gemini API Key](#how-to-obtain-a-gemini-api-key)
- [Local Development Setup](#local-development-setup)
  - [Backend Setup (Spring Boot)](#1-backend-setup-spring-boot)
  - [Frontend Setup (React + Vite)](#2-frontend-setup-react--vite)
- [API Reference](#api-reference)
- [Running Tests](#running-tests)
- [GitHub Setup & Push Commands](#github-setup--push-commands)
- [Deployment Guide](#deployment-guide)
  - [Backend Deployment (Render)](#backend-deployment-render)
  - [Frontend Deployment (Vercel)](#frontend-deployment-vercel)
- [Troubleshooting](#troubleshooting)
- [Known Limitations & Trade-offs](#known-limitations--trade-offs)

---

## Architecture & Request Flow

```
+-------------------------------------------------------------+
|                     React Frontend (Vite)                   |
|  - Material UI v6 interface                                 |
|  - Client-side URL validation & state management            |
|  - Axios HTTP Client                                        |
+------------------------------+------------------------------+
                               |
                   POST /api/summarize { url }
                               |
                               v
+-------------------------------------------------------------+
|                Spring Boot 3.x Backend                      |
|                                                             |
|  1. SummarizeController                                     |
|     - Validates incoming URL syntax (@Valid)                |
|                                                             |
|  2. WebScraperService                                       |
|     - SSRF DNS & IP Validation (Blocks localhost, RFC 1918) |
|     - Safe redirect validation (Max 3 hops)                 |
|     - Jsoup HTML fetch & DOM sanitization                   |
|     - Strips scripts, ads, nav, footer, headers             |
|     - Extracts article/main content                         |
|                                                             |
|  3. GeminiService                                           |
|     - RestClient communicates with Google Gemini API        |
|     - Prompt injection defenses with delimiter isolation    |
|     - Parses model JSON & handles safety/quota errors       |
|                                                             |
|  4. GlobalExceptionHandler                                  |
|     - Standardized JSON responses for all error types       |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|            Google Gemini API (gemini-1.5-flash)             |
|     - Free tier Generative Language REST API                |
+-------------------------------------------------------------+
```

---

## Technologies Used

### Frontend
- **React 19** with **Vite 8**: Blazing-fast modern JavaScript single-page application.
- **Material UI (MUI v6)** & **Emotion**: Professional, clean design system with responsive layouts.
- **Axios**: HTTP client with configurable base URLs and centralized error translation.
- **Material Icons**: Clean, intuitive visual cues.

### Backend
- **Java 21 / 17**: Compatible with Java 17 LTS and Java 21 LTS.
- **Spring Boot 3.3.4**: Modern microservice framework.
- **Spring Web & RestClient**: High-performance HTTP client for upstream AI calls.
- **Jsoup 1.18.3**: Resilient HTML parser and DOM cleaner.
- **Jakarta Validation**: Robust validation of incoming requests.
- **JUnit 5, Mockito & AssertJ**: Comprehensive unit and MockMvc test coverage.

### AI Integration
- **Google Gemini API**: Configurable model (default `gemini-1.5-flash`) via the free-tier Google AI Studio.

---

## Key Features

1. **Clean, Modern UI**: Application title `"WebSage AI – Web Page Summarizer"` with clean typography, clear empty states, and quick-fill sample URLs.
2. **Instant Copy & Reset**: Copy summary to clipboard with toast notification, and 1-click reset to analyze another URL.
3. **Comprehensive Content Cleaning**: Jsoup removes ads, scripts, navigation, footers, headers, cookie notices, and tracking embeds to ensure only relevant content reaches the LLM.
4. **Token Control**: Extracted text is safely truncated (max 12,000 characters) to avoid overwhelming model context limits.
5. **SSRF Guard**: Strict validation of schemes, hostnames, and resolved IP addresses against loopback, private ranges, link-local, and cloud metadata endpoints.
6. **Prompt Injection Defense**: Scraped text is encapsulated between strict boundaries with explicit system instructions to ignore embedded instructions.
7. **No Exposed Secrets**: API keys are strictly read from server environment variables and never sent to the client.

---

## Security & SSRF Protection

Allowing arbitrary web scraping introduces Server-Side Request Forgery (SSRF) vulnerabilities. WebSage AI enforces multi-layered defenses:

- **Protocol Restriction**: Only `http://` and `https://` schemes are allowed (`file://`, `ftp://`, `gopher://`, `ldap://` are immediately rejected).
- **DNS Resolution Check**: Hostnames are resolved into `InetAddress` lists before connecting.
- **IP Range Filtering**:
  - `127.0.0.0/8` & `::1` (Loopback)
  - `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16` (Private RFC 1918 networks)
  - `169.254.0.0/16` (Link-local & AWS/GCP/Azure instance metadata `169.254.169.254`)
  - `100.64.0.0/10` (Carrier-Grade NAT)
  - `0.0.0.0/8` & multicast ranges
- **Redirect Re-validation**: HTTP redirects (301, 302, 307, 308) are not followed blindly; each redirect destination is fully re-evaluated against the SSRF filter up to a maximum of 3 hops.

---

## Prerequisites

- **Java Development Kit (JDK)**: Java 17 or Java 21
  - Check with: `java -version`
- **Apache Maven**: 3.8+ or 3.9+
  - Check with: `mvn -v`
- **Node.js**: 18+ or 20+
  - Check with: `node -v`
- **npm**: 9+ or 10+
  - Check with: `npm -v`
- A free **Google Gemini API Key** from [Google AI Studio](https://aistudio.google.com/)

---

## How to Obtain a Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/).
2. Sign in with your Google account.
3. Click the **"Get API key"** button in the sidebar or top banner.
4. Click **"Create API key"** (select a free project or create one).
5. Copy the generated key. (Free tier includes 15 requests per minute, which is more than enough for testing and demonstration).

---

## Local Development Setup

### 1. Backend Setup (Spring Boot)

#### Important Note on `.env` in Spring Boot:
> **Why Spring Boot doesn't read `.env` by default**: Standard Spring Boot uses `application.yml` or `application.properties` and reads environment variables injected into the process environment (e.g. via terminal export, Docker, or system environment variables).

#### Option A: Set Environment Variables in Terminal (Recommended)

**On Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your_actual_gemini_api_key_here"
$env:GEMINI_MODEL="gemini-1.5-flash"
$env:SERVER_PORT="8080"
$env:FRONTEND_ORIGIN="http://localhost:5173"
cd backend
mvn spring-boot:run
```

**On Linux / macOS (Bash / Zsh):**
```bash
export GEMINI_API_KEY="your_actual_gemini_api_key_here"
export GEMINI_MODEL="gemini-1.5-flash"
export SERVER_PORT="8080"
export FRONTEND_ORIGIN="http://localhost:5173"
cd backend
mvn spring-boot:run
```

#### Option B: Run with Maven Properties
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments="--app.gemini.api-key=your_gemini_key"
```

The backend server will start on `http://localhost:8080`.
You can verify the backend is running by opening: `http://localhost:8080/api/health`

---

### 2. Frontend Setup (React + Vite)

1. Open a new terminal and navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```

2. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
   *(On Windows PowerShell: `Copy-Item .env.example .env`)*

3. Verify the `.env` contents:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

4. Install frontend dependencies:
   ```bash
   npm install
   ```

5. Start the Vite development server:
   ```bash
   npm run dev
   ```

6. Open your browser and navigate to:
   **`http://localhost:5173`**

---

## API Reference

### 1. Health Check
- **Endpoint**: `GET /api/health`
- **Response**:
```json
{
  "status": "UP",
  "service": "WebSage AI Backend",
  "timestamp": 1773619420000
}
```

### 2. Summarize Webpage
- **Endpoint**: `POST /api/summarize`
- **Headers**: `Content-Type: application/json`

**Sample Request**:
```json
{
  "url": "https://en.wikipedia.org/wiki/Artificial_intelligence"
}
```

**Sample 200 OK Response**:
```json
{
  "url": "https://en.wikipedia.org/wiki/Artificial_intelligence",
  "title": "Artificial intelligence - Wikipedia",
  "summary": "Artificial intelligence (AI) is the intelligence of machines or software, distinct from the intelligence of living beings. Major AI research goals include reasoning, knowledge representation, planning, learning, natural language processing, and perception. The field was founded as an academic discipline in 1956 and has experienced cycles of optimism followed by periods of disillusionment and loss of funding. In recent years, advances in deep learning and large language models have driven widespread practical adoption across numerous disciplines and commercial applications."
}
```

**Sample Error Responses**:

- **Invalid URL (HTTP 400)**:
```json
{
  "error": "INVALID_URL",
  "message": "URL must start with http:// or https:// and have a valid host format.",
  "timestamp": "2026-10-15T17:30:00Z"
}
```

- **SSRF Blocked (HTTP 400)**:
```json
{
  "error": "UNSAFE_URL",
  "message": "Access to local or internal host 'localhost' is blocked.",
  "timestamp": "2026-10-15T17:30:00Z"
}
```

- **Scraping Failure (HTTP 422)**:
```json
{
  "error": "SCRAPING_FAILED",
  "message": "The webpage does not contain sufficient readable text to generate a summary. The site may require JavaScript rendering or user authentication.",
  "timestamp": "2026-10-15T17:30:00Z"
}
```

---

## Running Tests

### Backend Unit & Integration Tests
The backend test suite covers URL validation, SSRF filters, Jsoup error handling, MockMvc controller endpoints, and AI error propagation.

Run all 23 tests with:
```bash
cd backend
mvn test
```

Expected output:
```
[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Frontend Build Test
Verify that the React production bundle compiles cleanly:
```bash
cd frontend
npm run build
```

---

## GitHub Setup & Push Commands

To push this repository to your public GitHub account:

1. Create a **new public repository** on GitHub (e.g. `websage-ai`). Do not initialize it with a README or .gitignore (they are already prepared).
2. Run the following commands from the project root (`fullstack`):

```bash
# 1. Initialize git (if not already initialized)
git init

# 2. Add all files (secrets and build artifacts are ignored by .gitignore)
git add .

# 3. Create initial commit
git commit -m "feat: complete production-ready WebSage AI full-stack application"

# 4. Set default branch to main
git branch -M main

# 5. Link your GitHub remote repository
git remote add origin https://github.com/sujalrathore1/websage-ai.git

# 6. Push to GitHub
git push -u origin main
```

---

## Deployment Guide

### Backend Deployment (Render)

Render supports containerized Spring Boot applications via Docker:

1. Sign in to [Render](https://render.com/).
2. Click **"New +"** -> **"Web Service"**.
3. Connect your GitHub repository (`sujalrathore1/websage-ai`).
4. Select the repository and choose:
   - **Environment**: `Docker`
   - **Root Directory**: `backend`
   - **DockerfilePath**: `Dockerfile`
5. Under **Environment Variables**, add:
   - `GEMINI_API_KEY`: *(Your Google AI Studio API key)*
   - `GEMINI_MODEL`: `gemini-3.5-flash-lite`
   - `FRONTEND_ORIGIN`: `https://websage-ai-delta.vercel.app`
   - `PORT`: `8080`
6. Click **"Deploy Web Service"**.
7. Live Backend URL: [https://websage-backend-e3ak.onrender.com](https://websage-backend-e3ak.onrender.com)
   Verify health by visiting: [https://websage-backend-e3ak.onrender.com/api/health](https://websage-backend-e3ak.onrender.com/api/health).

---

### Frontend Deployment (Vercel)

1. Sign in to [Vercel](https://vercel.com/).
2. Click **"Add New..."** -> **"Project"**.
3. Import your GitHub repository (`sujalrathore1/websage-ai`).
4. Configure the project:
   - **Framework Preset**: `Vite`
   - **Root Directory**: Click "Edit" and select `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
5. Under **Environment Variables**, add:
   - `VITE_API_BASE_URL`: `https://websage-backend-e3ak.onrender.com`
6. Click **"Deploy"**.
7. Live Frontend URL: [https://websage-ai-delta.vercel.app](https://websage-ai-delta.vercel.app)
8. Update the backend's `FRONTEND_ORIGIN` variable on Render with `https://websage-ai-delta.vercel.app` to ensure CORS permits requests.

---

## Troubleshooting

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| **"GEMINI_API_KEY is not configured"** | Backend environment variable is missing | Set `GEMINI_API_KEY` in your terminal or deployment dashboard. |
| **"Network Error / Backend Unreachable"** | Backend is not running or CORS is misconfigured | Ensure Spring Boot is running on port 8080 and `FRONTEND_ORIGIN` allows `http://localhost:5173`. |
| **"Unable to scrape content / 422"** | Target site requires JS execution or Cloudflare bot check | Test with another static HTML website like Wikipedia or Mozilla MDN. |
| **"Access to local or internal host is blocked"** | SSRF protection intercepted localhost or private IP | This is intended security behavior. Only public URLs can be summarized. |
| **"Rate limit exceeded / 429"** | Gemini free tier 15 RPM limit reached | Wait 30 seconds and retry. |

---

## Known Limitations & Trade-offs

1. **Static HTML vs. Client-Side Rendering**: Jsoup fetches and parses server-rendered HTML. Single-page applications (SPAs) that render 100% of their text dynamically via client-side JavaScript without SSR may yield minimal text. (This matches the project specification to avoid heavy browser automation like Puppeteer/Playwright).
2. **Bot Protections & CAPTCHAs**: Websites protected by Cloudflare Turnstile, Akamai, or CAPTCHA walls will reject standard HTTP fetch requests with 403 or 429 status codes. WebSage AI gracefully catches and reports these scenarios to the user.
3. **Paywalled Content**: Subscription or login-protected articles cannot be fetched without credentials.
4. **Context Length Truncation**: To prevent exceeding Gemini context windows and incurring unnecessary latency, webpages longer than 12,000 characters are safely trimmed before prompt dispatch.

---

## License
MIT License. Created for the WebSage AI Technical Assessment.
