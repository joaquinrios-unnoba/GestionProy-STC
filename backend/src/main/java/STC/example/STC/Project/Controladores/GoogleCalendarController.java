package STC.example.STC.Project.Controladores;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import STC.example.STC.Project.Servicios.GoogleCalendarService;

@Controller
public class GoogleCalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @PostMapping("/subir-csv")
    public ResponseEntity<String> subirCsv(
            MultipartFile file,
            @RequestParam String calendarTitle,
            @RequestParam String timeZone,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) throws GeneralSecurityException, IOException {

        //Obtengo el token del usuario PARA PODER ACCEDER A SU GOOGLE CALENDAR
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        //Convierto el CSV a lista de eventos
        List<Event> eventos = googleCalendarService.csvToEventList(file,timeZone);
        //Se crea el nuevo calendario
        googleCalendarService.crearCalendarioConEventos(accessToken, eventos, calendarTitle, timeZone);

        return ResponseEntity.ok("Calendario creado exitosamente.");
    }
}
