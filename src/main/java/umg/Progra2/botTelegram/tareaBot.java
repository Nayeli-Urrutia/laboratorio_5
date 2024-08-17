package umg.Progra2.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class tareaBot extends TelegramLongPollingBot {

    private static final double TIPO_CAMBIO_EURO_A_QUETZAL = 8.89;
    private static final List<Long> LISTA_CHAT_IDS = List.of(
            //id Edgar
            5792621349L,
            //id Manuel
            6602268509L,
            //id Karen
            6984229154L,
            //id Alejandro
            5454689659L




    );

    @Override
    public String getBotUsername() {
        return "@Manu03_bot";
    }

    @Override
    public String getBotToken() {
        return "7438710937:AAGOu2OjDf0sls5lsvhMj9erPXynaD4SbcE";
    }


    @Override
    public void onUpdateReceived(Update update) {

        // obtener informacion de la persona que manda los mensajes
        String nombre = update.getMessage().getFrom().getFirstName();
        String apellido = update.getMessage().getFrom().getLastName();
        String nickName = update.getMessage().getFrom().getUserName();


        if (update.hasMessage() && update.getMessage().hasText()) {

            System.out.println("Hola" + nickName + " Tu nombre es " + nombre + " y tu apellido es:" + apellido);
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            // manejo de mansajes

            if (message_text.toLowerCase().equals("hola")) {
                sendText(chat_id, " Hola " + nombre + " gusto de saludarte ");
            }

            // Ejercicio 1
            if (message_text.toLowerCase().equals("/info")) {
                sendText(chat_id, "Nombre: Nayeli Urrutia \nCarnet: 0905-23-5575 \nSemestre: 4to Semestre  ");
            }

            //Ejercicio 2
            if (message_text.toLowerCase().equals("/progra")) {
                sendText(chat_id, "El curso de programación es esencial para desarrollar habilidades en lógica y resolución de problemas mediante código. \n" +
                        "Los estudiantes aprenden diversos lenguajes y técnicas, adquiriendo una base sólida para el desarrollo de software.");
            }


            //Ejercicio 3
            if (message_text.toLowerCase().equals("/hola")) {
                LocalDateTime fechaHoraActual = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM, HH:mm");
                String fecha = fechaHoraActual.format(formatter);
                sendText(chat_id, "Hola " + nombre + " hoy es " + fecha);
            }

            //Ejercicio 4

            if (message_text.toLowerCase().startsWith("/cambio")) {
                try {
                    // Extraer el valor numérico del mensaje
                    String[] parts = message_text.split(" ");
                    if (parts.length == 2) {
                        double euros = Double.parseDouble(parts[1]);
                        double quetzales = euros * TIPO_CAMBIO_EURO_A_QUETZAL;
                        String respuesta = String.format("Son %.2f quetzales.", quetzales);

                        // Enviar la respuesta
                        sendText(chat_id, respuesta);
                    } else {
                        sendText(chat_id, "Por favor, proporciona un monto en Euros después del comando. Ejemplo: /cambio 100");
                    }
                } catch (NumberFormatException e) {
                    sendText(chat_id, "El monto proporcionado no es válido. Asegúrate de ingresar un número.");
                }
            }

            //Ejercicio 5
            if (message_text.toLowerCase().startsWith("/grupal")) {
                String[] parts = message_text.split(" ", 2);
                if (parts.length == 2) {
                    String mensaje = parts[1];
                    for (Long id : LISTA_CHAT_IDS) {
                        sendText(id, mensaje);
                    }
                    sendText(chat_id, "El mensaje ha sido enviado a todos los compañeros.");
                } else {
                    sendText(chat_id, "Por favor, proporciona un mensaje después del comando. Ejemplo: /grupal Hola a todos.");
                }

            } else {
                sendText(chat_id, "Comando no reconocido. \nUtiliza: \n/info para obtener información, \n/cambio [monto] para calcular el cambio, \n/progra para saber sobre el curso, \n/grupal [mensaje] para enviar un mensaje a tus compañeros.");
            }
        }
    }







    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

}


