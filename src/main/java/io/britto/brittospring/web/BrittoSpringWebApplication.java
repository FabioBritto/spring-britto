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

			/*
			 * new File(".").getAbsolutePath() -> Procura classes no diretório atual
			 */
			Context context = tomcat.addContext("", new File(".").getAbsolutePath());

			Tomcat.addServlet(context, "BrittoDispatchServlet", new BrittoDispatchServlet());
			/*
			 * Aqui eu redireciono TODA E QUALQUER URL para o meu Servlet
			 */
			context.addServletMappingDecoded("/*", "BrittoDispatchServlet");

			/*
			 * getServer().await() -> Ele fica em modo "bloqueando" esperando as requisições
			 */
			tomcat.start();

			fim = System.currentTimeMillis();
			BrittoLogger.log("Embeded Web Container",
					"Spring Britto Web Application starten in " + ((double) (fim - inicio) / 1000) + " seconds");

			tomcat.getServer().await();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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
		/*
		 * Vou percorrer minha Estrutura de Dados
		 
		for(RequestControllerData item : ControllersMap.values.values()) {
			BrittoLogger.log("","     " + item.httpMethod + ": " + item.url + ": [" + item.controllerClass + "." + item.controllerMethod + "]");
		}
		*/

	}

	private static void extractMethods(String className) throws Exception {
		
		String httpMethod = "";
		String path = "";
		
		/*
		 * Recuperação de todos os métodos da classe
		 */
		for(Method method: Class.forName(className).getDeclaredMethods()) {
			/*
			 * Para cada método, recupero todas as suas anotações
			 */
			for(Annotation annotation : method.getAnnotations()) {
				/*
				 * SE o meu método está anotado com BrittoGetMehtod, eu pego o valor de sua anotação e exibo no LOG
				 */
				if(annotation.annotationType().getName().equals("io.britto.brittospring.annotations.BrittoGetMethod")) {
					httpMethod = "GET";
					path = ((BrittoGetMethod)annotation).value(); 
					//BrittoLogger.log("", "   + method " + method.getName() + "  - URL GET = " + path);
				}
				else if(annotation.annotationType().getName().equals("io.britto.brittospring.annotations.BrittoPostMethod")) {
					httpMethod = "POST";
					path = ((BrittoPostMethod)annotation).value(); 
					//BrittoLogger.log("", "   + method " + method.getName() + "  - URL POST = " + path);
				}
				RequestControllerData getData = new RequestControllerData(httpMethod, path, className, method.getName());
				/*
				 * Verbo HTTP + caminho/path me darão a informação de qual classe e método
				 */
				ControllersMap.values.put(httpMethod + path, getData);
			}
		}
	}	
}
