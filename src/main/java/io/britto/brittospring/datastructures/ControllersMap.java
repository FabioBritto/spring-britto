package io.britto.brittospring.datastructures;

import java.util.HashMap;

/*
 * Ao encontrar um método Controller, é adicionado ao HashMap passando:
 * - Método HTTP + Path
 * - Dados da requisição ao Controller
 * 
 * O propósito é consultar esta estrutura no meu BrittoDispatchServlet e a partir daí, realizar as invocações
 * dos respectivos métodos
 */

public class ControllersMap {

	public static HashMap<String, RequestControllerData> values = new HashMap<>();
}
