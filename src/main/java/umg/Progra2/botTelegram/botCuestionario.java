package umg.Progra2.botTelegram;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import umg.Progra2.Service.RespuestaService;
import umg.Progra2.Service.UserService;
import umg.Progra2.model.Respuesta;
import umg.Progra2.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class botCuestionario extends TelegramLongPollingBot {

    private Map<Long, String> estadoConversacion = new HashMap<>();
    private Map<Long, Integer> indicePregunta = new HashMap<>();
    private Map<Long, String> seccionActiva = new HashMap<>();
    private Map<String, String[]> preguntas = new HashMap<>();
    User usuarioConectado = null;
    UserService userService = new UserService();

    private String[] seccionesDisponibles() {
        List<String> secciones = new ArrayList<>();
        for (String seccion : preguntas.keySet()) {
            if (!seccionActiva.containsValue(seccion)) {
                secciones.add(seccion);
            }
        }
        return secciones.toArray(new String[0]);
    }

    public botCuestionario() {
        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{
                "1.1- ¿Qué haces para combatir el aburrimiento?",
                "1.2- ¿Cuál es la última serie o película que viste y te gustó?",
                "1.3- Si pudieras tener un superpoder, ¿cuál elegirías y por qué?",
                "1.4- ¿Qué libro o artículo has leído recientemente que te haya impactado?",
                "1.5- ¿Tienes algún pasatiempo o actividad que te relaje?"
        });

        preguntas.put("SECTION_2", new String[]{
                "2.1- ¿Cuál es tu hobby favorito?",
                "2.2- ¿Cual es tu meta?",
                "2.3- Si pudieras viajar a cualquier parte del mundo, ¿a dónde irías?",
                "2.4- ¿Qué tipo de música te gusta y cuál es tu artista favorito?",
                "2.5- ¿Tienes algún animal de compañía o te gustaría tener uno?"
        });

        preguntas.put("SECTION_3", new String[]{
                "3.1- ¿Qué habilidad nueva te gustaría aprender?",
                "3.2- ¿Cuál es tu comida favorita?",
                "3.3- ¿Qué harías si ganaras la lotería?",
                "3.4- ¿Cuál es tu lugar favorito para relajarte o pasar el tiempo libre?",
                "3.5- ¿Qué película o serie te ha hecho reír más recientemente?"
        });

        preguntas.put("SECTION_4", new String[]{
                "4.1- ¿Qué te inspira a seguir adelante en los momentos difíciles?",
                "4.2- ¿Qué edad tienes ?",
                "4.3- ¿Cómo te ves en cinco años?",
                "4.4- ¿Cuál es tu mayor logro hasta ahora y qué aprendiste de él?",
                "4.5- ¿Qué metas te gustaría alcanzar en el próximo año?"
        });
    }

    @Override
    public String getBotUsername() {
        return " @Manu03_bot";
    }

    @Override
    public String getBotToken() {
        return "7438710937:AAGOu2OjDf0sls5lsvhMj9erPXynaD4SbcE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String mensaje_Texto = update.getMessage().getText();
            String userFirstName = update.getMessage().getFrom().getFirstName();
            String userLastName = update.getMessage().getFrom().getLastName();
            String nickName = update.getMessage().getFrom().getUserName();

            try {
                String state = estadoConversacion.getOrDefault(chat_id, "");
                usuarioConectado = userService.getUserByTelegramId(chat_id);

                if (mensaje_Texto.equals("/menu")) {
                    // Si el usuario envía el comando /menu
                    sendMenu(chat_id);
                    return;
                }

                if (usuarioConectado == null && state.isEmpty()) {
                    sendText(chat_id, "Hola " + formatUserInfo(userFirstName, userLastName, nickName) + ", no tienes un usuario registrado en el sistema. Por favor ingresa tu correo electrónico:");
                    estadoConversacion.put(chat_id, "ESPERANDO_CORREO");
                    return;
                }

                if (state.equals("ESPERANDO_CORREO")) {
                    processEmailInput(chat_id, mensaje_Texto);
                    return;
                }


                if (seccionActiva.containsKey(chat_id)) {
                    manejaCuestionario(chat_id, mensaje_Texto);

                } else {
                    sendText(chat_id, "Hola " + formatUserInfo(userFirstName, userLastName, nickName) + ", envía /menu para iniciar el cuestionario.");
                }
            } catch (Exception e) {
                sendText(chat_id, "Ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.");
            }
        } else if (update.hasCallbackQuery()) {
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            inicioCuestionario(chat_id, data);
        }
    }

    private void processEmailInput(long chat_id, String email) {
        sendText(chat_id, "Recibo su Correo: " + email);
        estadoConversacion.remove(chat_id);
        try {
            usuarioConectado = userService.getUserByEmail(email);
        } catch (Exception e) {
            System.err.println("Error al obtener el usuario por correo: " + e.getMessage());
            e.printStackTrace();
        }

        if (usuarioConectado == null) {
            sendText(chat_id, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
        } else {
            usuarioConectado.setTelegramid(chat_id);
            try {
                userService.updateUser(usuarioConectado);
            } catch (Exception e) {
                System.err.println("Error al actualizar el usuario: " + e.getMessage());
                e.printStackTrace();
            }

            sendText(chat_id, "Usuario actualizado con éxito!");
        }
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
        } else {
            // Verifica si todas las secciones han sido completadas
            if (seccion.equals("SECTION_4")) {
                // El cuestionario se completa después de la sección 4
                sendText(chatId, "¡Has completado el cuestionario!");
                seccionActiva.remove(chatId);
                indicePregunta.remove(chatId);
                // Opcional: Puedes agregar lógica para manejar la finalización, como guardar el estado final en la base de datos.
            } else {
                // Mensaje al usuario para seleccionar la siguiente sección
                sendText(chatId, "Sección completada. Por favor, selecciona la siguiente sección del menú.");
                seccionActiva.remove(chatId);
                indicePregunta.remove(chatId);
                sendMenu(chatId);
            }
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        // Validar la edad si estamos en la segunda pregunta de la sección 4
        if (section.equals("SECTION_4") && index == 1) {
            try {
                int edad = Integer.parseInt(response);
                if (edad < 0 || edad > 120) {
                    sendText(chatId, "Por favor, ingresa una edad válida (entre 0 y 120 años).");
                    return;
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "Por favor, ingresa un número válido para la edad.");
                return;
            }
        }

        // Crear una instancia de Respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setSeccion(section);
        respuesta.setTelegramId(chatId);
        respuesta.setPreguntaId(index);
        respuesta.setRespuestaTexto(response);

        // Guardar la respuesta en la base de datos
        RespuestaService respuestaService = new RespuestaService();
        try {
            respuestaService.guardarRespuesta(respuesta);
            sendText(chatId, "Tu respuesta fue: " + response);
        } catch (SQLException e) {
            sendText(chatId, "Hubo un error al guardar tu respuesta. Inténtalo de nuevo.");
            e.printStackTrace();
        }

        // Avanza a la siguiente pregunta
        indicePregunta.put(chatId, index + 1);

        // Envía la siguiente pregunta
        enviarPregunta(chatId);
    }

    private String formatUserInfo(String firstName, String lastName, String userName) {
        return firstName + " " + lastName + " (" + userName + ")";
    }
    private void sendText(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una sección:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea los botones del menú
        rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
        rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
        rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
        rows.add(crearFilaBoton("Sección 4", "SECTION_4"));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }
}

