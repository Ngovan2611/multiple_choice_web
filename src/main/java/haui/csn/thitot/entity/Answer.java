package haui.csn.thitot.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer answerId;

    @OneToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(name = "is_correct", length = 10)
    private String isCorrect;

    @Column(name = "answer_a", length = 255)
    private String answerA;

    @Column(name = "answer_b", length = 255)
    private String answerB;

    @Column(name = "answer_c", length = 255)
    private String answerC;

    @Column(name = "answer_d", length = 255)
    private String answerD;




}
