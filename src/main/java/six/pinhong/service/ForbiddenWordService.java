package six.pinhong.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.pinhong.model.ForbiddenWord;
import six.pinhong.model.ForbiddenWordRepository;
import six.pinhong.model.ProductReview;

@Transactional
@Service
public class ForbiddenWordService {

    @Autowired
    private ForbiddenWordRepository forbiddenWordRepository;

    public List<ForbiddenWord> getForbiddenWords() {
    	return forbiddenWordRepository.findAll();
    }

    public void addForbiddenWord(String word) {
        ForbiddenWord forbiddenWord = new ForbiddenWord();
        forbiddenWord.setWord(word);
        forbiddenWordRepository.save(forbiddenWord);
    }

    public void removeForbiddenWord(Integer id) {
        forbiddenWordRepository.deleteById(id);
    }

    // 替換評論中的敏感字
    public String replaceForbiddenWords(String reviewContent) {
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        for (ForbiddenWord forbiddenWord : forbiddenWords) {
            String word = forbiddenWord.getWord();
            if (reviewContent.contains(word)) {
                StringBuilder replacement = new StringBuilder();
                for (int i = 0; i < word.length(); i++) {
                    replacement.append('*');
                }
                reviewContent = reviewContent.replace(word, replacement.toString());
            }
        }
        return reviewContent;
    }
    
    public ForbiddenWord findByWord(String word) {
    	return forbiddenWordRepository.findByWord(word);
    }
}
