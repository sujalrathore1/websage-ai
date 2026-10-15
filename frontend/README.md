# WebSage AI – Frontend (React + Vite + Material UI)

The frontend client for **WebSage AI**, a web page summarizer powered by Java Spring Boot and Google Gemini.

---

## Tech Stack
- **Framework**: React 19 (via Vite)
- **UI Library**: Material UI (MUI v6) & Emotion
- **HTTP Client**: Axios
- **Icons**: Material UI Icons

---

## Features
- **URL Input & Validation**: Client-side validation ensuring well-formed HTTP/HTTPS URLs.
- **Responsive Layout**: Built with MUI responsive containers for mobile, tablet, and desktop.
- **Loading State**: Clear "Analyzing webpage..." indicator while content is scraped and summarized.
- **Result Presentation**: Professional card displaying page title, source URL, word count, reading time, and AI summary.
- **Copy-to-Clipboard**: One-click summary copying with instant feedback and toast alerts.
- **Reset / Try Another**: Instant reset capability to test other links.
- **Sample URL Picker**: Quick-start sample links (Wikipedia, MDN Docs, NASA News) for immediate testing.
- **Comprehensive Error Handling**: Contextual error alerts for invalid URLs, SSRF restrictions, scraping failures, rate limits, and network issues.

---

## Setup & Running Locally

### 1. Prerequisites
- Node.js 18+ (tested with Node 20 / 24)
- npm 9+

### 2. Environment Configuration
Copy the `.env.example` file to `.env`:
```bash
cp .env.example .env
```
Inside `.env`, configure the backend API endpoint:
```env
VITE_API_BASE_URL=http://localhost:8080
```

### 3. Install Dependencies
```bash
npm install
```

### 4. Start Development Server
```bash
npm run dev
```
The application will start at `http://localhost:5173`.

### 5. Build for Production
```bash
npm run build
```
Build output is generated in the `dist/` directory.

---

## Deployment (Vercel)
To deploy the frontend to Vercel:
1. Connect your GitHub repository to Vercel.
2. Set the **Root Directory** to `frontend`.
3. Set the **Build Command** to `npm run build`.
4. Set the **Output Directory** to `dist`.
5. Add the Environment Variable:
   - `VITE_API_BASE_URL`: The live URL of your deployed Spring Boot backend (e.g. `https://websage-backend.onrender.com`).
