package com.bianchini.jbcatalog.components;

import com.bianchini.jbcatalog.entities.User;
import com.bianchini.jbcatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {  //Turbinador de token, adicionar valores ao token

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        User user = userRepository.findByEmail(oAuth2Authentication.getName());  //oAuth2Authentication já tem o nome usuario armazenado.
        Map<String, Object> map = new HashMap<>();                               //Criar um map
        map.put("userFirstName", user.getFirstName());                           //adicionar os valores que deseja
        map.put("userId", user.getId());                                         //adicionar os valores que deseja

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken;  //Fazendo um cast, pois o oAuth2AccessToken não tem o método setAdditionalInformation
        token.setAdditionalInformation(map);  //adicionar o map

        return oAuth2AccessToken;  //retornar o token com as informações adicionadas
    }
}
