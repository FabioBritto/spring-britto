package io.britto.brittospring.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;

import io.britto.brittospring.datastructures.ControllersInstances;
import io.britto.brittospring.datastructures.ControllersMap;
import io.britto.brittospring.datastructures.DependencyInjectionMap;
import io.britto.brittospring.datastructures.RequestControllerData;
import io.britto.brittospring.datastructures.ServiceImplMap;
import io.britto.brittospring.util.BrittoLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BrittoDispatchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/*
	 * O método "service" recebe requisições HTTP padrão, independente do método usado na requisição.
	 * Como o Servlet recebe e trata toda e qualquer requisição, não existe a necessidade de usar
	 * "doPost" ou "doGet" por exemplo. 
	 * 
	 * A primeira verificação que é feita garante que, caso a URL termine com "/favicon.ico", eu termino
	 * o método sem fazer nada.
	 * 
	 * Depois, eu recupero dados da requisição (URL e Método HTTP) para poder encontrar a classe que irei trabalhar.
	 * Ou seja, com esses dois valores, consigo montar a minha "key" para acessar o VALUE do meu HashMap ControllersMap.
	 * Uma vez recuperado, posso armazenar em um RequestControllerData.
	 * 
	 * Logo depois, é verificado se já existe uma instância para esse Controller. Caso não exista, é criado dinamicamente.
	 */
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if(request.getRequestURL().toString().endsWith("/favicon.ico")) {
			return;
		}
		
		PrintWriter out = new PrintWriter(response.getWriter());
		Gson gson = new Gson();
		
		
		String url = request.getRequestURI();
		String httpMethod = request.getMethod().toUpperCase();
		String key = httpMethod + url;
		
		RequestControllerData data = ControllersMap.values.get(key);
		BrittoLogger.log("BrittoDispatcherServlet", "URL: " + url + "(" + httpMethod + ") - Handler " + data.getControllerClass() + "." + data.getControllerMethod());
		
		Object controller;
		BrittoLogger.log("DispatcherServlet", "Searching for Controller Instance");
		try {
			controller = ControllersInstances.instances.get(data.controllerClass);
			if(controller == null) {
				BrittoLogger.log("DispatcherServlet", "Crating new Controller Instance");
				controller = Class.forName(data.controllerClass).getDeclaredConstructor().newInstance();
				ControllersInstances.instances.put(data.controllerClass, controller);
				
				injectDependencies(controller);
			}
			
			/*
			 * Preciso extrair o método desta classe (da instância que eu acabei de criar dinamicamente caso ela já não exista)
			 * O método a ser extraído, é o método que irá atender à requisição
			 * 
			 * No método abaixo, eu basicamente percorro a lista de métodos maepados. Se algum deles bater com o método da 
			 * requisição, então existe correspondência
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
			 * Verificação da existência ou não de parâmetros no método Controller
			 */
			if(controllerMethod.getParameterCount() > 0) {
				BrittoLogger.log("BrittoDispatchServlet", "Method " + controllerMethod.getName() + " has parameters");
				Object arg;
				Parameter parameter = controllerMethod.getParameters()[0];

				
				if(parameter.getAnnotations()[0].annotationType().getName().equals("io.britto.brittospring.annotations.BrittoBody")) {
					String body = readBytesFromRequest(request);
					System.out.println(body.toString());
					/*
					 * Leitura de dados do BODY da requisição
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
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Método responsável pela leitura de bytes da requisição. Uso este método para poder recuperar o BODY
	 */
	private String readBytesFromRequest(HttpServletRequest request) throws Exception {
		StringBuilder str = new StringBuilder();
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		
		while((line = br.readLine()) != null) {
			str.append(line);
		}
		return str.toString();
	}
	
	/*
	 * Método responsável pela INJEÇÃO DE DEPENDÊNCIAS (semelhante ao @AutoWired)
	 */
	
	private void injectDependencies(Object client) throws Exception {
		for(Field f : client.getClass().getDeclaredFields()) {
			String attrType = f.getType().getName();
			BrittoLogger.log("BrittoDispatchServlet", "Injected " + f.getName() + " Field has type " + attrType);
			
			Object serviceImpl;
			
			if(DependencyInjectionMap.objects.get(attrType) == null) { 
				//Se não houver pela declaração da interface...
				BrittoLogger.log("Dependency Injection", "Couldn't find Instance for " + attrType);
				String implType = ServiceImplMap.implementations.get(attrType);
				//Buscando pela declaração da implementação
				if(implType != null) {
					BrittoLogger.log("Dependency Injection", "Found Instance for " + implType);
					//Se encontrei declaraçãp pela implementação, preciso ver se existe alguma instância
					serviceImpl = DependencyInjectionMap.objects.get(implType);
					if(serviceImpl == null) {
						BrittoLogger.log("Dependency Injection", "Injecting new Object");
						//Se não houver, eu crio uma instância
						serviceImpl =  Class.forName(implType).getDeclaredConstructor().newInstance();
						DependencyInjectionMap.objects.put(implType, serviceImpl);
					}
					//Agora preciso atribuir esta instância ao atributo
					f.setAccessible(true);
					f.set(client, serviceImpl);
					BrittoLogger.log("Dependency Injection", "Injected Object sucessfully");
				}
			}
			
		}
	}
}
