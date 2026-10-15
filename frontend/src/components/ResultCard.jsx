import React, { useState } from 'react';
import {
  Card,
  CardContent,
  CardActions,
  Typography,
  Box,
  Button,
  Chip,
  Divider,
  Link,
  Tooltip,
  Stack,
} from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import CheckIcon from '@mui/icons-material/Check';
import RestartAltIcon from '@mui/icons-material/RestartAlt';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';

export default function ResultCard({ result, onReset, onCopySuccess }) {
  const [copied, setCopied] = useState(false);

  if (!result) return null;

  const { title, summary, url } = result;

  const wordCount = summary ? summary.trim().split(/\s+/).length : 0;
  const estimatedReadSecs = Math.max(15, Math.round((wordCount / 200) * 60));

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(summary);
      setCopied(true);
      if (onCopySuccess) onCopySuccess();
      setTimeout(() => setCopied(false), 2500);
    } catch {
      // Fallback for older browsers
      const textarea = document.createElement('textarea');
      textarea.value = summary;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand('copy');
      document.body.removeChild(textarea);
      setCopied(true);
      if (onCopySuccess) onCopySuccess();
      setTimeout(() => setCopied(false), 2500);
    }
  };

  return (
    <Card
      elevation={3}
      sx={{
        borderRadius: 3,
        border: 1,
        borderColor: 'divider',
        backgroundColor: 'background.paper',
        overflow: 'hidden',
      }}
    >
      <Box
        sx={{
          px: { xs: 2.5, sm: 3.5 },
          py: 2,
          backgroundColor: 'action.hover',
          borderBottom: 1,
          borderColor: 'divider',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 1,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <AutoAwesomeIcon color="primary" fontSize="small" />
          <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'text.primary' }}>
            AI-Generated Summary
          </Typography>
        </Box>

        <Stack direction="row" spacing={1} alignItems="center">
          <Chip
            size="small"
            label={`${wordCount} words`}
            variant="outlined"
            sx={{ fontWeight: 500 }}
          />
          <Chip
            size="small"
            label={`~${estimatedReadSecs}s read`}
            variant="outlined"
            sx={{ fontWeight: 500 }}
          />
        </Stack>
      </Box>

      <CardContent sx={{ p: { xs: 2.5, sm: 3.5 } }}>
        {/* Page Title & Source Link */}
        <Box sx={{ mb: 2.5 }}>
          <Typography
            variant="h5"
            component="h2"
            sx={{
              fontWeight: 700,
              mb: 1,
              lineHeight: 1.3,
              fontSize: { xs: '1.25rem', sm: '1.5rem' },
            }}
          >
            {title || 'Webpage Summary'}
          </Typography>

          {url && (
            <Link
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              color="primary"
              underline="hover"
              sx={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: 0.5,
                fontSize: '0.875rem',
                wordBreak: 'break-all',
              }}
            >
              <span>{url}</span>
              <OpenInNewIcon sx={{ fontSize: '0.95rem' }} />
            </Link>
          )}
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* AI Summary Content */}
        <Box sx={{ py: 1 }}>
          <Typography
            variant="body1"
            sx={{
              lineHeight: 1.8,
              color: 'text.primary',
              fontSize: { xs: '0.975rem', sm: '1.05rem' },
              whiteSpace: 'pre-line',
            }}
          >
            {summary}
          </Typography>
        </Box>
      </CardContent>

      <Divider />

      {/* Action Bar */}
      <CardActions
        sx={{
          px: { xs: 2.5, sm: 3.5 },
          py: 2,
          display: 'flex',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 1.5,
          backgroundColor: 'background.default',
        }}
      >
        <Button
          variant="outlined"
          color="inherit"
          startIcon={<RestartAltIcon />}
          onClick={onReset}
          sx={{
            textTransform: 'none',
            borderRadius: 2,
            fontWeight: 600,
          }}
        >
          Try Another URL
        </Button>

        <Tooltip title={copied ? 'Copied!' : 'Copy summary to clipboard'}>
          <Button
            variant="contained"
            color={copied ? 'success' : 'primary'}
            startIcon={copied ? <CheckIcon /> : <ContentCopyIcon />}
            onClick={handleCopy}
            sx={{
              textTransform: 'none',
              borderRadius: 2,
              fontWeight: 600,
              minWidth: 150,
              boxShadow: copied ? 'none' : '0 2px 8px rgba(25, 118, 210, 0.25)',
            }}
          >
            {copied ? 'Copied!' : 'Copy Summary'}
          </Button>
        </Tooltip>
      </CardActions>
    </Card>
  );
}
