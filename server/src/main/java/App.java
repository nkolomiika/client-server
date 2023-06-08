import commands.available.*;
import exceptions.ExitObligedException;
import managers.CollectionManager;
import managers.CommandCollection;
import managers.Parser;
import network.Configuration;
import utils.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class App {
    public static int port = Configuration.PORT;
    public static final int connection_timeout = 60 * 1000;
    private static final Printable console = new Console();

    public static void main(String[] args) {
        /*if (args.length != 1) {
            console.printError("Поместите путь в аргументы командной строки!");
            return;
        }*/

        Parser parser = new Parser(console);
        CollectionManager collectionManager = new CollectionManager(parser);

        try {
            collectionManager.setDragons(parser.convertToDragons());
        } catch (FileNotFoundException e) {
            console.printError("Файл не найден");
        } catch (ExitObligedException e){

        }

        CommandCollection commandCollection = new CommandCollection(parser);

        commandCollection.addCommand(List.of(

                new Add(collectionManager),
                new AddIfMax(collectionManager),
                new AddIfMin(collectionManager),
                new Clear(collectionManager),
                new ExecuteScript(),
                new Exit(),
                new FilterContainsName(collectionManager),
                new FilterStartsWithDescription(collectionManager),
                new Help(commandCollection),
                new Info(collectionManager),
                new RemoveById(collectionManager),
                new RemoveGreater(collectionManager),
                //new Save(collectionManager, commandCollection),
                new Show(collectionManager),
                new UpdateId(collectionManager)
        ));

        RequestHandler requestHandler = new RequestHandler(commandCollection);
        DatagramServer server = null;
        try {
            server = new DatagramServer(InetAddress.getLocalHost(), port, connection_timeout, requestHandler, parser, collectionManager, console);
        } catch (UnknownHostException e) {
            console.printError("Неизвестный хост");
        } catch (SocketException e) {
            console.printError("Случилась ошибка сокета");
        }
        try {
            server.run();
        } catch (IOException e) {
            console.printError("Неизвестная ошибка ");
            e.printStackTrace();
        }
    }
}