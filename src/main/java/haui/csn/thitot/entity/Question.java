package haui.csn.thitot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    @JsonIgnore
    private Exam exam;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; //

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Answer answer;



    public Question(Subject subject, String questionText, String imageUrl) {
        this.subject = subject;
        this.questionText = questionText;
        this.imageUrl = imageUrl;
    }
}
