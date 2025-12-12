import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Login from './pages/Login';
import Candidate from './pages/Candidate/CandidateDashboard';
import Admin from './pages/Admin/Admin';
import Manager from './pages/Manager/Manager';
import { AppBar, Box, Button, Toolbar, Typography } from '@mui/material';
import { AuthProvider, useAuth } from './AuthContext'; // Import AuthProvider and useAuth

// --- Updated PrivateRoute ---
function PrivateRoute({ children, allow }) {
  const { currentUser } = useAuth();
  return currentUser && allow.includes(currentUser.role) ? children : <Navigate to="/login" replace />;
}

// --- Updated TopBar ---
function TopBar() {
  const nav = useNavigate();
  const { currentUser, logout } = useAuth();

  const onLogout = () => {
    logout();
    nav('/login');
  };

  return (
    <AppBar position="static" sx={{ backgroundColor: '#1976d2' }} elevation={1}>
      <Toolbar>
        <Typography variant="h6" sx={{ flex: 1 }}>Employee Evaluation Hub</Typography>
        {currentUser && (
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Typography variant="body2" sx={{ alignSelf: 'center', mr: 1 }}>
              User: {currentUser.email}
            </Typography>
            <Button size="small" variant="outlined" onClick={onLogout} sx={{ color: 'white', borderColor: 'white' }}>Logout</Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
}

// --- Main App Component ---
export default function App() {
  return (
    <BrowserRouter>
      {/* AuthProvider now wraps everything that needs login info */}
      <AuthProvider>
        <TopBar />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/candidate"
            element={
              <PrivateRoute allow={['EMPLOYEE']}>
                <Candidate />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <PrivateRoute allow={['ADMIN']}>
                <Admin />
              </PrivateRoute>
            }
          />
          <Route
            path="/manager"
            element={
              <PrivateRoute allow={['MANAGER']}>
                <Manager />
              </PrivateRoute>
            }
          />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}