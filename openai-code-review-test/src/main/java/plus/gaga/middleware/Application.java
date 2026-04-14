package plus.gaga.middleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import plus.gaga.middleware.sdk.infrastructure.git.GitCommand;
import plus.gaga.middleware.sdk.infrastructure.openai.IOpenAI;
import plus.gaga.middleware.sdk.infrastructure.openai.impl.DeepSeek;
import plus.gaga.middleware.sdk.infrastructure.weixin.WeiXin;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    @ConfigurationProperties(prefix = "github")
    public GitProperties gitProperties() {
        return new GitProperties();
    }

    @Bean
    public GitCommand gitCommand(GitProperties gitProperties) {
        return new GitCommand(
                gitProperties.getReviewLogUri(),
                gitProperties.getToken(),
                gitProperties.getProject(),
                gitProperties.getBranch(),
                gitProperties.getAuthor(),
                gitProperties.getMessage()
        );
    }

    @Bean
    @ConfigurationProperties(prefix = "weixin")
    public WeiXinProperties weiXinProperties() {
        return new WeiXinProperties();
    }

    @Bean
    public WeiXin weiXin(WeiXinProperties weiXinProperties) {
        return new WeiXin(
                weiXinProperties.getAppid(),
                weiXinProperties.getSecret(),
                weiXinProperties.getTouser(),
                weiXinProperties.getTemplateId()
        );
    }

    @Bean
    @ConfigurationProperties(prefix = "deepseek")
    public DeepSeekProperties deepSeekProperties() {
        return new DeepSeekProperties();
    }

    @Bean
    public IOpenAI openAI(DeepSeekProperties deepSeekProperties) {
        return new DeepSeek(
                deepSeekProperties.getApihost(),
                deepSeekProperties.getApikeysecret()
        );
    }

    // 配置类
    public static class GitProperties {
        private String reviewLogUri;
        private String token;
        private String project;
        private String branch;
        private String author;
        private String message;

        public String getReviewLogUri() { return reviewLogUri; }
        public void setReviewLogUri(String reviewLogUri) { this.reviewLogUri = reviewLogUri; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getProject() { return project; }
        public void setProject(String project) { this.project = project; }
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class WeiXinProperties {
        private String appid;
        private String secret;
        private String touser;
        private String templateId;

        public String getAppid() { return appid; }
        public void setAppid(String appid) { this.appid = appid; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public String getTouser() { return touser; }
        public void setTouser(String touser) { this.touser = touser; }
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
    }

    public static class DeepSeekProperties {
        private String apihost;
        private String apikeysecret;

        public String getApihost() { return apihost; }
        public void setApihost(String apihost) { this.apihost = apihost; }
        public String getApikeysecret() { return apikeysecret; }
        public void setApikeysecret(String apikeysecret) { this.apikeysecret = apikeysecret; }
    }
}
