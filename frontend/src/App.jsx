import React, { useState } from 'react';
import {
  ThemeProvider,
  createTheme,
  CssBaseline,
  Box,
  Container,
  Snackbar,
  Alert,
  Stack,
} from '@mui/material';
import Header from './components/Header';
import UrlInputForm from './components/UrlInputForm';
import LoadingIndicator from './components/LoadingIndicator';
import ResultCard from './components/ResultCard';
import ErrorMessage from './components/ErrorMessage';
import EmptyState from './components/EmptyState';
import Footer from './components/Footer';
import { summarizeWebpage } from './services/api';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#2563eb', // Modern vibrant blue
      light: '#60a5fa',
      dark: '#1d4ed8',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#0d9488', // Teal accent
    },
    background: {
      default: '#f8fafc', // Soft slate background
      paper: '#ffffff',
    },
    text: {
      primary: '#0f172a',
      secondary: '#475569',
    },
    divider: '#e2e8f0',
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    h5: {
      fontWeight: 700,
    },
    h6: {
      fontWeight: 600,
    },
    button: {
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
  },
});

export default function App() {
  const [loading, setLoading] = useState(false);
  const [currentUrl, setCurrentUrl] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const handleSubmit = async (url) => {
    setCurrentUrl(url);
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const data = await summarizeWebpage(url);
      setResult(data);
    } catch (err) {
      setError({
        code: err.code || 'UNKNOWN_ERROR',
        message: err.message || 'Failed to summarize the webpage.',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setError(null);
    setLoading(false);
  };

  const handleRetry = () => {
    if (currentUrl) {
      handleSubmit(currentUrl);
    }
  };

  const handleCopySuccess = () => {
    setSnackbarOpen(true);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          backgroundColor: 'background.default',
        }}
      >
        <Header />

        <Container
          component="main"
          maxWidth="md"
          sx={{
            flex: 1,
            py: { xs: 3, sm: 5 },
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Stack spacing={3}>
            {/* URL Input Form */}
            <UrlInputForm
              onSubmit={handleSubmit}
              loading={loading}
              initialUrl={currentUrl}
            />

            {/* Loading State */}
            {loading && <LoadingIndicator targetUrl={currentUrl} />}

            {/* Error Message */}
            {!loading && error && (
              <ErrorMessage error={error} onRetry={handleRetry} />
            )}

            {/* Result Display */}
            {!loading && result && (
              <ResultCard
                result={result}
                onReset={handleReset}
                onCopySuccess={handleCopySuccess}
              />
            )}

            {/* Empty State (when no result, error, or loading) */}
            {!loading && !result && !error && <EmptyState />}
          </Stack>
        </Container>

        <Footer />

        {/* Copy Notification Toast */}
        <Snackbar
          open={snackbarOpen}
          autoHideDuration={3000}
          onClose={() => setSnackbarOpen(false)}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        >
          <Alert
            onClose={() => setSnackbarOpen(false)}
            severity="success"
            variant="filled"
            sx={{ width: '100%', borderRadius: 2 }}
          >
            Summary copied to clipboard!
          </Alert>
        </Snackbar>
      </Box>
    </ThemeProvider>
  );
}
