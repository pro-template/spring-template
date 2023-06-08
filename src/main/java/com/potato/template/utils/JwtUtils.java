package com.potato.template.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    /**
     * 密钥
     */
    public static final String SECRET_KEY = "QgLyAzgG97uM7B6XJJ3yWd2M1z20HqmLQgLyAzgG97uM7B6XJJ3yWd2M1z20HqmLQgLyAzgG97uM7B6XJJ3yWd2M1z20HqmL";

    /**
     *  token过期时间
     */
    public static final long EXPIRE = 1000 * 60 * 60 * 24;

    /**
     * 生成token字符串
     * @param id
     * @param email
     * @return
     */
    public static String getJwtToken(String id, String email) {
        // 现在时间
        Date now =  new Date();
        // 过期时间
        Date expiration = new Date(now.getTime()+EXPIRE);
        // 生成jwt
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("user")
                .claim("id", id)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 判断token是否存在与有效
     *
     * @param token
     * @return
     */
    public static boolean checkToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证jwt是否有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authorization");
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过获取用户id
     * @param token
     * @return
     */
    public static String parseId(String token){
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
        return (String) claimsJws.getBody().get("id");
    }
}
