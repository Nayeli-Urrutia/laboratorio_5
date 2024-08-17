package umg.Progra2;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.Progra2.botTelegram.Bot;
import umg.Progra2.botTelegram.PokemomBot;
import umg.Progra2.botTelegram.tareaBot;

import java.sql.SQLOutput;

public class Main {
    public static void main(String[] args) {

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            //Bot bot = new Bot();

           //PokemomBot poke = new PokemomBot();

            tareaBot tarea = new tareaBot();

            botsApi.registerBot(tarea);

            System.out.println("**********************INGRESAR************************* ");
            System.out.println("/info: (Mostrar Información");
            System.out.println("/progra: (Comentario Sobre la Programación) ");
            System.out.println("/hola: (Mostrar fecha)");
            System.out.println("/cambio: (Cambio de Euros a Quetzalez)");
            System.out.println("/grupal: (Mensaje al grupo)");
            System.out.println("******************************************************* ");
        }
    catch(Exception ex){

                System.out.println("error" + ex.getMessage());
            }
    }
}