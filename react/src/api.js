// --- Base Config ---
export const API_BASE_URL = 'http://localhost:8081';

/**
 * Generic API fetch wrapper.
 * Prepends API_BASE_URL to endpoints.
 * Handles JSON and text responses.
 */
export const fetchAPI = async (endpoint, options = {}) => {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
  if (!response.ok) {
    const errorInfo = await response.json().catch(() => ({ message: 'An unknown error occurred' }));
    throw new Error(errorInfo.message || 'Network response was not ok');
  }
  if (response.status === 200 && options.method === 'DELETE') {
    return { success: true };
  }
  const text = await response.text();
  try {
    return JSON.parse(text); 
  } catch (e) {
    return text || { success: true };
  }
};


const fetchQuestionsForReview = (categoryId) => {
  const endpoint = `/api/questionAnswer/category/${categoryId}`;
  return fetchAPI(endpoint, { method: 'GET' });
};

const submitReview = (categoryId, reviews) => {
   return fetchAPI('/api/questionAnswer/status', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reviews),
  });
};

export const adminApi = {
  fetchQuestionsForReview,
  submitReview,
};

// --- Candidate: Real APIs ---

const getAllExams = (candidateId) => 
  fetchAPI(`/api/assessments/user/${candidateId}`);

const getExamById = (candidateId, examId) => 
  fetchAPI(`/api/${candidateId}/assessment/${examId}`);

const getResults = (candidateId, examId) => 
  fetchAPI(`/api/${candidateId}/assessment/results/${examId}`);

const submitExam = (candidateId, examId, payload) => 
  fetchAPI(`/api/${candidateId}/assessment/${examId}/submit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
export const api = {
  getAllExams,
  getExamById,
  getResults,
  submitExam,
};