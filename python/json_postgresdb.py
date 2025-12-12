import json
import psycopg2
from datetime import datetime

# Load JSON data
with open('./generated_mcq/mcq_java_tutorial_20251010_105828.json', 'r') as f:
    data = json.load(f)

# Extract timestamp
created_date = datetime.fromisoformat(data['metadata']['generation_timestamp'])

# Database connection parameters
conn = psycopg2.connect(
    dbname='ker',
    user='postgres',
    password='postgres',
    host='10.255.255.254',
    port='5432'
)
cursor = conn.cursor()

# Category ID (assumed to be known or fixed)
category_id = 1

# Loop through mcq_results
for result in data.get('mcq_results', []):
    if result.get('success'):
        for q in result.get('questions', []):
            question_text = q.get('question')
            options_json = json.dumps(q.get('options'))
            correct_answer = q.get('correct_answer')

            cursor.execute("""
                INSERT INTO question_answer (category_id, question, options, answer, created_date)
                VALUES (%s, %s, %s::jsonb, %s, %s)
            """, (
                category_id,
                question_text,
                options_json,
                correct_answer,
                created_date
            ))

# Commit and close
conn.commit()
cursor.close()
conn.close()
print("✅ Data inserted successfully.")
