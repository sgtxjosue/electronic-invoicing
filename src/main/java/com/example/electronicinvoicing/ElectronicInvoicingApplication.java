package com.example.electronicinvoicing;

import com.example.electronicinvoicing.domain.Role;
import com.example.electronicinvoicing.domain.RoleName;
import com.example.electronicinvoicing.domain.User;
import com.example.electronicinvoicing.repository.RoleRepository;
import com.example.electronicinvoicing.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ElectronicInvoicingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicInvoicingApplication.class, args);
	}

	@Bean
	CommandLineRunner initDefaultAdmin(UserRepository userRepository,
	                                   RoleRepository roleRepository,
	                                   PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByUsername("admin")) {
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseGet(() -> {
							Role role = new Role();
							role.setName(RoleName.ROLE_ADMIN);
							return roleRepository.save(role);
						});

				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.getRoles().add(adminRole);
				userRepository.save(admin);
			}
		};
	}

}
