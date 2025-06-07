package STC.example.STC.Project.Servicios;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "STC-Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); //Con esto parceamos los JSONs que vienen de la API de google

    public void crearCalendarioConEventos(String accessToken, List<Event> eventos) throws GeneralSecurityException, IOException {

        //Objeto que usamos para hacer solicitudes HTTP
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        //Tocken Oauth del usuario autenticado, conseguido desde el accessToken del usuario
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(accessToken);

        //Aca construyo una instancia del Servicio de google calendar, para poder trabajar con la cuenta de google calendar del usuario. Lo usamos para interactuar con la API
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME) //Etiqueta
                .build();

        // Creo un nuevo calendario de Google calendar
        com.google.api.services.calendar.model.Calendar nuevoCalendario = new com.google.api.services.calendar.model.Calendar();
        nuevoCalendario.setSummary("Calendario generado por IA"); //Titulo del nuevo calendario
        nuevoCalendario.setTimeZone("America/Argentina/Buenos_Aires"); //Zona horaria

        //Con el insert() creo un calendario en el calendar del usuario("service") y le mando el recientemente creado("nuevoCalendario").
        // Con el excute() hago que me devuelva dicho calendario y tambien ejecuto
        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(nuevoCalendario).execute();

        //Inserto cada uno de los eventos extraidos del .csv y los voy agregando al nuevo calendario del usuario recientemente creado("createdCalendar"), con execute() mando dicha accion al calendar del usuario("service")
        for (Event event : eventos) {
            service.events().insert(createdCalendar.getId(), event).execute();
        }
    }


    //Con este metodo transformo el .csv a una list de Eventos de calendar para luego pasarlo al Service y crear el calendario
    public List<Event> csvToEventList(MultipartFile file) {
        List<Event> eventos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    Event evento = new Event();
                    evento.setSummary(partes[0]); //Titulo
                    evento.setDescription(partes[1]);

                    DateTime start = new DateTime(partes[2]);
                    DateTime end = new DateTime(partes[3]);

                    evento.setStart(new EventDateTime().setDateTime(start));
                    evento.setEnd(new EventDateTime().setDateTime(end));

                    eventos.add(evento);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el CSV", e);
        }

        return eventos;
    }

    //SOBRE CARGA DE METODO PARA PRUEBAS EN EL MAIN---------------------------
    public List<Event> csvToEventList(InputStream inputStream) {
        List<Event> eventos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    Event evento = new Event();
                    evento.setSummary(partes[0]);
                    evento.setDescription(partes[1]);

                    DateTime start = new DateTime(partes[2]);
                    DateTime end = new DateTime(partes[3]);

                    evento.setStart(new EventDateTime().setDateTime(start));
                    evento.setEnd(new EventDateTime().setDateTime(end));

                    eventos.add(evento);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // O loguealo
        }

        return eventos;
    }


}



