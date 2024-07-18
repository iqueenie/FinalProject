package six.liang.model;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;
import six.hsiao.model.MembersBean;

@Entity
@Table(name = "lotteryRecords")
@Component
public class LotteryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memberId", insertable = false, updatable = false)
    private Integer memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private MembersBean memberBean;

    private LocalDate drawDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awardId", nullable = false)
    private Award award;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public MembersBean getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(MembersBean memberBean) {
        this.memberBean = memberBean;
    }

    public LocalDate getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(LocalDate drawDate) {
        this.drawDate = drawDate;
    }

    public Award getAward() {
        return award;
    }

    public void setAward(Award award) {
        this.award = award;
    }
}
