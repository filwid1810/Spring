package org.example;

public class Authentication {

    public Authentication(IUserRepository userRepository) {
    }

    public static String hashPassword(String admin123) {
        Hasher.hashPassword(admin123);
        return admin123;
    }

    public User authenticate(String admin, String admin123) {
        
    return null;
    }
}
