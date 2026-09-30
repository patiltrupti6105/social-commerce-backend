package com.socialcommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for Social Commerce API.
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API docs at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI socialCommerceOpenAPI() {
        // Define JWT security scheme
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("Social Commerce API")
                .description("""
                    A comprehensive social commerce platform combining e-commerce with social networking.
                    
                    ## Features
                    - 🔐 JWT Authentication (register, login, refresh token)
                    - 👥 User Management (buyers, sellers, admins)
                    - 📦 Product Catalog (with admin approval workflow)
                    - 🛒 Shopping Cart & Orders
                    - 💬 Social Feed (posts, comments, likes)
                    - ⭐ Product Reviews & Ratings
                    - 🔔 Notifications
                    - 📊 Analytics Dashboard
                    
                    ## Authentication
                    1. Register a user at `/api/v1/auth/register`
                    2. Login at `/api/v1/auth/login` to get an access token
                    3. Click the 🔓 Authorize button above and paste: `Bearer YOUR_TOKEN`
                    4. All protected endpoints will now work
                    
                    ## User Roles
                    - **BUYER**: Browse products, shop, post content
                    - **SELLER**: All buyer features + create/manage products
                    - **ADMIN**: Approve products, moderate content, manage users
                    
                    ## Product Workflow
                    Products follow: DRAFT → PENDING_REVIEW → ACTIVE (visible to public)
                    
                    ## UUID Architecture
                    This API uses UUID-first design for security and scalability.
                    - Users are identified by UUID externally
                    - Prevents enumeration attacks
                    - Ready for distributed systems
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Social Commerce Team")
                    .email("support@socialcommerce.com")
                    .url("https://socialcommerce.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.socialcommerce.com")
                    .description("Production Server (if deployed)")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter JWT token obtained from /api/v1/auth/login")));
    }
}
