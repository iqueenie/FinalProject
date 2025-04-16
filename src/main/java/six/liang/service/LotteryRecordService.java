package six.liang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import six.liang.model.Award;
import six.liang.model.LotteryRecord;
import six.liang.model.LotteryRecordRepository;
import six.hsiao.model.MembersBean;
import six.liang.model.AwardRepository;
import six.hsiao.model.MembersRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class LotteryRecordService {

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private LotteryRecordRepository lotteryRecordRepository;

    @Autowired
    private MembersRepository membersRepository;
    
//    檢查會員是否已抽獎
    public boolean hasDrawnToday(String memberAccount) {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        LocalDate today = LocalDate.now();
        return lotteryRecordRepository.findByMemberBeanAndDrawDate(member, today).isPresent();
    }

//    執行抽獎，並保存紀錄
    public Award drawLottery(String memberAccount) throws Exception {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new Exception("Member not found.");
        }

        LocalDate today = LocalDate.now();
        Optional<LotteryRecord> todayRecords = lotteryRecordRepository.findByMemberBeanAndDrawDate(member, today);
        if (todayRecords.isPresent()) {
            throw new IllegalStateException("您今天已經參與過抽獎了，請明天再試。");
        }

        List<Award> awards = awardRepository.findAll();
        if (awards.isEmpty()) {
            throw new Exception("No awards available.");
        }

        Award award = getRandomAward(awards);
        member.setPoints(member.getPoints() + award.getPoints());
        membersRepository.save(member);

        LotteryRecord record = new LotteryRecord();
        record.setMemberBean(member);
        record.setMemberId(member.getMemberId());
        record.setDrawDate(today);
        record.setAward(award); 
        lotteryRecordRepository.save(record);

        return award;
    }
//		根據awards機率抽獎
    private Award getRandomAward(List<Award> awards) {
        Random random = new Random();
        double totalWeight = awards.stream().mapToDouble(Award::getProbability).sum();
        double randomValue = random.nextDouble() * totalWeight;

        for (Award award : awards) {
            randomValue -= award.getProbability();
            if (randomValue <= 0) {
                return award;
            }
        }
        return awards.get(awards.size() - 1); 
    }

//    獲取所有會員抽獎紀錄
    public List<LotteryRecord> getRecordsByMemberAccount(String memberAccount) {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new RuntimeException("Member not found.");
        }
        return lotteryRecordRepository.findByMemberBean(member);
    }

//    獲取所有會員最近幾天抽獎紀錄
    public List<LotteryRecord> getRecentRecords(String memberAccount, int days) {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new RuntimeException("Member not found.");
        }
        LocalDate startDate = LocalDate.now().minusDays(days);
        return lotteryRecordRepository.findByMemberBeanAndDrawDateAfter(member, startDate);
    }
    
    public List<LotteryRecord> getAllRecords() {
        return lotteryRecordRepository.findAll();
    }

    public List<LotteryRecord> filterRecords(LocalDate startDate, LocalDate endDate, Integer memberId, Long awardId) {
        if (startDate != null && endDate != null) {
            return lotteryRecordRepository.findByDrawDateBetween(startDate, endDate);
        } else if (memberId != null) {
            return lotteryRecordRepository.findByMemberId(memberId);
        } else if (awardId != null) {
            return lotteryRecordRepository.findByAwardId(awardId);
        } else {
            return lotteryRecordRepository.findAll();
        }
    }
    public List<LotteryRecord> filterRecords(LocalDate startDate, LocalDate endDate) {
        return lotteryRecordRepository.findAllByDrawDateBetween(startDate, endDate);
    }
}
