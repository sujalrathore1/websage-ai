import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 45000, // 45 seconds for scraping + AI synthesis
});

/**
 * Summarize webpage content by URL
 * @param {string} url The target webpage URL
 * @returns {Promise<{url: string, title: string, summary: string}>}
 */
export async function summarizeWebpage(url) {
  try {
    const response = await apiClient.post('/api/summarize', { url });
    return response.data;
  } catch (error) {
    if (error.response) {
      // Backend returned standard ErrorResponse JSON
      const data = error.response.data;
      const errorObj = new Error(data.message || 'Failed to process request.');
      errorObj.code = data.error || `HTTP_${error.response.status}`;
      errorObj.status = error.response.status;
      throw errorObj;
    } else if (error.request) {
      // No response from server (network error or backend down)
      const errorObj = new Error(
        `Unable to connect to the backend server at ${API_BASE_URL}. Please ensure the Spring Boot server is running.`
      );
      errorObj.code = 'NETWORK_ERROR';
      errorObj.status = 0;
      throw errorObj;
    } else {
      const errorObj = new Error(error.message || 'An unexpected client error occurred.');
      errorObj.code = 'CLIENT_ERROR';
      throw errorObj;
    }
  }
}

export default apiClient;
