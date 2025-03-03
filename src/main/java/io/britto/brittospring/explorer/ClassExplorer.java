package io.britto.brittospring.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassExplorer {

	/*
	 * A ideia é que o retrieveAllClasses chame o packageExplorer() e ele, recursivamente acesse o diretório
	 */
	public static List<String> retrieveAllClasses(Class<?> sourceClass){
		return packageExplorer(sourceClass.getPackageName());
	}
	
	public static List<String> packageExplorer(String packageName){
		
		List<String> classNames = new ArrayList<>();
		try {
			/*
			 * Primeiro, eu descubro qual o diretório base/raiz do meu pacote.
			 * 
			 * Dada a pasta onde se encontram as classes do meu projeto (getSystemClassLoader()),
			 * eu defino o packageName como uma pasta (?)
			 */
			InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("\\.", "/"));
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			
			String linha;
			/*
			 * Aqui eu acesso linha a linha do meu diretório (equivalente a dar um ls no cmd)
			 * Estou procurando arquivos .class. Quando acho, adiciono à minha lista.
			 * Se "linha" não termina com .class, significa que é uma outra pasta.
			 * Se for uma pasta, o que está dentro do else, vai concatenar o conteúdo de "packageName"
			 * de forma a passar RECURSIVAMENTE um novo endereço, que é o dessa pasta encontrada
			 */
			while((linha = br.readLine()) != null) {
				if(linha.endsWith(".class")) {
					linha = linha.substring(0, linha.indexOf(".class"));
					classNames.add(packageName + "." + linha);
				}
				else {
					classNames.addAll(packageExplorer(packageName + "." + linha));
				}
			}
			return classNames;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
