package STC.example.STC.Project;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileNotFoundException;

@SpringBootApplication
public class StcProjectApplication {
    public static void main(String[] args) throws FileNotFoundException {

        // Cargar las variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.configure()
            .directory("./backend")
            .load();

        // Configurar las variables de entorno necesarias para la aplicación
        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("GEMINI_API_KEY", dotenv.get("GEMINI_API_KEY"));

        SpringApplication.run(StcProjectApplication.class, args);
    }
}
