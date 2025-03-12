package io.britto.brittospring.datastructures;

import java.util.HashMap;

public class ControllersInstances {
	
	/*
	 * HashMap utilizado para armazenar:
	 * - Nome da classe Controller
	 * - Uma nova instância desta Controller
	 * 
	 * Será feita associação de cada nome de classe Controller para suas respectivas instâncias
	 */

	public static HashMap<String, Object> instances = new HashMap<>();
}
