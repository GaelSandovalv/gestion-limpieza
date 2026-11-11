package mx.limpieza.modelo.seguridad;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifra las contraseñas para que nunca se guarden en texto plano.
 * Usa PBKDF2 con SHA-256: mezcla la contraseña con una sal aleatoria y repite
 * el cálculo muchas veces, para que adivinar contraseñas sea muy lento.
 * El resultado se guarda como "sal:hash" (en Base64).
 */
public final class Contrasenas {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 600_000;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final int TAMANO_SAL = 16;
    private static final String SEPARADOR = ":";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Contrasenas() {
    }

    /** Genera el hash que se guarda en la base de datos. */
    public static String generarHash(String contrasena) {
        byte[] sal = new byte[TAMANO_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = calcularHash(sal, contrasena);
        Base64.Encoder base64 = Base64.getEncoder();
        return base64.encodeToString(sal) + SEPARADOR + base64.encodeToString(hash);
    }

    /** Indica si la contraseña escrita corresponde al hash guardado. */
    public static boolean verificar(String contrasena, String hashGuardado) {
        if (contrasena == null || contrasena.isEmpty() || hashGuardado == null) {
            return false;
        }
        String[] partes = hashGuardado.split(SEPARADOR);
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] sal = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            return MessageDigest.isEqual(hashEsperado, calcularHash(sal, contrasena));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcularHash(byte[] sal, String contrasena) {
        PBEKeySpec especificacion = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LONGITUD_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
