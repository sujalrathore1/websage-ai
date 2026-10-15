import React from 'react';
import {
  Alert,
  AlertTitle,
  Box,
  Button,
  Typography,
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';

export default function ErrorMessage({ error, onRetry }) {
  if (!error) return null;

  const code = error.code || 'ERROR';
  const message = error.message || 'An unexpected error occurred while processing the webpage.';

  let alertTitle = 'Processing Error';
  let hint = 'Please check the webpage URL and try again.';
  let severity = 'error';

  switch (code) {
    case 'INVALID_URL':
      alertTitle = 'Invalid Webpage URL';
      hint = 'Ensure the URL starts with http:// or https:// and points to a valid public website.';
      severity = 'warning';
      break;

    case 'UNSAFE_URL':
      alertTitle = 'Restricted URL (SSRF Protection)';
      hint = 'WebSage AI blocks requests to localhost, loopback IPs, internal network subnets, and cloud metadata services for security.';
      severity = 'warning';
      break;

    case 'SCRAPING_FAILED':
      alertTitle = 'Unable to Scrape Webpage';
      hint = 'The webpage could not be fetched or does not contain sufficient text. Websites requiring authentication, CAPTCHA, or heavy client-side JavaScript rendering cannot be scraped with basic HTML extractors.';
      severity = 'error';
      break;

    case 'RATE_LIMITED':
      alertTitle = 'AI Rate Limit Exceeded';
      hint = 'The Google Gemini API free-tier quota has been temporarily reached. Please wait a moment and try again.';
      severity = 'warning';
      break;

    case 'AI_SERVICE_ERROR':
      alertTitle = 'AI Summarization Error';
      hint = 'The AI service encountered an issue generating the summary. Check that the GEMINI_API_KEY environment variable is configured in the backend.';
      severity = 'error';
      break;

    case 'NETWORK_ERROR':
      alertTitle = 'Backend Unreachable';
      hint = 'Could not connect to the Spring Boot server. Ensure the backend is running locally on port 8080 or check your network connection.';
      severity = 'error';
      break;

    default:
      alertTitle = 'Error';
      hint = 'An unexpected issue occurred. Try again or test with a different URL.';
      severity = 'error';
  }

  return (
    <Alert
      severity={severity}
      sx={{
        borderRadius: 3,
        border: 1,
        borderColor: `${severity}.light`,
        '& .MuiAlert-icon': {
          alignItems: 'center',
        },
      }}
      action={
        onRetry && (
          <Button
            color="inherit"
            size="small"
            startIcon={<RefreshIcon />}
            onClick={onRetry}
            sx={{ fontWeight: 600, textTransform: 'none' }}
          >
            Retry
          </Button>
        )
      }
    >
      <AlertTitle sx={{ fontWeight: 700, mb: 0.5 }}>{alertTitle}</AlertTitle>
      <Typography variant="body2" sx={{ mb: 0.5, fontWeight: 500 }}>
        {message}
      </Typography>
      <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
        {hint}
      </Typography>
    </Alert>
  );
}
