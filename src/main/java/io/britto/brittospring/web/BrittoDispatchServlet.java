package io.britto.brittospring.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;

import io.britto.brittospring.datastructures.ControllersInstances;
import io.britto.brittospring.datastructures.ControllersMap;
import io.britto.brittospring.datastructures.RequestControllerData;
import io.britto.brittospring.util.BrittoLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BrittoDispatchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//Preciso ignorar o FAVICON. O browser pede ele, mas eu só preciso da URL
		if(request.getRequestURL().toString().endsWith("/favicon.ico")) {
			return;
		}
		
		PrintWriter out = new PrintWriter(response.getWriter());
		Gson gson = new Gson();
		
		
		String url = request.getRequestURI();
		String httpMethod = request.getMethod().toUpperCase();
		String key = httpMethod + url;
		/*
		 * A URI pega só o final da URL. Isso, somado com o método, me fornece a key pro meu HashMap de ControllersMap
		 * Com ele, consigo criar um RequestControllerData
		 */
		RequestControllerData data = ControllersMap.values.get(key);
		BrittoLogger.log("BrittoDispatcherServlet", "URL: " + url + "(" + httpMethod + ") - Handler " + data.getControllerClass() + "." + data.getControllerMethod());
		
		/*
		 * Verificação se existe uma instância da classe Controller correspondente
		 * Se não houver uma instância desta classe, eu crio uma instância DINAMINCAMENTE
		 */
		Object controller;
		BrittoLogger.log("DispatcherServlet", "Searching for Controller Instance");
		try {
			controller = ControllersInstances.instances.get(data.controllerClass);
			if(controller == null) {
				BrittoLogger.log("DispatcherServlet", "Crating new Controller Instance");
				controller = Class.forName(data.controllerClass).getDeclaredConstructor().newInstance();
				ControllersInstances.instances.put(data.controllerClass, controller);
			}
			
			/*
			 * Preciso extrair o método desta classe (da instância que eu acabei de criar dinamicamente caso ela já não exista
			 * O método a ser extraído, é o método que irá atender à requisição
			 * 
			 * No método abaixo, eu basicamente percorro a lista de métodos maepados. Se algum deles bater com o método da 
			 * requisição, então existe corr espondência
			 */
			
			Method controllerMethod = null;
			for(Method met: controller.getClass().getMethods()) {
				if(met.getName().equals(data.controllerMethod)) {
					controllerMethod = met;
					break;
				}
			}
			BrittoLogger.log("DispatcherServlet", "Invoking method: " + controllerMethod.getName() + " to handle Request");
			
			/*
			 * Meu método tem parâmetros?
			 */
			if(controllerMethod.getParameterCount() > 0) {
				BrittoLogger.log("BrittoDispatchServlet", "Method " + controllerMethod.getName() + " has parameters");
				Object arg;
				Parameter parameter = controllerMethod.getParameters()[0];
				if(parameter.getAnnotations()[0].annotationType().getName().equals("io.britto.brittospring.annotations.BrittoBody")) {
					String body = readBytesFromRequest(request);
					/*
					 * Preciso ler os dados que vêm da Requisição
					 */
					BrittoLogger.log("", "     Found Parameter from Request of type " + parameter.getType().getName());
					BrittoLogger.log("", "     Parameter content: " +  body);
					arg = gson.fromJson(body, parameter.getType());
					
					out.println(gson.toJson(controllerMethod.invoke(controller, arg)));
				}
				else {
					out.println(gson.toJson(controllerMethod.invoke(controller)));
				}
			}
			
			
			/*
			 * Eu invoco o método a partir da instância criada
			 */
			
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String readBytesFromRequest(HttpServletRequest request) throws Exception {
		StringBuilder str = new StringBuilder();
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		
		while((line = br.readLine()) != null) {
			str.append(line);
		}
		return str.toString();
	}
}
