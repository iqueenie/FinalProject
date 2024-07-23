package six.pinhong.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "forbiddenWord")
@Component
public class ForbiddenWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer forbiddenWordId;

    @Column(nullable = false, unique = true)
    private String word;

	public Integer getForbiddenWordId() {
		return forbiddenWordId;
	}

	public void setForbiddenWordId(Integer forbiddenWordId) {
		this.forbiddenWordId = forbiddenWordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public ForbiddenWord(String word) {
		this.word = word;
	}

	public ForbiddenWord() {
	}

}