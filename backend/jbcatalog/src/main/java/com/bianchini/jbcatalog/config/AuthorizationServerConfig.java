package com.bianchini.jbcatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

//Classe de configuração para o servidor de autorização/autenticação.

@Configuration  //Classe de configuração
@EnableAuthorizationServer  //Define que é uma classe de autorização do server oauth
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //Injeção de dependẽncias.

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  //Método para encryptografar

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;  // //Métodos que podem acessar(ler, criar um token codificando, decodificar) o token JWT

    @Autowired
    private JwtTokenStore tokenStore; //Métodos que podem acessar(ler, criar um token codificando, decodificar) o token JWT

    @Autowired
    private AuthenticationManager authenticationManager;

    //Métodos(AuthorizationServerConfigurerAdapter) de configuração para sobrescrever.

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {  //Método configura as credenciais da aplicação.
        clients.inMemory()
                .withClient("jbcatalog")  //nome do cliente(aplicação)
                .secret(passwordEncoder.encode("jbcatalog123"))  //senha
                .scopes("read", "write") //tipo leitura e escrita
                .authorizedGrantTypes("password") //tipo de autorização
                .accessTokenValiditySeconds(86400); //tempo de expiração do token
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {  //Quem vai autorizar e o formato do token
        endpoints.authenticationManager(authenticationManager)  //Vai processar a autenticação(gerenciador de autenticação)
                .tokenStore(tokenStore)                         // Vai processar o token
                .accessTokenConverter(accessTokenConverter);     // Vai processar o token
    }
}
