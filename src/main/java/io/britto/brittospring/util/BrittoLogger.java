package io.britto.brittospring.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrittoLogger {

	public static final String GREEN  = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String WHITE  = "\u001B[37m";
	public static final String RESET  = "\u001B[0m";
	
	public static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	public static void showBanner() {
		System.out.println(YELLOW);
		System.out.println(" __    __      _________            .__              __________        .__  __    __            __    __   ");
		System.out.println(" \\ \\   \\ \\    /   _____/____________|__| ____    ____\\______   \\_______|__|/  |__/  |_  ____    \\ \\   \\ \\  Spring Britto Web Framework");
		System.out.println("  \\ \\   \\ \\   \\_____  \\\\____ \\_  __ \\  |/    \\  / ___\\|    |  _/\\_  __ \\  \\   __\\   __\\/  _ \\    \\ \\   \\ \\ For Study Purposes");
		System.out.println("  / /   / /   /        \\  |_> >  | \\/  |   |  \\/ /_/  >    |   \\ |  | \\/  ||  |  |  | (  <_> )   / /   / / By Fabio Britto");
		System.out.println(" /_/   /_/   /_______  /   __/|__|  |__|___|  /\\___  /|______  / |__|  |__||__|  |__|  \\____/   /_/   /_/  Content learned wity Professor Isidro on IsiFlix -> https://www.isiflix.com.br");
		System.out.println("                     \\/|__|                 \\//_____/        \\/                                           ");
		System.out.println(RESET);
	}
	
	public static void log(String modulo, String mensagem) {
		
		
		String date = LocalDateTime.now().format(formatDate);
		
		System.out.printf(GREEN + "%15s\t" + YELLOW + "%-30s:" + WHITE + "%s\n" + RESET, date, modulo, mensagem);
	}
}
