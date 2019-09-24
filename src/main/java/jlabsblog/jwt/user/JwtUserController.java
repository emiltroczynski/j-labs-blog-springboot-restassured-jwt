package jlabsblog.jwt.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class JwtUserController {
	private JwtUserRepository jwtUserRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public JwtUserController(
			JwtUserRepository jwtUserRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.jwtUserRepository = jwtUserRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	public void signUp(@RequestBody JwtUser user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		jwtUserRepository.save(user);
	}
}
