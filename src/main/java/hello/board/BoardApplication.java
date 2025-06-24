package hello.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class BoardApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(BoardApplication.class, args);
	}

	@Bean
	public AuditorAware<Long> auditorAware() {
		return () -> {
			ServletRequestAttributes requestAttributes =
					(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (requestAttributes == null) {
				return Optional.empty();
			}

			HttpServletRequest servletRequest = requestAttributes.getRequest();
			HttpSession session = servletRequest.getSession(false);
			if (session == null) {
				return Optional.empty();
			}

			Long memberId = (Long) session.getAttribute(SessionConst.MEMBER_ID);
			if (memberId == null) {
				return Optional.empty();
			}
			return Optional.of(memberId);
		};
	}

}
