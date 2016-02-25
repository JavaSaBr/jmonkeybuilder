package com.ss.editor.config;

import java.net.InetSocketAddress;

/**
 * Обработчик изменения конфигурации по переданным аргументам.
 * 
 * @author Ronn
 */
public class CommandLineConfig {

	public static void args(String[] args) {

		for(String arg : args) {

			final String[] values = arg.split("=", 2);

			if(values.length < 2) {
				continue;
			}

			switch(values[0]) {
				case "server": {
					Config.SERVER_SOCKER_ADDRESS = getAddressFrom(values[1]);
					break;
				}
			}
		}
	}

	private static InetSocketAddress getAddressFrom(String value) {
		final String[] values = value.split(":", 2);
		return new InetSocketAddress(values[0], Integer.parseInt(values[1]));
	}
}
