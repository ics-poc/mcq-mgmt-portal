export const subjects = ['Math', 'Java', 'SQL'];

function genQuestions(prefix) {
  return Array.from({ length: 25 }, (_, i) => ({
    id: i + 1,
    q: `${prefix} Question ${i + 1}?`,
    options: ['Option A', 'Option B', 'Option C', 'Option D'],
    answer: i % 4, // demo answer key
  }));
}

export const questionBank = {
  Math: genQuestions('Math'),
  Java: genQuestions('Java'),
  SQL: genQuestions('SQL'),
};