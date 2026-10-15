import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Chip,
  Container,
} from '@mui/material';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import PublicIcon from '@mui/icons-material/Public';

export default function Header() {
  return (
    <AppBar
      position="static"
      elevation={0}
      sx={{
        backgroundColor: 'background.paper',
        borderBottom: 1,
        borderColor: 'divider',
        color: 'text.primary',
      }}
    >
      <Container maxWidth="md">
        <Toolbar disableGutters sx={{ py: 1.5, display: 'flex', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
            <Box
              sx={{
                width: 42,
                height: 42,
                borderRadius: 2,
                bgcolor: 'primary.main',
                color: 'primary.contrastText',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 4px 12px rgba(25, 118, 210, 0.25)',
              }}
            >
              <PublicIcon fontSize="medium" />
            </Box>
            <Box>
              <Typography
                variant="h6"
                component="h1"
                sx={{
                  fontWeight: 700,
                  letterSpacing: '-0.02em',
                  lineHeight: 1.2,
                }}
              >
                WebSage AI
              </Typography>
              <Typography
                variant="caption"
                color="text.secondary"
                sx={{ fontWeight: 500 }}
              >
                Web Page Summarizer
              </Typography>
            </Box>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Chip
              icon={<AutoAwesomeIcon sx={{ fontSize: '1rem !important' }} />}
              label="Google Gemini"
              size="small"
              color="primary"
              variant="outlined"
              sx={{ fontWeight: 600, display: { xs: 'none', sm: 'inline-flex' } }}
            />
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}
