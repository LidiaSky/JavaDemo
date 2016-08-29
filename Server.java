/*
DataBaseAgent.java и Server.java - это серверная часть приложения "Интернет-аукцион", реализованного в рамках дипломной  работы.
Server.java - запуск веб-сервера на определенном порту, и обработка запросов к классу DataBaseAgent.
*/
package auctionServer;

import java.io.IOException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;


/**
 *
 * @author Лидия
 */
public class Server {
   /* public static void main(String [] args)
    {
        RobotLauncher launcher = new RobotLauncher();
        System.out.println(launcher.Search("science"));
    }                                              */
    private static final int port = 4444;

    public static void main(String args[]) throws IOException, XmlRpcException {
        // Запускаем веб-сервер на указанном порту.
        
        try
        {
            WebServer webServer = new WebServer(port);
            // Запускаем на веб-сервере сервер XMLRPC
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            // Добавляем обработчик запросов - класс DataBaseAgent
            phm.addHandler("DataBaseAgent",DataBaseAgent.class);

            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig =
                    (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);
            // Запускаем веб-сервер.
            webServer.start();
        }
        catch(Exception exc)
        {
            System.out.println("Exception occured!");
        }
        System.out.println("Сервер запущен!");
    }
}
