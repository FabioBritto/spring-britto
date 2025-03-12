package io.britto.brittospring.web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import io.britto.brittospring.annotations.BrittoGetMethod;
import io.britto.brittospring.annotations.BrittoPostMethod;
import io.britto.brittospring.datastructures.ControllersMap;
import io.britto.brittospring.datastructures.RequestControllerData;
import io.britto.brittospring.datastructures.ServiceImplMap;
import io.britto.brittospring.explorer.ClassExplorer;
import io.britto.brittospring.util.BrittoLogger;

public class BrittoSpringWebApplication {

	/*
	 * O método "run" é o primeiro método a ser chamado (assim como em uma aplicação Spring Boot).
	 * Ele recebe como parâmetro a classe base/raiz do projeto.
	 * 
	 * Para o Tomcat, existe um diretório chamado "webapps", onde são colocadas as pastas dos meus projetos.
	 * Aqui, é definido como Context, o diretório atual do projeto. Com isso, determino que ele procure
	 * pastas aqui.
	 * 
	 * Com "context.addServletMappingDecoded" é defino que tudo o que for digitado na URL,
	 * será capturado pelo meu único GRANDE SERVLET adicionado ao meu Tomcat: BrittoDispatcherServlet. 
	 * 
	 * As variáveis "inicio" e "fim" existem para registrar o início e fim da inicialização da aplicação
	 * 
	 * "tomcat.getServer().await()" coloca o Tomcat em modo de "aguardo" de novas requisições
	 */
	public static void run(Class<?> sourceClass) {

		// Zerando o log do Apache Tomcat
		java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
		long inicio, fim;

		try {
			inicio = System.currentTimeMillis();

			BrittoLogger.showBanner();

			BrittoLogger.log("Embeded Web Container", "Starting Spring Britto Application");
			extractMetaData(sourceClass);

			Tomcat tomcat = new Tomcat();

			Connector connector = new Connector();
			connector.setPort(8080);
			BrittoLogger.log("Embede Web Container", "Web Container started on port 8080");
			tomcat.setConnector(connector);

			Context context = tomcat.addContext("", new File(".").getAbsolutePath());

			Tomcat.addServlet(context, "BrittoDispatchServlet", new BrittoDispatchServlet());
			context.addServletMappingDecoded("/*", "BrittoDispatchServlet");

			tomcat.start();

			fim = System.currentTimeMillis();
			BrittoLogger.log("Embeded Web Container",
					"Spring Britto Web Application starten in " + ((double) (fim - inicio) / 1000) + " seconds");

			tomcat.getServer().await();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Com o uso da API Reflection, é possível fazer a recuperação dos metadados de cada classe.
	 * Dessa forma, são capturadas todas as anotações, percorrendo inicialmente todas as classes.
	 * Depois, é percorrido um vetor de anotações atrás de anotações @BrittoController e @BrittoService
	 * Esta verificação é feita comparando o "annotationType()" de cada anotação com o path final ".BrittoController"
	 * O mesmo é feito para o final ".BrittoService"
	 * Uma vez encontrada uma classe @BrittoController, é chamado o método "extractMethods()".
	 */
	private static void extractMetaData(Class<?> sourceClass) throws Exception {

		List<String> allClasses = ClassExplorer.retrieveAllClasses(sourceClass);

		for (String brittoClass : allClasses) {
			/*
			 * Recuperação das Annotations da Classe
			 */
			Annotation[] annotations = Class.forName(brittoClass).getAnnotations();

			for (Annotation classAnnotation : annotations) {
				if (classAnnotation.annotationType().getName()
						.equals("io.britto.brittospring.annotations.BrittoController")) {
					BrittoLogger.log("Metadata Explorer", "Found a Controller: " + brittoClass);
					extractMethods(brittoClass);
				}
				else if(classAnnotation.annotationType().getName().equals("io.britto.brittospring.annotations.BrittoService")) {
					BrittoLogger.log("Metadata Explorer", "Found a Service Implementation " + brittoClass);		
					
					for(Class<?> interf : Class.forName(brittoClass).getInterfaces()) {
						BrittoLogger.log("Metadata Explorer","        Class Implements: " + interf.getName());
						ServiceImplMap.implementations.put(interf.getName(), brittoClass);
					}
				}
			}
		}
	}

	/*
	 * Uma vez recebida a classe por parâmetro (className), é percorrida a lista de métodos contidos nesta classe.
	 * Se este método foi chamado, é porque se trata de uma requisição "capturada" por um @BrittoController.
	 * Portanto, são percorridos os métodos. Para cada método, são recuperadas as anotações. Ao percorrer, verifico se
	 * estou a anotação equivale a "BrittoGetMethod" ou "BrittoPostMethod". Se for o caso, as variáveis "httpMethdo" e
	 * "path" são preenchidas.
	 * No final, eu crio uma instância de RequestControllerData passando como parâmetro: className, o nome do método e
	 * os valores das duas variáveis (httpMethdo e path). Depois disso, o controller em questão é adicionado 
	 * ao Map de Controllers.
	 * 
	 */
	private static void extractMethods(String className) throws Exception {
		
		String httpMethod = "";
		String path = "";
		
		for(Method method: Class.forName(className).getDeclaredMethods()) {
			
			for(Annotation annotation : method.getAnnotations()) {
				
				if(annotation.annotationType().getName().equals("io.britto.brittospring.annotations.BrittoGetMethod")) {
					httpMethod = "GET";
					path = ((BrittoGetMethod)annotation).value(); 
				}
				else if(annotation.annotationType().getName().equals("io.britto.brittospring.annotations.BrittoPostMethod")) {
					httpMethod = "POST";
					path = ((BrittoPostMethod)annotation).value(); 
				}
				RequestControllerData getData = new RequestControllerData(httpMethod, path, className, method.getName());
				ControllersMap.values.put(httpMethod + path, getData);
			}
		}
	}	
}
