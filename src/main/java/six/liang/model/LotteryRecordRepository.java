package six.liang.model;

import org.springframework.data.jpa.repository.JpaRepository;
import six.liang.model.LotteryRecord;
import six.hsiao.model.MembersBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LotteryRecordRepository extends JpaRepository<LotteryRecord, Long> {
    Optional<LotteryRecord> findByMemberBeanAndDrawDate(MembersBean memberBean, LocalDate drawDate);
    List<LotteryRecord> findByMemberBean(MembersBean memberBean);
    List<LotteryRecord> findByDrawDateBefore(LocalDate date);
}