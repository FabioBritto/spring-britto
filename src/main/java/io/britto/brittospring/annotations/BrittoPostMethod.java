package io.britto.brittospring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Com @Retention(RetentionPolicy.RUNTIME) eu defino que será uma anotação para atuar em tempo de execução
 * Com @Target(ElementType.METHOD) eu defino que será aplicado a um método. Ou seja, eu a uso para
 * anotar um método que lidará com requisições POST
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BrittoPostMethod {

	public String value();
}
