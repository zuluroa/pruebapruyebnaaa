package co.com.sofka.parches.utils;

import co.com.sofka.parches.collections.Usuario;
import co.com.sofka.parches.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class Validaciones {

    final Logger log = Logger.getLogger("utils");

    private final UsuarioRepository repositorio;

    public Validaciones(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Mono<Usuario> verificarExistenciaUsuarioMongoYFirebaseParaCrearUsuario(String uid) {
        return verificarExistencia(uid);
    }

    public Mono<Usuario> verificarExistenciaUsuarioMongoYFirebaseParaIniciarSesion(String uid) {
        return verificarExistencia(uid)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT)));
    }



    private Mono<Usuario> verificarExistencia(String uid) {
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(uid);
        } catch (FirebaseAuthException e) {
            log.warning(e.getMessage());
        }

        if (userRecord == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT));
        }

        return repositorio.findByUid(uid);
    }
}
