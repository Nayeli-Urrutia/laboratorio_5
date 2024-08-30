package umg.Progra2.dao;

import umg.Progra2.db.DatabaseConnection;
import umg.Progra2.model.Respuesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RespuestaDao {

    public void insertarRespuesta(Respuesta respuesta) throws SQLException {
        String query = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, respuesta.getSeccion());
            statement.setLong(2, respuesta.getTelegramId());
            statement.setInt(3, respuesta.getPreguntaId());
            statement.setString(4, respuesta.getRespuestaTexto());
            statement.executeUpdate();
        }
    }

    public List<Respuesta> obtenerRespuestasPorTelegramId(long telegramId) throws SQLException {
        String query = "SELECT * FROM tb_respuestas WHERE telegram_id = ?";
        List<Respuesta> respuestas = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, telegramId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Respuesta respuesta = new Respuesta();
                respuesta.setId(resultSet.getInt("id"));
                respuesta.setSeccion(resultSet.getString("seccion"));
                respuesta.setTelegramId(resultSet.getLong("telegram_id"));
                respuesta.setPreguntaId(resultSet.getInt("pregunta_id"));
                respuesta.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                respuesta.setFechaRespuesta(resultSet.getTimestamp("fecha_respuesta"));
                respuestas.add(respuesta);
            }
        }
        return respuestas;
    }

    public List<Respuesta> obtenerTodasRespuestas() throws SQLException {
        String query = "SELECT * FROM tb_respuestas";
        List<Respuesta> respuestas = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Respuesta respuesta = new Respuesta();
                respuesta.setId(resultSet.getInt("id"));
                respuesta.setSeccion(resultSet.getString("seccion"));
                respuesta.setTelegramId(resultSet.getLong("telegram_id"));
                respuesta.setPreguntaId(resultSet.getInt("pregunta_id"));
                respuesta.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                respuesta.setFechaRespuesta(resultSet.getTimestamp("fecha_respuesta"));
                respuestas.add(respuesta);
            }
        }
        return respuestas;
    }

}
