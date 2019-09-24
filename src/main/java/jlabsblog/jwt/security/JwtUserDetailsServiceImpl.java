package jlabsblog.jwt.security;

import static java.util.Collections.emptyList;

import jlabsblog.jwt.user.JwtUser;
import jlabsblog.jwt.user.JwtUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	private JwtUserRepository jwtUserRepository;

	public JwtUserDetailsServiceImpl(JwtUserRepository applicationUserRepository) {
		this.jwtUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		JwtUser applicationUser = jwtUserRepository.findByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
	}
}
