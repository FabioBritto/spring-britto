package io.britto.brittospring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Com @Retention(RetentionPolicy.RUNTIME) eu defino que será uma anotação para atuar em tempo de execução
 * Com @Target(ElementType.TYPE) eu defino que será aplicado a uma classe/a um tipo. Ou seja, sendo uma 
 * anotação referente à classe, eu anoto na declaração da mesma (como é feito com @Service)
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BrittoService {

}
