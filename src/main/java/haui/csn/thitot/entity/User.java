package haui.csn.thitot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    private String full_name;
    private String username;
    @Column(name = "class_name")
    private String className;
    private String password;
    private String email;
    private String phone;
    private String role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private String created_at;


}
