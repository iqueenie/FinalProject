package six.pinhong.model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberActionLogRepository extends JpaRepository<MemberActionLog, Integer> {
}
