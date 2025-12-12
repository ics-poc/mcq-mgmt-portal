package com.ker.demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShuffleController<T> {

	private final List<T> questions;
	private final List<Integer> shuffledIndices;
	private int batchSize;
	private int currentIndex;

	public ShuffleController(List<T> questions, int batchSize) {
		this.questions = questions;
		this.batchSize = batchSize;
		this.currentIndex = 0;
		this.shuffledIndices = new ArrayList<>();
		for (int i = 0; i < questions.size(); i++) {
			shuffledIndices.add(i);
		}
		fisherYatesShuffle(shuffledIndices);
	}

	private void fisherYatesShuffle(List<Integer> list) {
		Random rand = new Random();
		for (int i = list.size() - 1; i > 0; i--) {
			int j = rand.nextInt(i + 1);
			Collections.swap(list, i, j);
		}
	}

	public List<T> getNextBatch() {
		if (currentIndex >= questions.size()) {
			return Collections.emptyList();
		}
		int endIndex = Math.min(currentIndex + batchSize, questions.size());
		List<T> batch = new ArrayList<>();
		for (int i = currentIndex; i < endIndex; i++) {
			batch.add(questions.get(shuffledIndices.get(i)));
		}
		currentIndex = endIndex;
		return batch;
	}

	public void setBatchSize(int newBatchSize) {
		this.batchSize = newBatchSize;
	}

	public void reset() {
		currentIndex = 0;
		fisherYatesShuffle(shuffledIndices);
	}

	public static class Question {
		public Long id;
		public String question;

		public Question() {
		}

		@Override
		public String toString() {
			return "Question{id=" + id + ", question='" + question + "'}";
		}
	}

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		try {
// Load questions from JSON file
//            List<Question> questions = mapper.readValue(
//                    new File("questions.json"),
//                    new TypeReference<List<Question>>() {}
//            );
			InputStream input = ShuffleController.class.getResourceAsStream("/questions.json");
			if (input == null) {
				throw new RuntimeException("Resource questions.json not found!");
			}
			List<Question> questions = mapper.readValue(input, new TypeReference<List<Question>>() {
			});
			ShuffleController<Question> sampler = new ShuffleController<>(questions, 5);
			List<Question> batch1 = sampler.getNextBatch();
			System.out.println("Batch 1:");
			batch1.forEach(System.out::println);
			List<Question> batch2 = sampler.getNextBatch();
			System.out.println("Batch 2:");
			batch2.forEach(System.out::println);
			sampler.setBatchSize(5);
			List<Question> batch3 = sampler.getNextBatch();
			System.out.println("Batch 3 (batch size 5):");
			batch3.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
