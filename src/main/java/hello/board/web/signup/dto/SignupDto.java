package hello.board.web.signup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SignupDto {
    @NotBlank
    @Length(max = 5)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 30)
    private String password;
}
