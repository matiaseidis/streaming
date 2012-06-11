package org.test.streaming.prevalence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public class Cachos implements Serializable{
	
	@Getter private final CopyOnWriteArrayList<Cacho> cachos = new CopyOnWriteArrayList<Cacho>();

	public boolean addCacho(Cacho newCacho) {
		List<Cacho> toAdd = new ArrayList<Cacho>();
		List<Cacho> toRemove = new ArrayList<Cacho>();
		boolean absent = true;
		
		for ( Cacho cacho : cachos) {
			if (cacho.isChoterThan(newCacho)){
				toAdd.add(newCacho);
				absent = false;
				toRemove.add(cacho);
			}
		}
		
		if(absent){
			toAdd.add(newCacho);
		}

		cachos.removeAll(toRemove);
		cachos.addAll(toAdd);
		
		return !toAdd.isEmpty();
	}
	
}
