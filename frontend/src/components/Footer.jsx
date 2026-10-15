import React from 'react';
import { Box, Typography, Link, Container } from '@mui/material';

export default function Footer() {
  return (
    <Box
      component="footer"
      sx={{
        mt: 'auto',
        py: 3,
        borderTop: 1,
        borderColor: 'divider',
        backgroundColor: 'background.paper',
        textAlign: 'center',
      }}
    >
      <Container maxWidth="md">
        <Typography variant="body2" color="text.secondary">
          <strong>WebSage AI</strong> – Built with React, Vite, Material UI, Java 21, Spring Boot 3, and Jsoup.
        </Typography>
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
          AI Summarization powered by{' '}
          <Link
            href="https://aistudio.google.com/"
            target="_blank"
            rel="noopener noreferrer"
            underline="hover"
          >
            Google Gemini API (Free Tier)
          </Link>
          .
        </Typography>
      </Container>
    </Box>
  );
}
