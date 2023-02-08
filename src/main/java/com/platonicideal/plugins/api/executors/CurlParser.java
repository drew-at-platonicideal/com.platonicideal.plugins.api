package com.platonicideal.plugins.api.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.iterators.PeekingIterator;
import org.springframework.stereotype.Service;

@Service
public class CurlParser {

	public List<String> parse(String curl) {
		String[] elements = curl.split(" ");
		PeekingIterator<String> ittr = new PeekingIterator<String>(Arrays.asList(elements).iterator());
		List<String> toReturn = new ArrayList<>();
		while(ittr.hasNext()) {
			String s = ittr.peek();
			if(s.startsWith("--data")) {
				toReturn.addAll(getData(ittr));
			} else {
				toReturn.add(ittr.next());
			}
		}
		return toReturn;
	}

	private Collection<? extends String> getData(PeekingIterator<String> ittr) {
		List<String> toReturn = new ArrayList<>();
		toReturn.add(ittr.next());
		String data = "";
		while(!ittr.peek().equals("--request")) {
			data += ittr.next();
			data += " ";
		}
		data = data.trim();
		toReturn.add(data);
		return toReturn;
	}
	
}
