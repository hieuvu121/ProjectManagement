package com.example.prj_management.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserEntityTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void canPersistUser() {
        User user = User.builder()
                .email("alice@example.com")
                .passwordHash("hashed_password")
                .name("Alice")
                .build();

        User saved = em.persistAndFlush(user);
        em.clear();

        User found = em.find(User.class, saved.getId());
        assertThat(found.getId()).isNotNull();
        assertThat(found.getEmail()).isEqualTo("alice@example.com");
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getPasswordHash()).isEqualTo("hashed_password");
    }
}
