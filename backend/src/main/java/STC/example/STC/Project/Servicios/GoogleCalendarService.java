package STC.example.STC.Project.Servicios;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GoogleCalendarService {

    // Nombre de la aplicación, se usa para identificar la app en Google Calendar
    private static final String APPLICATION_NAME = "STC-Project";
    // Con esto parceamos los JSONs que vienen de la API de Google Calendar
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public void crearCalendarioConEventos(String accessToken, List<Event> eventos, String calendarTitle, String timeZone) throws GeneralSecurityException, IOException {
        // Objeto que usamos para hacer solicitudes HTTP
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Token Oauth del usuario autenticado, conseguido desde el accessToken del usuario
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(accessToken);

        // Se construye una instancia del Servicio de Google Calendar, para poder trabajar con la cuenta de Google Calendar del usuario. Se usa para interactuar con la API
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME) //Etiqueta
                .build();

        // Se crea un nuevo calendario de Google calendar
        com.google.api.services.calendar.model.Calendar nuevoCalendario = new com.google.api.services.calendar.model.Calendar();
        // Título del calendario
        nuevoCalendario.setSummary(calendarTitle);
        // Zona horaria del calendario
        nuevoCalendario.setTimeZone(timeZone);

        // Con el insert() se crea un calendario en el calendar del usuario y se manda el recientemente creado
        // Con el excute() se hace que devuelva dicho calendario y también se ejecuta
        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(nuevoCalendario).execute();

        // Inserta cada uno de los eventos extraidos del .csv y los va agregando al nuevo calendario del usuario recientemente creado, con execute() manda dicha acción al calendario del usuario
        for (Event event : eventos) {
            service.events().insert(createdCalendar.getId(), event).execute();
        }
    }

    // Con este método se transforma el .csv a una lista de eventos para luego pasarlo al Service y crear el calendario
    public List<Event> csvToEventList(MultipartFile file, String timeZone) {
        List<Event> eventos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linea;
            // Salta la primera línea del .csv (cabecera)
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    Event evento = new Event();
                    evento.setSummary(partes[0]);
                    evento.setDescription(partes[1]);

                    // Parseo de fechas considerando zona horaria
                    String startStr = partes[2].trim().replaceAll("^\"|\"$", "");
                    String endStr = partes[3].trim().replaceAll("^\"|\"$", "");

                    java.time.ZoneId zoneId = java.time.ZoneId.of(timeZone);

                    java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm[:ss]]");

                    java.time.ZonedDateTime startZoned;
                    java.time.ZonedDateTime endZoned;

                    if (startStr.length() == 10) {
                        java.time.LocalDate startDate = java.time.LocalDate.parse(startStr);
                        startZoned = startDate.atStartOfDay(zoneId);
                    } else {
                        java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(startStr, dateTimeFormatter);
                        startZoned = startDateTime.atZone(zoneId);
                    }

                    if (endStr.length() == 10) {
                        java.time.LocalDate endDate = java.time.LocalDate.parse(endStr);
                        endZoned = endDate.atStartOfDay(zoneId);
                    } else {
                        java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(endStr, dateTimeFormatter);
                        endZoned = endDateTime.atZone(zoneId);
                    }

                    // Formatea en RFC3339 (ISO 8601 extendido) con segundos siempre presentes
                    java.time.format.DateTimeFormatter rfc3339Formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                    String startIso = startZoned.format(rfc3339Formatter);
                    String endIso = endZoned.format(rfc3339Formatter);

                    DateTime start = new DateTime(startIso);
                    DateTime end = new DateTime(endIso);

                    evento.setStart(new EventDateTime().setDateTime(start).setTimeZone(timeZone));
                    evento.setEnd(new EventDateTime().setDateTime(end).setTimeZone(timeZone));

                    eventos.add(evento);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el CSV", e);
        }

        return eventos;
    }
}
