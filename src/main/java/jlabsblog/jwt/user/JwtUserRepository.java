package jlabsblog.jwt.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtUserRepository extends JpaRepository<JwtUser, Long> {
	JwtUser findByUsername(String username);
}
