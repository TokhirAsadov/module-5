package uz.pdp.entity;

import lombok.*;

import java.util.UUID;

//@Getter(value = AccessLevel.PACKAGE)
//@Setter(value = AccessLevel.PACKAGE)
//@ToString(exclude = {"password","id","username","login"})
//@ToString(of = {"password","id","username","login"})
//@ToString(includeFieldNames = false)
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private UUID id;
    @NonNull
    private String firstName;
    private final String lastName;
    private String middleName;
    private String username;
    @NonNull
    private String login;
    @NonNull
    private String password;
    private String email;

}
