package com.ss.editor.config;

import org.sample.client.GameThread;
import org.sample.client.Starter;
import org.sample.client.document.DocumentConfig;
import org.sample.client.util.GameUtil;
import rlib.network.NetworkConfig;
import rlib.util.Util;
import rlib.util.VarTable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Класс для конфигурирование игры.
 *
 * @author Ronn
 */
public abstract class Config {

    public static final String CONFIG_RESOURCE_PATH = "/org/sample/client/config/config.xml";

    /**
     * Адресс сокета сервера.
     */
    public static InetSocketAddress SERVER_SOCKER_ADDRESS;

    /**
     * Адресс сервера.
     */
    public static String SERVER_HOST;

    /**
     * Путь к папке клиента.
     */
    public static String PROJECT_PATH;

    /**
     * Порт для подключения к серверу.
     */
    public static int SERVER_PORT;

    /**
     * Размер читаемого буфера.
     */
    public static int NETWORK_READ_BUFFER_SIZE;

    /**
     * Размер записываемого буфера.
     */
    public static int NETWORK_WRITE_BUFFER_SIZE;

    /**
     * Размер группы поток сети.
     */
    public static int NETWORK_GROUP_SIZE;

    /**
     * Приоритет сетевых потоков.
     */
    public static int NETWORK_THREAD_PRIORITY;

    /**
     * Настройки сети.
     */
    public static NetworkConfig NETWORK_CONFIG = new NetworkConfig() {

        @Override
        public String getGroupName() {
            return "Network";
        }

        @Override
        public int getGroupSize() {
            return NETWORK_GROUP_SIZE;
        }

        @Override
        public int getReadBufferSize() {
            return NETWORK_READ_BUFFER_SIZE;
        }

        @Override
        public Class<? extends Thread> getThreadClass() {
            return GameThread.class;
        }

        @Override
        public int getThreadPriority() {
            return NETWORK_THREAD_PRIORITY;
        }

        @Override
        public int getWriteBufferSize() {
            return NETWORK_WRITE_BUFFER_SIZE;
        }

        @Override
        public boolean isVisibleReadException() {
            return true;
        }

        @Override
        public boolean isVisibleWriteException() {
            return false;
        }
    };

    /**
     * Отображать дебаг.
     */
    public static boolean DEV_DEBUG;

    /**
     * Загрузка конфига игры.
     */
    public static void init() throws UnknownHostException {

        final VarTable vars = new DocumentConfig(GameUtil.getInputStream(CONFIG_RESOURCE_PATH)).parse();

        SERVER_HOST = vars.getString("Server.host", "localhost");
        SERVER_PORT = vars.getInteger("Server.port", 1000);

        SERVER_SOCKER_ADDRESS = new InetSocketAddress(InetAddress.getByName(SERVER_HOST), SERVER_PORT);

        NETWORK_READ_BUFFER_SIZE = vars.getInteger("Network.readBufferSize", 8388608);
        NETWORK_WRITE_BUFFER_SIZE = vars.getInteger("Network.writeBufferSize", 8388608);
        NETWORK_GROUP_SIZE = vars.getInteger("Network.groupSize", 1);
        NETWORK_THREAD_PRIORITY = vars.getInteger("Network.threadPriority", 5);

        DEV_DEBUG = vars.getBoolean("Dev.debug", false);

        PROJECT_PATH = Util.getRootFolderFromClass(Starter.class).toString();
    }
}
