package io.britto.brittospring.web;

import java.util.List;

import io.britto.brittospring.explorer.ClassExplorer;

public class BrittoSpringWebApplication {

	public static void run(Class<?> sourceClass) {
		
		List<String> allClasses =  ClassExplorer.retrieveAllClasses(sourceClass);
		
		for(String s : allClasses) {
			System.out.println(s);
		}
		
		//Zerando o log do Apache Tomcat
//		java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
//		long inicio, fim;
//		
//		try {
//			inicio = System.currentTimeMillis();
//			BrittoLogger.log("Main Module", "Starting Spring Britto Application");
//			BrittoLogger.showBanner();
//			Tomcat tomcat = new Tomcat();
//			
//			Connector connector = new Connector();
//			connector.setPort(8080);
//			
//			tomcat.setConnector(connector);
//			
//			/*
//			 * new File(".").getAbsolutePath() -> Procura classes no diretório atual
//			 */
//			Context context = tomcat.addContext("", new File(".").getAbsolutePath());
//			
//			Tomcat.addServlet(context, "BrittoDispatchServlet", new BrittoDispatchServlet()); 			
//			/*
//			 * Aqui eu redireciono TODA E QUALQUER URL para o meu Servlet
//			 */
//			context.addServletMappingDecoded("/*", "BrittoDispatchServlet");
//			
//			/*
//			 * getServer().await() -> Ele fica em modo "bloqueando" esperando as requisições
//			 */
//			tomcat.start();
//			
//			fim = System.currentTimeMillis();
//			BrittoLogger.log("Main Module", "Spring Britto Web Application starten in " + ((double) (fim-inicio) / 1000) + " seconds");
//			
//			tomcat.getServer().await();
//			
//			
//		}
//		catch(Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
