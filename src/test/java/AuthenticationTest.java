//
//
//import com.umcsuser.carrent.Authentication;
//import com.umcsuser.carrent.IUserRepository;
//import com.umcsuser.carrent.models.User;
//import com.umcsuser.carrent.repositories.impl.UserJsonRepository;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AuthenticationTest {
//
//    @Test
//    void shouldAuthenticateUserWithCorrectLoginAndPassword() {
//        IUserRepository userRepository = new UserJsonRepository();
//        Authentication authentication = new Authentication(userRepository);
//
//        User user = authentication.authenticate("admin", "admin123");
//
//        assertNotNull(user);
//        assertEquals("admin", user.getLogin());
//    }
//
//    @Test
//    void shouldNotAuthenticateUserWithWrongPassword() {
//        IUserRepository userRepository = new UserJsonRepository();
//        Authentication authentication = new Authentication(userRepository);
//
//        User user = authentication.authenticate("admin", "zlehaslo");
//
//        assertNull(user);
//    }
//
//    @Test
//    void shouldNotAuthenticateNonExistingUser() {
//        IUserRepository userRepository = new UserJsonRepository();
//        Authentication authentication = new Authentication(userRepository);
//
//        User user = authentication.authenticate("brak", "admin123");
//
//        assertNull(user);
//    }
//
//    @Test
//    void hashPasswordShouldReturnSameHashForSameInput() {
//        String hash1 = Authentication.hashPassword("admin123");
//        String hash2 = Authentication.hashPassword("admin123");
//
//        assertEquals(hash1, hash2);
//    }
//}