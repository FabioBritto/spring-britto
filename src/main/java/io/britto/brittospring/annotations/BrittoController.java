package io.britto.brittospring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
 * Com @Retention eu defino que será uma anotação para rodar em tempo de execução
 * Com @Target eu defino que será aplicado a uma instância de uma classe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BrittoController {
 
}
