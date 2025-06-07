package STC.example.STC.Project.Controladores;


import STC.example.STC.Project.Servicios.GoogleCalendarService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/GoogleCalendar")
public class GoogleCalendarController {


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @PostMapping("/subir-csv")
    public ResponseEntity<String> subirCsv(@RequestParam("file") MultipartFile file,
                                           OAuth2AuthenticationToken authentication) throws GeneralSecurityException, IOException {
        //Obtengo el accessToken del usuario
        OAuth2AuthorizedClient authorizedClient = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String accessToken = authorizedClient.getAccessToken().getTokenValue(); //Obtengo el tocken del usuario logueado PARA PODER ACCEDER A SU GOOGLE CALENDAR

        //Convierto el CSV a lista de eventos
        List<Event> eventos = googleCalendarService.csvToEventList(file);
        //Se crea el nuevo calendario
        googleCalendarService.crearCalendarioConEventos(accessToken, eventos);

        return ResponseEntity.ok("Calendario creado exitosamente.");
    }

    //Con este metodo transformo el .csv a una list de Eventos de calendar para luego pasarlo al Service y crear el calendario
    /*private List<Event> csvToEventList(MultipartFile file) {
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
    }*/


}
