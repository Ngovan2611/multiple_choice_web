package haui.csn.thitot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subjectId;

    @Column(name = "subject_name")
    private String subjectName;

    private String description;

    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

}

