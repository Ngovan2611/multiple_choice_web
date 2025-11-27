package haui.csn.thitot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonIgnore
    private Exam exam;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public Question() {}

    public Question(Exam exam, String questionText, String imageUrl) {
        this.exam = exam;
        this.questionText = questionText;
        this.imageUrl = imageUrl;
    }
}

