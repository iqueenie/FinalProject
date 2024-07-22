package six.pinhong.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Integer> {
	
    List<ForbiddenWord> findAll();
}

