package io.britto.brittospring.explorer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassExplorer {

	/*
	 * O método "RetrieveAllClasses" é responsável pela chamada do método "packageExplorer".
	 * Isso deve acontecer de forma recursiva. até a chegada do caso base, que é quando todos
	 * os diretórios são totalmente explorados.
	 * O parâmetro "Class<?> sourceClass" é passado desde a chamada do método inicial "run".
	 */
	public static List<String> retrieveAllClasses(Class<?> sourceClass){
		return packageExplorer(sourceClass.getPackageName());
	}
	
	/*
	 * O método "packageExplorer" é responsável por explorar todos os diretórios do meu projeto.
	 * Isso é feito para que, ao ser chamado o método "retrieveAllClasses", a extração dos metadados
	 * das minhas classes, métodos e annotations possa acontecer.
	 * É recebido como parâmetro o "String packageName" que tem como valor, o nome do pacote
	 * onde está localizada a classe base/raiz do projeto.
	 * É armazenado em "stream" o caminho da pasta raiz, trocando cada "."(ponto) por "/".
	 */
	public static List<String> packageExplorer(String packageName){
		
		List<String> classNames = new ArrayList<>();
		try {
			InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("\\.", "/"));
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			
			String linha;
			/*
			 * Aqui, é iniciado um loop while que verifica cada arquivo dentro de um diretório.
			 * Caso a extensão do arquivo seja ".class", é adicionado na lista "classNames" o nome deste arquivo.
			 * Caso o arquivo não termine com esta extensão, significa que ele é uma pasta. Então, o método
			 * é chamado novamente (recursão), para que sejam analisados os arquivos desta pasta retornando 
			 * DENTRO de uma chamada de "classNames.addAll". Ou seja o método retornará uma List<String> que 
			 * será armazenada na lista. O loop continua até que tudo seja explorado.
			 */
			while((linha = br.readLine()) != null) {
				if(linha.endsWith(".class")) {
					linha = linha.substring(0, linha.indexOf(".class"));
					classNames.add(packageName + "." + linha);
				}
				else {
				/*
				 * NOVA PASTA = packageName -> nome do diretório atual +
				 * 				"." -> semelhante ao "/"
				 * 				linha -> nome da pasta encontrada
				 */
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
