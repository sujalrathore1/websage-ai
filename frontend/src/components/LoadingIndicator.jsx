import React from 'react';
import {
  Card,
  CardContent,
  Box,
  Typography,
  CircularProgress,
  LinearProgress,
} from '@mui/material';
import LanguageIcon from '@mui/icons-material/Language';
import PsychologyIcon from '@mui/icons-material/Psychology';

export default function LoadingIndicator({ targetUrl }) {
  return (
    <Card
      elevation={2}
      sx={{
        borderRadius: 3,
        border: 1,
        borderColor: 'primary.light',
        backgroundColor: 'background.paper',
        overflow: 'hidden',
      }}
    >
      <LinearProgress color="primary" />
      <CardContent sx={{ p: { xs: 3, sm: 4 }, textAlign: 'center' }}>
        <Box
          sx={{
            display: 'inline-flex',
            position: 'relative',
            mb: 2.5,
          }}
        >
          <CircularProgress size={56} thickness={4} color="primary" />
          <Box
            sx={{
              top: 0,
              left: 0,
              bottom: 0,
              right: 0,
              position: 'absolute',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <PsychologyIcon color="primary" fontSize="small" />
          </Box>
        </Box>

        <Typography variant="h6" component="h3" sx={{ fontWeight: 600, mb: 1 }}>
          Analyzing webpage...
        </Typography>

        <Typography variant="body2" color="text.secondary" sx={{ maxWidth: 480, mx: 'auto', mb: 2 }}>
          WebSage AI is securely fetching the HTML content, extracting readable text with Jsoup, and synthesizing a 100–150 word summary with Google Gemini.
        </Typography>

        {targetUrl && (
          <Box
            sx={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 0.75,
              px: 2,
              py: 0.75,
              borderRadius: 2,
              backgroundColor: 'action.hover',
              maxWidth: '100%',
            }}
          >
            <LanguageIcon fontSize="small" color="action" />
            <Typography
              variant="caption"
              color="text.secondary"
              sx={{
                fontWeight: 500,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {targetUrl}
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
}
