package io.britto.brittospring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Field = Atributo
 * 
 * Com @Retention(RetentionPolicy.RUNTIME) eu defino que será uma anotação para atuar em tempo de execução
 * Com @Target(ElementType.FIELD) eu defino que será aplicado a um atributo de uma classe. Esta anotação
 * tem seu funcionamento parecido com o @AutoWired, sendo usada para INJEÇÃO DE DEPENDÊNCIAS.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BrittoInjected {

	
}
