package STC.example.STC.Project.Controladores;


import STC.example.STC.Project.Servicios.GoogleCalendarService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GoogleCalendarController {


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @PostMapping("/subir-csv")
    public ResponseEntity<String> subirCsv(@RequestParam("file") MultipartFile file,
                                           OAuth2AuthenticationToken authentication) throws GeneralSecurityException, IOException {

        System.out.println("Nombre del archivo recibido: " + file.getOriginalFilename());
                                    
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

    //------UNICAMENTE PARA PRUEBAS, DESPUES LO BORRO O NO LO USEN----------
    //Devuelve el token de autenticacion del usuario
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> mostrarToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);

        return ResponseEntity.ok(response);
    }


}