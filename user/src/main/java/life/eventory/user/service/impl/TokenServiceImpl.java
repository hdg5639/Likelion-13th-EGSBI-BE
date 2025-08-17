package life.eventory.user.service.impl;

import com.nimbusds.jose.jwk.RSAKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import life.eventory.user.service.TokenService;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {
    private final JwtEncoder jwtencoder;
    private final RSAKey rsaKey;

    public String issueAccessToken(String userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("user")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(rsaKey.getKeyID())
                .build();

        return jwtencoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
