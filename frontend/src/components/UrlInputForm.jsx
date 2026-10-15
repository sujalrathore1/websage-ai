import React, { useState } from 'react';
import {
  Paper,
  TextField,
  Button,
  Box,
  InputAdornment,
  IconButton,
  Typography,
  Chip,
  Stack,
} from '@mui/material';
import LinkIcon from '@mui/icons-material/Link';
import ClearIcon from '@mui/icons-material/Clear';
import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import CircularProgress from '@mui/material/CircularProgress';

const SAMPLE_URLS = [
  {
    label: 'Wikipedia: Artificial Intelligence',
    url: 'https://en.wikipedia.org/wiki/Artificial_intelligence',
  },
  {
    label: 'Mozilla MDN: Web Security',
    url: 'https://developer.mozilla.org/en-US/docs/Web/Security',
  },
  {
    label: 'NASA News',
    url: 'https://www.nasa.gov/news-release/',
  },
];

export default function UrlInputForm({ onSubmit, loading, initialUrl = '' }) {
  const [url, setUrl] = useState(initialUrl);
  const [validationError, setValidationError] = useState('');

  const validateUrl = (input) => {
    if (!input || !input.trim()) {
      return 'Please enter a webpage URL.';
    }
    const trimmed = input.trim();
    if (!trimmed.startsWith('http://') && !trimmed.startsWith('https://')) {
      return 'URL must begin with http:// or https://';
    }
    try {
      const parsed = new URL(trimmed);
      if (!parsed.hostname || !parsed.hostname.includes('.')) {
        return 'Please enter a valid website domain.';
      }
    } catch {
      return 'Please enter a valid, well-formed URL.';
    }
    return '';
  };

  const handleSubmit = (e) => {
    if (e) e.preventDefault();
    const error = validateUrl(url);
    if (error) {
      setValidationError(error);
      return;
    }
    setValidationError('');
    onSubmit(url.trim());
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !loading) {
      handleSubmit(e);
    }
  };

  const handleClear = () => {
    setUrl('');
    setValidationError('');
  };

  const handleSampleClick = (sampleUrl) => {
    setUrl(sampleUrl);
    setValidationError('');
  };

  return (
    <Paper
      elevation={2}
      sx={{
        p: { xs: 2.5, sm: 3.5 },
        borderRadius: 3,
        border: 1,
        borderColor: 'divider',
        backgroundColor: 'background.paper',
      }}
    >
      <Typography variant="h6" component="h2" sx={{ fontWeight: 600, mb: 1 }}>
        Summarize Any Webpage
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
        Enter a public article, documentation, or news URL to extract its main content and generate a concise 100–150 word AI summary.
      </Typography>

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
          <TextField
            fullWidth
            id="url-input"
            label="Webpage URL"
            placeholder="Paste a webpage URL here (e.g., https://example.com/article)..."
            value={url}
            onChange={(e) => {
              setUrl(e.target.value);
              if (validationError) setValidationError('');
            }}
            onKeyDown={handleKeyDown}
            disabled={loading}
            error={Boolean(validationError)}
            helperText={validationError}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <LinkIcon color={validationError ? 'error' : 'action'} />
                </InputAdornment>
              ),
              endAdornment: url && !loading && (
                <InputAdornment position="end">
                  <IconButton
                    size="small"
                    onClick={handleClear}
                    edge="end"
                    aria-label="clear url input"
                  >
                    <ClearIcon fontSize="small" />
                  </IconButton>
                </InputAdornment>
              ),
            }}
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
              },
            }}
          />

          <Button
            type="submit"
            variant="contained"
            size="large"
            disabled={loading}
            startIcon={
              loading ? (
                <CircularProgress size={20} color="inherit" />
              ) : (
                <AutoFixHighIcon />
              )
            }
            sx={{
              px: { sm: 4 },
              py: { xs: 1.5, sm: 'auto' },
              borderRadius: 2,
              fontWeight: 600,
              textTransform: 'none',
              fontSize: '1rem',
              minWidth: { sm: 160 },
              boxShadow: '0 4px 14px rgba(25, 118, 210, 0.35)',
            }}
          >
            {loading ? 'Analyzing...' : 'Summarize'}
          </Button>
        </Stack>

        {/* Sample URLs quick-picker */}
        {!loading && (
          <Box sx={{ mt: 2.5, pt: 2, borderTop: 1, borderColor: 'divider' }}>
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{ display: 'block', mb: 1, fontWeight: 500 }}
            >
              Try a sample webpage:
            </Typography>
            <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
              {SAMPLE_URLS.map((sample) => (
                <Chip
                  key={sample.url}
                  label={sample.label}
                  size="small"
                  variant="outlined"
                  onClick={() => handleSampleClick(sample.url)}
                  sx={{
                    cursor: 'pointer',
                    '&:hover': {
                      backgroundColor: 'action.hover',
                      borderColor: 'primary.main',
                    },
                  }}
                />
              ))}
            </Stack>
          </Box>
        )}
      </Box>
    </Paper>
  );
}
