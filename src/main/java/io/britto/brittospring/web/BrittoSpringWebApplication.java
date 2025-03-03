package io.britto.brittospring.web;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class BrittoSpringWebApplication {

	public static void run() {
		
		try {
			Tomcat tomcat = new Tomcat();
			
			Connector connector = new Connector();
			connector.setPort(8080);
			
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
			tomcat.getServer().await();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
