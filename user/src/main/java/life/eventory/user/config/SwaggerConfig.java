package life.eventory.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addServersItem(new Server()
                        .url("https://gateway.gamja.cloud")
                        .description("Production server"));
    }

    private Info apiInfo() {
        return new Info()
                .title("이벤토리")
                .description("이벤토리 {서버명} 서버 API Docs")
                .version("1.0.0");
    }
}
