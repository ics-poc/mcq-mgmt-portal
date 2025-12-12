import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Avatar, Button, TextField, Box, Typography, Container, Paper } from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { useAuth } from '../AuthContext'; 

export default function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [pwd, setPwd] = useState('');
  const [err, setErr] = useState('');

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr('');
    try {
      const encodedPassword = btoa(pwd);
      const response = await fetch('http://localhost:8081/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: email.trim(),
          password: encodedPassword,
        }),
      });
      if (!response.ok) {
        throw new Error('Login failed');
      }
      const data = await response.json();
      login(data); 
      let path;
      switch (data.roleCode) {
        case 'ADMIN':
          path = '/admin';
          break;
        case 'MANAGER':
          path = '/manager'; 
          break;
        case 'EMPLOYEE':
        default:
          path = '/candidate';
          break;
      }
      nav(path, { replace: true });
    } catch (error) {
      console.error('Login error:', error);
      setErr('Invalid credentials');
    }
  };

  return (
    <Container maxWidth="xs" sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center' }}>
      <Paper elevation={3} sx={{ p: 4, width: '100%' }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">Sign in</Typography>
          <Box component="form" onSubmit={onSubmit} sx={{ mt: 1 }}>
            <TextField margin="normal" required fullWidth label="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <TextField margin="normal" required fullWidth label="Password" type="password" value={pwd} onChange={(e) => setPwd(e.target.value)} />
            {err && <Typography color="error" variant="body2">{err}</Typography>}
            <Button type="submit" fullWidth variant="contained" sx={{ mt: 2 }}>Sign In</Button>
            <Box sx={{ mt: 2 }}>
              {/* <Typography variant="caption">
                Demo users: candidate@example.com / pass123, admin@example.com / admin123
              </Typography> */}
            </Box>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}
