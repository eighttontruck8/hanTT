package org.example.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_member_email",
                columnNames = "email")
})
@Getter
public class Member {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String pwHash;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Member() { } // JPA용

    public Member(String email, String pw_hash, String nickname) {
        this.email = email;
        this.pwHash = pw_hash;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    public static Member create(String email, String pwHash, String nickname){
        return new Member(email, pwHash, nickname);
    }
}
