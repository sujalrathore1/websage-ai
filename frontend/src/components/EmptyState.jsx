import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Paper,
  Stack,
} from '@mui/material';
import LinkIcon from '@mui/icons-material/Link';
import CleaningServicesIcon from '@mui/icons-material/CleaningServices';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import ShieldOutlinedIcon from '@mui/icons-material/ShieldOutlined';

const STEPS = [
  {
    icon: <LinkIcon color="primary" />,
    title: '1. Paste Public URL',
    description: 'Provide any public news article, blog post, or documentation page.',
  },
  {
    icon: <CleaningServicesIcon color="primary" />,
    title: '2. Clean Content Extraction',
    description: 'Jsoup strips noise, navigation, and ads, extracting the core article text.',
  },
  {
    icon: <AutoAwesomeIcon color="primary" />,
    title: '3. Gemini AI Summary',
    description: 'Google Gemini generates a focused, objective 100–150 word summary.',
  },
];

export default function EmptyState() {
  return (
    <Box sx={{ mt: 2, textAlign: 'center' }}>
      <Typography
        variant="overline"
        color="text.secondary"
        sx={{ fontWeight: 700, letterSpacing: '0.1em' }}
      >
        How It Works
      </Typography>

      <Grid container spacing={2.5} sx={{ mt: 0.5 }}>
        {STEPS.map((step, idx) => (
          <Grid item xs={12} sm={4} key={idx}>
            <Paper
              elevation={0}
              sx={{
                p: 3,
                height: '100%',
                borderRadius: 3,
                border: 1,
                borderColor: 'divider',
                backgroundColor: 'background.paper',
                textAlign: 'left',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  borderColor: 'primary.light',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)',
                },
              }}
            >
              <Box
                sx={{
                  width: 40,
                  height: 40,
                  borderRadius: 2,
                  bgcolor: 'action.hover',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  mb: 2,
                }}
              >
                {step.icon}
              </Box>
              <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.75 }}>
                {step.title}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.6 }}>
                {step.description}
              </Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>

      <Box
        sx={{
          mt: 3,
          py: 1.5,
          px: 2.5,
          display: 'inline-flex',
          alignItems: 'center',
          gap: 1,
          borderRadius: 2,
          backgroundColor: 'action.hover',
        }}
      >
        <ShieldOutlinedIcon fontSize="small" color="action" />
        <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 500 }}>
          Protected by SSRF security: internal networks, loopbacks, and metadata IPs are blocked.
        </Typography>
      </Box>
    </Box>
  );
}
