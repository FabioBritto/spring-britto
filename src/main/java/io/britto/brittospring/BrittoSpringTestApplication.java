package io.britto.brittospring;

import io.britto.brittospring.web.BrittoSpringWebApplication;

public class BrittoSpringTestApplication {

	/*
	 * Chamo o método "run" passando como parâmetro a classe base/raiz do meu projeto.
	 * Este é o método inicial do meu framework, portanto, é o primeiro código a ser executado.
	 */
	public static void main(String[] args) {
		BrittoSpringWebApplication.run(BrittoSpringTestApplication.class); 
	}
}
