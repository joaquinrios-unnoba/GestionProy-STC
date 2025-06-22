package STC.example.STC.Project.Controladores;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import STC.example.STC.Project.Servicios.GoogleCalendarService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoogleCalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
class GoogleCalendarControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoogleCalendarService googleCalendarService;

    // Test para el endpoint /subir-csv
    @Test
    void subirCsvDebeCrearCalendarioYResponderOk() throws Exception {
        // Datos simulados del archivo CSV
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "Subject,Description,Start Date,End Date\nClase de prueba,Descripción de prueba,2050-12-01T10:00:00,2050-12-01T12:00:00".getBytes()
        );

        String calendarTitle = "Mi calendario de test";
        String timeZone = "America/Argentina/Buenos_Aires";

        // Simula la conversión del CSV a una lista de eventos
        List<Event> eventosMock = List.of(new Event().setSummary("Clase de prueba")
                .setDescription("Descripción de prueba")
                .setStart(new EventDateTime().setDateTime(new DateTime("2050-12-01T10:00:00Z")).setTimeZone(timeZone))
                .setEnd(new EventDateTime().setDateTime(new DateTime("2050-12-01T12:00:00Z")).setTimeZone(timeZone)));

        // Simula el comportamiento del servicio
        when(googleCalendarService.csvToEventList(any(), eq(timeZone))).thenReturn(eventosMock);
        doNothing().when(googleCalendarService)
                .crearCalendarioConEventos(anyString(), eq(eventosMock), eq(calendarTitle), eq(timeZone));

        // Realiza el request simulando la subida del archivo CSV
        mockMvc.perform(
                multipart("/subir-csv")
                        .file(file)
                        .param("calendarTitle", calendarTitle)
                        .param("timeZone", timeZone)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Calendario creado exitosamente."));

        // Verifica que el servicio fue llamado con los parámetros correctos
        verify(googleCalendarService).csvToEventList(any(), eq(timeZone));
        verify(googleCalendarService).crearCalendarioConEventos(anyString(), eq(eventosMock), eq(calendarTitle), eq(timeZone));
    }

    // Configuraión de prueba para el resolver de OAuth2AuthorizedClient
    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        // Añade un resolver de argumentos para OAuth2AuthorizedClient
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new OAuth2AuthorizedClientResolverFake());
        }

        // Clase fake para simular el resolver de OAuth2AuthorizedClient
        static class OAuth2AuthorizedClientResolverFake implements HandlerMethodArgumentResolver {
            // Implementa el método supportsParameter para indicar que este resolver maneja OAuth2AuthorizedClient
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.getParameterType().equals(OAuth2AuthorizedClient.class);
            }

            // Implementa el método resolveArgument para devolver un OAuth2AuthorizedClient simulado
            @Override
            public Object resolveArgument(
                    MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                OAuth2AccessToken token = new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        "FAKE_ACCESS_TOKEN",
                        Instant.now(),
                        Instant.now().plusSeconds(3600)
                );
                OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
                when(authorizedClient.getAccessToken()).thenReturn(token);
                return authorizedClient;
            }
        }
    }
}
