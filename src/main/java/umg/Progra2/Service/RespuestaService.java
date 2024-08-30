package umg.Progra2.Service;

import umg.Progra2.dao.RespuestaDao;
import umg.Progra2.model.Respuesta;

import java.sql.SQLException;
import java.util.List;

public class RespuestaService {

    private RespuestaDao respuestaDao = new RespuestaDao();

    public void guardarRespuesta(Respuesta respuesta) throws SQLException {
        respuestaDao.insertarRespuesta(respuesta);
    }

    public List<Respuesta> obtenerRespuestasPorTelegramId(long telegramId) throws SQLException {
        return respuestaDao.obtenerRespuestasPorTelegramId(telegramId);
    }

    public List<Respuesta> obtenerTodasRespuestas() throws SQLException {
        return respuestaDao.obtenerTodasRespuestas();
    }


}

