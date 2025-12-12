import React from 'react';
import {
  Box, Typography, Paper, Stack, Grid, Accordion,
  AccordionSummary, AccordionDetails, List, ListItem, Chip
} from '@mui/material';
import { CheckCircle, Cancel, HelpOutline, ExpandMore } from '@mui/icons-material';

export default function ResultAnalysis({ result }) {
  if (!result || !result.managerTemplate || !result.managerTemplate.questions) {
    return <Typography>Detailed analysis is not available for this result.</Typography>;
  }

  const questions = result.managerTemplate.questions;
  let correct = 0;
  let incorrect = 0;
  let unanswered = 0;
  questions.forEach(q => {
    if (q.selectedAnswer === null || q.selectedAnswer === undefined) {
      unanswered++;
    } else if (q.selectedAnswer === q.answer) {
      correct++;
    } else {
      incorrect++;
    }
  });
  
  const getOptionStyle = (optionLetter, userChoice, correctLetter) => {
    const style = { display: 'flex', alignItems: 'center', mb: 1, p: 1, borderRadius: 1, border: '1px solid transparent' };
    const isCorrect = optionLetter === correctLetter;
    const isUserChoice = optionLetter === userChoice;

    if (isCorrect) {
      style.backgroundColor = 'rgba(46, 125, 50, 0.1)'; // success light
      style.borderColor = 'rgba(46, 125, 50, 0.5)';
    } else if (isUserChoice) {
      style.backgroundColor = 'rgba(211, 47, 47, 0.1)'; // error light
      style.borderColor = 'rgba(211, 47, 47, 0.5)';
    }
    return style;
  };

  return (
    <Box>
      <Typography variant="h5" align="center" gutterBottom sx={{ fontWeight: 'bold' }}>
        Overall Score: {result.score.toFixed(2)}%
      </Typography>
      <Grid container spacing={2} sx={{ mb: 3, textAlign: 'center' }}>
        <Grid item xs={4}>
          <Paper elevation={0} sx={{ p: 2, bgcolor: 'success.lighter', border: '1px solid', borderColor: 'success.light' }}>
            <Stack direction="row" spacing={1} justifyContent="center" alignItems="center">
              <CheckCircle color="success" />
              <Typography variant="h6">{correct}</Typography>
            </Stack>
            <Typography variant="body2">Correct</Typography>
          </Paper>
        </Grid>
        <Grid item xs={4}>
          <Paper elevation={0} sx={{ p: 2, bgcolor: 'error.lighter', border: '1px solid', borderColor: 'error.light' }}>
            <Stack direction="row" spacing={1} justifyContent="center" alignItems="center">
              <Cancel color="error" />
              <Typography variant="h6">{incorrect}</Typography>
            </Stack>
            <Typography variant="body2">Incorrect</Typography>
          </Paper>
        </Grid>
        <Grid item xs={4}>
          <Paper elevation={0} sx={{ p: 2, bgcolor: 'warning.lighter', border: '1px solid', borderColor: 'warning.light' }}>
            <Stack direction="row" spacing={1} justifyContent="center" alignItems="center">
              <HelpOutline color="warning" />
              <Typography variant="h6">{unanswered}</Typography>
            </Stack>
            <Typography variant="body2">Unanswered</Typography>
          </Paper>
        </Grid>
      </Grid>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMore />}>
          <Typography>Review All Questions</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <List dense sx={{ p: 0 }}>
            {questions.map((q, index) => {
              let optionsList = [];
              try {
                const optionsObj = typeof q.options === 'string' ? JSON.parse(q.options) : q.options;
                optionsList = Object.entries(optionsObj);
              } catch (e) {
                console.error("Failed to parse options JSON:", q.options);
                return null;
              }
              const userChoice = q.selectedAnswer;
              const correctLetter = q.answer;
              return (
                <ListItem key={q.questionId || index} divider sx={{ flexDirection: 'column', alignItems: 'flex-start' }}>
                  <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>{`${index + 1}. ${q.question}`}</Typography>
                  <Box component="div" sx={{ width: '100%' }}>
                    {optionsList.map(([letter, text]) => {
                      const isCorrect = letter === correctLetter;
                      const isUserChoice = letter === userChoice;

                      return (
                        <Box key={letter} sx={getOptionStyle(letter, userChoice, correctLetter)}>
                          <Typography variant="body2" sx={{ mr: 1, minWidth: '24px' }}>
                            {isCorrect && <CheckCircle fontSize="small" color="success" />}
                            {!isCorrect && isUserChoice && <Cancel fontSize="small" color="error" />}
                          </Typography>
                          <Typography component="span" variant="body2">{`(${letter}) ${text}`}</Typography>
                       </Box>
                      );
                    })}
                    {(userChoice === null || userChoice === undefined) && (
                      <Chip icon={<HelpOutline />} label="You did not answer this question." size="small" variant="outlined" color="warning" />
                    )}
                  </Box>
                </ListItem>
              );
            })}
          </List>
        </AccordionDetails>
      </Accordion>
    </Box>
  );
};