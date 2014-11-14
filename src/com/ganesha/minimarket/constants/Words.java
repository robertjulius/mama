package com.ganesha.minimarket.constants;

import java.util.HashMap;
import java.util.Map;

public class Words {

	private static final Map<Integer, String> SEQUENCES = new HashMap<>();

	static {
		SEQUENCES.put(1, "Pertama");
		SEQUENCES.put(2, "Ke-Dua");
		SEQUENCES.put(3, "Ke-Tiga");
		SEQUENCES.put(4, "Ke-Empat");
		SEQUENCES.put(5, "Ke-Lima");
		SEQUENCES.put(6, "Ke-Enam");
		SEQUENCES.put(7, "Ke-Tujuh");
		SEQUENCES.put(8, "Ke-Delapan");
		SEQUENCES.put(9, "Ke-Sembilan");
		SEQUENCES.put(10, "Ke-Sepuluh");
	}

	public static String getSequenceWord(int sequence) {
		return SEQUENCES.get(sequence);
	}
}
